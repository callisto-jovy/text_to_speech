package de.yugata.tts.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtil {

    public static final String URL_PATTERN = "((http|https)\\:\\/\\/)?[a-zA-Z0-9\\.\\/\\?\\:@\\-_=#]+\\.([a-zA-Z]){2,6}([a-zA-Z0-9\\.\\&\\/\\?\\:@\\-_=#])*";

    public static final String CLEAR_EXP = "\\s['|’]|['|’]\\s|[\\^_~@!&;#:\\-%—“”‘\\\"%\\*/{}\\[\\]\\(\\)\\\\|<>=+]";

    public static final Gson GSON = new GsonBuilder()
            .create();

    /**
     * Private constructor. Prevent instantiation.
     */
    private StringUtil() {
    }


    /**
     * Sanitizes the text, removes URLs & runs a clear regex.
     *
     * @param text the text to sanitize.
     * @return the text with replaced characters.
     */
    public static String sanitizeText(final String text) {
        return text
                .replaceAll(URL_PATTERN, " ")
                .replaceAll(CLEAR_EXP, " ")
                .replace("+", "plus")
                .replace("&", "and");
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
     * The method tries to split the string in a way, in which sentences are preserved,
     * i.e. the string is split at colons, commas & lastly - if no other option is possible - spaces
     *
     * @param string    the string to split.
     * @param blockSize the maximum size one block might have.
     * @return an array of the generated blocks.
     */
    public static String[] splitSentences(String string, final int blockSize) {
        // Clear the string of newlines & control characters
        string = string.replaceAll("[\\p{C}\\r\\n]", "");
        // Add spaces where no spaces are.
        string = sanitizeText(string);

        final List<String> blocks = new ArrayList<>((int) Math.ceil((float) string.length() / blockSize)); // Rough estimate
        final String[] delimiters = {",", "."};

        // String builder to add characters to and reset.
        StringBuilder sentenceBuffer = new StringBuilder(blockSize);
        // iterate over the string with two variables:
        // (i) => index in the string
        // (j) => length of the current block
        for (int i = 0, j = 0; i < string.length(); i++, j++) {
            // A new block has to be created.
            if (j >= blockSize) {
                // Search for the next closest delimiter (backwards)
                int lastDelimiter = findClosestLastDelimiter(string, i, delimiters);
                int lastBuilderDelimiter = findClosestLastDelimiter(sentenceBuffer.toString(), sentenceBuffer.length(), delimiters);

                // no next sentence / subsequence is available, backtrack to the last "word" bound, e.g. space
                if (lastDelimiter == -1 || lastBuilderDelimiter == -1) {
                    lastDelimiter = string.lastIndexOf(" ", i);
                    lastBuilderDelimiter = sentenceBuffer.lastIndexOf(" ");
                }

                // Add the builder (as a substring till the last delimiter in the builder)
                blocks.add(sentenceBuffer.substring(0, lastBuilderDelimiter));

                i = lastDelimiter + 1; // Backtrack to the position after the last delimiter.
                j = 0; //Reset j
                sentenceBuffer.setLength(0); // Reset the buffer
            }
            sentenceBuffer.append(string.charAt(i));
        }
        // Add the remaining characters.
        blocks.add(sentenceBuffer.toString());

        return blocks.toArray(String[]::new);
    }

    private static int findClosestLastDelimiter(final String input, int fromIndex, final String... delimiters) {
        int closestIndex = -1;

        for (final String test : delimiters) {
            final int result = input.lastIndexOf(test, fromIndex);

            if (result != -1 && result >= closestIndex) {
                closestIndex = result;
            }
        }
        return closestIndex;
    }
}
