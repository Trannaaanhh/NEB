package TCP; // Bạn có thể đổi tên package này cho phù hợp với project NetBeans

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPClientSum {

    public static void main(String[] args) throws Exception {
        // Thay đổi IP này thành IP server của bạn nếu cần
        String serverIP = "203.162.10.109"; 
        int serverPort = 2206;
        
        // Sử dụng MSV từ ví dụ của bạn, và qCode từ đề bài này
        String studentCode = "B22DCVT034"; 
        String qCode = "PDhpRMDs";
        int timeout = 5000; // 5 giây

        // Sử dụng try-with-resources để Socket tự động đóng
        try (Socket socket = new Socket()) {
            
            // Kết nối tới server với thời gian timeout
            socket.connect(new InetSocketAddress(serverIP, serverPort), timeout);
            
            // Đặt thời gian timeout cho việc đọc dữ liệu
            socket.setSoTimeout(timeout);

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            // a. Gửi mã sinh viên và mã câu hỏi
            String request = studentCode + ";" + qCode;
            out.write(request.getBytes());
            out.flush();
            System.out.println("Sent: " + request);

            // b. Nhận dữ liệu từ server (chuỗi các số)
            byte[] buffer = new byte[1024];
            int bytesRead = in.read(buffer);
            String receivedData = new String(buffer, 0, bytesRead).trim();
            System.out.println("Received: " + receivedData);

            // c. Tính tổng và gửi lên server
            // Tách chuỗi bằng dấu "|", chuyển sang số và tính tổng
            // Phải dùng "\\|" vì "|" là ký tự đặc biệt trong regex
            long sum = Arrays.stream(receivedData.split("\\|"))
                             .mapToLong(Long::parseLong)
                             .sum();
            
            String sumStr = String.valueOf(sum);
            out.write(sumStr.getBytes());
            out.flush();
            System.out.println("Sent sum: " + sumStr);

            // d. Đóng kết nối
            // Kết nối được tự động đóng khi kết thúc khối try-with-resources
        }
    }
}