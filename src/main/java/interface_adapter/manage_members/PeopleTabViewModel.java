package interface_adapter.manage_members;

import interface_adapter.ViewModel;

public class PeopleTabViewModel extends ViewModel<ManageMembersState> {

    public PeopleTabViewModel() {
        super("people");
        setState(new ManageMembersState());
    } // PeopleTabViewModel
}
