package interface_adapter.dashboard;

import interface_adapter.ViewModel;

/**
 * The View Model for the Dashboard View.
 */
public class DashboardViewModel extends ViewModel<LoggedInState> {

    public DashboardViewModel() {
        super("dashboard");
        setState(new LoggedInState());
    }

}
