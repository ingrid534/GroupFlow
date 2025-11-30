package use_case.creategrouptask;

import data_access.InMemoryGroupDataAccessObject;
import data_access.InMemoryMembershipDataAccessObject;
import data_access.InMemoryTaskDataAccessObject;
import data_access.InMemoryUserDataAccessObject;
import entity.group.Group;
import entity.membership.Membership;
import entity.task.Task;
import entity.task.TaskFactory;
import entity.user.User;
import entity.user.UserRole;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGroupTaskInteractorTest {

    private static class TestPresenter implements CreateGroupTaskOutputBoundary {
        CreateGroupTaskOutputData received;

        @Override
        public void present(CreateGroupTaskOutputData outputData) {
            this.received = outputData;
        }
    }

    @Test
    void testOnlyModeratorsCanCreate() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryGroupDataAccessObject groupDAO = new InMemoryGroupDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDAO = new InMemoryMembershipDataAccessObject();

        // User is NOT a moderator
        User user = new User("alice", "pw");
        userDAO.save(user);
        userDAO.setCurrentUsername("alice");

        // membership: not a moderator
        membershipDAO.save(new Membership("alice", "g1", UserRole.MEMBER, true));

        // group exists
        groupDAO.save(new Group("Group", "g1", null));

        TestPresenter presenter = new TestPresenter();

        CreateGroupTaskInteractor interactor =
                new CreateGroupTaskInteractor(taskDAO, presenter,
                        new TaskFactory(), userDAO, groupDAO, membershipDAO);

        CreateGroupTaskInputData input = new CreateGroupTaskInputData(
                "Test Desc",
                "2025-01-01",
                Collections.emptyList(),
                "g1"
        );

        interactor.execute(input);

        assertNotNull(presenter.received);
        assertFalse(presenter.received.isSuccess());
        assertEquals("Only moderators may create tasks in this group.",
                presenter.received.getMessage());
    }

    // ----------------------------------------------------------------------
    // 2. Group not found
    // ----------------------------------------------------------------------
    @Test
    void testGroupNotFound() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryGroupDataAccessObject groupDAO = new InMemoryGroupDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDAO = new InMemoryMembershipDataAccessObject();

        User mod = new User("bob", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("bob");

        membershipDAO.save(new Membership("bob", "g1", UserRole.MODERATOR, true));

        TestPresenter presenter = new TestPresenter();

        CreateGroupTaskInteractor interactor =
                new CreateGroupTaskInteractor(taskDAO, presenter,
                        new TaskFactory(), userDAO, groupDAO, membershipDAO);

        // Passing group "missing"
        CreateGroupTaskInputData input = new CreateGroupTaskInputData(
                "Desc",
                "2024-01-01",
                Collections.emptyList(),
                "missing"
        );

        interactor.execute(input);

        assertNotNull(presenter.received);
        assertFalse(presenter.received.isSuccess());
        assertEquals("Group not found.", presenter.received.getMessage());
    }

    // ----------------------------------------------------------------------
    // 3. Create task WITH deadline
    // ----------------------------------------------------------------------
    @Test
    void testCreateTaskWithDeadline() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryGroupDataAccessObject groupDAO = new InMemoryGroupDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDAO = new InMemoryMembershipDataAccessObject();

        // moderator
        User mod = new User("carol", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("carol");
        membershipDAO.save(new Membership("carol", "g1", UserRole.MODERATOR, true));

        // group
        Group group = new Group("Proj", "g1", null);
        groupDAO.save(group);

        TestPresenter presenter = new TestPresenter();

        CreateGroupTaskInteractor interactor =
                new CreateGroupTaskInteractor(taskDAO, presenter,
                        new TaskFactory(), userDAO, groupDAO, membershipDAO);

        CreateGroupTaskInputData input = new CreateGroupTaskInputData(
                "Write report",
                "2030-12-25 11:30",
                Arrays.asList("carol"),
                "g1"
        );

        interactor.execute(input);

        assertNotNull(presenter.received);
        assertTrue(presenter.received.isSuccess());
        assertEquals("Task created successfully.", presenter.received.getMessage());

        // Verify task was saved
        Task saved = taskDAO.getTasksForGroup("g1").get(0);
        assertEquals("Write report", saved.getDescription());
        assertEquals("2030-12-25",
                saved.getDueDate().get().toLocalDate().toString());

        // Group contains task id
        assertTrue(group.getTasks().contains(saved.getID()));

        // Assignee "carol" now has this task
        assertTrue(userDAO.get("carol").getTasks().contains(saved.getID()));
    }

    // ----------------------------------------------------------------------
    // 4. Create task WITHOUT deadline (dueDate null)
    // ----------------------------------------------------------------------
    @Test
    void testCreateTaskWithoutDeadline_Null() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryGroupDataAccessObject groupDAO = new InMemoryGroupDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDAO = new InMemoryMembershipDataAccessObject();

        User mod = new User("dave", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("dave");
        membershipDAO.save(new Membership("dave", "g1", UserRole.MODERATOR, true));

        Group group = new Group("NoDeadline", "g1", null);
        groupDAO.save(group);

        TestPresenter presenter = new TestPresenter();

        CreateGroupTaskInteractor interactor =
                new CreateGroupTaskInteractor(taskDAO, presenter,
                        new TaskFactory(), userDAO, groupDAO, membershipDAO);

        // due date = null
        CreateGroupTaskInputData input = new CreateGroupTaskInputData(
                "TaskX",
                null,
                null,
                "g1"
        );

        interactor.execute(input);

        assertTrue(presenter.received.isSuccess());
        Task saved = taskDAO.getTasksForGroup("g1").get(0);
        assertTrue(saved.getDueDate().isEmpty());
    }

    // ----------------------------------------------------------------------
    // 5. Create task WITHOUT deadline (empty string)
    // ----------------------------------------------------------------------
    @Test
    void testCreateTaskWithoutDeadline_EmptyString() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryGroupDataAccessObject groupDAO = new InMemoryGroupDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDAO = new InMemoryMembershipDataAccessObject();

        User mod = new User("eve", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("eve");
        membershipDAO.save(new Membership("eve", "g1", UserRole.MODERATOR, true));

        Group group = new Group("NoDeadline", "g1", null);
        groupDAO.save(group);

        TestPresenter presenter = new TestPresenter();

        CreateGroupTaskInteractor interactor =
                new CreateGroupTaskInteractor(taskDAO, presenter,
                        new TaskFactory(), userDAO, groupDAO, membershipDAO);

        CreateGroupTaskInputData input = new CreateGroupTaskInputData(
                "TaskY",
                "",               // empty, means no deadline
                Collections.emptyList(),
                "g1"
        );

        interactor.execute(input);

        Task saved = taskDAO.getTasksForGroup("g1").get(0);
        assertTrue(saved.getDueDate().isEmpty());
    }

    // ----------------------------------------------------------------------
    // 6. Assign users — including missing users
    // ----------------------------------------------------------------------
    @Test
    void testAssigneeHandlingWithMissingUsers() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryGroupDataAccessObject groupDAO = new InMemoryGroupDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDAO = new InMemoryMembershipDataAccessObject();

        User mod = new User("mike", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("mike");
        membershipDAO.save(new Membership("mike", "g1", UserRole.MODERATOR, true));

        Group group = new Group("GroupTest", "g1", null);
        groupDAO.save(group);

        // only u1 exists; u2 does NOT
        User u1 = new User("u1", "pw");
        userDAO.save(u1);

        TestPresenter presenter = new TestPresenter();

        CreateGroupTaskInteractor interactor =
                new CreateGroupTaskInteractor(taskDAO, presenter,
                        new TaskFactory(), userDAO, groupDAO, membershipDAO);

        CreateGroupTaskInputData input = new CreateGroupTaskInputData(
                "Some task",
                null,
                Arrays.asList("u1", "ghostUser"),
                "g1"
        );

        interactor.execute(input);

        Task saved = taskDAO.getTasksForGroup("g1").get(0);

        // u1 should have task
        assertTrue(userDAO.get("u1").getTasks().contains(saved.getID()));

        // ghostUser ignored — no exception
        assertTrue(presenter.received.isSuccess());
    }

    @Test
    void testInvalidDateFormatTriggersError() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryGroupDataAccessObject groupDAO = new InMemoryGroupDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDAO = new InMemoryMembershipDataAccessObject();

        // moderator setup
        User mod = new User("zoe", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("zoe");
        membershipDAO.save(new Membership("zoe", "g1", UserRole.MODERATOR, true));

        // group exists
        Group group = new Group("Grp", "g1", null);
        groupDAO.save(group);

        TestPresenter presenter = new TestPresenter();

        CreateGroupTaskInteractor interactor =
                new CreateGroupTaskInteractor(taskDAO, presenter, new TaskFactory(),
                        userDAO, groupDAO, membershipDAO);

        // <-- DEADLINE PROVIDED BUT INVALID FORMAT
        CreateGroupTaskInputData input = new CreateGroupTaskInputData(
                "Bad date test",
                "NOT_A_DATE",      // invalid: should trigger catch(DateTimeParseException)
                null,
                "g1"
        );

        interactor.execute(input);

        assertNotNull(presenter.received);
        assertFalse(presenter.received.isSuccess());
        assertEquals("Invalid date.", presenter.received.getMessage());
    }

}
