/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package WS;
import CharacterService.CharacterService;
import CharacterService.CharacterService_Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Admin
 */
public class WS_DataService_RTHlurbJ {

    public static void main(String[] args) throws Exception {
        // --- ⚠️ THAY MÃ SINH VIÊN CỦA BẠN VÀO ĐÂY ---
        String studentCode = "B22DCVT034";
        String qCode = "LrRbGCEt";
        // -------------------------------------------

        System.out.println("Starting Client Program...");

        // Khởi tạo dịch vụ web từ các lớp trong package WS
        CharacterService_Service service = new CharacterService_Service();
        CharacterService port = service.getCharacterServicePort();

        // a. Nhận danh sách ký tự (dưới dạng mã Integer)
        System.out.println("1. Requesting character list from server...");
        List<Integer> receivedInts = port.requestCharacter(studentCode, qCode);

        // Chuyển đổi sang List<Character> để in ra cho dễ nhìn
        List<Character> originalChars = receivedInts.stream()
                                                    .map(i -> (char) i.intValue())
                                                    .collect(Collectors.toList());
        System.out.println("=> Received characters: " + originalChars);

        // b. Sắp xếp danh sách theo thứ tự alphabet
        System.out.println("\n2. Sorting the list...");
        // Chỉ cần sort List<Integer> là đủ vì mã ASCII/Unicode cũng theo thứ tự alphabet
        Collections.sort(receivedInts);

        // Chuyển đổi lại để hiển thị kết quả đã sắp xếp
        List<Character> sortedChars = receivedInts.stream()
                                                  .map(i -> (char) i.intValue())
                                                  .collect(Collectors.toList());
        System.out.println("=> Sorted list: " + sortedChars);

        // c. Gửi danh sách đã sắp xếp về server
        System.out.println("\n3. Submitting sorted list back to the server...");
        port.submitCharacterCharArray(studentCode, qCode, receivedInts);
        System.out.println("=> Result sent successfully.");

        // d. Kết thúc chương trình
        System.out.println("\n✅ Program finished.");
    }
}
