package RMI;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Giao diện từ xa (định nghĩa theo yêu cầu)
 * Được định nghĩa là package-private (không public) để nằm chung file.
 */
interface ObjectService extends Remote {
    public Serializable requestObject(String studentCode, String qAlias) throws RemoteException;
    public void submitObject(String studentCode, String qAlias, Serializable object) throws RemoteException;
}

/**
 * Lớp ProductX (định nghĩa theo yêu cầu)
 * Được định nghĩa là package-private (không public) để nằm chung file.
 */
class ProductX implements Serializable {
    // Trường dữ liệu: private static final long serialVersionUID = 20171107;
    private static final long serialVersionUID = 20171107;
    
    String id;
    String code;
    String discountCode;
    int discount;

    // Hàm khởi tạo đầy đủ
    public ProductX(String id, String code, String discountCode, int discount) {
        this.id = id;
        this.code = code;
        this.discountCode = discountCode;
        this.discount = discount;
    }
    
    @Override
    public String toString() {
        return "ProductX{" + "id='" + id + '\'' + ", code='" + code + '\'' +
               ", discountCode='" + discountCode + '\'' + ", discount=" + discount + '}';
    }
}

/**
 * Lớp RMI Client public
 * Tên file phải là RMIClientDiscountCode.java
 */
public class RMIClientDiscountCode {

    public static void main(String[] args) {
        String registryHost = "203.162.10.109"; 
        int registryPort = 1099; 
        
        String studentCode = "B22DCVT034"; // Ví dụ MSV
        String qCode = "cMiPqwNg";
        String serviceName = "RMIObjectService";

        try {
            // 1. Kết nối và tra cứu RMI
            Registry registry = LocateRegistry.getRegistry(registryHost, registryPort);
            ObjectService service = (ObjectService) registry.lookup(serviceName);
            System.out.println("Client connected to RMI Service: " + serviceName);

            // 1. Triệu gọi phương thức requestObject
            Serializable response = service.requestObject(studentCode, qCode);
            ProductX product = (ProductX) response;
            System.out.println("Received: " + product);

            // 2. Tính tổng các chữ số trong discountCode
            int sumOfDigits = 0;
            for (char c : product.discountCode.toCharArray()) {
                if (Character.isDigit(c)) {
                    sumOfDigits += Character.getNumericValue(c);
                }
            }
            
            // Cập nhật giá trị khuyến mãi (discount)
            product.discount = sumOfDigits;
            System.out.println("Updated: " + product);

            // 3. Triệu gọi phương thức submitObject
            service.submitObject(studentCode, qCode, product);
            System.out.println("Updated product submitted.");

            // 4. Kết thúc chương trình client
        } catch (Exception e) {
            System.err.println("RMI Client exception:");
            e.printStackTrace();
        }
    }
}