package use_case.create_group;

import entity.group.Group;
import entity.group.GroupFactory;
import entity.group.GroupType;
import entity.membership.Membership;
import entity.user.User;
import entity.membership.MembershipFactory;
import entity.user.UserRole;

/**
 * The CreateGroup Interactor.
 */

public class CreateGroupInteractor implements CreateGroupInputBoundary {
    private final CreateGroupDataAccessInterface groupDataAccessObject;
    private final CreateGroupUserDataAccessInterface userDataAccessObject;
    // TODO: change to interface
    private final CreateGroupMembershipDataAccessInterface membershipDataAccessObject;
    private final CreateGroupOutputBoundary createGroupPresenter;
    private final GroupFactory groupFactory;
    private final MembershipFactory membershipFactory;

    public CreateGroupInteractor(CreateGroupDataAccessInterface groupDataAccessObject,
                                 CreateGroupUserDataAccessInterface userDataAccessObject,
                                 CreateGroupMembershipDataAccessInterface membershipDataAccessObject,
                                 CreateGroupOutputBoundary createGroupOutputBoundary,
                                 GroupFactory groupFactory,
                                 MembershipFactory membershipFactory) {
        this.groupDataAccessObject = groupDataAccessObject;
        this.userDataAccessObject = userDataAccessObject;
        this.membershipDataAccessObject = membershipDataAccessObject;
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
            final Group group = groupFactory.create(groupName, "", groupType);
            groupDataAccessObject.save(group);

            final User groupCreator = userDataAccessObject.get(userDataAccessObject.getCurrentUsername());

            final Membership membership = membershipFactory.create(groupCreator.getName(),
                    group.getGroupID(), UserRole.MODERATOR, true);

            membershipDataAccessObject.save(membership);

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
