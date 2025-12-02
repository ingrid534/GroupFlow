package view;

import data_access.DBMeetingDataAccessObject;
import interface_adapter.createmeeting.CreateMeetingController;
import interface_adapter.createmeeting.CreateMeetingState;
import interface_adapter.createmeeting.CreateMeetingViewModel;
import use_case.view_meeting.ViewMeetingsOutputData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * The CreateMeetingView class represents the user interface for creating a meeting.
 * It provides input fields for description and date, as well as buttons to create or cancel.
 * Displays meetings from the ViewModel state instead of querying the database.
 */
public class CreateMeetingView extends JPanel implements ActionListener, PropertyChangeListener {
    private final String viewName = "create meeting";
    private final CreateMeetingViewModel createMeetingViewModel;
    private JDialog dialog;
    private final JTextField descriptionInputField = new JTextField(25);
    private final JTextField dateInputField = new JTextField(20);
    private final JLabel title;
    private final JButton createMeetingButton;
    private final JButton cancel;
    private final JLabel errorField = new JLabel();
    private final JPanel descriptionInfo;
    private final JPanel dateInfo;
    private JTextArea meetingsDisplay;
    private CreateMeetingController createMeetingController;
    private String groupId;

    public CreateMeetingView(String groupId, String groupName, CreateMeetingController createMeetingController,
                             CreateMeetingViewModel createMeetingViewModel,
                             DBMeetingDataAccessObject meetingDataAccessObject) {
        this.groupId = groupId;
        this.createMeetingViewModel = createMeetingViewModel;
        this.createMeetingViewModel.addPropertyChangeListener(this);

        title = new JLabel("Enter meeting details.");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setMaximumSize(new Dimension(Integer.MAX_VALUE, title.getPreferredSize().height));

        styleFields();
        descriptionInfo = createDescriptionPanel();
        dateInfo = createDatePanel();
        cancel = new JButton("Cancel");
        createMeetingButton = new JButton("Create Meeting");

        errorField.setForeground(Color.RED);
        errorField.setSize(new Dimension(errorField.getPreferredSize().width, 30));
        errorField.setAlignmentX(Component.LEFT_ALIGNMENT);

        buildLayout();
        addDocumentListeners();

        // Load initial meetings from database and populate state
        loadInitialMeetings(groupId, meetingDataAccessObject);

        // Refresh display from state
        refreshMeetingsDisplay();
    }

    private JPanel createDescriptionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel("Meeting Description");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionInputField.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionInputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(label);
        panel.add(descriptionInputField);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    /**
     * Load initial meetings for this group from the database and store in state.
     */
    private void loadInitialMeetings(String groupId, DBMeetingDataAccessObject meetingDataAccessObject) {
        try {
            List<entity.meeting.Meeting> dbMeetings = meetingDataAccessObject.getMeetingsForGroup(groupId);
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            List<use_case.view_meeting.ViewMeetingsOutputData.MeetingDTO> dtos = new java.util.ArrayList<>();

            if (dbMeetings != null) {
                for (entity.meeting.Meeting meeting : dbMeetings) {
                    String dateString = meeting.hasDate() && meeting.getDate().isPresent()
                            ? meeting.getDate().get().format(fmt)
                            : "No date";
                    dtos.add(new use_case.view_meeting.ViewMeetingsOutputData.MeetingDTO(
                            meeting.getID(),
                            meeting.getDescription(),
                            dateString
                    ));
                }
            }

            // Store meetings in state
            CreateMeetingState state = createMeetingViewModel.getState();
            state.setMeetings(dtos);
            createMeetingViewModel.setState(state);

        } catch (Exception e) {
            System.err.println("ERROR loading initial meetings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JPanel createDatePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel("Meeting date (yyyy-MM-dd HH:mm)");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateInputField.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateInputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(label);
        panel.add(dateInputField);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    private void buildLayout() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 50, 40, 50));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new EmptyBorder(0, 0, 0, 15));

        JLabel formTitle = new JLabel("Create New Meeting");
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formTitle.setFont(formTitle.getFont().deriveFont(Font.BOLD, 18f));

        leftPanel.add(formTitle);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(title);
        leftPanel.add(Box.createVerticalStrut(30));
        leftPanel.add(descriptionInfo);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(dateInfo);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(errorField);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(createMeetingButton);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(cancel);
        leftPanel.add(Box.createVerticalGlue());

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(new EmptyBorder(0, 20, 0, 0));

        JLabel meetingsTitle = new JLabel("Submitted Meetings");
        meetingsTitle.setFont(meetingsTitle.getFont().deriveFont(Font.BOLD, 18f));
        rightPanel.add(meetingsTitle, BorderLayout.NORTH);

        meetingsDisplay = new JTextArea();
        meetingsDisplay.setEditable(false);
        meetingsDisplay.setBorder(new EmptyBorder(10, 10, 10, 10));
        meetingsDisplay.setLineWrap(true);
        meetingsDisplay.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(meetingsDisplay);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(0.5);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        descriptionInfo.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, descriptionInfo.getPreferredSize().height));
        dateInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, dateInfo.getPreferredSize().height));
    }

    private void addDocumentListeners() {
        descriptionInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateState() {
                CreateMeetingState state = createMeetingViewModel.getState();
                state.setDescription(descriptionInputField.getText());
                createMeetingViewModel.setState(state);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateState();
            }
        });

        dateInputField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateState() {
                CreateMeetingState state = createMeetingViewModel.getState();
                state.setDate(dateInputField.getText());
                createMeetingViewModel.setState(state);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateState();
            }
        });
    }

    /**
     * Refreshes the meetings display from the ViewModel state.
     * Does NOT query the database - uses data pushed by the presenter.
     */
    private void refreshMeetingsDisplay() {
        try {
            CreateMeetingState state = createMeetingViewModel.getState();
            List<ViewMeetingsOutputData.MeetingDTO> meetings = state.getMeetings();
            if (meetings != null && !meetings.isEmpty()) {
                for (var m : meetings) {
                    System.out.println("  - " + m.getDescription());
                }
            }

            StringBuilder str = new StringBuilder();
            if (meetings == null || meetings.isEmpty()) {
                str.append("No meetings scheduled yet.\n\n");
                str.append("Submit a meeting on the left to get started!");
            }
            else {
                str.append("SCHEDULED MEETINGS\n");
                str.append("=================\n\n");

                for (int i = 0; i < meetings.size(); i++) {
                    ViewMeetingsOutputData.MeetingDTO meeting = meetings.get(i);
                    str.append((i + 1)).append(". ").append(meeting.getDescription()).append("\n");
                    str.append("   Date: ").append(meeting.getDateString()).append("\n\n");
                }
            }

            meetingsDisplay.setText(str.toString());
            meetingsDisplay.setCaretPosition(0);

        } catch (Exception e) {
            String errorMsg = "Error displaying meetings: " + e.getClass().getSimpleName() + " - " + e.getMessage();
            meetingsDisplay.setText(errorMsg);
            e.printStackTrace();
        }
    }

    private void styleFields() {
        // Styling can be added here if needed
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        System.out.println("Click " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String prop = evt.getPropertyName();

        if ("state".equals(prop)) {
            // Handle create meeting state changes
            CreateMeetingState state = (CreateMeetingState) evt.getNewValue();
            setFields(state);
            errorField.setText(state.getMessage());

            if (state.isSuccess()) {
                String text;
                if (state.getMessage() != null && !state.getMessage().isEmpty()) {
                    text = state.getMessage();
                } else {
                    text = "Meeting successfully created.";
                }

                JOptionPane.showMessageDialog(
                        this,
                        text,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                descriptionInputField.setText("");
                dateInputField.setText("");
            }
        } else if ("meetings_updated".equals(prop)) {
            refreshMeetingsDisplay();
        }
    }

    private void setFields(CreateMeetingState state) {
        descriptionInputField.setText(state.getDescription());
        dateInputField.setText(state.getDate());
    }

    public String getViewName() {
        return viewName;
    }

    public void setCreateMeetingController(CreateMeetingController createMeetingController, String groupId) {
        this.createMeetingController = createMeetingController;
        this.groupId = groupId;
        attachListeners();
    }

    private void attachListeners() {
        createMeetingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (!evt.getSource().equals(createMeetingButton)) {
                    return;
                }

                CreateMeetingState state = createMeetingViewModel.getState();
                String description = state.getDescription();

                if (description == null || description.trim().isEmpty()) {
                    errorField.setText("Description cannot be empty.");
                    return;
                }

                String due = state.getDate();
                if (due != null && due.trim().isEmpty()) {
                    due = null;
                }

                createMeetingController.execute(description, due, groupId);
            }
        });

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                WindowEvent we = new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING);
                dialog.dispatchEvent(we);
                createMeetingViewModel.setState(new CreateMeetingState());
            }
        });
    }

    public void hookCreateMeetingModalOpen(JFrame application) {
        dialog = new JDialog(application, "Create Meeting", true);
        createMeetingViewModel.addPropertyChangeListener(evt -> {
            if ("openModal".equals(evt.getPropertyName())) {
                if (createMeetingViewModel.getState().getOpenModal()) {
                    openCreateMeetingModal(application);
                } else {
                    dialog.dispose();
                }
            }
        });
    }

    private void openCreateMeetingModal(JFrame parentFrame) {
        dialog.setMinimumSize(new Dimension(500, 380));
        dialog.setContentPane(this);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.pack();
        dialog.setVisible(true);
    }
}