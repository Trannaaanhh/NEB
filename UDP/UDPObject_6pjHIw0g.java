package UDP;

import java.io.*;
import java.net.*;
import java.util.Arrays;

class Product implements Serializable {
    private static final long serialVersionUID = 20161107L;
    String id, code, name; int quantity;
    public Product(String id, String code, String name, int quantity) {
        this.id = id; this.code = code; this.name = name; this.quantity = quantity;
    }
}
public class UDPObject_6pjHIw0g {
    public static void main(String[] args) throws Exception, ClassNotFoundException {
    DatagramSocket socket = new DatagramSocket();
        InetAddress serverAddr = InetAddress.getByName("203.162.10.109"); int port = 2209;
        String studentCode = "B22DCVT034"; 
        String qCode = "6pjHIw0g";
        // b1. Gửi thông điệp khởi tạo chứa mã sinh viên và mã câu hỏi
        byte[] sendData = (";" + studentCode + ";" + qCode).getBytes();
        socket.send(new DatagramPacket(sendData, sendData.length, serverAddr, port));
        // b2. Nhận đối tượng Product từ server
        byte[] receiveBuffer = new byte[65535];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);
        byte[] requestId = Arrays.copyOfRange(receivePacket.getData(), 0, 8);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(receivePacket.getData(), 8, receivePacket.getLength() - 8));
        Product product = (Product) ois.readObject();
        // b3. Sửa lại các thông tin bị sai của đối tượng
        // a. Đảo ngược lại từ đầu tiên và từ cuối cùng trong tên
        String[] words = product.name.split("\\s+");
        if (words.length > 1) {
            String temp = words[0];
            words[0] = words[words.length - 1];
            words[words.length - 1] = temp;
            product.name = String.join(" ", words);
        }
        // b. Đảo ngược lại các chữ số của số lượng
        String reversedQtyStr = new StringBuilder(String.valueOf(product.quantity)).reverse().toString();
        product.quantity = Integer.parseInt(reversedQtyStr);
        // b4. Gửi lại đối tượng đã được sửa đổi về server
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(product);
        byte[] productData = baos.toByteArray();
        byte[] finalSendData = new byte[8 + productData.length];
        System.arraycopy(requestId, 0, finalSendData, 0, 8);
        System.arraycopy(productData, 0, finalSendData, 8, productData.length);
        socket.send(new DatagramPacket(finalSendData, finalSendData.length, serverAddr, port));
        System.out.println("Client đã hoàn thành và đóng kết nối.");
    }
}