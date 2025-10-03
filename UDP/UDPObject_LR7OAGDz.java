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
class Student implements Serializable{
    private static final long serialVersionUID = 20171107;
    String id, code, name, email;
    public Student(String id, String code, String name, String email){
        this.id = id; this.code = code; this.name = name; this. email = email;
    }
}

public class UDPObject_LR7OAGDz {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress host = InetAddress.getByName("203.162.10.109"); int port = 2209;
        String studentCode = "B22DCVT034";
        String qCode = "LR7OAGDz";
        //a
        byte[] sendData = (";" + studentCode + ";" + qCode).getBytes();
        socket.send(new DatagramPacket(sendData, sendData.length, host ,port));
        //b
        byte[] buffer = new byte[65535];
        DatagramPacket received = new DatagramPacket(buffer,buffer.length);
        socket.receive(received);
        byte[] requestId = Arrays.copyOfRange(received.getData(), 0, 8);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(received.getData(), 8, received.getLength() - 8));
        Student st = (Student) ois.readObject();
        //c
        //chuan hoa ten theo quy tac
        String[] parts = st.name.trim().toLowerCase().split("\\s+");
        StringBuilder namesb = new StringBuilder();
        for (String part : parts) {namesb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");}
        st.name = namesb.toString().trim();
        //tao email
        String[] emailparts = st.name.toLowerCase().split("\\s+");
        String lastName = emailparts[emailparts.length - 1];
        StringBuilder createEmail = new StringBuilder(lastName);
        for (int i = 0; i < emailparts.length - 1; i++) {createEmail.append(emailparts[i].charAt(0));}
        st.email = createEmail.toString() + "@ptit.edu.vn";
        //gui lai server
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(st);
        byte[] studentData = baos.toByteArray();
        byte[] finalSendData = new byte[8 + studentData.length];
        System.arraycopy(requestId, 0, finalSendData, 0, 8);
        System.arraycopy(studentData, 0, finalSendData, 8, studentData.length);
        socket.send(new DatagramPacket(finalSendData, finalSendData.length, host, port));
        //d
        System.out.println(" client done and close!");
    }
}
