package interface_adapter.create_group;

import entity.group.GroupType;
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

    public void execute(String groupName, GroupType groupType) {
        final CreateGroupInputData createGroupInputData = new CreateGroupInputData(groupName, groupType);

        createGroupInteractor.execute(createGroupInputData);
    }

    public void openCreateGroupModal() {
        createGroupInteractor.openCreateGroupModal();
    }

}
