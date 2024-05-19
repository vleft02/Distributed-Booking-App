package aueb.hestia.android.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;

import aueb.hestia.R;

public class SearchRoomsActivity extends AppCompatActivity implements SearchRoomsView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_rooms_activity);
        SearchViewModel viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        viewModel.getPresenter().setView(this);

        findViewById(R.id.ShowFiltersPanelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintLayout FiltersPanel =  findViewById(R.id.FIltersPanel);

                FiltersPanelMethod(FiltersPanel);

            }


        });

    }


    @Override
    public void FiltersPanelMethod(ConstraintLayout FiltersPanel) {
        if(FiltersPanel.getVisibility()==View.VISIBLE){
            FiltersPanel.setVisibility(View.GONE);
        }else{
            FiltersPanel.setVisibility(View.VISIBLE);
        }

    }
}