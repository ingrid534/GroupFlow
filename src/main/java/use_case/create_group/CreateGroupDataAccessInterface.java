package use_case.create_group;

import entity.group.Group;

import java.util.List;

/**
 * Interface for Create Group Data Access.
 * This interface defines the contract for saving a group entity to the data source.
 * It is used by the Create Group use case to persist group data.
 */
public interface CreateGroupDataAccessInterface {
    /**
     * Saves the given group to the data source.
     *
     * @param group the group entity to be saved
     */
    void save(Group group);

    /**
     * Retrieves all groups that a given user belongs to.
     *
     * @param username the username whose group memberships are requested
     * @return a list of Group entities the user is a member of
     **/
    default List<Group> getGroupsForUser(String username) {
        return null;
    }
}
