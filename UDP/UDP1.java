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
class Student implements Serializable {
    public static final long serialVersionUID = 20171107;
    String id, code, name, email;
    public Student(String id, String code, String name, String email){
        this.id = id; this.code = code; this.name = name; this.email = email;
    }
    public Student(String code){this.code = code;}
    @Override
    public String toString(){
        return id + " - " + code + " - " + name + " - " + email;
    }
}

public class UDP1 {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress host = InetAddress.getByName("203.162.10.109");
        int port = 2209;
        String studentCode = "B22DCVT034";
        String qCode = "LR7OAGDz";
        String message = ";" + studentCode + ";" + qCode;
        //a
        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, host, port);
        socket.send(packet);
        System.out.println("done a\n");
        //b
        byte[] buffer = new byte[4096];
        DatagramPacket received = new DatagramPacket(buffer, buffer.length);
        socket.receive(received);
        System.out.println("done b\n");
        //c
        //chuan hoa ten + tao email
        byte[] requestIdBytes = new byte[8];
        System.arraycopy(buffer, 0, requestIdBytes, 0, 8);
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer, 8, received.getLength() - 8);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Student st = (Student) ois.readObject();
        st.name = normalizeName(st.name);
        st.email = createEmail(st.name);
        System.out.println("done chuan hoa\n");
        //gui len sv
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(st); oos.flush();
        byte[] studentBytes = baos.toByteArray();
        byte[] sendData = new byte[8 + studentBytes.length];
        System.arraycopy(requestIdBytes, 0, sendData, 0, 8);
        System.arraycopy(studentBytes, 0, sendData, 8, studentBytes.length);
        DatagramPacket sendBack = new DatagramPacket(sendData, sendData.length, host, port);
        socket.send(sendBack);
        System.out.println("done gui\n");
        System.out.println("done c\n");
        //d
        socket.close();
        System.out.println("done d\n");
    }
    
    private static String normalizeName(String s){
        String[] parts = s.trim().toLowerCase().split("//s+");
        StringBuilder sb = new StringBuilder();
        for(String p : parts){
            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
    
    private static String createEmail(String name){
        String[] parts = name.toLowerCase().split("\\s+");
        String lastName = parts[parts.length - 1];
        StringBuilder prefix = new StringBuilder(lastName);
        for(int i = 0; i < parts.length - 1; i++) prefix.append(parts[i].charAt(0));
        return prefix.toString() + "@ptit.edu.vn";
    }
}
