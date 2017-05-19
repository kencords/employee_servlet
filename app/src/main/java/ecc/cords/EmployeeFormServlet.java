package ecc.cords;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class EmployeeFormServlet extends HttpServlet {

	private List<LogMsg> logMsgs = new ArrayList<>();
	private FormValidator validator = new FormValidator();

	private final String[] hiredOptions = {"YES", "NO"};
	private final String[] textParamNames = {"title", "lastName", "firstName", "middleName", "suffix", "birthDate", "gwa", "strNo", "street",
	"brgy", "city", "zipcode"};
	private final String[] textDisplay = {"Title", "Last Name*", "First Name*", "Middle Name*", "Suffix", "Birthdate (yyyy-mm-dd)*", "GWA*",
	"STreet Number*", "Street*", "Barangay*", "City*", "Zipcode*"};
	private List<ContactDTO> contacts = new ArrayList<>();
	private List<RoleDTO> roles = new ArrayList<>();
	private EmployeeDTO employee;
	private boolean hasSaved = false;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		doPost(req,res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		handleEvents(req, res);

		out.println(Template.getHeader("Employee Records System: Employee Form"));
		out.println("<h1>ADD EMPLOYEE</h1>");
		out.println(Template.createForm("employeeForm", "POST",
			"<div style=\"border:1px solid black; width:500px; margin: 0 auto; text-align:center;\">" +
			Template.createLogMsg(logMsgs) + "<br>" +
			 Template.createEmphasizedText("PERSONAL INFORMATION") +
			createTextFields(req) +
			Template.createEmphasizedText("CONTACTS") +
			Contact_RoleUI.askContacts(contacts, false) +
			Template.createEmphasizedText("CAREER INFORMATION") +
			"<p style=\"display:inline-block;\">Currently Hired? &nbsp;</p>" +  
			Template.createDropDown(hiredOptions, "currentlyHired", false) + "<br>" +
			Template.createTextField("hireDate", validator.valueFiller(req, "hDate", ""), "Hire Date (yyyy-mm-dd)*") + "<br>" +
			Template.createEmphasizedText("ROLES") +
			Contact_RoleUI.askRoles(roles) + "<br><br>" +
			Template.createSubmitBtn("addEmployeeBtn", "", "ADD EMPLOYEE") +
			Template.createSubmitBtn("backBtn", "", "BACK") + "<br>" +
			"<br></div>"
		));
		if(validator.getHasSaved()){
			validator.setHasSaved(false);
		}
		out.println(Template.getClosing());
		out.close();
		logMsgs.clear();
	}

	public void setContacts(Set<ContactDTO> contactSet) {
		contacts = new ArrayList<ContactDTO>(contactSet);
	}

	private void handleEvents(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		if (req.getParameter("addEmployeeBtn") != null) {
			validator.saveEmployeeIfValid(logMsgs, contacts, roles, req, res, false);
			validator.setHasSaved(true);
		}
		if(req.getParameter("backBtn") != null) {
			roles.clear();
			contacts.clear();
			res.sendRedirect("home");
		}	
		if(req.getParameter("addContactBtn") != null) {
			processAddContact(req.getParameter("conOpt"), req.getParameter("contact"));
		}
		if(req.getParameter("addRoleBtn") != null){
			processAddRole(Integer.parseInt(req.getParameter("roleOpt")));
		}
		if(req.getParameter("delConBtn") != null) {
			processDeleteContact(Integer.parseInt(req.getParameter("delConBtn")));
		}
		if(req.getParameter("delRoleBtn") != null) {
			roles.remove(Integer.parseInt(req.getParameter("delRoleBtn")));
		}
	}

	private String createTextFields(HttpServletRequest req) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<textParamNames.length;i++) {
			if(textParamNames[i].equals("strNo")) {
				sb.append(Template.createEmphasizedText("ADDRESS") + "<br>");
			}
			sb.append(Template.createTextField(textParamNames[i], validator.valueFiller(req, textParamNames[i], ""), textDisplay[i]) + "<br>");
		}
		return sb.toString();
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
		contacts.add(new ContactDTO((contactType.equals(Contact_RoleUI.contactOptions[0])? "Landline" : 
		(contactType.equals(Contact_RoleUI.contactOptions[1])? "Mobile" : "Email"))  , contactValue));
	}

	private void processAddRole(int roleId) {
		RoleDTO role = new RoleDTO();
		try {
			role = EmployeeManager.getRole(roleId);
		} catch(Exception ex) {
			logMsgs.add(new LogMsg(EmployeeManager.getLogMsg(), "red"));
		}
		roles.add(role);
	}

	private void processDeleteContact(int index) {
		if(contacts.size()==1) {
			logMsgs.add(new LogMsg("Employee must have atleast one Contact!", "red"));
			return;
		}
		contacts.remove(index);
	}
}