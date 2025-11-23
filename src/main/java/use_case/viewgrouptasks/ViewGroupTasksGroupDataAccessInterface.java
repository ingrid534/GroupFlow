package use_case.viewgrouptasks;

import java.util.List;

public interface ViewGroupTasksGroupDataAccessInterface {
    /**
     * Returns all member names that belong to the current group.
     *
     * @return the list of member names for that group (never {@code null})
     */
    List<String> getMemberNames();
}
