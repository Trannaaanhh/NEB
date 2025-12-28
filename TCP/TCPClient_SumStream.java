package TCP;
import java.io.*;
import java.net.*;

public class TCPClient_SumStream {
    public static void main(String[] args) throws Exception {
        String sv = "B22DCVT034", Qcode = "uL4G2gj5";
        try (Socket s = new Socket("203.162.10.109", 2206)) {
            s.setSoTimeout(5000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            //a gui ma sinh vien va ma cau hoi
            os.write((sv + ";" + Qcode).getBytes());
            os.flush();
            //b nhan mot chuoi so nguyen tu server duoc phan tach voi nhau bang "|"
            byte[] buf = new byte[1024];
            int len = is.read(buf);
            String str = new String(buf, 0, len).trim();
            System.out.println("receive: " + str);
            //c tinh tong cac chuoi so nguyen va gui len sv
            int sum = 0;
            for (String val : str.split("\\|")) sum += Integer.parseInt(val);
            os.write(String.valueOf(sum).getBytes());
            os.flush();
            System.out.println("sent: " + sum);
        }
    }
}