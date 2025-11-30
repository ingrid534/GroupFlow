package use_case.create_schedule;

import entity.group.Group;

public interface CreateScheduleGroupDataAccessInterface {
    
    /**
     * Get the existing group by ID.
     * @param groupID id of the group
     * @return the group object
     */
    Group getGroup(String groupID);

    /**
     * Store the current group ID.
     * @param groupID the ID of the group user is currently in.
     */
    void setCurrentGroupID(String groupID);

    /**
     * Get the group ID of the current group user is in.
     * @return the current group ID
     */
    String getCurrentGroupID();

    /**
     * Save the new master schedule.
     * @param group the group with the updated schedule
     */
    void saveMasterSchedule(Group group);
}
