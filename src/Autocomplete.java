import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Autocomplete implements IAutocomplete {
    
    Node root = new Node();
    int k = -1;
    
    @Override
    public void addWord(String word, long weight) {
        

        // edge case: if the word isn't valid, do nothing
        if (!isStringValid(word)) {
            return;
        }
        
        this.root.setPrefixes(this.root.getPrefixes() + 1);


        // starting from the root
        Node curNode = this.root;

        // for each char of the word to add except the last one
        for (int i = 0; i < word.length() - 1; i++) {
            char c = word.charAt(i);

            // check if c is already a child of curNode
            Node[] refs = curNode.getReferences();
            int index = (int) c - (int)'a';
            
            // if it isn't, create a node with empty term object
            if (refs[index] == null) {
                // refs[index] = new Node();
                refs[index] = new Node("", 0); // not sure if this is right
                refs[index].setPrefixes(1);
            } else { // else update prefixes of the node for c
                refs[index].setPrefixes(refs[index].getPrefixes() + 1);
            }
            
            // advance curNode
            curNode = refs[index];
        }

        // for the last char of word to add
        char c = word.charAt(word.length() - 1);
        Node[] refs = curNode.getReferences();
        int index = (int) c - (int)'a';

        if (refs[index] == null) { 
            refs[index] = new Node(word, weight);
            refs[index].setWords(1);
            refs[index].setPrefixes(1);
        } else { // if the node for the last char of word already exists
            refs[index].setWords(1); // update word flag
            refs[index].setTerm(new Term(word, weight)); // update term (should be empty before)
            refs[index].setPrefixes(refs[index].getPrefixes() + 1);
        }
    


    }

    @Override
    public Node buildTrie(String filename, int k) {
        this.root = new Node();
        this.k = k;

        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(filename));
            String line;
            try {
                int numberOfWords = Integer.parseInt(br.readLine().trim());

                // keep reading lines till reaching end of file
                for (int i = 0; i < numberOfWords; i++) {
                    
                    line = br.readLine();
                    
                    // tokens[0] = weight, tokens[1] = word
                    String[] tokens = line.trim().split("\\s+");
                    long weight = Long.parseLong(tokens[0]);
                    String word = tokens[1].toLowerCase();

                    // if (!word.matches("[a-z]+")) {
                    //     continue;
                    // }
                    
                    addWord(word, weight);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return this.root;
    }

    @Override
    public Node getSubTrie(String prefix) {
        
        // edge case
        if (prefix == null) {
            return null;
        }

        // edge case
        if (prefix == "") {
            return this.root;
        }

        // first convert all chars in prefix to lowercase
        prefix = prefix.toLowerCase();
        // if (!prefix.matches("[a-z]+")) {
        //     return null;
        // }
        if (!isStringValid(prefix)) {
            return null;
        }

        Node curNode = this.root;

        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (curNode.getReferences()[(int) c - 97] == null) {
                return null;
            } else {
                curNode = curNode.getReferences()[(int) c - 97];
            }
        }
        
        return curNode; 
    }


    @Override
    public int countPrefixes(String prefix) {
        // edge case
        if (prefix == null) {
            return 0;
        }

        if (!isStringValid(prefix)) {
            return 0;
        }

        Node node = getSubTrie(prefix);

        if (node == null) {
            return 0;
        }
        
        return node.getPrefixes();
    }

    @Override
    public List<ITerm> getSuggestions(String prefix) {
        


        List<ITerm> toReturn = new ArrayList<>();

        // If prefix isn't valid, return an empty list
        if (!isStringValid(prefix)) {
            return toReturn;
        }
        
        Node node = getSubTrie(prefix);
        


        getSuggestionsHelper(toReturn, node);

        return toReturn;
    }

    @Override
    public int numberSuggestions() {
        return this.k;
    }


    private void getSuggestionsHelper(List<ITerm> list, Node node) {
        
        // edge case: node is null
        if (node == null) {
            return;
        }
        
        // Base case: node is leaf
        if (node.isLeaf()) {
            Term copy = new Term(node.getTerm().getTerm(), node.getTerm().getWeight());
            list.add(copy);
            return;
        }
        
        // Edge case: node is not a leaf but forms a word
        if (node.getWords() == 1) {
            Term copy = new Term(node.getTerm().getTerm(), node.getTerm().getWeight());
            list.add(copy);
        }

        // Recursive calls on all children
        for (Node child : node.getReferences()) {
            if (child == null) {
                continue;
            } else {                
                getSuggestionsHelper(list, child);
            }
        }
    }


    public Node getRoot() {
        return this.root;
    }


    /**
     * Checks if str contains only alphabetical chars
     * @param str to check
     * @return true if str contains only alphabetical chars, false otherwise. 
     */
    private boolean isStringValid(String str) {
        // first convert str to lowercase
        str = str.toLowerCase();
        
        // if there's any char in str that's not an alphabetical char, 
        // return false
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < 'a' || c > 'z') {
                return false;
            }
        }

        // otherwise return true
        return true;
    }

}




