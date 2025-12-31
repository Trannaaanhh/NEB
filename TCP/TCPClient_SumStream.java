package TCP;
//import java.util.*;
import java.net.*;
import java.io.*;

public class TCPClient_SumStream {
    public static void main(String[] args)throws Exception {
        String sv = "B22DCVT034", qCode = "uL4G2gj5", ip = "203.162.10.109"; int port = 2206;
        try(Socket s = new Socket()) {
            s.connect(new InetSocketAddress(ip, port), 5000);
            s.setSoTimeout(5000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            
            os.write((sv + ";" + qCode).getBytes());
            os.flush();
            
            byte[] buf = new byte[1024];
            int len = is.read(buf);
            String rcv = new String(buf, 0, len).trim();
            
            int sum = 0;
            for(String val : rcv.split("\\|")) sum += Integer.parseInt(val);
            os.write(String.valueOf(sum).getBytes());
            os.flush();
        }
    }
}
