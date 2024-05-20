package aueb.hestia.android.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import aueb.hestia.Domain.Room;
import aueb.hestia.R;

public class SearchRoomsRecyclerViewAdapter extends RecyclerView.Adapter<SearchRoomsRecyclerViewAdapter.ViewHolder>{

    private final ArrayList<Room> rooms;
    private final SearchRoomsRecyclerViewAdapter.SearchRoomsSelectionListener listener;

    SearchRoomsRecyclerViewAdapter(ArrayList<Room> rooms,SearchRoomsRecyclerViewAdapter.SearchRoomsSelectionListener listener )
    {
        this.rooms = rooms;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchRoomsRecyclerViewAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Room currentItem = rooms.get(position);
        holder.roomName.setText(String.valueOf(currentItem.getRoomName()));
        holder.roomPrice.setText(String.valueOf(currentItem.getPrice()) + " $");
        holder.roomRating.setText(String.valueOf(currentItem.getStars()));
        holder.roomDescription.setText(String.valueOf(currentItem.getArea())+", "+String.valueOf(currentItem.getNoOfPersons())+" Persons");

        //Room Image code

        holder.roomImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.selectRoom(currentItem);
            }
        });
    }

    /**
     *
     * @return επιστρέφει το μέγεθος της λίστας με τα εστιατόρια
     */
    @Override
    public int getItemCount() {
        return rooms.size();
    }
    /**
     * καλεί την μέθοδο που θέλουμε όταν πατηθεί ένα εστιατόριο
     */
    public interface SearchRoomsSelectionListener
    {
        void selectRoom(Room room);
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final TextView roomName;
        public final TextView roomPrice;
        public final TextView roomDescription;
        public final TextView roomRating;
        public ImageView roomImage;

        public ViewHolder(View view)
        {
            super(view);
            roomName = view.findViewById(R.id.RoomName);
            roomPrice = view.findViewById(R.id.RoomPrice);
            roomDescription = view.findViewById(R.id.RoomDescription);
            roomRating = view.findViewById(R.id.RoomRating);
            roomImage = view.findViewById(R.id.RoomImage);
        }
        /**
         * @return τα στοιχεία της παραγγελίας καλεστεί με System.out.print
         */
        @Override
        public String toString() {
            return super.toString() + " '" + roomName.getText() + "'";
        }
    }
}

