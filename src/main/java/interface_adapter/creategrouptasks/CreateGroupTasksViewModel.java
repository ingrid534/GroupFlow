package interface_adapter.creategrouptasks;

import interface_adapter.ViewModel;

/**
 * ViewModel for the CreateGroupTask use case.
 * Holds a {@link CreateGroupTasksState} and notifies the view on updates.
 */
public class CreateGroupTasksViewModel extends ViewModel<CreateGroupTasksState> {

    /**
     * Constructs a CreateGroupTaskViewModel with an empty initial state.
     */
    public CreateGroupTasksViewModel() {
        super("create_group_task");
        setState(new CreateGroupTasksState());
    }
}
