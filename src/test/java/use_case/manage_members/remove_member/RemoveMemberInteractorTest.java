package use_case.manage_members.remove_member;

import entity.membership.Membership;
import entity.user.UserRole;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RemoveMemberInteractorTest {

    // ------------------------------------------------------------
    // In memory membership DAO
    // ------------------------------------------------------------

    private static class InMemoryMembershipDAO implements RemoveMemberDataAccessInterface {

        private final Map<String, List<Membership>> groupToMembers = new HashMap<>();

        String lastRemovedGroupId;
        String lastRemovedUsername;

        void setMembers(String groupId, List<Membership> members) {
            groupToMembers.put(groupId, new ArrayList<>(members));
        }

        @Override
        public void removeMembership(String groupID, String username) {
            lastRemovedGroupId = groupID;
            lastRemovedUsername = username;

            List<Membership> list = groupToMembers.get(groupID);
            if (list == null) {
                return;
            }
            list.removeIf(m -> m.getUsername().equals(username));
        }

        @Override
        public List<Membership> getMembersForGroup(String groupID) {
            return groupToMembers.getOrDefault(groupID, Collections.emptyList());
        }
    }

    // ------------------------------------------------------------
    // Test presenter
    // ------------------------------------------------------------

    private static class TestPresenter implements RemoveMemberOutputBoundary {

        private RemoveMemberOutputData received;

        @Override
        public void prepareSuccessView(RemoveMemberOutputData removeMemberOutputData) {
            this.received = removeMemberOutputData;
        }

        public RemoveMemberOutputData getReceived() {
            return received;
        }
    }

    // ------------------------------------------------------------
    // TESTS
    // ------------------------------------------------------------

    @Test
    void removesExistingMemberAndReturnsUpdatedMap() {
        InMemoryMembershipDAO dao = new InMemoryMembershipDAO();
        TestPresenter presenter = new TestPresenter();

        dao.setMembers("g1", Arrays.asList(
                new Membership("alice", "g1", UserRole.MEMBER, true),
                new Membership("bob", "g1", UserRole.MEMBER, true),
                new Membership("charlie", "g1", UserRole.MEMBER, true)
        ));

        RemoveMemberInteractor interactor =
                new RemoveMemberInteractor(dao, presenter);

        RemoveMemberInputData input =
                new RemoveMemberInputData("g1", "bob");

        interactor.execute(input);

        // DAO call
        assertEquals("g1", dao.lastRemovedGroupId);
        assertEquals("bob", dao.lastRemovedUsername);

        // Output
        RemoveMemberOutputData out = presenter.getReceived();
        assertNotNull(out);

        Map<String, String> members = out.getMembers();
        assertNotNull(members);
        assertEquals(2, members.size());

        assertEquals("MEMBER", members.get("alice"));
        assertEquals("MEMBER", members.get("charlie"));
        assertFalse(members.containsKey("bob"));
    }

    @Test
    void removingNonExistingMemberKeepsMembersUnchanged() {
        InMemoryMembershipDAO dao = new InMemoryMembershipDAO();
        TestPresenter presenter = new TestPresenter();

        dao.setMembers("g2", Arrays.asList(
                new Membership("alice", "g2", UserRole.MEMBER, true),
                new Membership("bob", "g2", UserRole.MEMBER, true)
        ));

        RemoveMemberInteractor interactor =
                new RemoveMemberInteractor(dao, presenter);

        // Try to remove someone who is not in the group
        RemoveMemberInputData input =
                new RemoveMemberInputData("g2", "ghost");

        interactor.execute(input);

        RemoveMemberOutputData out = presenter.getReceived();
        assertNotNull(out);

        Map<String, String> members = out.getMembers();
        assertNotNull(members);
        assertEquals(2, members.size());

        assertEquals("MEMBER", members.get("alice"));
        assertEquals("MEMBER", members.get("bob"));
        assertFalse(members.containsKey("ghost"));
    }
}
