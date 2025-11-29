package view;

import interface_adapter.create_group.CreateGroupController;
import interface_adapter.creategrouptasks.CreateGroupTasksController;
import interface_adapter.creategrouptasks.CreateGroupTasksViewModel;
import interface_adapter.dashboard.DashboardViewModel;
import interface_adapter.dashboard.LoggedInState;
import interface_adapter.editgrouptask.EditGroupTaskController;
import interface_adapter.editgrouptask.EditGroupTaskViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.manage_members.PeopleTabViewModel;
import interface_adapter.manage_members.view_members.ViewMembersControllerFactory;
import interface_adapter.manage_members.view_pending.ViewPendingControllerFactory;
import interface_adapter.viewgrouptasks.ViewGroupTasksController;
import interface_adapter.viewgrouptasks.ViewGroupTasksViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Objects;

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
    // groupsModel stores group IDs plus "Home"
    private final DefaultListModel<String> groupsModel = new DefaultListModel<>();
    private final JList<String> groupsList = new JList<>(groupsModel);
    private final CardLayout cards = new CardLayout();
    private final JPanel workArea = new JPanel(cards);

    private ViewMembersControllerFactory viewMembersControllerFactory;
    private ViewPendingControllerFactory viewPendingControllerFactory;

    // Maps group IDs to their names
    private final java.util.Map<String, String> groupIdToName = new java.util.HashMap<>();

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

        groupsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                String idOrHome = (String) value;
                String displayText;

                if (HOME.equals(idOrHome)) {
                    displayText = HOME;
                } else {
                    String name = groupIdToName.get(idOrHome);
                    if (name != null) {
                        displayText = name;
                    }
                    else {
                        displayText = idOrHome;
                    }
                }

                return super.getListCellRendererComponent(
                        list, displayText, index, isSelected, cellHasFocus);
            }
        });
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

    private JPanel createGroupPanel(String groupId) {
        final JPanel groupPanel = new JPanel(new BorderLayout());

        final String groupName = groupIdToName.get(groupId);

        // Top header
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel title = new JLabel(groupName + ": [" + groupId + "]");
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

        tabs.addTab(HOME, placeholderPanel("Home panel for " + groupName));
        tabs.addTab("People", createPeopleTab(groupId));
        tabs.addTab("Meets", placeholderPanel("Meetings tab for " + groupName));
        tabs.addTab("Tasks", placeholderPanel("Tasks tab for " + groupName));
        tabs.addTab("Sched", placeholderPanel("Schedule tab for " + groupName));
        tabs.addTab("Optional", placeholderPanel("Optional tab for " + groupName));
        // initialize selected color (default white is hard to see)
        tabs.setForegroundAt(0, new Color(0x1E88E5));

        groupPanel.add(tabs, BorderLayout.CENTER);

        tabs.addChangeListener(event -> {
            int index = tabs.getSelectedIndex();
            String tabTitle = tabs.getTitleAt(index);

            if ("Tasks".equals(tabTitle)) {
                Component current = tabs.getComponentAt(index);

                if (current instanceof JPanel || current instanceof JScrollPane) {
                    tabs.setComponentAt(index, buildGroupTasksTab(groupId));
                }
            }
        });
        return groupPanel;
    }

    private PeopleTabView createPeopleTab(String groupId) {
        PeopleTabViewModel vm = new PeopleTabViewModel();
        PeopleTabView view = new PeopleTabView(vm, groupId);

        if (viewMembersControllerFactory != null) {
            view.setViewMembersController(
                    viewMembersControllerFactory.create(vm)
            );
        }

        if (viewPendingControllerFactory != null) {
            view.setViewPendingController(
                    viewPendingControllerFactory.create(vm)
            );
        }

        return view;
    } // createPeopleTab

    private JPanel placeholderPanel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea(text + "\n\nThis is a placeholder panel.");
        area.setEditable(false);
        area.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(area, BorderLayout.CENTER);
        return panel;
    }

    // --- Public API for AppBuilder and Controllers --------------------------

    public String getViewName() {
        return viewName;
    }

    /**
     * Updates the list of groups in the sidebar and the corresponding cards
     * in the work area.
     * @param groups a map from group id to group name
     */
    public void setGroups(Map<String, String> groups) {
        clearGroupData();
        addHomeEntry();
        loadGroups(groups);
        syncWorkAreaWithModel();
        selectInitialGroup();
    }

    /**
     * Clears all group related state, including the list model
     * and the id to name map.
     */
    private void clearGroupData() {
        groupsModel.clear();
        groupIdToName.clear();
    }

    /**
     * Adds the "Home" entry to the list model.
     * This is always the first entry in the list.
     */
    private void addHomeEntry() {
        groupsModel.addElement(HOME);
    }

    /**
     * Loads groups from the provided map, sorts them by display name,
     * populates the id to name map, and appends the group ids
     * to the list model.
     *
     * @param groups a map from group id to group name, may be null or empty
     */
    private void loadGroups(Map<String, String> groups) {
        if (groups == null || groups.isEmpty()) {
            return;
        }

        java.util.List<Map.Entry<String, String>> entries =
                new java.util.ArrayList<>(groups.entrySet());

        // Sort by name, case-insensitive
        entries.sort((entry1, entry2) -> {
            String n1 = entry1.getValue();
            String n2 = entry2.getValue();

            if (n1 == null && n2 == null) {
                return 0;
            }
            if (n1 == null) {
                return 1;
            }
            if (n2 == null) {
                return -1;
            }

            return String.CASE_INSENSITIVE_ORDER.compare(n1, n2);
        });

        for (Map.Entry<String, String> entry : entries) {
            String id = entry.getKey();
            String name = entry.getValue();
            if (name != null && !HOME.equalsIgnoreCase(name)) {
                groupIdToName.put(id, name);
                groupsModel.addElement(id);
            }
        }
    }

    /**
     * Ensures that the work area contains cards for each group id
     * in the list model and removes any cards that no longer correspond
     * to a list entry.
     */
    private void syncWorkAreaWithModel() {
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
    }

    /**
     * Selects the first entry in the groups list, if any,
     * and shows the corresponding card in the work area.
     */
    private void selectInitialGroup() {
        if (groupsList.getModel().getSize() > 0) {
            groupsList.setSelectedIndex(0);
            cards.show(workArea, groupsModel.get(0));
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
            if (st.getGroups() != null) {
                setGroups(st.getGroups());
            }
        } else if ("password".equals(evt.getPropertyName())) {
            LoggedInState st = (LoggedInState) evt.getNewValue();
            if (st.getPasswordError() == null) {
                JOptionPane.showMessageDialog(this, "password updated for " + st.getUsername());
            } else {
                JOptionPane.showMessageDialog(this, st.getPasswordError());
            }
        } else if ("groups".equals(evt.getPropertyName())) {
            LoggedInState st = (LoggedInState) evt.getNewValue();
            setGroups(st.getGroups());
        }

    }

    public void setLogoutController(LogoutController logoutController) {
        this.logoutController = logoutController;
    }

    public void setCreateGroupController(CreateGroupController createGroupController) {
        this.createGroupController = createGroupController;
    }

    public void setViewMembersControllerFactory(ViewMembersControllerFactory factory) {
        this.viewMembersControllerFactory = factory;
    }

    public void setViewPendingControllerFactory(ViewPendingControllerFactory factory) {
        this.viewPendingControllerFactory = factory;
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

    private JPanel buildGroupTasksTab(String groupId) {
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
                viewGroupTasksController, editGroupTaskController, createGroupTasksController, groupId);
    }
}
