package RMI; // Interface và Class được yêu cầu nằm trong package RMI

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashSet;
import java.util.Set;

/**
 * Giao diện từ xa (định nghĩa theo yêu cầu)
 * Được định nghĩa là package-private để có thể nằm chung file với Client.
 */
interface ObjectService extends Remote {
    public Serializable requestObject(String studentCode, String qCode) throws RemoteException;
    public void submitObject(String studentCode, String qCode, Serializable object) throws RemoteException;
}

/**
 * Lớp Ticket (định nghĩa theo yêu cầu)
 * Được định nghĩa là package-private để có thể nằm chung file với Client.
 */
class Ticket implements Serializable {
    // Trường dữ liệu: private static final long serialVersionUID = 20241133L;
    private static final long serialVersionUID = 20241133L;

    String id;
    String eventName;
    String saleDate;
    String ticketCode; // Thuộc tính để cập nhật

    // 02 hàm khởi dựng
    public Ticket() {}

    public Ticket(String id, String eventName, String saleDate) {
        this.id = id;
        this.eventName = eventName;
        this.saleDate = saleDate;
    }
    
    @Override
    public String toString() {
        return "Ticket{" + "id=" + id + ", eventName='" + eventName + '\'' +
               ", saleDate='" + saleDate + '\'' + ", ticketCode='" + ticketCode + '\'' + '}';
    }
}

/**
 * Lớp RMI Client public
 * Tên file phải là RMIClientGenerateTicketCode.java
 */
public class RMIClientGenerateTicketCode {

    public static void main(String[] args) {
        String registryHost = "203.162.10.109"; 
        int registryPort = 1099; 
        
        String studentCode = "B22DCVT034"; // Ví dụ MSV
        String qCode = "aiePJDDl";
        String serviceName = "RMIObjectService";

        try {
            // 1. Kết nối và tra cứu RMI
            Registry registry = LocateRegistry.getRegistry(registryHost, registryPort);
            ObjectService service = (ObjectService) registry.lookup(serviceName);
            System.out.println("Client connected to RMI Service: " + serviceName);

            // a. Triệu gọi phương thức requestObject
            Serializable response = service.requestObject(studentCode, qCode);
            Ticket ticket = (Ticket) response;
            System.out.println("Received: " + ticket);

            // b. Tạo mã ticketCode
            StringBuilder codeBuilder = new StringBuilder();

            // • Bắt đầu: Chữ cái đầu và cuối của eventName, viết hoa.
            String event = ticket.eventName;
            codeBuilder.append(Character.toUpperCase(event.charAt(0)));
            codeBuilder.append(Character.toUpperCase(event.charAt(event.length() - 1)));

            // • Thêm ngày và tháng từ saleDate (theo định dạng "MMdd")
            // Giả sử saleDate có định dạng "dd/MM/yyyy"
            String[] dateParts = ticket.saleDate.split("/");
            String day = dateParts[0];   // "15"
            String month = dateParts[1]; // "06"
            codeBuilder.append(month);
            codeBuilder.append(day);

            // • Kết thúc: Chữ số lớn nhất và nhỏ nhất không xuất hiện trong saleDate
            Set<Integer> digitsInDate = new HashSet<>();
            for (char c : ticket.saleDate.toCharArray()) {
                if (Character.isDigit(c)) {
                    digitsInDate.add(Character.getNumericValue(c));
                }
            }

            int smallestMissing = -1;
            int largestMissing = -1;

            // Tìm số nhỏ nhất không xuất hiện
            for (int i = 0; i <= 9; i++) {
                if (!digitsInDate.contains(i)) {
                    smallestMissing = i;
                    break;
                }
            }
            
            // Tìm số lớn nhất không xuất hiện
            for (int i = 9; i >= 0; i--) {
                if (!digitsInDate.contains(i)) {
                    largestMissing = i;
                    break;
                }
            }
            
            codeBuilder.append(largestMissing);
            codeBuilder.append(smallestMissing);

            // c. Cập nhật giá trị ticketCode
            ticket.ticketCode = codeBuilder.toString();
            System.out.println("Generated: " + ticket);

            // d. Triệu gọi phương thức submitObject
            service.submitObject(studentCode, qCode, ticket);
            System.out.println("Updated ticket submitted.");

            // e. Kết thúc chương trình client
        } catch (Exception e) {
            System.err.println("RMI Client exception:");
            e.printStackTrace();
        }
    }
}