/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package WS;
import java.util.*;
import client.DataService;
import client.DataService_Service;


/**
 *
 * @author Admin
 */
public class WSClient_RTHlurbJ {

    public static void main(String[] args) throws Exception {
        // --- ⚠️ CHANGE YOUR STUDENT CODE HERE ---
        String studentCode = "B22DCVT034"; 
        String qCode = "RTHlurbJ";
        // -------------------------------------------

        System.out.println("Starting Client Program...");

        // Initialize the web service from the auto-generated classes
        DataService_Service service = new DataService_Service();
        DataService port = service.getDataServicePort();

        // a. Call the getData method from the server
        System.out.println("1. Calling getData method from the server...");
        List<Integer> receivedData = port.getData(studentCode, qCode);
        System.out.println("=> Data received: " + receivedData);

        // Validate the received data
        if (receivedData == null || receivedData.size() < 2) {
            System.out.println("Error: Invalid data received.");
            return;
        }

        // b. Process the data
        System.out.println("\n2. Processing data...");
        int k = receivedData.get(0);
        List<Integer> processingList = new ArrayList<>(receivedData.subList(1, receivedData.size()));
        
        System.out.println("-> K value = " + k);
        System.out.println("-> List to process: " + processingList);

        if (k <= 0 || k > processingList.size()) {
            System.out.println("Error: K value is out of bounds.");
            return;
        }
        
        Collections.sort(processingList);
        System.out.println("-> List after sorting: " + processingList);

        int kthSmallest = processingList.get(k - 1);
        int kthLargest = processingList.get(processingList.size() - k);
        
        System.out.println("=> The " + k + "th largest element is: " + kthLargest);
        System.out.println("=> The " + k + "th smallest element is: " + kthSmallest);

        // c. Send the result back to the server
        System.out.println("\n3. Sending result back to the server...");
        List<Integer> resultData = new ArrayList<>();
        resultData.add(kthLargest);
        resultData.add(kthSmallest);

        // Call the method without assigning its result
        port.submitDataIntArray(studentCode, qCode, resultData);
        System.out.println("=> Result sent successfully. (This method has no return value).");

        // d. End of program
        System.out.println("\n✅ Program finished.");
    }
}
