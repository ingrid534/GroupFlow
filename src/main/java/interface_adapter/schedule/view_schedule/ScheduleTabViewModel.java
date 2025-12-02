package interface_adapter.schedule.view_schedule;

import interface_adapter.ViewModel;

public class ScheduleTabViewModel extends ViewModel<ScheduleTabState> {
    
    public ScheduleTabViewModel() {
        super("schedule");
        setState(new ScheduleTabState());
    }
}
