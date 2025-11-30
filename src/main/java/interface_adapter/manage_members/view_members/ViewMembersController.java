package interface_adapter.manage_members.view_members;

import use_case.manage_members.view_members.ViewMembersInputBoundary;
import use_case.manage_members.view_members.ViewMembersInputData;

public class ViewMembersController {
    private final ViewMembersInputBoundary viewMembersInteractor;

    public ViewMembersController(ViewMembersInputBoundary viewMembersInteractor) {
        this.viewMembersInteractor = viewMembersInteractor;
    }

    /**
     * Executes the View Members Use Case.
     * @param groupID the id of the group to view its members
     */
    public void execute(String groupID) {
        final ViewMembersInputData viewMembersInputData = new ViewMembersInputData(groupID);

        viewMembersInteractor.execute(viewMembersInputData);
    }
}
