package TCP;
//import java.util.*;
import java.net.*;
import java.io.*;

public class TCPClientDataBinary {
    public static void main(String[] args) throws Exception {
        String sv = "B22DCVT034", qCode = "fC3TVtaN", ip = "203.162.10.109"; int port = 2207;
        try(Socket s = new Socket()){
            s.connect(new InetSocketAddress(ip, port),5000);
            s.setSoTimeout(5000);
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            
            dos.writeUTF(sv + ";" + qCode);
            dos.flush();
            
            int rcv = dis.readInt();
            
            dos.writeUTF(Integer.toBinaryString(rcv));
            dos.flush();
        }
    }
}
