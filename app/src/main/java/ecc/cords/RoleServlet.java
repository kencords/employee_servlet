package ecc.cords;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class RoleServlet extends HttpServlet {

	private final String[] roleTableHdrs = {"ID", "Role Name", "Action"};
	private static DaoService daoService = new DaoService();
	private static DTO_EntityMapper mapper = new DTO_EntityMapper();

	private List<LogMsg> logMsgs = new ArrayList<>();

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		doPost(req,res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

		res.setContentType("text/html");
		PrintWriter out = res.getWriter();

		if(req.getParameter("delRoleBtn") != null) {
			processDeleteRole(Integer.parseInt(req.getParameter("delRoleBtn")));
		}

		out.println(Template.getHeader("Employee Records System: Roles Page"));
		out.println("<h1>MANAGE ROLES</h1>");
		out.println(Template.createLogMsg(logMsgs));
		out.println(Template.createForm("roles", "POST", 
			Template.createDiv("left",
				Template.createSubmitBtn("addRoleBtn", "", "Add Role")
			) + displayRoles()
		));
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
			"</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}

	public void processDeleteRole(int roleId) {
		RoleDTO role = new RoleDTO();
		try {
			role = EmployeeManager.getRole(roleId);
			logMsgs.add(new LogMsg(EmployeeManager.deleteRole(role), "green"));
		} catch(Exception ex) {
			logMsgs.add(new LogMsg(EmployeeManager.getLogMsg(), "red"));
			logMsgs.add(new LogMsg("Role is currently used by: \n" + getRoleOwners(role), "red"));
		}
	}

	private String getRoleOwners(RoleDTO role) {
		StringBuilder sb = new StringBuilder();
		role.getEmployees().stream()
						   .sorted((emp1,emp2) -> Long.compare(emp1.getEmpId(), emp2.getEmpId()))
		                   .forEach(employee -> sb.append("<br>" + employee));
		return sb.toString();
	}
}