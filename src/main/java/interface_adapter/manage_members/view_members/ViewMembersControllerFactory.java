package interface_adapter.manage_members.view_members;

import interface_adapter.manage_members.PeopleTabViewModel;
import use_case.manage_members.view_members.ViewMembersInputBoundary;
import use_case.manage_members.view_members.ViewMembersInteractor;
import use_case.manage_members.view_members.ViewMembersMembershipDataAccessInterface;

/**
 * Factory that wires ViewMembers use case pieces together
 * for a specific PeopleTabViewModel.
 */
public class ViewMembersControllerFactory {

    private final ViewMembersMembershipDataAccessInterface membershipDao;

    public ViewMembersControllerFactory(ViewMembersMembershipDataAccessInterface membershipDao) {
        this.membershipDao = membershipDao;
    }

    /**
     * Creates a fully wired ViewMembersController for the given PeopleTabViewModel.
     * This method sets up the presenter and interactor so that the controller can
     * retrieve members for a specific group and update the People tab UI.
     *
     * @param viewModel the view model that will be updated with retrieved member data
     * @return a new ViewMembersController wired to the appropriate interactor and presenter
     */
    public ViewMembersController create(PeopleTabViewModel viewModel) {
        ViewMembersPresenter presenter = new ViewMembersPresenter(viewModel);
        ViewMembersInputBoundary interactor =
                new ViewMembersInteractor(membershipDao, presenter);
        return new ViewMembersController(interactor);
    }
}
