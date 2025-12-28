package UDP;
import java.io.*; import java.net.*;

// Class Employee (Đặt class này chung file, bỏ public)
class Employee implements Serializable {
    private static final long serialVersionUID = 20261107L; // Copy chính xác số này
    private String id, name, hireDate; 
    private double salary;

    public Employee(String id, String name, double salary, String hireDate) {
        this.id = id; this.name = name; this.salary = salary; this.hireDate = hireDate;
    }
    // Getters / Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }
}

public class UDPClient_Employee {
    public static void main(String[] args) throws Exception {
        String sv = "B22DCVT034", qCode = "dNZiWm1N";
        InetAddress ip = InetAddress.getByName("203.162.10.109"); int port = 2209;
        try (DatagramSocket s = new DatagramSocket()) {
            // a. Gửi request ";sv;qCode"
            byte[] req = (";" + sv + ";" + qCode).getBytes();
            s.send(new DatagramPacket(req, req.length, ip, port));
            // b. Nhận thông điệp (8 byte RequestId + Object)
            byte[] buf = new byte[1024];
            DatagramPacket p = new DatagramPacket(buf, buf.length);
            s.receive(p);
            // Tách requestId (8 byte đầu)
            String reqId = new String(buf, 0, 8);         
            // Deserialize Object (Bỏ qua 8 byte đầu)
            ByteArrayInputStream bais = new ByteArrayInputStream(buf, 8, p.getLength() - 8);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Employee e = (Employee) ois.readObject();
            System.out.println("Received: " + e.getName() + " | " + e.getSalary());
            // c. Thực hiện xử lý
            // 1. Chuẩn hóa tên (Viết hoa chữ cái đầu)
            String[] words = e.getName().trim().toLowerCase().split("\\s+");
            String finalName = "";
            for(String w : words) finalName += Character.toUpperCase(w.charAt(0)) + w.substring(1) + " ";
            e.setName(finalName.trim());
            // 2. Tăng lương (x% với x = tổng chữ số năm). Lấy năm từ hireDate (yyyy-mm-dd)
            String[] dParts = e.getHireDate().split("-"); // [0]:yyyy, [1]:mm, [2]:dd
            int year = Integer.parseInt(dParts[0]); 
            int x = 0;
            while(year > 0) { x += year % 10; year /= 10; } // Tính tổng chữ số
            e.setSalary(e.getSalary() * (1 + x / 100.0));
            // 3. Format ngày: yyyy-mm-dd -> dd/mm/yyyy
            e.setHireDate(dParts[2] + "/" + dParts[1] + "/" + dParts[0]);
            // Gửi lại: Ghi 8 byte reqId trước, sau đó ghi Object
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(reqId.getBytes()); // Ghi requestId
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(e); // Ghi object
            oos.flush();
            // Gửi gói tin
            byte[] sendData = baos.toByteArray();
            s.send(new DatagramPacket(sendData, sendData.length, ip, port));
            System.out.println("Sent fixed object!");
        }
    }
}