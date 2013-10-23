package edu.cwru.eecs.futil.misc;

import java.util.Vector;

public class StringMisc {

	/**
	 * This is used to check if the input string corresponds to an integer
	 * @param str
	 * @return true if given string is a valid integer values.
	 */
	public static boolean isInteger(String str)
	{
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e){
			return false;
		}
		return true;
		//Pattern pattern = Pattern.compile("^[-\\+]?[\\d]+$");   
		//return pattern.matcher(str).matches(); 
	}

	/**
	 * This is used to check if the input string corresponds to a double
	 * @param str
	 * @return true if the given string is a valid double value.
	 */
	public static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
		} catch (NumberFormatException e){
			return false;
		}
		return true;
		//Pattern pattern = Pattern.compile("^[-+]?(\\d+[.]\\d*?|[.]\\d+?|\\d+?)([eE][-+]?\\d+)?$"); 
		//return pattern.matcher(str).matches();   
	}
	
	/**
	 * convert a string to integer
	 * @param str
	 * @return integer corresponds to a string
	 */
	public static int strToInt(String str)
	{
		return Integer.parseInt(str);
	}
	/**
	 * convert a string to double
	 * @param str
	 * @return double value corresponds to a string
	 */
	public static double strToDouble(String str)
	{
		return Double.parseDouble(str);
	}
	
	
	/**
	 * Note: this method is from Weka
	 * Rounds a double and converts it into String.
	 *
	 * @param value the double value
	 * @param afterDecimalPoint the (maximum) number of digits permitted
	 * after the decimal point
	 * @return the double as a formatted string
	 */
	public static /*@pure@*/ String doubleToString(double value, int afterDecimalPoint) {

		StringBuffer stringBuffer;
		double temp;
		int dotPosition;
		long precisionValue;

		temp = value * Math.pow(10.0, afterDecimalPoint);
		if (Math.abs(temp) < Long.MAX_VALUE) {
			precisionValue = 	(temp > 0) ? (long)(temp + 0.5) 
					: -(long)(Math.abs(temp) + 0.5);
			if (precisionValue == 0) {
				stringBuffer = new StringBuffer(String.valueOf(0));
			} else {
				stringBuffer = new StringBuffer(String.valueOf(precisionValue));
			}
			if (afterDecimalPoint == 0) {
				return stringBuffer.toString();
			}
			dotPosition = stringBuffer.length() - afterDecimalPoint;
			while (((precisionValue < 0) && (dotPosition < 1)) ||
					(dotPosition < 0)) {
				if (precisionValue < 0) {
					stringBuffer.insert(1, '0');
				} else {
					stringBuffer.insert(0, '0');
				}
				dotPosition++;
			}
			stringBuffer.insert(dotPosition, '.');
			if ((precisionValue < 0) && (stringBuffer.charAt(1) == '.')) {
				stringBuffer.insert(1, '0');
			} else if (stringBuffer.charAt(0) == '.') {
				stringBuffer.insert(0, '0');
			}
			int currentPos = stringBuffer.length() - 1;
			while ((currentPos > dotPosition) &&
					(stringBuffer.charAt(currentPos) == '0')) {
				stringBuffer.setCharAt(currentPos--, ' ');
			}
			if (stringBuffer.charAt(currentPos) == '.') {
				stringBuffer.setCharAt(currentPos, ' ');
			}

			return stringBuffer.toString().trim();
		}
		return new String("" + value);
	}
	
	public static String[] vectorToStrArray(Vector<String> vec) {
		String[] array = new String[vec.size()];
		for(int i=0; i<vec.size(); i++) 
			array[i] = vec.get(i);
		return array;
	}
}
