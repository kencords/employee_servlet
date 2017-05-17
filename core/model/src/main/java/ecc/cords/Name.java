package ecc.cords;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Name implements Comparable<Name>{

    @Column(name = "lastname")
	private String lastName;
    @Column(name = "firstname")
    private String firstName;
    @Column(name = "middlename")
    private String middleName;

    private String suffix;
    private String title;

    public Name() {}
    
    public Name(String lastName, String firstName, String middleName, String suffix, String title) {
    	this.lastName = lastName;
    	this.firstName = firstName;
    	this.middleName = middleName;
    	this.suffix = suffix;
    	this.title = title;
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

    public int compareTo(Name name) {
        return (lastName + firstName + middleName + suffix)
            .compareTo(name.getLastName() + name.getFirstName() + name.getMiddleName() + name.getSuffix());
    }
}