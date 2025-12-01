package use_case.manage_members.view_members;

import entity.membership.Membership;
import entity.user.UserRole;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ViewMembersInteractorTest {

    // ------------------------------------------------------------
    // In memory test DAO
    // ------------------------------------------------------------

    private static class InMemoryMembershipDAO implements ViewMembersMembershipDataAccessInterface {

        private final Map<String, List<Membership>> groupToMembers = new HashMap<>();
        private String lastRequestedGroupId;

        public void putMembers(String groupId, List<Membership> members) {
            groupToMembers.put(groupId, members);
        }

        public String getLastRequestedGroupId() {
            return lastRequestedGroupId;
        }

        @Override
        public List<Membership> getMembersForGroup(String groupId) {
            lastRequestedGroupId = groupId;
            return groupToMembers.getOrDefault(groupId, Collections.emptyList());
        }
    }

    // ------------------------------------------------------------
    // Test presenter
    // ------------------------------------------------------------

    private static class TestPresenter implements ViewMembersOutputBoundary {
        private ViewMembersOutputData received;

        @Override
        public void prepareSuccessView(ViewMembersOutputData viewMembersOutputData) {
            this.received = viewMembersOutputData;
        }

        public ViewMembersOutputData getReceived() {
            return received;
        }
    }

    // ------------------------------------------------------------
    // TESTS
    // ------------------------------------------------------------

    @Test
    void testNoMembers() {
        InMemoryMembershipDAO membershipDAO = new InMemoryMembershipDAO();
        TestPresenter presenter = new TestPresenter();

        ViewMembersInteractor interactor =
                new ViewMembersInteractor(membershipDAO, presenter);

        ViewMembersInputData input = new ViewMembersInputData("g1");
        interactor.execute(input);

        // DAO should be called with correct group id
        assertEquals("g1", membershipDAO.getLastRequestedGroupId());

        ViewMembersOutputData out = presenter.getReceived();
        assertNotNull(out);

        Map<String, String> members = out.getMembers();
        assertNotNull(members);
        assertTrue(members.isEmpty());
    }

    @Test
    void testMultipleMembers() {
        InMemoryMembershipDAO membershipDAO = new InMemoryMembershipDAO();
        TestPresenter presenter = new TestPresenter();

        List<Membership> g1Members = Arrays.asList(
                new Membership("alice", "g1", UserRole.MEMBER, true),
                new Membership("bob", "g1", UserRole.MODERATOR, true),
                new Membership("charlie", "g1", UserRole.MODERATOR, true)
        );
        membershipDAO.putMembers("g1", g1Members);

        ViewMembersInteractor interactor =
                new ViewMembersInteractor(membershipDAO, presenter);

        ViewMembersInputData input = new ViewMembersInputData("g1");
        interactor.execute(input);

        ViewMembersOutputData out = presenter.getReceived();
        assertNotNull(out);

        Map<String, String> members = out.getMembers();
        assertNotNull(members);
        assertEquals(3, members.size());

        assertEquals("MEMBER", members.get("alice"));
        assertEquals("MODERATOR", members.get("bob"));
        assertEquals("MODERATOR", members.get("charlie"));
    }
}
