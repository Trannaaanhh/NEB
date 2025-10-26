package TCP; // Hoặc tên package của bạn trong NetBeans

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class TCPClientFilterString {

    public static void main(String[] args) throws Exception {
        // Thay đổi IP này thành IP server của bạn
        String serverIP = "203.162.10.109"; 
        int serverPort = 2208;

        String studentCode = "B22DCVT034"; // Ví dụ MSV
        String qCode = "OGCRM8Ni";
        int timeout = 5000; // 5 giây

        // Sử dụng try-with-resources để tự động đóng Socket và các luồng
        try (Socket socket = new Socket()) {
            
            // Kết nối tới server với timeout
            socket.connect(new InetSocketAddress(serverIP, serverPort), timeout);
            
            // Đặt timeout cho việc đọc dữ liệu
            socket.setSoTimeout(timeout);

            // Khởi tạo luồng ký tự
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // a. Gửi mã sinh viên và mã câu hỏi
            String request = studentCode + ";" + qCode;
            writer.write(request);
            writer.newLine(); // Gửi ký tự xuống dòng để server có thể readLine()
            writer.flush();
            System.out.println("Sent: " + request);

            // b. Nhận một chuỗi ngẫu nhiên từ server
            String receivedString = reader.readLine();
            if (receivedString == null) {
                System.out.println("Server did not send data.");
                return;
            }
            System.out.println("Received: " + receivedString);

            // c. Xử lý chuỗi và gửi lên server
            // 1. Lấy stream các ký tự (dạng int)
            // 2. Lọc, chỉ giữ lại các ký tự là chữ cái
            // 3. Loại bỏ các ký tự trùng lặp (vẫn giữ thứ tự)
            // 4. Thu thập kết quả lại thành một String
            String processedString = receivedString.chars()
                    .filter(Character::isLetter)
                    .distinct()
                    .collect(StringBuilder::new, 
                             (sb, c) -> sb.append((char) c), 
                             StringBuilder::append)
                    .toString();

            writer.write(processedString);
            writer.newLine();
            writer.flush();
            System.out.println("Sent processed: " + processedString);

            // d. Đóng kết nối (tự động nhờ try-with-resources)
        }
    }
}