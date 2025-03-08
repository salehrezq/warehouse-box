package warehousebox.db;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author Saleh
 */
public class SearchFormatter {

    protected static String trimExtraSpaces(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }

    protected static String[] getUniqueArrayOfWords(String[] words) {
        ArrayList<String> uniqueWords = new ArrayList<>();
        Set<String> set = new LinkedHashSet<>();

        for (String word : words) {
            if (set.add(word)) {
                uniqueWords.add(word);
            }
        }
        return uniqueWords.toArray(String[]::new);
    }

    protected static String[] getArrayOfWords(String text) {
        return getUniqueArrayOfWords(trimExtraSpaces(text).toLowerCase().split("\\W+"));
    }
}
