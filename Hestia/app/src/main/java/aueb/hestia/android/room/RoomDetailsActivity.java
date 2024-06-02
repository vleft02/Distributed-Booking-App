package aueb.hestia.android.room;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import aueb.hestia.R;
import aueb.hestia.UserInterface.UserConsole;
import aueb.hestia.android.login.LoginViewModel;
import aueb.hestia.android.search.SearchRoomsActivity;
import aueb.hestia.android.search.SearchViewModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import aueb.hestia.Helper.DateRange;
import android.widget.Toast;

public class RoomDetailsActivity extends AppCompatActivity implements RoomDetailsView {
    String username;
    String roomname;
    double roomPrice;
    String roomArea;
    float roomRating;
    int noOfReviews;
    String dates;
    int noOfPersons;
    String roomImage;


    TextView ratingDesc;
    TextView noOfReviewsDesc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_details_activity);

        RoomDetailsViewModel viewModel = new ViewModelProvider(this).get(RoomDetailsViewModel.class);
        viewModel.getPresenter().setView(this);

        if (savedInstanceState == null) {
            Intent intent = getIntent();

            username = intent.getStringExtra("username");
            roomname = intent.getStringExtra("roomName");
            roomPrice = intent.getDoubleExtra("roomPrice", 0.0);
            roomArea = intent.getStringExtra("roomArea");
            roomImage = intent.getStringExtra("roomImage");
            roomRating = intent.getFloatExtra("roomRating", 0.0f);
            noOfReviews = intent.getIntExtra("noOfReviews", 0);
            dates = intent.getStringExtra("dates");
            noOfPersons = intent.getIntExtra("noOfPersons", 0);
        }

        findViewById(R.id.BookButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //book the room
                viewModel.getPresenter().book(username, roomname, dates);
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
        TextView dateDesc = findViewById(R.id.DatesDesc);

        ratingDesc = findViewById(R.id.RatingDesc);
        TextView personsDesc = findViewById(R.id.PersonsDesc);

        noOfReviewsDesc = findViewById(R.id.NoOfReviews);
        ImageView roomImageView = findViewById(R.id.RoomSpecificImage);


        Bitmap image = BitmapFactory.decodeFile(getFilesDir()+"/"+roomImage);
        roomImageView.setImageBitmap(image);


        // Set the text
        roomNameDesc.setText(roomname);
        areaDesc.setText(roomArea);
        roomp.setText(String.valueOf(roomPrice+"$/Night"));
        dateDesc.setText(dates);
        ratingDesc.setText(String.valueOf(roomRating));
        personsDesc.setText(String.format("%d Persons", noOfPersons));
        noOfReviewsDesc.setText(String.valueOf("By "+noOfReviews+" users"));
        findViewById(R.id.ReviewButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getPresenter().review(username,roomname);
            }
        });


    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDialog(String message) {

        new AlertDialog.Builder(RoomDetailsActivity.this)
                .setCancelable(true)
                .setTitle("Review")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        hideReviewWidget();
                    }
                }).create().show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public float getStarsroom() {
        return ((RatingBar) findViewById(R.id.ReviewRB)).getRating();
    }
    @Override
    public int getNoOfReviews(){
        return noOfReviews;
    }

    @Override
    public void updateRating() {
        float stars = getStarsroom();
        noOfReviews++;
        roomRating = (roomRating * (noOfReviews - 1) + stars) / noOfReviews;
        ratingDesc.setText(String.format("%.2f", roomRating));
        noOfReviewsDesc.setText(String.valueOf("By "+noOfReviews+" users" ));
    }

    @Override
    public void showBookingDialog(String message) {
        new AlertDialog.Builder(RoomDetailsActivity.this)
                .setCancelable(true)
                .setTitle("Booking")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onBackPressed();
                    }
                }).create().show();
    }

    @Override
    public void hideReviewWidget() {
        findViewById(R.id.ReviewRB).setVisibility(View.GONE);
        findViewById(R.id.ReviewButton).setVisibility(View.GONE);
    }



}
