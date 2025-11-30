package data_access;

import entity.task.Task;
import use_case.creategrouptask.CreateGroupTaskDataAccessInterface;
import use_case.editgrouptasks.EditGroupTasksDataAccessInterface;
import use_case.viewgrouptasks.ViewGroupTasksDataAccessInterface;
import use_case.viewtasks.ViewTasksDataAccessInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskDataAccessObject implements ViewTasksDataAccessInterface,
        ViewGroupTasksDataAccessInterface,
        CreateGroupTaskDataAccessInterface,
        EditGroupTasksDataAccessInterface {

    private final Map<String, Task> tasks = new HashMap<>();

    @Override
    public List<Task> getTasksForUser(String username) {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks.values()) {
            for (String u : t.getAssignees()) {
                if (u.equals(username)) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    @Override
    public Task getTask(String taskId) {
        return tasks.get(taskId);
    }

    @Override
    public List<Task> getTasksForGroup(String groupId) {
        List<Task> result = new ArrayList<>();
        for (Task t : tasks.values()) {
            if (t.getGroup().equals(groupId)) {
                result.add(t);
            }
        }
        return result;
    }

    @Override
    public void upsertTask(Task task) {
        this.tasks.put(task.getID(), task);
    }
}
