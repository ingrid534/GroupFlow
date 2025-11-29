package view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import interface_adapter.create_schedule.CreateScheduleViewModel;

public class CreateScheduleView extends JPanel{

    private final String viewName = "create schedule";
    private final CreateScheduleViewModel createScheduleViewModel;

    private final JLabel title;
    private JButton[][] buttons;

    public CreateScheduleView(CreateScheduleViewModel createScheduleViewModel) {
        this.createScheduleViewModel = createScheduleViewModel;

        title = new JLabel("Select your available time slots.");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        buildLayout();
    }

    /**
     * Build the layout for this view.
     */
    public void buildLayout() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setMinimumSize(new Dimension(300, 200));
        // Add rigid areas for spacing
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(title);
    }
}
