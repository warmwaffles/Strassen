package matrix;

/**
 * Implementing Strassen's method via linked lists
 *
 * @author Matthew Johnston (aka WarmWaffles)
 */
public class LinkedMatrix {

	private int width, height;
	private LinkedMatrixNode root;

	/**
	 * Builds a null matrix
	 */
	public LinkedMatrix() {
		root = null;
		height = 0;
		width = 0;
	}

	/**
	 * Builds a matrix with the node N and then it will find the size of the matrix
	 * auto-magically
	 * 
	 * @param n
	 */
	public LinkedMatrix(LinkedMatrixNode n) {
		root = n;
		height = 0;
		width = 0;
		compute_size();
	}

	/**
	 * Builds a Matrix object from a standard 2D array
	 *
	 * @param matrix
	 */
	public LinkedMatrix(int[][] matrix) {
		this();

		// If the programmer was dumb
		if (matrix == null || matrix.length < 1) {
			return;
		}

		width = matrix.length;
		height = matrix[0].length;

		root = new LinkedMatrixNode(matrix[0][0]);

		LinkedMatrixNode temp = null;
		LinkedMatrixNode current = root;
		LinkedMatrixNode top = root;
		LinkedMatrixNode beginning = root;

		// Setup the first row
		for (int i = 1; i < matrix[0].length; i++) {
			temp = new LinkedMatrixNode(matrix[0][i]);

			temp.west = current;
			current.east = temp;
			current = temp;
		}

		// Now proceed with the next set of rows
		for (int i = 1; i < matrix.length; i++) {
			top = beginning;
			for (int j = 0; j < matrix[i].length; j++) {
				temp = new LinkedMatrixNode(matrix[i][j]);

				// If we are not at the beginning of a new row
				if (j != 0) {
					temp.west = current;
					current.east = temp;
				} else {
					// We are at the beginning of the array you know
					beginning = temp;
				}

				// Set the north and south
				temp.north = top;
				top.south = temp;

				top = top.east;

				// now advance current
				current = temp;
			}
		}

	}

	private void compute_size() {
		LinkedMatrixNode t = root;
		height = 0;
		width = 0;

		while (t != null) {
			height++;
			t = t.south;
		}

		t = root;
		while (t != null) {
			width++;
			t = t.east;
		}
	}

	public LinkedMatrix split_vertical() {
		int size = width / 2;
		LinkedMatrixNode t = root;
		for (int i = 0; i < size; i++) {
			t = t.east;
		}

		LinkedMatrixNode new_root = t;
		while (t != null) {
			t.west.east = null;
			t.west = null;
			t = t.south;
		}

		compute_size();

		return new LinkedMatrix(new_root);
	}

	public LinkedMatrix split_horizontal() {
		int size = height / 2;

		LinkedMatrixNode t = root;
		for (int i = 0; i < size; i++) {
			t = t.south;
		}

		LinkedMatrixNode new_root = t;

		while (t != null) {
			t.north.south = null;
			t.north = null;
			t = t.east;
		}

		compute_size();

		return new LinkedMatrix(new_root);
	}

	/**
	 * Multiplies two linked matrices using Strassen's method.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public static LinkedMatrix multiply(LinkedMatrix x, LinkedMatrix y) {
		if (x.width == 2) {
			int a, b, c, d, e, f, g, h;
			a = x.root.value();            // x[0][0]
			b = x.root.east.value();       // x[0][1]
			c = x.root.south.value();      // x[1][0]
			d = x.root.south.east.value(); // x[1][1]

			e = y.root.value();            // y[0][0]
			f = y.root.east.value();       // y[0][1]
			g = y.root.south.value();      // y[1][0]
			h = y.root.south.east.value(); // y[1][1]

			int p1, p2, p3, p4, p5, p6, p7;
			p1 = a * (f - h);
			p2 = (a + b) * h;
			p3 = (c + d) * e;
			p4 = d * (g - e);
			p5 = (a + d) * (e + h);
			p6 = (b - d) * (g + h);
			p7 = (a - c) * (e + f);

			LinkedMatrixNode r, s, t, u;
			r = new LinkedMatrixNode(p5 + p4 - p2 + p6);
			s = new LinkedMatrixNode(p1 + p2);
			t = new LinkedMatrixNode(p3 + p4);
			u = new LinkedMatrixNode(p5 + p1 - p3 - p7);

			r.east  = s;
			r.south = t;
			s.west  = r;
			s.south = u;
			t.north = r;
			t.east  = u;
			u.west  = t;
			u.north = s;

			return new LinkedMatrix(r);
		}
		LinkedMatrix a, b, c, d, e, f, g, h;
		a = x;
		b = a.split_vertical();
		c = a.split_horizontal();
		d = b.split_horizontal();

		e = y;
		f = e.split_vertical();
		g = e.split_horizontal();
		h = f.split_horizontal();

		LinkedMatrix p1, p2, p3, p4, p5, p6, p7;
		p1 = multiply(a, subtract(f, h));
		p2 = multiply(add(a, b), h);
		p3 = multiply(add(c, d), e);
		p4 = multiply(d, subtract(g, e));
		p5 = multiply(add(a, d), add(e, h));
		p6 = multiply(subtract(b, d), add(g, h));
		p7 = multiply(subtract(a, c), add(e, f));
		
		LinkedMatrix r, s, t, u;
		r = add(subtract(add(p5, p4), p2), p6);
		s = add(p1, p2);
		t = add(p3, p4);
		u = subtract(subtract(add(p5, p1), p3), p7);

		return compose(r, s, t, u);
	}

	public static LinkedMatrix compose(LinkedMatrix r, LinkedMatrix s, LinkedMatrix t, LinkedMatrix u) {
		LinkedMatrixNode rcloth = r.root;
		LinkedMatrixNode scloth = s.root;

		LinkedMatrixNode tcloth = t.root;
		LinkedMatrixNode ucloth = u.root;

		// Stitch the top halves together (R |u| S) |u| (T |u| U)
		while(rcloth.east != null && tcloth.east != null) {
			rcloth = rcloth.east;
			tcloth = tcloth.east;
		}

		while (rcloth != null && tcloth != null) {
			// Begin stitching
			rcloth.east = scloth;
			scloth.west = rcloth;

			tcloth.east = ucloth;
			ucloth.west = tcloth;

			// Move all pointers southward
			rcloth = rcloth.south;
			scloth = scloth.south;
			tcloth = tcloth.south;
			ucloth = ucloth.south;
		}

		rcloth = r.root;
		// Need to traverse down to the bottom of the top cloth
		while(rcloth.south != null) {
			rcloth = rcloth.south;
		}

		tcloth = t.root;

		// Now stich them horizontally together
		while(rcloth != null && tcloth != null) {
			rcloth.south = tcloth;
			tcloth.north = rcloth;

			rcloth = rcloth.east;
			tcloth = tcloth.east;
		}


		return new LinkedMatrix(r.root);
	}

	public static LinkedMatrix subtract(LinkedMatrix a, LinkedMatrix b) {
		return matrix_alu(a, b, false);
	}

	public static LinkedMatrix add(LinkedMatrix a, LinkedMatrix b) {
		return matrix_alu(a, b, true);
	}

	private static LinkedMatrix matrix_alu(LinkedMatrix a, LinkedMatrix b, boolean is_add) {
		int[][] temp = new int[a.width][a.height];

		LinkedMatrixNode x = a.root;
		LinkedMatrixNode xb = a.root;

		LinkedMatrixNode y = b.root;
		LinkedMatrixNode yb = b.root;

		int i = 0;
		while(xb != null && yb != null ) {
			int j = 0;
			while(x != null && y != null) {

				if(is_add)
					temp[i][j] = x.value() + y.value();
				else
					temp[i][j] = x.value() - y.value();

				x = x.east;
				y = y.east;
				j++;
			}
			xb = xb.south;
			yb = yb.south;
			x = xb;
			y = yb;
			i++;
		}
		return new LinkedMatrix(temp);
	}

	@Override
	public String toString() {
		String s = "Begin Matrix\n   Width:  " + width + "\n   Height: " + height + "\n";

		LinkedMatrixNode t = root, beginning = root;
		while (beginning != null) {
			t = beginning;
			beginning = beginning.south;
			while (t != null) {
				s += " [" + t.value() + "] ";
				t = t.east;
			}
			s += "\n";
		}
		s += "End Matrix\n";

		return s;
	}
}
