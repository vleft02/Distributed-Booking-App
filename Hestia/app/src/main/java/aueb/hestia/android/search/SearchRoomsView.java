package aueb.hestia.android.search;

import android.app.AlertDialog;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

import aueb.hestia.Domain.Room;
import aueb.hestia.android.login.LoginActivity;

public interface SearchRoomsView{
    void toggleFiltersPanel(ConstraintLayout FiltersPanel);

    String getArea();
    int getNoOfPersons();
    float getStars();
    String getDates();

    void showMessage(String title, String message);
    void showRooms(ArrayList<Room> rooms);
}
