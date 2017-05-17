package ecc.cords;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "contacts")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Contact{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="contact_id")
	private Long contactId;

	@Column(name="contact_type")
	private String contactType;

	@Column(name="contact_value")
	private String contactValue;

	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(fetch=FetchType.EAGER, optional=false)
	@Fetch(FetchMode.SELECT)
	@JoinColumn(name="emp_id", nullable = false)
	private Employee employee;

	public Contact(){}

	public Contact(String contactType, String contactValue) {
		this.contactType = contactType;
		this.contactValue = contactValue;
	}

	public Long getContactId() {
		return contactId;
	}

	protected void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
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

        Contact tmp = (Contact) obj;

         return this.contactType.equals(tmp.getContactType()) 
         && this.contactValue.equals(tmp.getContactValue());
        
   }

   @Override
   	public int hashCode() {
        return java.util.Objects.hash(contactType, contactValue);
    }
}