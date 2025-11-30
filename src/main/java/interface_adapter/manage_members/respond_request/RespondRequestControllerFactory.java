package interface_adapter.manage_members.respond_request;

import interface_adapter.manage_members.PeopleTabViewModel;
import use_case.manage_members.respond_request.RespondRequestDataAccessInterface;
import use_case.manage_members.respond_request.RespondRequestInputBoundary;
import use_case.manage_members.respond_request.RespondRequestInteractor;

/**
 * Factory that wires Respond Request use case pieces together
 * for a specific PeopleTabViewModel.
 */
public class RespondRequestControllerFactory {
    RespondRequestDataAccessInterface membershipDao;

    public RespondRequestControllerFactory(RespondRequestDataAccessInterface membershipDao) {
        this.membershipDao = membershipDao;
    }

    /**
     * Creates a fully wired RespondRequestController for the given PeopleTabViewModel.
     * This method sets up the presenter and interactor so that the controller can
     * retrieve members for a specific group and update the People tab UI.
     *
     * @param viewModel the view model that will be updated with retrieved member data
     * @return a new RespondRequestController wired to the appropriate interactor and presenter
     */
    public RespondRequestController create(PeopleTabViewModel viewModel) {
        RespondRequestPresenter presenter = new RespondRequestPresenter(viewModel);
        RespondRequestInputBoundary interactor =
                new RespondRequestInteractor(membershipDao, presenter);
        return new RespondRequestController(interactor);
    }
}
