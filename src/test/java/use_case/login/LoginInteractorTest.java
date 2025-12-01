package use_case.login;

import data_access.InMemoryUserDataAccessObject;
import entity.group.Group;
import entity.user.User;
import entity.user.UserFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoginInteractorTest {

    @Test
    void successTest() {
        LoginInputData inputData = new LoginInputData("Paul", "password");
        LoginUserDataAccessInterface userRepository = new InMemoryUserDataAccessObject();

        // For the success test, we need to add Paul to the data access repository
        // before we log in.
        UserFactory factory = new UserFactory();
        User user = factory.create("Paul", "paul@gmail.com", "password");
        userRepository.save(user);

        // Dummy groups DAO: Paul has no groups for this test.
        LoginGroupsDataAccessInterface groupsRepository = new LoginGroupsDataAccessInterface() {
            @Override
            public List<Group> getGroupsForUser(String username) {
                return new ArrayList<>();
            }
        };

        // This creates a successPresenter that tests whether the test case is as we
        // expect.
        LoginOutputBoundary successPresenter = new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(LoginOutputData output) {
                assertEquals("Paul", output.getUsername());
                assertEquals("Paul", userRepository.getCurrentUsername());
                assertTrue(output.getGroups().isEmpty());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }
        };

        LoginInputBoundary interactor = new LoginInteractor(userRepository, groupsRepository, successPresenter);
        interactor.execute(inputData);
    }

    @Test
    void failurePasswordMismatchTest() {
        LoginInputData inputData = new LoginInputData("Paul", "wrong");
        LoginUserDataAccessInterface userRepository = new InMemoryUserDataAccessObject();

        // For this failure test, we need to add Paul to the data access repository
        // before we log in, and the passwords should not match.
        UserFactory factory = new UserFactory();
        User user = factory.create("Paul", "paul@gmail.com", "password");
        userRepository.save(user);

        // Dummy groups DAO (will not be used in this test because login fails).
        LoginGroupsDataAccessInterface groupsRepository = new LoginGroupsDataAccessInterface() {
            @Override
            public List<Group> getGroupsForUser(String username) {
                return new ArrayList<>();
            }
        };

        // This creates a presenter that tests whether the test case is as we expect.
        LoginOutputBoundary failurePresenter = new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(LoginOutputData output) {
                // this should never be reached since the test case should fail
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Incorrect password for \"Paul\".", error);
            }
        };

        LoginInputBoundary interactor = new LoginInteractor(userRepository, groupsRepository, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failureUserDoesNotExistTest() {
        LoginInputData inputData = new LoginInputData("Paul", "password");
        LoginUserDataAccessInterface userRepository = new InMemoryUserDataAccessObject();

        // We do NOT add Paul to the repo here. The user should not exist.
        //TODO:
        // Dummy groups DAO (will not be used in this test because login fails).
        LoginGroupsDataAccessInterface groupsRepository = new LoginGroupsDataAccessInterface() {
            @Override
            public List<Group> getGroupsForUser(String username) {
                return new ArrayList<>();
            }
        };

        // This creates a presenter that tests whether the test case is as we expect.
        LoginOutputBoundary failurePresenter = new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(LoginOutputData output) {
                // this should never be reached since the test case should fail
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Paul: Account does not exist.", error);
            }
        };

        LoginInputBoundary interactor = new LoginInteractor(userRepository, groupsRepository, failurePresenter);
        interactor.execute(inputData);
    }
}
