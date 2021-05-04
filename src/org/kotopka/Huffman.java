package org.kotopka;

import edu.princeton.cs.algs4.*;

/**
 * {@code Huffman} - Implements Huffman coding, from
 * <a href="https://algs4.cs.princeton.edu/home/">Algorithms 4th ed.</a>
 * by Robert Sedgewick and Kevin Wayne
 */
public class Huffman {

    private static final int R = 256; // extended ASCII

    private static class Node implements Comparable<Node> {
        // Huffman trie node
        private char ch;
        private int freq;
        private final Node left;
        private final Node right;

        Node(char ch, int freq, Node left, Node right) {
            this.ch = ch;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }

        @Override
        public int compareTo(Node o) {
            return this.freq - o.freq;
        }

    }

    private static String[] buildCode(Node root) {
        String[] st = new String[R];
        buildCode(st, root, "");
        return st;
    }

    private static void buildCode(String[] st, Node x, String s) {
        if (x.isLeaf()) {
            st[x.ch] = s;
            return;
        }

        buildCode(st, x.left, s + '0');
        buildCode(st, x.right, s + '1');
    }

    private static Node buildTrie(int[] freq) {
        MinPQ<Node> pq = new MinPQ<>();

        // init pq with singleton trees
        for (char c = 0; c < R; c++) {
            if  (freq[c] > 0) {
                pq.insert(new Node(c, freq[c], null, null));
            }
        }

        while (pq.size() > 1) {
            // merge two smallest sub-trees
            Node x = pq.delMin();
            Node y = pq.delMin();
            Node parent = new Node('\0', x.freq + y.freq, x, y);
            pq.insert(parent);
        }

        return pq.delMin();
    }

    private static void writeTrie(BinaryOut out, Node x) {
        // write bitstring-encoded trie
        if (x.isLeaf()) {
            out.write(true);
            out.write(x.ch, 8);
            return;
        }

        out.write(false);
        writeTrie(out, x.left);
        writeTrie(out, x.right);
    }

    private static Node readTrie(BinaryIn in) {
        // reconstruct the trie from a preorder bitstream representation
        if (in.readBoolean()) {
            return new Node(in.readChar(), 0, null, null);
        }

        Node left = readTrie(in);
        Node right = readTrie(in);

        return new Node('\0', 0, left, right);
    }

    public static void expand(String infile, String outfile) {
        BinaryIn in = new BinaryIn(infile);
        BinaryOut out = new BinaryOut(outfile);

        Node root = readTrie(in);
        int n = in.readInt();

        for (int i = 0; i < n; i++) {
            // expand ith codeword
            Node x = root;

            while (!x.isLeaf()) {
                if (in.readBoolean()) {
                    x = x.right;
                } else {
                    x = x.left;
                }
            }

            out.write(x.ch, 8);
        }

        out.close();
    }

    public static void compress(String infile, String outfile) {
        BinaryIn in = new BinaryIn(infile);
        BinaryOut out = new BinaryOut(outfile);

        // read input
        String s = in.readString();
        char[] input = s.toCharArray();

        // tabulate frequency counts
        int[] freq = new int[R];

        for (int i = 0; i < input.length; i++) {
            freq[input[i]]++;
        }

        // build Huffman code trie
        Node root = buildTrie(freq);

        // build code table
        String[] st = new String[R];
        buildCode(st, root, "");

        // print trie for decoder
        writeTrie(out, root);

        // print number of chars
        out.write(input.length);

        // use Huffman code to encode input
        for (int i = 0; i < input.length; i++) {
            String code = st[input[i]];

            for (int j = 0; j < code.length(); j++) {
                if (code.charAt(j) == '1') {
                    out.write(true);
                } else {
                    out.write(false);
                }
            }
        }

        out.close();
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java Huffman infile <option> outfile");
            System.out.println("options:");
            System.out.println("  + compress file");
            System.out.println("  - expand file");
            System.exit(1);
        }

        String infile = args[0];
        String option = args[1];
        String outfile = args[2];

        switch (option) {
            case "-" -> compress(infile, outfile);
            case "+" -> expand(infile, outfile);
            default -> System.out.println("Invalid option: " + option);
        }
    }
}
