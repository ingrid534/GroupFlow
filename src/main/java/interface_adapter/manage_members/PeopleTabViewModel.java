package interface_adapter.view_members;

import interface_adapter.ViewModel;

public class PeopleTabViewModel extends ViewModel {
    public PeopleTabViewModel() {
        super("sign up");
        setState(new ManageMembersState());
    }
}
