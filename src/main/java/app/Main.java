package app;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;

public class Main {

    /**
     * Main method.
     *
     * @param args Arguments.
     */
    public static void main(String[] args) {
        final AppBuilder appBuilder = new AppBuilder();

        FlatDarkLaf.setup();

        final JFrame application = appBuilder
                .addLoginView()
                .addSignupView()
                .addViewTasksUseCase()
                .addDashboardView()
                .addCreateGroupView()
                .addJoinGroupView()
                .addSignupUseCase()
                .addLoginUseCase()
                .addLogoutUseCase()
                // .addChangePasswordUseCase()
                .addCreateGroupUseCase()
                .addJoinGroupUseCase()
                .addViewMembersUseCase()
                .addViewPendingUseCase()
                .addGroupTasksUseCases()
                .addRemoveMemberUseCase()
                .addRespondRequestUseCase()
                .addUpdateRoleUseCase()
                .addCreateScheduleView()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
