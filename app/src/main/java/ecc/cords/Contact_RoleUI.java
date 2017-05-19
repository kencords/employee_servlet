package ecc.cords;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Contact_RoleUI {

	private static DTO_EntityMapper mapper = new DTO_EntityMapper();
	private static List<RoleDTO> availRoles = new ArrayList<>();

	public static final String[] contactOptions = {"Landline (xxx-xxxx)", "Mobile (xxxx-xxx-xxxx)", "Email"};

	public static String askContacts(List<ContactDTO> contacts, boolean isEdit) {
		StringBuilder sb = new StringBuilder();
		if(contacts.size() > 0) {
			for(int i=0; i<contacts.size(); i++) {
				ContactDTO contact = contacts.get(i);
				sb.append(Template.createReadOnlyTextField(contact.getContactType() + "" + i, contact.getContactType()));
				if(!isEdit) {
					sb.append(Template.createReadOnlyTextField(contact.getContactValue() + "" + i, contact.getContactValue()));
				}
				else {
					sb.append(Template.createTextField("contact" + i, contact.getContactValue(), "Enter Contact"));
					sb.append(Template.createSubmitBtn("updateConBtn", i + "", "UPDATE"));
				}
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

	public static String askRoles(List<RoleDTO> roles) {
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
}