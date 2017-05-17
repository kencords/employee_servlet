package ecc.cords;

import java.util.List;
import java.util.stream.Collectors;

public class Template {

	public static String getHeader(String title) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head><title>" + title + "</title></head>");
		sb.append("<body>");
		return sb.toString();
	}

	public static String getClosing() {
		StringBuilder sb = new StringBuilder();
		sb.append("</body>");
		sb.append("</html>");
		return sb.toString();
	}

	public static String createDiv(String align, String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div align=\"" + align +"\">");
		sb.append(content);
		sb.append("</div>");
		return sb.toString();
	}

	public static String createDropDown(String[] options, String name) {
		StringBuilder sb = new StringBuilder();
		sb.append("<br><select name=\"" + name + "\">\n");
		for(String option : options) {
			sb.append("<option value= \"" + option + "\">\n\t" + option + "\n</option>");
		}
		sb.append("</select>");
		return sb.toString();
	}

	public static String createForm(String action, String method, String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("<form action=\"" + action + "\" method=\"" + method + "\">\n");
		sb.append(content);
		sb.append("</form>");
		return sb.toString();
	}

	public static String createLogMsg(List<LogMsg> msgs) {
		StringBuilder sb = new StringBuilder();
		msgs = msgs.stream()
				   .sorted((s1,s2) -> s1.getLogMsg().compareTo(s2.getLogMsg()))
				   .collect(Collectors.toList());
		for(LogMsg msg : msgs) {
			sb.append("<p style=\"color:" + msg.getColor() + ";\">" + msg.getLogMsg() + "</p>\n");
		}
		return sb.toString();
	}

	public static String createSubmitBtn(String name, String value, String display) {
		return "<button style=\"display:inline-block;\" type = \"submit\" " + "value= \"" + value + "\" name=\"" + name + "\">" + display
		+ "</button>";  
	}

}