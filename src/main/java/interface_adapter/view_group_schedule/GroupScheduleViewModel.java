package interface_adapter.view_group_schedule;

import interface_adapter.ViewModel;

public class GroupScheduleViewModel extends ViewModel<GroupScheduleState> {
    public GroupScheduleViewModel() {
        super("group schedule");
        setState(new GroupScheduleState());
    }
}
