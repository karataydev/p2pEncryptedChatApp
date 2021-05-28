import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.List;

public class GatewaySender implements Runnable{

    private MyGUI myGUI;
    private String username;
    private InetAddress inetAddress;
    private DatagramSocket socket;
    private String gatewayDev;
    private List<String> gatewayList;

    public GatewaySender(String inetAddr, String username, MyGUI myGUI) throws UnknownHostException, SocketException {
        this.username = username;
        this.inetAddress = InetAddress.getByName(inetAddr);
        this.myGUI = myGUI;
    }

    @Override
    public void run() {
        DatagramPacket packet;
        try {
            socket = new DatagramSocket(6791);
        } catch (SocketException ex) {
            ex.printStackTrace();
            //parent.quit();
        }


        while(true){

            while(myGUI.getGatePair()==null){
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            try {
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(myGUI.getGatePair());
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] data = bos.toByteArray();

            myGUI.setGatePair(null);

            System.out.println("Gateway packet sending to: " + inetAddress);
            packet = new DatagramPacket(data, data.length, inetAddress, 6790);
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    //getters and setters


    public List<String> getGatewayList() {
        return gatewayList;
    }

    public void setGatewayList(List<String> gatewayList) {
        this.gatewayList = gatewayList;
    }

    public MyGUI getMyGUI() {
        return myGUI;
    }

    public void setMyGUI(MyGUI myGUI) {
        this.myGUI = myGUI;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public String getGatewayDev() {
        return gatewayDev;
    }

    public void setGatewayDev(String gatewayDev) {
        this.gatewayDev = gatewayDev;
    }
}


