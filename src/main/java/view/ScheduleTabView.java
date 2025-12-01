package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import interface_adapter.create_schedule.CreateScheduleController;

public class ScheduleTabView extends JPanel {
    private final JButton createSched = new JButton("Add Your Availability");

    private CreateScheduleController createScheduleController;

    public ScheduleTabView(String groupName) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        addCreateSchedButton();
        addGroupSched(groupName);
    }

    /**
     * Make the button where user can input availability.
     */
    public void addCreateSchedButton() {
        createSched.setAlignmentY(Component.TOP_ALIGNMENT);
        createSched.setMaximumSize(new Dimension(180, 20));
        add(createSched);

        createSched.addActionListener(evt -> createScheduleController.openScheduleModal());
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

    public void setCreateScheduleController(CreateScheduleController createScheduleController) {
        this.createScheduleController = createScheduleController;
    }

}
