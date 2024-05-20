package aueb.hestia.android.search;

import static java.lang.Integer.parseInt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import aueb.hestia.UserInterface.UserConsole;
import aueb.hestia.Domain.Room;
import aueb.hestia.R;
import aueb.hestia.android.login.LoginActivity;
import aueb.hestia.android.room.RoomDetailsActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.json.simple.JSONObject;


public class SearchRoomsActivity extends AppCompatActivity implements SearchRoomsView ,SearchRoomsRecyclerViewAdapter.SearchRoomsSelectionListener{

    SearchViewModel viewModel;
    String username;
    TextView welcomeText;
    TextView noRoomsText;
    ConstraintLayout filtersPanel;
    RecyclerView roomsRecyclerView;
    UserConsole uc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_rooms_activity);
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        viewModel.getPresenter().setView(this);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            if (extras != null) {
                username = extras.getString("username");
            }
        }
        filtersPanel = findViewById(R.id.FIltersPanel);
        welcomeText = findViewById(R.id.WelcomeText);
        roomsRecyclerView = findViewById(R.id.RoomsRecyclerView);
        noRoomsText = findViewById(R.id.NoRoomsFoundMessage);


        filtersPanel.setVisibility(View.GONE);
        welcomeText.setText("Welcome Back "+username+" !");
        findViewById(R.id.ShowFiltersPanelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintLayout FiltersPanel =  findViewById(R.id.FIltersPanel);

                toggleFiltersPanel(FiltersPanel);

            }


        });


        findViewById(R.id.FromDateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText fromDateField = findViewById(R.id.FromDateField);
                Calendar fromDate = Calendar.getInstance();
                showDatePickerDialog(fromDate, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        fromDate.set(year, month, dayOfMonth);

                        fromDateField.setText(fromDate.get(Calendar.DAY_OF_MONTH)+"/"+fromDate.get(Calendar.MONTH)+"/"+fromDate.get(Calendar.YEAR));
                    }
                });
            }


        });

        findViewById(R.id.ToDateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText toDateField = findViewById(R.id.ToDateField);
                Calendar toDate = Calendar.getInstance();
                showDatePickerDialog(toDate, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        toDate.set(year, month, dayOfMonth);

                        toDateField.setText(toDate.get(Calendar.DAY_OF_MONTH)+"/"+toDate.get(Calendar.MONTH)+"/"+toDate.get(Calendar.YEAR));
                    }
                });
            }
        });

        //pare ta inputs tou xristi oste na kaneis anazitisi
        findViewById(R.id.ApplyFiltersButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Room> rooms = viewModel.getPresenter().search();
                showRooms(rooms);
            }
        });
    }


    @Override
    public void toggleFiltersPanel(ConstraintLayout FiltersPanel) {
        if(FiltersPanel.getVisibility()==View.VISIBLE){
            FiltersPanel.setVisibility(View.GONE);
        }else{
            FiltersPanel.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public String getArea() {
         return ((EditText)findViewById(R.id.AreaField)).getText().toString().trim();
    }

    @Override
    public int getNoOfPersons() {
        String content = (((EditText)findViewById(R.id.PersonsField)).getText().toString());
        int noOfPersons = Integer.valueOf(content);
        return noOfPersons;
    }

    @Override
    public float getStars() {
        return ((RatingBar)findViewById(R.id.RatingBar)).getRating();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public String getDates() {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        String from = ((EditText) findViewById(R.id.FromDateField)).getText().toString().trim();
        String to = ((EditText) findViewById(R.id.ToDateField)).getText().toString().trim();

        LocalDate fromDate =LocalDate.parse(from,inputFormatter);
        LocalDate toDate = LocalDate.parse(to,inputFormatter);

        String dateRange = fromDate.format(outputFormatter)+"-"+toDate.format(outputFormatter);

        return dateRange;
    }


    private void showDatePickerDialog(Calendar date, DatePickerDialog.OnDateSetListener listener) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                listener,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }


    public void showRooms(ArrayList<Room> rooms)
    {
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomsRecyclerView.setAdapter(new SearchRoomsRecyclerViewAdapter(rooms, this));
    }

    @Override
    public void selectRoom(Room room) {
        Intent intent = new Intent(SearchRoomsActivity.this, RoomDetailsActivity.class);
        intent.putExtra("username",username);
        intent.putExtra("roomName",room.getRoomName());
        intent.putExtra("roomPrice",room.getPrice());
        intent.putExtra("roomArea",room.getArea());
        intent.putExtra("roomRating",room.getArea());
        intent.putExtra("noOfReviews", room.getNoOfReviews());
        intent.putExtra("dates",room.getAvailability());

        startActivity(intent);
    }
}