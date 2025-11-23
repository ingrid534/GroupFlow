package data_access;

import entity.group.Group;
import use_case.create_group.CreateGroupDataAccessInterface;
import use_case.creategrouptask.CreateGroupTaskGroupDataAccessInterface;
import use_case.viewgrouptasks.ViewGroupTasksGroupDataAccessInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * A MongoDB-based implementation of all user-related data access operations.
 *
 * <p>
 * This class handles creating, retrieving, updating, and checking the existence
 * of user records stored inside a MongoDB "users" collection.
 * </p>
 *
 */
public class DBGroupDataAccessObject implements CreateGroupDataAccessInterface, ViewGroupTasksGroupDataAccessInterface,
        CreateGroupTaskGroupDataAccessInterface {

    // TODO: Implement along with any other DB Connection details
    @Override
    public void save(Group group) {
    }

    @Override
    public List<String> getMemberNames() {
        return new ArrayList<>();
    }

    @Override
    public Group getCurrentGroup() {
        return null;
    }

    @Override
    public String getGroupId() {
        return "";
    }
}
