package UDP;

import java.io.*;
import java.net.*;
import java.util.*;

public class UDPClientMinMax {

    public static void main(String[] args) throws Exception {
        String serverIP = "203.162.10.109"; 
        int serverPort = 2207;
        
        String studentCode = "B22DCVT034";
        String qCode = "UhnK6BoU";

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddr = InetAddress.getByName(serverIP);

            // a. Gửi thông điệp là chuỗi “;studentCode;qCode”
            String request = ";" + studentCode + ";" + qCode;
            byte[] sendData = request.getBytes();
            socket.send(new DatagramPacket(sendData, sendData.length, serverAddr, serverPort));
            System.out.println("Sent: " + request);

            // b. Nhận thông điệp từ server
            byte[] receiveBuffer = new byte[8192];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            String receivedString = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
            System.out.println("Received: " + receivedString);

            // c. Thực hiện tìm giá trị lớn nhất và giá trị nhỏ nhất...
            String[] parts = receivedString.split(";", 2);
            String requestId = parts[0];
            
            IntSummaryStatistics stats = Arrays.stream(parts[1].split(","))
                                               .mapToInt(Integer::parseInt)
                                               .summaryStatistics();
            
            int max = stats.getMax();
            int min = stats.getMin();

            // ...và gửi thông điệp lên lên server theo định dạng “requestId;max,min”
            String result = requestId + ";" + max + "," + min;
            byte[] resultData = result.getBytes();
            socket.send(new DatagramPacket(resultData, resultData.length, serverAddr, serverPort));
            System.out.println("Sent result: " + result);

            // d. Đóng socket và kết thúc chương trình
        }
    }
}