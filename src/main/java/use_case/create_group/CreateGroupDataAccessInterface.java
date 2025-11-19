package use_case.create_group;

import entity.group.Group;
import entity.user.User;

public interface CreateGroupDataAccessInterface {
    void save(Group group);
}
