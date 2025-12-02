package use_case.create_schedule;

import java.util.List;
import entity.membership.Membership;

public interface CreateScheduleMembershipDataAccessInterface {

    /**
     * Return all the memberships for the given group.
     * @param groupID the group to search memberships
     * @return a list of all the memberships.
     */
    List<Membership> getMembersForGroup(String groupID);
}
