import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class IndexBuilder implements IIndexBuilder {

    /**
     * <parseFeed> Parse each document/rss feed in the list and return a Map of
     * each document and all the words in it. (punctuation and special
     * characters removed)
     *
     * @param feeds a List of rss feeds to parse
     * @return a Map of each document (identified by its url) and the list of
     * words in it.
     */
    @Override
    public Map<String, List<String>> parseFeed(List<String> feeds) {
        Map<String, List<String>> ret =  new HashMap<>();

        for (String feed : feeds) {
            Document doc = null;
            try {
                doc = Jsoup.connect(feed).get();
                Elements links = doc.getElementsByTag("link");
                for (Element link : links){
                    //do something with link
                    // linkText is a URL
                    String linkText = link.text();
                    Document linkDoc = Jsoup.connect(linkText).get();
                    Elements body = linkDoc.getElementsByTag("body");
                    // Get the content (String) from body object
                    String text = body.text();
                    // Clean the string
                    text = text.trim();
                    List<String> listOfWords = new ArrayList<>();
                    String[] words = text.split("\\s+");
                    for (String word : words) {

                        word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
                        listOfWords.add(word);
                    }

                    ret.put(linkText, listOfWords);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return ret;
    }

    /**
     * @param docs a map computed by {@parseFeed}
     * @return the forward index: a map of all documents and their
     * tags/keywords. the key is the document, the value is a
     * map of a tag term and its TFIDF value.
     * The values (Map<String, Double>) are sorted
     * by lexicographic order on the key (tag term)
     */
    @Override
    public Map<String, Map<String, Double>> buildIndex(Map<String, List<String>> docs) {

        // This will be used for calculating idf scores
        int totalNumberOfDocs = docs.size();

        Map<String, Map<String, Double>> ret = new TreeMap<>();

        // This maps each word to # of docs containing the word
        Map<String, Double> wordNumDocsMap = new HashMap<>();



        // This map maps each doc(URL) to a map that maps each word to their freq in that doc
//        Map<String, Map<String, Double>> mapOfWordFreqMapEachDoc = new HashMap<>();

        // Each entry is a doc and a list of words in that doc
        for (Map.Entry<String, List<String>> entry : docs.entrySet()) {
            String docURL = entry.getKey();
            List<String> docListOfWords = entry.getValue();

            // This is the size of the list of words in each doc
            int eachDocSize = docListOfWords.size();

            // Calculate how many times each word appears in a doc
            // Store the result in wordFreqMapEachDoc
            Map<String, Double> wordFreqMapEachDoc = new HashMap<>();

            for (String word : docListOfWords) {
                Double count = wordFreqMapEachDoc.get(word);
                if (count == null) {
                    wordFreqMapEachDoc.put(word, 1.0);
                } else {
                    wordFreqMapEachDoc.put(word, count + 1.0);
                }
            }

//            mapOfWordFreqMapEachDoc.put(docURL, wordFreqMapEachDoc);

            // Calculate tf score for each word in the doc
            // Each entry2 is a unique word and its freq in each doc
            Map<String, Double> tfOfEachWordInEachDoc = new HashMap<>();
            for (Map.Entry<String, Double> entry2 : wordFreqMapEachDoc.entrySet()) {
                String word = entry2.getKey();
                double freq = entry2.getValue();

                // update map counting how many docs contain "word"
                Double count = wordNumDocsMap.get(word);
                if (count == null) {
                    wordNumDocsMap.put(word, 1.0);
                } else {
                    wordNumDocsMap.put(word, count + 1.0);
                }

                tfOfEachWordInEachDoc.put(word, (double) (freq/eachDocSize));
            }

            // We will update ret later
            // We just temporarily store each doc and a map mapping each word to its tf score
            ret.put(docURL, tfOfEachWordInEachDoc);

        }


        for (Map.Entry<String, Map<String, Double>> entry3 : ret.entrySet()) {

            String docURL = entry3.getKey();
            Map<String, Double> tfOfEachWordInEachDoc = entry3.getValue();

            for (Map.Entry<String, Double> entry4 : tfOfEachWordInEachDoc.entrySet()) {
                String word = entry4.getKey();
                Double tfScore = entry4.getValue();

                Double idfScore = Math.log(totalNumberOfDocs / wordNumDocsMap.get(word));

                Double tfidf = tfScore * idfScore;

                // overwrite the tf score for word with tf idf score
                tfOfEachWordInEachDoc.put(word, tfidf);
            }

            ret.put(docURL, tfOfEachWordInEachDoc);

        }

        return ret;
    }

    /**
     * Build an inverted index consisting of a map of each tag term and a Collection (Java)
     * of Entry objects mapping a document with the TFIDF value of the term
     * (for that document)
     * The Java collection (value) is sorted by reverse tag term TFIDF value
     * (the document in which a term has the
     * highest TFIDF should be listed first).
     *
     * @param index the index computed by {@buildIndex}
     * @return inverted index - a sorted Map of the documents in which term is a keyword
     */
    @Override
    public Map<?, ?> buildInvertedIndex(Map<String, Map<String, Double>> index) {
        // Use AbstractMap.SimpleEntry

        // Maybe Map<String, List<SimpleEntry>>
        return null;
    }

    /**
     * @param invertedIndex
     * @return a sorted collection of terms and articles Entries are sorted by
     * number of articles. If two terms have the same number of
     * articles, then they should be sorted by reverse lexicographic order.
     * The Entry class is the Java abstract data type
     * implementation of a tuple
     * https://docs.oracle.com/javase/9/docs/api/java/util/Map.Entry.html
     * One useful implementation class of Entry is
     * AbstractMap.SimpleEntry
     * https://docs.oracle.com/javase/9/docs/api/java/util/AbstractMap.SimpleEntry.html
     */
    @Override
    public Collection<Map.Entry<String, List<String>>> buildHomePage(Map<?, ?> invertedIndex) {
        // Collection can list or TreeSet (sorted)
        // Inside Collection, sorted 1st by List<String>.size(), and then by String.compareTo (alphabetical order)
        return null;
    }

    /**
     * Create a file containing all the words in the inverted index. Each word
     * should occupy a line Words should be written in lexicographic order
     * assign a weight of 0 to each word. The method must store the words into a
     * file named autocomplete.txt
     *
     * @param homepage the collection used to generate the homepage (buildHomePage)
     * @return A collection containing all the words written into the file sorted by lexicographic order
     */
    @Override
    public Collection<?> createAutocompleteFile(Collection<Map.Entry<String, List<String>>> homepage) {
        // (For auto-grader) first line written into the file should be the number of terms

        return null;
    }

    /**
     * @param queryTerm
     * @param invertedIndex
     * @return
     */
    @Override
    public List<String> searchArticles(String queryTerm, Map<?, ?> invertedIndex) {

        // Cast the argument (invertedIndex) before using it
        List<String> ret = new ArrayList<>();
        // if queryTerm isn't in invertedIndex, return an empty list
        if (invertedIndex.containsKey(queryTerm)) {
            return ret;
        }
        // iterate through invertedIndex

        return null;
    }
}
