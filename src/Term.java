public class Term implements ITerm {

    // added instance variables
    String term;
    long weight;

    /**
     * Initialize a Term with a given query String and weight
     */
    public Term(String term, long weight) {
        
        if (term == null || weight < 0) {
            throw new IllegalArgumentException();
        }

        this.term = term;
        this.weight = weight;

    }

    // =================
    // Overrides!
    // =================
    // done (i think)
    @Override
    public int compareTo(ITerm that) {

        return this.term.compareTo(((Term) that).getTerm());
    }

    // i guess this should work? 
    @Override
    public String toString() {
        return this.weight + "\t" + this.term;
    }

    // done

    public long getWeight() {
        return this.weight;
    }

    // done

    public String getTerm() {
        return this.term;
    }

    // done

    public void setWeight(long weight) {
        this.weight = weight;
    }

    // done

    public String setTerm(String term) {
        this.term = term;
        return this.term;
    }


}
