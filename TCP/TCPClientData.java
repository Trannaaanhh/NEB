package TCP; // Hoặc tên package của bạn trong NetBeans

import java.io.*;
import java.net.*;

public class TCPClientData {

    public static void main(String[] args) throws Exception {
        // Thay đổi IP này thành IP server của bạn
        String serverIP = "203.162.10.109"; 
        int serverPort = 2207;
        
        // Sử dụng MSV từ ví dụ
        String studentCode = "B22DCVT034"; 
        String qCode = "rghN9t18";
        int timeout = 5000; // 5 giây

        // Sử dụng try-with-resources để tự động đóng Socket và các luồng
        try (Socket socket = new Socket()) {
            
            // Kết nối tới server với timeout
            socket.connect(new InetSocketAddress(serverIP, serverPort), timeout);
            
            // Đặt timeout cho việc đọc dữ liệu
            socket.setSoTimeout(timeout);

            // Khởi tạo luồng DataInputStream và DataOutputStream
            DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataIn = new DataInputStream(socket.getInputStream());

            // a. Gửi mã sinh viên và mã câu hỏi
            String request = studentCode + ";" + qCode;
            dataOut.writeUTF(request); // Gửi chuỗi
            dataOut.flush();
            System.out.println("Sent: " + request);

            // b. Nhận một số nguyên từ server
            int receivedNumber = dataIn.readInt(); // Đọc số nguyên
            System.out.println("Received: " + receivedNumber);

            // c. Chuyển đổi và gửi lên server
            String binaryString = Integer.toBinaryString(receivedNumber);
            String hexString = Integer.toHexString(receivedNumber).toUpperCase();
            
            String result = binaryString + ";" + hexString;
            
            dataOut.writeUTF(result); // Gửi chuỗi kết quả
            dataOut.flush();
            System.out.println("Sent result: " + result);

            // d. Đóng kết nối (tự động nhờ try-with-resources)
        }
    }
}