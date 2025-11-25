package interface_adapter.joingroup;

import interface_adapter.ViewModel;

public class JoinGroupViewModel extends ViewModel<JoinGroupState> {

    public static final String TITLE_LABEL = "Join a Group";
    public static final String GROUP_CODE_LABEL = "Group Code:";
    public static final String JOIN_BUTTON_LABEL = "Join";
    public static final String CANCEL_BUTTON_LABEL = "Cancel";

    public JoinGroupViewModel() {
        super("join group");
        setState(new JoinGroupState());
    }
}
