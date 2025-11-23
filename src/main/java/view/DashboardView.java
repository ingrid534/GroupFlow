package view;

import interface_adapter.create_group.CreateGroupController;
import interface_adapter.creategrouptasks.CreateGroupTasksController;
import interface_adapter.creategrouptasks.CreateGroupTasksViewModel;
import interface_adapter.dashboard.DashboardViewModel;
import interface_adapter.dashboard.LoggedInState;
import interface_adapter.editgrouptask.EditGroupTaskController;
import interface_adapter.editgrouptask.EditGroupTaskViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.viewgrouptasks.ViewGroupTasksController;
import interface_adapter.viewgrouptasks.ViewGroupTasksViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;

/**
 * Dashboard (formerly LoggedInView):.
 * - Left sidebar: user-specific groups
 * - Right area: per-group working area with tabs (placeholders for now)
 * - Header with username + logout button
 * - Change-password inline control
 */
public class DashboardView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "dashboard";
    private final DashboardViewModel dashboardViewModel;
    private static final String HOME = "Home";
    private ViewGroupTasksViewModel viewGroupTasksViewModel;
    private EditGroupTaskViewModel editGroupTasksViewModel;
    private CreateGroupTasksViewModel createGroupTasksViewModel;

    // Views
    private ViewTasksView viewTasksView;

    // Controllers
    private LogoutController logoutController;
    private CreateGroupController createGroupController;
    private ViewGroupTasksController viewGroupTasksController;
    private EditGroupTaskController editGroupTaskController;
    private CreateGroupTasksController createGroupTasksController;

    // Header widgets
    private final JLabel usernameLabel = new JLabel();
    private JButton logoutButton;
    private JButton createGroup;

    // Main layout pieces
    private final DefaultListModel<String> groupsModel = new DefaultListModel<>();
    private final JList<String> groupsList = new JList<>(groupsModel);
    private final CardLayout cards = new CardLayout();
    private final JPanel workArea = new JPanel(cards);

    public DashboardView(DashboardViewModel dashboardViewModel, ViewTasksView viewTasksView) {
        this.dashboardViewModel = Objects.requireNonNull(dashboardViewModel);
        this.dashboardViewModel.addPropertyChangeListener(this);
        this.viewTasksView = Objects.requireNonNull(viewTasksView);

        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // Header
        add(buildHeader(), BorderLayout.NORTH);

        logoutButton.addActionListener(this);

        // Body: sidebar + workspace
        add(buildBody(), BorderLayout.CENTER);

        // Sidebar behavior
        groupsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        groupsList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                String sel = groupsList.getSelectedValue();
                if (sel != null) {
                    cards.show(workArea, sel);
                }
            }
        });

        // TODO: switch to dynamic user data 1.
        setGroups(List.of("Group 1", "Group 2", "Group 3"));
        groupsList.setSelectedIndex(0);
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(6, 6, 6, 6));
        header.setBackground(new Color(247, 248, 250));

        JLabel title = new JLabel("Group Workspace");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        header.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(new JLabel("User: "));
        right.add(usernameLabel);

        logoutButton = new JButton("Log Out");
        right.add(logoutButton);

        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JComponent buildBody() {
        // Sidebar (groups)
        JScrollPane sidebar = new JScrollPane(groupsList);
        sidebar.setPreferredSize(new Dimension(200, 500));

        // Workspace (cards)
        workArea.setBorder(new EmptyBorder(0, 0, 0, 0));
        workArea.add(buildHomePanel(), HOME);

        // TODO: switch to dynamic user data 2.
        // for (String groupName : currentUser.getGroups()) {
        // workArea.add(createGroupPanel(groupName), groupName);
        // }

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, workArea);
        split.setDividerLocation(200);
        split.setResizeWeight(0);
        return split;
    }

    private JPanel buildHomePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new EmptyBorder(12, 12, 12, 12));

        if (viewTasksView != null) {
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.setBorder(new EmptyBorder(0, 12, 0, 0));

            JLabel tasks = new JLabel("Your Tasks:");
            tasks.setBorder(new EmptyBorder(0, 0, 8, 0));
            rightPanel.add(tasks, BorderLayout.NORTH);

            rightPanel.add(viewTasksView, BorderLayout.CENTER);

            p.add(rightPanel, BorderLayout.EAST);
        }

        p.add(new JLabel("Welcome! Select a group to view its workspace."), BorderLayout.NORTH);
        createGroup = new JButton("Create Group");
        p.add(createGroup);
        createGroup.addActionListener(
                evt -> {
                    if (evt.getSource().equals(createGroup)) {
                        createGroupController.openCreateGroupModal();
                    }
                }
        );
        return p;
    }

    private JPanel createGroupPanel(String name) {
        final JPanel groupPanel = new JPanel(new BorderLayout());

        // Top header
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel title = new JLabel(name + "  [code]");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        header.add(title, BorderLayout.WEST);
        header.add(new JLabel("\uD83D\uDC64"), BorderLayout.EAST);
        groupPanel.add(header, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.addChangeListener(event -> {
            for (int i = 0; i < tabs.getTabCount(); i++) {
                if (i == tabs.getSelectedIndex()) {
                    tabs.setForegroundAt(i, new Color(0x1E88E5));
                } else {
                    tabs.setForegroundAt(i, Color.DARK_GRAY);
                }
            }
        });

        tabs.addTab(HOME, placeholderPanel("Home panel for " + name));
        tabs.addTab("People", new PeopleTabPanel(name));
        tabs.addTab("Meets", placeholderPanel("Meetings tab for " + name));
        tabs.addTab("Tasks", placeholderPanel("Tasks tab for " + name));
        tabs.addTab("Sched", placeholderPanel("Schedule tab for " + name));
        tabs.addTab("Optional", placeholderPanel("Optional tab for " + name));
        // initialize selected color (default white is hard to see)
        tabs.setForegroundAt(0, new Color(0x1E88E5));

        groupPanel.add(tabs, BorderLayout.CENTER);

        tabs.addChangeListener(event -> {
            int index = tabs.getSelectedIndex();
            String tabTitle = tabs.getTitleAt(index);

            if ("Tasks".equals(tabTitle)) {
                Component current = tabs.getComponentAt(index);

                if (current instanceof JPanel || current instanceof JScrollPane) {
                    tabs.setComponentAt(index, buildGroupTasksTab());
                }
            }
        });
        return groupPanel;
    }

    private JPanel placeholderPanel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea(text + "\n\nThis is a placeholder panel.");
        area.setEditable(false);
        area.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(area, BorderLayout.CENTER);
        return panel;
    }

    // --- Public API for AppBuilder / Controllers ---------------------------

    public String getViewName() {
        return viewName;
    }

    // TODO: add logout implementation
    // public void setLogoutController(LogoutController c) {
    // this.logoutController = c;
    // }

    /**
     * Allows the presenter to push the user's groups once loaded.
     * 
     * @param groups The list of groups to be pushed
     */
    // TODO: need to break this up into helper methods
    public void setGroups(List<String> groups) {
        groupsModel.clear();
        // always pin Home on top
        groupsModel.addElement(HOME);

        if (groups != null && !groups.isEmpty()) {
            // sort + dedupe case-insensitively, and drop "Home"
            SortedSet<String> sorted = new java.util.TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            for (String g : groups) {
                if (g != null && !HOME.equalsIgnoreCase(g)) {
                    sorted.add(g);
                }
            }
            // add sorted groups below Home
            for (String g : sorted) {
                groupsModel.addElement(g);
            }
        }

        // sync workArea cards to match the model
        // + remove cards that no longer exist in the model
        java.util.Set<String> wanted = new java.util.LinkedHashSet<>();
        for (int i = 0; i < groupsModel.size(); i++) {
            wanted.add(groupsModel.get(i));
        }

        java.util.List<Component> toRemove = new java.util.ArrayList<>();
        for (Component c : workArea.getComponents()) {
            String name = c.getName();
            if (name != null && !wanted.contains(name)) {
                toRemove.add(c);
            }
        }
        for (Component c : toRemove) {
            workArea.remove(c);
        }

        // ensure each non-Home group has a panel
        for (int i = 0; i < groupsModel.size(); i++) {
            String key = groupsModel.get(i);
            if (HOME.equals(key)) {
                continue;
            }

            boolean exists = false;
            for (Component c : workArea.getComponents()) {
                if (key.equals(c.getName())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                JPanel gp = createGroupPanel(key);
                gp.setName(key);
                workArea.add(gp, key);
            }
        }

        if (groupsList.getModel().getSize() > 0) {
            groupsList.setSelectedIndex(0);
        }
    }

    // --- Events -------------------------------------------------------------

    /** Logout button. */
    @Override
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
        logoutController.execute();
    }

    /** React to presenter updates. */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            LoggedInState st = (LoggedInState) evt.getNewValue();
            usernameLabel.setText(st.getUsername());
        } else if ("password".equals(evt.getPropertyName())) {
            LoggedInState st = (LoggedInState) evt.getNewValue();
            if (st.getPasswordError() == null) {
                JOptionPane.showMessageDialog(this, "password updated for " + st.getUsername());
                // passwordInputField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, st.getPasswordError());
            }
        }
    }

    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }

    public void setCreateGroupController(CreateGroupController createGroupController) {
        this.createGroupController = createGroupController;
    }

    public void setViewGroupTasksController(ViewGroupTasksController controller) {
        this.viewGroupTasksController = controller;
    }

    public void setEditGroupTaskController(EditGroupTaskController controller) {
        this.editGroupTaskController = controller;
    }

    public void setCreateGroupTasksController(CreateGroupTasksController controller) {
        this.createGroupTasksController = controller;
    }

    public void setViewGroupTasksViewModel(ViewGroupTasksViewModel viewModel) {
        this.viewGroupTasksViewModel = viewModel;
    }

    public void setEditGroupTasksViewModel(EditGroupTaskViewModel viewModel) {
        this.editGroupTasksViewModel = viewModel;
    }

    public void setCreateGroupTasksViewModel(CreateGroupTasksViewModel viewModel) {
        this.createGroupTasksViewModel = viewModel;
    }

    private JPanel buildGroupTasksTab() {
        if (viewGroupTasksViewModel == null
                || editGroupTasksViewModel == null
                || createGroupTasksViewModel == null
                || viewGroupTasksController == null
                || editGroupTaskController == null
                || createGroupTasksController == null) {

            JPanel p = new JPanel();
            p.add(new JLabel("Tasks system not initialized yet."));
            return p;
        }
        return new GroupTasksView(viewGroupTasksViewModel, editGroupTasksViewModel, createGroupTasksViewModel,
                viewGroupTasksController, editGroupTaskController, createGroupTasksController);
    }
}
