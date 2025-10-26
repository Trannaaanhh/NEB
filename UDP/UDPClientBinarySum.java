package UDP; // Hoặc tên package của bạn trong NetBeans

import java.io.*;
import java.net.*;
import java.util.*;

public class UDPClientBinarySum {

    public static void main(String[] args) throws Exception {
        String serverIP = "203.162.10.109";
        int serverPort = 2208;
        
        String studentCode = "B22DCVT034"; 
        String qCode = "uLpAe0V9";

        // Sử dụng try-with-resources để socket tự động đóng
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddr = InetAddress.getByName(serverIP);

            // a. Gửi thông điệp là một chuỗi chứa mã sinh viên và mã câu hỏi
            String request = ";" + studentCode + ";" + qCode;
            byte[] sendData = request.getBytes();
            socket.send(new DatagramPacket(sendData, sendData.length, serverAddr, serverPort));
            System.out.println("Sent: " + request);

            // b. Nhận thông điệp là một chuỗi từ server theo định dạng “requestId;b1,b2”
            byte[] receiveBuffer = new byte[8192];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            String receivedString = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
            System.out.println("Received: " + receivedString);

            // c. Thực hiện tính tổng hai số nhị phân nhận được
            String[] parts = receivedString.split(";", 2);
            String requestId = parts[0];
            String[] binaryNumbers = parts[1].split(",");
            
            // Chuyển đổi từ nhị phân (radix 2) sang số nguyên
            // (Ví dụ: "0100011111001101" -> 18381)
            int n1 = Integer.parseInt(binaryNumbers[0], 2);
            int n2 = Integer.parseInt(binaryNumbers[1], 2);
            
            int sum = n1 + n2; // (Ví dụ: 18381 + 53749 = 72130)
            
            // ...chuyển về dạng thập phân và gửi lên server theo định dạng “requestId;sum”
            String result = requestId + ";" + sum;
            byte[] resultData = result.getBytes();
            socket.send(new DatagramPacket(resultData, resultData.length, serverAddr, serverPort));
            System.out.println("Sent result: " + result);

            // d. Đóng socket và kết thúc chương trình (tự động nhờ try-with-resources)
        }
    }
}