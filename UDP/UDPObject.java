package UDP;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class UDPObject {
    public static void main(String[] args) {
        String host = "203.162.10.109";
        int port = 2209;
        String studentCode = "B22DCVT034";
        String qCode = "6pjHIw0g";

        try (DatagramSocket socket = new DatagramSocket()) {
            // 1. Gửi ";studentCode;qCode"
            String req = ";" + studentCode + ";" + qCode;
            byte[] send = req.getBytes();
            socket.send(new DatagramPacket(send, send.length,
                    InetAddress.getByName(host), port));

            // 2. Nhận: 8 byte requestId + Product
            byte[] buf = new byte[65535];
            DatagramPacket pkt = new DatagramPacket(buf, buf.length);
            socket.receive(pkt);
            byte[] res = Arrays.copyOf(pkt.getData(), pkt.getLength());
            String reqId = new String(res, 0, 8, "UTF-8");
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(res, 8, res.length - 8));
            Product p = (Product) ois.readObject();

            // 3. Sửa dữ liệu
            p.setName(fixName(p.getName()));
            p.setQuantity(fixQuantity(p.getQuantity()));

            // 4. Gửi lại
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(reqId.getBytes("UTF-8"));
            new ObjectOutputStream(baos).writeObject(p);
            byte[] back = baos.toByteArray();
            socket.send(new DatagramPacket(back, back.length,
                    InetAddress.getByName(host), port));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static String fixName(String s) {
        String[] w = s.trim().split("\\s+");
        if (w.length > 1) { String t = w[0]; w[0] = w[w.length - 1]; w[w.length - 1] = t; }
        return String.join(" ", w);
    }
    private static int fixQuantity(int q) {
        return Integer.parseInt(new StringBuilder(""+q).reverse().toString());
    }
}

class Product implements Serializable {
    private static final long serialVersionUID = 20161107L;
    private String id, code, name; private int quantity;
    public Product(String id, String code, String name, int quantity) {
        this.id=id; this.code=code; this.name=name; this.quantity=quantity;
    }
    public String getName(){return name;} public int getQuantity(){return quantity;}
    public void setName(String n){name=n;} public void setQuantity(int q){quantity=q;}
}
