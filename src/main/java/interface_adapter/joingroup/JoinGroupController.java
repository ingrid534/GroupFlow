package interface_adapter.joingroup;

import use_case.join_group.JoinGroupInputBoundary;
import use_case.join_group.JoinGroupInputData;

public class JoinGroupController {
    private final JoinGroupInputBoundary interactor;

    public JoinGroupController(JoinGroupInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String groupCode) {
        interactor.execute(new JoinGroupInputData(groupCode));
    }

}
