package ecc.cords;

import java.util.HashSet;
import java.util.List;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class FormValidator {

	private String lName;
	private String fName;
	private String mName;
	private String title;
	private String suffix;
	private String bDate;
	private String gwa;
	private String strNo;
	private String street;
	private String brgy;
	private String city;
	private String zipcode;
	private String curHired;
	private String hDate;
	private boolean hasSaved;
	private boolean isAccepted;
	private EmployeeDTO employee = new EmployeeDTO();

	public boolean getHasSaved() {
		return hasSaved;
	}	

	public void setHasSaved(boolean hasSaved) {
		this.hasSaved = hasSaved;
	}

	public void setEmployee(EmployeeDTO employee) {
		this.employee = employee;
	}

	public void saveEmployeeIfValid(List<LogMsg> logMsgs, List<ContactDTO> contacts, List<RoleDTO> roles,
	HttpServletRequest req, HttpServletResponse res, boolean isEdit) 
	throws IOException, ServletException {
		isAccepted = true;
		lName = req.getParameter("lastName");
		fName = req.getParameter("firstName");
		mName = req.getParameter("middleName");
		title = req.getParameter("title");
		suffix = req.getParameter("suffix");
		bDate = req.getParameter("birthDate");
		gwa = req.getParameter("gwa");
		strNo = req.getParameter("strNo");
		street = req.getParameter("street");
		brgy = req.getParameter("brgy");
		city = req.getParameter("city");
		zipcode = req.getParameter("zipcode");
		curHired = req.getParameter("currentlyHired");
		hDate = req.getParameter("hireDate");
		isAccepted = validateEmployeeForm(logMsgs, contacts.size());
		if(isAccepted) {
			try {
				saveEmployee(logMsgs, contacts, roles, isEdit);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public String valueFiller(HttpServletRequest req, String paramName, String initVal) {
		return ((req.getParameter(paramName) == null || (hasSaved && isAccepted))? initVal : req.getParameter(paramName));
	}

	private void saveEmployee(List<LogMsg> logMsgs, List<ContactDTO> contacts, List<RoleDTO> roles, boolean isEdit) throws Exception {
		if(!isEdit) { 
			employee = new EmployeeDTO();
		}
		employee.setLastName(lName);
		employee.setFirstName(fName);
		employee.setMiddleName(mName);
		employee.setSuffix(suffix);
		employee.setTitle(title);
		employee.setBirthDate(Utils.convertToDate(bDate));
		employee.setGwa(Float.parseFloat(gwa));
		employee.setAddress(fillAddress());		
		employee.setContacts(new HashSet<ContactDTO>(contacts));
		employee.setCurrentlyHired(curHired.equals("YES"));
		employee.setHireDate(Utils.convertToDate(hDate));
		employee.setRoles(new HashSet<RoleDTO>(roles));
		if(!isEdit){
			logMsgs.add(EmployeeManager.addEmployee(employee));
		}
		if(isEdit) {
			logMsgs.add(EmployeeManager.updateEmployee(employee));
			System.out.println("save: " + employee.getContacts());
		}
	}

	private AddressDTO fillAddress() {
		AddressDTO address = new AddressDTO();
		address.setStreetNo(Integer.parseInt(strNo));
		address.setStreet(street);
		address.setBrgy(brgy);
		address.setCity(city);
		address.setZipcode(zipcode);
		return address;
	}

	private boolean validateEmployeeForm(List<LogMsg> logMsgs, int contactSize) {
		boolean isValid = true;
		isValid &= !lName.trim().equals("");
		isValid &= !fName.trim().equals("");
		isValid &= !mName.trim().equals("");
		isValid &= !bDate.trim().equals("");
		isValid &= !gwa.trim().equals("");
		isValid &= !strNo.trim().equals("");
		isValid &= !street.trim().equals("");
		isValid &= !brgy.trim().equals("");
		isValid &= !city.trim().equals("");
		isValid &= !zipcode.trim().equals("");
		isValid &= !hDate.trim().equals("");
		if(!isValid) {
			logMsgs.add(new LogMsg("Please fill up fields marked with *", "red"));
		}
		if(contactSize == 0) {
			isValid = false;
			logMsgs.add(new LogMsg("Employee must have atleast one contact!", "red"));
		}
		if(bDate!=null && !bDate.equals("") && !Utils.isValidDate(bDate)){
			isValid = false;
			logMsgs.add(new LogMsg("Invalid Birthdate!", "red"));
		}
		if(hDate!=null && !hDate.equals("") && !Utils.isValidDate(hDate)){
			isValid = false;
			logMsgs.add(new LogMsg("Invalid Hire Date!", "red"));
		}
		return isValid;
	}
}