package ecc.cords;

import java.util.List;
import java.util.stream.Collectors;

public class Template {

	public static String getHeader(String title) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>\n");
		sb.append("<head><title>" + title + "</title></head>\n");
		sb.append("<body>\n");
		return sb.toString();
	}

	public static String getClosing() {
		StringBuilder sb = new StringBuilder();
		sb.append("</body>\n");
		sb.append("</html>");
		return sb.toString();
	}

	public static String createDiv(String align, String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div align=\"" + align +"\">");
		sb.append(content);
		sb.append("</div>\n");
		return sb.toString();
	}

	public static String createDropDown(String[] options, String name, boolean isDifVal) {
		StringBuilder sb = new StringBuilder();
		sb.append("<select name=\"" + name + "\">\n");
		for(String option : options) {
			sb.append("<option value= \"" + (isDifVal? option.split(",")[0] :option) + "\">\n\t" + 
			(isDifVal? option.split(",")[1] : option) + "\n</option>");
		}
		sb.append("</select>\n");
		return sb.toString();
	}

	public static String createSelectedDropDown(String[] options, String name, String selected) {
		StringBuilder sb = new StringBuilder();
		sb.append("<select name=\"" + name + "\">\n");
		for(String option : options) {
			sb.append("<option " + (option.equals(selected)? "selected" : "") + " value= \"" + option + "\">\n\t" + 
			option + "\n</option>");
		}
		sb.append("</select>\n");
		return sb.toString();
	}

	public static String createForm(String action, String method, String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("<form action=\"" + action + "\" method=\"" + method + "\">\n");
		sb.append(content);
		sb.append("</form>\n");
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

	public static String createEmphasizedText(String msg) {
		return "<h3>" + msg + "</h3>\n";
	}	

	public static String createSubmitBtn(String name, String value, String display) {
		return "<button style=\"display:inline-block;\" type = \"submit\" " + "value= \"" + value + "\" name=\"" + name + "\">" + display
		+ "</button>";  
	}

	public static String createReadOnlyTextField(String name, String value) {
		return "<input type=text name=\"" + name + "\" value =\"" + value +  "\" readonly/>\n";
	}

	public static String createTextField(String name, String value, String placeholder) {
		return "<input type=text name=\"" + name + "\" value =\"" + value +  "\" placeholder = \"" + placeholder + "\"/>\n";
	}

}