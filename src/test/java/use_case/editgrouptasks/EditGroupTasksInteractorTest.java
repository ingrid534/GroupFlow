package use_case.editgrouptasks;

import data_access.InMemoryMembershipDataAccessObject;
import data_access.InMemoryTaskDataAccessObject;
import data_access.InMemoryUserDataAccessObject;
import entity.membership.Membership;
import entity.task.Task;
import entity.user.User;
import entity.user.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;          // <-- NEW
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EditGroupTasksInteractorTest {

    private static class TestPresenter implements EditGroupTasksOutputBoundary {
        EditGroupTasksOutputData received;

        @Override
        public void present(EditGroupTasksOutputData outputData) {
            this.received = outputData;
        }
    }

    // ---------------------------------------------------------------------
    //  1. USER NOT MODERATOR → BLOCKED
    // ---------------------------------------------------------------------
    @Test
    void testOnlyModeratorsCanEdit() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject memDAO = new InMemoryMembershipDataAccessObject();

        User u = new User("alice", "pw");
        userDAO.save(u);
        userDAO.setCurrentUsername("alice");

        memDAO.save(new Membership("alice", "g1", UserRole.MEMBER, true));

        TestPresenter presenter = new TestPresenter();
        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, presenter, userDAO, memDAO);

        EditGroupTasksInputData input = new EditGroupTasksInputData(
                "t1", "desc", "2030-01-01 00:00", false,
                Collections.emptyList(), "g1"
        );

        interactor.execute(input);

        assertFalse(presenter.received.isSuccess());
        assertEquals("Only moderators can edit tasks.", presenter.received.getMessage());
    }

    // ---------------------------------------------------------------------
    //  2. TASK NOT FOUND
    // ---------------------------------------------------------------------
    @Test
    void testTaskNotFound() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject memDAO = new InMemoryMembershipDataAccessObject();

        User u = new User("bob", "pw");
        userDAO.save(u);
        userDAO.setCurrentUsername("bob");

        memDAO.save(new Membership("bob", "g1", UserRole.MODERATOR, true));

        TestPresenter presenter = new TestPresenter();
        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, presenter, userDAO, memDAO);

        EditGroupTasksInputData input = new EditGroupTasksInputData(
                "missing", "x", "2030-01-01 00:00", false, null, "g1"
        );

        interactor.execute(input);

        assertFalse(presenter.received.isSuccess());
        assertEquals("Task not found.", presenter.received.getMessage());
    }

    // ---------------------------------------------------------------------
    //  3. VALID EDIT — DESCRIPTION, DUE DATE, COMPLETION
    // ---------------------------------------------------------------------
    @Test
    void testEditDescriptionDueDateCompletion() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject memDAO = new InMemoryMembershipDataAccessObject();

        User mod = new User("carol", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("carol");

        memDAO.save(new Membership("carol", "g1", UserRole.MODERATOR, true));

        // OLD: new Task("Old", "g1");
        Task task = new Task(
                "t-edit-1",
                "Old",
                "g1",
                false,
                new ArrayList<>()
        );
        taskDAO.upsertTask(task);

        TestPresenter presenter = new TestPresenter();
        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, presenter, userDAO, memDAO);

        EditGroupTasksInputData input = new EditGroupTasksInputData(
                task.getID(),
                "New Description",
                "2030-12-25 13:45",
                true,
                null,
                "g1"
        );

        interactor.execute(input);

        assertTrue(presenter.received.isSuccess());
        assertEquals("Task updated successfully.", presenter.received.getMessage());
        assertEquals("New Description", task.getDescription());
        assertTrue(task.isCompleted());
        assertEquals("2030-12-25T13:45", task.getDueDate().get().toString());
    }

    // ---------------------------------------------------------------------
    //  4. INVALID DATE FORMAT
    // ---------------------------------------------------------------------
    @Test
    void testInvalidDateRejected() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject memDAO = new InMemoryMembershipDataAccessObject();

        User mod = new User("x", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("x");
        memDAO.save(new Membership("x", "g1", UserRole.MODERATOR, true));

        // OLD: new Task("Task", "g1");
        Task task = new Task(
                "t-invalid",
                "Task",
                "g1",
                false,
                new ArrayList<>()
        );
        taskDAO.upsertTask(task);

        TestPresenter presenter = new TestPresenter();
        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, presenter, userDAO, memDAO);

        EditGroupTasksInputData input = new EditGroupTasksInputData(
                task.getID(),
                null,
                "NOT_A_DATE",
                null,
                null,
                "g1"
        );

        interactor.execute(input);

        assertFalse(presenter.received.isSuccess());
        assertEquals("Invalid date.", presenter.received.getMessage());
    }

    // ---------------------------------------------------------------------
    //  5. ASSIGNEE UPDATE — USER TASK LISTS UPDATED
    // ---------------------------------------------------------------------
    @Test
    void testEditAssigneesUpdatesUserTaskLists() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject memDAO = new InMemoryMembershipDataAccessObject();

        User mod = new User("mod", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("mod");
        memDAO.save(new Membership("mod", "g1", UserRole.MODERATOR, true));

        User u1 = new User("u1", "pw");
        User u2 = new User("u2", "pw");
        userDAO.save(u1);
        userDAO.save(u2);

        // OLD: new Task("Test", "g1");
        Task task = new Task(
                "t-assignees",
                "Test",
                "g1",
                true,
                new ArrayList<>()
        );
        task.addAssignee("u1");
        u1.addTask(task.getID());
        taskDAO.upsertTask(task);

        TestPresenter presenter = new TestPresenter();
        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, presenter, userDAO, memDAO);

        EditGroupTasksInputData input = new EditGroupTasksInputData(
                task.getID(),
                null,
                null,
                null,
                Arrays.asList("u2"),
                "g1"
        );

        interactor.execute(input);

        assertTrue(presenter.received.isSuccess());

        // NOTE: these assertions assume the interactor correctly:
        // 1) removes the task from old assignees
        // 2) adds it to new assignees
        assertFalse(u1.getTasks().contains(task.getID()));
        assertTrue(u2.getTasks().contains(task.getID()));
    }

    // ---------------------------------------------------------------------
    //  6. NO ASSIGNEE CHANGE → STILL SUCCESS
    // ---------------------------------------------------------------------
    @Test
    void testNoAssigneeChangeStillSuccess() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject memDAO = new InMemoryMembershipDataAccessObject();

        User mod = new User("m", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("m");
        memDAO.save(new Membership("m", "g1", UserRole.MODERATOR, true));

        // OLD: new Task("Task", "g1");
        Task task = new Task(
                "t-no-assignee-change",
                "Task",
                "g1",
                false,
                new ArrayList<>()
        );
        taskDAO.upsertTask(task);

        TestPresenter presenter = new TestPresenter();
        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, presenter, userDAO, memDAO);

        EditGroupTasksInputData input = new EditGroupTasksInputData(
                task.getID(),
                null,
                null,
                null,
                null,
                "g1"
        );

        interactor.execute(input);

        assertTrue(presenter.received.isSuccess());
        assertEquals("Task updated successfully.", presenter.received.getMessage());
    }

    // ---------------------------------------------------------------------
    //  7. EMPTY STRING DUE DATE → DO NOT UPDATE
    // ---------------------------------------------------------------------
    @Test
    void testEmptyDueDateDoesNotChangeDueDate() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject memDAO = new InMemoryMembershipDataAccessObject();

        User mod = new User("z", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("z");
        memDAO.save(new Membership("z", "g1", UserRole.MODERATOR, true));

        // OLD: new Task("Task", "g1");
        Task task = new Task(
                "t-empty-date",
                "Task",
                "g1",
                false,
                new ArrayList<>()
        );
        LocalDateTime oldDue = LocalDateTime.of(2030, 1, 1, 10, 0);
        task.setDueDate(oldDue);
        taskDAO.upsertTask(task);

        TestPresenter presenter = new TestPresenter();
        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, presenter, userDAO, memDAO);

        EditGroupTasksInputData input = new EditGroupTasksInputData(
                task.getID(),
                null,
                "",      // empty → do not update
                null,
                null,
                "g1"
        );

        interactor.execute(input);

        assertEquals(oldDue, task.getDueDate().get());
    }

    @Test
    void testMembershipNullAllowsEditing() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject memDAO = new InMemoryMembershipDataAccessObject();

        // User exists but DOES NOT have a membership in g1
        User user = new User("lonely", "pw");
        userDAO.save(user);
        userDAO.setCurrentUsername("lonely");

        // Task exists
        Task task = new Task(
                "t-null-membership",
                "Description",
                "g1",
                false,
                new ArrayList<>()
        );
        taskDAO.upsertTask(task);

        TestPresenter presenter = new TestPresenter();

        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, presenter, userDAO, memDAO);

        EditGroupTasksInputData input = new EditGroupTasksInputData(
                task.getID(),
                "Updated",
                null,
                null,
                null,
                "g1"
        );

        interactor.execute(input);

        // Because membership == null, edit is permitted
        assertNotNull(presenter.received);
        assertTrue(presenter.received.isSuccess());
        assertEquals("Task updated successfully.", presenter.received.getMessage());

        // also ensure update happened
        assertEquals("Updated", task.getDescription());
    }

    @Test
    void testMarkIncompleteBranchExecutes() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject memDAO = new InMemoryMembershipDataAccessObject();

        User mod = new User("q", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("q");
        memDAO.save(new Membership("q", "g1", UserRole.MODERATOR, true));

        Task task = new Task(
                "t-incomplete-branch",
                "Task",
                "g1",
                false,
                new ArrayList<>()
        );
        task.markCompleted(); // MUST start completed so markIncomplete actually flips state
        taskDAO.upsertTask(task);

        TestPresenter presenter = new TestPresenter();
        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, presenter, userDAO, memDAO);

        EditGroupTasksInputData input = new EditGroupTasksInputData(
                task.getID(),
                null,
                null,
                false,     // explicitly request incomplete
                null,
                "g1"
        );

        interactor.execute(input);

        assertFalse(task.isCompleted());
        assertTrue(presenter.received.isSuccess());
    }

    @Test
    void testOldAssigneeUserNotFound() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject memDAO = new InMemoryMembershipDataAccessObject();

        // Moderator
        User mod = new User("mod", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("mod");
        memDAO.save(new Membership("mod", "g1", UserRole.MODERATOR, true));

        // Task with old assignee: "ghost" (user does NOT exist)
        Task task = new Task(
                "t-old-null",
                "Task",
                "g1",
                false,
                new ArrayList<>()
        );
        task.addAssignee("ghost");     // ghost user does NOT exist!
        taskDAO.upsertTask(task);

        TestPresenter presenter = new TestPresenter();
        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, presenter, userDAO, memDAO);

        // newAssignees null -> test still hits old loop
        EditGroupTasksInputData input = new EditGroupTasksInputData(
                task.getID(),
                null,
                null,
                null,
                new ArrayList<>(),  // empty but non-null → hits old loop
                "g1"
        );

        interactor.execute(input);

        assertTrue(presenter.received.isSuccess());
        assertTrue(task.getAssignees().isEmpty());   // old removed (task-level)
        // no crash from u == null branch
    }

    @Test
    void testNewAssigneeUserNotFound() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();
        InMemoryMembershipDataAccessObject memDAO = new InMemoryMembershipDataAccessObject();

        // Moderator
        User mod = new User("mod2", "pw");
        userDAO.save(mod);
        userDAO.setCurrentUsername("mod2");
        memDAO.save(new Membership("mod2", "g1", UserRole.MODERATOR, true));

        // Task with no old assignees
        Task task = new Task(
                "t-new-null",
                "Task",
                "g1",
                false,
                new ArrayList<>()
        );
        taskDAO.upsertTask(task);

        TestPresenter presenter = new TestPresenter();
        EditGroupTasksInteractor interactor =
                new EditGroupTasksInteractor(taskDAO, presenter, userDAO, memDAO);

        // Add "ghost" as new assignee → user does NOT exist
        EditGroupTasksInputData input = new EditGroupTasksInputData(
                task.getID(),
                null,
                null,
                null,
                List.of("ghost"),  // ghost user does NOT exist
                "g1"
        );

        interactor.execute(input);

        assertTrue(presenter.received.isSuccess());
        assertEquals(List.of("ghost"), task.getAssignees()); // stored
        // but user not updated since u == null
    }

}
