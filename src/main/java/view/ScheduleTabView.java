package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import interface_adapter.schedule.create_schedule.CreateScheduleController;
import interface_adapter.schedule.view_schedule.ScheduleTabViewModel;

public class ScheduleTabView extends JPanel {
    private final JButton createSched = new JButton("Add Your Availability");

    private CreateScheduleController createScheduleController;
    private ScheduleTabViewModel viewModel;
    private final String groupId;

    public ScheduleTabView(String groupName, ScheduleTabViewModel viewModel, String groupId) {
        this.groupId = groupId;
        this.viewModel = viewModel;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        buildScheduleDisplay();
        addGroupSched(groupName);
    }

    /**
     * Make the button where user can input availability.
     */
    public void addCreateSchedButton() {
        createSched.setAlignmentY(Component.TOP_ALIGNMENT);
        createSched.setMaximumSize(new Dimension(180, 20));
        add(createSched);

        createSched.addActionListener(evt -> {
            if (createScheduleController != null) {
                createScheduleController.openScheduleModal();
            }
        });
    }

    public ScheduleTabViewModel getViewModel() {
        return viewModel;
    }

    /**
     * Add the group availability display.
     * @param groupName The label for this panel.
     */
    public void addGroupSched(String groupName) {
        JPanel groupSched = new JPanel();
        groupSched.add(new JLabel(groupName));
        groupSched.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(groupSched);
    }

    /**
     * Set the schedule controller.
     * @param createScheduleController controller
     */
    public void setCreateScheduleController(CreateScheduleController createScheduleController) {
        System.out.println("create schedule controller set");
        this.createScheduleController = createScheduleController;
    }

    public String getGroupId() {
        return groupId;
    }

    private void buildScheduleDisplay() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel title = new JLabel("Group Schedule", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        this.add(title);
        this.add(Box.createRigidArea(new Dimension(0, 10)));

        createSched.setAlignmentX(Component.CENTER_ALIGNMENT);
        createSched.addActionListener(evt -> {
            if (createScheduleController != null) {
                // Set the groupId in the view model state before opening modal
                viewModel.getState().setGroupId(groupId);
                createScheduleController.openScheduleModal();
            }
        });
        this.add(createSched);
        this.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel mainPanel = new JPanel(new BorderLayout());

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        JPanel topRow = buildDaysRow(days);
        mainPanel.add(topRow, BorderLayout.NORTH);

        JPanel schedulePanel = new JPanel(new GridLayout(12, 8, 1, 1));
        schedulePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        buildGrid(schedulePanel);
        mainPanel.add(schedulePanel, BorderLayout.CENTER);
        this.add(mainPanel);
        
    }

    private JPanel buildDaysRow(String[] days) {
        JPanel row = new JPanel(new GridLayout(1, 8));
        row.add(new JLabel(""));

        for (String day: days) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setBorder(new LineBorder(Color.BLACK));
            row.add(label);
        }
        return row;
    }

    private void buildGrid(JPanel panel) {
        int start = 8;
        Color[][] slots = viewModel.getState().getMasterSchedule();

        for (int r = 0; r < 12; r++) {
            int hour = start + r;
            JLabel hourLabel = new JLabel(hour + ":00", SwingConstants.CENTER);
            hourLabel.setBorder(new LineBorder(Color.BLACK));
            panel.add(hourLabel);

            for (int c = 0; c < 7; c++) {
                JPanel cell = new JPanel();
                cell.setPreferredSize(new Dimension(40, 30));
                cell.setBorder(new LineBorder(Color.BLACK, 1));
                cell.setBackground(slots[r][c]);
                panel.add(cell);


            }
        }
    }

}
