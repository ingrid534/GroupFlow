package interface_adapter.manage_members.view_pending;

import interface_adapter.manage_members.PeopleTabViewModel;
import use_case.manage_members.view_pending.*;

/**
 * Factory that wires View Pending use case pieces together
 * for a specific PeopleTabViewModel.
 */
public class ViewPendingControllerFactory {
    private final ViewPendingMembershipDataAccessInterface membershipDao;

    public ViewPendingControllerFactory(ViewPendingMembershipDataAccessInterface membershipDao) {
        this.membershipDao = membershipDao;
    }

    /**
     * Creates a fully wired ViewPendingController for the given PeopleTabViewModel.
     * This method sets up the presenter and interactor so the controller can retrieve
     * pending membership requests for a group and update the People tab UI.
     *
     * @param viewModel the view model that will be updated with pending membership data
     * @return a new ViewPendingController wired to its interactor and presenter
     */
    public ViewPendingController create(PeopleTabViewModel viewModel) {
        ViewPendingOutputBoundary presenter = new ViewPendingPresenter(viewModel);
        ViewPendingInputBoundary interactor =
                new ViewPendingInteractor(membershipDao, presenter);
        return new ViewPendingController(interactor);
    }
}
