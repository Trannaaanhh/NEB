package UDP;
import java.io.*; 
import java.net.*;

public class UDPClient_SumDigits {
    public static void main(String[] args) throws Exception {
        String sv = "B22DCVT034", qCode = "GwRPS6qN";
        InetAddress ip = InetAddress.getByName("203.162.10.109"); int port = 2207;
        try (DatagramSocket s = new DatagramSocket()) {
            // a. Gửi thông điệp ";studentCode;qCode"
            byte[] buf = (";" + sv + ";" + qCode).getBytes();
            s.send(new DatagramPacket(buf, buf.length, ip, port));
            // b. Nhận "requestId;num"
            byte[] rcv = new byte[1024];
            DatagramPacket p = new DatagramPacket(rcv, rcv.length);
            s.receive(p);
            String[] data = new String(p.getData(), 0, p.getLength()).trim().split(";");
            String reqId = data[0], num = data[1];
            // c. Tính tổng các chữ số
            int sum = 0;
            for (char c : num.toCharArray()) {
                sum += c - '0'; // Trừ '0' để lấy giá trị số của ký tự
            }
            // Gửi "requestId;sumDigits"
            byte[] send = (reqId + ";" + sum).getBytes();
            s.send(new DatagramPacket(send, send.length, ip, port));
            System.out.println("Sent: " + reqId + ";" + sum);
            // d. Đóng socket (Tự động)
        }
    }
}