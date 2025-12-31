package TCP;
import java.util.*;
import java.net.*;
import java.io.*;

public class TCPClient_CharCount {
    public static void main(String[] args) throws Exception {
        String sv = "B22DCVT034", qCode = "4g6DiEih", ip = "203.162.10.109"; int port = 2208;
        try(Socket s = new Socket()) {
            s.connect(new InetSocketAddress(ip, port), 5000);
            s.setSoTimeout(5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            
            out.write(sv + ";" + qCode);
            out.newLine();
            out.flush();
            
            String rcv = in.readLine();    
            
            int tansuat[] = new int[256];
            for (char c : rcv.toCharArray()) tansuat[c]++;
            String res = "";
            for(int i = 0; i < rcv.length(); i++) {
                char c = rcv.charAt(i);
                if(Character.isLetterOrDigit(c) && tansuat[c] > 1) {
                    res += c + ":" + tansuat[c] + ",";
                    tansuat[c] = 0;
                }
            }
            out.write(res);
            out.newLine();
            out.flush();
        }
    }
}
