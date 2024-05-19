package aueb.hestia.android.search;

import android.widget.EditText;

import aueb.hestia.R;
import aueb.hestia.android.login.LoginView;

public class SearchPresenter {
    private SearchRoomsView view;

    public SearchRoomsView getView() {
        return view;
    }



    public void setView(SearchRoomsView view) {
        this.view = view;
    }

    public void search() {

    }
}
