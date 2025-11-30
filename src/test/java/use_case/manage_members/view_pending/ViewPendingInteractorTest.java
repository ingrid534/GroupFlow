package use_case.manage_members.view_pending;

import entity.membership.Membership;
import entity.user.UserRole;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ViewPendingInteractorTest {

    // ------------------------------------------------------------
    // In memory test DAO
    // ------------------------------------------------------------

    private static class InMemoryPendingDAO implements ViewPendingMembershipDataAccessInterface {

        private final Map<String, List<Membership>> data = new HashMap<>();
        private String lastRequestedGroupId;

        public void putPending(String groupId, List<Membership> pending) {
            data.put(groupId, pending);
        }

        public String getLastRequestedGroupId() {
            return lastRequestedGroupId;
        }

        @Override
        public List<Membership> getPendingForGroup(String groupId) {
            lastRequestedGroupId = groupId;
            return data.getOrDefault(groupId, Collections.emptyList());
        }
    }

    // ------------------------------------------------------------
    // Test presenter
    // ------------------------------------------------------------

    private static class TestPresenter implements ViewPendingOutputBoundary {
        private ViewPendingOutputData received;

        @Override
        public void prepareSuccessView(ViewPendingOutputData outputData) {
            this.received = outputData;
        }

        public ViewPendingOutputData getReceived() {
            return received;
        }
    }

    // ------------------------------------------------------------
    // TESTS
    // ------------------------------------------------------------

    @Test
    void testNoPendingRequests() {
        InMemoryPendingDAO dao = new InMemoryPendingDAO();
        TestPresenter presenter = new TestPresenter();

        ViewPendingInteractor interactor =
                new ViewPendingInteractor(dao, presenter);

        interactor.execute(new ViewPendingInputData("g1"));

        assertEquals("g1", dao.getLastRequestedGroupId());

        ViewPendingOutputData out = presenter.getReceived();
        assertNotNull(out);
        assertTrue(out.getPending().isEmpty());
    }

    @Test
    void testSinglePendingRequest() {
        InMemoryPendingDAO dao = new InMemoryPendingDAO();
        TestPresenter presenter = new TestPresenter();

        List<Membership> pendingList = Collections.singletonList(
                new Membership("alice", "g2", UserRole.MEMBER, false)
        );
        dao.putPending("g2", pendingList);

        ViewPendingInteractor interactor =
                new ViewPendingInteractor(dao, presenter);

        interactor.execute(new ViewPendingInputData("g2"));

        ViewPendingOutputData out = presenter.getReceived();
        assertNotNull(out);

        List<String> pending = out.getPending();
        assertEquals(1, pending.size());
        assertEquals("alice", pending.get(0));
    }

    @Test
    void testMultiplePendingUsers() {
        InMemoryPendingDAO dao = new InMemoryPendingDAO();
        TestPresenter presenter = new TestPresenter();

        List<Membership> pendingList = Arrays.asList(
                new Membership("bob", "g3", UserRole.MEMBER, false),
                new Membership("charlie", "g3", UserRole.MEMBER, false),
                new Membership("david", "g3", UserRole.MEMBER, false)
        );

        dao.putPending("g3", pendingList);

        ViewPendingInteractor interactor =
                new ViewPendingInteractor(dao, presenter);

        interactor.execute(new ViewPendingInputData("g3"));

        ViewPendingOutputData out = presenter.getReceived();
        assertNotNull(out);

        List<String> names = out.getPending();
        assertEquals(3, names.size());
        assertEquals(Arrays.asList("bob", "charlie", "david"), names);
    }
}
