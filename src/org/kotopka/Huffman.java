package org.kotopka;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.MinPQ;

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

    private static void writeTrie(Node x) {
        // write bitstring-encoded trie
        if (x.isLeaf()) {
            BinaryStdOut.write(true);
            BinaryStdOut.write(x.ch, 8);
            return;
        }

        BinaryStdOut.write(false);
        writeTrie(x.left);
        writeTrie(x.right);
    }

    private static Node readTrie() {
        // reconstruct the trie from a preorder bitstream representation

        if (BinaryStdIn.readBoolean()) {
            return new Node(BinaryStdIn.readChar(), 0, null, null);
        }

        Node left = readTrie();
        Node right = readTrie();

        return new Node('\0', 0, left, right);
    }

    public static void expand() {
        Node root = readTrie();
        int n = BinaryStdIn.readInt();

        for (int i = 0; i < n; i++) {
            // expand ith codeword
            Node x = root;

            while (!x.isLeaf()) {
                if (BinaryStdIn.readBoolean()) {
                    x = x.right;
                } else {
                    x = x.left;
                }
            }

            BinaryStdOut.write(x.ch, 8);
        }

        BinaryStdOut.close();
    }

    public static void compress() {
        // read input
        String s = BinaryStdIn.readString();
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
        writeTrie(root);

        // print number of chars
        BinaryStdOut.write(input.length);

        // use Huffman code to encode input
        for (int i = 0; i < input.length; i++) {
            String code = st[input[i]];

            for (int j = 0; j < code.length(); j++) {
                if (code.charAt(j) == '1') {
                    BinaryStdOut.write(true);
                } else {
                    BinaryStdOut.write(false);
                }
            }
        }

        BinaryStdOut.close();
    }

}
