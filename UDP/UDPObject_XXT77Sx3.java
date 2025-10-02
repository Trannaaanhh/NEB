package UDP;

import java.io.*;
import java.net.*;
import java.util.*;

class Book implements Serializable {
    private static final long serialVersionUID = 20251107L;
    String id, title, author, isbn, publishDate;
    public Book(String id, String title, String author, String isbn, String publishDate) {
        this.id = id; this.title = title; this.author = author; this.isbn = isbn; this.publishDate = publishDate;
    }
}

public class UDPObject_XXT77Sx3 {
    private static String capitalizeWords(String text) {
        String[] words = text.trim().toLowerCase().split("\\s+");
        StringJoiner sj = new StringJoiner(" ");
        for (String w : words) {if (!w.isEmpty()) {sj.add(Character.toUpperCase(w.charAt(0)) + w.substring(1));}}
        return sj.toString();
    }

    public static void main(String[] args) throws Exception, ClassNotFoundException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddr = InetAddress.getByName("203.162.10.109"); 
        int port = 2209;
        String studentCode = "B22DCVT034";
        String qCode = "XXT77Sx3";
        // a. Gửi thông điệp khởi tạo chứa mã sinh viên và mã câu hỏi
        byte[] sendData = (";" + studentCode + ";" + qCode).getBytes();
        socket.send(new DatagramPacket(sendData, sendData.length, serverAddr, port));
        // b. Nhận đối tượng Book từ server
        byte[] receiveBuffer = new byte[65535];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);
        byte[] requestId = Arrays.copyOfRange(receivePacket.getData(), 0, 8);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(receivePacket.getData(), 8, receivePacket.getLength() - 8));
        Book book = (Book) ois.readObject();
        // c. Chuẩn hóa thông tin của đối tượng Book
        // Chuẩn hóa title
        book.title = capitalizeWords(book.title);
        // Chuẩn hóa author theo định dạng "Họ, Tên"
        String[] authorWords = book.author.trim().split("\\s+");
        if (authorWords.length > 1) {
            String lastName = capitalizeWords(authorWords[authorWords.length - 1]);
            StringJoiner firstName = new StringJoiner(" ");
            for (int i = 0; i < authorWords.length - 1; i++) {firstName.add(authorWords[i]);}
            book.author = lastName + ", " + capitalizeWords(firstName.toString());
        }
        // Chuẩn hóa mã ISBN (XXX-X-XX-XXXXXX-X)
        if (book.isbn != null && book.isbn.length() == 13) {book.isbn = book.isbn.substring(0, 3) + "-" + book.isbn.substring(3, 4) + "-" + book.isbn.substring(4, 6) + "-" + book.isbn.substring(6, 12) + "-" + book.isbn.substring(12, 13);        }
        // Chuẩn hóa publishDate (từ yyyy-mm-dd sang mm/yyyy)
        String[] dateParts = book.publishDate.split("-");
        if (dateParts.length == 3) {book.publishDate = dateParts[1] + "/" + dateParts[0];}
        // d. Gửi lại đối tượng đã được chuẩn hóa về server
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(book);
        byte[] bookData = baos.toByteArray();
        byte[] finalSendData = new byte[8 + bookData.length];
        System.arraycopy(requestId, 0, finalSendData, 0, 8);
        System.arraycopy(bookData, 0, finalSendData, 8, bookData.length);
        socket.send(new DatagramPacket(finalSendData, finalSendData.length, serverAddr, port));
        System.out.println("Client đã hoàn thành và đóng kết nối.");
    }
}
