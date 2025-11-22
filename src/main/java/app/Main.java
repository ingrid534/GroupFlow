package app;

import javax.swing.JFrame;

public class Main {

    /**
     * Main method.
     *
     * @param args Arguments.
     */
    public static void main(String[] args) {
        final AppBuilder appBuilder = new AppBuilder();
        final JFrame application = appBuilder
                .addLoginView()
                .addSignupView()
                .addViewTasksUseCase()
                .addGroupTasksUseCases()
                .addDashboardView()
                .addCreateGroupView()
                .addSignupUseCase()
                .addLoginUseCase()
                .addLogoutUseCase()
                // .addChangePasswordUseCase()
                .addCreateGroupUseCase()
                .build();

        application.pack();
        application.setLocationRelativeTo(null);
        application.setVisible(true);
    }
}
