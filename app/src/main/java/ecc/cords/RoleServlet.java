package ecc.cords;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class RoleServlet extends HttpServlet {

	private final String[] roleTableHdrs = {"ID", "Role Name", "Action"};
	private final String[] ownerHdrs = {"ID", "Employee Name"};
	private static DaoService daoService = new DaoService();
	private static DTO_EntityMapper mapper = new DTO_EntityMapper();

	private List<LogMsg> logMsgs = new ArrayList<>();

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		doPost(req,res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

		res.setContentType("text/html");
		PrintWriter out = res.getWriter();

		if(req.getParameter("cancel") != null) {
			logMsgs.add(new LogMsg(req.getParameter("cancel"), "red"));
		}

		if(req.getParameter("homeBtn") != null) {
			res.sendRedirect("home");
		}

		if(req.getParameter("addNowBtn") != null) {
			processAddRole(req.getParameter("role_name").trim());
		}

		if(req.getParameter("editNowBtn") != null) {
			processEditRole(Integer.parseInt(req.getParameter("editNowBtn")), req.getParameter("role_name_ed"));
		}

		if(req.getParameter("delRoleBtn") != null) {
			processDeleteRole(Integer.parseInt(req.getParameter("delRoleBtn")));
		}

		out.println(Template.getHeader("Employee Records System: Roles Page"));
		out.println("<h1>MANAGE ROLES</h1>");
		out.println(Template.createLogMsg(logMsgs));
		out.println(Template.createForm("roles", "POST", 
			Template.createDiv("left",
				Template.createSubmitBtn("addRoleBtn", "", "Add Role") + "\n" +
				Template.createSubmitBtn("homeBtn", "", "Manage Employees") + "\n"
			) + displayRoles()
			+ (req.getParameter("addRoleBtn") != null ? createAddRoleFields() : "" )
			+ (req.getParameter("editRoleBtn") != null? createEditRoleFields(Integer.parseInt(req.getParameter("editRoleBtn"))) : "")
		));

		if(req.getParameter("showOwnerBtn") !=null) {
			out.println(displayRoleOwners(Integer.parseInt(req.getParameter("showOwnerBtn"))));
		}

		out.println(Template.getClosing());
		out.close();
		logMsgs.clear();
	}

	private String displayRoles() {
		StringBuilder sb = new StringBuilder();
		sb.append("<table border=\"1\">");
		sb.append("<tr>");
		for(String header : roleTableHdrs) {
			sb.append("<th align=\"center\">" + header + "</th>");
		}
		sb.append("</tr>");
		List<RoleDTO> roleList = mapper.getAllRoles().stream()
													 .sorted((role1,role2) -> Long.compare(role1.getRoleId(), role2.getRoleId()))
													 .collect(Collectors.toList());
		for(RoleDTO role : roleList) {
			sb.append("<tr>");
			sb.append("<td align=\"center\">" + role.getRoleId() + "</td>");
			sb.append("<td align=\"center\">" + role.getRoleName() + "</td>");
			sb.append("<td align=\"center\">" + 
				Template.createSubmitBtn("editRoleBtn", role.getRoleId() + "",  "Edit") +
				Template.createSubmitBtn("delRoleBtn", role.getRoleId() + "",  "Delete") +
				Template.createSubmitBtn("showOwnerBtn", role.getRoleId() + "", "Show Owner") +
			"</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}

	private String displayRoleOwners(int roleId) {
		RoleDTO role = new RoleDTO();
		try{
			role = EmployeeManager.getRole(roleId);
		} catch(Exception ex) {
			logMsgs.add(new LogMsg(EmployeeManager.getLogMsg(), "red"));
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<table border=\"1\">");
		sb.append("<tr>");
		for(String header : ownerHdrs) {
			sb.append("<th align=\"center\">" + header + "</th>");
		}
		sb.append("</tr>");
		Set<EmployeeDTO> employees = role.getEmployees();
		if(employees.size()==0) {
			return "<p>Role " + role.getRoleName() + " has no owner</p>";
		}
		sb.append("<p>Role " + role.getRoleName() + " is used by:</p>");
		for(EmployeeDTO employee : employees) {
			sb.append("<tr>");
			sb.append("<td align=\"center\">" + employee.getEmpId() + "</td>");
			sb.append("<td align=\"center\">" + employee.getLastName() + ", " + employee.getFirstName() + " " +
			employee.getMiddleName() + " " + employee.getSuffix() + "</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}

	private void processAddRole(String roleName) {
		if(roleName.equals("")) {
			logMsgs.add(new LogMsg("Role Name must not be Empty!", "red"));
			return;
		}
		logMsgs.add(EmployeeManager.createRole(roleName.toUpperCase()));
	}

	private void processEditRole(int roleId, String roleName) {
		if(roleName.equals("")) {
			logMsgs.add(new LogMsg("Role Name must not be Empty!", "red"));
			return;
		}
		RoleDTO role = new RoleDTO();
		try {
			role = EmployeeManager.getRole(roleId);
			logMsgs.add(EmployeeManager.updateRole(role, roleName));
		} catch(Exception ex) {
			logMsgs.add(new LogMsg(EmployeeManager.getLogMsg(), "red"));
			logMsgs.add(new LogMsg("Role is currently used by: \n" + getRoleOwners(role), "red"));
		}
	}

	private void processDeleteRole(int roleId) {
		RoleDTO role = new RoleDTO();
		try {
			role = EmployeeManager.getRole(roleId);
			logMsgs.add(new LogMsg(EmployeeManager.deleteRole(role), "green"));
		} catch(Exception ex) {
			logMsgs.add(new LogMsg(EmployeeManager.getLogMsg(), "red"));
			logMsgs.add(new LogMsg("Role is currently used by: \n" + getRoleOwners(role), "red"));
		}
	}

	private String createAddRoleFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("<br>\n");
		sb.append(Template.createTextField("role_name", "", "Role Name"));
		sb.append(Template.createSubmitBtn("addNowBtn", "","Add"));
		sb.append(Template.createSubmitBtn("cancel", "Add Role Cancelled","Cancel"));
		return sb.toString();
	}

	private String createEditRoleFields(int roleId) {
		StringBuilder sb = new StringBuilder();
		RoleDTO role = new RoleDTO();
		try {
			role = EmployeeManager.getRole(roleId);
		} catch(Exception ex) {
			logMsgs.add(new LogMsg(EmployeeManager.getLogMsg(), "red"));
		}
		sb.append("<br>" + "Edit Role " + role.getRoleName() + "<br>\n");
		sb.append(Template.createTextField("role_name_ed", "",  "New Role Name"));
		sb.append(Template.createSubmitBtn("editNowBtn", roleId + "","Save"));
		sb.append(Template.createSubmitBtn("cancel", "Edit Role Cancelled","Cancel"));
		return sb.toString();
	}

	private String getRoleOwners(RoleDTO role) {
		StringBuilder sb = new StringBuilder();
		role.getEmployees().stream()
						   .sorted((emp1,emp2) -> Long.compare(emp1.getEmpId(), emp2.getEmpId()))
		                   .forEach(employee -> sb.append("<br>" + employee));
		return sb.toString();
	}
}