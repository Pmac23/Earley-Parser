/**
 * @author Suraj Didwania
 * @author Priyanka Makhijani
 * this reads the grammar in from a file
 */
public class DRule {
    RuleBase rule;
    int dotPos;
    int colNum;
    double weight;
    DRule bckPointer1;
    DRule bckPointer2;
    int identifier;

    DRule(DRule other) {
        this.rule = other.rule;
        this.dotPos = other.dotPos;
        this.colNum = other.colNum;
        this.weight = other.weight;
        this.bckPointer1 = null; // backpointers are not copied
        this.bckPointer2 = null; // backpointers are not copied
        this.identifier = other.identifier;

        assert (this.dotPos <= this.rule.size - 1);
    }
    DRule(RuleBase rule, int dotPos, int colNum) {
        this.rule = rule;
        this.dotPos = dotPos;
        this.colNum = colNum;
        this.weight = this.rule.weight;
        this.bckPointer1 = null; // set backpointers to null
        this.bckPointer2 = null; // set backpointers to null
        this.identifier = 0;
        assert (this.dotPos <= this.rule.size - 1);
    }
    /**
     * task:  Finished or not the dotPos
     * @return : boolean
     */
    boolean isFinished()
    {
        if (dotPos == rule.par.size() - 1)
            return true;
        else
            return false;
    }
    /**
     * task:  this return the netxToken present in the grammar
     * @return : String
     */
    String nextToken() {
        if (this.isFinished())
            return null;
        else
            return this.rule.par.get(this.dotPos + 1);
    }
    /**
     * task:  this takes a otherrule and tries to equate it
     * @param otherRule: column index
     * @return : boolean
     */
    @Override
    public boolean equals(Object otherRule) {
        if (!(otherRule instanceof DRule))
            return false;

        DRule other = (DRule) otherRule;

        if (this.colNum != other.colNum)
            return false;

        if (this.dotPos != other.dotPos)
            return false;

        if (!this.rule.equals(other.rule))
            return false;

        return true;
    }
    /**
     * task:  this returns hashcode
     * @return : integer
     */
    @Override
    public int hashCode() {
        int result = 17;
        final int prime = 31;

        result = result * prime + this.colNum;
        result = result * prime + this.dotPos;
        result = result * prime + this.rule.hashCode();

        return result;
    }
    /**
     * task:  what does every toString function do?
     * @return : String
     */
    @Override
    public String toString() {
        String str = this.colNum + " " + this.rule.getHead() + " --> ";
        for (int i = 1; i < this.rule.par.size(); i++) {
            if (this.dotPos == i - 1)
                str = str + ". ";

            str = str + this.rule.par.get(i) + " ";
        }
        if (this.dotPos == this.rule.par.size() - 1)
            str = str + ".";

        str = str + " (" + this.weight + ") "; // add Rule weight also

        return str;
    }
}
