package ecc.cords;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class EmployeeDTO {

	private Long empId;
	private String lastName;
	private String firstName;
	private String middleName;
	private String suffix;
	private String title;
	private Float gwa;
	private Date birthDate;
	private Date hireDate;
	private boolean currentlyHired;

	private AddressDTO address;
	private Set<ContactDTO> contacts = new HashSet<>();
	private Set<RoleDTO> roles = new HashSet<>();

	public Long getEmpId() {
		return empId;
	}

	public void setEmpId(Long empId) {
		this.empId = empId;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Float getGwa(){
		return gwa;
	}

	public void setGwa(Float gwa) {
		this.gwa = gwa;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

	public boolean isCurrentlyHired() {
		return currentlyHired;
	}

	public void setCurrentlyHired(boolean currentlyHired) {
		this.currentlyHired = currentlyHired;
	}

	public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public Set<ContactDTO> getContacts() {
        return contacts;
    }

    public void setContacts(Set<ContactDTO> contacts) {
        this.contacts = contacts;
    }

    public Set<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDTO> roles) { 
        this.roles = roles;
    }

    @Override
    public String toString(){
        return "Employee ID: " + empId + " Employee Name: " + lastName + ", " + firstName + " " + middleName + " " + suffix; 
    }
}