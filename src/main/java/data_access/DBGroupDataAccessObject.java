package data_access;

import entity.group.Group;
import use_case.create_group.CreateGroupDataAccessInterface;

/**
 * A MongoDB-based implementation of all user-related data access operations.
 *
 * <p>
 * This class handles creating, retrieving, updating, and checking the existence
 * of user records stored inside a MongoDB "users" collection.
 * </p>
 *
 */
public class DBGroupDataAccessObject implements CreateGroupDataAccessInterface {

    // TODO: Implement along with any other DB Connection details
    @Override
    public void save(Group group) {

    }
}
