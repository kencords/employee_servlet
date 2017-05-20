package ecc.cords;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils{

	public static String formatDate(Date date) {
		return new SimpleDateFormat("MMMM dd yyyy").format(date);
	}

	public static String formatDateSimplified(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	public static Date convertToDate(String date) throws Exception{
		return new SimpleDateFormat("yyyy-MM-dd").parse(date);
	}

	public static boolean isValidDate(String date) {
		Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

		Matcher matcher = pattern.matcher(date);
		if(!matcher.matches())
			System.out.println("Invalid Date!");
		return matcher.matches();
	}

	public static boolean isValidEmail(String email){
		Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

		Matcher matcher = pattern.matcher(email);
		if(!matcher.matches())
			System.out.println("Invalid Email!");
		return matcher.matches();
	}

	public static boolean isValidLandline(String landline){
		Pattern pattern = Pattern.compile("\\d{3}-\\d{4}");

		Matcher matcher = pattern.matcher(landline);
		if(!matcher.matches())
			System.out.println("Invalid Landline!");
		return matcher.matches();
	}

	public static boolean isValidMobile(String mobile){
		Pattern pattern = Pattern.compile("\\d{4}-\\d{3}-\\d{4}");

		Matcher matcher = pattern.matcher(mobile);
		if(!matcher.matches())
			System.out.println("Invalid Mobile!");
		return matcher.matches();
	}
}