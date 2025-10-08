package RMI;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

// --- Interface và Class BookX không thay đổi ---

interface ObjectService extends Remote {
    Serializable requestObject(String studentCode, String qCode) throws RemoteException;
    void submitObject(String studentCode, String qCode, Serializable object) throws RemoteException;
}

class BookX implements Serializable {
    private static final long serialVersionUID = 20241124L;
    String id, title, author, genre, code;
    int yearPublished;

    public BookX() {}
    public BookX(String id, String title, String author, int yearPublished, String genre) {
        this.id = id; this.title = title; this.author = author;
        this.yearPublished = yearPublished; this.genre = genre;
    }
    public void setCode(String code) { this.code = code; }

    @Override
    public String toString() {
        return "BookX{" + "id=" + id + ", title='" + title + '\'' + ", author='" + author + '\'' +
               ", yearPublished=" + yearPublished + ", genre='" + genre + '\'' + ", code='" + code + '\'' + '}';
    }
}


public class RMI_BookCodeGenerator_61Pl9TCy {

    public static void main(String[] args) throws Exception {
        String studentCode = "B22DCVT034"; // <<< THAY MÃ SINH VIÊN
        String qCode = "61Pl9TCy";

        System.out.println("Dang ket noi den RMI Server...");
        ObjectService service = (ObjectService) LocateRegistry.getRegistry("203.162.10.109", 1099).lookup("RMIObjectService");
        BookX book = (BookX) service.requestObject(studentCode, qCode);
        System.out.println("Da nhan duoc doi tuong: " + book);

        // b. Tạo mã code cho sách
        
        // ======================== CẬP NHẬT LOGIC MỚI TẠI ĐÂY ========================
        String[] authorParts = book.author.trim().split("\\s+");
        
        // Lấy ký tự đầu của từ đầu tiên
        char firstInitial = authorParts[0].charAt(0);
        
        // Lấy từ cuối cùng
        String lastWord = authorParts[authorParts.length - 1];
        // Lấy ký tự CUỐI CÙNG của từ cuối cùng
        char lastInitial = lastWord.charAt(lastWord.length() - 1);
        
        // Ghép và viết hoa để tạo ra "HK"
        String authorCode = ("" + firstInitial + lastInitial).toUpperCase();
        // =======================================================================
        
        String yearCode = String.format("%02d", book.yearPublished % 100);
        String genreCode = String.valueOf(book.genre.length());
        String titleCode = String.valueOf(book.title.length() % 10);
        String finalCode = authorCode + yearCode + genreCode + titleCode;
        
        System.out.println("Ma code duoc tao: " + finalCode);

        // c. Cập nhật và gửi lại đối tượng
        book.setCode(finalCode);
        service.submitObject(studentCode, qCode, book);
        System.out.println("Da gui doi tuong da cap nhat len Server thanh cong!");
    }
}