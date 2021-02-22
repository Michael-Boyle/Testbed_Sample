package testbed;


/**
 * a simple Tuple struct for convenience (specifically
 * of static initializers in the resource classes of
 * dll)
 * 
 * @author michael
 *
 * @param <X>  The first type in this tuple
 * @param <Y>  The second type in this tuple
 */
public class Tuple<X, Y> {

	public final X x;
	public final Y y;
	
	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}
	
	// "Tuple who?" - James Gosling
}
