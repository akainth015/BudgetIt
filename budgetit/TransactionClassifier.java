package budgetit;

import org.apache.commons.text.similarity.JaccardSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;

public class TransactionClassifier {
    private static final HashMap<String, ArrayList<Transaction>> categories = new HashMap<>();
    private static final JaccardSimilarity jaccardSimilarity = new JaccardSimilarity();
    private static final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

    static String getMostSimilarCategory(String vendorName) {
        HashMap<String, Double> similarityScores = new HashMap<>();
        HashMap<String, Integer> distanceScores = new HashMap<>();
        categories.forEach((category, transactions) -> {
            Optional<Double> bestScore = transactions.parallelStream().map(transaction ->
                    jaccardSimilarity.apply(transaction.getVendorName(), vendorName)).max(Double::compareTo);
            Optional<Integer> bestDistance = transactions.parallelStream().map(transaction ->
                    levenshteinDistance.apply(transaction.getVendorName(), vendorName)).min(Integer::compareTo);
            bestScore.ifPresent(aDouble -> similarityScores.put(category, aDouble));
            bestDistance.ifPresent(integer -> distanceScores.put(category, integer));
        });

        Map.Entry<String, Double> bestCategory;
        try {
            bestCategory = Collections.max(similarityScores.entrySet(), Map.Entry.comparingByValue());
            if (bestCategory != null && bestCategory.getValue() > 0.85) {
                return bestCategory.getKey();
            }
        } catch (Exception e) {
            return null;
        }

        Map.Entry<String, Integer> bestCategoryByDistance = Collections.min(distanceScores.entrySet(), Map.Entry.comparingByValue());
        if (bestCategoryByDistance != null && bestCategoryByDistance.getValue() <= 10) {
            return bestCategoryByDistance.getKey();
        }

        return null;
    }

    public static HashMap<String, ArrayList<Transaction>> getCategories() {
        return categories;
    }
}
