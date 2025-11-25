package use_case.join_group;

public interface JoinGroupOutputBoundary {
    /**
     * Prepares the success view for the JoinGroup Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(JoinGroupOutputData outputData);

    /**
     * Prepares the failure view for the JoinGroup Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);

    // Switches to the Dashboard View.
    // void switchToDashboardView();

}
