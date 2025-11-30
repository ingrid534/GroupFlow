package use_case.viewgrouptasks;

import data_access.InMemoryGroupDataAccessObject;
import data_access.InMemoryTaskDataAccessObject;
import entity.group.Group;
import entity.group.GroupType;
import entity.membership.Membership;
import entity.task.Task;
import entity.user.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ViewGroupTasksInteractorTest {

    private static class TestPresenter implements ViewGroupTasksOutputBoundary {
        private ViewGroupTasksOutputData received;

        @Override
        public void present(ViewGroupTasksOutputData outputData) {
            this.received = outputData;
        }

        public ViewGroupTasksOutputData getReceived() {
            return received;
        }
    }

    // ------------------------------------------------------------
    //                      TESTS
    // ------------------------------------------------------------

    @Test
    void testNoTasksForGroup() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryGroupDataAccessObject groupDAO = new InMemoryGroupDataAccessObject();

        Group g = new Group("Study Group", "g1", GroupType.STUDY);
        groupDAO.save(g);

        TestPresenter presenter = new TestPresenter();
        ViewGroupTasksInteractor interactor =
                new ViewGroupTasksInteractor(taskDAO, presenter, groupDAO);

        interactor.execute(new ViewGroupTasksInputData("g1"));

        ViewGroupTasksOutputData out = presenter.getReceived();
        assertNotNull(out);

        assertTrue(out.getTasks().isEmpty());
        assertTrue(out.getNames().isEmpty());
    }

    @Test
    void testSingleTaskWithDueDate() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryGroupDataAccessObject groupDAO = new InMemoryGroupDataAccessObject();

        Group g = new Group("Project Group", "g2", GroupType.PROJECT);
        g.addMembership(new Membership("alice", "g2", UserRole.MEMBER, true));
        g.addMembership(new Membership("bob", "g2", UserRole.MEMBER, true));
        groupDAO.save(g);

        LocalDateTime due = LocalDateTime.of(2030, 5, 10, 12, 0);

        Task task = new Task(
                "id1",
                "Write report",
                "g2",
                false,
                new ArrayList<>(Arrays.asList("alice", "bob")),
                due
        );
        taskDAO.upsertTask(task);

        TestPresenter presenter = new TestPresenter();
        ViewGroupTasksInteractor interactor =
                new ViewGroupTasksInteractor(taskDAO, presenter, groupDAO);

        interactor.execute(new ViewGroupTasksInputData("g2"));

        ViewGroupTasksOutputData out = presenter.getReceived();
        assertNotNull(out);

        assertEquals(1, out.getTasks().size());

        ViewGroupTasksOutputData.TaskDTO dto = out.getTasks().get(0);

        assertEquals("id1", dto.getId());
        assertEquals("Write report", dto.getDescription());
        assertFalse(dto.isCompleted());
        assertEquals(Arrays.asList("alice", "bob"), dto.getAssigneeUserIds());

        String expected = due.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        assertEquals(expected, dto.getDueDateString());

        assertEquals(Arrays.asList("alice", "bob"), out.getNames());
    }

    @Test
    void testTaskWithNoDueDate() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryGroupDataAccessObject groupDAO = new InMemoryGroupDataAccessObject();

        Group g = new Group("No Date Group", "g3", GroupType.STUDY);
        g.addMembership(new Membership("charlie", "g3", UserRole.MEMBER, true));
        groupDAO.save(g);

        Task task = new Task(
                "id2",
                "Open-ended task",
                "g3",
                false,
                new ArrayList<>(Collections.singletonList("charlie"))
        );
        taskDAO.upsertTask(task);

        TestPresenter presenter = new TestPresenter();
        ViewGroupTasksInteractor interactor =
                new ViewGroupTasksInteractor(taskDAO, presenter, groupDAO);

        interactor.execute(new ViewGroupTasksInputData("g3"));

        ViewGroupTasksOutputData out = presenter.getReceived();

        assertNotNull(out);
        assertEquals(1, out.getTasks().size());

        ViewGroupTasksOutputData.TaskDTO dto = out.getTasks().get(0);

        assertEquals("Open-ended task", dto.getDescription());
        assertEquals("No due date", dto.getDueDateString());
        assertEquals(Collections.singletonList("charlie"), dto.getAssigneeUserIds());

        assertEquals(Collections.singletonList("charlie"), out.getNames());
    }

    @Test
    void testMultipleTasksForGroupIncludingCompleted() {
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryGroupDataAccessObject groupDAO = new InMemoryGroupDataAccessObject();

        Group g = new Group("Mixed Group", "g4", GroupType.STUDY);
        g.addMembership(new Membership("user1", "g4", UserRole.MEMBER, true));
        g.addMembership(new Membership("user2", "g4", UserRole.MEMBER, true));
        g.addMembership(new Membership("user3", "g4", UserRole.MEMBER, true));
        groupDAO.save(g);

        LocalDateTime f1 = LocalDateTime.now().plusDays(3);
        Task t1 = new Task("id3", "Task A", "g4", false,
                new ArrayList<>(Collections.singletonList("user1")), f1);

        LocalDateTime f2 = LocalDateTime.now().plusDays(1);
        Task t2 = new Task("id4", "Task B", "g4", true,
                new ArrayList<>(Collections.singletonList("user2")), f2);

        Task t3 = new Task("id5", "Task C", "g4", false,
                new ArrayList<>(Collections.singletonList("user3")));

        taskDAO.upsertTask(t1);
        taskDAO.upsertTask(t2);
        taskDAO.upsertTask(t3);

        TestPresenter presenter = new TestPresenter();
        ViewGroupTasksInteractor interactor =
                new ViewGroupTasksInteractor(taskDAO, presenter, groupDAO);

        interactor.execute(new ViewGroupTasksInputData("g4"));

        ViewGroupTasksOutputData out = presenter.getReceived();
        assertNotNull(out);

        List<ViewGroupTasksOutputData.TaskDTO> list = out.getTasks();
        assertEquals(3, list.size());

        list.sort(Comparator.comparing(ViewGroupTasksOutputData.TaskDTO::getDescription));

        assertEquals("Task A", list.get(0).getDescription());
        assertEquals("Task B", list.get(1).getDescription());
        assertEquals("Task C", list.get(2).getDescription());

        assertEquals(Arrays.asList("user1", "user2", "user3"), out.getNames());
    }
}
