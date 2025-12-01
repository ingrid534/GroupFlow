package use_case.join_group;

import entity.membership.Membership;

public interface JoinGroupMembershipDataAccessInterface {
    /**
     * Saves the membership group to the data source.
     *
     * @param membership the membership entity to be saved
     */
    void save(Membership membership);
    
    /**
     * Retrieves a membership by user id and group id.
     *
     * @param userID  The user id.
     * @param groupID The group id.
     * @return The Membership object if found and null if not.
     */
    Membership get(String userID, String groupID);
}
