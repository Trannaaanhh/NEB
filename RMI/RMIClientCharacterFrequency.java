package RMI; // Interface được yêu cầu nằm trong package RMI

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedHashMap; // Dùng để đếm (giữ thứ tự)
import java.util.Map;


interface CharacterService extends Remote {
    public String requestCharacter(String studentCode, String qCode) throws RemoteException;
    public void submitCharacter(String studentCode, String qCode, String strSubmit) throws RemoteException;
}


public class RMIClientCharacterFrequency {

    public static void main(String[] args) {
        // Thay đổi IP nếu cần. 203.162.10.109 là IP server từ các bài trước.
        String registryHost = "203.162.10.109"; 
        int registryPort = 1099; // Port RMI Registry mặc định
        
        String studentCode = "B22DCVT034"; // Ví dụ MSV
        String qCode = "ge18ZDSd";
        String serviceName = "RMICharacterService";

        try {
            // 1. Kết nối và tra cứu RMI
            Registry registry = LocateRegistry.getRegistry(registryHost, registryPort);
            CharacterService service = (CharacterService) registry.lookup(serviceName);
            System.out.println("Client connected to RMI Service: " + serviceName);

            // a. Triệu gọi phương thức requestCharacter
            String inputString = service.requestCharacter(studentCode, qCode);
            System.out.println("Received: " + inputString);

            // b. Thực hiện đếm tần số, giữ nguyên thứ tự xuất hiện
            
            // Sử dụng LinkedHashMap để đảm bảo thứ tự xuất hiện
            Map<Character, Integer> charCounts = new LinkedHashMap<>();
            for (char c : inputString.toCharArray()) {
                // Đếm tần suất
                charCounts.put(c, charCounts.getOrDefault(c, 0) + 1);
            }

            // Tạo chuỗi kết quả (Ví dụ: "A3B2C1")
            StringBuilder result = new StringBuilder();
            for (Map.Entry<Character, Integer> entry : charCounts.entrySet()) {
                result.append(entry.getKey());    // <Ký tự>
                result.append(entry.getValue());  // <Số lần xuất hiện>
            }
            
            String processedString = result.toString();
            System.out.println("Processed: " + processedString);

            // c. Triệu gọi phương thức submitCharacter
            service.submitCharacter(studentCode, qCode, processedString);
            System.out.println("Processed data submitted.");

            // d. Kết thúc chương trình client
        } catch (Exception e) {
            System.err.println("RMI Client exception:");
            e.printStackTrace();
        }
    }
}