package linked.matrix;

/**
 * Implementing Strassen's method via linked lists
 *
 * @author Matthew Johnston
 */
public class LinkedMatrix {

	private int width, height;
	private Node root;

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
	public LinkedMatrix(Node n) {
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
		if (matrix == null || matrix.length < 1)
			return;


		width = matrix.length;
		height = matrix[0].length;

		root = new Node(matrix[0][0]);

		Node temp = null;
		Node current = root;
		Node top = root;
		Node beginning = root;

		// Setup the first row
		for (int i = 1; i < matrix[0].length; i++) {
			temp = new Node(matrix[0][i]);

			temp.west = current;
			current.east = temp;
			current = temp;
		}

		// Now proceed with the next set of rows
		for (int i = 1; i < matrix.length; i++) {
			top = beginning;
			for (int j = 0; j < matrix[i].length; j++) {
				temp = new Node(matrix[i][j]);

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

	/**
	 * Goes through and computes the size of the matrix and sets the width and
	 * height appropriately.
	 */
	private void compute_size() {
		Node t = root;
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

	/**
	 * Splits the matrix in half vertically. It removes the links from the right
	 * half and left half. The right half is returned while the left half remains
	 *
	 * @return
	 */
	public LinkedMatrix split_vertical() {
		int size = width / 2;
		Node t = root;

		for (int i = 0; i < size; i++) {
			t = t.east;
		}

		Node new_root = t;

		while (t != null) {
			t.west.east = null;
			t.west = null;
			t = t.south;
		}

		compute_size();

		return new LinkedMatrix(new_root);
	}

	/**
	 * Splits the matrix in half horizontally. It removes the links on the upper
	 * half from the lower half. The lower half is returned as a new Matrix
	 *
	 * @return
	 */
	private LinkedMatrix split_horizontal() {
		int size = height / 2;

		Node t = root;

		for (int i = 0; i < size; i++)
			t = t.south;


		Node new_root = t;

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

			// TODO: Optimize these math equations
			int p1, p2, p3, p4, p5, p6, p7;
			p1 = a * (f - h);
			p2 = (a + b) * h;
			p3 = (c + d) * e;
			p4 = d * (g - e);
			p5 = (a + d) * (e + h);
			p6 = (b - d) * (g + h);
			p7 = (a - c) * (e + f);

			Node r, s, t, u;
			r = new Node(p5 + p4 - p2 + p6);
			s = new Node(p1 + p2);
			t = new Node(p3 + p4);
			u = new Node(p5 + p1 - p3 - p7);

			r.east = s;
			r.south = t;
			s.west = r;
			s.south = u;
			t.north = r;
			t.east = u;
			u.west = t;
			u.north = s;

			return new LinkedMatrix(r);
		}

		// Split the matrices down to quarters
		LinkedMatrix a, b, c, d, e, f, g, h;
		a = x;
		b = a.split_vertical();
		c = a.split_horizontal();
		d = b.split_horizontal();

		e = y;
		f = e.split_vertical();
		g = e.split_horizontal();
		h = f.split_horizontal();

		// Essentially the base case above but just in recursive mode
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

		// Stitch all of the resultant matrices together
		return compose(r, s, t, u);
	}

	/**
	 * Builds up the 2D array with the 4 submatrices. It runs quickly through the
	 * 4 sub-matrices and links them up appropriately and returns the the Top
	 * Left Matrix
	 *
	 * @param r - Top Left Matrix
	 * @param s - Top Right Matrix
	 * @param t - Bottom Left Matrix
	 * @param u - Bottom Right Matrix
	 *
	 * @return A combined matrix
	 */
	private static LinkedMatrix compose(LinkedMatrix r, LinkedMatrix s, LinkedMatrix t, LinkedMatrix u) {
		// Like stitching a cloth
		Node rcloth = r.root;
		Node scloth = s.root;

		Node tcloth = t.root;
		Node ucloth = u.root;

		// Stitch the top halves together (R |u| S) |u| (T |u| U)
		while (rcloth.east != null && tcloth.east != null) {
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
		while (rcloth.south != null)
			rcloth = rcloth.south;


		tcloth = t.root;

		// Now stich them horizontally together
		while (rcloth != null && tcloth != null) {
			rcloth.south = tcloth;
			tcloth.north = rcloth;

			rcloth = rcloth.east;
			tcloth = tcloth.east;
		}


		return new LinkedMatrix(r.root);
	}

	/**
	 * Stub function
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static LinkedMatrix subtract(LinkedMatrix a, LinkedMatrix b) {
		return matrix_alu(a, b, false);
	}

	/**
	 * Stub function
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static LinkedMatrix add(LinkedMatrix a, LinkedMatrix b) {
		return matrix_alu(a, b, true);
	}

	/**
	 * Does simple addition or subtraction for the two matrices
	 *
	 * @param a
	 * @param b
	 * @param is_add
	 * @return
	 */
	private static LinkedMatrix matrix_alu(LinkedMatrix a, LinkedMatrix b, boolean is_add) {
		// TODO: Get rid of this and create nodes and link them up
		int[][] temp = new int[a.width][a.height];

		Node x = a.root;
		Node xb = a.root;

		Node y = b.root;
		Node yb = b.root;

		int i = 0;
		while (xb != null && yb != null) {
			int j = 0;
			while (x != null && y != null) {

				if (is_add)
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

		Node t = root, beginning = root;
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
