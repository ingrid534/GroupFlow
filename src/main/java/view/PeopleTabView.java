package view;

import entity.user.UserRole;
import interface_adapter.manage_members.ManageMembersState;
import interface_adapter.manage_members.PeopleTabViewModel;
import interface_adapter.manage_members.remove_member.RemoveMemberController;
import interface_adapter.manage_members.respond_request.RespondRequestController;
import interface_adapter.manage_members.view_members.ViewMembersController;
import interface_adapter.manage_members.view_pending.ViewPendingController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * A panel representing the "People" tab of a group UI.
 * Displays group members and pending membership requests.
 * Members can be assigned roles and removed, and pending requests
 * can be accepted or declined.
 */
public class PeopleTabView extends JPanel implements ActionListener, PropertyChangeListener {

    /** Panel that lists current members. */
    private final JPanel membersListPanel = new JPanel();
    /** Panel that lists pending membership requests. */
    private final JPanel pendingListPanel = new JPanel();
    /** Available roles a member can have. */
    private final String[] roles = {"Member", "Admin", "Moderator"};

    /** Fixed height for each row/item in the lists. */
    private static final int ROW_HEIGHT = 35;
    /** Split pane dividing members list from pending requests (if visible). */
    private final JSplitPane split;

    // Controllers
    private ViewMembersController viewMembersController;
    private ViewPendingController viewPendingController;
    private RemoveMemberController removeMemberController;
    private RespondRequestController respondRequestController;

    private final PeopleTabViewModel peopleTabViewModel;

    private final String groupID;
    private final String currentUsername;

    /** Role of the current user in this group, if known. */
    private UserRole currentUserRole;

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

        // Top bar with Refresh button
        addRefreshButton();

        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(1.0);
        split.setDividerSize(4);

        // Create Members panel
        JPanel membersPanel = createListPanel(
                "Members",
                membersListPanel,
                new String[]{"Name A-Z", "Name Z-A", "Role"}
        );

        split.setLeftComponent(membersPanel);
        split.setRightComponent(null);
        add(split, BorderLayout.CENTER);
    }

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
     * @param title       displayed title of the panel
     * @param listPanel   the internal panel that items will be added to
     * @param sortOptions sorting choices displayed in a combo box
     * @return the constructed panel
     */
    private JPanel createListPanel(String title, JPanel listPanel, String[] sortOptions) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Sort by:"));
        JComboBox<String> sortCombo = new JComboBox<>(sortOptions);
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
     * Adds a member row to the members list.
     *
     * @param name the member display name
     * @param role the current role assigned to the member
     */
    public void addMember(String name, String role) {
        final JPanel row = createRowPanel();

        boolean isCurrentUser = name != null && name.equalsIgnoreCase(currentUsername);

        final JLabel nameLabel = new JLabel(name);

        // Highlight the current user
        if (isCurrentUser) {
            nameLabel.setText(name + " (You)");
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
        }

        JComboBox<UserRole> roleDropdown = new JComboBox<>(UserRole.values());

        // Match role string to UserRole
        UserRole matchedRole = parseUserRole(role);
        if (matchedRole != null) {
            roleDropdown.setSelectedItem(matchedRole);
        }

        JButton removeBtn = new JButton("\u2716");
        removeBtn.addActionListener(event -> {
            removeMemberController.execute(groupID, name);
        });

        // Permission rules
        if (isCurrentUser) {
            // No self removal from this tab
            removeBtn.setEnabled(false);
            removeBtn.setToolTipText("");
            // If current user is only a member, also do not let them change their own role here
            if (currentUserRole == UserRole.MEMBER) {
                roleDropdown.setEnabled(false);
            }
        } else if (currentUserRole == UserRole.MEMBER) {
            // Plain members cannot manage other users
            roleDropdown.setEnabled(false);
            removeBtn.setEnabled(false);
            removeBtn.setToolTipText("Members cannot remove other users.");
        }

        addComponentsToRow(row, nameLabel, roleDropdown, removeBtn);

        membersListPanel.add(row);
        membersListPanel.add(Box.createVerticalStrut(2));
        membersListPanel.revalidate();
        membersListPanel.repaint();
    }

    /**
     * Updates the People tab with the given list of members.
     * This method clears the current member display and rebuilds it
     * using the provided username-to-role mapping.
     *
     * @param members a map where each key is a username and each value is that user's role
     */
    public void setMembers(Map<String, String> members) {
        membersListPanel.removeAll();
        currentUserRole = null;

        if (members == null || members.isEmpty()) {
            membersListPanel.revalidate();
            membersListPanel.repaint();
            return;
        }

        // First pass: find the current user's role
        for (Map.Entry<String, String> entry : members.entrySet()) {
            String name = entry.getKey();
            String role = entry.getValue();

            if (name != null && name.equalsIgnoreCase(currentUsername)) {
                currentUserRole = parseUserRole(role);
                break;
            }
        }

        // Second pass: build all rows
        for (Map.Entry<String, String> entry : members.entrySet()) {
            this.addMember(entry.getKey(), entry.getValue());
        }

        membersListPanel.revalidate();
        membersListPanel.repaint();
    }

    /**
     * Adds a user to the pending membership requests list.
     * If the pending panel does not yet exist, it is created.
     *
     * @param username the username of the user who requested to join
     */
    public void addPending(String username) {

        // Create pending section if needed
        if (split.getRightComponent() == null) {
            JPanel pendingPanel = createListPanel(
                    "Pending Requests",
                    pendingListPanel,
                    new String[]{"Name A-Z", "Name Z-A"}
            );
            split.setRightComponent(pendingPanel);
            split.setDividerLocation(0.6);
        }

        final JPanel row = createRowPanel();

        final JLabel nameLabel = new JLabel(username);

        JButton acceptBtn = new JButton("\u2714");
        acceptBtn.setForeground(new Color(3, 120, 3));
        acceptBtn.addActionListener(event -> respondRequestController.execute(groupID, username, true));

        JButton declineBtn = new JButton("\u2716");
        declineBtn.setForeground(new Color(220, 20, 60));
        declineBtn.addActionListener(event -> respondRequestController.execute(groupID, username, false));

        // Plain members cannot manage pending requests
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
        pendingListPanel.revalidate();
        pendingListPanel.repaint();
    }

    /**
     * Updates the People tab with the given list of pending membership requests.
     * This method clears the current pending panel and rebuilds it using
     * the provided list of usernames.
     *
     * @param pending a list of usernames representing users who have requested to join the group
     */
    public void setPending(ArrayList<String> pending) {
        pendingListPanel.removeAll();

        if (pending == null || pending.isEmpty()) {
            // Empty pending users list -> hide the right side
            if (split.getRightComponent() != null) {
                split.setRightComponent(null);
                split.setDividerLocation(1.0);
            }
            pendingListPanel.revalidate();
            pendingListPanel.repaint();
            return;
        }

        for (String entry : pending) {
            this.addPending(entry);
        }

        pendingListPanel.revalidate();
        pendingListPanel.repaint();
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
     * @param row        the panel representing the row
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
     * Removes a pending request row and cleans up spacing fillers.
     * If no pending requests remain, the entire pending panel is removed.
     *
     * @param row the row panel to remove
     */
    private void removePendingRow(JPanel row) {
        int index = pendingListPanel.getComponentZOrder(row);
        if (index != -1) {
            pendingListPanel.remove(index);

            if (index < pendingListPanel.getComponentCount()) {
                Component next = pendingListPanel.getComponent(index);
                if (next instanceof Box.Filler) {
                    pendingListPanel.remove(next);
                }
            }
        }

        pendingListPanel.revalidate();
        pendingListPanel.repaint();

        // If no pending requests left, remove the right panel entirely
        if (pendingListPanel.getComponentCount() == 0 && split.getRightComponent() != null) {
            split.setRightComponent(null);
            split.setDividerLocation(1.0);
        }
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

    @Override
    public void actionPerformed(ActionEvent e) {
        // No button actions wired through ActionListener directly yet
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
