package interface_adapter.create_group;

import interface_adapter.ViewModel;

/**
 * The View Model for the Create Group View.
 */

public class CreateGroupViewModel extends ViewModel<CreateGroupState> {

    public CreateGroupViewModel() {
        super("create group");
        setState(new CreateGroupState());
    }
}
