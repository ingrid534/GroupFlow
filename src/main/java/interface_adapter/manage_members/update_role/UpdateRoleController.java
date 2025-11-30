package interface_adapter.manage_members.update_role;

import entity.user.UserRole;
import use_case.manage_members.update_role.UpdateRoleInputBoundary;
import use_case.manage_members.update_role.UpdateRoleInputData;

public class UpdateRoleController {
    private final UpdateRoleInputBoundary updateRoleInteractor;

    public UpdateRoleController(UpdateRoleInputBoundary respondRequestInteractor) {
        this.updateRoleInteractor = respondRequestInteractor;
    }

    /**
     * Executes the Update Role use case for a specific user in the given group.
     * This is used when moderator changes another member's role.
     *
     * @param groupID  the ID of the group where the role update is taking place
     * @param username the username of the member whose role is being changed
     * @param newRole  the new role that should be assigned to the member
     */
    public void execute(String groupID, String username, UserRole newRole) {
        final UpdateRoleInputData updateRoleInputData =
                new UpdateRoleInputData(groupID, username, newRole);

        updateRoleInteractor.execute(updateRoleInputData);
    }
}
