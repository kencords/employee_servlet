package ecc.cords;

import java.util.ArrayList;
import java.util.List;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class HomeServlet extends HttpServlet {

	private final String[] empTableHdrs = {"ID", "Name", "GWA", "Hire Date", "Action"};
	private final String[] sortOptions = {"GWA", "LastName", "Hire Date"};
	private static DaoService daoService = new DaoService();
	private static DTO_EntityMapper mapper = new DTO_EntityMapper();
	private String order;

	private List<LogMsg> logMsgs = new ArrayList<>();

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		doPost(req,res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		order = "";
		req.getSession().invalidate();
		handleEvents(req, res);
		out.println(Template.getHeader("Employee Records System: Home Page"));
		out.println("<h1>EMPLOYEE RECORDS SYSTEM</h1>");
		out.println(Template.createLogMsg(logMsgs));
		out.println(Template.createForm("home", "GET" ,
			Template.createDiv("right", 
				Template.createDropDown(sortOptions, "sort", false) +
				Template.createSubmitBtn("sortBtn", "", "Sort") +
				Template.createSubmitBtn("addEmpBtn", "", "Add Employee") +
				Template.createSubmitBtn("roleBtn", "", "Manage Roles") +
				displayEmployees(order)
		)));
		out.println(Template.getClosing());
		out.close();
		logMsgs.clear();
	}

	private String displayEmployees(String order) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table style=\"width:100%\" border=\"1\">");
		sb.append("<tr>");
		for(String header : empTableHdrs) {
			sb.append("<th align=\"center\">" + header + "</th>");
		}
		sb.append("</tr>");
		String dbOrder = "name.lastName";
		if(order!=null && !order.equals("")) {
			dbOrder = order.equals("GWA")? "gwa" : (order.equals("LastName")? "name.lastName" : "hireDate");
		}
		List<EmployeeDTO> empList = mapper.mapSimplifiedEmployees(dbOrder);
		for(EmployeeDTO employee : empList) {
			sb.append("<tr>");
			sb.append("<td align=\"center\">" + employee.getEmpId() + "</td>");
			sb.append("<td align=\"center\">" + employee.getLastName() + ", " + employee.getFirstName() + " " +
			employee.getMiddleName() + " " + employee.getSuffix() + "</td>");
			sb.append("<td align=\"center\">" + employee.getGwa() + "</td>");
			sb.append("<td align=\"center\">" + employee.getHireDate() + "</td>");
			sb.append("<td align=\"center\">" + 
				Template.createSubmitBtn("viewEmpBtn", employee.getEmpId() + "",  "View") +
				Template.createSubmitBtn("delEmpBtn", employee.getEmpId() + "",  "Delete") +
			"</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}

	private void handleEvents(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		if(req.getParameter("viewEmpBtn") != null) {
			redirect(res, "employeeProfile?empId=" + req.getParameter("viewEmpBtn"));
		}
		if(req.getParameter("delEmpBtn") != null) {
			logMsgs.add(EmployeeManager.deleteEmployee(Integer.parseInt(req.getParameter("delEmpBtn"))));
		}
		if(req.getParameter("sortBtn") != null) {
			order = req.getParameter("sort");
			logMsgs.add(new LogMsg("Sorted By " + order, "green"));
		}
		if(req.getParameter("addEmpBtn")!= null) {
			redirect(res, "employeeForm");
		}
		if(req.getParameter("roleBtn") != null) {
			logMsgs.clear();
			redirect(res, "roles");
		}
	}

	private void redirect(HttpServletResponse res, String dest) throws IOException, ServletException {
		logMsgs.clear();
		res.sendRedirect(dest);
	}
}