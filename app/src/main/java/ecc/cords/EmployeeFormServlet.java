package ecc.cords;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
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
	"Street Number*", "Street*", "Barangay*", "City*", "Zipcode*"};
	private boolean hasSaved = false;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		doPost(req,res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		handleEvents(req, res);
		EmployeeDTO employee = new EmployeeDTO();
		if(!res.isCommitted()) {
			employee = (EmployeeDTO) req.getSession().getAttribute("newEmp");
		}

		out.println(Template.getHeader("Employee Records System: Employee Form"));
		out.println("<h1>ADD EMPLOYEE</h1>");
		out.println(Template.createForm("employeeForm", "POST",
			"<div style=\"border:1px solid black; width:500px; margin: 0 auto; text-align:center;\">" +
			Template.createLogMsg(logMsgs) + "<br>" +
			 Template.createEmphasizedText("PERSONAL INFORMATION") +
			createTextFields(req) +
			Template.createEmphasizedText("CONTACTS") +
			Contact_RoleUI.askContacts(new ArrayList<>(employee.getContacts()), false) +
			Template.createEmphasizedText("CAREER INFORMATION") +
			"<p style=\"display:inline-block;\">Currently Hired? &nbsp;</p>" +  
			Template.createDropDown(hiredOptions, "currentlyHired", false) + "<br>" +
			Template.createTextField("hireDate", validator.valueFiller(req, "hireDate", ""), "Hire Date (yyyy-mm-dd)*") + "<br>" +
			Template.createEmphasizedText("ROLES") +
			Contact_RoleUI.askRoles(new ArrayList<>(employee.getRoles())) + "<br><br>" +
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

	private EmployeeDTO createEmployee(HttpServletRequest req) throws IOException, ServletException {
		if(req.getSession().getAttribute("newEmp") == null) {
			EmployeeDTO employee = new EmployeeDTO();
			employee.setContacts(new HashSet<ContactDTO>());
			employee.setRoles(new HashSet<RoleDTO>());
			req.getSession().setAttribute("newEmp", employee);
			return new EmployeeDTO();
		}
		return (EmployeeDTO) req.getSession().getAttribute("newEmp");
	}

	private void handleEvents(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		EmployeeDTO employee = createEmployee(req);
		List<ContactDTO> contacts = new ArrayList<>(employee.getContacts());
		List<RoleDTO> roles = new ArrayList<>(employee.getRoles());
		if (req.getParameter("addEmployeeBtn") != null) {
			validator.saveEmployeeIfValid(logMsgs, contacts, roles, req, res, false);
			validator.setHasSaved(true);
		}
		if(req.getParameter("backBtn") != null) {
			req.getSession().invalidate();
			res.sendRedirect("home");
		}	
		if(req.getParameter("addContactBtn") != null) {
			processAddContact(contacts, req.getParameter("conOpt"), req.getParameter("contact"));
		}
		if(req.getParameter("addRoleBtn") != null){
			processAddRole(roles, Integer.parseInt(req.getParameter("roleOpt")));
		}
		if(req.getParameter("delConBtn") != null) {
			processDeleteContact(contacts, Integer.parseInt(req.getParameter("delConBtn")));
		}
		if(req.getParameter("delRoleBtn") != null) {
			roles.remove(Integer.parseInt(req.getParameter("delRoleBtn")));
		}
		employee.setContacts(new HashSet<>(contacts));
		employee.setRoles(new HashSet<>(roles));
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

	private void processAddContact(List<ContactDTO> contacts, String contactType, String contactValue) {
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

	private void processAddRole(List<RoleDTO> roles, int roleId) {
		RoleDTO role = new RoleDTO();
		try {
			role = EmployeeManager.getRole(roleId);
		} catch(Exception ex) {
			logMsgs.add(new LogMsg(EmployeeManager.getLogMsg(), "red"));
		}
		roles.add(role);
	}

	private void processDeleteContact(List<ContactDTO> contacts, int index) {
		if(contacts.size()==1) {
			logMsgs.add(new LogMsg("Employee must have atleast one Contact!", "red"));
			return;
		}
		contacts.remove(index);
	}
}