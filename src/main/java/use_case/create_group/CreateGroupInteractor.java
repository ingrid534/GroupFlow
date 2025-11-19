package use_case.create_group;

import entity.group.Group;
import entity.group.GroupFactory;
import entity.group.GroupType;
import entity.user.User;
import entity.membership.MembershipFactory;
import entity.user.UserRole;

/**
 * The CreateGroup Interactor
 */

public class CreateGroupInteractor implements CreateGroupInputBoundary{
    private final CreateGroupDataAccessInterface groupDataAccessObject;
    private final LoggedInDataAccessInterface userDataAccessObject;
    private final CreateGroupOutputBoundary createGroupPresenter;
    private final GroupFactory groupFactory;
    private final MembershipFactory membershipFactory;

    public CreateGroupInteractor(CreateGroupDataAccessInterface groupDataAccessObject, LoggedInDataAccessInterface userDataAccessObject,
                                 CreateGroupOutputBoundary createGroupOutputBoundary,
                                 GroupFactory groupFactory,
                                 MembershipFactory membershipFactory) {
        this.groupDataAccessObject = groupDataAccessObject;
        this.userDataAccessObject = userDataAccessObject;
        this.createGroupPresenter = createGroupOutputBoundary;
        this.groupFactory = groupFactory;
        this.membershipFactory = membershipFactory;
    }

    @Override
    public void execute(CreateGroupInputData createGroupInputData) {
        final String groupName = createGroupInputData.getGroupName();
        final GroupType groupType = createGroupInputData.getGroupType();

        if (groupName.isEmpty()) {
            createGroupPresenter.prepareFailView("Group names cannot be empty.");
        } else if (groupType == null) {
            createGroupPresenter.prepareFailView("Select a group type.");
        } else {
            final Group group = groupFactory.create(groupName, groupType);
            groupDataAccessObject.save(group);

            final User groupCreator = userDataAccessObject.get(userDataAccessObject.getCurrentUsername());

            // TODO: Add membership once we have ID's from DB
             membershipFactory.create(groupCreator.getUserID(), group.getGroupID(), UserRole.MODERATOR);

             // someDAO.save(membership)?

            final CreateGroupOutputData createGroupOutputData = new CreateGroupOutputData(
                    group.getGroupID(), groupName, groupType
            );
            createGroupPresenter.prepareSuccessView(createGroupOutputData);
        }
    }

    @Override
    public void openCreateGroupModal() {
        createGroupPresenter.openCreateGroupModal();
    }

}
