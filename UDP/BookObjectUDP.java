package UDP;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

class Book implements Serializable {
    private static final long serialVersionUID = 20251107L;
    String id, title, author, isbn, publishDate;
    public Book(String id, String title, String author, String isbn, String publishDate) {
        this.id = id; this.title = title; this.author = author; this.isbn = isbn; this.publishDate = publishDate;
    }
    @Override
    public String toString() {
        return "Book{" + "id=" + id + ", title=" + title + ", author=" + author + ", isbn=" + isbn + ", publishDate=" + publishDate + '}';
    }
}

public class BookObjectUDP {
    public static void main(String[] args) throws Exception {
        // Sử dụng try-with-resources để socket tự động đóng
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddr = InetAddress.getByName("203.162.10.109");
            int port = 2209;
            String studentCode = "B22DCVT034";
            String qCode = "XXT77Sx3";

            // a. Gửi thông điệp khởi tạo
            byte[] sendData = (";" + studentCode + ";" + qCode).getBytes();
            socket.send(new DatagramPacket(sendData, sendData.length, serverAddr, port));

            // b. Nhận đối tượng từ server
            byte[] receiveBuffer = new byte[8192];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            byte[] requestId = Arrays.copyOfRange(receivePacket.getData(), 0, 8);
            System.out.println(new String(requestId)); // In requestId để khớp log

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(receivePacket.getData(), 8, receivePacket.getLength() - 8));
            Book book = (Book) ois.readObject();
            System.out.println(book); // In đối tượng gốc để khớp log

            // c. Chuẩn hóa và in log theo đúng thứ tự yêu cầu
            // Chuẩn hóa title
            String normalizedTitle = Arrays.stream(book.title.trim().split("\\s+"))
                .map(w -> Character.toUpperCase(w.charAt(0)) + w.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
            System.out.println(normalizedTitle);
            
            // Chuẩn hóa author theo logic "Họ, Tên"
            String[] authorParts = book.author.trim().split("\\s+");
            
            // SỬA LỖI Ở ĐÂY: Chuyển "họ" (từ đầu tiên) thành VIẾT HOA TOÀN BỘ
            String ho = authorParts[0].toUpperCase();
            
            String ten = Arrays.stream(authorParts).skip(1)
                .map(w -> Character.toUpperCase(w.charAt(0)) + w.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
            String normalizedAuthor = ho + ", " + ten;
            System.out.println(normalizedAuthor);
            
            // Chuẩn hóa ISBN
            String normalizedIsbn = String.format("%s-%s-%s-%s-%s",
                book.isbn.substring(0, 3), book.isbn.substring(3, 4), book.isbn.substring(4, 6),
                book.isbn.substring(6, 12), book.isbn.substring(12));
            System.out.println(normalizedIsbn);
            
            // Chuẩn hóa ngày
            String[] dateParts = book.publishDate.split("-");
            String normalizedDate = dateParts[1] + "/" + dateParts[0];
            System.out.println(normalizedDate);

            // Gán lại các giá trị đã chuẩn hóa cho đối tượng
            book.title = normalizedTitle;
            book.author = normalizedAuthor;
            book.isbn = normalizedIsbn;
            book.publishDate = normalizedDate;

            // d. Gửi lại đối tượng đã chuẩn hóa
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new ObjectOutputStream(baos).writeObject(book);
            byte[] bookData = baos.toByteArray();
            
            byte[] finalSendData = new byte[8 + bookData.length];
            System.arraycopy(requestId, 0, finalSendData, 0, 8);
            System.arraycopy(bookData, 0, finalSendData, 8, bookData.length);
            
            socket.send(new DatagramPacket(finalSendData, finalSendData.length, serverAddr, port));
            
            System.out.println(book); // In đối tượng sau khi sửa để khớp log
        }
    }
}