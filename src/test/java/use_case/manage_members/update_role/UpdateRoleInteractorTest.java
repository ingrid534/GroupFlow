package use_case.manage_members.update_role;

import entity.membership.Membership;
import entity.user.UserRole;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UpdateRoleInteractorTest {

    // ------------------------------------------------------------
    // In memory test DAO
    // ------------------------------------------------------------

    private static class InMemoryMembershipDAO implements UpdateRoleDataAccessInterface {

        private final Map<String, List<Membership>> groupToMembers = new HashMap<>();

        private String lastUpdatedGroupId;
        private String lastUpdatedUsername;
        private UserRole lastUpdatedRole;

        public void putMembers(String groupId, List<Membership> members) {
            groupToMembers.put(groupId, new ArrayList<>(members));
        }

        public String getLastUpdatedGroupId() {
            return lastUpdatedGroupId;
        }

        public String getLastUpdatedUsername() {
            return lastUpdatedUsername;
        }

        public UserRole getLastUpdatedRole() {
            return lastUpdatedRole;
        }

        @Override
        public void updateMembership(String groupID, String username, UserRole newRole) {
            this.lastUpdatedGroupId = groupID;
            this.lastUpdatedUsername = username;
            this.lastUpdatedRole = newRole;

            List<Membership> list = groupToMembers.get(groupID);
            if (list == null) {
                return;
            }

            for (int i = 0; i < list.size(); i++) {
                Membership m = list.get(i);
                if (m.getUsername().equals(username)) {
                    // Replace with a new Membership with updated role
                    Membership updated = new Membership(
                            m.getUsername(),
                            m.getGroup(),
                            newRole,
                            m.isApproved()
                    );
                    list.set(i, updated);
                    break;
                }
            }
        }

        @Override
        public List<Membership> getMembersForGroup(String groupID) {
            return groupToMembers.getOrDefault(groupID, Collections.emptyList());
        }
    }

    // ------------------------------------------------------------
    // Test presenter
    // ------------------------------------------------------------

    private static class TestPresenter implements UpdateRoleOutputBoundary {

        private UpdateRoleOutputData received;

        @Override
        public void prepareSuccessView(UpdateRoleOutputData updateRoleOutputData) {
            this.received = updateRoleOutputData;
        }

        public UpdateRoleOutputData getReceived() {
            return received;
        }
    }

    // ------------------------------------------------------------
    // TESTS
    // ------------------------------------------------------------

    @Test
    void testUpdateRoleChangesMembershipAndReturnsUpdatedMap() {
        InMemoryMembershipDAO dao = new InMemoryMembershipDAO();
        TestPresenter presenter = new TestPresenter();

        List<Membership> members = Arrays.asList(
                new Membership("alice", "g1", UserRole.MEMBER, true),
                new Membership("bob", "g1", UserRole.MODERATOR, true)
        );
        dao.putMembers("g1", members);

        UpdateRoleInteractor interactor =
                new UpdateRoleInteractor(dao, presenter);

        UpdateRoleInputData input =
                new UpdateRoleInputData("g1", "alice", UserRole.MODERATOR);

        interactor.execute(input);

        // Check DAO update was called with correct values
        assertEquals("g1", dao.getLastUpdatedGroupId());
        assertEquals("alice", dao.getLastUpdatedUsername());
        assertEquals(UserRole.MODERATOR, dao.getLastUpdatedRole());

        // Check presenter received updated data
        UpdateRoleOutputData out = presenter.getReceived();
        assertNotNull(out);

        Map<String, String> membersMap = out.getMembers();
        assertNotNull(membersMap);
        assertEquals(2, membersMap.size());

        // alice should now be MODERATOR
        assertEquals("MODERATOR", membersMap.get("alice"));
        // bob stays MODERATOR
        assertEquals("MODERATOR", membersMap.get("bob"));
    }

    @Test
    void testUpdateRoleOnNonExistingUserDoesNotBreakAndMapStaysSame() {
        InMemoryMembershipDAO dao = new InMemoryMembershipDAO();
        TestPresenter presenter = new TestPresenter();

        List<Membership> members = Collections.singletonList(
                new Membership("alice", "g2", UserRole.MEMBER, true)
        );
        dao.putMembers("g2", members);

        UpdateRoleInteractor interactor =
                new UpdateRoleInteractor(dao, presenter);

        // Try to update role for a user that does not exist in the group
        UpdateRoleInputData input =
                new UpdateRoleInputData("g2", "ghost", UserRole.MODERATOR);

        interactor.execute(input);

        UpdateRoleOutputData out = presenter.getReceived();
        assertNotNull(out);

        Map<String, String> membersMap = out.getMembers();
        assertNotNull(membersMap);
        assertEquals(1, membersMap.size());

        // alice should still be MEMBER
        assertEquals("MEMBER", membersMap.get("alice"));
        // ghost should not appear
        assertFalse(membersMap.containsKey("ghost"));
    }
}
