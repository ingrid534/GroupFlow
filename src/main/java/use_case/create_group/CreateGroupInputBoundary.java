package use_case.create_group;

/**
 * Interface for the Create Group Input Boundary.
 * This interface defines the contract for the Create Group use case.
 * It is implemented by classes that handle the business logic for creating groups.
 */
public interface CreateGroupInputBoundary {

    /**
     * Executes the Create Group use case.
     * This method processes the input data required to create a group.
     *
     * @param createGroupInputData the input data containing the group details
     */
    void execute(CreateGroupInputData createGroupInputData);

    /**
     * Opens the Create Group modal.
     * This method triggers the display of the modal for creating a group.
     */
    void openCreateGroupModal();
}
