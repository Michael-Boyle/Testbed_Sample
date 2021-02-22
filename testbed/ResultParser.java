package testbed;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * parser for a little markup language that represents the Results of functions
 * 
 * <p>
 * 
 * The declarations for the results of all my tests were horrible, so I figured
 * it would be best to just push them into their own txt file (dll/Results.txt). 
 * What this potentially makes it very easy to do is to create GUI applications
 * that prompt the user for method results and writes them to file. This would 
 * make result input far easier.
 * 
 * @author michael
 *
 */
public class ResultParser {
	
	
	// the file to parse from
	File file;
	// regular expression that matches characters surrounded by whitespace
	// or quotes or angle brackets
	private final Pattern reg = Pattern.compile("(<[^>]*>|\"[^\"]*\"|\\S+)");
	
	
	/**
	 * creates a new ResultParser that will read from the given filename
	 * 
	 * @param filename  the file to read from
	 */
	public ResultParser(String filename) {
		this.file = new File(filename);
	}
	
	
	/**
	 * looks up the results associated with the given TestCase description
	 * in the file associated with this ResultParser
	 * 
	 * @param caseName  the description of the TestCase
	 * @return          the ResultSet associated with the description
	 */
	public ResultSet readResultSet(String caseName) {
		ResultSet ret = new ResultSet();
		
		String all = getResStr(caseName);
		Matcher match = reg.matcher(all);
				
		String next = "";
		while(match.find()) {
			next = match.group();
			
			if (next.contains("<")) {
				
				String funcName = next.substring(next.indexOf("<") + 1, next.indexOf(">"));
				String endName = "</" + funcName + ">";
				
				match.find();
				next = match.group();
				
				Stack<Result> methodResults = new Stack<Result>();
				
				while (!next.equals(endName)) {
					Result r = new Result(getObject(next));
					methodResults.push(r);
					match.find();
					next = match.group();
				}
				
				ret.addResult(funcName, methodResults.toArray(new Result[methodResults.size()]));
			}
		}
		return ret;
	}
	
	
	/**
	 * get the object represented by the String
	 * 
	 * <p>
	 * 
	 * This is implementation dependent. Any new expected Results
	 * need to be accounted for here. This doesn't bother me so 
	 * much since this is a convenience class for this testbed
	 * specifically.
	 * 
	 * @param s  the String representing an Object
	 * @return   the Object represented by the given String
	 */
	@SuppressWarnings("rawtypes")
	private Object getObject(String s) {
		switch (s) {
		case "null":
			return null;
		case "true":
			return true;
		case "false":
			return false;
		case "IndexOutOfBounds":
			return new IndexOutOfBoundsException();
		case "NoSuchElement":
			return new NoSuchElementException();
		case "IllegalState":
			return new IllegalStateException();
		case "ListIterator":
			return new ArrayList().listIterator();
		case "Iterator":
			return new ArrayList().iterator();
		default:
			// if it is numeric
			if (s.matches("[-+]?\\d*\\.?\\d+")) {
				return Integer.parseInt(s);
			}
			if (s.charAt(0) == '\"' && s.charAt(s.length() - 1) == '\"') {
				return s.substring(1, s.length() - 1);
			}
		}
		throw new ResultParserException(s + " is not a valid result entry");
	}
	
	
	/**
	 * represents some problem with the ResultParser
	 * 
	 * @author michael
	 *
	 */
	public class ResultParserException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public ResultParserException(String s) {
			super(s);
		}
	}
	
	/**
	 * Get the section of the file associated with the TestCase description
	 * 
	 * @param caseName  the description of the TestCase to search for
	 * @return          the section of the File associated with the given description
	 */
	private String getResStr(String caseName) {
		StringBuilder ret = new StringBuilder();
		
		try {
			Scanner scan = new Scanner(this.file);
			String startName = "<" + caseName + ">";
			String endName = "</" + caseName + ">";
						
			while (!scan.nextLine().equals(startName)) {}
			
			String x = scan.nextLine();
			while (!x.equals(endName)) {
				ret.append(x + "\n");
				x = scan.nextLine();
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.err.println("file:" + file.getName() + " not found");
			e.printStackTrace();
		}
		
		if (ret.length() == 0) {
			throw new NoSuchElementException(caseName + " not in file");
		}
		
		return ret.toString();
	}
	
	
	
	
	// I'll call it "Michaelsoft Windows: Markup Language for the 22nd Century"
}
