package interface_adapter.schedule.view_schedule;

import interface_adapter.ViewModel;
import interface_adapter.view_group_schedule.GroupScheduleState;

public class ScheduleTabViewModel extends ViewModel<GroupScheduleState> {
    
    public ScheduleTabViewModel() {
        super("schedule");
        setState(new GroupScheduleState());
    }
}
