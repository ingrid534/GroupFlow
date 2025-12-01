package use_case.join_group;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class JoinGroupInteractorTest {
    @Test
    void successValidGroupCodeTest() {
        String validCode = "ABCDEF";
        JoinGroupInputData inputData = new JoinGroupInputData(validCode);

        JoinGroupUserDataAccessInterface dataAccess = new JoinGroupUserDataAccessInterface() {
            @Override
            public boolean groupCodeExists(String groupCode) {
                return validCode.equals(groupCode);
            }
        };

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

        JoinGroupInputBoundary interactor = new JoinGroupInteractor(successPresenter, dataAccess);
        interactor.execute(inputData);
    }

    @Test
    void failureEmptyGroupCodeTest() {
        JoinGroupInputData inputData = new JoinGroupInputData("");

        JoinGroupUserDataAccessInterface dataAccess = new JoinGroupUserDataAccessInterface() {
            @Override
            public boolean groupCodeExists(String groupCode) {
                return false;
            }
        };

        JoinGroupOutputBoundary failurePresenter = new JoinGroupOutputBoundary() {
            @Override
            public void prepareSuccessView(JoinGroupOutputData outputData) {
                fail("Use case success is unexpected for an empty group code.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("Group code cannot be empty.", errorMessage);
            }
        };

        JoinGroupInputBoundary interactor = new JoinGroupInteractor(failurePresenter, dataAccess);
        interactor.execute(inputData);
    }

    @Test
    void failureInvalidGroupCodeTest() {
        JoinGroupInputData inputData = new JoinGroupInputData("ZZZZZZ");

        JoinGroupUserDataAccessInterface dataAccess = new JoinGroupUserDataAccessInterface() {
            @Override
            public boolean groupCodeExists(String groupCode) {
                return false;
            }
        };

        JoinGroupOutputBoundary failurePresenter = new JoinGroupOutputBoundary() {
            @Override
            public void prepareSuccessView(JoinGroupOutputData outputData) {
                fail("Use case success is unexpected for an invalid group code.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("Invalid Group Code", errorMessage);
            }
        };

        JoinGroupInputBoundary interactor = new JoinGroupInteractor(failurePresenter, dataAccess);

        interactor.execute(inputData);
    }
}
