/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package WS;

import ObjectService.Employee;
import ObjectService.ObjectService;
import ObjectService.ObjectService_Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author Admin
 */
public class WS_ObjectService_meTEkpIr {

    public static void main(String[] args) throws Exception {
        // --- ⚠️ THAY CÁC THÔNG SỐ NÀY ---
        String studentCode = "B22DCVT034"; // <<== THAY BẰNG MÃ SINH VIÊN
        String qCode = "meTEkpIr";
        // ------------------------------------

        System.out.println("🚀 Starting Client Program for ObjectService...");

        // Khởi tạo dịch vụ web
        ObjectService_Service service = new ObjectService_Service();
        ObjectService port = service.getObjectServicePort();

        // a. Triệu gọi phương thức requestEmployee để nhận đối tượng
        System.out.println("1. Requesting Employee object from server...");
        Employee employee = port.requestEmployee(studentCode, qCode);

        if (employee == null || employee.getStartDate() == null || employee.getEndDate() == null) {
            System.out.println("Error: Received null Employee object or dates.");
            return;
        }
        
        System.out.println("=> Employee object received successfully.");

        // b. Tính toán số ngày làm việc
        System.out.println("\n2. Calculating working days...");

        // Lấy ngày bắt đầu và kết thúc từ đối tượng Employee
        XMLGregorianCalendar xmlStartDate = employee.getStartDate();
        XMLGregorianCalendar xmlEndDate = employee.getEndDate();

        // Chuyển đổi từ kiểu XMLGregorianCalendar sang kiểu LocalDate để dễ xử lý
        LocalDate startDate = xmlStartDate.toGregorianCalendar().toZonedDateTime().toLocalDate();
        LocalDate endDate = xmlEndDate.toGregorianCalendar().toZonedDateTime().toLocalDate();
        
        System.out.println("-> Start Date: " + startDate);
        System.out.println("-> End Date: " + endDate);

        long workingDays = 0;
        LocalDate currentDate = startDate;

        // Vòng lặp để duyệt qua từng ngày từ ngày bắt đầu đến ngày kết thúc
        while (!currentDate.isAfter(endDate)) {
            // Lấy ngày trong tuần (Thứ Hai, Thứ Ba,...)
            DayOfWeek day = currentDate.getDayOfWeek();
            
            // Nếu không phải Thứ Bảy và Chủ Nhật thì tăng biến đếm
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                workingDays++;
            }
            
            // Chuyển sang ngày tiếp theo
            currentDate = currentDate.plusDays(1);
        }

        System.out.println("=> Calculated Working Days: " + workingDays);

        // Cập nhật thuộc tính workingDays cho đối tượng Employee
        // Cần ép kiểu về int vì setter của Employee thường nhận kiểu int
        employee.setWorkingDays((int) workingDays);
        
        // c. Gửi đối tượng Employee đã cập nhật trở lại server
        System.out.println("\n3. Submitting updated Employee object back to the server...");
        port.submitEmployee(studentCode, qCode, employee);
        System.out.println("=> Employee object submitted successfully.");

        // d. Kết thúc chương trình
        System.out.println("\n✅ Program finished.");
    }
}