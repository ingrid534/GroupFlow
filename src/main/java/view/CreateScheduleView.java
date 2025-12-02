package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import interface_adapter.schedule.create_schedule.CreateScheduleController;
import interface_adapter.schedule.create_schedule.CreateScheduleState;
import interface_adapter.schedule.create_schedule.CreateScheduleViewModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class CreateScheduleView extends JPanel implements PropertyChangeListener {

    private final String viewName = "create schedule";
    private final CreateScheduleViewModel createScheduleViewModel;

    private final JLabel errorField = new JLabel();
    private final JLabel title;

    private JButton[][] buttons;
    private final JButton createSchedule;

    private CreateScheduleController createScheduleController;

    public CreateScheduleView(CreateScheduleViewModel createScheduleViewModel) {
        this.createScheduleViewModel = createScheduleViewModel;
        this.createScheduleViewModel.addPropertyChangeListener(this);

        title = new JLabel("Select your available time slots.");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        createSchedule = new JButton("Create Schedule");
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

        createSchedule.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(createSchedule);

        JPanel buttonPanel = new JPanel(new GridLayout(7, 12, 1, 1));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttons = new JButton[7][12];

        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 12; col++) {
                JButton cell = new JButton();
                cell.setPreferredSize(new Dimension(40, 30));
                cell.setBorder(new LineBorder(Color.BLACK, 1));
                cell.setMargin(new Insets(0, 0, 0, 0));

                // when a user clicks a button, should toggle green/white
                final int r = row;
                final int c = col;
                cell.addActionListener(evt -> {
                    boolean[][] slots = createScheduleViewModel.getState().getSelectedSlots();
                    slots[r][c] = !slots[r][c];
                    if (slots[r][c]) {
                        cell.setBackground(Color.GREEN);
                    } else {
                        cell.setBackground(Color.WHITE);
                    }
                });

                buttons[row][col] = cell;
                buttonPanel.add(cell);
            }
        }

        this.add(buttonPanel);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final CreateScheduleState state = (CreateScheduleState) evt.getNewValue();
        setFields(state);

        errorField.setText(state.getError());
    }

    /**
     * Updates the input fields based on the given state.
     *
     * @param state the current state of the Create Schedule view
     */
    private void setFields(CreateScheduleState state) {
        // TODO: move this to the GroupScheduleView?
        // for (int row = 0; row < 7; row++) {
        //     for (int col = 0; col < 12; col++) {
        //         if (buttons[row][col] != null) {
        //             buttons[row][col].setBackground(schedule[row][col]);
        //         }
        //     }
        // }

        errorField.setText(state.getError());
    }

    /**
     * Returns the name of the view.
     *
     * @return the name of the view
     */
    public String getViewName() {
        return viewName;
    }

    /**
     * Sets the Create Schedule controller and attaches listeners to the buttons.
     *
     * @param scheduleController the controller for handling Create Schedule actions
     */
    public void setCreateScheduleController(CreateScheduleController scheduleController) {
        this.createScheduleController = scheduleController;
        attachListeners();
    }

    /**
     * Attach listeners to the Create Schedule button.
     */
    public void attachListeners() {
        createSchedule.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (!evt.getSource().equals(createSchedule)) {
                        return;
                    }

                    final CreateScheduleState currentState = createScheduleViewModel.getState();

                    createScheduleController.execute(
                                currentState.getSelectedSlots()
                    );
                }
            }
        );
    }

    /**
     * Hooks the Create Schedule modal to the application frame.
     * This method listens for changes in the view model to open or close the modal.
     *
     * @param application the main application frame
     */
    public void hookCreateScheduleModalOpen(JFrame application) {
        JDialog dialog = new JDialog(application, "Create Schedule", true);
        createScheduleViewModel.addPropertyChangeListener(evt -> {
            if ("openModal".equals(evt.getPropertyName())) {
                if (createScheduleViewModel.getState().getOpenModal()) {
                    openCreateScheduleModal(dialog, application);
                } else {
                    dialog.dispose();
                }
            }
        });
    }

    /**
     * Opens the Create Group modal dialog.
     *
     * @param dialog the modal dialog to display
     * @param parentFrame the parent frame of the modal dialog
     */
    private void openCreateScheduleModal(JDialog dialog, JFrame parentFrame) {
        dialog.setMinimumSize(new Dimension(400, 250));
        dialog.setContentPane(this);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.pack();
        dialog.setVisible(true);
    }
}
