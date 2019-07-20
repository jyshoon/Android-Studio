package gnu.education.so.project;

public class RoomListItemData {
    public RoomListItemData (String _roomName, String _num, String _status) {
        roomName = _roomName;
        numPlayers = _num;
        roomStatus = _status;
    }
    private String roomName;
    private String numPlayers;
    private String roomStatus;

    public void setRoomName (String name) {
        roomName = name;
    }

    public void setNumPlayers (String num) {
        numPlayers = num;
    }

    public void setRoomStatus (String status) {
        roomStatus = status;
    }

    public String getRoomName () {
        return roomName;
    }

    public String getNumPlayers ()  {
        return numPlayers;
    }

    public String getRoomStatus () {
        return roomStatus;
    }
}
