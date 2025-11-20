package interface_adapter.logout;

import use_case.logout.LogoutInputBoundary;

/**
 * The controller for the Logout Use Case.
 */
public class LogoutController {

    private LogoutInputBoundary logoutInputBoundary;

    public LogoutController(LogoutInputBoundary logoutInputBoundary) {
        this.logoutInputBoundary = logoutInputBoundary;
    }

    /**
     * Executes the Logout Use Case.
     */
    public void execute() {
        logoutInputBoundary.execute();

    }
}
