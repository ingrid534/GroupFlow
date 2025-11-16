package use_case.create_group;

import entity.group.Group;
import entity.group.GroupFactory;
import entity.user.User;

/**
 * The CreateGroup Interactor
 */

public class CreateGroupInteractor implements CreateGroupInputBoundary{
    private final CreateGroupDataAccessInterface dataAccessObject;
    private final CreateGroupOutputBoundary createGroupPresenter;
    private final GroupFactory groupFactory;

    public CreateGroupInteractor(CreateGroupDataAccessInterface dataAccessObject,
                                 CreateGroupOutputBoundary createGroupOutputBoundary,
                                 GroupFactory groupFactory) {
        this.dataAccessObject = dataAccessObject;
        this.createGroupPresenter = createGroupOutputBoundary;
        this.groupFactory = groupFactory;
    }

    @Override
    public void execute(CreateGroupInputData createGroupInputData) {
        final String groupName = createGroupInputData.getGroupName();
        final String groupType = createGroupInputData.getGroupType();
        System.out.println(createGroupInputData.getGroupCreatorUsername());
        final User groupCreator = dataAccessObject.get(dataAccessObject.getCurrentUsername());
        System.out.println(groupCreator.getName());

        final Group group = groupFactory.create(groupName, groupType, groupCreator);
        dataAccessObject.save(group);

        final CreateGroupOutputData createGroupOutputData = new CreateGroupOutputData(group.getGroupID());
        createGroupPresenter.prepareSuccessView(createGroupOutputData);

    }

    @Override
    public void switchToDashboardView() {
        createGroupPresenter.switchToDashboardView();
    }
}
