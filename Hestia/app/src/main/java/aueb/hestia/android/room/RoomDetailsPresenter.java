package aueb.hestia.android.room;

import aueb.hestia.android.login.LoginView;

public class RoomDetailsPresenter {

    private RoomDetailsView view;

    public RoomDetailsView getView() {
        return view;
    }

    private String RoomName;
    public void setView(RoomDetailsView view) {
        this.view = view;
    }

    //book(){}
}
