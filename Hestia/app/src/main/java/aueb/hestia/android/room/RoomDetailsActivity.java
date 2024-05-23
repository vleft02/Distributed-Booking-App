package aueb.hestia.android.room;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import aueb.hestia.R;
import aueb.hestia.UserInterface.UserConsole;
import aueb.hestia.android.login.LoginViewModel;
import aueb.hestia.android.search.SearchViewModel;
import java.util.ArrayList;
import aueb.hestia.Helper.DateRange;
import android.widget.Toast;

public class RoomDetailsActivity extends AppCompatActivity implements RoomDetailsView{
    String username;
    String roomname;
    double roomPrice;
    String roomArea;
    float roomRating;
    int noOfReviews;
    String dates;
    int noOfPersons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_details_activity);

        RoomDetailsViewModel viewModel = new ViewModelProvider(this).get(RoomDetailsViewModel.class);
        viewModel.getPresenter().setView(this);

        if (savedInstanceState == null){
            Intent intent = getIntent();

            username = intent.getStringExtra("username");
            roomname = intent.getStringExtra("roomName");
            roomPrice = intent.getDoubleExtra("roomPrice",0.0);
            roomArea = intent.getStringExtra("roomArea");
            roomRating = intent.getFloatExtra("roomRating",0.0f);
            noOfReviews = intent.getIntExtra("noOfReviews",0 );
            dates = intent.getStringExtra("dates");
            noOfPersons = intent.getIntExtra("noOfPersons",0 );
        }

        findViewById(R.id.BookButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //book the room
                viewModel.getPresenter().book(username,roomname,dates);
            }
        });


        findViewById(R.id.BackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Find TextView
        TextView roomNameDesc = findViewById(R.id.RoomNameDesc);
        TextView areaDesc = findViewById(R.id.AreaDesc);
        TextView roomp = findViewById(R.id.RoomPrice);
        TextView ratingDesc = findViewById(R.id.RatingDesc);
        TextView personsDesc = findViewById(R.id.PersonsDesc);


        // Set the text
        roomNameDesc.setText(roomname);
        areaDesc.setText(roomArea);
        roomp.setText(String.valueOf(roomPrice));
        ratingDesc.setText(String.valueOf(roomRating));
        personsDesc.setText(String.format("%d Persons", noOfPersons));


    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}