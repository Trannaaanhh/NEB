package TCP;
import java.util.*;
import java.net.*;
import java.io.*;

class Laptop implements Serializable{
    private static final long serialVersionUID = 20150711L;
    public int id, quantity;
    public String code, name;
    public Laptop(int id, String code, String name, int quantity) {
        this.id = id; this. code = code; this.name = name; this.quantity = quantity;
    }
    public String toString() {return id + " " + code + " " + name + " " + quantity; }
}

public class TCPClient_Laptop {
    public static void main(String[] args)throws Exception {
        String sv = "B22DCVT034", qCode = "kxFJkZzl", ip = "203.162.10.109"; int port = 2209;
        try(Socket s = new Socket()) {
            s.connect(new InetSocketAddress(ip, port), 5000);
            s.setSoTimeout(5000);
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            
            oos.writeObject(sv + ";" + qCode);
            oos.flush();
            
            Laptop lp = (Laptop) ois.readObject();
            
            lp.quantity = Integer.parseInt(new StringBuilder(String.valueOf(lp.quantity)).reverse().toString());
            String[] arr = lp.name.trim().split("\\s+");
            if(arr.length > 1) {
                String tmp = arr[0]; 
                arr[0] = arr[arr.length - 1];
                arr[arr.length - 1] = tmp;
                lp.name = String.join(" ", arr);
            }
            oos.writeObject(lp);
            oos.flush();
        }
    }
}
