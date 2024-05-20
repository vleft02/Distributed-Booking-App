package aueb.hestia.android.search;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

import aueb.hestia.Domain.Room;

public interface SearchRoomsView{
    void toggleFiltersPanel(ConstraintLayout FiltersPanel);

    String getArea();
    int getNoOfPersons();
    float getStars();
    String getDates();

    void showRooms(ArrayList<Room> rooms);
}
