package view;

import data_access.DBMeetingDataAccessObject;
import interface_adapter.create_group.CreateGroupController;
import interface_adapter.creategrouptasks.CreateGroupTasksController;
import interface_adapter.creategrouptasks.CreateGroupTasksViewModel;
import interface_adapter.createmeeting.CreateMeetingViewFactory;
import interface_adapter.dashboard.DashboardViewModel;
import interface_adapter.dashboard.LoggedInState;
import interface_adapter.editgrouptask.EditGroupTaskController;
import interface_adapter.editgrouptask.EditGroupTaskViewModel;
import interface_adapter.logout.LogoutController;
import interface_adapter.joingroup.JoinGroupController;
import interface_adapter.manage_members.PeopleTabViewModel;
import interface_adapter.manage_members.remove_member.RemoveMemberControllerFactory;
import interface_adapter.manage_members.respond_request.RespondRequestControllerFactory;
import interface_adapter.manage_members.update_role.UpdateRoleControllerFactory;
import interface_adapter.manage_members.view_members.ViewMembersControllerFactory;
import interface_adapter.manage_members.view_pending.ViewPendingControllerFactory;
import interface_adapter.viewgrouptasks.ViewGroupTasksController;
import interface_adapter.viewgrouptasks.ViewGroupTasksViewModel;
import interface_adapter.createmeeting.CreateMeetingController;
import interface_adapter.createmeeting.CreateMeetingViewModel;

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
 * Dashboard (formerly LoggedInView).
 * - Left sidebar: user specific groups
 * - Right area: per group working area with tabs
 * - Header with username and logout button
 */
public class DashboardView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "dashboard";
    private final DashboardViewModel dashboardViewModel;
    private static final String HOME = "Home";
    private ViewGroupTasksViewModel viewGroupTasksViewModel;
    private EditGroupTaskViewModel editGroupTasksViewModel;
    private CreateGroupTasksViewModel createGroupTasksViewModel;
    private CreateMeetingViewFactory createMeetingViewFactory;

    // Views
    private ViewTasksView viewTasksView;
    private CreateMeetingViewModel createMeetingViewModel;

    // Controllers
    private LogoutController logoutController;
    private CreateGroupController createGroupController;
    private JoinGroupController joinGroupController;
    private ViewGroupTasksController viewGroupTasksController;
    private EditGroupTaskController editGroupTaskController;
    private CreateGroupTasksController createGroupTasksController;
    private CreateMeetingController createMeetingController;


    // Header widgets
    private final JLabel usernameLabel = new JLabel();
    private JButton logoutButton;

    // Main layout pieces
    // groupsModel stores group IDs plus "Home"
    private final DefaultListModel<String> groupsModel = new DefaultListModel<>();
    private final JList<String> groupsList = new JList<>(groupsModel);
    private final CardLayout cards = new CardLayout();
    private final JPanel workArea = new JPanel(cards);

    private ViewMembersControllerFactory viewMembersControllerFactory;
    private ViewPendingControllerFactory viewPendingControllerFactory;
    private RemoveMemberControllerFactory removeMemberControllerFactory;
    private RespondRequestControllerFactory respondRequestControllerFactory;
    private UpdateRoleControllerFactory updateRoleControllerFactory;

    // Maps group IDs to their names
    private final java.util.Map<String, String> groupIdToName = new java.util.HashMap<>();
    private String currentUsername;
    private DBMeetingDataAccessObject meetingDataAccessObject;

    public DashboardView(DashboardViewModel dashboardViewModel, ViewTasksView viewTasksView) {
        this.dashboardViewModel = Objects.requireNonNull(dashboardViewModel);
        this.dashboardViewModel.addPropertyChangeListener(this);
        this.viewTasksView = Objects.requireNonNull(viewTasksView);

        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // Header
        add(buildHeader(), BorderLayout.NORTH);

        logoutButton.addActionListener(this);

        // Body: sidebar plus workspace
        add(buildBody(), BorderLayout.CENTER);

        // Sidebar behavior
        groupsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        groupsList.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                String sel = groupsList.getSelectedValue();
                if (sel != null) {
                    cards.show(workArea, sel);
                    Component panel = null;
                    for (Component c : workArea.getComponents()) {
                        if (sel.equals(c.getName())) {
                            panel = c;
                            break;
                        }
                    }

                    updateTasksTabIfPossible(panel, sel);
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
                    } else {
                        displayText = idOrHome;
                    }
                }

                return super.getListCellRendererComponent(
                        list, displayText, index, isSelected, cellHasFocus);
            }
        });
    }

    private void updateTasksTabIfPossible(Component panel, String sel) {
        if (panel instanceof JPanel) {
            JPanel panel2 = (JPanel) panel;
            JTabbedPane tabs = findTabbedPane(panel2);
            if (tabs != null) {
                int idx = tabs.indexOfTab("Tasks");
                if (idx != -1) {
                    tabs.setComponentAt(idx, buildGroupTasksTab(sel));
                }
            }
        }
    }

    private JTabbedPane findTabbedPane(Container root) {
        for (Component c : root.getComponents()) {
            if (c instanceof JTabbedPane) {
                return (JTabbedPane) c;
            }
            if (c instanceof Container) {
                JTabbedPane found = findTabbedPane((Container) c);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(6, 6, 6, 6));

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
        ActionListener createGroupListener = evt -> {
            if (createGroupController != null) {
                createGroupController.openCreateGroupModal();
            }
        };

        ActionListener joinGroupListener = evt -> {
            if (joinGroupController != null) {
                String code = JOptionPane.showInputDialog(
                        this,
                        "Enter Group ID:",
                        "Join Group",
                        JOptionPane.PLAIN_MESSAGE
                );
                if (code != null && !code.trim().isEmpty()) {
                    joinGroupController.execute(code.trim());
                }
            }
        };

        return new DashboardHomePanel(viewTasksView, createGroupListener, joinGroupListener);
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
                    tabs.setForegroundAt(i, Color.WHITE);
                }
            }
        });

        tabs.addTab(HOME, placeholderPanel("Home panel for " + groupName));
        tabs.addTab("People", createPeopleTab(groupId));
        tabs.addTab("Meets", createMeetingTab(groupId));
        tabs.addTab("Tasks", placeholderPanel("Tasks tab for " + groupName));
        tabs.addTab("Sched", new ScheduleTabView("Schedule tab for " + groupName));
        tabs.addTab("Optional", placeholderPanel("Optional tab for " + groupName));
        tabs.setForegroundAt(0, new Color(0x1E88E5));

        groupPanel.add(tabs, BorderLayout.CENTER);
        return groupPanel;
    }

    private PeopleTabView createPeopleTab(String groupId) {
        PeopleTabViewModel vm = new PeopleTabViewModel();
        PeopleTabView view = new PeopleTabView(vm, groupId, currentUsername);

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
        if (removeMemberControllerFactory != null) {
            view.setRemoveMemberController(
                    removeMemberControllerFactory.create(vm)
            );
        }

        if (respondRequestControllerFactory != null) {
            view.setRespondRequestController(
                    respondRequestControllerFactory.create(vm)
            );
        }

        if (updateRoleControllerFactory != null) {
            view.setUpdateRoleController(
                    updateRoleControllerFactory.create(vm)
            );
        }

        return view;
    }

    private JPanel placeholderPanel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea(text + "\n\nThis is a placeholder panel.");
        area.setEditable(false);
        area.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(area, BorderLayout.CENTER);
        return panel;
    }

    private CreateMeetingView createMeetingTab(String groupId) {
        String groupName = groupIdToName.get(groupId);
        if (groupName == null) {
            groupName = groupId;
        }

        return createMeetingViewFactory.create(groupId, groupName);
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

    @Override
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
        logoutController.execute();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            LoggedInState st = (LoggedInState) evt.getNewValue();
            currentUsername = st.getUsername();
            usernameLabel.setText(currentUsername);
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

    public void setJoinGroupController(JoinGroupController joinGroupController) {
        this.joinGroupController = joinGroupController;
    }

    public void setCreateMeetingController(CreateMeetingController controller) {
        this.createMeetingController = controller;
    }

    public void setCreateMeetingViewModel(CreateMeetingViewModel viewModel) {
        this.createMeetingViewModel = viewModel;
    }

    public void setMeetingDataAccessObject(DBMeetingDataAccessObject meetingDataAccessObject) {
        this.meetingDataAccessObject = meetingDataAccessObject;
    }

    public void setCreateMeetingViewFactory(CreateMeetingViewFactory factory) {
        this.createMeetingViewFactory = factory;
    }

    public void setViewMembersControllerFactory(ViewMembersControllerFactory factory) {
        this.viewMembersControllerFactory = factory;
    }

    public void setViewPendingControllerFactory(ViewPendingControllerFactory factory) {
        this.viewPendingControllerFactory = factory;
    }

    public void setRemoveMemberControllerFactory(RemoveMemberControllerFactory factory) {
        this.removeMemberControllerFactory = factory;
    }

    public void setRespondRequestControllerFactory(RespondRequestControllerFactory factory) {
        this.respondRequestControllerFactory = factory;
    }

    public void setUpdateRoleControllerFactory(UpdateRoleControllerFactory factory) {
        this.updateRoleControllerFactory = factory;
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
