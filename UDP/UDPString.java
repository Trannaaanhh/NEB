/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UDP;

import java.net.*;
import java.io.*;
/**
 *
 * @author Admin
 */
public class UDPString {
    public static void main(String[] args) throws IOException {
        String host = "203.162.10.109";
        int port =2208;
        String studentCode = "B22DCVT034";
        String qCode = "DbbRpenO";
        try(DatagramSocket socket = new DatagramSocket()){
            String req = ";" + studentCode + ";" +qCode;
            byte[] send = req.getBytes();
            socket.send(new DatagramPacket(send, send.length, InetAddress.getByName(host), port));
            System.out.println("done a \n");
            
            byte[] buf = new byte[65535];
            DatagramPacket pkt = new DatagramPacket(buf, buf.length);
            socket.receive(pkt);
            String response = new String(pkt.getData(), 0, pkt.getLength(),"UTF-8");
            String[] parts = response.split(";", 2);
            String requestId = parts[0];
            String data = parts[1];
            System.out.println("done b \n");
            
            String fixed = normalize(data);
            System.out.println("done c \n");
            
            String sendBack = requestId + ";" + fixed;
            byte[] back = sendBack.getBytes();
            socket.send(new DatagramPacket(back, back.length, InetAddress.getByName(host), port));
            System.out.println("done d \n");
        }
    }

    private static String normalize(String input) {
        String[] words = input.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words){
            sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
