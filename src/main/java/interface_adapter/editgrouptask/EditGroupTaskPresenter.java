package interface_adapter.editgrouptask;

import use_case.editgrouptasks.EditGroupTasksOutputBoundary;
import use_case.editgrouptasks.EditGroupTasksOutputData;

/**
 * Presenter for the EditGroupTasks use case.
 * Converts output data into ViewModel state and notifies observers.
 */
public class EditGroupTaskPresenter implements EditGroupTasksOutputBoundary {

    private final EditGroupTaskViewModel viewModel;

    /**
     * Constructs an EditGroupTaskPresenter.
     *
     * @param viewModel the ViewModel to update
     */
    public EditGroupTaskPresenter(EditGroupTaskViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(EditGroupTasksOutputData response) {
        EditGroupTaskState state = viewModel.getState();

        state.setSuccess(response.isSuccess());
        state.setMessage(response.getMessage());

        viewModel.setState(state);
        viewModel.firePropertyChange("edit_result");
    }
}
