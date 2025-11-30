package use_case.creategrouptask;

import entity.membership.Membership;

public interface CreateGroupTasksMembershipDataAccessInterface {

    /**
     * Retrieves a membership by user id and group id.
     *
     * @param username  The user id.
     * @param groupID The group id.
     * @return The Membership object if found.
     * @throws RuntimeException If membership is not found.
     */
    Membership get(String username, String groupID);
}
