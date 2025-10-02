package UDP;

import java.io.*;
import java.net.*;

class Product implements Serializable {
    private static final long serialVersionUID = 20161107;
    String id; String code; String name; int quantity;
    public Product(String id, String code, String name, int quantity) {
        this.id = id; this.code = code; this.name = name; this.quantity = quantity;
    }
    @Override
    public String toString() {
        return id + code + name + quantity;
    }
}

public class UDPObject_6pjHIw0g {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress host = InetAddress.getByName("203.162.10.109"); int port = 2209;
        String studentCode = "B22DCVT034"; 
        String qCode = "6pjHIw0g";
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
        Product product = (Product) ois.readObject();
        //Thay doi thong tin
        product.name = fixName(product.name);
        product.quantity = fixQuantity(product.quantity);
        System.out.println("Done thay doi");
        //Sua doi doi tuong de gui lai
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(product);
        oos.flush();
        byte[] productBytes = baos.toByteArray();
        byte[] sendData = new byte[8 + productBytes.length];
        System.arraycopy(requestIdBytes, 0, sendData, 0, 8);
        System.arraycopy(productBytes, 0, sendData, 8, productBytes.length);
        DatagramPacket sendBack = new DatagramPacket(sendData, sendData.length, host, port);
        socket.send(sendBack);
        System.out.println("Done sua doi");
        //d
        socket.close();
        System.out.println("done d\n Product sent: " + product);
    }
    
    private static String fixName(String name) {
        String[] words = name.trim().split("\\s+");
        if (words.length > 1) {
            String temp = words[0];
            words[0] = words[words.length - 1];
            words[words.length - 1] = temp;
        }
        return String.join(" ", words);
    }
    
    private static int fixQuantity(int quantity) {
        String reversedString = new StringBuilder(String.valueOf(quantity)).reverse().toString();
        return Integer.parseInt(reversedString);
    }
}