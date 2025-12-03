package use_case.create_group;

import data_access.InMemoryGroupDataAccessObject;
import data_access.InMemoryMembershipDataAccessObject;
import data_access.InMemoryUserDataAccessObject;
import entity.group.GroupFactory;
import entity.group.GroupType;
import entity.membership.MembershipFactory;
import entity.user.User;
import entity.user.UserFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateGroupInteractorTest {

    @Test
    void successTest() {
        CreateGroupInputData inputData = new CreateGroupInputData("Andrew's Group", GroupType.PROJECT);
        CreateGroupDataAccessInterface groupRepository = new InMemoryGroupDataAccessObject();

        // We're going to need a current user to signify the creator of this group.
        // So, we also need memory for our users.
        CreateGroupUserDataAccessInterface userRepository = new InMemoryUserDataAccessObject();

        UserFactory userFactory = new UserFactory();
        User user = userFactory.create("Andrew", "andrew@gmail.com", "password123");
        userRepository.save(user);
        userRepository.setCurrentUsername(user.getName());

        CreateGroupOutputBoundary successPresenter = new CreateGroupOutputBoundary() {
            @Override
            public void prepareSuccessView(CreateGroupOutputData outputData) {
                assertEquals("Andrew's Group", outputData.getGroupName());
                assertEquals(GroupType.PROJECT, outputData.getGroupType());
                assertFalse(outputData.getGroupID().isBlank());
                assertTrue(outputData.getGroups().containsKey(outputData.getGroupID()));
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Failure is unexpected");
            }

            @Override
            public void openCreateGroupModal() {
                // For UI purposes only
            }
        };

        CreateGroupMembershipDataAccessInterface membershipRepository = new InMemoryMembershipDataAccessObject();

        GroupFactory groupFactory = new GroupFactory();
        MembershipFactory membershipFactory = new MembershipFactory();

        CreateGroupInputBoundary interactor =
                new CreateGroupInteractor(
                        groupRepository,
                        userRepository,
                        membershipRepository,
                        successPresenter,
                        groupFactory,
                        membershipFactory
                );

        interactor.openCreateGroupModal();
        interactor.execute(inputData);
    }

    @Test
    void failureBlankNameTest() {
        CreateGroupInputData inputData = new CreateGroupInputData("", GroupType.STUDY);

        CreateGroupDataAccessInterface groupRepository = new InMemoryGroupDataAccessObject();
        CreateGroupUserDataAccessInterface userRepository = new InMemoryUserDataAccessObject();
        CreateGroupMembershipDataAccessInterface membershipRepository = new InMemoryMembershipDataAccessObject();

        UserFactory userFactory = new UserFactory();
        GroupFactory groupFactory = new GroupFactory();
        MembershipFactory membershipFactory = new MembershipFactory();

        User user = userFactory.create("Andrew", "test@gmail.com", "password123");
        userRepository.save(user);
        userRepository.setCurrentUsername(user.getName());

        CreateGroupOutputBoundary failurePresenter = new CreateGroupOutputBoundary() {
            @Override
            public void prepareSuccessView(CreateGroupOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("Group names cannot be empty.", errorMessage);
            }

            @Override
            public void openCreateGroupModal() {
                // For UI use only.
            }
        };

        CreateGroupInputBoundary interactor = new CreateGroupInteractor(
                groupRepository,
                userRepository,
                membershipRepository,
                failurePresenter,
                groupFactory,
                membershipFactory
            );

        interactor.execute(inputData);
    }

    @Test
    void failureNoSelectedTypeTest() {
        CreateGroupInputData inputData = new CreateGroupInputData("Andrew's Group", null);

        CreateGroupDataAccessInterface groupRepository = new InMemoryGroupDataAccessObject();
        CreateGroupUserDataAccessInterface userRepository = new InMemoryUserDataAccessObject();
        CreateGroupMembershipDataAccessInterface membershipRepository = new InMemoryMembershipDataAccessObject();

        UserFactory userFactory = new UserFactory();
        GroupFactory groupFactory = new GroupFactory();
        MembershipFactory membershipFactory = new MembershipFactory();

        User user = userFactory.create("Andrew", "test@gmail.com", "password123");
        userRepository.save(user);
        userRepository.setCurrentUsername(user.getName());

        CreateGroupOutputBoundary failurePresenter = new CreateGroupOutputBoundary() {
            @Override
            public void prepareSuccessView(CreateGroupOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("Select a group type.", errorMessage);
            }

            @Override
            public void openCreateGroupModal() {
                // For UI use only.
            }
        };

        CreateGroupInputBoundary interactor = new CreateGroupInteractor(
                groupRepository,
                userRepository,
                membershipRepository,
                failurePresenter,
                groupFactory,
                membershipFactory
        );

        interactor.execute(inputData);
    }
}
