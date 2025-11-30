package use_case.creategrouptask;

import entity.task.Task;

public interface CreateGroupTaskDataAccessInterface {

    /**
     * Saves the task with the given info.
     *
     * @param task task to be saved
     */
    void upsertTask(Task task);
}
