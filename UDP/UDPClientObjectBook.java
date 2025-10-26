package UDP; // Hoặc tên package của bạn

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Lớp Book (không public) được định nghĩa trong cùng file.
 * Tên đầy đủ là UDP.Book (do package là UDP)
 */
class Book implements Serializable {
    private static final long serialVersionUID = 20251107L;
    String id, title, author, isbn, publishDate;
    public Book(String id, String title, String author, String isbn, String publishDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publishDate = publishDate;
    }

    @Override
    public String toString() {
        return "Book{" + "id=" + id + ", title=" + title + ", author=" + author + ", isbn=" + isbn + ", publishDate=" + publishDate + '}';
    }
}

/**
 * Lớp Client public
 */
public class UDPClientObjectBook {
    
    public static void main(String[] args) throws Exception {
        String serverIP = "203.162.10.109";
        int serverPort = 2209;
        
        String studentCode = "B22DCVT034";
        String qCode = "vlgXR7mH";
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddr = InetAddress.getByName(serverIP);

            // a. Gửi thông điệp là một chuỗi “;studentCode;qCode”
            String request = ";" + studentCode + ";" + qCode;
            byte[] sendData = request.getBytes();
            socket.send(new DatagramPacket(sendData, sendData.length, serverAddr, serverPort));
            System.out.println("Sent request: " + request);

            // b. Nhận thông điệp (requestId + đối tượng Book)
            byte[] receiveBuffer = new byte[8192];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            // Tách 8 byte đầu (requestId)
            byte[] requestId = Arrays.copyOfRange(receivePacket.getData(), 0, 8);
            System.out.println("Received requestId: " + new String(requestId));

            // Đọc đối tượng từ các byte còn lại
            ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData(), 8, receivePacket.getLength() - 8);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Book receivedBook = (Book) ois.readObject();
            System.out.println("Received Book: " + receivedBook);

            // c. Thực hiện chuẩn hóa
            
            // Chuẩn hóa title: viết hoa chữ cái đầu của mỗi từ
            receivedBook.title = Arrays.stream(receivedBook.title.trim().split("\\s+"))
                    .map(w -> Character.toUpperCase(w.charAt(0)) + w.substring(1).toLowerCase())
                    .collect(Collectors.joining(" "));

            // Chuẩn hóa author: "HỌ, Tên" (Họ viết hoa, Tên viết hoa chữ cái đầu)
            String[] authorParts = receivedBook.author.trim().split("\\s+");
            String ho = authorParts[0].toUpperCase();
            String ten = Arrays.stream(authorParts).skip(1)
                    .map(w -> Character.toUpperCase(w.charAt(0)) + w.substring(1).toLowerCase())
                    .collect(Collectors.joining(" "));
            receivedBook.author = ho + ", " + ten;

            // Chuẩn hóa mã ISBN (13 chữ số)
            receivedBook.isbn = String.format("%s-%s-%s-%s-%s",
                    receivedBook.isbn.substring(0, 3),
                    receivedBook.isbn.substring(3, 4),
                    receivedBook.isbn.substring(4, 6),
                    receivedBook.isbn.substring(6, 12),
                    receivedBook.isbn.substring(12));

            // Chuyển đổi publishDate từ yyyy-mm-dd sang mm/yyyy
            String[] dateParts = receivedBook.publishDate.split("-");
            receivedBook.publishDate = dateParts[1] + "/" + dateParts[0];
            
            System.out.println("Fixed Book: " + receivedBook);

            // d. Gửi lại đối tượng đã được chuẩn hóa (requestId + đối tượng)
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(receivedBook);
            oos.flush();
            byte[] bookData = baos.toByteArray();

            // Ghép requestId và data
            byte[] finalSendData = new byte[8 + bookData.length];
            System.arraycopy(requestId, 0, finalSendData, 0, 8);
            System.arraycopy(bookData, 0, finalSendData, 8, bookData.length);

            socket.send(new DatagramPacket(finalSendData, finalSendData.length, serverAddr, serverPort));
            System.out.println("Sent fixed book.");

            // Đóng socket (tự động nhờ try-with-resources)
        }
    }
}