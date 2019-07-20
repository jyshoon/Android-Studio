package gnu.education.so.project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.education.so.project.R;

import java.util.ArrayList;

public class RoomListAdapter extends BaseAdapter {

    private ArrayList<RoomListItemData> roomItemList = null;



    public RoomListAdapter (ArrayList<RoomListItemData> _list) {
        roomItemList = _list;
    }

    @Override
    public int getCount () {

        return roomItemList.size();
    }

    @Override
    public Object getItem(int position)
    {

        return roomItemList.get(position);
    }

    @Override
    public long getItemId(int position)
    {

        return position;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            Log.d("--------------------->", "___");
            LayoutInflater inflater =
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.room_list_item, parent, false);
        }
        else {
            Log.d("++++++++++++++++", "+++++++++++");
        }

        TextView oTextRoomName = (TextView) convertView.findViewById(R.id.roomListItemName);
        TextView oTextNumPlayers = (TextView) convertView.findViewById(R.id.roomListItemCount);
        TextView oTextRoomStatus = (TextView) convertView.findViewById(R.id.roomListItemStatus);

        oTextRoomName.setText(roomItemList.get(position).getRoomName());
        oTextNumPlayers.setText(roomItemList.get(position).getNumPlayers());
        oTextRoomStatus.setText(roomItemList.get(position).getRoomStatus());
        return convertView;
    }

}
