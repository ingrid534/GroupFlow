package interface_adapter.viewgrouptasks;

import interface_adapter.viewtasks.ViewTasksViewModel;
import use_case.viewgrouptasks.ViewGroupTasksOutputBoundary;
import use_case.viewgrouptasks.ViewGroupTasksOutputData;

/**
 * Presenter for the ViewGroupTasks use case.
 * Converts output data into ViewModel state and notifies observers.
 */
public class ViewGroupTasksPresenter implements ViewGroupTasksOutputBoundary {

    private final ViewGroupTasksViewModel viewModel;
    private final ViewTasksViewModel viewTasksViewModel;

    public ViewGroupTasksPresenter(ViewGroupTasksViewModel viewModel,
                                   ViewTasksViewModel viewTasksViewModel) {
        this.viewModel = viewModel;
        this.viewTasksViewModel = viewTasksViewModel;
    }

    @Override
    public void present(ViewGroupTasksOutputData response) {
        ViewGroupTasksState state = viewModel.getState();
        state.setTasks(response.getTasks());
        state.setMemberNames(response.getNames());
        state.setError(null);

        viewModel.setState(state);
        viewModel.firePropertyChange("tasks");

        viewTasksViewModel.firePropertyChange();
    }
}
