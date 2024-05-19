package aueb.hestia.android.search;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import aueb.hestia.UserInterface.UserConsole;
import aueb.hestia.Domain.Room;
import aueb.hestia.R;
import java.util.*;
import org.json.simple.JSONObject;


public class SearchRoomsActivity extends AppCompatActivity implements SearchRoomsView {
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
        SearchViewModel viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
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
                String area = getArea();
                int noOfPersons = getNoOfPersons();
                float stars = getStars();
                String dates = getDates();
                JSONObject search = new JSONObject();
                search.put("area",area);
                search.put("dateRange",dates);
                search.put("noOfPersons",noOfPersons);
                search.put("stars",stars);
                search.put("function","search");
                ArrayList<Room> response = (ArrayList<Room>)new UserConsole(search).request();

                //edo skeftomai na kaloume mia methodo poy tha tiponei ta apotelesmata
                //apo ti lista room sto room_list_item.xml
//                for (Room room : response)
//                            {
//                                System.out.println(room);
//                            }
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

    @Override
    public String getDates() {

        String dateRange= ((EditText) findViewById(R.id.FromDateField)).getText().toString().trim() + "-" + ((EditText) findViewById(R.id.ToDateField)).getText().toString().trim();
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
}