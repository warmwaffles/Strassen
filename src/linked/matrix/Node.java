package linked.matrix;

/**
 * A simple Node object that contains links to its neighbors
 * @author Matthew Johnston
 */
public class Node {
	private int val;
	public Node north, east, south, west;

	/**
	 * Builds a simple node that is not linked up with anything
	 *
	 * @param val - The value the node will hold
	 */
	public Node(int val) {
		this.val = val;
		north    = null;
		east     = null;
		south    = null;
		west     = null;
	}

	public int value(){
		return val;
	}

	public void set_value(int val) {
		this.val = val;
	}

	@Override
	public String toString(){
		return "Node: " + val;
	}

}