package use_case.editgrouptasks;

import entity.membership.Membership;

public interface EditGroupTasksMembershipDataAccessInterface {

    /**
     * Retrieves a membership by user id and group id.
     *
     * @param userID  The user id.
     * @param groupID The group id.
     * @return The Membership object if found.
     * @throws RuntimeException If membership is not found.
     */
    Membership get(String userID, String groupID);
}
