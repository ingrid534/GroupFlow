package use_case.create_group;

import entity.group.Group;

public interface CreateGroupDataAccessInterface {
    void save(Group group);
}
