package RMI; // Interface được yêu cầu nằm trong package RMI

import java.io.ByteArrayOutputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
 * Tên file phải là RMIClientEvenOddSort.java
 */
public class RMIClientEvenOddSort {

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

            // a. Triệu gọi phương thức requestData
            byte[] data = service.requestData(studentCode, qCode);
            System.out.println("Received data (length): " + data.length);
            // System.out.println("Original: " + Arrays.toString(data));

            // b. Phân chia mảng byte[] chẵn-lẻ
            ByteArrayOutputStream evenBytes = new ByteArrayOutputStream();
            ByteArrayOutputStream oddBytes = new ByteArrayOutputStream();

            for (byte b : data) {
                if (b % 2 == 0) {
                    evenBytes.write(b); // Byte chẵn
                } else {
                    oddBytes.write(b); // Byte lẻ
                }
            }

            // Gộp 2 mảng: chẵn trước, lẻ sau
            evenBytes.write(oddBytes.toByteArray());
            byte[] processedData = evenBytes.toByteArray();
            
            System.out.println("Processed data (length): " + processedData.length);
            // System.out.println("Processed: " + Arrays.toString(processedData));

            // c. Triệu gọi phương thức submitData
            service.submitData(studentCode, qCode, processedData);
            System.out.println("Processed data submitted.");

            // d. Kết thúc chương trình client
        } catch (Exception e) {
            System.err.println("RMI Client exception:");
            e.printStackTrace();
        }
    }
}