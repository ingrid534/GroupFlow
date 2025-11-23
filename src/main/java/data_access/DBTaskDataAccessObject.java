package data_access;

import entity.task.Task;
import use_case.creategrouptask.CreateGroupTaskDataAccessInterface;
import use_case.editgrouptasks.EditGroupTasksDataAccessInterface;
import use_case.viewgrouptasks.ViewGroupTasksDataAccessInterface;
import use_case.viewtasks.ViewTasksDataAccessInterface;

import java.util.List;
import java.util.ArrayList;

public class DBTaskDataAccessObject implements ViewTasksDataAccessInterface, ViewGroupTasksDataAccessInterface,
        CreateGroupTaskDataAccessInterface, EditGroupTasksDataAccessInterface {

    // Need to be implemented
    @Override
    public List<Task> getTasksForCurrentUser() {
        return new ArrayList<>();
    }

    @Override
    public Task getTask(String taskId) {
        return null;
    }

    @Override
    public List<Task> getTasksForGroup() {
        return new ArrayList<>();
    }

    @Override
    public void saveTask(Task task) { }
}
