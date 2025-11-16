package interface_adapter.create_group;

import use_case.create_group.CreateGroupInputBoundary;
import use_case.create_group.CreateGroupInputData;

/**
 * The controller for the Create Group Use Case.
 */
public class CreateGroupController {
    private final CreateGroupInputBoundary createGroupInteractor;

    public CreateGroupController(CreateGroupInputBoundary createGroupInteractor) {
        this.createGroupInteractor = createGroupInteractor;
    }

    /**
     * Executes the Login Use Case.
     * @param groupName the name of the group to create
     * @param groupType the type of group being created
     */

    public void execute(String groupName, String groupType, String groupCreator) {
        final CreateGroupInputData createGroupInputData = new CreateGroupInputData(groupName, groupType, groupCreator);

        createGroupInteractor.execute(createGroupInputData);
    }

    /**
     * Executes the "switch to Dashboard" Use Case.
     */
    public void switchToDashboard() {
        createGroupInteractor.switchToDashboardView();
    }


}
