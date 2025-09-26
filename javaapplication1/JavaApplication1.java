/*
 * TCP Client - Gửi/nhận dữ liệu với server [qCode: UxlvA3Hz]
 */
package javaapplication1;

import java.io.*;
import java.net.*;
import java.util.*;

public class JavaApplication1 {
    public static void main(String[] args) throws IOException {
        String serverAddress = "localhost";
        int port = 2307;
        String studentCode = "B22DCVT034";
        String qCode = "UxlvA3Hz";
        String message = studentCode + ";" + qCode;
        try(Socket socket = new Socket(serverAddress, port)){
            socket.setSoTimeout(5000);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            
            dos.writeUTF(message);
            dos.flush();
            System.out.println("done a: " + message);
            
            String data = dis.readUTF();
            System.out.println("done b: " + data); 
            
            String[] parts = data.split(",");
            int[] arr = Arrays.stream(parts).mapToInt(Integer::parseInt).toArray();
            int veer = 0;
            int sum = 0;
            int direction = 0;
            for (int i = 1; i < arr.length; i++) {
                int j = arr[i] - arr[i - 1];
                sum += Math.abs(j);
                int newd = j > 0 ? 1 :(j < 0 ? -1 : direction);
                if (newd != 0 && direction != 0 && newd != direction){
                veer++;
                }
                if(newd != 0) direction = newd;                
            }
            System.out.println("done c: ");
            System.out.println("       Number of changes of direction: " + veer);
            System.out.println("       Total number of changes: " + sum);
            dos.write(veer);
            dos.write(sum);
            dos.flush();
            
            dos.close();
            dis.close();
            socket.close();
            System.out.println("done d");
        }
    }
}
