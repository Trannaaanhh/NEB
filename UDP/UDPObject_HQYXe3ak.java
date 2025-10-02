package UDP;

import java.io.*;
import java.net.*;
import java.util.*;

class Customer implements Serializable {
    private static final long serialVersionUID = 20151107L;
    String id, code, name, dayOfBirth, userName;
    public Customer(String id, String code, String name, String dayOfBirth, String userName) {
        this.id = id; this.code = code; this.name = name; this.dayOfBirth = dayOfBirth; this.userName = userName;
    }
}
public class UDPObject_HQYXe3ak {
    public static void main(String[] args) throws Exception, ClassNotFoundException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddr = InetAddress.getByName("203.162.10.109"); int port = 2209;
        String studentCode = "B22DCVT034"; 
        String qCode = "HQYXe3ak";
        // b1. Gửi thông điệp khởi tạo chứa mã sinh viên và mã câu hỏi
        byte[] sendData = (";" + studentCode + ";" + qCode).getBytes();
        socket.send(new DatagramPacket(sendData, sendData.length, serverAddr, port));

        // b2. Nhận đối tượng Customer từ server
        byte[] receiveBuffer = new byte[65535];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);
        byte[] requestId = Arrays.copyOfRange(receivePacket.getData(), 0, 8);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(receivePacket.getData(), 8, receivePacket.getLength() - 8));
        Customer customer = (Customer) ois.readObject();
        // b3. Chuẩn hóa thông tin của đối tượng Customer
        String[] nameParts = customer.name.trim().toLowerCase().split("\\s+");
        // a. Chuẩn hóa tên theo định dạng "HỌ, Tên"
        if (nameParts.length > 0) {
            String lastName = nameParts[nameParts.length - 1].toUpperCase();
            StringJoiner firstName = new StringJoiner(" ");
            for (int i = 0; i < nameParts.length - 1; i++) {
                String part = nameParts[i];
                firstName.add(Character.toUpperCase(part.charAt(0)) + part.substring(1));
            }
            customer.name = lastName + ", " + firstName.toString();
        }
        // c. Tạo tài khoản khách hàng
        if (nameParts.length > 0) {
            StringBuilder usernameBuilder = new StringBuilder();
            for (int i = 0; i < nameParts.length - 1; i++) {
                usernameBuilder.append(nameParts[i].charAt(0));
            }
            usernameBuilder.append(nameParts[nameParts.length - 1]);
            customer.userName = usernameBuilder.toString();
        }
        // b. Chuẩn hóa ngày sinh từ mm-dd-yyyy -> dd/mm/yyyy
        String[] dateParts = customer.dayOfBirth.split("-");
        if (dateParts.length == 3) {
            customer.dayOfBirth = dateParts[1] + "/" + dateParts[0] + "/" + dateParts[2];
        }
        // b4. Gửi lại đối tượng đã được chuẩn hóa về server
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(customer);
        byte[] customerData = baos.toByteArray();
        byte[] finalSendData = new byte[8 + customerData.length];
        System.arraycopy(requestId, 0, finalSendData, 0, 8);
        System.arraycopy(customerData, 0, finalSendData, 8, customerData.length);
        socket.send(new DatagramPacket(finalSendData, finalSendData.length, serverAddr, port));
        System.out.println("Client đã hoàn thành và đóng kết nối.");
    }
}