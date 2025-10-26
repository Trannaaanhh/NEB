package TCP; // Hoặc tên package của bạn trong NetBeans

import java.io.*;
import java.net.*;
import java.util.*;

class Laptop implements Serializable {
    private static final long serialVersionUID = 20150711L; 
    int id; String code; String name; int quantity;
    public Laptop(int id, String code, String name, int quantity) {
        this.id = id; this.code = code; this.name = name; this.quantity = quantity;
    }
    @Override
    public String toString() {
        return "Laptop{" + "id=" + id + ", code=" + code + ", name=" + name + ", quantity=" + quantity + '}';
    }
}
// Lớp public chính, tên file phải là TCPClientObject.java
public class TCPClientObject {
    public static void main(String[] args) throws Exception {
        String serverIP = "203.162.10.109";
        int serverPort = 2209;
        
        String studentCode = "B22DCVT034"; // Ví dụ MSV
        String qCode = "zZIYrrw0";
        int timeout = 5000; // 5 giây

        try (Socket socket = new Socket()) {
            
            socket.connect(new InetSocketAddress(serverIP, serverPort), timeout);
            socket.setSoTimeout(timeout);

            // QUAN TRỌNG: Khởi tạo ObjectOutputStream TRƯỚC ObjectInputStream
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            // 1. Gửi đối tượng là chuỗi "studentCode;qCode"
            String request = studentCode + ";" + qCode;
            oos.writeObject(request);
            oos.flush();
            System.out.println("Sent request: " + request);

            // 2. Nhận một đối tượng Laptop từ server
            Laptop receivedLaptop = (Laptop) ois.readObject();
            System.out.println("Received: " + receivedLaptop);

            // 3. Sửa các thông tin sai
            
            // a) Sửa tên: Đảo từ đầu tiên và từ cuối cùng
            String[] nameParts = receivedLaptop.name.trim().split("\\s+");
            if (nameParts.length > 1) {
                String firstWord = nameParts[0];
                String lastWord = nameParts[nameParts.length - 1];
                
                nameParts[0] = lastWord;
                nameParts[nameParts.length - 1] = firstWord;
                
                receivedLaptop.name = String.join(" ", nameParts);
            }

            // b) Sửa số lượng: Đảo ngược số
            String quantityStr = String.valueOf(receivedLaptop.quantity);
            String reversedQuantityStr = new StringBuilder(quantityStr).reverse().toString();
            receivedLaptop.quantity = Integer.parseInt(reversedQuantityStr);

            System.out.println("Fixed: " + receivedLaptop);

            // Gửi đối tượng vừa được sửa sai lên server
            oos.writeObject(receivedLaptop);
            oos.flush();

            // 4. Đóng socket (tự động nhờ try-with-resources)
        }
    }
}