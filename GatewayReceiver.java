import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class GatewayReceiver implements Runnable{

    private DatagramSocket socket;
    private MyGUI myGUI;

    public GatewayReceiver(MyGUI myGUI) {
        this.myGUI = myGUI;
    }

    @Override
    public void run() {
        DatagramPacket packet;
        MyPair temp = null;

        try {
            socket = new DatagramSocket(6790);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        packet = new DatagramPacket(new byte[128000], 128000);
        while(true){
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
                temp = (MyPair) in.readObject();
                myGUI.setMyPair(temp);
                System.out.println("packet came to GatewayReceiver: " + temp.getUsername());

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }



        }


    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public MyGUI getMyGUI() {
        return myGUI;
    }

    public void setMyGUI(MyGUI myGUI) {
        this.myGUI = myGUI;
    }
}
