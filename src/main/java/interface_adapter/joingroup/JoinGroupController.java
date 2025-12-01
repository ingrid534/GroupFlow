package interface_adapter.joingroup;

import use_case.join_group.JoinGroupInputBoundary;
import use_case.join_group.JoinGroupInputData;

import java.io.IOException;

public class JoinGroupController {
    private final JoinGroupInputBoundary interactor;

    public JoinGroupController(JoinGroupInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Executes the JoinGroup Use Case.
     *
     * @param groupCode the groupCode of the user logging in
     */
    public void execute(String groupCode) throws IOException {
        interactor.execute(new JoinGroupInputData(groupCode));
    }

}
