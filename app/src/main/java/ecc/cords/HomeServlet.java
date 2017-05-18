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

	private List<LogMsg> logMsgs = new ArrayList<>();

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		doPost(req,res);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		String order = "";

		if(req.getParameter("delEmpBtn") != null) {
			processDeleteEmployee(Integer.parseInt(req.getParameter("delEmpBtn")));
		}

		if(req.getParameter("sortBtn") != null) {
			order = req.getParameter("sort");
			logMsgs.add(new LogMsg("Sorted By " + order, "green"));
		}

		if(req.getParameter("addEmpBtn")!= null) {
			res.sendRedirect("employeeForm");
		}

		if(req.getParameter("roleBtn") != null) {
			res.sendRedirect("roles");
		}

		out.println(Template.getHeader("Employee Records System: Home Page"));
		out.println("<h1>EMPLOYEE RECORDS SYSTEM</h1>");
		out.println(Template.createLogMsg(logMsgs));
		out.println(Template.createForm("home", "POST" ,
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
				Template.createSubmitBtn("editEmpBtn", employee.getEmpId() + "",  "Edit") +
				Template.createSubmitBtn("delEmpBtn", employee.getEmpId() + "",  "Delete") +
			"</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}

	private void processDeleteEmployee(int empId) {
		try {
			daoService.deleteElement(EmployeeManager.getEmployee(empId));
		} catch(Exception ex) {
			System.out.println("EmpID: " + empId);
			logMsgs.add(new LogMsg(EmployeeManager.getLogMsg(), "red"));
			return;
		}
		logMsgs.add(new LogMsg("Deleted Employee " + empId + "!", "green"));
	}
}