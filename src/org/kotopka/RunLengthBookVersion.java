package org.kotopka;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * {@code RunLengthBookVersion} - Version of run-length encoding from
 * <a href="https://algs4.cs.princeton.edu/home/">Algorithms 4th ed.</a>
 * by Robert Sedgewick and Kevin Wayne
 */
public class RunLengthBookVersion {

    public static void expand() {
        boolean b = false;

        while (!BinaryStdIn.isEmpty()) {
            char count = BinaryStdIn.readChar();

            for (int i = 0; i < count; i++) {
                BinaryStdOut.write(b);
            }

            b = !b;
        }

        BinaryStdOut.close();
    }

    public static void compress() {
        char count = 0;
        boolean b, old = false;

        while (!BinaryStdIn.isEmpty()) {
            b = BinaryStdIn.readBoolean();

            if (b != old) {
                BinaryStdOut.write(count, 8);
                count = 0;
                old = !old;
            } else {
                if (count == 255) {
                    BinaryStdOut.write(count, 8);
                    count = 0;
                    BinaryStdOut.write(count, 8);
                }
            }

            count++;
        }

        BinaryStdOut.write(count);
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("java RunLengthBookVersion <operation (- for compress, + for expand)>");
            System.exit(-1);
        }

        String operation = args[0];

        switch (operation) {
            case "+" -> expand();
            case "-" -> compress();
        }
    }

}
