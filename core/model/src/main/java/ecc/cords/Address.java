package ecc.cords;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "addr_id")
	private Long addrId;

	@Column(name = "str_no")
	private int streetNo;
	private String street;
	private String brgy;
	private String city;
	private String zipcode;

	public Address() {}

	public Address(int streetNo, String street, String brgy, String city, String zipcode) {
		this.streetNo = streetNo;
		this.street = street;
		this.brgy = brgy;
		this.city = city;
		this.zipcode = zipcode;
	}

	public Long getAddrId() {
		return addrId;
	}

	protected void setAddrId(Long addrId) {
		this.addrId = addrId;
	}

	public int getStreetNo() {
		return streetNo;
	}

	public void setStreetNo(int streetNo) {
		this.streetNo = streetNo;
	}

	public String getStreet(){
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getBrgy() {
		return brgy;
	}

	public void setBrgy(String brgy) {
		this.brgy = brgy;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null || getClass() != obj.getClass())
			return false;
		Address tmp = (Address) obj;

		return this.streetNo == tmp.getStreetNo() && this.street.equals(tmp.getStreet()) && this.brgy.equals(tmp.getBrgy()) 
		&& this.city.equals(tmp.getCity()) && this.zipcode.equals(tmp.getZipcode());
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(streetNo, street, brgy, city, zipcode);
	}
}