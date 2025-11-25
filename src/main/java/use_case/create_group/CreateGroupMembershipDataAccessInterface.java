package use_case.create_group;

import entity.membership.Membership;

public interface CreateGroupMembershipDataAccessInterface {

    /**
     * Saves the membership group to the data source.
     *
     * @param membership the membership entity to be saved
     */
    void save(Membership membership);
}
