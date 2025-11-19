package interface_adapter.viewtasks;

import use_case.viewtasks.ViewTasksOutputBoundary;
import use_case.viewtasks.ViewTasksOutputData;
import view.ViewTasksView;

public class ViewTasksPresenter implements ViewTasksOutputBoundary {
    private final ViewTasksViewModel viewTasksViewModel;

    public ViewTasksPresenter(ViewTasksViewModel viewTasksViewModel) {
        this.viewTasksViewModel = viewTasksViewModel;
    }

    @Override
    public void presentTasks(ViewTasksOutputData outputData) {
        viewTasksViewModel.firePropertyChange(outputData.getTasks());
    }
}
