package ecc.cords;

import java.util.HashSet;
import java.util.Set;

public class RoleDTO {

	private Long roleId;
	private String roleName;

	private Set<EmployeeDTO> employees = new HashSet<>();

	public RoleDTO() {

	}
	
	public RoleDTO(Long roleId, String roleName) {
		this.roleId = roleId;
		this.roleName = roleName;
	}

	public Long getRoleId() {
		return roleId;
	}

	protected void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Set<EmployeeDTO> getEmployees() {
		return employees;
	}

	public void setEmployees(Set<EmployeeDTO> employees) {
		this.employees = employees;
	}

	@Override
	public String toString() {
		return roleName;
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj == null &&!this.getClass().equals(obj.getClass())) 
	    	return false;

	    RoleDTO role2 = (RoleDTO) obj;
	    return this.roleName.equals(role2.getRoleName());
   	}

   	@Override
   	public int hashCode() {
      	int tmp = 0;
      	tmp = (roleId + roleName).hashCode();
      	return tmp;
   	}
}