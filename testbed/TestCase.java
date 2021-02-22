package testbed;
import java.util.function.Supplier;


/**
 * Represents a situation to be tested.
 * 
 * @author michael
 * 
 */
public class TestCase {
	
	// The description of the sitation being tested
	private String description;
	// The Supplier to supply the Object for testing
	private Supplier<?> op;
	// The parameters with which to call the functions of the object
	private DomainSet params;
	// The expected results of the functions of the object give the parameters
	private ResultSet expected;
	
	
	/**
	 * Create a new TestCase
	 * 
	 * @param description  The string description of this TestCase
	 * @param op           The Supplier that returns the new object to be tested
	 * @param params       The DomainSet representing the domains of each function in the 
	 * 					   given object.
	 * @param expected     the ResultSet containing the expected results of each function
	 * 					   in the given object.
	 */
	public TestCase(String description, Supplier<?> op, DomainSet params, ResultSet expected) {
		this.description = description;
		this.op = op;
		this.params = params;
		this.expected = expected;
	}
	
	
	/**
	 * get the Supplier of this TestCase that returns the Object to be tested
	 * 
	 * @return the Supplier that supplies the tested Object of this TestCase
	 */
	public Supplier<?> getOp() {
		return this.op;
	}
	
	/**
	 * get the ResultSet representing the expected results of each function 
	 * in the Class being tested by this TestCase
	 * 
	 * @return The expected ResultSet of this TestCase
	 */
	public ResultSet getExpected() {
		return this.expected;
	}
	
	/**
	 * get the String description of this TestCase
	 * 
	 * @return the String description of this TestCase
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * get the set of parameters for the functions of the Class being tested
	 * by this TestCase
	 * 
	 * @return the DomainSet representing the set of parameters for the functions of 
	 * 		   this testcase
	 */
	public DomainSet getParams() {
		return this.params;
	}
	
	

}
