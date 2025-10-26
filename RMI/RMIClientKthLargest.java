package RMI; // Interface được yêu cầu nằm trong package RMI

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Giao diện từ xa (định nghĩa theo yêu cầu)
 * Được định nghĩa là package-private để có thể nằm chung file với Client.
 */
interface DataService extends Remote {
    public Object requestData(String studentCode, String qCode) throws RemoteException;
    public void submitData(String studentCode, String qCode, Object data) throws RemoteException;
}

/**
 * Lớp RMI Client public
 * Tên file phải là RMIClientKthLargest.java
 */
public class RMIClientKthLargest {

    public static void main(String[] args) {
        String registryHost = "203.162.10.109"; 
        int registryPort = 1099; 
        
        String studentCode = "B22DCVT034"; // Ví dụ MSV
        String qCode = "MqNsxF1K";
        String serviceName = "RMIDataService";

        try {
            // 1. Kết nối và tra cứu RMI
            Registry registry = LocateRegistry.getRegistry(registryHost, registryPort);
            DataService service = (DataService) registry.lookup(serviceName);
            System.out.println("Client connected to RMI Service: " + serviceName);

            // a. Triệu gọi phương thức requestData
            // Nhận Object, nhưng biết rõ nó là String
            Object response = service.requestData(studentCode, qCode);
            String inputString = (String) response;
            System.out.println("Received: " + inputString);

            // b. Sử dụng thuật toán để tìm phần tử lớn thứ K
            
            // Tách chuỗi (ví dụ: "3, 1, 5, 12, 2, 11; 3")
            String[] parts = inputString.split(";");
            String arrayStr = parts[0].trim();
            int k = Integer.parseInt(parts[1].trim());

            // Chuyển chuỗi mảng thành List<Integer>
            List<Integer> numbers = Arrays.stream(arrayStr.split(","))
                                          .map(s -> s.trim()) // Xóa khoảng trắng
                                          .map(Integer::parseInt) // Chuyển sang Integer
                                          .collect(Collectors.toList());

            // Sắp xếp mảng theo thứ tự giảm dần
            // [3, 1, 5, 12, 2, 11] -> [12, 11, 5, 3, 2, 1]
            Collections.sort(numbers, Collections.reverseOrder());

            // Lấy phần tử lớn thứ K (chỉ số K-1)
            // K=3 -> chỉ số 2 -> 5
            int kthLargest = numbers.get(k - 1);
            System.out.println("Found " + k + "-th largest: " + kthLargest);

            // c. Triệu gọi phương thức submitData
            // Gửi kết quả (int được auto-box thành Integer, là 1 Object)
            service.submitData(studentCode, qCode, kthLargest);
            System.out.println("Result submitted.");

            // d. Kết thúc chương trình client
        } catch (Exception e) {
            System.err.println("RMI Client exception:");
            e.printStackTrace();
        }
    }
}