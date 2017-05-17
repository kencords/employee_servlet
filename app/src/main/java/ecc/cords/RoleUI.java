package ecc.cords;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RoleUI {

	private static RoleUI roleUI = null;
	private static DTO_EntityMapper mapper = new DTO_EntityMapper();
	private static String logMsg = "";

	public  void manageRoles() {
		while(true) {
			System.out.print("\033\143");
			System.out.println("MANAGE ROLES...");
			System.out.println(logMsg.equals("")? "" : "\n" + logMsg + "\n");
			logMsg = "";
			System.out.println("\nROLES LIST:\n");
			System.out.println(getRoles());
			System.out.println("1. ADD ROLE");
			System.out.println("2. EDIT ROLE");
			System.out.println("3. DELETE ROLE");
			System.out.println("4. BACK");
			RoleDTO role = new RoleDTO();
			String choice = InputHelper.askChoice("What do you want to do? (Enter Choice Number): ");
			try {
				switch(choice) {
					case "1":
						logMsg = EmployeeManager.createRole(InputHelper.askString("Enter Role Name: ", false).toUpperCase());
						break;
					case "2":
						role = EmployeeManager.getRole(InputHelper.askPositiveNumber("Enter Role ID: ", false));
						logMsg = EmployeeManager.updateRole(role,InputHelper.askString("Enter Role Name: ", false).toUpperCase());
						break;
					case "3":
						role = EmployeeManager.getRole(InputHelper.askPositiveNumber("Enter Role ID: ", false));
						logMsg = EmployeeManager.deleteRole(role);
						break;
					case "4":
						return;
				}
			}catch(Exception exception) {
				logMsg = EmployeeManager.getLogMsg();
				if(choice.equals("2")||choice.equals("3")){
					logMsg += "\nRole is currently used by:" + getRoleOwners(role);
				}
			}
		}
	}

	public Set<RoleDTO> askRoles()	{
		Set<RoleDTO> roles = new HashSet<>();
		List<RoleDTO> availRoles = mapper.getAllRoles();
		while(true)	{
			System.out.println("\nROLES LIST:\n");
			availRoles = availRoles.stream()
		 						   .filter(role -> !roles.contains(role))
		  			 			   .sorted((role1,role2) -> Long.compare(role1.getRoleId(), role2.getRoleId()))
			   		  			   .collect(Collectors.toList());									 
			if(availRoles.size()==0) {
				break;
			}
			availRoles.forEach(System.out::println);
			int role_id = InputHelper.askPositiveNumber("What Role? (Enter role number): ", false);
			try {
				roles.add(EmployeeManager.getRole(role_id));
			} catch(Exception exception){
				System.out.println(EmployeeManager.getLogMsg() + "\n");
				continue;
			}
			boolean isDone = InputHelper.askBoolean("Done adding roles? (Y|N): ");
			if(isDone && roles.size()!=0) {
				break;
			}
			else if(isDone && roles.size()==0) {
				System.out.println("Please add atleast one role!");
				continue;
			}
		}
		return roles;
	}

	public String getFilteredRoles(EmployeeDTO employee) {
		StringBuilder sb = new StringBuilder();
		mapper.getAllRoles().stream()
						  	.filter(role -> !employee.getRoles().contains(role))
						  	.sorted((role1,role2) -> Long.compare(role1.getRoleId(), role2.getRoleId()))
							.forEach(role -> sb.append(role + "\n"));
		return sb.toString();
	}

	public String getRoles() {
		StringBuilder sb = new StringBuilder();
		mapper.getAllRoles().stream()
							.sorted((role1,role2) -> Long.compare(role1.getRoleId(), role2.getRoleId()))
							.forEach(role -> sb.append(role + "\n"));
		return sb.toString();
	}

	private String getRoleOwners(RoleDTO role) {
		StringBuilder sb = new StringBuilder();
		role.getEmployees().stream()
						   .sorted((emp1,emp2) -> Long.compare(emp1.getEmpId(), emp2.getEmpId()))
		                   .forEach(employee -> sb.append("\n" + employee));
		return sb.toString();
	}

	public static RoleUI getInstance() {
		if(roleUI == null) {
			roleUI = new RoleUI();
		}
		return roleUI;
	}
}