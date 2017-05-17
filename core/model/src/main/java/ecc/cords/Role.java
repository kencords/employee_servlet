package ecc.cords;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "roles")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Role{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_id")
	private Long roleId;

	@Column(name = "role")
	private String roleName;

	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	private Set<Employee> employees = new HashSet<>();

	public Role(){}

	public Role(String roleName) {
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

	public Set<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(Set<Employee> employees) {
		this.employees = employees;
	}

	@Override
	public boolean equals(Object obj) {
	    if (obj == null &&!this.getClass().equals(obj.getClass())) 
	    	return false;

	    Role role2 = (Role) obj;
	    return this.roleName.equals(role2.getRoleName());
   	}

   	@Override
   	public int hashCode() {
      	int tmp = 0;
      	tmp = (roleId + roleName).hashCode();
      	return tmp;
   	}
}