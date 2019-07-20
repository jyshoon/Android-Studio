package gnu.education.so.project;

import java.net.Socket;

public class SocketSingleton {
    private static Socket socket;
    public static void setSocket(Socket socketpass){
        socket = socketpass;
    }
    public static Socket getSocket(){
        return socket;
    }
}
