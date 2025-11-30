package use_case.manage_members.respond_request;

import entity.membership.Membership;
import entity.user.UserRole;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RespondRequestInteractorTest {

    // ------------------------------------------------------------
    // In-memory DAO
    // ------------------------------------------------------------

    private static class InMemoryMembershipDAO implements RespondRequestDataAccessInterface {

        private final Map<String, List<Membership>> groupToMembers = new HashMap<>();
        private final Map<String, List<Membership>> groupToPending = new HashMap<>();

        String lastGroupUpdated;
        String lastUserUpdated;
        Boolean lastAccepted;

        public void setMembers(String groupId, List<Membership> members) {
            groupToMembers.put(groupId, new ArrayList<>(members));
        }

        public void setPending(String groupId, List<Membership> pending) {
            groupToPending.put(groupId, new ArrayList<>(pending));
        }

        @Override
        public void updateMembership(String groupID, String username, boolean accepted) {
            lastGroupUpdated = groupID;
            lastUserUpdated = username;
            lastAccepted = accepted;

            List<Membership> pendingList = groupToPending.get(groupID);
            if (pendingList == null) {
                pendingList = new ArrayList<>();
            }

            // Case 1 -> Accept request
            if (accepted) {
                // Move from pending → members
                Membership acceptedMember = null;
                for (Membership m : pendingList) {
                    if (m.getUsername().equals(username)) {
                        acceptedMember = m;
                        break;
                    }
                }
                if (acceptedMember != null) {
                    pendingList.remove(acceptedMember);
                    List<Membership> membersList = groupToMembers.computeIfAbsent(groupID, k -> new ArrayList<>());
                    membersList.add(new Membership(username, groupID, UserRole.MEMBER, true));
                }
            }
            // Case 2 -> Decline request
            else {
                pendingList.removeIf(m -> m.getUsername().equals(username));
            }

            groupToPending.put(groupID, pendingList);
        }

        @Override
        public List<Membership> getMembersForGroup(String groupID) {
            return groupToMembers.getOrDefault(groupID, Collections.emptyList());
        }

        @Override
        public List<Membership> getPendingForGroup(String groupID) {
            return groupToPending.getOrDefault(groupID, Collections.emptyList());
        }
    }

    // ------------------------------------------------------------
    // Test presenter
    // ------------------------------------------------------------

    private static class TestPresenter implements RespondRequestOutputBoundary {
        private RespondRequestOutputData received;

        @Override
        public void prepareSuccessView(RespondRequestOutputData outputData) {
            this.received = outputData;
        }

        public RespondRequestOutputData getReceived() {
            return received;
        }
    }

    // ------------------------------------------------------------
    // TESTS
    // ------------------------------------------------------------

    @Test
    void acceptsPendingRequest_userMovesToMembers() {
        InMemoryMembershipDAO dao = new InMemoryMembershipDAO();
        TestPresenter presenter = new TestPresenter();

        // pending: alice
        dao.setPending("g1", new ArrayList<>(List.of(
                new Membership("alice", "g1", UserRole.MEMBER, false)
        )));

        // existing members: bob
        dao.setMembers("g1", new ArrayList<>(List.of(
                new Membership("bob", "g1", UserRole.MEMBER, true)
        )));

        RespondRequestInteractor interactor =
                new RespondRequestInteractor(dao, presenter);

        interactor.execute(new RespondRequestInputData("g1", "alice", true));

        // DAO was called properly
        assertEquals("g1", dao.lastGroupUpdated);
        assertEquals("alice", dao.lastUserUpdated);
        assertTrue(dao.lastAccepted);

        // Output
        RespondRequestOutputData out = presenter.getReceived();
        assertNotNull(out);

        // Members should now include bob and alice
        Map<String, String> members = out.getMembers();
        assertEquals(2, members.size());
        assertEquals("MEMBER", members.get("alice"));
        assertEquals("MEMBER", members.get("bob"));

        // Pending should be empty
        assertTrue(out.getPending().isEmpty());
    }

    @Test
    void declinesPendingRequest_userIsRemovedFromPendingOnly() {
        InMemoryMembershipDAO dao = new InMemoryMembershipDAO();
        TestPresenter presenter = new TestPresenter();

        // pending: alice, charlie
        dao.setPending("g2", new ArrayList<>(List.of(
                new Membership("alice", "g2", UserRole.MEMBER, false),
                new Membership("charlie", "g2", UserRole.MEMBER, false)
        )));

        // existing members: bob
        dao.setMembers("g2", new ArrayList<>(List.of(
                new Membership("bob", "g2", UserRole.MEMBER, true)
        )));

        RespondRequestInteractor interactor =
                new RespondRequestInteractor(dao, presenter);

        interactor.execute(new RespondRequestInputData("g2", "alice", false));

        // DAO call correct?
        assertEquals("g2", dao.lastGroupUpdated);
        assertEquals("alice", dao.lastUserUpdated);
        assertFalse(dao.lastAccepted);

        RespondRequestOutputData out = presenter.getReceived();
        assertNotNull(out);

        // Members unchanged
        Map<String, String> members = out.getMembers();
        assertEquals(1, members.size());
        assertEquals("MEMBER", members.get("bob"));

        // Pending should now only contain charlie
        List<String> pendingUsers = out.getPending();
        assertEquals(1, pendingUsers.size());
        assertEquals("charlie", pendingUsers.get(0));
    }
}
