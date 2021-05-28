
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.util.Enumeration;
import java.util.Scanner;

public class MyGUI extends JFrame implements ActionListener{


    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String msg = null;
    private String username ="";
    private String netDev = "";
    private Boolean isGate = false;
    private MyPair gatePair;
    private MyPair myPair = null;
    private Thread gatewaySenderThread;
    private Thread gatewayReceiverThread;
    private Thread senderThread;
    private Thread receiverThread;
    private JTextField textField1;
    private JPanel myPanel;
    private JMenuBar myMenuBar;
    private JMenu fileMenu,helpMenu;
    private JMenuItem connectItem, disconnectItem, generateItem;
    private JTextPane textPane1;
    private JTextPane textPane2;
    private JScrollPane sp;


    public MyGUI() throws HeadlessException {
        textField1.addActionListener(this);
        myMenuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        helpMenu = new JMenu("Help");
        helpMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                JOptionPane.showMessageDialog(myPanel,"Hello, This Application is developed by \nMaruf Emre Karatay.\nfor Network Course");

            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });
        connectItem = new JMenuItem("Connect to Network");
        connectItem.addActionListener(this);
        fileMenu.add(connectItem);

        generateItem = new JMenuItem("Generate Keys");
        generateItem.addActionListener(this);
        fileMenu.add(generateItem);

        disconnectItem = new JMenuItem("Disconnect from Network");
        disconnectItem.addActionListener(this);
        fileMenu.add(disconnectItem);



        myMenuBar.add(fileMenu);
        myMenuBar.add(helpMenu);
        setJMenuBar(myMenuBar);
        add(myPanel);
        Dimension dim = new Dimension(800,700);
        setMinimumSize(dim);
        setResizable(false);
        setTitle("Encrypted Chat App by Maruf Emre Karatay");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        pack();
        setVisible(true);
    }



    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==textField1){
            msg=textField1.getText();
            textField1.setText("");
            Cipher encryptCipher = null;
            try {
                encryptCipher = Cipher.getInstance("RSA");
            } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                noSuchAlgorithmException.printStackTrace();
            } catch (NoSuchPaddingException noSuchPaddingException) {
                noSuchPaddingException.printStackTrace();
            }
            try {
                encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            } catch (InvalidKeyException invalidKeyException) {
                invalidKeyException.printStackTrace();
            }
            byte[] secretMessageBytes = msg.getBytes(StandardCharsets.UTF_8);
            try {
                byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
                myPair = new MyPair(1,username,encryptedMessageBytes);
            } catch (IllegalBlockSizeException illegalBlockSizeException) {
                illegalBlockSizeException.printStackTrace();
            } catch (BadPaddingException badPaddingException) {
                badPaddingException.printStackTrace();
            }

        }

        if(e.getSource()== connectItem){
            JPanel pane = new JPanel();
            pane.setLayout(new GridLayout(0, 2, 2, 2));

            JTextField daysField = new JTextField(5);
            JTextField assignmentField = new JTextField(5);
            JCheckBox gatewayCheck = new JCheckBox();

            pane.add(new JLabel("Your username:"));
            pane.add(daysField);
            String s ="";
            try {
                Enumeration<NetworkInterface> niff = NetworkInterface.getNetworkInterfaces();
                while (niff.hasMoreElements()) {
                    String param = niff.nextElement().toString();
                    System.out.println(param);
                }
            } catch (SocketException socketException) {
                socketException.printStackTrace();
            }

            pane.add(new JLabel("Network device name: " ));
            pane.add(assignmentField);

            pane.add(new JLabel("Gateway Node:"));
            pane.add(gatewayCheck);


            int option = JOptionPane.showConfirmDialog(this, pane, "Please fill all the fields", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE);




            username = daysField.getText();
            netDev = assignmentField.getText();
            isGate = gatewayCheck.isSelected();

            Sender sender = null;
            Receiver receiver = null;
            try {
                sender = new Sender(username,this);
                receiver = new Receiver(username,this);
            } catch (UnknownHostException unknownHostException) {
                unknownHostException.printStackTrace();
            } catch (SocketException socketException) {
                socketException.printStackTrace();
            }


            senderThread = new Thread(sender);
            receiverThread = new Thread(receiver);

            senderThread.start();
            receiverThread.start();
            System.out.println("ENCODED in host: " + privateKey.getEncoded().toString());
            myPair = new MyPair(9,username, privateKey.getEncoded());
            pane.removeAll();

            if(isGate){
                JTextPane txtPane = new JTextPane();
                String content = "";

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://172.105.87.123:8080"))
                        .build();

                HttpResponse<String> response = null;
                try {
                    response = client.send(request,
                            HttpResponse.BodyHandlers.ofString());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                System.out.println("response from inline server:" + response.body());


                content = response.body();
                txtPane.setText(content);
                txtPane.setEditable(false);
                pane.add(new JLabel("Gateway List:"));
                pane.add(txtPane);

                JTextField pref = new JTextField();
                pane.add(new JLabel("Preferred gateway IP"));
                pane.add(pref);

                int option2 = JOptionPane.showConfirmDialog(this, pane, "Please fill all the fields", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE);



                GatewaySender gatewaySender = null;
                GatewayReceiver gatewayReceiver = null;
                try {
                     gatewaySender = new GatewaySender(pref.getText(),username,this);
                     gatewayReceiver = new GatewayReceiver(this);

                } catch (UnknownHostException unknownHostException) {
                    unknownHostException.printStackTrace();
                } catch (SocketException socketException) {
                    socketException.printStackTrace();
                }

                gatewaySenderThread = new Thread(gatewaySender);
                gatewayReceiverThread = new Thread(gatewayReceiver);

                gatewayReceiverThread.start();
                gatewaySenderThread.start();
                gatePair = new MyPair(9,username, privateKey.getEncoded());


            }
        }
        if(e.getSource()== disconnectItem){
            myPair = new MyPair( 7, username, "key".getBytes(StandardCharsets.UTF_8));
            System.exit(1);
        }
        if(e.getSource()== generateItem){
            KeyPairGenerator generator = null;
            try {
                generator = KeyPairGenerator.getInstance("RSA");
            } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                noSuchAlgorithmException.printStackTrace();
            }
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();
            System.out.println("Keys generated.");
        }

    }
    public static void main(String[] args) throws UnknownHostException, SocketException {
        MyGUI myGUI = new MyGUI();
    }

    public JTextField getTextField1() {
        return textField1;
    }

    public void setTextField1(JTextField textField1) {
        this.textField1 = textField1;
    }

    public JTextPane getTextPane1() {
        return textPane1;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setTextPane1(JTextPane textPane1) {
        this.textPane1 = textPane1;
    }

    public JTextPane getTextPane2() {
        return textPane2;
    }

    public void setTextPane2(JTextPane textPane2) {
        this.textPane2 = textPane2;
    }

    public void addTextPane1(String text) throws BadLocationException {
        StyledDocument document = (StyledDocument) textPane1.getDocument();
        document.insertString(document.getLength(), text, null);
    }

    public JPanel getMyPanel() {
        return myPanel;
    }

    public void setMyPanel(JPanel myPanel) {
        this.myPanel = myPanel;
    }

    public MyPair getMyPair() {
        return myPair;
    }

    public void setMyPair(MyPair myPair) {
        this.myPair = myPair;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNetDev() {
        return netDev;
    }

    public void setNetDev(String netDev) {
        this.netDev = netDev;
    }

    public Boolean getIsGate() {
        return isGate;
    }

    public void setIsGate(Boolean gate) {
        isGate = gate;
    }

    public MyPair getGatePair() {
        return gatePair;
    }

    public void setGatePair(MyPair gatePair) {
        this.gatePair = gatePair;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public Boolean getGate() {
        return isGate;
    }

    public void setGate(Boolean gate) {
        isGate = gate;
    }
}
