package aueb.hestia.android.room;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import aueb.hestia.R;
import aueb.hestia.android.login.LoginViewModel;

public class RoomDetailsActivity extends AppCompatActivity implements RoomDetailsView{


    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_details_activity);

        RoomDetailsViewModel viewModel = new ViewModelProvider(this).get(RoomDetailsViewModel.class);
        viewModel.getPresenter().setView(this);

        if (savedInstanceState == null){
            Intent intent = getIntent();

        }


    }
}