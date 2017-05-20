package ecc.cords;

import java.util.ArrayList;
import java.util.List;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class EditEmployeeServlet extends HttpServlet {

	private static DTO_EntityMapper mapper = new DTO_EntityMapper();

	private final String[] hiredOptions = {"YES", "NO"};

	private List<ContactDTO> contacts = new ArrayList<>();
	private List<RoleDTO> roles = new ArrayList<>();
	private EmployeeDTO employee = new EmployeeDTO();
	private EmployeeDTO prevEmployee = new EmployeeDTO();
	private AddressDTO address = new AddressDTO();
	private List<LogMsg> logMsgs = new ArrayList<>();
	private FormValidator validator = new FormValidator();
	private String method = "POST";
	private boolean isInitial = true;
	private boolean validEmployee = true;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		doPost(req,res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		handleEvents(req, res);
		method = "POST";
		loadEmployee(res, req.getParameter("empId"));

		out.println(Template.getHeader("Employee Records System: Edit Employee"));
		out.println("<h1>EDIT EMPLOYEE</h1>");
		if(validEmployee) {
			out.println(Template.createForm("editEmployee", method,
				"<div style=\"border:1px solid black; width:500px; margin: 0 auto; text-align:center;\">" +
				Template.createLogMsg(logMsgs) + "<br>" +
				Template.createEmphasizedText("PERSONAL INFORMATION") +
				Template.createTextField("title", validator.valueFiller(req, "title", employee.getTitle()), "Title") + "<br>" +
				Template.createTextField("lastName", validator.valueFiller(req, "lastName", employee.getLastName()), "Last Name*") + "<br>" +
				Template.createTextField("firstName", validator.valueFiller(req, "firstName", employee.getFirstName()), "First Name*") + "<br>" +
				Template.createTextField("middleName", validator.valueFiller(req, "middleName", employee.getMiddleName()), "Middle Name*") + "<br>" +
				Template.createTextField("suffix", validator.valueFiller(req, "suffix", employee.getSuffix()), "Suffix") + "<br>" +
				Template.createTextField("birthDate", validator.valueFiller(req, "birthDate", Utils.formatDateSimplified(employee.getBirthDate())), "Birth Date (yyyy-mm-dd)*") + "<br>" +
				Template.createTextField("gwa", validator.valueFiller(req, "gwa", employee.getGwa() + ""), "GWA") + "<br>" +
				Template.createEmphasizedText("ADDRESS") +
				Template.createTextField("strNo", validator.valueFiller(req, "strNo", address.getStreetNo() + ""), "Street Number*") + "<br>" +
				Template.createTextField("street", validator.valueFiller(req, "street", address.getStreet()), "Street*") + "<br>" +
				Template.createTextField("brgy", validator.valueFiller(req, "brgy", address.getBrgy()), "Brgy*") + "<br>" +
				Template.createTextField("city", validator.valueFiller(req, "city", address.getCity()), "City*") + "<br>" +
				Template.createTextField("zipcode", validator.valueFiller(req, "zipcode", address.getZipcode()), "Zipcode*") + "<br>" +
				Template.createEmphasizedText("CONTACTS") +
				Contact_RoleUI.askContacts(contacts, true) +
				Template.createEmphasizedText("CAREER INFORMATION") +
				"<p style=\"display:inline-block;\">Currently Hired? &nbsp;</p>" +  
				Template.createSelectedDropDown(hiredOptions, "currentlyHired", (employee.isCurrentlyHired()? "YES" : "NO")) + "<br>" +
				Template.createTextField("hireDate", validator.valueFiller(req, "hDate", Utils.formatDateSimplified(employee.getHireDate())), "HIRE DATE (yyyy-mm-dd)*") + "<br>" +
				Template.createEmphasizedText("ROLES") +
				Contact_RoleUI.askRoles(roles) + "<br><br>" +
				Template.createSubmitBtn("saveEmployeeBtn", "", "SAVE EMPLOYEE") +
				Template.createSubmitBtn("backBtn", "", "BACK") + "<br>" +
				"<input type=\"hidden\" name=\"empId\" value=\"" + employee.getEmpId() + "\"/><br>" +
				"<br></div>"
			));
		}
		else {
			out.println(Template.createLogMsg(logMsgs));
		}
		if(validator.getHasSaved()){
			validator.setHasSaved(false);
		}
		out.println(Template.getClosing());
		out.close();
		logMsgs.clear();
	}

	private void handleEvents(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		if(req.getParameter("saveEmployeeBtn") != null) {
			validator.setEmployee(employee);
			validator.saveEmployeeIfValid(logMsgs, contacts, roles, req, res, true);
			validator.setHasSaved(true);
			isInitial = true;
		}
		if(req.getParameter("backBtn") != null) {
			roles.clear();
			contacts.clear();
			method = "GET";
			logMsgs.clear();
			isInitial = true;
			res.sendRedirect("employeeProfile?empId=" + employee.getEmpId());
		}
		if(req.getParameter("addContactBtn") != null) {
			processAddContact(req.getParameter("conOpt"), req.getParameter("contact"));
		}
		if(req.getParameter("delConBtn") != null) {
			processDeleteContact(Integer.parseInt(req.getParameter("delConBtn")));
		}
		if(req.getParameter("updateConBtn") != null) {
			int index = Integer.parseInt(req.getParameter("updateConBtn"));
			contacts.get(index).setContactValue(req.getParameter("contact"+index));
		}
		if(req.getParameter("addRoleBtn") != null){
			processAddRole(Integer.parseInt(req.getParameter("roleOpt")));
		}
		if(req.getParameter("delRoleBtn") != null) {
			EmployeeManager.deleteEmployeeRole(employee, roles.get(Integer.parseInt(req.getParameter("delRoleBtn"))));
			roles = new ArrayList<>(employee.getRoles());
		}	
	}

	private void loadEmployee(HttpServletResponse res, String empId) throws IOException, ServletException {
		if(empId == null) {
			validEmployee = false;
			res.sendError(404,"Employee not specified!");
			return;
		}
		if(isInitial || !empId.equals(employee.getEmpId() + "")) {
			try {	
				employee = mapper.mapToEmployeeDTO(EmployeeManager.getEmployee(Integer.parseInt(empId)));
			} catch(Exception ex) {
				validEmployee = false;
				res.sendError(404,"Employee not found!");
				return;
			}
			validEmployee = true;
			prevEmployee = employee;
			contacts = new ArrayList<>(employee.getContacts());
			roles = new ArrayList<>(employee.getRoles());
			address = employee.getAddress();
			isInitial = false;
		}
	}

	private void processAddContact(String contactType, String contactValue) {
		if(contactType.equals(Contact_RoleUI.contactOptions[0]) && !Utils.isValidLandline(contactValue)) {
			logMsgs.add(new LogMsg("Invalid Landline!", "red"));
			return;
		}
		else if(contactType.equals(Contact_RoleUI.contactOptions[1]) && !Utils.isValidMobile(contactValue)) {
			logMsgs.add(new LogMsg("Invalid Mobile!", "red"));
			return;
		}
		else if(contactType.equals(Contact_RoleUI.contactOptions[2]) && !Utils.isValidEmail(contactValue)) {
			logMsgs.add(new LogMsg("Invalid Email!", "red"));
			return;
		}
		employee.getContacts().add(new ContactDTO((contactType.equals(Contact_RoleUI.contactOptions[0])? "Landline" : 
		(contactType.equals(Contact_RoleUI.contactOptions[1])? "Mobile" : "Email"))  , contactValue));
		contacts = new ArrayList<>(employee.getContacts());
	}

	private void processDeleteContact(int index) {
		if(contacts.size() == 1) {
			logMsgs.add(new LogMsg("Employee must have atleast one Contact!", "red"));
			return;
		}	
		try {
			EmployeeManager.deleteContact(employee, contacts.get(index));
			contacts = new ArrayList<>(employee.getContacts());
		} catch(Exception ex) {
			ex.printStackTrace();
			logMsgs.add(new LogMsg("Cannot delete contact!", "red"));
		}
	}

	private void processAddRole(int role_id) {
		System.out.println("pAR");
		try {
			employee = EmployeeManager.addEmployeeRole(employee, role_id);
		} catch(Exception ex) {
			ex.printStackTrace();
			logMsgs.add(new LogMsg("Cannot add role!", "red"));
		}
		roles = new ArrayList<>(employee.getRoles());
	}
}