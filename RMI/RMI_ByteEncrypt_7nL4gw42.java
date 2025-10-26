package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

// Interface không public để có thể nằm chung file với public class
interface ByteService extends Remote {
    byte[] requestData(String studentCode, String qCode) throws RemoteException;
    void submitData(String studentCode, String qCode, byte[] data) throws RemoteException;
}

public class RMI_ByteEncrypt_7nL4gw42 {
    public static void main(String[] args) throws Exception {
        // Khai báo thông tin
        String studentCode = "B22DCVT034"; 
        String qCode = "PMAPUXLF";
        
        // a. Kết nối, tìm và gọi phương thức requestData
        ByteService byteService = (ByteService) LocateRegistry.getRegistry("203.162.10.109", 1099).lookup("RMIByteService");
        byte[] data = byteService.requestData(studentCode, qCode);

        // b. Mã hóa XOR
        byte[] key = "PTIT".getBytes();
        byte[] encryptedData = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            encryptedData[i] = (byte) (data[i] ^ key[i % key.length]);
        }

        // c. Gửi lại dữ liệu đã mã hóa
        byteService.submitData(studentCode, qCode, encryptedData);
        
        // d. Chương trình tự kết thúc
        System.out.println("Hoan thanh!");
    }
}