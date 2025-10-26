package RMI; // Interface được yêu cầu nằm trong package RMI

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Base64; // Import thư viện Base64
import java.util.Arrays; // Dùng để in mảng (nếu cần gỡ lỗi)

/**
 * Giao diện từ xa (định nghĩa theo yêu cầu)
 * Được định nghĩa là package-private để có thể nằm chung file với Client.
 */
interface ByteService extends Remote {
    public byte[] requestData(String studentCode, String qCode) throws RemoteException;
    public void submitData(String studentCode, String qCode, byte[] data) throws RemoteException;
}

/**
 * Lớp RMI Client public
 * Tên file phải là RMIClientBase64Decode.java
 */
public class RMIClientBase64Decode {

    public static void main(String[] args) {
        // Thay đổi IP nếu cần. 203.162.10.109 là IP server từ các bài trước.
        String registryHost = "203.162.10.109"; 
        int registryPort = 1099; // Port RMI Registry mặc định
        
        String studentCode = "B22DCVT034"; // Ví dụ MSV
        String qCode = "PMAPUXLF";
        String serviceName = "RMIByteService";

        try {
            // 1. Kết nối tới RMI Registry
            Registry registry = LocateRegistry.getRegistry(registryHost, registryPort);

            // 2. Tra cứu (lookup) đối tượng từ xa
            ByteService service = (ByteService) registry.lookup(serviceName);
            System.out.println("Client connected to RMI Service: " + serviceName);

            // a. Triệu gọi phương thức requestData để nhận mảng byte (Base64)
            byte[] base64Data = service.requestData(studentCode, qCode);
            System.out.println("Received Base64 data (length): " + base64Data.length);
            // System.out.println("Received array: " + Arrays.toString(base64Data));


            // b. Thực hiện giải mã dữ liệu nhận được từ định dạng Base64
            byte[] decodedData = Base64.getDecoder().decode(base64Data);
            
            System.out.println("Decoded data (length): " + decodedData.length);
            // System.out.println("Decoded array: " + Arrays.toString(decodedData));

            // c. Triệu gọi phương thức submitData để gửi mảng dữ liệu gốc
            service.submitData(studentCode, qCode, decodedData);
            System.out.println("Decoded data submitted.");

            // d. Kết thúc chương trình client
        } catch (Exception e) {
            System.err.println("RMI Client exception:");
            e.printStackTrace();
        }
    }
}