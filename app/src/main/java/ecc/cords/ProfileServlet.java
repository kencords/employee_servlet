package ecc.cords;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class ProfileServlet extends HttpServlet {

	private static DTO_EntityMapper mapper = new DTO_EntityMapper();

	private List<LogMsg> logMsgs = new ArrayList<>();

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		handleEvents(req, res);

		EmployeeDTO employee = new EmployeeDTO();
		if(req.getParameter("empId")==null || req.getParameter("empId").equals("")) {
			res.sendError(404,"Employee not specified!");
		}
		else {
			try {
				employee = mapper.mapToEmployeeDTO(EmployeeManager.getEmployee(Integer.parseInt(req.getParameter("empId"))));
			} catch(Exception ex) {
				res.sendError(404,"Employee not found!");
			}
		}
		out.println(Template.getHeader("Employee Records System: Employee Profile"));
		out.println("<h1>EMPLOYEE PROFILE</h1>");
		out.println(Template.createLogMsg(logMsgs));
		out.println(Template.createForm("employeeProfile", "GET", 
			Template.createDiv("left",
				Template.createSubmitBtn("editEmpBtn", req.getParameter("empId"), "Edit Employee") + "\n" +
				Template.createSubmitBtn("backBtn", "", "Back") + "\n" +
				Template.createDiv("left",
					showEmployeeDetails(employee)
			)) 
		));	
		out.println(Template.getClosing());
		out.close();
		logMsgs.clear();
	}
	
	private void handleEvents(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		if(req.getParameter("editEmpBtn") != null) {
			logMsgs.clear();
			res.sendRedirect("editEmployee?empId=" + req.getParameter("editEmpBtn"));
		}
		if(req.getParameter("backBtn") != null) {
			logMsgs.clear();
			res.sendRedirect("home");
		}
	}

	private String showEmployeeDetails(EmployeeDTO employee) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n<br><b>NAME:</b>" +  " " + (employee.getTitle().equals("")? "" : employee.getTitle() + " ") 
		+ employee.getFirstName() + " " + employee.getMiddleName() + ", " + employee.getLastName() + " " +
		employee.getSuffix());
		sb.append("\n<br><b>ADDRESS:</b> " + employee.getAddress());
		sb.append("\n<br><b>BIRTHDATE:</b> " + Utils.formatDate(employee.getBirthDate()));
		sb.append("\n<br><b>GWA:</b> " + employee.getGwa());
		String curHired = employee.isCurrentlyHired() ? "YES" : "NO";
		sb.append("\n<br><b>CURRENTLY HIRED:</b> " + curHired);
		sb.append("\n<br><b>DATE HIRED:</b> " + Utils.formatDate(employee.getHireDate()));
		sb.append("\n<br><br><b>CONTACTS:</b><br>");
		List<ContactDTO> contacts = new ArrayList<>(employee.getContacts().stream()
																		  .sorted((c1,c2) -> (c1.getContactType()+c1.getContactValue()).compareTo(c2.getContactType()+c2.getContactValue()))
																		  .collect(Collectors.toList()));
		for(int i=0; i < contacts.size(); i++) {							
			sb.append(contacts.get(i).getContactType() + ": " + contacts.get(i).getContactValue() + "<br>");
		}
		sb.append("\n<br><b>ROLES:</b><br> " + employee.getRoles().stream()
												   .sorted((role1, role2) -> Long.compare(role1.getRoleId(), role2.getRoleId()))
												   .collect(Collectors.toList())  + "\n");
		return sb.toString();
	}
}