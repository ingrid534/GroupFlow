package interface_adapter.creategrouptasks;

import interface_adapter.viewtasks.ViewTasksViewModel;
import use_case.creategrouptask.CreateGroupTaskOutputBoundary;
import use_case.creategrouptask.CreateGroupTaskOutputData;

/**
 * Presenter for the CreateGroupTask use case.
 * Updates the ViewModel state and notifies observers.
 */
public class CreateGroupTasksPresenter implements CreateGroupTaskOutputBoundary {

    private final CreateGroupTasksViewModel viewModel;
    private final ViewTasksViewModel viewTasksViewModel;

    /**
     * Constructs a CreateGroupTasksPresenter.
     *
     * @param viewModel          the ViewModel to update
     * @param viewTasksViewModel the viewTasksViewModel to update
     */
    public CreateGroupTasksPresenter(CreateGroupTasksViewModel viewModel, ViewTasksViewModel viewTasksViewModel) {
        this.viewModel = viewModel;
        this.viewTasksViewModel = viewTasksViewModel;
    }

    @Override
    public void present(CreateGroupTaskOutputData response) {
        CreateGroupTasksState state = viewModel.getState();

        state.setSuccess(response.isSuccess());
        state.setMessage(response.getMessage());

        viewModel.setState(state);
        viewModel.firePropertyChange("create_result");

        viewTasksViewModel.firePropertyChange();
    }
}
