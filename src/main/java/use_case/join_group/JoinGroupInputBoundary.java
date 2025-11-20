package use_case.join_group;

import use_case.login.LoginInputData;

public interface JoinGroupInputBoundary {
    /**
     * Executes the joinGroup use case.
     * @param joinGroupInputData the input data
     */
    void execute(JoinGroupInputData joinGroupInputData);

    /**
     * Executes the switch to group view use case.
     */
    //void switchToDashboardView();
}
