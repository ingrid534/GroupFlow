package use_case.creategrouptask;

import entity.task.Task;

public interface CreateGroupTaskDataAccessInterface {

    /**
     * Saves the task with updated info (not sure if needed).
     *
     * @param task task to be saved
     */
    void saveTask(Task task);
}
