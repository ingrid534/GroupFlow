package use_case.join_group;

import java.io.IOException;

public interface JoinGroupInputBoundary {
    /**
     * Executes the joinGroup use case.
     * @param joinGroupInputData the input data
     * @throws IOException if an I/O Error occurs during execution (i.e. in sending the email when joining a group).
     */
    void execute(JoinGroupInputData joinGroupInputData) throws IOException;

    // Executes the switch to group view use case.
    // void switchToDashboardView();
}
