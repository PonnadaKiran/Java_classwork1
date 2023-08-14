import java.io.*;
import java.util.*;

public class HuffmanZipper {
    private static final int BUFFER_SIZE = 4096;

    public static void compress(String inputFile, String outputFile) throws IOException {
        // Read the input file into a byte array
        byte[] inputBytes;
        try (InputStream in = new FileInputStream(inputFile)) {
            inputBytes = in.readAllBytes();
        }

        // Count the frequency of each byte in the input data
        int[] frequency = new int[256];
        for (byte b : inputBytes) {
            frequency[b & 0xFF]++;
        }

        // Build the Huffman tree using a priority queue
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.frequency));
        for (int i = 0; i < 256; i++) {
            if (frequency[i] > 0) {
                queue.add(new Node((byte) i, frequency[i]));
            }
        }
        while (queue.size() > 1) {
            Node left = queue.poll();
            Node right = queue.poll();
            queue.add(new Node(left, right));
        }
        Node root = queue.poll();

        // Write the Huffman tree to the output file
        try (BitOutputStream out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)))) {
            writeHeader(root, out);

            // Encode the input data using the Huffman tree
            Map<Byte, String> codeMap = new HashMap<>();
            buildCodeMap(root, "", codeMap);
            for (byte b : inputBytes) {
                String code = codeMap.get(b);
                for (int i = 0; i < code.length(); i++) {
                    out.writeBit(code.charAt(i) - '0');
                }
            }

            // Flush any remaining bits in the output buffer
            out.flush();
        }
    }

    public static void decompress(String inputFile, String outputFile) throws IOException {
        // Read the Huffman tree from the input file
        try (BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFile)))) {
            Node root = readHeader(in);

            // Decode the input data using the Huffman tree
            try (BitOutputStream out = new BitOutputStream(
                    new BufferedOutputStream(new FileOutputStream(outputFile)))) {
                Node node = root;
                int bit;
                while ((bit = in.readBit()) != -1) {
                    if (node.isLeaf()) {
                        out.writeByte(node.data);
                        node = root;
                    }
                    if (bit == 0) {
                        node = node.left;
                    } else {
                        node = node.right;
                    }
                }
            }
        }
    }

    private static void writeHeader(Node node, BitOutputStream out) throws IOException {
        if (node.isLeaf()) {
            out.writeBit(1);
            out.writeByte(node.data);
        } else {
            out.writeBit(0);
            writeHeader(node.left, out);
            writeHeader(node.right, out);
        }
    }

    private static Node readHeader(BitInputStream in) throws IOException {
        int bit = in.readBit();
        if (bit == -1) {
            throw new EOFException();
        }
        if (bit == 1) {
            return new Node((byte) in.readByte(), 0);
        } else {
            Node left = readHeader(in);
            Node right = readHeader(in);
            return new Node(left, right);
        }
    }

    private static void buildCodeMap(Node node, String code, Map<Byte, String> codeMap) {
        if (node.isLeaf()) {
            codeMap.put(node.data, code);
        } else {
            buildCodeMap(node.left, code + "0", codeMap);
            buildCodeMap(node.right, code + "1", codeMap);
        }
    }

    private static class Node {
        private final byte data;
        private final int frequency;
        private final Node left;
        private final Node right;

        public Node(byte data, int frequency) {
            this.data = data;
            this.frequency = frequency;
            this.left = null;
            this.right = null;
        }

        public Node(Node left, Node right) {
            this.data = 0;
            this.frequency = left.frequency + right.frequency;
            this.left = left;
            this.right = right;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }
    }

    private static class BitInputStream implements AutoCloseable {
        private final InputStream in;
        private int buffer;
        private int bitsLeft;

        public BitInputStream(InputStream in) {
            this.in = in;
            this.buffer = 0;
            this.bitsLeft = 0;
        }

        public int readBit() throws IOException {
            if (bitsLeft == 0) {
                buffer = in.read();
                if (buffer == -1) {
                    return -1;
                }
                bitsLeft = 8;
            }
            bitsLeft--;
            return (buffer >> bitsLeft) & 1;
        }

        public byte readByte() throws IOException {
            int value = 0;
            for (int i = 7; i >= 0; i--) {
                int bit = readBit();
                if (bit == -1) {
                    throw new EOFException();
                }
                value |= bit << i;
            }
            return (byte) value;
        }

        public void close() throws IOException {
            in.close();
        }
    }

    private static class BitOutputStream implements AutoCloseable {
        private final OutputStream out;
        private int buffer;
        private int bitsLeft;

        public BitOutputStream(OutputStream out) {
            this.out = out;
            this.buffer = 0;
            this.bitsLeft = 8;
        }

        public void writeBit(int bit) throws IOException {
            buffer |= bit << (bitsLeft - 1);
            bitsLeft--;
            if (bitsLeft == 0) {
                out.write(buffer);
                buffer = 0;
                bitsLeft = 8;
            }
        }

        public void writeByte(byte b) throws IOException {
            for (int i = 7; i >= 0; i--) {
                writeBit((b >> i) & 1);
            }
        }

        public void flush() throws IOException {
            if (bitsLeft < 8) {
                out.write(buffer);
            }
            out.flush();
        }

        public void close() throws IOException {
            flush();
            out.close();
        }
    }

    // To use this file zipper, you can call the `compress` method to compress a
    // file, and the `decompress` method to decompress a compressed file. For
    // example:

    // ```java
    public static void main(String[] args) throws IOException {
        String inputFile = "input.txt";
        String compressedFile = "compressed.bin";
        String outputFile = "output.txt";

        // Compress the input file
        HuffmanZipper.compress(inputFile, compressedFile);

        // Decompress the compressed file
        HuffmanZipper.decompress(compressedFile, outputFile);
    }
}
