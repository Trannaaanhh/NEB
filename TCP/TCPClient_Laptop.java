package TCP;
import java.io.*; 
import java.net.*;

// Class Laptop đưa lên đầu, nén gọn dòng code
class Laptop implements Serializable {
    private static final long serialVersionUID = 20150711L;
    private int id, quantity; private String code, name; // Gộp khai báo
    public Laptop(int id, String code, String name, int quantity) {
        this.id = id; this.code = code; this.name = name; this.quantity = quantity; // Gộp gán
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String toString() { return id + " " + code + " " + name + " " + quantity; }
}

public class TCPClient_Laptop {
    public static void main(String[] args) throws Exception {
        String sv = "B22DCVT034", qCode = "kxFJkZzl";
        try (Socket s = new Socket("203.162.10.109", 2209)) {
            s.setSoTimeout(5000);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            // a. Gửi request
            oos.writeObject(sv + ";" + qCode); oos.flush();
            // b. Nhận object
            Laptop lp = (Laptop) ois.readObject();
            System.out.println("Received: " + lp);
            // c. Sửa thông tin
            // Sửa số lượng (đảo ngược)
            lp.setQuantity(Integer.parseInt(new StringBuilder(String.valueOf(lp.getQuantity())).reverse().toString()));
            // Sửa tên (đảo đầu cuối)
            String[] arr = lp.getName().trim().split("\\s+");
            if (arr.length > 1) {
                String tmp = arr[0]; arr[0] = arr[arr.length-1]; arr[arr.length-1] = tmp;
                lp.setName(String.join(" ", arr));
            }
            // Gửi lại và đóng
            oos.writeObject(lp); oos.flush();
            System.out.println("Sent: " + lp);
        }
    }
}