package org.kotopka;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.TST;

/**
 * {@code LZW} - Implementation of the Lempel-Ziv-Welch (LZW) compression algorithm
 * from <a href="https://algs4.cs.princeton.edu/home/">Algorithms 4th ed.</a>
 * by Robert Sedgewick and Kevin Wayne
 * <br><br>
 * Note: unfortunately, as of Oracle Java 7 update 6, the String.substring() method is O(n),
 * instead of O(1) as it was previously. As a result, this algorithm is much less efficient
 * than when it was written for the book.
 */
public class LZW {

    private static final int R = 256;    // number of input chars
    private static final int L = 4096;   // number of codewords = 2^12
    private static final int WIDTH = 12; // codeword width

    public static void compress() {
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<>();

        for (int i = 0; i < R; i++) {
            st.put("" + (char) i, i);
        }

        int code = R + 1; // R is codeword for EOF

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input); // find max prefix match
            BinaryStdOut.write(st.get(s), WIDTH); // print s's encoding
            int t = s.length();

            if (t < input.length() && code < L) {
                st.put(input.substring(0, t + 1), code++); // add to symbol table
            }

            input = input.substring(t); // scan past s in input
        }

        BinaryStdOut.write(R, WIDTH);
        BinaryStdOut.close();
    }
}
