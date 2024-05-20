package aueb.hestia.RoomAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import aueb.hestia.Domain.Room;
import aueb.hestia.R;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private List<Room> roomList;

    public RoomAdapter(List<Room> roomList) {
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_list_item, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.roomName.setText(room.getRoomName());
        holder.roomDescription.setText(room.getArea());
        holder.roomRating.setText(Float.toString(room.getStars()));
        holder.roomPrice.setText(Double.toString(room.getPrice()));
        holder.roomImage.setImageResource(Integer.parseInt(room.getRoomImage()));
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView roomImage;
        TextView roomName, roomDescription, roomRating, roomPrice, star;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomImage = itemView.findViewById(R.id.RoomImage);
            roomName = itemView.findViewById(R.id.RoomName);
            roomDescription = itemView.findViewById(R.id.RoomDescription);
            roomRating = itemView.findViewById(R.id.RoomRating);
            roomPrice = itemView.findViewById(R.id.RoomPrice);
            star = itemView.findViewById(R.id.Star);
        }
    }
}

