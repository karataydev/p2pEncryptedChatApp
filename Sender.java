import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Sender implements Runnable {
    private MyGUI myGUI;
    private String username;
    private InetAddress inetAddress;
    private MulticastSocket socket;


    public Sender(String username,MyGUI myGUI) throws UnknownHostException, SocketException {
        this.username = username;
        this.inetAddress = InetAddress.getByName("ff02::1");
        this.myGUI = myGUI;
    }

    @Override
    public void run() {
        DatagramPacket packet;
        try {
            socket = new MulticastSocket(6789);
            InetAddress inetlocal = null;

            Iterator it = NetworkInterface.getByName(myGUI.getNetDev()).inetAddresses().iterator();
            while(it.hasNext()){
                inetlocal = (InetAddress) it.next();
                System.out.println(inetlocal);

                if(!inetlocal.isLinkLocalAddress()){
                    break;
                }

            }
            socket.setNetworkInterface(NetworkInterface.getByName(myGUI.getNetDev()));



        } catch (SocketException ex) {
            ex.printStackTrace();
            //parent.quit();
        } catch (IOException e) {
            e.printStackTrace();
        }


        while(true){

            while(myGUI.getMyPair()==null){
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


           ByteArrayOutputStream bos = new ByteArrayOutputStream();

            try {
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(myGUI.getMyPair());
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] data = bos.toByteArray();

            myGUI.setMyPair(null);
            System.out.println("packet sent to: " + inetAddress);
            packet = new DatagramPacket(data, data.length, inetAddress, 6789);
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
    //getters and setters
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

    public MulticastSocket getSocket() {
        return socket;
    }

    public void setSocket(MulticastSocket socket) {
        this.socket = socket;
    }
}
