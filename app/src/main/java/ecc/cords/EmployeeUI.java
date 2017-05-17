package ecc.cords;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EmployeeUI {

	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RESET = "\u001B[0m";

	private static EmployeeUI employeeUI = null;
	private static DaoService daoService = new DaoService();
	private static DTO_EntityMapper mapper = new DTO_EntityMapper();
	private static String logMsg = "";

	public String addEmployee() throws Exception {
		System.out.print("\033\143");
		System.out.println("ADD NEW EMPLOYEE...\n\n");
		System.out.println("BASIC INFORMATION");
		EmployeeDTO employee = new EmployeeDTO();
		employee.setLastName(InputHelper.askString("Enter Lastname: ", false));
		employee.setFirstName(InputHelper.askString("Enter Firstname: ", false));
		employee.setMiddleName(InputHelper.askString("Enter Middlename: ", false));
		employee.setSuffix(InputHelper.askString("Enter Suffix: ", true));
		employee.setTitle(InputHelper.askString("Enter Title: ", true));
		employee.setBirthDate(InputHelper.askDate("Enter Birthdate (yyyy-mm-dd): "));
		employee.setGwa(InputHelper.askPositiveFloat("Enter GWA (float): ", false));
		employee.setAddress(askAddressDTO());		
		System.out.println("CONTACT INFORMATION");
		employee.setContacts(ContactUI.getInstance().askContacts(true));
		System.out.println("\nCAREER INFORMATION");
		employee.setCurrentlyHired(InputHelper.askBoolean("Is currently hired? (Y|N): "));
		employee.setHireDate(InputHelper.askDate("Enter Date Hired (yyyy-mm-dd): "));
		employee.setRoles(RoleUI.getInstance().askRoles());
		return EmployeeManager.addEmployee(employee);
	}

	public AddressDTO askAddressDTO() {
		System.out.println("ADDRESS:");
		AddressDTO address = new AddressDTO();
		address.setStreetNo(InputHelper.askPositiveNumber("Enter Street No: ", false));
		address.setStreet(InputHelper.askString("Enter Street: ", false));
		address.setBrgy(InputHelper.askString("Enter Barangay: ", false));
		address.setCity(InputHelper.askString("Enter City: ", false));
		address.setZipcode(InputHelper.askString("Enter Zipcode: ", false));
		return address;
	}

	public boolean editEmployee() throws Exception {
		System.out.print("\033\143");
		System.out.println("EDIT EMPLOYEE...\n\n");
		System.out.println("EMPLOYEE LIST:");
		System.out.println(getEmployees());
		int id = InputHelper.askPositiveNumber("\nEnter Employee ID: ", false);
		EmployeeDTO employee = mapper.mapToEmployeeDTO(EmployeeManager.getEmployee(id));
		return manageEmployee(employee);
	}

	public EmployeeDTO editEmployeeDetails(EmployeeDTO employee) throws Exception {
		while(true) {
			System.out.println("\n1. EDIT NAME");
			System.out.println("2. EDIT BIRTHDATE");
			System.out.println("3. EDIT ADDRESS");
			System.out.println("4. EDIT GWA");
			System.out.println("5. EDIT CAREER INFORMATION");
			System.out.println("6. DONE");
			String choice = InputHelper.askChoice("\nWhat do you want to do? (Enter Choice Number): ");
			switch(choice) {
				case "1":
					employee = editEmployeeName(employee);
					break;
				case "2":
					System.out.println("\nEDIT BIRTHDATE\n");
					employee.setBirthDate(InputHelper.askDate("Enter Birthdate (yyyy-mm-dd): "));
					logMsg = "Pending Employee Birthdate Revision";
					break;
				case "3":
					employee = editEmployeeAddress(employee);
					break;
				case "4":
					System.out.println("\nEDIT GWA\n");
					employee.setGwa(InputHelper.askPositiveFloat("Enter GWA (float): ", false));
					logMsg = "-Pending Employee GWA Revision";
					break;
				case "5":
					System.out.println("\nEDIT CAREER INFORMATION\n");
					employee.setCurrentlyHired(InputHelper.askBoolean("Is currently hired? (Y|N): "));
					employee.setHireDate(InputHelper.askDate("Enter Date Hired (yyyy-mm-dd): "));
					logMsg = "Pending Employee CAREER INFORMATION Revision"; 
					break;
				case "6":
					return employee;
				default:
					System.out.println("Invalid Choice!");
			}
		}
	}

	public String deleteEmployee() throws Exception {
		System.out.print("\033\143");
		System.out.println("DELETE EMPLOYEE...\n\n");
		System.out.println("EMPLOYEE LIST:");
		System.out.println(getEmployees());
		int id = InputHelper.askPositiveNumber("\nEnter Employee ID: ", false);		
		daoService.deleteElement(EmployeeManager.getEmployee(id));
		return "Deleted Employee " + id + "!";
	}

	public String getEmployeeDetail(EmployeeDTO employee, String type) {
		StringBuilder sb = new StringBuilder();
		sb.append("\nEMPLOYEE ID: " + employee.getEmpId());
		sb.append("\nNAME: " + employee.getTitle() + " " + employee.getFirstName() + 
		" " + employee.getMiddleName() + " " + 
		(type.equals("LN")? emphasizeText(employee.getLastName()) : employee.getLastName()) + 
		" " + employee.getSuffix());
		sb.append("\nADDRESS: " + employee.getAddress());
		sb.append("\nBIRTHDATE: " + Utils.formatDate(employee.getBirthDate()));
		sb.append(type.equals("GWA")? emphasizeText("\nGWA: " + employee.getGwa()) : "\nGWA: " + employee.getGwa());
		String curHired = employee.isCurrentlyHired() ? "YES" : "NO";
		sb.append("\nCURRENTLY HIRED: " + curHired);
		sb.append(type.equals("DH")? emphasizeText("\nDATE HIRED: " + Utils.formatDate(employee.getHireDate())) : "\nDATE HIRED: " + Utils.formatDate(employee.getHireDate()));
		sb.append("\nCONTACTS: "); 
		List<ContactDTO> contacts = new ArrayList<>(employee.getContacts().stream()
																		  .sorted((c1,c2) -> (c1.getContactType()+c1.getContactValue()).compareTo(c2.getContactType()+c2.getContactValue()))
																		  .collect(Collectors.toList()));
		for(int i=0; i < contacts.size(); i++) {							
			sb.append("\n" + (type.equals("")? "["+ (i+1) + "]" : "") + ("[" + contacts.get(i).getContactType() + ": " + contacts.get(i).getContactValue() + "]"));
		}
		sb.append("\nROLES: " + employee.getRoles().stream()
												   .sorted((role1, role2) -> Long.compare(role1.getRoleId(), role2.getRoleId()))
												   .collect(Collectors.toList())  + "\n");
		return sb.toString();
	}

	public String getEmployeeDetails(List<EmployeeDTO> employees, String type) {
		StringBuilder sb = new StringBuilder();
		employees.forEach(employee -> sb.append(getEmployeeDetail(employee,type)));
		return sb.toString(); 
	}

	public boolean manageEmployee(EmployeeDTO employee) throws Exception {
		while(true) {
			System.out.println(getEmployeeDetail(employee,""));
			System.out.println("1. EDIT EMPLOYEE DETAILS");
			System.out.println("2. ADD ROLE");
			System.out.println("3. DELETE ROLE");
			System.out.println("4. ADD CONTACT");
			System.out.println("5. UPDATE CONTACT");
			System.out.println("6. DELETE CONTACT");
			System.out.println("7. DONE");
			System.out.println("8. BACK");
			System.out.println(logMsg.equals("")? "" : "\n" + logMsg + "\n");
			logMsg = "";
			String choice = InputHelper.askChoice("What do you want to do? (Enter Choice Number): ");
			int id = 0;
			try {
				switch(choice) {
					case "1":
						employee = editEmployeeDetails(employee);
						break;
					case "2":
						System.out.println("\n" + RoleUI.getInstance().getFilteredRoles(employee));
						id = InputHelper.askPositiveNumber("What Role? (Enter Role ID): ", false);
						employee = EmployeeManager.addEmployeeRole(employee, id);
						break;
					case "3":
						id = InputHelper.askPositiveNumber("What Role? (Enter Role ID to delete): ", false);
						employee = EmployeeManager.deleteEmployeeRole(employee, id);
						break;
					case "4":
						employee = EmployeeManager.addContact(employee, ContactUI.getInstance().askContacts(false));
						break;
					case "5":
						ContactUI.getInstance().manageContact(employee, false);
						break;	
					case "6":
						ContactUI.getInstance().manageContact(employee, true);
						break;
					case "7":
						daoService.updateElement(mapper.mapToEmployee(employee, false));
						return true;
					case "8":
						return false;
					default:
						System.out.println("Invalid Choice!");
				}
			} catch(Exception exception) {
				exception.printStackTrace();
				logMsg = EmployeeManager.getLogMsg();
			}
		}
	}

	private EmployeeDTO editEmployeeName(EmployeeDTO employee) {
		System.out.println("\nEDIT EMPLOYEE NAME\n");
		employee.setLastName(InputHelper.askString("Enter Lastname: ", false));
		employee.setFirstName(InputHelper.askString("Enter Firstname: ", false));
		employee.setMiddleName(InputHelper.askString("Enter Middlename: ", false));
		employee.setSuffix(InputHelper.askString("Enter Suffix: ", true));
		employee.setTitle(InputHelper.askString("Enter Title: ", true));
		logMsg = "Pending Employee Name Revision";
		return employee;
	}

	private EmployeeDTO editEmployeeAddress(EmployeeDTO employee) {
		System.out.println("\nEDIT EMPLOYEE ADDRESS\n");
		employee.getAddress().setStreetNo(InputHelper.askPositiveNumber("Enter Street No: ", false));
		employee.getAddress().setStreet(InputHelper.askString("Enter Street: ", false));
		employee.getAddress().setBrgy(InputHelper.askString("Enter Barangay: ", false));
		employee.getAddress().setCity(InputHelper.askString("Enter City: ", false));
		employee.getAddress().setZipcode(InputHelper.askString("Enter Zipcode: ", false));
		logMsg = "Pending Employee Address Revision";
		return employee;
	}

	private String emphasizeText(String msg) {
		return ANSI_GREEN + msg + ANSI_RESET;
	}

	private String getEmployees(){
		StringBuilder sb = new StringBuilder();
		List<EmployeeDTO> employees = mapper.mapSimplifiedEmployees("");
		employees.stream()
				 .sorted((employee1,employee2) -> Long.compare(employee1.getEmpId(), employee2.getEmpId()))
				 .forEach(employee -> sb.append(employee + "\n"));
		return sb.toString();
	}

	public static EmployeeUI getInstance(){
		if(employeeUI == null) {
			employeeUI = new EmployeeUI();
		}
		return employeeUI;
	}
}