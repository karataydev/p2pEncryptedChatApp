import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.text.BadLocationException;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

public class Receiver implements Runnable {

    private MyGUI myGUI;
    private Scanner sc;
    private InetAddress inetAddress;
    private InetAddress myInetAddress;
    private String username;
    private List<MyPair> userList;
    private Boolean isGate;




    public Receiver(String username,MyGUI myGUI) throws UnknownHostException, SocketException {
        this.username = username;
        userList = new ArrayList<MyPair>();
        sc = new Scanner(System.in);
        inetAddress = InetAddress.getByName("ff02::1");
        this.myGUI = myGUI;
        this.isGate = myGUI.getIsGate();

    }
    public void userListToGUI(){
        String txt = "";
        for(MyPair p: userList){
            txt = txt + p.getUsername() + "\n";
        }
        myGUI.getTextPane2().setText(txt);
    }

    @Override
    public void run() {
        MulticastSocket socket = null;
        InetAddress inetlocal = null;
        try {
            Iterator it = NetworkInterface.getByName(myGUI.getNetDev()).inetAddresses().iterator();
            while(it.hasNext()){
                inetlocal = (InetAddress) it.next();
                System.out.println(inetlocal);

                if(inetlocal.isLinkLocalAddress()){
                    break;
                }

            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {

            socket = new MulticastSocket(6789);
            socket.setNetworkInterface(NetworkInterface.getByName(myGUI.getNetDev()));
            socket.joinGroup(inetAddress);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DatagramPacket packet = new DatagramPacket(new byte[128000], 128000);
        MyPair temp = null;
        while(true){
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
                temp = (MyPair) in.readObject();
                if(isGate){
                    myGUI.setGatePair(temp);
                }
                System.out.println("packet came from: " + packet.getAddress()+" with code: "+temp.getCode());
                if(temp.getCode()==9){
                    userList.add(temp);
                    userListToGUI();


                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    ObjectOutputStream outs = new ObjectOutputStream(bout);
                    for (MyPair element : userList) {
                        outs.writeObject(element);
                    }
                    byte[] bytes = bout.toByteArray();

                    myGUI.setMyPair(new MyPair(8, temp.getUsername(),bytes));

                }
                else if(temp.getCode()==1){
                    Cipher decryptCipher = Cipher.getInstance("RSA");
                    MyPair finalTemp1 = temp;
                    MyPair userpair = userList.stream().filter((p) -> p.getUsername().equals(finalTemp1.getUsername())).findAny().orElse(null);
                    System.out.println("Message before decryption: " + temp.getData());
                    System.out.println("Key of the user: " + userpair.getData().toString());

                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(userpair.getData());
                    PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);


                    Cipher cipher = Cipher.getInstance("RSA");

                    cipher.init(Cipher.DECRYPT_MODE, privateKey);
                    byte[] result = cipher.doFinal(temp.getData());
                    String decryptedMessage = new String(result, StandardCharsets.UTF_8);


                    myGUI.addTextPane1(temp.getUsername()+": "+ decryptedMessage+"\n");
                }
                else if(temp.getCode()==8 && temp.getUsername().equals(username) && !packet.getAddress().equals(inetlocal)){


                    System.out.println("Receiving user list: ");
                    try{
                        ByteArrayInputStream bais = new ByteArrayInputStream(temp.getData());
                        ObjectInputStream inArray = new ObjectInputStream(bais);
                        Object obj = null;
                        userList.clear();
                        for(;;){
                            try{
                                obj = inArray.readObject();
                                userList.add((MyPair) obj);
                                System.out.println("user: "+userList.get(userList.size()-1));
                            }
                            catch (EOFException exc){
                                break;
                            }

                        }
                        inArray.close();
                        bais.close();

                    }
                    catch (IOException e){
                        e.printStackTrace();

                    }
                    userListToGUI();


                }
                else if(temp.getCode()==7){

                    MyPair finalTemp = temp;
                    userList.removeIf(myPair -> myPair.getUsername().equals(finalTemp.getUsername()));
                    System.out.println(temp.getUsername() + ": disconnected.");
                    userListToGUI();
                }
                in.close();

            } catch (IOException | ClassNotFoundException | BadLocationException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException | InvalidKeyException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            }

        }


    }
    //Getters and setters
    public MyGUI getMyGUI() {
        return myGUI;
    }

    public void setMyGUI(MyGUI myGUI) {
        this.myGUI = myGUI;
    }

    public Scanner getSc() {
        return sc;
    }

    public void setSc(Scanner sc) {
        this.sc = sc;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public InetAddress getMyInetAddress() {
        return myInetAddress;
    }

    public void setMyInetAddress(InetAddress myInetAddress) {
        this.myInetAddress = myInetAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<MyPair> getUserList() {
        return userList;
    }

    public void setUserList(List<MyPair> userList) {
        this.userList = userList;
    }

    public Boolean getGate() {
        return isGate;
    }

    public void setGate(Boolean gate) {
        isGate = gate;
    }
}
