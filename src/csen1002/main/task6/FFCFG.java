package csen1002.main.task6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Write your info here
 * 
 * @name Mostafa Mohamed Abdelnasser
 * @id 43-8530
 * @labNumber 11
 */

public class FFCFG {

	/**
	 * Constructs a CFG for which the First and Follow are to be computed
	 *
	 * @param description A string representation of a CFG as specified in the task
	 *                    description
	 */
	HashMap<String, ArrayList> CFG = new HashMap<String, ArrayList>();
	HashMap<String, ArrayList> First = new HashMap<String, ArrayList>();
	HashMap<String, ArrayList> Follow = new HashMap<String, ArrayList>();
	ArrayList<String> varIndex = new ArrayList<String>();
	public FFCFG(String description) {
		String[] rules = description.split(";");
		for(String rule : rules){
			//Start parsing productions
			String[] productions = rule.split(",");
			//First string is the variable name
			String var = productions[0];
			varIndex.add(var);
			//Initialize the arraylist of productions
			ArrayList<String> productionsList = new ArrayList<String>();

			//Loop on the parsed productions and add them to the list
			for(int i = 1;i < productions.length;i++){
				String production = productions[i];
				productionsList.add(production);
				// Get every terminal and add it to the hashmap
				for(Character c : production.toCharArray()){
					if(Character.isLowerCase(c)) {
						ArrayList<String> tmp = new ArrayList<String>();
						tmp.add(c + "");
						First.put(c + "", tmp);
						Follow.put(c + "", new ArrayList<String>());
					}
				}
			}
			//Add the list to the CFG HashMap
			CFG.put(var,productionsList);

			// Initialize the First HashMap
			First.put(var, new ArrayList<>());
			Follow.put(var, new ArrayList<>());

		}
		Follow.get("S").add("$");
	}

	/**
	 * Calculates the First of each variable in the CFG.
	 * 
	 * @return A string representation of the First of each variable in the CFG,
	 *         formatted as specified in the task description.
	 */
	public String first() {
		// Initialize output variables
		String out = "";
		// Loop on the variables


				// Check if it's a terminal, then that's our first
		boolean change = true;
		while(change){
		change = false;
			for(String variable : varIndex){
				// Get the productions
				ArrayList<String> productions = CFG.get(variable);
				// Get the first arraylist for this variable
				ArrayList<String> firstList = First.get(variable);
				// Loop on each production
					// Loop on every production for the variable
					for (String production : productions){
						char[] prodChars = production.toCharArray();
						// Check for letter epsilons
						boolean eps = true;
						for(char c : prodChars){
							String bVariable = c+"";
							ArrayList<String> bFirstList = First.get(bVariable);
							if(!bFirstList.contains("e")) {
								eps = false;
								break;
							}
						}
						if(eps)
							if(!firstList.contains("e")) {
								firstList.add("e");
								change = true;
							}
						for(int i = 0;i < prodChars.length;i++){
							String bVariable = prodChars[i]+"";
							// Check for epsilons
							boolean eps2 = true;
							for(int j = 0;j < i;j++) {
								eps2 = true;
								String cVariable = prodChars[j]+"";
								ArrayList<String> cFirstList = First.get(cVariable);
								if(!cFirstList.contains("e")) {
									eps2 = false;
									break;
								}
							}
							if(eps2) {
								// Add all first terminals
								ArrayList<String> bFirstList = First.get(bVariable);
								for(String terminal : bFirstList)
									if(!firstList.contains(terminal) && !terminal.equals("e") && Character.isLowerCase(terminal.charAt(0))) {
										firstList.add(terminal);
										change = true;
									}
							}
						}
					}
				}
			}
		for(String variable : varIndex){
			ArrayList<String> firstList = First.get(variable);
			Object[] sorted = firstList.toArray();
			Arrays.sort(sorted);
			out += variable + ",";
			for(Object s : sorted)
				out += s;
			out += ";";
		}
		return out.substring(0,out.length()-1);
	}

	private void firstHelper(String production, ArrayList<String> firstList) {
		if(Character.isLowerCase(production.charAt(0))){
			firstList.add(production.charAt(0)+"");
		}
		if(Character.isUpperCase(production.charAt(0))){
			ArrayList<String> nxtFirstList = new ArrayList<>();
			firstHelper(production.charAt(0)+"", nxtFirstList);
			// Check all the firsts in each production if they contain epsilon
			boolean eps = true;
			for(char var : production.toCharArray()){
				ArrayList<String> currentVariableFirst = First.get(var+"");
				if(Character.isLowerCase(var) || !currentVariableFirst.contains("e")) {
					eps = false;
					break;
				}
			}
			if(eps)
				firstList.add("e");
		}

		return ;
	}

	/**
	 * Calculates the Follow of each variable in the CFG.
	 * 
	 * @return A string representation of the Follow of each variable in the CFG,
	 *         formatted as specified in the task description.
	 */
	public String follow() {
		first();
		String out = "";
		boolean change = true;
		while(change){
			change = false;
			for(String variable : varIndex) {
				// Get the productions
				ArrayList<String> productions = CFG.get(variable);
				// Get the first arraylist for this variable
				ArrayList<String> followList = Follow.get(variable);
				// Loop on each production
				// Loop on every production for the variable
				for (String production : productions) {
					char[] prodChars = production.toCharArray();
					int i = 0;
					int j = 1;
					while(i < prodChars.length){
						//String alpha = prodChars[i-1]+"";
						String bVariable = prodChars[i]+"";
						String beta;
						if(j == prodChars.length)
							beta = "e";
						else
							beta = prodChars[j]+"";
						// Add all first of beta terminals
						ArrayList<String> bFollowList = Follow.get(bVariable);
						ArrayList<String> betaFirstList = First.get(beta);
						for(String terminal : betaFirstList){
							if(!bFollowList.contains(terminal)
									&& !terminal.equals("e")) {

								bFollowList.add(terminal);
								change = true;
							}
						}
						if(betaFirstList.contains("e")){
							j++;
							if(j >= prodChars.length)
								for(String terminal : followList)
									if(!bFollowList.contains(terminal) ) {
										bFollowList.add(terminal);
										change = true;
									}
						}
						else {
							i++;
							j = i + 1;
						}
						if(j > prodChars.length) {
							i++;
							j = i + 1;
						}
					}
				}
			}
		}
		for(String variable : varIndex){
			ArrayList<String> followList = Follow.get(variable);
			boolean flag = false;
			if(followList.contains("$")) {
				flag = true;
				followList.remove("$");
			}
			Object[] sorted = followList.toArray();
			Arrays.sort(sorted);
			out += variable + ",";
			for(Object s : sorted)
				out += s;
			if(flag)
				out += "$";
			out += ";";
		}
		return out.substring(0,out.length()-1);
	}
}
