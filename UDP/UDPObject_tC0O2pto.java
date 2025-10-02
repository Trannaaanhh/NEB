package UDP;

import java.io.*;
import java.net.*;
import java.util.*;

class Employee implements Serializable {
    private static final long serialVersionUID = 20261107L;
    String id, name, hireDate; double salary;
    public Employee(String id, String name, double salary, String hireDate) {
            this.id = id; this.name = name; this.salary = salary; this.hireDate = hireDate;
    }
}
public class UDPObject_tC0O2pto {
    public static void main(String[] args) throws Exception, ClassNotFoundException{
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddr = InetAddress.getByName("203.162.10.109");
        int port = 2209;
        String studentCode = "B22DCVT034"; 
        String qCode = "tC0O2pto";
            // a. Gửi thông điệp khởi tạo
            byte[] sendData = (";" + studentCode + ";" + qCode).getBytes();
            socket.send(new DatagramPacket(sendData, sendData.length, serverAddr, port));
            // b. Nhận đối tượng từ server
            byte[] receiveBuffer = new byte[65535];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);
            byte[] requestId = Arrays.copyOfRange(receivePacket.getData(), 0, 8);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(receivePacket.getData(), 8, receivePacket.getLength() - 8));
            Employee emp = (Employee) ois.readObject();
            // c. Xử lý và gửi lại đối tượng đã chuẩn hóa
            // Chuẩn hóa tên
            String[] words = emp.name.trim().toLowerCase().split("\\s+");
            StringBuilder normalizedName = new StringBuilder();
            for (String w : words) normalizedName.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
            emp.name = normalizedName.toString().trim();
            // Tăng lương
            int sumDigits = 0;
            for (char c : emp.hireDate.substring(0, 4).toCharArray()) sumDigits += Character.getNumericValue(c);
            emp.salary *= (1 + sumDigits / 100.0);
            // Chuẩn hóa ngày
            String[] dateParts = emp.hireDate.split("-");
            emp.hireDate = dateParts[2] + "/" + dateParts[1] + "/" + dateParts[0];
            // Gửi lại server
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new ObjectOutputStream(baos).writeObject(emp);
            byte[] empData = baos.toByteArray();
            byte[] finalSendData = new byte[8 + empData.length];
            System.arraycopy(requestId, 0, finalSendData, 0, 8);
            System.arraycopy(empData, 0, finalSendData, 8, empData.length);
            socket.send(new DatagramPacket(finalSendData, finalSendData.length, serverAddr, port));
    }
}