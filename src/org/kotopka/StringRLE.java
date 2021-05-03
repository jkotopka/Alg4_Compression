package org.kotopka;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * {@code StringRLE} - Basic Run-Length Encoding.
 * <br><br>
 * NOT intended as an actual compression scheme, instead this program
 * "compresses" a file to a human-readable text file containing integers separated by spaces. Each integer
 * corresponds to the number of the particular bit, either 0 or 1, in an alternating fashion starting from 0.
 * E.g. 2 4 3 1 3 3 would be 0011110001000111.
 * <br><br>
 * Expansion is performed by reading the "compressed" file, converting the integers to a {@code String} of 0s and 1s,
 * and parsing each sequential substring of 8 chars into a {@code byte}, which is then written
 * into a new file.
 * <br><br>
 * The idea here is NOT to be efficient but to be able to print out the binary representation of each step of the process.
 */
public class StringRLE {

    private static final byte EOF = -1;

    public static void main(String[] args) {
        String inputFilename = args[0];
        String outputFilename = args[1];
//        bitStringPrinter(inputFilename);
//        compress(inputFilename, outputFilename);
        expand(inputFilename, outputFilename);
    }

    public static void compress(String inputFilename, String outputFilename) {
        try (FileInputStream inputStream = new FileInputStream(inputFilename);
             PrintWriter writer = new PrintWriter(outputFilename)) {

            int character;
            int count = 0;
            boolean isZero = true;
            boolean writeBitCount = false;

            System.out.println("Compressing " + inputFilename + " to file: " + outputFilename);

            while ((character = inputStream.read()) != EOF) {
                System.out.println(byteToBitString(character));

                for (int bitMask = 1 << 7; bitMask > 0 ; bitMask >>= 1) {
                    if ((character & bitMask) == 0) {
                        if (!isZero) writeBitCount = true;
                    } else {
                        if (isZero) writeBitCount = true;
                    }

                    if (writeBitCount) {
                        writer.write(count + " ");
                        isZero = !isZero;
                        writeBitCount = false;
                        count = 0;
                    }

                    count++;
                }
            }

            // write remaining portion of "count"
            writer.write(String.valueOf(count));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void expand(String inputFilename, String outputFilename) {
        try (FileOutputStream outputStream = new FileOutputStream(outputFilename)) {
            StringBuilder sb = new StringBuilder();
            String[] fileContents = Files.readString(Paths.get(inputFilename)).split(" ");

            System.out.println("Expanding file " + inputFilename + " to file: " + outputFilename);

            // build the String of 0s and 1s
            for (int i = 0; i < fileContents.length; i++) {
                int count = Integer.parseInt(fileContents[i]);
                int current;

                if (i % 2 == 0) {
                    current = 0;
                } else {
                    current = 1;
                }

                while (count > 0) {
                    sb.append(current);
                    count--;
                }
            }

            String s = sb.toString().trim();

            // write the parsed 0s and 1s to file
            for (int c = 0; c + 8 <= s.length(); c += 8) {
                String temp = s.substring(c, c + 8);

                System.out.println(temp);

                byte b = (byte) (Integer.parseInt(temp, 2) & 0xff);
                outputStream.write(b);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void bitStringPrinter(String filename) {
        try (FileInputStream inputStream = new FileInputStream(filename)) {
            int character;
            int byteCount = 0;

            while ((character = inputStream.read()) != EOF) {
                if (byteCount > 0 && byteCount % 2 == 0) {
                    System.out.println();
                }

                System.out.print(byteToBitString(character) +  " ");

                byteCount++;
            }

            System.out.println("\n" + byteCount + " bytes, (" + (byteCount * 8) + " bits)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String byteToBitString(int byteVal) {
        if (byteVal > 255) throw new IllegalArgumentException("Invalid byte value: " + byteVal);

        StringBuilder bitString = new StringBuilder();

        for (int bitMask = 1 << 7; bitMask > 0; bitMask >>= 1) {
            if ((byteVal & bitMask) == 0) {
                bitString.append(0);
            } else {
                bitString.append(1);
            }
        }

        return bitString.toString();
    }

}
