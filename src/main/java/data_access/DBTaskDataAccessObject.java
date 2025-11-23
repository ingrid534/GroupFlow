package data_access;

import entity.group.Group;
import entity.task.Task;
import entity.user.User;
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
    public List<String> getTasksForCurrentUser() {
        return new ArrayList<>();
    }

    @Override
    public Task getTask(String taskId) {
        return null;
    }

    @Override
    public User getUserFromUsername(String username) {
        return null;
    }

    @Override
    public User getUser(String userId) {
        return null;
    }

    @Override
    public boolean isModerator() {
        return true;
    }

    @Override
    public Group getCurrentGroup() {
        return null;
    }

    @Override
    public String getGroupId() {
        return "";
    }

    @Override
    public List<String> getMemberNames() {
        return new ArrayList<>();
    }

    @Override
    public List<Task> getTasksForGroup() {
        return new ArrayList<>();
    }

    @Override
    public void saveGroup(Group group) { }

    @Override
    public void saveTask(Task task) { }

    @Override
    public void saveUser(User group) { }
}
