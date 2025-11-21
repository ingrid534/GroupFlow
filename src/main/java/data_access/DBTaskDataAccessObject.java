package data_access;

import entity.task.Task;
import use_case.viewtasks.ViewTasksDataAccessInterface;
import java.util.List;
import java.util.ArrayList;

public class DBTaskDataAccessObject implements ViewTasksDataAccessInterface {
    // Need to be implemented
    @Override
    public List<String> getTasksForUser(String username) {
        return new ArrayList<>();
    }

    @Override
    public Task getTask(String taskId) {
        return null;
    }
}
