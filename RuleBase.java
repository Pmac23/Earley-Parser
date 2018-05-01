import java.util.ArrayList;
/**
 * @author Suraj Didwania
 * @author Priyanka Makhijani
 * Rule Base Implementation
 */
class RuleBase {
    ArrayList<String> par;
    String aux;
    double size;
    double weight;

    String getHead() {
        return par.get(0);
    }
    /**
     * task:  Constructor to define the rulebase and all its parameters
     */
    RuleBase(String sent) {
        this.par = new ArrayList<>();
        String[] tok = sent.split("\\s+");
        String[] auxs = sent.split("\\s+", 2);
        this.aux = auxs[1];
        double proba = Double.parseDouble(tok[0]);
        this.weight = -(Math.log(proba) / Math.log(2));
        for (int i = 1; i < tok.length; i++) {
            par.add(tok[i]);
        }
        this.size = par.size();
        assert (this.size == tok.length - 1);

    }
    /**
     * task:  this takes a otherrule and tries to equate it
     * @param otherRule: column index
     * @return : boolean
     */
    @Override
    public boolean equals(Object otherRule) {

        if (!(otherRule instanceof RuleBase))
            return false;

        RuleBase other = (RuleBase) otherRule;
        for (int i = 0; i < this.par.size(); i++) {
            if (!this.par.get(i).equals(other.par.get(i)))
                return false;
        }
        assert (this.weight == other.weight);
        return true;

    }
    /**
     * task:  this returns hashcode
     * @return : integer
     */
    @Override
    public int hashCode() // to set rule as key to hashTable
    {
        return this.aux.hashCode();
    }

    @Override
    public String toString() // debug
    {
        String s = this.weight + " " + this.getHead() + " --> ";
        for (int i = 1; i < par.size(); i++) {
            s = s + par.get(i) + " ";
        }
        return s;
    }

}