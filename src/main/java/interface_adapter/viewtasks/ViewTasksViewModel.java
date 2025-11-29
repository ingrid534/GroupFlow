package interface_adapter.viewtasks;

import interface_adapter.ViewModel;

/**
 * ViewModel storing task display data for the Swing UI.
 */
public class ViewTasksViewModel extends ViewModel<ViewTasksState> {

    public ViewTasksViewModel() {
        super("tasks");
        setState(new ViewTasksState());
    }
}
