package matrix;

/**
 * A simple Node object that contains links to its neighbors
 * @author Matthew Johnston
 */
public class LinkedMatrixNode {
	private int val;
	public LinkedMatrixNode north, east, south, west;

	public LinkedMatrixNode(int val) {
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