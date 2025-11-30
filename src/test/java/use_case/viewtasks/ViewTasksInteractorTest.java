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

    // -----------------------------
    // Test Presenter
    // -----------------------------
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
        ViewTasksDataAccessInterface taskDAO = new ViewTasksDataAccessInterface() {
            @Override
            public List<Task> getTasksForUser(String username) {
                List<Task> list = new ArrayList<>();
                list.add(null);
                return list;
            }
        };

        ViewTasksUserDataAccessInterface userDAO = () -> "alice";

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

        Task completed = new Task("id1", "Completed Task", "g1", false, new ArrayList<>());
        completed.addAssignee("bob");
        completed.markCompleted();
        taskDAO.upsertTask(completed);

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

        Task overdue = new Task("id2", "Overdue", "g1", false, new ArrayList<>(),
                LocalDateTime.now().minusDays(3));
        overdue.addAssignee("bob");
        taskDAO.upsertTask(overdue);

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

        Task t = new Task("id3", "No Date", "gX", false, new ArrayList<>());
        t.addAssignee("charlie");
        taskDAO.upsertTask(t);

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

        Task t = new Task("id4", "Future Work", "group123", false, new ArrayList<>(), future);
        t.addAssignee("dave");
        taskDAO.upsertTask(t);

        TestPresenter presenter = new TestPresenter();
        ViewTasksInteractor interactor =
                new ViewTasksInteractor(taskDAO, presenter, userDAO);

        interactor.execute();

        List<ViewTasksOutputData.TaskDTO> tasks =
                presenter.getReceived().getTasks();

        assertEquals(1, tasks.size());

        ViewTasksOutputData.TaskDTO dto = tasks.get(0);

        // Interactor uses "yyyy-MM-dd HH:mm"
        String expected = future.format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        );

        assertEquals(expected, dto.getDueDateString());
        assertEquals("Future Work", dto.getDescription());
        assertEquals("group123", dto.getGroupId());
    }

    @Test
    void testMultipleTasksMixedFilters() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryUserDataAccessObject userDAO = new InMemoryUserDataAccessObject();

        userDAO.setCurrentUsername("eva");
        userDAO.save(new User("eva", "pw"));

        // Valid with date
        LocalDateTime f1 = LocalDateTime.now().plusDays(3);
        Task valid1 = new Task("id5", "Valid 1", "g1", false, new ArrayList<>(), f1);
        valid1.addAssignee("eva");

        // Valid no date
        Task valid2 = new Task("id6", "Valid 2", "g2", false, new ArrayList<>());
        valid2.addAssignee("eva");

        // Completed
        Task completed = new Task("id7", "Completed", "g3", false, new ArrayList<>());
        completed.addAssignee("eva");
        completed.markCompleted();

        // Overdue
        Task overdue = new Task("id8", "Overdue", "g4", false, new ArrayList<>(),
                LocalDateTime.now().minusDays(2));
        overdue.addAssignee("eva");

        taskDAO.upsertTask(valid1);
        taskDAO.upsertTask(valid2);
        taskDAO.upsertTask(completed);
        taskDAO.upsertTask(overdue);

        TestPresenter presenter = new TestPresenter();
        ViewTasksInteractor interactor =
                new ViewTasksInteractor(taskDAO, presenter, userDAO);

        interactor.execute();

        List<ViewTasksOutputData.TaskDTO> result =
                presenter.getReceived().getTasks();

        assertEquals(2, result.size());

        // Sort alphabetically for deterministic testing
        result.sort(Comparator.comparing(ViewTasksOutputData.TaskDTO::getDescription));

        assertEquals("Valid 1", result.get(0).getDescription());
        assertEquals("Valid 2", result.get(1).getDescription());
    }
}
