package interface_adapter.manage_members.update_role;

import interface_adapter.manage_members.PeopleTabViewModel;
import use_case.manage_members.update_role.UpdateRoleDataAccessInterface;
import use_case.manage_members.update_role.UpdateRoleInputBoundary;
import use_case.manage_members.update_role.UpdateRoleInteractor;

/**
 * Factory that wires Update Role use case pieces together
 * for a specific PeopleTabViewModel.
 */
public class UpdateRoleControllerFactory {
    UpdateRoleDataAccessInterface membershipDao;

    public UpdateRoleControllerFactory(UpdateRoleDataAccessInterface membershipDao) {
        this.membershipDao = membershipDao;
    }

    /**
     * Creates a fully wired UpdateRoleController for the given PeopleTabViewModel.
     * This method sets up the presenter and interactor so that the controller can
     * retrieve members for a specific group and update the People tab UI.
     *
     * @param viewModel the view model that will be updated with retrieved member data
     * @return a new UpdateRoleController wired to the appropriate interactor and presenter
     */
    public UpdateRoleController create(PeopleTabViewModel viewModel) {
        UpdateRolePresenter presenter = new UpdateRolePresenter(viewModel);
        UpdateRoleInputBoundary interactor =
                new UpdateRoleInteractor(membershipDao, presenter);
        return new UpdateRoleController(interactor);
    }
}
