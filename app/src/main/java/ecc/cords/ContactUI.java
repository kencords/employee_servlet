package ecc.cords;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ContactUI {

	private static ContactUI contactUI = null;
	private static DTO_EntityMapper mapper = new DTO_EntityMapper();
	private static String logMsg = "";

	public void manageContact(EmployeeDTO employee, boolean toDelete) throws Exception {
		System.out.println("\nCONTACT DETAILS:");
		List<ContactDTO> contacts = new ArrayList<>(employee.getContacts().stream()
																		  .sorted((c1,c2) -> (c1.getContactType()+c1.getContactValue()).compareTo(c2.getContactType()+c2.getContactValue()))
																		  .collect(Collectors.toList()));
		for(int i=0; i < contacts.size(); i++) {
			System.out.println("["+ (i+1) + "][" + contacts.get(i).getContactType() + ": " + contacts.get(i).getContactValue() + "]");
		}
		int id = 0;
		do{
			id = InputHelper.askPositiveNumber("What contact detail to update/delete? (Enter Number): ", false);
			if(id == 0 || id>contacts.size())
				System.out.println("Invalid Contact Index!");
		} while(id == 0 || id>contacts.size());
		ContactDTO contact = contacts.get(id-1); 
		String value = "";
		if(!toDelete) {
			if(contact.getContactType().equals("Landline"))
				contact.setContactValue(InputHelper.askLandline("Enter Landline (xxx-xxxx): "));
			else if(contact.getContactType().equals("Mobile"))
				contact.setContactValue(InputHelper.askMobile("Enter Mobile (xxxx-xxx-xxxx): "));
			else if(contact.getContactType().equals("Email"))
				contact.setContactValue(InputHelper.askEmail("Enter Email: "));
			return;
		}
		EmployeeManager.deleteContact(employee,contact);
	}

	public Set<ContactDTO> askContacts(boolean isNew) {
		Set<ContactDTO> contacts = new HashSet<>(); 
		while(true) {
			System.out.println("\nWHAT TYPE OF CONTACT?");
			System.out.println("1. LANDLINE");
			System.out.println("2. MOBILE");
			System.out.println("3. EMAIL");
			System.out.println("4. DONE");
			String choice = InputHelper.askChoice("What contact do you want to add? (Enter Choice Number): ");
			switch(choice){
				case "1":
					contacts.add(mapper.createContactDTO("Landline",InputHelper.askLandline("Enter Landline (xxx-xxxx): ")));
					break;
				case "2":
					contacts.add(mapper.createContactDTO("Mobile",InputHelper.askMobile("Enter Mobile (xxxx-xxx-xxxx): ")));
					break;
				case "3":
					contacts.add(mapper.createContactDTO("Email", InputHelper.askEmail("Enter Email: ")));
					break;
				case "4":
					if(contacts.size()==0 && isNew){
						System.out.println("Add at least one contact!");
						continue;
					}
					return contacts;
				default:
					System.out.println("Invalid Choice!");
			}
		}
	}

	public static ContactUI getInstance() {
		if(contactUI == null){
			contactUI = new ContactUI();
		}
		return contactUI;
	}
}