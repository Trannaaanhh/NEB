package UDP;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Client UDP gửi mã sinh viên + qCode, nhận 1 Book (cùng định dạng với server),
 * chuẩn hoá trường và gửi trả lại (đính kèm 8 byte requestId phía trước).
 *
 * Sửa/hoàn thiện bởi ChatGPT — fix các hàm chuẩn hoá và bảo đảm xử lý gói nhận.
 */
class Book implements Serializable {
    // phải khớp với server
    private static final long serialVersionUID = 20251107L;

    // để client dễ truy cập/ghi sửa trực tiếp
    public String id;
    public String title;
    public String author;
    public String isbn;
    public String publishDate;

    public Book(String id, String title, String author, String isbn, String publishDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publishDate = publishDate;
    }

    @Override
    public String toString() {
        return "Book Info:\n" +
               "\tID: " + id + "\n" +
               "\tTitle: " + title + "\n" +
               "\tAuthor: " + author + "\n" +
               "\tISBN: " + isbn + "\n" +
               "\tPublish Date: " + publishDate;
    }
}

public class UDPObject_XXT77Sx3 {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            InetAddress host = InetAddress.getByName("203.162.10.109");
            int port = 2209;

            // --- Thông tin sinh viên và mã câu hỏi ---
            String studentCode = "B22DCVT034"; // thay nếu cần
            String qCode = "XXT77Sx3";
            String initialMessage = ";" + studentCode + ";" + qCode;
            byte[] sendData = initialMessage.getBytes("UTF-8");
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, host, port);
            socket.send(sendPacket);
            System.out.println("Đã gửi mã sinh viên và mã câu hỏi.");

            // --- Nhận gói chứa requestId(8 bytes) + serialized Book ---
            byte[] receiveBuffer = new byte[16384]; // lớn hơn để an toàn
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);
            int receivedLen = receivePacket.getLength();
            if (receivedLen <= 8) {
                System.err.println("Gói nhận quá ngắn: không có đủ 8 byte requestId.");
                return;
            }

            // Sao chép 8 byte requestId chính xác (0..7)
            byte[] requestIdBytes = Arrays.copyOfRange(receivePacket.getData(), 0, 8);

            // Phần dữ liệu object bắt từ offset 8, độ dài = receivedLen - 8
            ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData(), 8, receivedLen - 8);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object obj = ois.readObject();
            if (!(obj instanceof Book)) {
                System.err.println("Không nhận được Book. Lớp nhận được: " + obj.getClass().getName());
                return;
            }
            Book receivedBook = (Book) obj;
            System.out.println("\nĐã nhận đối tượng Book từ server:\n" + receivedBook);

            // --- Chuẩn hoá ---
            receivedBook.title = normalizeTitle(receivedBook.title);
            receivedBook.author = normalizeAuthor(receivedBook.author);
            receivedBook.isbn = normalizeIsbn(receivedBook.isbn);
            receivedBook.publishDate = normalizeDate(receivedBook.publishDate);
            System.out.println("\nĐã chuẩn hóa thông tin.");

            // --- Serialize lại Book và gửi kèm requestId (8 bytes trước) ---
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(receivedBook);
            oos.flush();
            byte[] bookBytes = baos.toByteArray();

            byte[] finalSendData = new byte[8 + bookBytes.length];
            System.arraycopy(requestIdBytes, 0, finalSendData, 0, 8);
            System.arraycopy(bookBytes, 0, finalSendData, 8, bookBytes.length);

            DatagramPacket sendBackPacket = new DatagramPacket(finalSendData, finalSendData.length, host, port);
            socket.send(sendBackPacket);
            System.out.println("Đã gửi đối tượng Book đã chuẩn hóa về server.");
            System.out.println("\n--- Thông tin cuối cùng được gửi đi ---");
            System.out.println(receivedBook);

        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("\nHoàn thành. Đã đóng socket.");
            }
        }
    }

    // Viết hoa chữ cái đầu của mỗi từ, phần còn lại chữ thường
    private static String normalizeTitle(String s) {
        if (s == null || s.trim().isEmpty()) return "";
        String[] words = s.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.length() == 0) continue;
            sb.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) sb.append(word.substring(1));
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Chuẩn hóa author theo định dạng "HỌ, Tên"
     * - Lấy từ cuối cùng làm HỌ (viết hoa toàn bộ)
     * - Phần còn lại là Tên (giữ thứ tự ban đầu), mỗi từ viết hoa chữ cái đầu
     * Ví dụ: "Geaeog Nuakmoz Hwqeh Iqvwpj Qrgtrdzy Ojefxghxo"
     * -> "OJEFXGHXO, Geaeog Nuakmoz Hwqeh Iqvwpj Qrgtrdzy"
     */
    private static String normalizeAuthor(String s) {
        if (s == null || s.trim().isEmpty()) return "";
        // loại bỏ khoảng trắng thừa và dấu phẩy lạ
        String cleaned = s.trim().replaceAll("[,;]+", " ").replaceAll("\\s+", " ");
        String[] parts = cleaned.split("\\s+");
        if (parts.length < 2) {
            // không có đủ họ/tên -> chỉ chuẩn hoá như title
            return normalizeTitle(s);
        }
        String lastName = parts[parts.length - 1].toUpperCase(Locale.ROOT); // HỌ viết hoa toàn bộ
        StringBuilder firstNameBuilder = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (parts[i].length() == 0) continue;
            String w = parts[i].toLowerCase();
            firstNameBuilder.append(Character.toUpperCase(w.charAt(0)));
            if (w.length() > 1) firstNameBuilder.append(w.substring(1));
            if (i < parts.length - 2) firstNameBuilder.append(" ");
        }
        return lastName + ", " + firstNameBuilder.toString().trim();
    }

    // Format ISBN: nhóm 3-1-2-6-1 (cho ISBN13)
    private static String normalizeIsbn(String s) {
        if (s == null) return "";
        // giữ chỉ chữ số
        String digits = s.replaceAll("\\D+", "");
        if (digits.length() != 13) return s; // không phải 13 chữ số -> trả về nguyên bản
        return digits.substring(0, 3) + "-" +
               digits.substring(3, 4) + "-" +
               digits.substring(4, 6) + "-" +
               digits.substring(6, 12) + "-" +
               digits.substring(12, 13);
    }

    // yyyy-mm-dd -> mm/yyyy
    private static String normalizeDate(String s) {
        if (s == null) return "";
        String[] parts = s.trim().split("-");
        if (parts.length < 2) return s;
        String year = parts[0];
        String month = parts[1];
        if (month.length() == 1) month = "0" + month;
        return month + "/" + year;
    }
}
