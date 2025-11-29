package use_case.viewgrouptasks;

import data_access.InMemoryGroupDataAccessObject;
import data_access.InMemoryTaskDataAccessObject;
import entity.group.Group;
import entity.task.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ViewGroupTasksInteractorTest {
    private static class TestPresenter implements ViewGroupTasksOutputBoundary {
        private ViewGroupTasksOutputData received;

        @Override
        public void present(ViewGroupTasksOutputData outputData) {
            this.received = outputData;
        }

        ViewGroupTasksOutputData getReceived() {
            return received;
        }
    }

    @Test
    void testNoTasksForGroup() {
        // Arrange
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryGroupDataAccessObject groupDAO = new InMemoryGroupDataAccessObject();

        // Group with no tasks and no members
        Group group = new Group("Study Group", "g1", null);
        groupDAO.save(group);

        TestPresenter presenter = new TestPresenter();
        ViewGroupTasksInteractor interactor =
                new ViewGroupTasksInteractor(taskDAO, presenter, groupDAO);

        ViewGroupTasksInputData inputData = new ViewGroupTasksInputData("g1");

        // Act
        interactor.execute(inputData);

        // Assert
        ViewGroupTasksOutputData output = presenter.getReceived();
        assertNotNull(output);
        assertTrue(output.getTasks().isEmpty(),
                "Expected no tasks for an empty group");
        assertNotNull(output.getNames());
        assertTrue(output.getNames().isEmpty());
        // We don't need to assert on member names; they should just be an empty list.
    }

    @Test
    void testSingleTaskWithDueDate() {
        // Arrange
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryGroupDataAccessObject groupDAO = new InMemoryGroupDataAccessObject();

        Group group = new Group("Project Group", "g2", null);
        groupDAO.save(group);

        LocalDateTime due = LocalDateTime.of(2030, 5, 10, 12, 0);
        Task task = new Task("Write report", "g2", due);
        task.addAssignee("alice");
        task.addAssignee("bob");
        taskDAO.saveTask(task);

        TestPresenter presenter = new TestPresenter();
        ViewGroupTasksInteractor interactor =
                new ViewGroupTasksInteractor(taskDAO, presenter, groupDAO);

        ViewGroupTasksInputData inputData = new ViewGroupTasksInputData("g2");

        // Act
        interactor.execute(inputData);

        // Assert
        ViewGroupTasksOutputData output = presenter.getReceived();
        assertNotNull(output);

        List<ViewGroupTasksOutputData.TaskDTO> tasks = output.getTasks();
        assertEquals(1, tasks.size());

        ViewGroupTasksOutputData.TaskDTO dto = tasks.get(0);
        assertEquals(task.getID(), dto.getId());
        assertEquals("Write report", dto.getDescription());
        assertFalse(dto.isCompleted());
        assertEquals(task.getAssignees(), dto.getAssigneeUserIds());

        String expectedDate = due.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        assertEquals(expectedDate, dto.getDueDateString());
    }

    @Test
    void testTaskWithNoDueDateUsesFallbackString() {
        // Arrange
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryGroupDataAccessObject groupDAO = new InMemoryGroupDataAccessObject();

        Group group = new Group("No Date Group", "g3", null);
        groupDAO.save(group);

        // Use constructor without due date → dueDate = null → Optional.empty()
        Task task = new Task("Open-ended task", "g3");
        task.addAssignee("charlie");
        taskDAO.saveTask(task);

        TestPresenter presenter = new TestPresenter();
        ViewGroupTasksInteractor interactor =
                new ViewGroupTasksInteractor(taskDAO, presenter, groupDAO);

        ViewGroupTasksInputData inputData = new ViewGroupTasksInputData("g3");

        // Act
        interactor.execute(inputData);

        // Assert
        ViewGroupTasksOutputData output = presenter.getReceived();
        assertNotNull(output);

        List<ViewGroupTasksOutputData.TaskDTO> tasks = output.getTasks();
        assertEquals(1, tasks.size());

        ViewGroupTasksOutputData.TaskDTO dto = tasks.get(0);
        assertEquals("Open-ended task", dto.getDescription());
        assertEquals("No due date", dto.getDueDateString());
        assertEquals(task.getAssignees(), dto.getAssigneeUserIds());
    }

    @Test
    void testMultipleTasksForGroupIncludingCompleted() {
        // Arrange
        InMemoryTaskDataAccessObject taskDAO = new InMemoryTaskDataAccessObject();
        InMemoryGroupDataAccessObject groupDAO = new InMemoryGroupDataAccessObject();

        Group group = new Group("Mixed Group", "g4", null);
        groupDAO.save(group);

        // Task 1: future due date, not completed
        LocalDateTime f1 = LocalDateTime.now().plusDays(3);
        Task t1 = new Task("Task A", "g4", f1);
        t1.addAssignee("user1");

        // Task 2: future due date, completed
        LocalDateTime f2 = LocalDateTime.now().plusDays(1);
        Task t2 = new Task("Task B", "g4", f2);
        t2.addAssignee("user2");
        t2.markCompleted();

        // Task 3: no due date
        Task t3 = new Task("Task C", "g4");
        t3.addAssignee("user3");

        taskDAO.saveTask(t1);
        taskDAO.saveTask(t2);
        taskDAO.saveTask(t3);

        TestPresenter presenter = new TestPresenter();
        ViewGroupTasksInteractor interactor =
                new ViewGroupTasksInteractor(taskDAO, presenter, groupDAO);

        ViewGroupTasksInputData inputData = new ViewGroupTasksInputData("g4");

        // Act
        interactor.execute(inputData);

        // Assert
        ViewGroupTasksOutputData output = presenter.getReceived();
        assertNotNull(output);

        List<ViewGroupTasksOutputData.TaskDTO> dtos = output.getTasks();
        assertEquals(3, dtos.size(), "All tasks for the group should be returned (no filtering)");

        // HashMap does not guarantee order, so sort by description for deterministic assertions.
        dtos.sort(Comparator.comparing(ViewGroupTasksOutputData.TaskDTO::getDescription));

        ViewGroupTasksOutputData.TaskDTO dtoA = dtos.get(0);
        ViewGroupTasksOutputData.TaskDTO dtoB = dtos.get(1);
        ViewGroupTasksOutputData.TaskDTO dtoC = dtos.get(2);

        assertEquals("Task A", dtoA.getDescription());
        assertFalse(dtoA.isCompleted());

        assertEquals("Task B", dtoB.getDescription());
        assertTrue(dtoB.isCompleted());

        assertEquals("Task C", dtoC.getDescription());
        assertEquals("No due date", dtoC.getDueDateString());
    }
}
