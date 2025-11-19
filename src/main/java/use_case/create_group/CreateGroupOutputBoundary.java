package use_case.create_group;


/**
 * The output boundary for the Create Group Use Case
 */
public interface CreateGroupOutputBoundary {

    void prepareSuccessView(CreateGroupOutputData outputData);

    void prepareFailView(String errorMessage);

    void openCreateGroupModal();
}
