package UDP;
import java.util.*;
import java.io.*;
import java.net.*;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Admin
 */
//a
class Product implements Serializable{
    private static final long serialVersionUID =20161107;
    String id, code, name; int quantity;
    public Product(String id, String code, String name, int quantity){
        this.id = id; this.code = code; this.name =name; this.quantity = quantity;
    }
}

public class UDPObject_6pjHIw0g {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //thiet lap ket noi
        DatagramSocket socket = new DatagramSocket();
        InetAddress host = InetAddress.getByName("203.162.10.109"); int port = 2209;
        String studentCode = "B22DCVT034";
        String qCode = "6pjHIw0g";
        //b
        //gui thong diep
        byte[] sendData = (";" + studentCode + ";" + qCode).getBytes();
        socket.send(new DatagramPacket(sendData, sendData.length, host, port));
        //nhan doi tuong
        byte[] buffer = new byte[65535];
        DatagramPacket received = new DatagramPacket(buffer, buffer.length);
        socket.receive(received);
        //tach requestId va doi tuong
        byte[] requestId = Arrays.copyOfRange(received.getData(), 0, 8);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(received.getData(), 8, received.getLength() - 8));
        Product product = (Product) ois.readObject();
        //sua thong tin
        //dao nguoc tu dau tien va tu cuoi cung trong ten
        String[] words = product.name.split("\\s+");
        if (words.length > 1){
            String tmp = words[0];
            words[0] = words[words.length - 1];
            words[words.length - 1] = tmp;
            product.name = String.join(" ", words);
        }
        //dao nguoc cac chu so cua so luong
        String reversed = new StringBuilder(String.valueOf(product.quantity)).reverse().toString();
        product.quantity = Integer.parseInt(reversed);
        //gui doi tuong len sv
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(product);
        byte[] productData = baos.toByteArray();
        byte[] finalSendData = new byte[8 + productData.length];
        System.arraycopy(requestId, 0, finalSendData, 0, 8);
        System.arraycopy(productData, 0, finalSendData, 8, productData.length);
        socket.send(new DatagramPacket(finalSendData,finalSendData.length, host, port));
        System.out.println("done gui du lieu and close!");
    }
}
