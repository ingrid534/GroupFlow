package interface_adapter.viewgrouptasks;

import use_case.viewgrouptasks.ViewGroupTasksOutputBoundary;
import use_case.viewgrouptasks.ViewGroupTasksOutputData;

/**
 * Presenter for the ViewGroupTasks use case.
 * Converts output data into ViewModel state and notifies observers.
 */
public class ViewGroupTasksPresenter implements ViewGroupTasksOutputBoundary {

    private final ViewGroupTasksViewModel viewModel;

    public ViewGroupTasksPresenter(ViewGroupTasksViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(ViewGroupTasksOutputData response) {
        ViewGroupTasksState state = viewModel.getState();
        state.setTasks(response.getTasks());
        state.setError(null);

        viewModel.setState(state);
        viewModel.firePropertyChange("tasks");
    }
}
