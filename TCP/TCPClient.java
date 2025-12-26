package TCP;

import java.io.*;
import java.net.*;

public class TCPClient{
    public static void main(String[] args) throws Exception{
        String sv = "B22DCVT034", qCode = "fC3TVtaN";
        try(Socket s = new Socket()){
            s.connect(new InetSocketAddress("203.162.10.109", 2207),5000);
            s.setSoTimeout(5000);
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            //a
            dos.writeUTF(sv + ";" + qCode);
            dos.flush();
            //b
            int n = dis.readInt();
            //c
            dos.writeUTF(Integer.toBinaryString(n));
            dos.flush();
            //d
        }
    }
} 
        