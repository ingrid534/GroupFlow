package use_case.login;

import entity.group.Group;
import java.util.List;

/**
 * DAO interface for retrieving group information during the Login Use Case.
 */
public interface LoginGroupsDataAccessInterface {

    /**
     * Returns all groups that the given user is a member of.
     *
     * @param username the username whose groups are being retrieved
     * @return a list of groups the user belongs to
     */
    List<Group> getGroupsForUser(String username);
}
