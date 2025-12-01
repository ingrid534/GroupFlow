package use_case.join_group;

import java.io.IOException;

public interface JoinGroupInputBoundary {
    /**
     * Executes the joinGroup use case.
     * @param joinGroupInputData the input data
     */
    void execute(JoinGroupInputData joinGroupInputData) throws IOException;

    // Executes the switch to group view use case.
    // void switchToDashboardView();
}
