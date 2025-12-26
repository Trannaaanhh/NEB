package TCP;
import java.io.*;
import java.net.*;

public class TCPClient_CharCount {

    public static void main(String[] args) throws Exception {
        String sv = "B22DCVT034", qCode = "4g6DiEih";
        String serverIP = "203.162.10.109";
        int port = 2208;
        try (Socket s = new Socket(serverIP, port)) {
            s.setSoTimeout(5000); 
            // Dùng luồng ký tự (BufferedReader/BufferedWriter)
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            // a. Gửi mã sinh viên và mã câu hỏi
            out.write(sv + ";" + qCode);
            out.newLine(); // Bắt buộc xuống dòng với BufferedWriter
            out.flush();
            // b. Nhận chuỗi ngẫu nhiên từ server
            String str = in.readLine();
            System.out.println("Received: " + str);
            // c. Xử lý đếm ký tự xuất hiện > 1 lần
            int[] tanSuat = new int[256];
            for (char c : str.toCharArray()) tanSuat[c]++; // Bước 1: Đếm toàn bộ
            String res = "";
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                // Chỉ lấy chữ/số, xuất hiện > 1 lần, và chưa được thêm vào kết quả
                if (Character.isLetterOrDigit(c) && tanSuat[c] > 1) {
                    res += c + ":" + tanSuat[c] + ",";
                    tanSuat[c] = 0; // Reset về 0 để không lặp lại ký tự này
                }
            }
            // Gửi kết quả lên server
            out.write(res);
            out.newLine();
            out.flush();
            // d. Đóng kết nối (Tự động nhờ try-with-resources)
        }
    }
}
