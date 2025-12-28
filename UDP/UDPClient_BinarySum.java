package UDP;
import java.io.*; import java.net.*;

public class UDPClient_BinarySum {
    public static void main(String[] args) throws Exception {
        String sv = "B22DCVT034", qCode = "clUrLix6";
        InetAddress ip = InetAddress.getByName("203.162.10.109"); int port = 2208;

        try (DatagramSocket s = new DatagramSocket()) {
            // a. Gửi request: ";Masv;qCode"
            byte[] buf = (";" + sv + ";" + qCode).getBytes();
            s.send(new DatagramPacket(buf, buf.length, ip, port));

            // b. Nhận chuỗi: "requestId;b1,b2"
            byte[] rcv = new byte[1024];
            DatagramPacket p = new DatagramPacket(rcv, rcv.length);
            s.receive(p);
            String str = new String(p.getData(), 0, p.getLength()).trim();
            System.out.println("Received: " + str);

            // c. Tách chuỗi và tính tổng
            String[] parts = str.split(";"); // Tách lấy requestId và cụm nhị phân
            String reqId = parts[0];
            String[] nums = parts[1].split(","); // Tách b1 và b2
            
            // Đổi nhị phân (cơ số 2) sang int và cộng
            int sum = Integer.parseInt(nums[0], 2) + Integer.parseInt(nums[1], 2);
            
            // Gửi kết quả: "requestId;sum"
            byte[] send = (reqId + ";" + sum).getBytes();
            s.send(new DatagramPacket(send, send.length, ip, port));
            System.out.println("Sent: " + reqId + ";" + sum);
            
            // d. Đóng socket (Tự động)
        }
    }
}
