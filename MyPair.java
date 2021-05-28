import java.io.Serializable;

public class MyPair implements Serializable {
    private int code;
    private String username;
    private byte[] data;


    public MyPair(int code,String username, byte[] data) {
        this.code=code;
        this.username = username;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public MyPair() {
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    @Override
    public String toString() {
        return
                username + ": " + data.toString();
    }
}
