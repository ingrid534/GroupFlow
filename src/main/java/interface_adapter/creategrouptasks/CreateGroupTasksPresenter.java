package interface_adapter.creategrouptasks;

import use_case.creategrouptask.CreateGroupTaskOutputBoundary;
import use_case.creategrouptask.CreateGroupTaskOutputData;

/**
 * Presenter for the CreateGroupTask use case.
 * Updates the ViewModel state and notifies observers.
 */
public class CreateGroupTasksPresenter implements CreateGroupTaskOutputBoundary {

    private final CreateGroupTasksViewModel viewModel;

    /**
     * Constructs a CreateGroupTasksPresenter.
     *
     * @param viewModel the ViewModel to update
     */
    public CreateGroupTasksPresenter(CreateGroupTasksViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(CreateGroupTaskOutputData response) {
        CreateGroupTasksState state = viewModel.getState();

        state.setSuccess(response.isSuccess());
        state.setMessage(response.getMessage());

        viewModel.setState(state);
        viewModel.firePropertyChange("create_result");
    }
}
