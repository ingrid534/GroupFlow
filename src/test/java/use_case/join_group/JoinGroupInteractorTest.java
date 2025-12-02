package use_case.join_group;

import data_access.InMemoryGroupDataAccessObject;
import data_access.InMemoryMembershipDataAccessObject;
import data_access.InMemoryUserDataAccessObject;
import entity.group.Group;
import entity.group.GroupType;
import entity.membership.Membership;
import entity.membership.MembershipFactory;
import entity.user.User;
import entity.user.UserRole;
import org.junit.jupiter.api.Test;
import send_grid_api.InMemoryEmailer;
import send_grid_api.SendEmailInterface;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JoinGroupInteractorTest {
    @Test
    void successValidGroupCodeTest() throws IOException {
        String validCode = "ABCDEF";

        User moderator = new User("moderator", "moderator@gmail.com", "pass");
        Group group = new Group("existing group to join", validCode, GroupType.STUDY);
        Membership moderatorMembership = new Membership(moderator.getName(), group.getGroupID(), UserRole.MODERATOR, true);

        moderator.addMembership(moderatorMembership);
        group.addMembership(moderatorMembership);

        User myUser = new User("me", "test@gmail.com", "pass123");

        JoinGroupInputData inputData = new JoinGroupInputData(validCode);

        JoinGroupDataAccessInterface groupDataAccess = new InMemoryGroupDataAccessObject();
        JoinGroupUserDataAccessInterface userDataAccess = new InMemoryUserDataAccessObject();
        JoinGroupMembershipDataAccessInterface membershipDataAccess = new InMemoryMembershipDataAccessObject();
        MembershipFactory membershipFactory = new MembershipFactory();
        SendEmailInterface emailer = new InMemoryEmailer();

        userDataAccess.save(moderator);
        groupDataAccess.save(group);
        userDataAccess.save(myUser);
        userDataAccess.setCurrentUsername(myUser.getName());

        JoinGroupOutputBoundary successPresenter = new JoinGroupOutputBoundary() {
            @Override
            public void prepareSuccessView(JoinGroupOutputData outputData) {
                assertEquals(validCode, outputData.getGroupCode());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Use case failure is unexpected for a valid group code.");
            }
        };

        JoinGroupInputBoundary interactor =
                new JoinGroupInteractor(
                        successPresenter, groupDataAccess, userDataAccess, membershipDataAccess,
                        membershipFactory, emailer
                );

        interactor.execute(inputData);
    }

    @Test
    void failureEmptyGroupCodeTest() throws IOException {
        JoinGroupInputData inputData = new JoinGroupInputData("");


        String validCode = "ABCDEF";

        User moderator = new User("moderator", "moderator@gmail.com", "pass");
        Group group = new Group("existing group to join", validCode, GroupType.STUDY);
        Membership moderatorMembership = new Membership(moderator.getName(), group.getGroupID(), UserRole.MODERATOR, true);

        moderator.addMembership(moderatorMembership);
        group.addMembership(moderatorMembership);

        User myUser = new User("me", "test@gmail.com", "pass123");

        JoinGroupDataAccessInterface groupDataAccess = new InMemoryGroupDataAccessObject();
        JoinGroupUserDataAccessInterface userDataAccess = new InMemoryUserDataAccessObject();
        JoinGroupMembershipDataAccessInterface membershipDataAccess = new InMemoryMembershipDataAccessObject();
        MembershipFactory membershipFactory = new MembershipFactory();
        SendEmailInterface emailer = new InMemoryEmailer();

        userDataAccess.save(moderator);
        groupDataAccess.save(group);
        userDataAccess.save(myUser);
        userDataAccess.setCurrentUsername(myUser.getName());

        JoinGroupOutputBoundary failurePresenter = new JoinGroupOutputBoundary() {
            @Override
            public void prepareSuccessView(JoinGroupOutputData outputData) {
                fail("Use case success is unexpected for an empty group code.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("Group ID cannot be empty.", errorMessage);
            }
        };

        JoinGroupInputBoundary interactor =
                new JoinGroupInteractor(
                        failurePresenter, groupDataAccess, userDataAccess, membershipDataAccess,
                        membershipFactory, emailer
                );
        interactor.execute(inputData);
    }

    @Test
    void failureInvalidGroupCodeTest() throws IOException {
        JoinGroupInputData inputData = new JoinGroupInputData("ZZZZZZ");

        String validCode = "ABCDEF";
        Group group = new Group("name", validCode, GroupType.STUDY);

        JoinGroupDataAccessInterface groupDataAccess = new InMemoryGroupDataAccessObject();
        JoinGroupUserDataAccessInterface userDataAccess = new InMemoryUserDataAccessObject();
        JoinGroupMembershipDataAccessInterface membershipDataAccess = new InMemoryMembershipDataAccessObject();
        MembershipFactory membershipFactory = new MembershipFactory();
        SendEmailInterface emailer = new InMemoryEmailer();

        groupDataAccess.save(group);

        JoinGroupOutputBoundary failurePresenter = new JoinGroupOutputBoundary() {
            @Override
            public void prepareSuccessView(JoinGroupOutputData outputData) {
                fail("Use case success is unexpected for an invalid group code.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("Invalid group ID.", errorMessage);
            }
        };

        JoinGroupInputBoundary interactor =
                new JoinGroupInteractor(
                        failurePresenter, groupDataAccess, userDataAccess, membershipDataAccess,
                        membershipFactory, emailer
                );

        interactor.execute(inputData);
    }
}
