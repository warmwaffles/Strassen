package main;

import matrix.LinkedMatrix;
import matrix.Matrix;

/**
 * The driving benchmark class to test the linked matrix using strassens method
 * @author Matthew Johnston
 */
public class Main {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		int[][] A, B;

		int number_of_tests = 5;
		int range = 1000;
		int n = 2;
		int max = 2048;
		int a = 0;

		long start, end, standard_time, strassen_time, hybrid_time;

		int count = 0;
		while (n < 2048) {

			n *= 2;
			a = 2;
			for (int i = 0; i < number_of_tests; i++) {
				count++;
				a *= 2;
				// Build the arrays

				A = new int[n][n];
				B = new int[n][n];

				// Fill the arrays with data
				for (int x = 0; x < n; x++) {
					for (int y = 0; y < n; y++) {
						A[x][y] = (int) (Math.random() * range + 1);
						B[x][y] = (int) (Math.random() * range + 1);
					}
				}

				System.out.println("\n============================================================");
				System.out.print("Test #");
				System.out.println(count);
				System.out.println("============================================================\n");

				System.out.println("Matrix Size: " + n);
				// =================================================================
				// Standard Test
				// =================================================================
				Matrix p = new Matrix(A, A.length);
				Matrix q = new Matrix(B, B.length);

				start = System.currentTimeMillis();
				Matrix standard = Matrix.standard(p, q);
				end = System.currentTimeMillis();

				standard_time = (end - start);
				System.out.println("Standard Time: " + standard_time + " (miliseconds)");
				System.gc(); // Make a call to the garbage collector. Maybe it'll pick something up for once

				// =================================================================
				// Hybrid Test
				// =================================================================

				start = System.currentTimeMillis();
				Matrix hybrid = Matrix.hybrid(p, q, a);
				end = System.currentTimeMillis();

				hybrid_time = (end - start);
				System.out.println("Hybrid Time:   " + hybrid_time + " (miliseconds) cutoff: " + a);
				System.gc(); // Make a call to the garbage collector. Maybe it'll pick something up for once

				// =================================================================
				// Strassen Test
				// =================================================================
				LinkedMatrix x = new LinkedMatrix(A);//, A.length);
				LinkedMatrix y = new LinkedMatrix(B);//, B.length);

				start = System.currentTimeMillis();
				LinkedMatrix strassen = LinkedMatrix.multiply(x, y);
				end = System.currentTimeMillis();

				strassen_time = (end - start);
				System.out.println("Strassen Time: " + strassen_time + " (miliseconds)");
				System.gc(); // Make a call to the garbage collector. Maybe it'll pick something up for once

			}
		}
	}
}
