package ecc.cords;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EmployeeManager{

	private static DaoService daoService = new DaoService();
	private static DTO_EntityMapper mapper = new DTO_EntityMapper();
	private static String logMsg = "";

	public static LogMsg addEmployee(EmployeeDTO employeeDTO) {
		Employee employee = mapper.mapToEmployee(employeeDTO, true);
		try {
			daoService.saveElement(employee);
		} catch(Exception exception) {
			exception.printStackTrace();
			return new LogMsg("Employee Creation Failed!", "red");
		}
		return new LogMsg("Employee Creation Successful!", "green");
	}

	public static LogMsg updateEmployee(EmployeeDTO employeeDTO) {
		Employee employee = mapper.mapToEmployee(employeeDTO, false);
		try {
			daoService.updateElement(employee);
		} catch(Exception exception) {
			exception.printStackTrace();
			return new LogMsg("Employee Update Failed!", "red");
		}
		return new LogMsg("Employee Updated Successfully!", "green");
	}

	public static LogMsg deleteEmployee(int empId) {
		try {
			Employee employee = getEmployee(empId);
			for(Role role : employee.getRoles()) {
				daoService.evictCollection("ecc.cords.Role.employees", role.getRoleId());
			}
			daoService.deleteElement(employee);
		} catch(Exception ex) {
			System.out.println("EmpID: " + empId);
			ex.printStackTrace();
			return new LogMsg("Cannot delete Employee " + empId + "!", "red");
		}
		return new LogMsg("Deleted Employee " + empId + "!", "green");
	}
	
	public static EmployeeDTO addContact(EmployeeDTO employee, Set<ContactDTO> contacts) {
		contacts.forEach(contact -> { 
			contact.setEmployee(employee);
			employee.getContacts().add(contact);
		});
		return employee;
	}

	public static void updateContact(EmployeeDTO employee, Set<ContactDTO> contacts) {
		employee.getContacts().clear();
		employee.setContacts(contacts);
	}

	public static void deleteContact(EmployeeDTO employee, ContactDTO contact) throws Exception {
		Set<ContactDTO> contacts = employee.getContacts();
		contacts.remove(contact);
		employee.setContacts(contacts);
	}
													
	public static Employee getEmployee(int id) {
		Employee employee = new Employee();
		try {
			employee = daoService.getElement(Long.valueOf(id), Employee.class);
			employee.getEmpId();
			return employee;
		} catch(Exception exception) {
			logMsg = "Employee not found!";
			throw exception;
		}
	}

	public static EmployeeDTO addEmployeeRole(EmployeeDTO employee, int role_id) throws Exception{
		try {
			Set<RoleDTO> roles = employee.getRoles();
			Role role = daoService.getElement(Long.valueOf(role_id), Role.class);
			roles.add(mapper.mapToRoleDTO(role));
			System.out.println(role.getRoleName());
			employee.setRoles(roles);
			return employee;
		} catch(Exception exception) {
			logMsg = "Invalid role!";
			throw new Exception("",exception);
		}
	}

	public static EmployeeDTO deleteEmployeeRole(EmployeeDTO employee, RoleDTO role) {
		Set<RoleDTO> roles = employee.getRoles();
		roles.remove(role);
		employee.setRoles(roles);
		return employee;
	}

	public static LogMsg createRole(String role_str) {
		Role role = new Role(role_str);
		try {
			daoService.getElement(role);
			return new LogMsg("Role " + role_str + " already exists!", "red");
		} catch(Exception exception) {
			daoService.saveElement(role);
			return new LogMsg("Successfully created " + role_str + " Role!", "green");
		}
	}

	public static String deleteRole(RoleDTO roleDTO) throws Exception {
		Role role = mapper.mapToRole(roleDTO);
		try {
			daoService.deleteElement(role);
			return "Successfully deleted role " + role.getRoleName() + "!";
		} catch(Exception e) {
			logMsg = "Role " + role.getRoleName() + " cannot be deleted!";
			throw new Exception();
		}
	}

	public static LogMsg updateRole(RoleDTO roleDTO, String role_name) throws Exception {
		Role role = mapper.mapToRole(roleDTO);
		String prev_name = role.getRoleName();
		if(role.getEmployees().size()==0) {
			role.setRoleName(role_name);
			try {
				Role newRole = daoService.getElement(role);
				return new LogMsg("Role " + role_name + " already exists!", "red"); 
			} catch(Exception exception) {
				daoService.updateElement(role);
				return new LogMsg("Successfully updated role " + prev_name + " to " + role.getRoleName() + "!", "green");
			}
		}
		logMsg = "Role " + role.getRoleName() + " cannot be updated!";
		throw new Exception();
	}

	public static RoleDTO getRole(int role_id) throws Exception {
		Role role = new Role();
		try {
			role = daoService.getElement(Long.valueOf(role_id), Role.class);
			role.getRoleName();
		} catch(Exception exception) {
			logMsg = "Role does not exist!";
			throw exception;
		}
		return mapper.mapToRoleDTO(role);
	}

	public static String getLogMsg() {
		return logMsg;
	}

	private static Role addRole(Long roleId) {
		return daoService.getElement(roleId, Role.class);
	}
}