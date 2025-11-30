package use_case.manage_members.view_pending;

import java.util.ArrayList;

public class ViewPendingOutputData {
    // username -> role
    private final ArrayList<String> pending;

    public ViewPendingOutputData(ArrayList<String> pending) {
        this.pending = pending;
    }

    public ArrayList<String> getPending() {
        return pending;
    } // getPending
}
