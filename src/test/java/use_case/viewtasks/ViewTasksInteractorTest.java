package use_case.viewtasks;

import data_access.InMemoryTaskDataAccessObject;
import data_access.InMemoryUserDataAccessObject;
import entity.task.Task;
import entity.user.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ViewTasksInteractorTest {

    private static class TestPresenter implements ViewTasksOutputBoundary {
        private ViewTasksOutputData received;

        @Override
        public void presentTasks(ViewTasksOutputData response) {
            this.received = response;
        }

        public ViewTasksOutputData getReceived() {
            return received;
        }
    }

    // ------------------------------------------------------
    //                     TESTS
    // ------------------------------------------------------

    @Test
    void testNullTaskIsSkipped() {
        // Custom DAO that returns null in the list
        ViewTasksDataAccessInterface taskDAO = new ViewTasksDataAccessInterface() {
            @Override
            public List<Task> getTasksForUser(String username) {
                List<Task> list = new ArrayList<>();
                list.add(null);  // force the null branch
                return list;
            }
        };

        ViewTasksUserDataAccessInterface userDAO = new ViewTasksUserDataAccessInterface() {
            @Override
            public String getCurrentUsername() {
                return "alice";
            }
        };

        TestPresenter presenter = new TestPresenter();

        ViewTasksInteractor interactor =
                new ViewTasksInteractor(taskDAO, presenter, userDAO);

        interactor.execute();

        assertNotNull(presenter.getReceived());
        assertTrue(presenter.getReceived().getTasks().isEmpty());
    }

    @Test
    void testNoTasksForUser() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();

        userDAO.setCurrentUsername("alice");
        userDAO.save(new User("alice", "pw"));

        TestPresenter presenter = new TestPresenter();

        ViewTasksInteractor interactor =
                new ViewTasksInteractor(taskDAO, presenter, userDAO);

        interactor.execute();

        assertNotNull(presenter.getReceived());
        assertTrue(presenter.getReceived().getTasks().isEmpty());
    }

    @Test
    void testCompletedTaskIsSkipped() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();

        userDAO.setCurrentUsername("bob");
        userDAO.save(new User("bob", "pw"));

        Task completed = new Task("Completed Task", "g1");
        completed.addAssignee("bob");
        completed.markCompleted();
        taskDAO.saveTask(completed);

        TestPresenter presenter = new TestPresenter();
        ViewTasksInteractor interactor =
                new ViewTasksInteractor(taskDAO, presenter, userDAO);

        interactor.execute();

        assertTrue(presenter.getReceived().getTasks().isEmpty());
    }

    @Test
    void testOverdueTaskIsSkipped() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();

        userDAO.setCurrentUsername("bob");
        userDAO.save(new User("bob", "pw"));

        Task overdue = new Task("Overdue", "g1", LocalDateTime.now().minusDays(5));
        overdue.addAssignee("bob");
        taskDAO.saveTask(overdue);

        TestPresenter presenter = new TestPresenter();
        ViewTasksInteractor interactor =
                new ViewTasksInteractor(taskDAO, presenter, userDAO);

        interactor.execute();

        assertTrue(presenter.getReceived().getTasks().isEmpty());
    }

    @Test
    void testTaskWithNoDueDate() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();

        userDAO.setCurrentUsername("charlie");
        userDAO.save(new User("charlie", "pw"));

        Task t = new Task("No Date", "gX");
        t.addAssignee("charlie");
        taskDAO.saveTask(t);

        TestPresenter presenter = new TestPresenter();
        ViewTasksInteractor interactor =
                new ViewTasksInteractor(taskDAO, presenter, userDAO);

        interactor.execute();

        List<ViewTasksOutputData.TaskDTO> tasks =
                presenter.getReceived().getTasks();

        assertEquals(1, tasks.size());
        ViewTasksOutputData.TaskDTO dto = tasks.get(0);

        assertEquals("No Date", dto.getDescription());
        assertEquals("No due date", dto.getDueDateString());
        assertEquals("gX", dto.getGroupId());
        assertFalse(dto.isCompleted());
    }

    @Test
    void testTaskWithDueDateFormatsCorrectly() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();

        userDAO.setCurrentUsername("dave");
        userDAO.save(new User("dave", "pw"));

        LocalDateTime future = LocalDateTime.now().plusDays(2);

        Task t = new Task("Future Work", "group123", future);
        t.addAssignee("dave");
        taskDAO.saveTask(t);

        TestPresenter presenter = new TestPresenter();
        ViewTasksInteractor interactor =
                new ViewTasksInteractor(taskDAO, presenter, userDAO);

        interactor.execute();

        List<ViewTasksOutputData.TaskDTO> tasks =
                presenter.getReceived().getTasks();
        assertEquals(1, tasks.size());

        ViewTasksOutputData.TaskDTO dto = tasks.get(0);
        assertEquals("Future Work", dto.getDescription());
        assertEquals(future.toLocalDate().toString(), dto.getDueDateString());
        assertEquals("group123", dto.getGroupId());
    }

    @Test
    void testMultipleTasksMixedFilters() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();

        userDAO.setCurrentUsername("eva");
        userDAO.save(new User("eva", "pw"));

        // Valid
        LocalDateTime f1 = LocalDateTime.now().plusDays(3);
        Task valid1 = new Task("Valid 1", "g1", f1);
        valid1.addAssignee("eva");

        // Valid no date
        Task valid2 = new Task("Valid 2", "g2");
        valid2.addAssignee("eva");

        // Completed
        Task completed = new Task("Completed", "g3");
        completed.addAssignee("eva");
        completed.markCompleted();

        // Overdue
        Task overdue = new Task("Overdue", "g4", LocalDateTime.now().minusDays(2));
        overdue.addAssignee("eva");

        taskDAO.saveTask(valid1);
        taskDAO.saveTask(valid2);
        taskDAO.saveTask(completed);
        taskDAO.saveTask(overdue);

        TestPresenter presenter = new TestPresenter();
        ViewTasksInteractor interactor =
                new ViewTasksInteractor(taskDAO, presenter, userDAO);

        interactor.execute();

        List<ViewTasksOutputData.TaskDTO> result =
                presenter.getReceived().getTasks();

        assertEquals(2, result.size());
        result.sort(Comparator.comparing(ViewTasksOutputData.TaskDTO::getDescription));
        assertEquals("Valid 1", result.get(0).getDescription());
        assertEquals("Valid 2", result.get(1).getDescription());
    }
}
