package use_case.editgrouptasks;

import data_access.InMemoryMembershipDataAccessObject;
import data_access.InMemoryTaskDataAccessObject;
import data_access.InMemoryUserDataAccessObject;
import entity.membership.Membership;
import entity.task.Task;
import entity.user.User;
import entity.user.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class EditGroupTasksInteractorTest {

    private static class TestPresenter implements EditGroupTasksOutputBoundary {
        EditGroupTasksOutputData received;

        @Override
        public void present(EditGroupTasksOutputData outputData) {
            this.received = outputData;
        }
    }

    @Test
    void testOnlyModeratorsCanEdit() {
        // DAOs
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDAO = new InMemoryMembershipDataAccessObject();

        // User exists but is NOT a moderator
        User user = new User("alice", "pw");
        userDAO.save(user);
        userDAO.setCurrentUsername("alice");

        // membership exists but is not moderator
        Membership mem = new Membership("alice", "g1", UserRole.MEMBER, true);
        membershipDAO.save(mem);

        TestPresenter presenter = new TestPresenter();

        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, presenter, userDAO, membershipDAO);

        EditGroupTasksInputData input = new EditGroupTasksInputData(
                "t1",
                "new description",
                "2025-01-01",
                false,
                Collections.emptyList(),
                "g1"
        );

        interactor.execute(input);

        assertNotNull(presenter.received);
        assertFalse(presenter.received.isSuccess());
        assertEquals("Only moderators can edit tasks.", presenter.received.getMessage());
    }

    @Test
    void testTaskNotFound() {
        // DAOs
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDAO = new InMemoryMembershipDataAccessObject();

        // Moderator user
        User user = new User("bob", "pw");
        userDAO.save(user);
        userDAO.setCurrentUsername("bob");

        Membership mem = new Membership("bob", "g1", UserRole.MODERATOR, true);
        membershipDAO.save(mem);

        TestPresenter presenter = new TestPresenter();

        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, presenter, userDAO, membershipDAO);

        // No task with id "t404"
        EditGroupTasksInputData input = new EditGroupTasksInputData(
                "t404",
                "anything",
                "2024-05-05",
                false,
                Collections.emptyList(),
                "g1"
        );

        interactor.execute(input);

        assertNotNull(presenter.received);
        assertFalse(presenter.received.isSuccess());
        assertEquals("Task not found.", presenter.received.getMessage());
    }

    @Test
    void testEditDescriptionDueDateCompletion() {
        // DAOs
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDAO = new InMemoryMembershipDataAccessObject();

        // Moderator user
        User mod = new User("carol", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("carol");

        membershipDAO.save(new Membership("carol", "g1", UserRole.MODERATOR, true));

        // Task
        Task task = new Task("Old Description", "g1");
        taskDAO.saveTask(task);

        TestPresenter presenter = new TestPresenter();
        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, presenter, userDAO, membershipDAO);

        // New changes
        EditGroupTasksInputData input = new EditGroupTasksInputData(
                task.getID(),
                "New Description",
                "2030-12-25",   // due date as yyyy-MM-dd
                true,           // completed
                null,           // no change to assignees
                "g1"
        );

        interactor.execute(input);

        // Assertions
        assertNotNull(presenter.received);
        assertTrue(presenter.received.isSuccess());
        assertEquals("Task updated successfully.", presenter.received.getMessage());

        // Validate changes to the task
        assertEquals("New Description", task.getDescription());
        assertTrue(task.isCompleted());
        assertEquals("2030-12-25", task.getDueDate().get().toLocalDate().toString());
    }

    @Test
    void testEditAssigneesUpdatesUserTaskLists() {
        // DAOs
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDAO = new InMemoryMembershipDataAccessObject();

        // Moderator
        User mod = new User("dave", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("dave");
        membershipDAO.save(new Membership("dave", "g1", UserRole.MODERATOR, true));

        // Two users
        User u1 = new User("u1", "pw");
        User u2 = new User("u2", "pw");

        userDAO.save(u1);
        userDAO.save(u2);

        // Task with original assignee u1
        Task task = new Task("Some task", "g1");
        task.addAssignee("u1");
        u1.addTask(task.getID());
        taskDAO.saveTask(task);

        TestPresenter presenter = new TestPresenter();
        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, presenter, userDAO, membershipDAO);

        // Replace u1 with u2
        EditGroupTasksInputData input = new EditGroupTasksInputData(
                task.getID(),
                null,              // no description change
                null,              // no due date change
                null,              // no completed change
                Arrays.asList("u2"),
                "g1"
        );

        interactor.execute(input);

        // Presenter SHOULD have been called (only in assignee-change branch)
        assertNotNull(presenter.received);
        assertTrue(presenter.received.isSuccess());  // success defaults to true here
        assertEquals("Task updated successfully.", presenter.received.getMessage());

        // Old user should no longer have the task
        assertFalse(u1.getTasks().contains(task.getID()));

        // New user should now have the task
        assertTrue(u2.getTasks().contains(task.getID()));
    }

    @Test
    void testNoAssigneeChangeDoesNotTriggerAssigneeBranch() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDAO = new InMemoryMembershipDataAccessObject();

        User mod = new User("mod", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("mod");
        membershipDAO.save(new Membership("mod", "g1", UserRole.MODERATOR, true));

        Task task = new Task("Task", "g1");
        taskDAO.saveTask(task);

        TestPresenter presenter = new TestPresenter();
        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, presenter, userDAO, membershipDAO);

        EditGroupTasksInputData input = new EditGroupTasksInputData(
                task.getID(),
                null,
                null,
                null,
                null,      // <--- important: no assignee list
                "g1"
        );

        interactor.execute(input);

        assertNotNull(presenter.received);
        assertTrue(presenter.received.isSuccess());
        assertEquals("Task updated successfully.", presenter.received.getMessage());
    }

    @Test
    void testAssigneeLoopsHandleMissingUsers() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDAO = new InMemoryMembershipDataAccessObject();

        User mod = new User("mod2", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("mod2");
        membershipDAO.save(new Membership("mod2", "g1", UserRole.MODERATOR, true));

        Task task = new Task("Task", "g1");
        task.addAssignee("ghostUser");  // <--- user does NOT exist
        taskDAO.saveTask(task);

        TestPresenter presenter = new TestPresenter();
        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, presenter, userDAO, membershipDAO);

        EditGroupTasksInputData input = new EditGroupTasksInputData(
                task.getID(),
                null,
                null,
                null,
                Arrays.asList("ghostUser2"),  // <--- also does NOT exist
                "g1"
        );

        interactor.execute(input);

        assertNotNull(presenter.received);
        assertTrue(presenter.received.isSuccess());
    }

    @Test
    void markIncomplete() {
        // DAOs
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDAO = new InMemoryMembershipDataAccessObject();

        // Moderator user
        User user = new User("bob", "pw");
        userDAO.save(user);
        userDAO.setCurrentUsername("bob");

        Membership mem = new Membership("bob", "g1", UserRole.MODERATOR, true);
        membershipDAO.save(mem);

        Task task = new Task("Some task", "g1");
        task.markIncomplete();
        taskDAO.saveTask(task);

        TestPresenter presenter = new TestPresenter();

        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, presenter, userDAO, membershipDAO);

        EditGroupTasksInputData input = new EditGroupTasksInputData(
                task.getID(),
                "anything",
                "2024-05-05",
                false,
                Collections.emptyList(),
                "g1"
        );

        interactor.execute(input);

        assertNotNull(presenter.received);
        assertTrue(presenter.received.isSuccess());
        assertEquals(false, task.isCompleted());
    }

    @Test
    void testEmptyDueDateStringDoesNotUpdateDueDate() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject membershipDAO = new InMemoryMembershipDataAccessObject();

        User mod = new User("mod3", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("mod3");
        membershipDAO.save(new Membership("mod3", "g1", UserRole.MODERATOR, true));

        // Task with an initial due date
        Task task = new Task("Task", "g1");
        task.setDueDate(LocalDate.of(2030, 1, 1).atStartOfDay());
        taskDAO.saveTask(task);

        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, new TestPresenter(), userDAO, membershipDAO);

        // Pass an empty string as newDueDate
        EditGroupTasksInputData input = new EditGroupTasksInputData(
                task.getID(),
                null,
                "",          // <--- empty but not null
                null,
                null,
                "g1"
        );

        interactor.execute(input);

        // Due date should remain unchanged
        assertEquals("2030-01-01", task.getDueDate().get().toLocalDate().toString());
    }
}
