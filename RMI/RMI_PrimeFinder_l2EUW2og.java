package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

// Interface không public để nằm chung file với class
interface DataService extends Remote {
    Object requestData(String studentCode, String qCode) throws RemoteException;
    void submitData(String studentCode, String qCode, Object data) throws RemoteException;
}

public class RMI_PrimeFinder_l2EUW2og {

    /**
     * Hàm kiểm tra một số có phải là số nguyên tố hay không.
     * @param n Số cần kiểm tra.
     * @return true nếu n là số nguyên tố, ngược lại là false.
     */
    private static boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        // Chỉ cần kiểm tra ước đến căn bậc hai của n
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        // Khai báo thông tin
        String studentCode = "B22DCVT034"; // <<< THAY MÃ SINH VIÊN
        String qCode = "l2EUW2og";
        
        System.out.println("Dang ket noi den RMI Server...");
        // a. Kết nối, tìm dịch vụ và gọi phương thức requestData
        DataService dataService = (DataService) LocateRegistry.getRegistry("203.162.10.109", 1099).lookup("RMIDataService");
        
        // Nhận Object và ép kiểu về Integer
        int N = (Integer) dataService.requestData(studentCode, qCode);
        System.out.println("Da nhan duoc so N = " + N);

        // b. Tìm tất cả các số nguyên tố từ 1 đến N
        List<Integer> primeNumbers = new ArrayList<>();
        for (int i = 2; i <= N; i++) {
            if (isPrime(i)) {
                primeNumbers.add(i);
            }
        }
        System.out.println("Da tim thay cac so nguyen to: " + primeNumbers.toString());

        // c. Gửi lại danh sách List<Integer> đã tìm được
        dataService.submitData(studentCode, qCode, primeNumbers);
        System.out.println("Da gui ket qua len Server thanh cong!");
        
        // d. Chương trình tự kết thúc
    }
}