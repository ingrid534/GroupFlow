package interface_adapter.manage_members;

import interface_adapter.ViewModel;

public class PeopleTabViewModel extends ViewModel<ManageMembersState> {
    public PeopleTabViewModel() {
        super("sign up");
        setState(new ManageMembersState());
    }
}
