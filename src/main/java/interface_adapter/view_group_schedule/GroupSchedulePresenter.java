package interface_adapter.view_group_schedule;

import java.awt.Color;

import interface_adapter.ViewManagerModel;

public class GroupSchedulePresenter {
    private final ViewManagerModel viewManagerModel;
    private final GroupScheduleViewModel groupScheduleViewModel;

    static final Color NO_COLOR = new Color(255, 255, 255);
    static final Color LIGHT_GREEN = new Color(180, 255, 180);
    static final Color MED_GREEN = new Color(0, 150, 0);
    static final Color DARK_GREEN = new Color(0, 100, 0);
}
