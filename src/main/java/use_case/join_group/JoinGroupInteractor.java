package use_case.join_group;

import entity.membership.Membership;
import entity.membership.MembershipFactory;
import entity.user.User;
import entity.user.UserRole;
import use_case.create_group.CreateGroupMembershipDataAccessInterface;
import use_case.create_group.CreateGroupUserDataAccessInterface;

public class JoinGroupInteractor implements JoinGroupInputBoundary {

    private final JoinGroupOutputBoundary presenter;
    private final JoinGroupUserDataAccessInterface groupDataAccess;
    private final CreateGroupUserDataAccessInterface userDataAccess;
    private final CreateGroupMembershipDataAccessInterface membershipDataAccess;
    private final MembershipFactory membershipFactory;

    public JoinGroupInteractor(JoinGroupOutputBoundary presenter,
                               JoinGroupUserDataAccessInterface groupDataAccess,
                               CreateGroupUserDataAccessInterface userDataAccess,
                               CreateGroupMembershipDataAccessInterface membershipDataAccess,
                               MembershipFactory membershipFactory) {
        this.presenter = presenter;
        this.groupDataAccess = groupDataAccess;
        this.userDataAccess = userDataAccess;
        this.membershipDataAccess = membershipDataAccess;
        this.membershipFactory = membershipFactory;
    }

    @Override
    public void execute(JoinGroupInputData inputData) {

        String code = inputData.getGroupCode();   // this is your groupId / join code

        if (code == null || code.isBlank()) {
            presenter.prepareFailView("Group ID cannot be empty.");
            return;
        }

        if (!groupDataAccess.groupCodeExists(code)) {
            presenter.prepareFailView("Invalid group ID.");
            return;
        }

        // Who is requesting to join?
        String currentUsername = userDataAccess.getCurrentUsername();
        User user = userDataAccess.get(currentUsername);

        // Create a PENDING membership (approved = false, role = MEMBER)
        Membership pending = membershipFactory.create(
                user.getName(),   // or user.getUsername() depending on your User implementation
                code,
                UserRole.MEMBER,
                false              // <- NOT approved yet = join request
        );

        membershipDataAccess.save(pending);

        // Let the presenter know everything went fine
        presenter.prepareSuccessView(new JoinGroupOutputData(code));
    }
}
