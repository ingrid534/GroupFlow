package view;

import entity.user.UserRole;
import interface_adapter.manage_members.ManageMembersState;
import interface_adapter.manage_members.PeopleTabViewModel;
import interface_adapter.manage_members.remove_member.RemoveMemberController;
import interface_adapter.manage_members.respond_request.RespondRequestController;
import interface_adapter.manage_members.update_role.UpdateRoleController;
import interface_adapter.manage_members.view_members.ViewMembersController;
import interface_adapter.manage_members.view_pending.ViewPendingController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A panel representing the People tab of a group UI.
 * Displays group members and pending membership requests.
 * Members can be assigned roles and removed, and pending requests
 * can be accepted or declined.
 */
public class PeopleTabView extends JPanel implements PropertyChangeListener {

    private static final long serialVersionUID = 1L;

    /** Panel that lists current members. */
    private final JPanel membersListPanel = new JPanel();
    /** Panel that lists pending membership requests. */
    private final JPanel pendingListPanel = new JPanel();

    /** Fixed height for each row or item in the lists. */
    private static final int ROW_HEIGHT = 35;
    /** Split pane dividing members list from pending requests if visible. */
    private final JSplitPane split;

    // Controllers
    private ViewMembersController viewMembersController;
    private ViewPendingController viewPendingController;
    private RemoveMemberController removeMemberController;
    private RespondRequestController respondRequestController;
    private UpdateRoleController updateRoleController;

    private final PeopleTabViewModel peopleTabViewModel;

    private final String groupID;
    private final String currentUsername;

    /** Role of the current user in this group, if known. */
    private UserRole currentUserRole;

    /** Cached data used for rebuilding and sorting. */
    private final Map<String, String> currentMembers = new HashMap<>();
    private final List<String> currentPending = new ArrayList<>();

    /** Sort options. */
    private final String sortOption1 = "Name A-Z";
    private final String sortOption2 = "Name Z-A";
    private final String sortOption3 = "Role";
    /** Current sort options. */
    private String membersSortOption = sortOption1;
    private String pendingSortOption = sortOption1;

    /**
     * Creates a new PeopleTabView for a given group.
     *
     * @param peopleTabViewModel the view model backing this tab
     * @param groupID the unique identifier of the group
     * @param currentUsername the username of the current user
     */
    public PeopleTabView(PeopleTabViewModel peopleTabViewModel, String groupID, String currentUsername) {
        this.peopleTabViewModel = Objects.requireNonNull(peopleTabViewModel);
        this.peopleTabViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        this.groupID = groupID;
        this.currentUsername = currentUsername;

        addRefreshButton();

        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(1.0);
        split.setDividerSize(4);

        JPanel membersPanel = createListPanel(
                "Members",
                membersListPanel,
                new String[]{sortOption1, sortOption2, sortOption3}
        );

        split.setLeftComponent(membersPanel);
        split.setRightComponent(null);
        add(split, BorderLayout.CENTER);
    }

    /**
     * Adds the refresh button that reloads members and pending requests.
     */
    private void addRefreshButton() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> {
            if (viewMembersController != null) {
                viewMembersController.execute(groupID);
            }
            if (viewPendingController != null) {
                viewPendingController.execute(groupID);
            }
        });
        topBar.add(refreshButton);
        add(topBar, BorderLayout.NORTH);
    }

    /**
     * Creates a container panel for either the members list or the pending list,
     * including sorting controls and a scrollable content area.
     *
     * @param title displayed title of the panel
     * @param listPanel the internal panel that items will be added to
     * @param sortOptions sorting choices displayed in a combo box
     * @return the constructed panel
     */
    private JPanel createListPanel(String title, JPanel listPanel, String[] sortOptions) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Sort by:"));
        JComboBox<String> sortCombo = new JComboBox<>(sortOptions);

        // Wire sort behavior based on which panel this is
        sortCombo.addActionListener(event -> {
            String selected = (String) sortCombo.getSelectedItem();
            if (selected == null) {
                return;
            }
            if (listPanel == membersListPanel) {
                membersSortOption = selected;
                rebuildMembersPanel();
            } else if (listPanel == pendingListPanel) {
                pendingSortOption = selected;
                rebuildPendingPanel();
            }
        });

        top.add(sortCombo);
        panel.add(top, BorderLayout.NORTH);

        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setAlignmentY(TOP_ALIGNMENT);

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Parses a role string into a UserRole enum, ignoring case and extra spaces.
     *
     * @param role the role string, may be null
     * @return the matched UserRole or null if no match
     */
    private UserRole parseUserRole(String role) {
        if (role == null) {
            return null;
        }
        String normalized = role.trim();
        for (UserRole userRole : UserRole.values()) {
            if (userRole.name().equalsIgnoreCase(normalized)) {
                return userRole;
            }
        }
        return null;
    }

    /**
     * Returns the sorting order value for the given role.
     *
     * @param role the role to map to an order value
     * @return an integer representing the role order
     */
    private int roleOrder(UserRole role) {
        if (role == null) {
            return 3;
        }
        switch (role) {
            case MODERATOR:
                return 0;
            case MEMBER:
                return 1;
            default:
                return 2;
        }
    }

    /**
     * Adds a member row to the members list, with an option to lock the role dropdown.
     *
     * @param name the member display name
     * @param role the current role assigned to the member
     * @param lockRoleDropdownForUser true if the role dropdown for this user should be disabled
     */
    private void addMember(String name, String role, boolean lockRoleDropdownForUser) {
        final JPanel row = createRowPanel();

        boolean isCurrentUser = name != null && name.equalsIgnoreCase(currentUsername);

        final JLabel nameLabel = new JLabel(name);

        if (isCurrentUser) {
            nameLabel.setText(name + " (You)");
            nameLabel.setFont(nameLabel.getFont().deriveFont(java.awt.Font.BOLD));
        }

        JComboBox<UserRole> roleDropdown = new JComboBox<>(UserRole.values());

        UserRole matchedRole = parseUserRole(role);
        if (matchedRole != null) {
            roleDropdown.setSelectedItem(matchedRole);
        }
        final UserRole[] previousRole = {matchedRole};

        JButton removeBtn = new JButton("\u2716");
        removeBtn.addActionListener(event -> {
            if (removeMemberController != null) {
                removeMemberController.execute(groupID, name);
            }
        });

        applyMemberPermissions(isCurrentUser, roleDropdown, removeBtn, lockRoleDropdownForUser);

        roleDropdown.addActionListener(event -> {
            if (updateRoleController == null) {
                return;
            }
            if (!roleDropdown.isEnabled()) {
                return;
            }
            UserRole selected = (UserRole) roleDropdown.getSelectedItem();
            if (selected == null) {
                return;
            }
            if (Objects.equals(previousRole[0], selected)) {
                return;
            }
            updateRoleController.execute(groupID, name, selected);
            previousRole[0] = selected;
        });

        addComponentsToRow(row, nameLabel, roleDropdown, removeBtn);

        membersListPanel.add(row);
        membersListPanel.add(Box.createVerticalStrut(2));
    }

    /**
     * Applies permission rules to the member row controls.
     *
     * @param isCurrentUser true if this row represents the current user
     * @param roleDropdown the dropdown used to change the member role
     * @param removeBtn the button used to remove the member
     * @param lockRoleDropdownForUser true if the role dropdown should be disabled
     */
    private void applyMemberPermissions(boolean isCurrentUser,
                                        JComboBox<UserRole> roleDropdown,
                                        JButton removeBtn,
                                        boolean lockRoleDropdownForUser) {

        if (isCurrentUser) {
            removeBtn.setEnabled(false);
            removeBtn.setToolTipText("You cannot remove yourself from the group here.");
            if (currentUserRole == UserRole.MEMBER) {
                roleDropdown.setEnabled(false);
            }
        } else if (currentUserRole == UserRole.MEMBER) {
            roleDropdown.setEnabled(false);
            removeBtn.setEnabled(false);
            removeBtn.setToolTipText("Members cannot remove other users.");
        }

        if (lockRoleDropdownForUser) {
            roleDropdown.setEnabled(false);
            roleDropdown.setToolTipText("Cannot change role for the last moderator in the group.");
        }
    }

    /**
     * Updates the People tab with the given list of members.
     * This method caches the mapping and rebuilds the panel,
     * applying the current sorting selection.
     *
     * @param members a map where each key is a username and each value is that user's role
     */
    public void setMembers(Map<String, String> members) {
        currentMembers.clear();
        if (members != null) {
            currentMembers.putAll(members);
        }
        rebuildMembersPanel();
    }

    /**
     * Rebuilds the members panel from the cached currentMembers map,
     * applying sorting and last moderator protection.
     */
    private void rebuildMembersPanel() {
        membersListPanel.removeAll();
        currentUserRole = null;

        if (currentMembers.isEmpty()) {
            refreshPanel(membersListPanel);
            return;
        }

        int moderatorCount = 0;
        String lastModeratorUsername = null;

        for (Map.Entry<String, String> entry : currentMembers.entrySet()) {
            String name = entry.getKey();
            String role = entry.getValue();

            UserRole parsedRole = parseUserRole(role);

            if (name != null && name.equalsIgnoreCase(currentUsername)) {
                currentUserRole = parsedRole;
            }

            if (parsedRole == UserRole.MODERATOR) {
                moderatorCount++;
                lastModeratorUsername = name;
            }
        }

        List<Map.Entry<String, String>> entries = new ArrayList<>(currentMembers.entrySet());
        sortMembers(entries);

        for (Map.Entry<String, String> entry : entries) {
            String name = entry.getKey();
            String role = entry.getValue();

            boolean lockDropdownForThisUser = false;
            UserRole parsedRole = parseUserRole(role);

            if (parsedRole == UserRole.MODERATOR
                    && moderatorCount == 1
                    && name != null
                    && name.equalsIgnoreCase(lastModeratorUsername)) {
                lockDropdownForThisUser = true;
            }

            addMember(name, role, lockDropdownForThisUser);
        }

        refreshPanel(membersListPanel);
    }

    /**
     * Sorts the list of member entries according to the current membersSortOption.
     *
     * @param entries the entries to sort in place
     */
    private void sortMembers(List<Map.Entry<String, String>> entries) {
        if (sortOption2.equalsIgnoreCase(membersSortOption)) {
            entries.sort((ent1, ent2) -> ent2.getKey().compareToIgnoreCase(ent1.getKey()));
        } else if ("Role".equalsIgnoreCase(membersSortOption)) {
            entries.sort((ent1, ent2) -> {
                UserRole r1 = parseUserRole(ent1.getValue());
                UserRole r2 = parseUserRole(ent2.getValue());
                int cmp = Integer.compare(roleOrder(r1), roleOrder(r2));
                if (cmp != 0) {
                    return cmp;
                }
                return ent1.getKey().compareToIgnoreCase(ent2.getKey());
            });
        } else {
            entries.sort((ent1, ent2) -> ent1.getKey().compareToIgnoreCase(ent2.getKey()));
        }
    }

    /**
     * Adds a user to the pending membership requests list.
     * If the pending panel does not yet exist, it is created.
     *
     * @param username the username of the user who requested to join
     */
    public void addPending(String username) {

        ensurePendingPanelVisible();

        final JPanel row = createRowPanel();

        final JLabel nameLabel = new JLabel(username);

        JButton acceptBtn = new JButton("\u2714");
        acceptBtn.setForeground(new Color(3, 120, 3));
        acceptBtn.addActionListener(event -> {
            if (respondRequestController != null) {
                respondRequestController.execute(groupID, username, true);
            }
        });

        JButton declineBtn = new JButton("\u2716");
        declineBtn.setForeground(new Color(220, 20, 60));
        declineBtn.addActionListener(event -> {
            if (respondRequestController != null) {
                respondRequestController.execute(groupID, username, false);
            }
        });

        if (currentUserRole == UserRole.MEMBER) {
            acceptBtn.setEnabled(false);
            declineBtn.setEnabled(false);
            String tip = "Members cannot manage pending requests.";
            acceptBtn.setToolTipText(tip);
            declineBtn.setToolTipText(tip);
        }

        addComponentsToRow(row, nameLabel, acceptBtn, declineBtn);

        pendingListPanel.add(row);
        pendingListPanel.add(Box.createVerticalStrut(2));
    }

    /**
     * Ensures that the pending requests panel is visible on the split pane.
     */
    private void ensurePendingPanelVisible() {
        if (split.getRightComponent() == null) {
            JPanel pendingPanel = createListPanel(
                    "Pending Requests",
                    pendingListPanel,
                    new String[]{sortOption1, sortOption2}
            );
            split.setRightComponent(pendingPanel);
            split.setDividerLocation(0.6);
        }
    }

    /**
     * Hides the pending requests panel from the split pane.
     */
    private void hidePendingPanel() {
        if (split.getRightComponent() != null) {
            split.setRightComponent(null);
            split.setDividerLocation(1.0);
        }
    }

    /**
     * Updates the People tab with the given list of pending membership requests.
     * This method caches the list and rebuilds the panel,
     * applying the current sorting selection.
     *
     * @param pending a list of usernames representing users who have requested to join the group
     */
    public void setPending(ArrayList<String> pending) {
        currentPending.clear();
        if (pending != null) {
            currentPending.addAll(pending);
        }
        rebuildPendingPanel();
    }

    /**
     * Rebuilds the pending requests panel from the cached currentPending list,
     * applying sorting and visibility rules.
     */
    private void rebuildPendingPanel() {
        pendingListPanel.removeAll();

        if (currentPending.isEmpty()) {
            hidePendingPanel();
            refreshPanel(pendingListPanel);
            return;
        }

        ensurePendingPanelVisible();

        List<String> sorted = new ArrayList<>(currentPending);
        sortPending(sorted);

        for (String entry : sorted) {
            addPending(entry);
        }

        refreshPanel(pendingListPanel);
    }

    /**
     * Sorts the list of pending usernames according to the current pendingSortOption.
     *
     * @param pending the list of usernames to sort in place
     */
    private void sortPending(List<String> pending) {
        if (sortOption2.equalsIgnoreCase(pendingSortOption)) {
            pending.sort((str1, str2) -> str2.compareToIgnoreCase(str1));
        } else {
            pending.sort(String::compareToIgnoreCase);
        }
    }

    /**
     * Creates a consistent row panel for members or pending requests.
     *
     * @return a standard formatted row panel
     */
    private JPanel createRowPanel() {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW_HEIGHT));
        row.setMinimumSize(new Dimension(0, ROW_HEIGHT));
        row.setPreferredSize(new Dimension(0, ROW_HEIGHT));
        row.setBorder(new EmptyBorder(2, 5, 2, 5));
        return row;
    }

    /**
     * Adds components to a row with consistent sizing and spacing rules.
     *
     * @param row the panel representing the row
     * @param components the UI components to place into the row
     */
    private void addComponentsToRow(JPanel row, JComponent... components) {
        for (JComponent comp : components) {
            if (comp instanceof JButton) {
                comp.setPreferredSize(new Dimension(30, ROW_HEIGHT - 6));
                comp.setMaximumSize(new Dimension(30, ROW_HEIGHT - 6));
                comp.setAlignmentX(CENTER_ALIGNMENT);
            } else if (comp instanceof JComboBox) {
                comp.setPreferredSize(new Dimension(180, ROW_HEIGHT - 6));
                comp.setMaximumSize(new Dimension(180, ROW_HEIGHT - 6));
                comp.setAlignmentX(Component.CENTER_ALIGNMENT);
            } else {
                comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW_HEIGHT - 6));
                comp.setAlignmentY(CENTER_ALIGNMENT);
            }

            row.add(comp);
            row.add(Box.createHorizontalStrut(5));
        }
    }

    /**
     * Revalidates and repaints the given panel.
     *
     * @param panel the panel to refresh
     */
    private void refreshPanel(JPanel panel) {
        panel.revalidate();
        panel.repaint();
    }

    /**
     * Sets the controller responsible for loading the group's members.
     * Once assigned, this method immediately triggers the controller to
     * fetch the member list for the current group.
     *
     * @param viewMembersController the controller handling the View Members use case
     */
    public void setViewMembersController(ViewMembersController viewMembersController) {
        this.viewMembersController = viewMembersController;
        this.viewMembersController.execute(groupID);
    }

    /**
     * Sets the controller responsible for loading the group's pending membership requests.
     * Once assigned, this method immediately triggers the controller to
     * fetch the pending requests for the current group.
     *
     * @param viewPendingController the controller handling the View Pending use case
     */
    public void setViewPendingController(ViewPendingController viewPendingController) {
        this.viewPendingController = viewPendingController;
        this.viewPendingController.execute(groupID);
    }

    /**
     * Sets the controller responsible for removing a member from the group.
     * This method simply assigns the controller and does not trigger any actions by itself.
     *
     * @param removeMemberController the controller that handles the Remove Member use case
     */
    public void setRemoveMemberController(RemoveMemberController removeMemberController) {
        this.removeMemberController = removeMemberController;
    }

    /**
     * Sets the controller responsible for handling responses to join requests.
     * This only stores the controller reference and does not trigger any action by itself.
     *
     * @param respondRequestController the controller that processes approving or declining a join request
     */
    public void setRespondRequestController(RespondRequestController respondRequestController) {
        this.respondRequestController = respondRequestController;
    }

    /**
     * Sets the controller responsible for handling changes to member roles.
     * This only stores the controller reference and does not trigger any action by itself.
     *
     * @param updateRoleController the controller that processes member role updates
     */
    public void setUpdateRoleController(UpdateRoleController updateRoleController) {
        this.updateRoleController = updateRoleController;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("members".equals(evt.getPropertyName())) {
            ManageMembersState st = (ManageMembersState) evt.getNewValue();
            setMembers(st.getMembers());
        } else if ("pending".equals(evt.getPropertyName())) {
            ManageMembersState st = (ManageMembersState) evt.getNewValue();
            setPending(st.getPending());
        }
    }
}
