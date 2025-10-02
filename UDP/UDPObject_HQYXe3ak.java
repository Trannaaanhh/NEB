package UDP;

import java.io.*;
import java.net.*;

class Customer implements Serializable {
    private static final long serialVersionUID = 20151107;
    String id; String code; String name; String dayOfBirth; String userName;
    public Customer(String id, String code, String name, String dayOfBirth, String userName) {
        this.id = id; this.code = code; this.name = name; this.dayOfBirth = dayOfBirth; this.userName = userName;
    }
    @Override
    public String toString() {
        return id + code +  name + dayOfBirth + userName;
    }
}

public class UDPObject_HQYXe3ak {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress host = InetAddress.getByName("203.162.10.109"); 
        int port = 2209;
        String studentCode = "B22DCVT034"; 
        String qCode = "HQYXe3ak";
        String message = ";" + studentCode + ";" + qCode;
        byte[] data = message.getBytes();
        //a
        DatagramPacket packet = new DatagramPacket(data, data.length, host, port);
        socket.send(packet);
        System.out.println("done a\n");
        //b
        byte[] buffer = new byte[4096];
        DatagramPacket received = new DatagramPacket(buffer, buffer.length);
        socket.receive(received);
        System.out.println("Da nhan du lieu tu server.");
        //c
        byte[] requestIdBytes = new byte[8];
        System.arraycopy(buffer, 0, requestIdBytes, 0, 8); 
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer, 8, received.getLength() - 8);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Customer customer = (Customer) ois.readObject();
        String originalName = customer.name; 
        //Thay doi thong tin
        customer.name = formatName(customer.name);
        customer.dayOfBirth = formatDate(customer.dayOfBirth);
        customer.userName = createUsername(originalName);
        System.out.println("Done thay doi");
        //Sua doi doi tuong
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(customer);
        oos.flush();
        byte[] customerBytes = baos.toByteArray();
        byte[] sendData = new byte[8 + customerBytes.length];
        System.arraycopy(requestIdBytes, 0, sendData, 0, 8);
        System.arraycopy(customerBytes, 0, sendData, 8, customerBytes.length);
        DatagramPacket sendBack = new DatagramPacket(sendData, sendData.length, host, port);
        socket.send(sendBack);
        System.out.println("Done sua doi");
        //d
        socket.close();
        System.out.println("done d\n Customer sent: " + customer);
    }
    
    private static String formatName(String name) {
        String[] parts = name.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder(parts[parts.length - 1].toUpperCase());
        sb.append(", ");
        for (int i = 0; i < parts.length - 1; i++) {
            sb.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
            if (i < parts.length - 2) sb.append(" ");
        }
        return sb.toString();
    }
    
    private static String formatDate(String dob) {
        String[] parts = dob.split("-"); // [mm, dd, yyyy]
        return parts[1] + "/" + parts[0] + "/" + parts[2];
    }
    
    private static String createUsername(String name) {
        String[] parts = name.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) sb.append(parts[i].charAt(0));
        sb.append(parts[parts.length - 1]);
        return sb.toString();
    }
}