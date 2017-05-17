package ecc.cords;

import java.util.Date;
import java.util.Set;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "employees")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Employee{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id")
    private Long empId;

    @Temporal(TemporalType.DATE)
    @Column(name = "birthdate")
    private Date birthDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "hiredate")
    private Date hireDate;

    private float gwa;

    @Column(name = "currentlyhired")
    private boolean currentlyHired;

    @Embedded   
    private Name name;

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToOne
    @Cascade({CascadeType.ALL})
    @JoinColumn(name="addr_id")
    private Address address;

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy="employee", fetch=FetchType.EAGER, orphanRemoval=true)
    @Cascade({CascadeType.ALL})
    @Fetch(FetchMode.SELECT)
    private Set<Contact> contacts;

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name = "employee_role", 
        joinColumns = { @JoinColumn(name = "emp_id")}, 
        inverseJoinColumns = { @JoinColumn(name = "role_id")})
    @Fetch(FetchMode.SELECT)
    private Set<Role> roles;

    public Employee(){}
    
    public Employee(Name name, Date birthDate, Date hireDate, float gwa, boolean currentlyHired, Address address, Set<Contact> contacts, Set<Role> roles){
        this.name = name;
        this.birthDate = birthDate;
        this.hireDate = hireDate;
        this.gwa = gwa;
        this.currentlyHired = currentlyHired;
        this.address = address;
        this.contacts = contacts;
        this.roles = roles;
    }

    public Long getEmpId() {
        return empId;
    }

    protected void setEmpId(Long empId) {
        this.empId = empId;
    }

    public Name getName(){
        return name;
    }

    public void setName(Name name){
        this.name = name;
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

    public float getGwa() {
        return gwa;
    }

    public void setGwa(float gwa) {
        this.gwa = gwa;
    }

    public boolean isCurrentlyHired() {
        return currentlyHired;
    }

    public void setCurrentlyHired(boolean currentlyHired) {
        this.currentlyHired = currentlyHired;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Set<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(Set<Contact> contacts) {
        this.contacts = contacts;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) { 
        this.roles = roles;
    }
}