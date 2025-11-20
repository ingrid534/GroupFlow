package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * A panel representing the "People" tab of a group UI.
 * Displays group members and pending invitations.
 * Members can be assigned roles and removed, and pending invitations
 * can be accepted or declined.
 */
public class PeopleTabPanel extends JPanel {

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

    /**
     * Creates a new PeopleTabPanel for a given group name.
     *
     * @param groupName the name of the group (might have to change to the group object)
     */
    public PeopleTabPanel(String groupName) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

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

        // Example data (temporary)
        addMember("Alice", "Member");
        addMember("Bob", "Admin");
        addMember("Charlie", "Moderator");

        split.setLeftComponent(membersPanel);
        split.setRightComponent(null);
        add(split, BorderLayout.CENTER);

        // Example pending invites (temporary)
        addPending("Bob1");
        addPending("Bob2");
        addPending("Bob3");
        addPending("Bob4");
        addPending("Bob5");
        addPending("Bob6");
        addPending("Bob7");
        addPending("Bob8");
        addPending("Bob9");
        addPending("Bob10");
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

        JLabel nameLabel = new JLabel(name);
        JComboBox<String> roleDropdown = new JComboBox<>(roles);
        roleDropdown.setSelectedItem(role);
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
     * Adds a user to the pending-invitations list.
     * If the pending panel does not yet exist, it is created.
     *
     * @param username the username of the invited person
     */
    public void addPending(String username) {

        // Create pending section if needed.
        if (split.getRightComponent() == null) {
            JPanel pendingPanel = createListPanel(
                    "Pending Invites",
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
                comp.setPreferredSize(new Dimension(130, ROW_HEIGHT - 6));
                comp.setMaximumSize(new Dimension(130, ROW_HEIGHT - 6));
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
}
