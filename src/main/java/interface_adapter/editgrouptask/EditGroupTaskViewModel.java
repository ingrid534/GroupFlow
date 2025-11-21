package interface_adapter.editgrouptask;

import interface_adapter.ViewModel;

/**
 * ViewModel for the EditGroupTasks use case.
 * Holds and manages the {@link EditGroupTaskState}.
 */
public class EditGroupTaskViewModel extends ViewModel<EditGroupTaskState> {

    /**
     * Constructs an EditGroupTasksViewModel with an empty initial state.
     */
    public EditGroupTaskViewModel() {
        super("edit_group_tasks");
        setState(new EditGroupTaskState());
    }
}
