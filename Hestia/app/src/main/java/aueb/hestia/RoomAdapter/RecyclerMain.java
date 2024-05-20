package aueb.hestia.RoomAdapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import aueb.hestia.Domain.Room;
import aueb.hestia.R;

public class RecyclerMain extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_results);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        roomList = new ArrayList<>();
        // Populate the roomList with Room objects
        //roomList.add(new Room("Vagelis", "Deluxe Double Suite", 2, "Airport", 4,10,120,"fdgdfdhgh"));
        //roomList.add(new Room("Vagelis", "Deluxe Double Suite", 2, "Airport", 4,10,120,"fdgdfdhgh"));
        //roomList.add(new Room("Vagelis", "Deluxe Double Suite", 2, "Airport", 4,10,120,"fdgdfdhgh"));
        //roomList.add(new Room("Standard Room", "A comfortable room", "4.2", "80$ Per Night", R.drawable.standard_room));
        // Add more rooms as needed

        roomAdapter = new RoomAdapter(roomList);
        recyclerView.setAdapter(roomAdapter);
    }
}
