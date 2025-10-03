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
class Employee implements Serializable{
    private static final long serialVersionUID = 20261107L;
    String id, name, hireDate; double salary;
    public Employee(String id, String name, Double salary, String hireDate){
        this.id = id; this.name = name; this.salary = salary; this.hireDate = hireDate;
    }
}

public class UDP1 {
    public static void main(String[] args)throws IOException, ClassNotFoundException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress host = InetAddress.getByName("203.162.10.109"); int port = 2209;
        String studentCode = "B22DCVT034";
        String qCode = "tC0O2pto";
        //a
        byte[] sendData = (";" + studentCode + ";" + qCode).getBytes();
        socket.send(new DatagramPacket(sendData, sendData.length, host, port));
        //b
        byte[] buffer = new byte[65535];
        DatagramPacket received = new DatagramPacket(buffer,buffer.length);
        socket.receive(received);
        byte[] requestId = Arrays.copyOfRange(received.getData(), 0, 8);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(received.getData(), 8, received.getLength() - 8));
        Employee ep = (Employee) ois.readObject();
        //c
        //chuan hoa ten
        String[] parts = ep.name.trim().toLowerCase().split("\\s+");
        StringBuilder normalizedName = new StringBuilder();
        for (String p : parts) { normalizedName.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1)).append(" ");}
        ep.name = normalizedName.toString().trim();
        //tang luong
        int sum = 0;
        for(char c : ep.hireDate.substring(0,4).toCharArray()) {sum += Character.getNumericValue(c);}
        ep.salary *= ( 1 + sum/100.0);
        //chuan hoa ngay
        String[] dateparts = ep.hireDate.split("-");
        ep.hireDate = dateparts[2] + "/" + dateparts[1] + "/" + dateparts[0];
        //gui lai server
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(ep);
        byte[] empData = baos.toByteArray();
        byte[] finalSendData = new byte[8 + empData.length];
        System.arraycopy(requestId, 0, finalSendData, 0, 8);
        System.arraycopy(empData, 0, finalSendData, 8, empData.length);
        socket.send(new DatagramPacket(finalSendData, finalSendData.length, host, port));
        //d
        System.out.println("Client done and close!");
    }
}
