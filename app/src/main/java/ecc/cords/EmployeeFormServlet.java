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
	private static DaoService daoService = new DaoService();
	private static DTO_EntityMapper mapper = new DTO_EntityMapper();
	private FormValidator validator = new FormValidator();

	private final String[] contactOptions = {"Landline (xxx-xxxx)", "Mobile (xxxx-xxx-xxxx)", "Email"};
	private final String[] hiredOptions = {"YES", "NO"};
	private List<ContactDTO> contacts = new ArrayList<>();
	private List<RoleDTO> availRoles = new ArrayList<>();
	private List<RoleDTO> roles = new ArrayList<>();

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		doPost(req,res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

		res.setContentType("text/html");
		PrintWriter out = res.getWriter();

		if (req.getParameter("addEmployeeBtn") != null) {
			validator.saveEmployeeIfValid(logMsgs, contacts, roles, req, res);
		}

		if(req.getParameter("cancelBtn") != null) {
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

		out.println(Template.getHeader("Employee Records System: Employee Form"));
		out.println("<h1>ADD EMPLOYEE</h1>");

		out.println(Template.createForm("employeeForm", "POST",
			"<div style=\"border:1px solid black; width:500px; margin: 0 auto; text-align:center;\">" +
			Template.createLogMsg(logMsgs) + "<br>" +
			Template.createEmphasizedText("PERSONAL INFORMATION") +
			Template.createTextField("title", "", "Title") + "<br>" +
			Template.createTextField("lName", "", "Last Name*") + "<br>" +
			Template.createTextField("fName", "", "First Name*") + "<br>" +
			Template.createTextField("mName", "", "Middle Name*") + "<br>" +
			Template.createTextField("suffix", "", "Suffix") + "<br>" +
			Template.createTextField("bDate", "", "Birth Date (yyyy-mm-dd)*") + "<br>" +
			Template.createTextField("gwa", "", "GWA") + "<br>" +
			Template.createEmphasizedText("ADDRESS") +
			Template.createTextField("strNo", "", "Street Number*") + "<br>" +
			Template.createTextField("street", "", "Street*") + "<br>" +
			Template.createTextField("brgy", "", "Brgy*") + "<br>" +
			Template.createTextField("city", "", "City*") + "<br>" +
			Template.createTextField("zipcode", "", "Zipcode*") + "<br>" +
			Template.createEmphasizedText("CONTACTS") +
			askContacts() +
			Template.createEmphasizedText("CAREER INFORMATION") +
			"<p style=\"display:inline-block;\">Currently Hired? &nbsp;</p>" +  
			Template.createDropDown(hiredOptions, "hireOpt", false) + "<br>" +
			Template.createTextField("hDate", "", "Hire Date (yyyy-mm-dd)*") + "<br>" +
			Template.createEmphasizedText("ROLES") +
			askRoles() + "<br><br>" +
			Template.createSubmitBtn("addEmployeeBtn", "", "ADD EMPLOYEE") +	
			Template.createSubmitBtn("cancelBtn", "", "CANCEL") + "<br>" +
			"<br></div>"
		));

		out.println(Template.getClosing());
		out.close();
		logMsgs.clear();
	}

	public void setContacts(Set<ContactDTO> contactSet) {
		contacts = new ArrayList<ContactDTO>(contactSet);
	}

	private String askContacts() {
		StringBuilder sb = new StringBuilder();
		if(contacts.size() > 0) {
			for(int i=0; i<contacts.size(); i++) {
				ContactDTO contact = contacts.get(i);
				sb.append(Template.createReadOnlyTextField(contact.getContactType() + "" + i, contact.getContactType()));
				sb.append(Template.createReadOnlyTextField(contact.getContactValue() + "" + i, contact.getContactValue()));
				sb.append(Template.createSubmitBtn("delConBtn", i + "", "DELETE"));
				sb.append("<br>");
			}
		}
		sb.append(Template.createDropDown(contactOptions, "conOpt", false));
		sb.append(Template.createTextField("contact", "", "Enter Contact"));
		sb.append(Template.createSubmitBtn("addContactBtn", "", "ADD"));
		sb.append("<br>");
		return sb.toString();
	}

	private String askRoles() {
		StringBuilder sb = new StringBuilder();
		if(roles.size() > 0) {
			for(int i=0; i<roles.size(); i++) {
				RoleDTO role = roles.get(i);
				sb.append(Template.createReadOnlyTextField(role.getRoleName() + "" + i, role.getRoleName()));
				sb.append(Template.createSubmitBtn("delRoleBtn", i + "", "DELETE"));
				sb.append("<br>");
			}
		}
		availRoles = mapper.getAllRoles()
						   .stream()
						   .filter(role -> !roles.contains(role))
		  			 	   .sorted((role1,role2) -> Long.compare(role1.getRoleId(), role2.getRoleId()))
			   		  	   .collect(Collectors.toList());
		if(availRoles.size()>0) {
			String[] roleOption = new String[availRoles.size()];
			for(int i=0; i<availRoles.size(); i++) {
				roleOption[i] = availRoles.get(i).getRoleId() + "," + availRoles.get(i).getRoleName();
			}	
			sb.append(Template.createDropDown(roleOption, "roleOpt", true));
			sb.append(Template.createSubmitBtn("addRoleBtn", "", "ADD"));
		}
		return sb.toString();
	}

	private void processAddContact(String contactType, String contactValue) {
		if(contactType.equals(contactOptions[0]) && !Utils.isValidLandline(contactValue)){
			logMsgs.add(new LogMsg("Invalid Landline!", "red"));
			return;
		}

		else if(contactType.equals(contactOptions[1]) && !Utils.isValidMobile(contactValue)){
			logMsgs.add(new LogMsg("Invalid Mobile!", "red"));
			return;
		}

		else if(contactType.equals(contactOptions[2]) && !Utils.isValidEmail(contactValue)){
			logMsgs.add(new LogMsg("Invalid Email!", "red"));
			return;
		}
		contacts.add(new ContactDTO((contactType.equals(contactOptions[0])? "Landline" : 
		(contactType.equals(contactOptions[1])? "Mobile" : "Email"))  , contactValue));
	}

	private void processAddRole(int roleId) {
		RoleDTO role = new RoleDTO();
		try{
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