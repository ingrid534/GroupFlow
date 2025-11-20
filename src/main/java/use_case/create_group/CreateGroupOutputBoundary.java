package use_case.create_group;

/**
 * The output boundary for the Create Group Use Case.
 * This interface defines the contract for presenting the results of the Create Group use case.
 * It is implemented by classes responsible for preparing and displaying the output to the user (i.e. Presenter).
 */
public interface CreateGroupOutputBoundary {

    /**
     * Prepares the success view for the Create Group use case.
     * This method is called when the group creation is successful and is responsible
     * for presenting the success output to the user.
     *
     * @param outputData the output data containing the details of the created group
     */
    void prepareSuccessView(CreateGroupOutputData outputData);

    /**
     * Prepares the failure view for the Create Group use case.
     * This method is called when the group creation fails and is responsible
     * for presenting the error message to the user.
     *
     * @param errorMessage the error message describing the reason for the failure
     */
    void prepareFailView(String errorMessage);

    /**
     * Opens the Create Group modal.
     * This method triggers the display of the modal for creating a group.
     */
    void openCreateGroupModal();
}
