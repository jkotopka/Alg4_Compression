package org.kotopka;

import edu.princeton.cs.algs4.BinaryStdIn;

public class BinaryDump {

    public static void main(String[] args) {
        int width = Integer.parseInt(args[0]);
        int count;

        for (count = 0; !BinaryStdIn.isEmpty(); count++) {
            if (width == 0) continue;
            if (count != 0 && count % width == 0) {
                System.out.println();
            }
            if (BinaryStdIn.readBoolean()) {
                System.out.print(1);
            } else {
                System.out.print(0);
            }
        }

        System.out.println("\n" + count + " bits");
    }
}
