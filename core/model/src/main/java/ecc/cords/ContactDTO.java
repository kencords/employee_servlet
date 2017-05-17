package ecc.cords;

public class ContactDTO {

	private Long contactId;
	private String contactType;
	private String contactValue;

	private EmployeeDTO employee;

	public ContactDTO(String contactType, String contactValue) {
		this.contactType = contactType;
		this.contactValue = contactValue;
	}

	public Long getContactId() {
		return contactId;
	}

	protected void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public EmployeeDTO getEmployee() {
		return employee;
	}

	public void setEmployee(EmployeeDTO employee) {
		this.employee = employee;
	}

	public String getContactType() {
		return contactType;
	}

	public void setContactType(String contactType) {
		this.contactType = contactType;
	}

	public String getContactValue() {
		return contactValue;
	}

	public void setContactValue(String contactValue) {
		this.contactValue = contactValue;
	}

	@Override	
	public String toString() {
		return "[" + this.contactId + "][" + this.contactType + ": " + this.contactValue + "]";
	}

	@Override
   	public boolean equals(Object obj) {
       if(obj == null || getClass() != obj.getClass())
         return false;

        ContactDTO tmp = (ContactDTO) obj;
        return this.contactType.equals(tmp.getContactType()) 
        && this.contactValue.equals(tmp.getContactValue());
        
   }

   @Override
   	public int hashCode() {
        return java.util.Objects.hash(contactType, contactValue);
    }
}