/*=========================================================*/
/*       					         					   */
/*	          Earley Parser             			       */
/*						       							   */
/*=========================================================*/
import java.io.*;
import java.util.*;
import java.lang.System;
import java.util.regex.*;


/**This class implements an Earley parser for CFG's
 * @author Suraj Didwania : dsuraj@hawk.iit.edu
 * @author Priyanka Makhijani :
 * @version 0.1
 * Assumption: We assume that only one root is present
 */
public class Earley {
    static ArrayList<ArrayList<DRule>> cols;
    static ArrayList<HashMap<DRule, Integer>> hashIndexes;
    static HashMap<String, ArrayList<ArrayList<Integer>>> hashInd;
    static HashMap<String, ArrayList<RuleBase>> rules;
    static HashMap<String, ArrayList<RuleBase>> prefixTableMap;
    static HashMap<String, ArrayList<String>> leftParent;
    static HashMap<String, ArrayList<String>> leftAncestor;
    static int attach_nullCount = 0;


    /**
     * task: To check if the given token is terminal or not
     * 		by checking hashmap rules and return true or false
     * 		and finally calls leftParent to put the 1st part
     * @param rules: Hashmap  to define rules assigned
     * @param token : token to define if the token is terminal or not
     * @return returns boolean
     */
    public static boolean isNonTerminal(HashMap<String, ArrayList<RuleBase>> rules, String token) {
        if (rules.containsKey(token))
            return true;   	//return true if it's a non-terminal
        else
            return false;
    }

    /**
     * task: read the file line by line using bufferedreader and assign Rule Base
     * 		then calls prefixTableMap to put the 1st part and 2nd part
     * 		and finally calls leftParent to put the 1st part
     * @param fileName: Filename to assign the rules to
     */
    public static HashMap<String, ArrayList<RuleBase>> getRules(String fileName) {
        rules = new HashMap<>();
        prefixTableMap = new HashMap<>();
        leftParent = new HashMap<>();
        BufferedReader rule_br = null; //load the file
        try {
            rule_br = new BufferedReader(new FileReader(new File(fileName)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String curr_string = null;

        try {
            while ((curr_string = rule_br.readLine()) != null) {
                if (curr_string.length() == 0)
                    continue;
                RuleBase r = new RuleBase(curr_string);

                if (!prefixTableMap.containsKey(r.par.get(0) + " " + r.par.get(1)))
                {
                    ArrayList<RuleBase> ruleArr = new ArrayList<>();
                    ruleArr.add(r);
                    prefixTableMap.put(r.par.get(0) + " " + r.par.get(1), ruleArr);

                    if (!leftParent.containsKey(r.par.get(1))) {
                        ArrayList<String> ar = new ArrayList<String>();
                        ar.add(r.par.get(0));
                        leftParent.put(r.par.get(1), ar);
                    } else {
                        leftParent.get(r.par.get(1)).add(r.getHead());
                    }

                }
                else {
                    prefixTableMap.get(r.par.get(0) + " " + r.par.get(1)).add(r);
                }
                if (rules.containsKey(r.getHead())) {
                    rules.get(r.getHead()).add(r);
                } else {
                    ArrayList<RuleBase> ar = new ArrayList<RuleBase>();
                    ar.add(r);
                    rules.put(r.getHead(), ar);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            rule_br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rules;

    }
    /**
     * task: read the file line by line using bufferedreader and assign Rule Base
     * 		then calls prefixTableMap to put the 1st part and 2nd part
     * 		and finally calls leftParent to put the 1st part
     * @param fileName: Filename to assign the rules to.
     * @return sentence: Hashmap sentence
     */
    public static ArrayList<String> getSentences(String fileName) throws Exception {
        ArrayList<String> sentence = new ArrayList<String>();
        BufferedReader sent = null;
        try {
            sent = new BufferedReader(new FileReader(new File(fileName)));  //load the file

            String curr = null;

            while ((curr = sent.readLine()) != null) {
                if (curr.length() == 0)
                    continue;
                sentence.add(curr);
            }
            sent.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sentence;

    }
    /**
     * task: to add rule onto hashmap and define and see if the rule is
     * 		finished or not
     * 		Insert onto hashmaps
     * @param colIdx: column index
     * @param dotRule: Drule class object
     */
    public static void addRule(int colIdx, DRule dotRule) throws IOException {
        cols.get(colIdx).add(dotRule);
        hashIndexes.get(colIdx).put(dotRule, cols.get(colIdx).size() - 1);

        if (!dotRule.isFinished()) {
            int ruleIndex = cols.get(colIdx).size() - 1;
            String nextToken = dotRule.nextToken();

            if (!hashInd.containsKey(nextToken)) {
                ArrayList<ArrayList<Integer>> arr = new ArrayList<>();
                for (int i = 0; i < cols.size(); i++) {
                    arr.add(new ArrayList<>());
                }
                arr.get(colIdx).add(ruleIndex);
                hashInd.put(dotRule.nextToken(), arr);
            } else {
                hashInd.get(dotRule.nextToken()).get(colIdx).add(ruleIndex);
            }
        }
    }

    /**
     * task:this function predicts possible completions of p
     * 		, and adds them to v. It checks left Ancestor and check the value
     * @param colIdx: column index
     * @param nextToken: Token to check if terminal or not
     * @return : Null
     */
    public static void predict(String nextToken, int colIdx) throws IOException {
        ArrayList<String> selectNT = leftAncestor.get(nextToken);
        ArrayList<RuleBase> ruleSet = new ArrayList<>();
        if (selectNT != null) {
            for (String s : selectNT) {
                for (RuleBase r : prefixTableMap.get(nextToken + " " + s)) {
                    ruleSet.add(r);
                }
            }
        }
        HashMap<DRule, Integer> hashInd = hashIndexes.get(colIdx);

        if (ruleSet.size() > 0) {
            RuleBase r1 = ruleSet.get(0);
            DRule dr1 = new DRule(r1, 0, colIdx);

            if (!hashInd.containsKey(dr1))
            {
                for (RuleBase r : ruleSet)
                {
                    DRule dr = new DRule(r, 0, colIdx);
                    addRule(colIdx, dr);
                }
            }
        }
    }
    /**
     * task:  this checks if we can scan s on the current production p
     * 		, and adds them to hashmap. It checks and add into rules if assigned
     * @param colIdx: column index
     * @param currWord: Current word in the grammar
     * @param currRule: Rules assigned currently.
     * @return : boolean value
     */
    public static boolean scan(DRule currRule, String currWord, int colIdx)  throws Exception {
        HashMap<DRule, Integer> hashIndex = hashIndexes.get(colIdx + 1);
        assert (!currRule.isFinished());
        String token = currRule.nextToken();
        if (!token.equals(currWord)) {
            return false;
        }
        else
        {
            DRule newRule = new DRule(currRule);
            newRule.dotPos += 1;

            newRule.bckPointer1 = currRule;
            newRule.bckPointer2 = null;

            if (!hashIndex.containsKey(newRule))
            {
                try {
                    addRule(colIdx + 1, newRule);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            } else
            {
                DRule oldRule = cols.get(colIdx + 1).get(hashIndex.get(newRule));
                if (newRule.weight < oldRule.weight)
                {
                    cols.get(colIdx + 1).set(hashIndex.get(oldRule), null);
                    try {
                        addRule(colIdx + 1, newRule); // add new Rule
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {

                    assert (false);
                }

                return false;
            }
        }
    }


    /**
     * task:  this takes a completed production and tries to attach it back in the
     * 		, cols table, putting any attachments into cur.
     * 	        if the next thing in one rule is the first thing in this rule,
     * 	        we attach.  otherwise ignore
     * @param colIdx: column index
     * @param dr: Production assigned
     * @return : NO
     */
    public static void attach(DRule dr, int colIdx)  throws Exception {
        HashMap<DRule, Integer> hashIndex = hashIndexes.get(colIdx);
        String token = dr.rule.getHead();
        double drWeight = dr.weight;
        ArrayList<DRule> prevCol = cols.get(dr.colNum);
        ArrayList<DRule> currCol = cols.get(colIdx);
        if (hashInd.containsKey(token)) {

            ArrayList<Integer> iter = hashInd.get(token).get(dr.colNum);
            for (int i : iter)
            {
                DRule tmp = prevCol.get(i);
                if (tmp == null)
                    continue;

                DRule newRule = new DRule(tmp);
                newRule.dotPos += 1;
                newRule.weight += drWeight;

                newRule.bckPointer1 = tmp;
                newRule.bckPointer2 = dr;
                if (!hashIndex.containsKey(newRule))
                {
                    try {
                        addRule(colIdx, newRule);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    DRule drd = currCol.get(hashIndex.get(newRule));
                    assert (hashIndex.get(drd) == hashIndex.get(newRule));

                    if (newRule.weight < drd.weight) // compare weights of two rules
                    {
                        attach_nullCount++;
                        currCol.set(hashIndex.get(drd), null);
                        try {
                            addRule(colIdx, newRule);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        }
    }

    /**
     * task:  this takes a completed finalrules and check if the rules are finished
     * 		if so it goes for recursion onto left tree and right tree.
     * 	        Preorder traversal of the algorithm
     * @param finalRules: Final rules assigned in the grammar
     * @param dr: Production assigned
     * @return : NO
     */
    public static void printTrace(ArrayList<RuleBase> finalRules, DRule dr)  throws Exception {
        if (dr != null) {
            if (dr.isFinished()) {
                finalRules.add(dr.rule);
            }
            printTrace(finalRules, dr.bckPointer1);
            printTrace(finalRules, dr.bckPointer2);
        }
    }
    /**task: this prints the table in a human-readable fashion.
     * format is one column at a time, lists the word in the sentence
     * and then the productions for that column.
     * @param index: The columns of the table
     * @param finalRules :Final rules assigned in the grammar
     */
    static int j = 0;

    public static void printParse(ArrayList<RuleBase> finalRules, int index)   throws Exception{

        j++;
        RuleBase curr = finalRules.get(index);
        //this is a vector of vectors, storing the columns of the table
        if (index == finalRules.size() - 1) {
            System.out.print("(" + curr.getHead());
            for (int i = 1; i < curr.par.size(); i++) {
                System.out.print(" " + curr.par.get(i));
            }
            System.out.print(" )");
            return;
        }
        System.out.print("(" + curr.getHead() + " ");

        for (int i = 1; i < curr.par.size(); i++) {
            if (curr.par.get(i).equals(finalRules.get(j).getHead())) {
                printParse(finalRules, j);
            }
            //scan adds productions to next
            else if (!isNonTerminal(rules, curr.par.get(i))) {
                System.out.print(curr.par.get(i) + " ");
            }
        }
        System.out.print(")");
    }

    /**task: this finds the left ancestor and fill the tree.
     * format is one column at a time, lists the word in the sentence
     * and then the productions for that column.
     * @param token: token to define if the token is terminal or not
     *
     */

    public static void fillLeftAncestor(String token) throws Exception {
        if (leftParent.get(token) == null)
            return;

        for (String s : leftParent.get(token)) {
            if (!leftAncestor.containsKey(s)) {
                ArrayList<String> ar = new ArrayList<String>();
                ar.add(token);
                leftAncestor.put(s, ar);
                fillLeftAncestor(s);
            } else {
                if (!leftAncestor.get(s).contains(token)) {
                    leftAncestor.get(s).add(token);
                }
            }
        }
    }
    /**The main function, it reads in a grammar and a file to tag, or gets
     * sentences from the command line.
     *
     * There are two modes, interactive and batch.
     * Interactive: java earleyParser grammar
     * Batch: java earleyParser grammar sentence_file
     *
     * @param argv the standard command line argument array
     */
    public static void main(String args[]) throws Exception,IOException {
        final String gramFile = args[0];
        final String senFile = args[1];
        rules = getRules(gramFile);
        ArrayList<String> sentences = getSentences(senFile);
        leftAncestor = new HashMap<>();

        for (String sentence : sentences) {
            ArrayList<RuleBase> finalRules = new ArrayList<>();

            String[] sent = sentence.split("\\s+");
            int len = sent.length;

            cols = new ArrayList<>();
            for (int i = 0; i < len + 1; i++) {
                ArrayList<DRule> tmp = new ArrayList<>();
                cols.add(tmp);
            }

            hashIndexes = new ArrayList<>();
            for (int i = 0; i < len + 1; i++) {
                HashMap<DRule, Integer> tmp = new HashMap<>();
                hashIndexes.add(tmp);
            }

            hashInd = new HashMap<>();

            for (RuleBase rootRule : rules.get("ROOT"))
            {
                DRule rootDotRule = new DRule(rootRule, 0, 0);
                addRule(0, rootDotRule);

            }
            for (int i = 0; i <= sent.length; i++)
            {
                if (i < sent.length) {
                    leftAncestor.clear();
                    fillLeftAncestor(sent[i]);
                }
                ArrayList<DRule> currCol = cols.get(i);
                for (int j = 0; j < currCol.size(); j++) {

                    DRule currRule = currCol.get(j);
                    if (currRule == null)
                    {
                        continue;
                    }
                    if (!currRule.isFinished())
                    {
                        String nextToken = currRule.nextToken();

                        if (isNonTerminal(rules, nextToken)) {

                            predict(nextToken, i);

                        } else {


                            if (i != sent.length)
                                scan(currRule, sent[i], i);

                        }
                    } else {

                        attach(currRule, i);
                    }
                }
            }

            DRule finalRule = null;
            int pass = 0;
            /* Task: Right now we simply check to see if a parse exists;
            *       in other words, we see if there's a "ROOT -> x x x ."
            *       production in the last column.  If there is, it's returned; otherwise return null.
            */
            for (RuleBase rootRul : rules.get("ROOT")) {
                finalRule = new DRule(rootRul, rootRul.par.size() - 1, 0);
                HashMap<DRule, Integer> hashIndex = hashIndexes.get(sent.length);

                if (hashIndex.containsKey(finalRule)) {
                    printTrace(finalRules, cols.get(sent.length).get(hashIndex.get(finalRule)));
                    j = 0;
                    printParse(finalRules, 0);

                    System.out.println();

                    System.out.println(cols.get(sent.length).get(hashIndex.get(finalRule)).weight);
                    pass = 1;
                    break;
                }

            }
            if (pass == 0)
                System.out.println("NONE");

        }
    }
}
