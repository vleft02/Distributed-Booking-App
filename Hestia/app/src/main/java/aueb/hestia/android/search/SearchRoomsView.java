package aueb.hestia.android.search;

import androidx.constraintlayout.widget.ConstraintLayout;

public interface SearchRoomsView{
    void toggleFiltersPanel(ConstraintLayout FiltersPanel);

    String getArea();
    int getNoOfPersons();
    float getStars();
    String getDates();
}
