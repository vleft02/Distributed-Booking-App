package aueb.hestia.android.room;

import androidx.lifecycle.ViewModel;

import aueb.hestia.android.login.LoginPresenter;

public class RoomDetailsViewModel extends ViewModel {
    private RoomDetailsPresenter roomDetailsPresenter;

    public RoomDetailsViewModel()
    {
        roomDetailsPresenter = new RoomDetailsPresenter();
    }

    public RoomDetailsPresenter getPresenter() {
        return roomDetailsPresenter;
    }
}
