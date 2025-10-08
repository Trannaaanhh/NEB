package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

// Interface không public để nằm chung file với class
interface CharacterService extends Remote {
    String requestCharacter(String studentCode, String qCode) throws RemoteException;
    void submitCharacter(String studentCode, String qCode, String strSubmit) throws RemoteException;
}

public class RMI_VigenereCipher_G7eVPpI2 {

    public static void main(String[] args) throws Exception {
        // Khai báo thông tin
        String studentCode = "B22DCVT034"; // <<< THAY MÃ SINH VIÊN
        String qCode = "G7eVPpI2";

        System.out.println("Dang ket noi den RMI Server...");
        // a. Kết nối, tìm dịch vụ và gọi phương thức requestCharacter
        CharacterService charService = (CharacterService) LocateRegistry.getRegistry("203.162.10.109", 1099).lookup("RMICharacterService");
        
        // Nhận chuỗi và tách thành "Từ khóa" và "Chuỗi đầu vào"
        String receivedString = charService.requestCharacter(studentCode, qCode);
        String[] parts = receivedString.split(";");
        String key = parts[0];
        String plaintext = parts[1];
        System.out.println("Da nhan duoc: Tu khoa='" + key + "', Chuoi vao='" + plaintext + "'");

        // b. Thực hiện mã hóa Vigenère
        StringBuilder encryptedText = new StringBuilder();
        for (int i = 0; i < plaintext.length(); i++) {
            char plainChar = plaintext.charAt(i);
            char keyChar = key.charAt(i % key.length());

            // SỬA LỖI Ở ĐÂY: Chuyển toàn bộ phép tính về hệ ký tự viết thường (dùng 'a' làm gốc)
            int plainValue = plainChar - 'a';
            int keyValue = keyChar - 'a';
            int encryptedValue = (plainValue + keyValue) % 26;
            char encryptedChar = (char) ('a' + encryptedValue);
            
            encryptedText.append(encryptedChar);
        }
        
        String result = encryptedText.toString();
        System.out.println("Chuoi sau khi ma hoa: " + result);

        // c. Gửi lại chuỗi đã được mã hóa
        charService.submitCharacter(studentCode, qCode, result);
        System.out.println("Da gui ket qua len Server thanh cong!");
        
        // d. Chương trình tự kết thúc
    }
}