package com.ufu.crar.util;


public class StdArrayIO {

    // it doesn't make sense to instantiate this class
    private StdArrayIO() { }

    /**
     * Reads a 1D array of doubles from standard input and returns it.
     *
     * @return the 1D array of doubles
     */
    public static double[] readDouble1D() {
        int n = StdIn.readInt();
        double[] a = new double[n];
        for (int i = 0; i < n; i++) {
            a[i] = StdIn.readDouble();
        }
        return a;
    }

    /**
     * Prints an array of doubles to standard output.
     *
     * @param a the 1D array of doubles
     */
    public static void print(double[] a) {
        int n = a.length;
        StdOut.println(n);
        for (int i = 0; i < n; i++) {
            StdOut.printf("%9.5f ", a[i]);
        }
        StdOut.println();
    }

        
    /**
     * Reads a 2D array of doubles from standard input and returns it.
     *
     * @return the 2D array of doubles
     */
    public static double[][] readDouble2D() {
        int m = StdIn.readInt();
        int n = StdIn.readInt();
        double[][] a = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = StdIn.readDouble();
            }
        }
        return a;
    }

    /**
     * Prints the 2D array of doubles to standard output.
     *
     * @param a the 2D array of doubles
     */
    public static void print(double[][] a) {
        int m = a.length;
        int n = a[0].length;
        StdOut.println(m + " " + n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                StdOut.printf("%9.5f ", a[i][j]);
            }
            StdOut.println();
        }
    }


    /**
     * Reads a 1D array of integers from standard input and returns it.
     *
     * @return the 1D array of integers
     */
    public static int[] readInt1D() {
        int n = StdIn.readInt();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = StdIn.readInt();
        }
        return a;
    }

    /**
     * Prints an array of integers to standard output.
     *
     * @param a the 1D array of integers
     */
    public static void print(int[] a) {
        int n = a.length;
        StdOut.println(n);
        for (int i = 0; i < n; i++) {
            StdOut.printf("%9d ", a[i]);
        }
        StdOut.println();
    }

        
    /**
     * Reads a 2D array of integers from standard input and returns it.
     *
     * @return the 2D array of integers
     */
    public static int[][] readInt2D() {
        int m = StdIn.readInt();
        int n = StdIn.readInt();
        int[][] a = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = StdIn.readInt();
            }
        }
        return a;
    }

    /**
     * Print a 2D array of integers to standard output.
     *
     * @param a the 2D array of integers
     */
    public static void print(int[][] a) {
        int m = a.length;
        int n = a[0].length;
        StdOut.println(m + " " + n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                StdOut.printf("%9d ", a[i][j]);
            }
            StdOut.println();
        }
    }


    /**
     * Reads a 1D array of booleans from standard input and returns it.
     *
     * @return the 1D array of booleans
     */
    public static boolean[] readBoolean1D() {
        int n = StdIn.readInt();
        boolean[] a = new boolean[n];
        for (int i = 0; i < n; i++) {
            a[i] = StdIn.readBoolean();
        }
        return a;
    }

    /**
     * Prints a 1D array of booleans to standard output.
     *
     * @param a the 1D array of booleans
     */
    public static void print(boolean[] a) {
        int n = a.length;
        StdOut.println(n);
        for (int i = 0; i < n; i++) {
            if (a[i]) StdOut.print("1 ");
            else      StdOut.print("0 ");
        }
        StdOut.println();
    }

    /**
     * Reads a 2D array of booleans from standard input and returns it.
     *
     * @return the 2D array of booleans
     */
    public static boolean[][] readBoolean2D() {
        int m = StdIn.readInt();
        int n = StdIn.readInt();
        boolean[][] a = new boolean[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = StdIn.readBoolean();
            }
        }
        return a;
    }

   /**
     * Prints a 2D array of booleans to standard output.
     *
     * @param a the 2D array of booleans
     */
    public static void print(boolean[][] a) {
        int m = a.length;
        int n = a[0].length;
        StdOut.println(m + " " + n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (a[i][j]) StdOut.print("1 ");
                else         StdOut.print("0 ");
            }
            StdOut.println();
        }
    }


   /**
     * Unit tests {@code StdArrayIO}.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        // read and print an array of doubles
        double[] a = StdArrayIO.readDouble1D();
        StdArrayIO.print(a);
        StdOut.println();

        // read and print a matrix of doubles
        double[][] b = StdArrayIO.readDouble2D();
        StdArrayIO.print(b);
        StdOut.println();

        // read and print a matrix of doubles
        boolean[][] d = StdArrayIO.readBoolean2D();
        StdArrayIO.print(d);
        StdOut.println();
    }

}

