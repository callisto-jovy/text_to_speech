package de.yugata.bot.util;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

public class StringUtil {

    /**
     * Private constructor. Prevent instantiation.
     */
    private StringUtil() {
    }

    public static List<String> wrap(String txt, FontMetrics fm, int maxWidth) {
        final StringTokenizer st = new StringTokenizer(txt);

        final List<String> wrappedLines = new ArrayList<>();
        String line = "";
        String lineBeforeAppend;

        while (st.hasMoreTokens()) {
            final String seg = st.nextToken();
            lineBeforeAppend = line;
            line += seg + " ";
            final int width = fm.stringWidth(line);

            if (width > maxWidth) { //new Line.
                wrappedLines.add(lineBeforeAppend);
                line = seg + " ";
            }
        }

        //the remaining part.
        if (!line.isEmpty()) {
            wrappedLines.add(line);
        }
        return wrappedLines;
    }

    /**
     * Splits the given string into blocks with a given size.
     * If the string cannot be evenly.
     *
     * @param string    string divide.
     * @param blockSize the size of all blocks.
     * @return String[] with all blocks.
     */
    public static String[] splitIntoBlocks(final String string, final int blockSize) {
        final String[] blocks = new String[(int) Math.ceil((float) string.length() / blockSize)];
        Arrays.fill(blocks, "");

        for (int i = 0, j = 0; i < string.length(); i++) {
            if (i % blockSize == 0) j++;

            final char c = string.charAt(i);
            blocks[j - 1] += c;
        }
        return blocks;
    }

    /**
     * Splits a given string into blocks which are a maximum given length.
     * The given delimiter is used to identify the last occurrence's index at which a new block is started.
     *
     * @param string    the string to split.
     * @param blockSize the maximum size one block might have.
     * @param delimiter the delimiter at which the blocks are split.
     * @return an array of the generated blocks.
     */
    public static String[] splitIntoBlocksAtDelimiter(final String string, final int blockSize, final String delimiter) {
        final List<String> blocks = new ArrayList<>();

        // String builder to add characters to and reset.
        StringBuilder builder = new StringBuilder();
        // iterate over the string with two variables:
        // (i) => index in the string
        // (j) => length of the current block
        for (int i = 0, j = 0; i < string.length(); i++, j++) {
            // A new block has to be created.
            if (j >= blockSize) {
                // Search for the next closest delimiter
                final int lastDelimiter = string.lastIndexOf(delimiter, i);
                // Add the builder (as a substring till the last delimiter in the builder)
                blocks.add(builder.substring(0, builder.lastIndexOf(delimiter)));

                i = lastDelimiter + 1; // Backtrack to the position after the last delimiter.
                j = 0; //Reset j
                builder = new StringBuilder();
            }
            builder.append(string.charAt(i));
        }
        // Add the remaining characters.
        blocks.add(builder.toString());
        return blocks.toArray(String[]::new);
    }
}
