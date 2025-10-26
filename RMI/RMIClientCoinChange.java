package RMI;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

/**
 * Giao diện từ xa (định nghĩa theo yêu cầu)
 * Được định nghĩa là package-private (không public) để nằm chung file.
 */
interface DataService extends Remote {
    public Object requestData(String studentCode, String qCode) throws RemoteException;
    public void submitData(String studentCode, String qCode, Object data) throws RemoteException;
}

/**
 * Lớp RMI Client public
 * Tên file phải là RMIClientCoinChange.java
 */
public class RMIClientCoinChange {

    public static void main(String[] args) {
        String registryHost = "203.162.10.109"; 
        int registryPort = 1099; 
        
        String studentCode = "B22DCVT034"; // Ví dụ MSV
        String qCode = "UPelcTzM";
        String serviceName = "RMIDataService";

        try {
            // 1. Kết nối và tra cứu RMI
            Registry registry = LocateRegistry.getRegistry(registryHost, registryPort);
            DataService service = (DataService) registry.lookup(serviceName);
            System.out.println("Client connected to RMI Service: " + serviceName);

            // a. Triệu gọi phương thức requestData để nhận số tiền
            Object response = service.requestData(studentCode, qCode);
            int amount = (Integer) response;
            System.out.println("Received amount: " + amount);

            // b. Sử dụng thuật toán xếp đồng xu (Tham lam)
            int[] denominations = {10, 5, 2, 1}; // Mệnh giá (sắp xếp giảm dần)
            int coinCount = 0;
            int remainingAmount = amount;
            List<String> coinsUsed = new ArrayList<>();

            for (int coin : denominations) {
                // Lấy số lượng đồng xu của mệnh giá hiện tại
                int numCoins = remainingAmount / coin; 
                
                if (numCoins > 0) {
                    coinCount += numCoins; // Tăng tổng số đồng xu
                    remainingAmount %= coin; // Cập nhật số tiền còn lại
                    
                    // Thêm các đồng xu đã dùng vào danh sách
                    for (int i = 0; i < numCoins; i++) {
                        coinsUsed.add(String.valueOf(coin));
                    }
                }
            }
            
            // c. Triệu gọi phương thức submitData
            String coinListStr = String.join(",", coinsUsed);
            
            // *** FIX LỖI Ở ĐÂY ***
            // Thêm một khoảng trắng " " sau dấu ";" để khớp với log.
            String result = coinCount + "; " + coinListStr;

            System.out.println("Submitting: " + result);
            service.submitData(studentCode, qCode, result);
            System.out.println("Result submitted.");

            // d. Kết thúc chương trình client
        } catch (Exception e) {
            System.err.println("RMI Client exception:");
            e.printStackTrace();
        }
    }
}