package data_access;

import entity.membership.Membership;
import use_case.create_group.CreateGroupMembershipDataAccessInterface;
import use_case.creategrouptask.CreateGroupTasksMembershipDataAccessInterface;
import use_case.editgrouptasks.EditGroupTasksMembershipDataAccessInterface;

import java.util.HashMap;
import java.util.Map;

public class InMemoryMembershipDataAccessObject implements CreateGroupMembershipDataAccessInterface,
        CreateGroupTasksMembershipDataAccessInterface,
        EditGroupTasksMembershipDataAccessInterface {

    private final Map<Keys, Membership> memberships = new HashMap<>();

    @Override
    public void save(Membership membership) {
        memberships.put(new Keys(membership.getUsername(), membership.getGroup()), membership);
    }

    @Override
    public Membership get(String userID, String groupID) {
        return memberships.get(new Keys(userID, groupID));
    }

    public static class Keys {
        private final String USER_ID;
        private final String GROUP_ID;

        public Keys(String userID, String groupID) {
            this.USER_ID = userID;
            this.GROUP_ID = groupID;
        }

        public String getUserID() {
            return USER_ID;
        }

        public String getGroupID() {
            return GROUP_ID;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Keys)) return false;
            Keys keys = (Keys) o;
            return USER_ID.equals(keys.USER_ID) &&
                    GROUP_ID.equals(keys.GROUP_ID);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(USER_ID, GROUP_ID);
        }
    }


}
