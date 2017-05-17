package ecc.cords;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class DTO_EntityMapper {

	private static DaoService daoService = new DaoService();

	public Address createAddress(AddressDTO addressDTO) {
		Address address = new Address(addressDTO.getStreetNo(), addressDTO.getStreet(), addressDTO.getBrgy(), addressDTO.getCity(), addressDTO.getZipcode());
		address.setAddrId(addressDTO.getAddrId());
		return address;
	}

	public AddressDTO createAddressDTO(Address address) {
		return new AddressDTO(address.getAddrId(), address.getStreetNo(), address.getStreet(), address.getBrgy(), address.getCity(), address.getZipcode());
	}

	public Contact createContact(ContactDTO contactDTO) {
		Contact contact = new Contact(contactDTO.getContactType(), contactDTO.getContactValue());
		contact.setContactId(contactDTO.getContactId());
		contact.setEmployee(mapToEmployee(contactDTO.getEmployee(), false));
		return contact;
	}

	public ContactDTO createContactDTO(String contactType, String contactValue) {
		return new ContactDTO(contactType, contactValue);
	}

	public Set<Contact> createContactSet(Employee employee, Set<ContactDTO> contactsDTO) {
		Set<Contact> contacts = employee.getContacts();
		if(contacts == null)
			contacts = new HashSet<>();
		contacts.clear();
		try {
			for(ContactDTO contactDTO : contactsDTO) {
				Contact contact = new Contact(contactDTO.getContactType(), contactDTO.getContactValue());
				if(contactDTO.getContactId()!= null)
					contact.setContactId(contactDTO.getContactId());
				contact.setEmployee(employee);
				contacts.add(contact);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return contacts;
	}

	public Set<ContactDTO> createContactSetDTO(Set<Contact> contacts) {
		Set<ContactDTO> contactsDTO = new HashSet<>();
		contacts.forEach(contact -> {
			ContactDTO contactDTO = new ContactDTO(contact.getContactType(), contact.getContactValue());
			contactDTO.setContactId(contact.getContactId());
			contactsDTO.add(contactDTO);
		});
		return contactsDTO;
	}

	public Set<Role> createRoleSet(Set<RoleDTO> rolesDTO) {
		Set<Role> roles = new HashSet<>();
		rolesDTO.forEach(role -> roles.add(mapToRole(role)));
		return roles;
	}

	public Set<RoleDTO> createRoleSetDTO(Set<Role> roles) {
		Set<RoleDTO> rolesDTO = new HashSet<>();
		roles.forEach(role -> {
			try { 
				rolesDTO.add(mapToRoleDTO(role));
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		});
		return rolesDTO;
	}

	public List<RoleDTO> getAllRoles() {
		List<Role> roles = daoService.getAllElements(Role.class);
		List<RoleDTO> rolesDTO = new ArrayList<>();
		roles.forEach(role -> {
			rolesDTO.add(new RoleDTO(role.getRoleId(), role.getRoleName()));
		});
		return rolesDTO;
	}

	public Employee mapToEmployee(EmployeeDTO employeeDTO, boolean isNew) {
		Employee employee = new Employee();
		if(!isNew) {
			employee = daoService.getElement(employeeDTO.getEmpId(), Employee.class);
		}
		employee.setEmpId(employeeDTO.getEmpId());
		employee.setName(new Name(employeeDTO.getLastName(), employeeDTO.getFirstName(), employeeDTO.getMiddleName(), employeeDTO.getSuffix(), 
		employeeDTO.getTitle()));
		employee.setBirthDate(employeeDTO.getBirthDate());
		employee.setGwa(employeeDTO.getGwa());
		employee.setAddress(createAddress(employeeDTO.getAddress()));
		employee.setCurrentlyHired(employeeDTO.isCurrentlyHired());
		employee.setHireDate(employeeDTO.getHireDate());
		employee.setRoles(createRoleSet(employeeDTO.getRoles()));
		employee.setContacts(createContactSet(employee, employeeDTO.getContacts()));
		return employee;
	}

	public EmployeeDTO mapToEmployeeDTO(Employee employee) throws Exception {
		EmployeeDTO empDTO = new EmployeeDTO();
		empDTO.setEmpId(employee.getEmpId());
		empDTO.setLastName(employee.getName().getLastName());
		empDTO.setFirstName(employee.getName().getFirstName());
		empDTO.setMiddleName(employee.getName().getMiddleName());
		empDTO.setSuffix(employee.getName().getSuffix());
		empDTO.setTitle(employee.getName().getTitle());
		empDTO.setBirthDate(employee.getBirthDate());
		empDTO.setGwa(employee.getGwa());
		empDTO.setCurrentlyHired(employee.isCurrentlyHired());
		empDTO.setHireDate(employee.getHireDate());
		empDTO.setAddress(createAddressDTO(employee.getAddress()));
		empDTO.setContacts(createContactSetDTO(employee.getContacts()));
		empDTO.setRoles(createRoleSetDTO(employee.getRoles()));
		return empDTO;
	}

	public List<EmployeeDTO> mapEmployeeDTOList(String order) {
		List<Employee> employees = (!order.equals("") ? daoService.getOrderedEmployees(order) : 
		daoService.getAllElements(Employee.class));
		List<EmployeeDTO> employeesDTO = new ArrayList<>();
		employees.forEach(e -> {
			try {
				employeesDTO.add(mapToEmployeeDTO(e));
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		});
		return employeesDTO;		
	}

	public List<EmployeeDTO> mapSimplifiedEmployees(String order) {
		List<Object[]> employees = daoService.getSimplifiedEmployees(order);
		List<EmployeeDTO> employeesDTO = new ArrayList<>();
		employees.forEach(obj -> {
			EmployeeDTO employeeDTO = new EmployeeDTO();
			employeeDTO.setEmpId((Long)obj[0]);
			employeeDTO.setLastName(((Name)obj[1]).getLastName());
			employeeDTO.setFirstName(((Name)obj[1]).getFirstName());
			employeeDTO.setMiddleName(((Name)obj[1]).getMiddleName());
			employeeDTO.setSuffix(((Name)obj[1]).getSuffix());
			employeeDTO.setTitle(((Name)obj[1]).getTitle());
			employeeDTO.setHireDate((Date)obj[2]);
			employeeDTO.setGwa((float)obj[3]);
			employeesDTO.add(employeeDTO);
		});
		return employeesDTO;
	}

	public RoleDTO mapToRoleDTO(Role role) throws Exception {
		RoleDTO roleDTO = new RoleDTO(role.getRoleId(), role.getRoleName());
		Set<EmployeeDTO> employees = new HashSet<>();
		role.getEmployees().forEach(employee -> employees.add(createEmployeeDTO(employee)));
		roleDTO.setEmployees(employees);
		return roleDTO;
	}

	public Role mapToRole(RoleDTO roleDTO) {
		Role role = new Role();
		role.setRoleId(roleDTO.getRoleId());
		role.setRoleName(roleDTO.getRoleName());
		Set<Employee> employees = new HashSet<>();
		roleDTO.getEmployees().forEach(employee -> employees.add(createEmployee(employee)));
		role.setEmployees(employees);	
		return role;
	}

	private Employee createEmployee(EmployeeDTO employeeDTO) {
		Employee employee = new Employee();
		employee.setEmpId(employeeDTO.getEmpId());
		employee.setName(new Name(employeeDTO.getLastName(), employeeDTO.getFirstName(), employeeDTO.getMiddleName(), employeeDTO.getSuffix(), 
		employeeDTO.getTitle()));
		return employee;
	}

	private EmployeeDTO createEmployeeDTO(Employee employee) {
		EmployeeDTO empDTO = new EmployeeDTO();
		empDTO.setEmpId(employee.getEmpId());
		empDTO.setLastName(employee.getName().getLastName());
		empDTO.setFirstName(employee.getName().getFirstName());
		empDTO.setMiddleName(employee.getName().getMiddleName());
		empDTO.setSuffix(employee.getName().getSuffix());
		empDTO.setTitle(employee.getName().getTitle());
		return empDTO;
	}
}