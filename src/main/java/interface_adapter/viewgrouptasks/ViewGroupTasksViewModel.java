package interface_adapter.viewgrouptasks;

import interface_adapter.ViewModel;

/**
 * ViewModel for viewing all tasks inside a group.
 * Holds a {@link ViewGroupTasksState} and notifies observers on updates.
 */
public class ViewGroupTasksViewModel extends ViewModel<ViewGroupTasksState> {

    /**
     * Constructs a ViewGroupTasksViewModel with an empty initial state.
     */
    public ViewGroupTasksViewModel() {
        super("group_tasks");
        setState(new ViewGroupTasksState());
    }
}
