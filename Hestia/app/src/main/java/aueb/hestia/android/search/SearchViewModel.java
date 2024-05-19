package aueb.hestia.android.search;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModel;

public class SearchViewModel  extends ViewModel {
    private SearchPresenter searchPresenter;

    public SearchViewModel()
    {
        searchPresenter = new SearchPresenter();
    }

    public SearchPresenter getPresenter() {
        return searchPresenter;
    }
}
