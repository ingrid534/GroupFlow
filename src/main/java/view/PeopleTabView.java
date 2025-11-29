package view;

import entity.user.UserRole;
import interface_adapter.manage_members.ManageMembersState;
import interface_adapter.manage_members.PeopleTabViewModel;
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
 * Displays group members and pending invitations.
 * Members can be assigned roles and removed, and pending invitations
 * can be accepted or declined.
 */
public class PeopleTabView extends JPanel implements ActionListener, PropertyChangeListener {

    /** Panel that lists current members. */
    private final JPanel membersListPanel = new JPanel();
    /** Panel that lists pending invitations. */
    private final JPanel pendingListPanel = new JPanel();
    /** Available roles a member can have. */
    private final String[] roles = {"Member", "Admin", "Moderator"};

    /** Fixed height for each row/item in the lists. */
    private static final int ROW_HEIGHT = 35;
    /** Split pane dividing members list from pending invitations (if visible). */
    private final JSplitPane split;

    // Controllers
    private ViewMembersController viewMembersController;
    private ViewPendingController viewPendingController;

    private final PeopleTabViewModel peopleTabViewModel;

    private final String groupID;

    /**
     * Creates a new PeopleTabPanel for a given group name.
     *
     * @param peopleTabViewModel ..
     * @param groupID the name of the group (might have to change to the group id)
     */
    public PeopleTabView(PeopleTabViewModel peopleTabViewModel, String groupID) {
        this.peopleTabViewModel = Objects.requireNonNull(peopleTabViewModel);
        this.peopleTabViewModel.addPropertyChangeListener(this);

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        this.groupID = groupID;

        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(1.0);
        split.setDividerSize(4);

        // Create Members panel
        JPanel membersPanel;
        membersPanel = createListPanel(
                "Members",
                membersListPanel,
                new String[]{"Name A-Z", "Name Z-A", "Role"}
        );

        split.setLeftComponent(membersPanel);
        split.setRightComponent(null);
        add(split, BorderLayout.CENTER);
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
     * Adds a member row to the members list.
     *
     * @param name the member's display name
     * @param role the current role assigned to the member
     */
    public void addMember(String name, String role) {
        JPanel row = createRowPanel();

        final JLabel nameLabel = new JLabel(name);
        JComboBox<UserRole> roleDropdown = new JComboBox<>(UserRole.values());

        // Safely match String role to a UserRole, ignoring case and extra spaces
        UserRole matchedRole = null;
        if (role != null) {
            String normalized = role.trim();
            for (UserRole userRole : UserRole.values()) {
                if (userRole.name().equalsIgnoreCase(normalized)) {
                    matchedRole = userRole;
                    break;
                }
            }
        }

        if (matchedRole != null) {
            roleDropdown.setSelectedItem(matchedRole);
        }

        JButton removeBtn = new JButton("\u2716");
        removeBtn.addActionListener(event -> {
            membersListPanel.remove(row);
            membersListPanel.revalidate();
            membersListPanel.repaint();
        });

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
        for (Map.Entry<String, String> entry : members.entrySet()) {
            this.addMember(entry.getKey(), entry.getValue());
        } // for entry
    } // setMembers

    /**
     * Adds a user to the pending-invitations list.
     * If the pending panel does not yet exist, it is created.
     *
     * @param username the username of the invited person
     */
    public void addPending(String username) {

        // Create pending section if needed.
        if (split.getRightComponent() == null) {
            JPanel pendingPanel = createListPanel(
                    "Pending Requests",
                    pendingListPanel,
                    new String[]{"Name A-Z", "Name Z-A"}
            );
            split.setRightComponent(pendingPanel);
            split.setDividerLocation(0.6);
        }

        JPanel row = createRowPanel();

        final JLabel nameLabel = new JLabel(username);

        JButton acceptBtn = new JButton("\u2714");
        acceptBtn.setForeground(new Color(3, 120, 3));
        acceptBtn.addActionListener(event -> {
            addMember(username, roles[0]);
            removePendingRow(row);
        });

        JButton declineBtn = new JButton("\u2716");
        declineBtn.setForeground(new Color(220, 20, 60));
        declineBtn.addActionListener(event -> removePendingRow(row));

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
        } // for entry
    } // setPending

    /**
     * Creates a consistent row panel for members or pending invites.
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
     * Removes a pending invitation row and cleans up spacing/fillers.
     * If no pending invites remain, the entire pending panel is removed.
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

        // If no pending invites left, remove the right panel entirely
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
        // Immediately load members for this group
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
        // Immediately load members for this group
        this.viewPendingController.execute(groupID);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("members".equals(evt.getPropertyName())) {
            ManageMembersState st = (ManageMembersState) evt.getNewValue();
            setMembers(st.getMembers());
        } else if ("pending".equals(evt.getPropertyName())) {
            ManageMembersState st = (ManageMembersState) evt.getNewValue();
            setPending(st.getPending());
        } // if
    } // propertyChange

}
