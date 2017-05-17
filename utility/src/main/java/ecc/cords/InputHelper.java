package ecc.cords;

import java.util.Scanner;
import java.util.Date;
import java.text.SimpleDateFormat;

public class InputHelper{

	private static Scanner input = new Scanner(System.in);

	private InputHelper(){}

	public static boolean askBoolean(String msg) {
		String bool = "";
		do {
			System.out.print(msg);
			bool = input.nextLine().toUpperCase();
		} while(!bool.toUpperCase().matches("Y|N"));
		return bool.equals("Y");
	}

	public static String askChoice(String msg) {
		System.out.print(msg);
		return input.nextLine();
	}

	public static Date askDate(String msg) throws Exception {
		String date;
		do {
			System.out.print(msg);
			date = input.nextLine();
		} while(!Utils.isValidDate(date));
		return new SimpleDateFormat("yyyy-MM-dd").parse(date);
	}

	public static String askString(String msg, boolean isOptional) {
		String str = "";
		do {
			System.out.print(msg);
			str = input.nextLine().trim();
		} while((str == null || str.isEmpty()) && !isOptional);
		return str;
	}

	public static String askLandline(String msg) {
		String str = "";
		do {
			System.out.print(msg);
			str = input.nextLine();
			if(str.equals(""))
				break;
		} while(!Utils.isValidLandline(str));
		return str;
	}

	public static String askMobile(String msg) {
		String str = "";
		do {
			System.out.print(msg);
			str = input.nextLine();
			if(str.equals(""))
				break;
		} while(!Utils.isValidMobile(str));
		return str;
	}

	public static String askEmail(String msg) {
		String str = "";
		do {
			System.out.print(msg);
			str = input.nextLine();
		} while(!Utils.isValidEmail(str));
		return str;
	}

	public static float askPositiveFloat(String msg, boolean isOptional) {
		String tmp = null;
		float val = 0;
		
		do {
			try {
				System.out.print(msg);
				tmp = input.nextLine();
				val = (Float.parseFloat(tmp) >= 0) ? Float.parseFloat(tmp) : 0;

			} catch(Exception ex){
				System.err.println("Not valid float");
				tmp = "";
			}
		}while((tmp == null || tmp.isEmpty()) && !isOptional);		
		return val;
	}

	public static int askPositiveNumber(String msg, boolean isOptional) {
		String tmp;
		int val = 0;
		do {
			System.out.print(msg);
			tmp = input.nextLine();
			
			if(isNumeric(tmp) && !tmp.isEmpty() && Integer.parseInt(tmp) >= 0) {
				val = Integer.parseInt(tmp);
			}
			else {
				System.out.println("Invalid Input!");
				tmp = "";
			}
		} while((tmp== null || tmp.isEmpty()) && !isOptional);		
		return val;
	}
	
	public static boolean isNumeric(String str) { 
		try {
			int val = Integer.parseInt(str);
			return true;
		} catch(NumberFormatException ex) {
			return false;
		}
	}

}