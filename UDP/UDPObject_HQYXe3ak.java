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
class Customer implements Serializable{
    private static final long serialVersionUID = 20151107;
    String id, code, name, dayOfBirth, userName;
    public Customer(String id, String code, String name, String dayOfBirth, String userName){
        this.id = id; this.code = code; this.name = name; this.dayOfBirth = dayOfBirth; this.userName = userName;
    }
}

public class UDPObject_HQYXe3ak {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress host = InetAddress.getByName("203.162.10.109"); int port = 2209;
        String studentCode = "B22DCVT034";
        String qCode = "HQYXe3ak";
        //b
        //gui chuoi
        byte[] data = (";" + studentCode + ";" + qCode).getBytes();
        socket.send(new DatagramPacket(data, data.length, host, port));
        //nhan doi tuong
        byte[] buffer = new byte[65535];
        DatagramPacket received = new DatagramPacket(buffer,buffer.length);
        socket.receive(received);
        byte[] requestId = Arrays.copyOfRange(received.getData(), 0, 8);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(received.getData(), 8, received.getLength()-8));
        Customer customer = (Customer) ois.readObject();
        //sua ten khach hang
        String[] parts = customer.name.trim().toLowerCase().split("\\s+");
        if (parts.length > 0){
            String lastName = parts[parts.length - 1].toUpperCase();
            StringJoiner firstName = new StringJoiner(" ");
            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];
                firstName.add(Character.toUpperCase(part.charAt(0)) + part.substring(1));                
            }
            customer.name = lastName + ", " + firstName.toString();
        }
        //chuan hoa ngay sinh
        String[] dateparts = customer.dayOfBirth.split("-");
        if (dateparts.length == 3) {customer.dayOfBirth = dateparts[1] + "/" + dateparts[0] + "/" + dateparts[2];}
        //tao tai khoan cho khach
        if(parts.length > 0){
            StringBuilder usernameBuilder = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {usernameBuilder.append(parts[i].charAt(0));}
            usernameBuilder.append(parts[parts.length - 1]);
            customer.userName = usernameBuilder.toString();
        }
        //gui du lieu len server
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(customer);
        byte[] customerData = baos.toByteArray();
        byte[] finalSendData = new byte[8 + customerData.length];
        System.arraycopy(requestId, 0, finalSendData, 0, 8);
        System.arraycopy(customerData, 0, finalSendData, 8, customerData.length);
        socket.send(new DatagramPacket(finalSendData, finalSendData.length, host, port));
        System.out.println("Client hoan tanh va dong!");
    }
}