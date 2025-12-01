package use_case.join_group;

import entity.group.Group;
import entity.membership.Membership;
import entity.membership.MembershipFactory;
import entity.user.User;
import entity.user.UserRole;
import send_grid_api.SendEmailInterface;
import use_case.create_group.CreateGroupMembershipDataAccessInterface;
import use_case.create_group.CreateGroupUserDataAccessInterface;

import java.io.IOException;

public class JoinGroupInteractor implements JoinGroupInputBoundary {

    private final JoinGroupOutputBoundary presenter;
    private final JoinGroupUserDataAccessInterface groupDataAccess;
    private final CreateGroupUserDataAccessInterface userDataAccess;
    private final CreateGroupMembershipDataAccessInterface membershipDataAccess;
    private final MembershipFactory membershipFactory;
    private final SendEmailInterface emailer;

    public JoinGroupInteractor(JoinGroupOutputBoundary presenter,
                               JoinGroupUserDataAccessInterface groupDataAccess,
                               CreateGroupUserDataAccessInterface userDataAccess,
                               CreateGroupMembershipDataAccessInterface membershipDataAccess,
                               MembershipFactory membershipFactory,
                               SendEmailInterface emailer) {
        this.presenter = presenter;
        this.groupDataAccess = groupDataAccess;
        this.userDataAccess = userDataAccess;
        this.membershipDataAccess = membershipDataAccess;
        this.membershipFactory = membershipFactory;
        this.emailer = emailer;
    }

    @Override
    public void execute(JoinGroupInputData inputData) throws IOException {

        String code = inputData.getGroupCode();

        if (code == null || code.isBlank()) {
            presenter.prepareFailView("Group ID cannot be empty.");
            return;
        }

        if (!groupDataAccess.groupCodeExists(code)) {
            presenter.prepareFailView("Invalid group ID.");
            return;
        }

        String currentUsername = userDataAccess.getCurrentUsername();
        User user = userDataAccess.get(currentUsername);

        Membership existing = membershipDataAccess.get(currentUsername, code);
        if (existing != null) {
            presenter.prepareFailView("You are already in this group!");
            return;
        }

        Membership pending = membershipFactory.create(
                user.getName(),
                code,
                UserRole.MEMBER,
                false
        );

        Group requestedGroup = groupDataAccess.getGroup(code);
        User moderator = userDataAccess.get(requestedGroup.getModerator());
        emailer.sendEmail(moderator.getEmail(), SendEmailInterface.EmailType.GROUP_INVITE);

        membershipDataAccess.save(pending);

        presenter.prepareSuccessView(new JoinGroupOutputData(code));
    }
}
