package interface_adapter.manage_members.remove_member;

import interface_adapter.manage_members.PeopleTabViewModel;
import use_case.manage_members.remove_member.RemoveMemberDataAccessInterface;
import use_case.manage_members.remove_member.RemoveMemberInputBoundary;
import use_case.manage_members.remove_member.RemoveMemberInteractor;

/**
 * Factory that wires Remove Member use case pieces together
 * for a specific PeopleTabViewModel.
 */
public class RemoveMemberControllerFactory {
    RemoveMemberDataAccessInterface membershipDao;

    public RemoveMemberControllerFactory(RemoveMemberDataAccessInterface membershipDao) {
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
    public RemoveMemberController create(PeopleTabViewModel viewModel) {
        RemoveMemberPresenter presenter = new RemoveMemberPresenter(viewModel);
        RemoveMemberInputBoundary interactor =
                new RemoveMemberInteractor(membershipDao, presenter);
        return new RemoveMemberController(interactor);
    }
}
