package matrix;

/**
 *
 * @author Matthew Johnston
 * @author Solomon Wagner
 */
public class Matrix {

	public int[][] matrix;
	public int size;

	/**
	 * Builds a new matrix
	 *
	 * @param A
	 * @param size
	 */
	public Matrix(int[][] A, int size) {
		this.matrix = A;
		this.size = size;
	}

	// =========================================================================
	//                          PUBLIC METHODS
	// =========================================================================
	/**
	 * Does a strassens multiplication for the two matrices
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public static Matrix strassen(Matrix x, Matrix y) {
		if (x.size == 2) {
			int[][] matrix = new int[2][2];

			int a, b, c, d, e, f, g, h;
			a = x.matrix[0][0];
			b = x.matrix[0][1];
			c = x.matrix[1][0];
			d = x.matrix[1][1];

			e = y.matrix[0][0];
			f = y.matrix[0][1];
			g = y.matrix[1][0];
			h = y.matrix[1][1];

			int p1, p2, p3, p4, p5, p6, p7;
			p1 = a * (f - h);
			p2 = (a + b) * h;
			p3 = (c + d) * e;
			p4 = d * (g - e);
			p5 = (a + d) * (e + h);
			p6 = (b - d) * (g + h);
			p7 = (a - c) * (e + f);

			int r, s, t, u;
			r = p5 + p4 - p2 + p6;
			s = p1 + p2;
			t = p3 + p4;
			u = p5 + p1 - p3 - p7;

			matrix[0][0] = r;
			matrix[0][1] = s;
			matrix[1][0] = t;
			matrix[1][1] = u;

			return new Matrix(matrix, 2);
		}

		int n = x.size / 2;

		Matrix a, b, c, d, e, f, g, h;

		// ========================
		// Matrix X
		// ========================
		a = submatrix(x, 0, 0, n);
		b = submatrix(x, 0, n, n);
		c = submatrix(x, n, 0, n);
		d = submatrix(x, n, n, n);

		// ========================
		// Matrix Y
		// ========================
		e = submatrix(y, 0, 0, n);
		f = submatrix(y, 0, n, n);
		g = submatrix(y, n, 0, n);
		h = submatrix(y, n, n, n);

		Matrix p1, p2, p3, p4, p5, p6, p7;
		p1 = strassen(a, subtract(f, h));
		p2 = strassen(add(a, b), h);
		p3 = strassen(add(c, d), e);
		p4 = strassen(d, subtract(g, e));
		p5 = strassen(add(a, d), add(e, h));
		p6 = strassen(subtract(b, d), add(g, h));
		p7 = strassen(subtract(a, c), add(e, f));

		Matrix r, s, t, u;
		r = add(subtract(add(p5, p4), p2), p6);
		s = add(p1, p2);
		t = add(p3, p4);
		u = subtract(subtract(add(p5, p1), p3), p7);

		return compose(r, s, t, u);
	}

	public static Matrix hybrid(Matrix x, Matrix y, int min) {
		int n = x.size / 2;

		if (n < min) {
			return standard(x, y);
		}

		Matrix a, b, c, d, e, f, g, h;
		a = submatrix(x, 0, 0, n);
		b = submatrix(x, 0, n, n);
		c = submatrix(x, n, 0, n);
		d = submatrix(x, n, n, n);
		e = submatrix(y, 0, 0, n);
		f = submatrix(y, 0, n, n);
		g = submatrix(y, n, 0, n);
		h = submatrix(y, n, n, n);

		Matrix p1, p2, p3, p4, p5, p6, p7;
		p1 = hybrid(a, subtract(f, h), min);
		p2 = hybrid(add(a, b), h, min);
		p3 = hybrid(add(c, d), e, min);
		p4 = hybrid(d, subtract(g, e), min);
		p5 = hybrid(add(a, d), add(e, h), min);
		p6 = hybrid(subtract(b, d), add(g, h), min);
		p7 = hybrid(subtract(a, c), add(e, f), min);

		Matrix r, s, t, u;
		r = add(subtract(add(p5, p4), p2), p6);
		s = add(p1, p2);
		t = add(p3, p4);
		u = subtract(subtract(add(p5, p1), p3), p7);

		return compose(r, s, t, u);
	}

	/**
	 * Stub function
	 *
	 * @see Matrix.matrix_alu()
	 * @param a
	 * @param b
	 * @param c
	 * @return a modified matrix c
	 */
	public static Matrix add(Matrix a, Matrix b) {
		return matrix_alu(a, b, true);
	}

	/**
	 * Stub function
	 *
	 * @see Matrix.matrix_alu()
	 * @param a
	 * @param b
	 * @return
	 */
	public static Matrix subtract(Matrix a, Matrix b) {
		return matrix_alu(a, b, false);
	}

	/**
	 * Checks whether matrix B equals the current matrix
	 *
	 * @param b
	 * @return
	 */
	public boolean equals(Matrix b) {
		// It's obvious
		if (b.matrix.length != matrix.length) {
			return false;
		}

		// Its obvious
		if (b.matrix[0].length != matrix[0].length) {
			return false;
		}

		// Now go through N^2 times and check all values
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				if (matrix[i][j] != b.matrix[i][j]) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public String toString() {
		String r = "\nBegin Matrix Output:\n";
		for (int i = 0; i < matrix.length; i++) {
			r += "{";
			for (int j = 0; j < matrix[i].length; j++) {
				r += " [" + matrix[i][j] + "] ";
			}
			r += "}\n";
		}
		r += "End Matrix Output\n";
		return r;
	}

	/**
	 * Standard O(n^3) implementation
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static Matrix standard(Matrix a, Matrix b) {
		int[][] c = new int[a.size][a.size];

		for (int i = 0; i < a.size; i++) {
			for (int j = 0; j < a.size; j++) {
				c[i][j] = row_by_column(a, b, i, j);
			}
		}

		return new Matrix(c, a.size);
	}

	/**
	 * Exactly what the name says, it goes row by column
	 *
	 * @param a
	 * @param b
	 * @param x
	 * @param y
	 * @return
	 */
	private static int row_by_column(Matrix a, Matrix b, int x, int y) {
		int sum = 0;

		for (int i = 0; i < a.size; i++) {
			sum += a.matrix[x][i] * b.matrix[i][y];
		}

		return sum;
	}

	// =========================================================================
	//                          PRIVATE METHODS
	// =========================================================================
	/**
	 * Does simple ALU operations on a matrix. Add or subtract only
	 *
	 * @param a
	 * @param b
	 * @param is_add
	 * @return
	 */
	private static Matrix matrix_alu(Matrix a, Matrix b, boolean is_add) {
		int[][] r = new int[a.size][a.size];

		for (int x = 0; x < a.matrix.length; x++) {
			for (int y = 0; y < a.matrix.length; y++) {
				if (is_add) {
					r[x][y] = a.matrix[x][y] + b.matrix[x][y];
				} else {
					r[x][y] = a.matrix[x][y] - b.matrix[x][y];
				}
			}
		}

		return new Matrix(r, a.size);
	}

	/**
	 * Composes 4 sub-matrixes together
	 *
	 * @param r
	 * @param s
	 * @param t
	 * @param u
	 * @return
	 */
	private static Matrix compose(Matrix r, Matrix s, Matrix t, Matrix u) {
		int size = r.size * 2;
		int[][] matrix = new int[size][size];

		for (int i = 0; i < r.size; i++) {                       // i = Row
			for (int j = 0; j < r.size; j++) {                   // j = Column
				matrix[i][j] = r.matrix[i][j];                   // Upper Left
				matrix[i][j + s.size] = s.matrix[i][j];          // Upper Right
				matrix[i + t.size][j] = t.matrix[i][j];          // Lower Left
				matrix[i + u.size][j + u.size] = u.matrix[i][j]; // Lower Right
			}
		}

		return new Matrix(matrix, size);
	}

	/**
	 * Gets a sub-matrix from a matrix
	 *
	 * @param a - Matrix to get the sub-matrix from
	 * @param t - Top location you want to start from
	 * @param l - Left location you want to start from
	 * @param n - How many rows and columns you want
	 *
	 * @return A new sub matrix;
	 */
	private static Matrix submatrix(Matrix a, int t, int l, int n) {
		int[][] r = new int[n][n];

		int x, y = 0;

		for (int p = t; p < t + n; p++) {
			x = 0;
			for (int q = l; q < l + n; q++) {
				r[y][x] = a.matrix[p][q];
				x++;
			}
			y++;
		}

		return new Matrix(r, n);
	}
}
