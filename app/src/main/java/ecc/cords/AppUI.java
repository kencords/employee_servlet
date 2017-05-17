package ecc.cords;

import java.util.logging.Level;
import java.util.stream.Collectors;

public class AppUI{

	private static DTO_EntityMapper mapper = new DTO_EntityMapper();
	private static String logMsg = "";

	public AppUI() {
		startApplication();
	}
	
	public void startApplication() {
		while(true) {
			System.out.print("\033\143\n");
			System.out.println("EMPLOYEE RECORDS SYSTEM v2.0");
			System.out.println(logMsg.equals("")? "" : "\n" + logMsg + "\n");
			logMsg = "";
			System.out.println("\n1. ADD EMPLOYEE        5. SORT BY GWA");
			System.out.println("2. DELETE EMPLOYEE     6. SORT BY LASTNAME");
			System.out.println("3. EDIT EMPLOYEE       7. SORT BY HIREDATE");
			System.out.println("4. MANAGE ROLES        8. EXIT");
			try {
				String choice = InputHelper.askChoice("\nWhat do you want to do? (Enter Choice Number): ");

				switch(choice){
					case "1":
						logMsg = EmployeeUI.getInstance().addEmployee();
						break;
					case "2":
						logMsg = EmployeeUI.getInstance().deleteEmployee();
						break;
					case "3":
						boolean isEdited = EmployeeUI.getInstance().editEmployee();
						logMsg = isEdited? "Successfully Edited Employee!" : "Edit Employee Cancelled!";
						break;
					case "4":
						RoleUI.getInstance().manageRoles();
						break;
					case "5":
						logMsg = "\nEMPLOYEE LIST SORTED BY GWA\n" + EmployeeUI.getInstance().getEmployeeDetails(mapper.mapEmployeeDTOList("").stream()
								.sorted((emp1,emp2) -> Float.compare(emp1.getGwa(), emp2.getGwa()))
								.collect(Collectors.toList()), "GWA");
						break;
					case "6":
						logMsg = "\nEMPLOYEE LIST SORTED BY LASTNAME\n" + EmployeeUI.getInstance()
								 .getEmployeeDetails(mapper.mapEmployeeDTOList("name.lastName"), "LN"); 
						break;
					case "7":
						logMsg = "\nEMPLOYEE LIST SORTED BY HIRE DATE\n" + EmployeeUI.getInstance().getEmployeeDetails(mapper.mapEmployeeDTOList("hireDate"), "DH"); 
						break;
					case "8":
						System.exit(0);
					default:
						System.out.println("Invalid Choice!");
				}
			} catch(Exception e) {
				logMsg = EmployeeManager.getLogMsg();
			}
		}
	}

	public static void main(String[] args) {
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
		new AppUI();
	}
}