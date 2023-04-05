/**
 * @author Harry Smith
 */

public class Node {

    private Term term;
    private int words;
    private int prefixes; 
    // A node's prefixes = sum of all its children's prefixes + the node's words
    private Node[] references;

    final int alphabetSize = 26;

    /**
     * Initialize a Node with an empty string and 0 weight; useful for
     * writing tests.
     */
    public Node() {
        
        this.term = new Term("", 0);


        // The following lines of code should exist in both constructors 
        // initialize all references to null 
        this.references = new Node[26];
        for (int i = 0; i < alphabetSize; i++) {
            this.references[i] = null;
        }


        // not sure about the following 2 lines
        this.words = 0;
        this.prefixes = 0;
        
    }

    /**
     * Initialize a Node with the given query string and weight.
     * @throws IllegalArgumentException if query is null or if weight is negative.
     */
    public Node(String query, long weight) {

        if (query == null || weight < 0) {
            throw new IllegalArgumentException();
        }

        this.term = new Term(query, weight);


        // The following lines of code should exist in both constructors 
        // initialize all references to null 
        this.references = new Node[26];
        for (int i = 0; i < alphabetSize; i++) {
            this.references[i] = null;
        }

        // not sure about the following 2 lines
        this.words = 0;
        this.prefixes = 0;
                

    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public int getWords() {
        return words;
    }

    public void setWords(int words) {
        this.words = words;
    }

    public int getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(int prefixes) {
        this.prefixes = prefixes;
    }

    public Node[] getReferences() {
        // Node[] copy = new Node[alphabetSize];
        // for (int i = 0; i < this.references.length; i++) {
        //     copy[i] = this.references[i];
        // }

        // return copy;
        return this.references;
    }

    public void setReferences(Node[] references) {
        this.references = references;
    }

    public boolean isLeaf() {
        Node[] refs = this.references;

        // iterate through refs
        for (int i = 0; i < refs.length; i++) {
            // if there's a valid children pointer, it's not a leaf
            if (refs[i] != null) {
                return false;
            }
        }

        return true;
    }
}
