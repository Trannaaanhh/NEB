package WS; // Tên package chứa lớp main của bạn

import java.util.Comparator;
import java.util.List;

public class WSClientSortStringLength {

    public static void main(String[] args) {
        String studentCode = "B22DCVT034"; // Thay bằng MSV của bạn
        String qCode = "8mSkFN5d";

        try {
            // 1. Khởi tạo dịch vụ
            
            // FIX 2: Sửa tên lớp Service (thêm dấu "_" và bỏ 1 chữ "S")
            CharacterService_Service service = new CharacterService_Service(); 
            
            // Dòng này đã đúng
            CharacterService port = service.getCharacterServicePort();

            // a. Triệu gọi phương thức requestStringArray
            List<String> stringList = port.requestStringArray(studentCode, qCode);
            System.out.println("Received: " + stringList);

            // b. Sắp xếp mảng theo độ dài của từ
            stringList.sort(Comparator.comparingInt(String::length));

            System.out.println("Sorted: " + stringList);

            // c. Triệu gọi phương thức submitCharacterStringArray
            port.submitCharacterStringArray(studentCode, qCode, stringList);
            System.out.println("Submitted sorted list.");

            // d. Kết thúc chương trình
        } catch (Exception e) {
            System.err.println("Web Service client exception:");
            e.printStackTrace();
        }
    }
}