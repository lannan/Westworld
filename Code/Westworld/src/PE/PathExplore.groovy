package PE

import Z3.STPSolver

class PathExplore {
	
	//List for PC package
	ArrayList<PCPackage> pcPackageList = new  ArrayList<PCPackage>();
	//set for unsatisfied path condition
	Set<String> unSATSet = new HashSet<String>();
	
	//Profiling info
	String profilingInfo;

	//to record how many we have negated
	int negateNumber;

	
	//constructed function
	public PathExplore(ArrayList<PCPackage> pcPackageList, Set<String> unSATSet) 
	{
		this.pcPackageList = pcPackageList;
		
		this.unSATSet = unSATSet;

		this.negateNumber = 0;		
	}
	
	//explore a list of pc and get the solutions with bound
	public ArrayList<SolutionPackage> explorePath() 
	{
		//Prepare a work list
		ArrayList<PCPackage> workList = new  ArrayList<PCPackage>();
		workList.addAll(pcPackageList);
		
		//prepare a solution list
		ArrayList<SolutionPackage> solutionList = new ArrayList<SolutionPackage>();
		
		
		for(int i = 0; i < workList.size(); i++) 
		{
			PCPackage pck = workList.get(i);
			ArrayList<SolutionPackage> temp = getChildPC(pck);
			solutionList.addAll(temp);
			
		}
		
		return solutionList;	
	}
	
	//communicate with STP(Z3) 
	private ArrayList<SolutionPackage> getChildPC(PCPackage pck) 
	{
		ArrayList<String> declare = pck.getDeclare();
		String PCbound = pck.getPathBound();
		String profilingInfo;
		ArrayList<String> pathConditions = pck.getPC();
		ArrayList<String> negatedPC = new ArrayList<String>();
		
		Set<String> set = new HashSet<String>();
		
		ArrayList<String> ifCountList = pck.getIfCount();
		
		
		//the return list
		ArrayList<SolutionPackage> solutionList = new ArrayList<SolutionPackage>();
		
		//No more new conditions, so no new solution
		if(PCbound == pathConditions.size().toString()) 
		{
			return solutionList;
		}
		
		if(PCbound == "null")
		{
			//negate all the path conditions
			for(int i = 0; i < pathConditions.size(); i++) 
			{
				String pc = pathConditions.get(i);
				//PROCESS PC
				pc = negate(pc);
				negateNumber = negateNumber + 1;
				
				//add all the asserts before this assert
				for(int j = 0; j < i; j++) {
					String temp = pathConditions.get(j);
					negatedPC.add(temp);
				}
				//finish the assert part
				negatedPC.add(pc);
				//record the new PC bound
				int newPCbound = i;
				profilingInfo = "";
				for(int j = 0 ; j < ifCountList.size(); j++) 
				{
					String ifCountStr = ifCountList.get(j);
					int ifCount = Integer.parseInt(ifCountStr);
					if(newPCbound<ifCount)
					{
						profilingInfo = profilingInfo;
					}
					else 
					{
						ArrayList<String> profilingInfoList = pck.getProfilingInfo();
						profilingInfo = profilingInfo + profilingInfoList.get(j)
					}
				}
				
//				if(newPCbound<ifCount)
//				{
//					profilingInfo = "";
//				}
//				else 
//				{
//					profilingInfo = pck.getProfilingInfo();
//				}
				//generate the SMT file for STP
				String outputFilePath = generateSMTfile(declare, negatedPC, profilingInfo);
//				//get the solution
				STPSolver stp = new STPSolver(outputFilePath);
//				
				String solution = stp.getSolution();
				
				println "solution: $solution"
				println "newBound: $newPCbound"
				
				if(solution != "No Solution") 
				{
					if(unSATSet.contains(pc))
					{
						unSATSet.remove(pc);
					}
					
					solution = processSolution(solution);
					
					//make the package
					SolutionPackage sp = new SolutionPackage(solution, newPCbound);
					//add into solution list
					solutionList.add(sp);
				}
				else 
				{
					unSATSet.add(pc);
				}	
			}
			
		}
		else 
		{
			//negate all the path conditions after pc bound
			int tempInt = Integer.parseInt(PCbound)+1;
			for(int i = tempInt; i < pathConditions.size(); i++) 
			{
				profilingInfo = "";
				String pc = pathConditions.get(i);
				
				pc = negate(pc);
				negateNumber = negateNumber + 1;
				
				//add all the asserts before this assert
				for(int j = 0; j < i; j++) {
					String temp = pathConditions.get(j);
					negatedPC.add(temp);
				}
				//finish the assert part
				negatedPC.add(pc);
				
				//record the new PC bound
				int newPCbound = i;
				for(int j = 0 ; j < ifCountList.size(); j++) 
				{
					String ifCountStr = ifCountList.get(j);
					int ifCount = Integer.parseInt(ifCountStr);
					if(newPCbound<ifCount)
					{
						profilingInfo = "";
					}
					else 
					{
						ArrayList<String> profilingInfoList = pck.getProfilingInfo();
						profilingInfo = profilingInfo + profilingInfoList.get(j)
					}
				}
				//generate the SMT file for STP
				String outputFilePath = generateSMTfile(declare, negatedPC, profilingInfo);
				//get the solution
				STPSolver stp = new STPSolver(outputFilePath);
				
				String solution = stp.getSolution();
				
				println "solution: $solution"
				println "newBound: $newPCbound"
				
				if(solution != "No Solution") 
				{
					// check if this pc has been recorded as no solution if yes remove it
					if(unSATSet.contains(pc)) 
					{
						unSATSet.remove(pc);
					}
					solution = processSolution(solution);
					
					//make the package
					SolutionPackage sp = new SolutionPackage(solution, newPCbound);
					//add into solution list
					solutionList.add(sp);
				}
				else
				{
					unSATSet.add(pc);
				}
				
			}
		}
		
		return solutionList;
	}
	
	
	private String negate(String orignal) 
	{
		String[] temp = orignal.split("assert");
		String newPC = "(assert (not" + temp[1] + ")"
		return newPC;
	}
	
	private String processSolution(String solution) 
	{
		String out = "";
		String[] temp = solution.split("\\(define-fun ");
		for(int i = 1; i< temp.size(); i++) 
		{
			String[] temp2 = temp[i].split(" \\(\\) ");
			String name = temp2[0];
			String[] temp3 = temp2[1].split("\n  ");
			String value = temp3[1].replace(")","");
			value = value.replace("\n","");
			//delete the " in string
			value = value.replace("\"","");
			out = out + "#" + name + "@" + value;
		}
		//println "out: $out"
		return out;
	}
	
	
	//make a SMT file for STP
	private String generateSMTfile(ArrayList<String> declare, ArrayList<String> negatedPC, String profileInfo) 
	{
		String filepath = "E:\\OutPut\\SMTFile\\query" + negateNumber + ".smt2";
		
		//write to file
		FileWriter fw = new FileWriter(filepath, true);
		BufferedWriter bw = new BufferedWriter(fw);
		
		ArrayList<String> output = new ArrayList<String>();
		
		for(int i =0; i< declare.size();i++) 
		{
			String temp = declare.get(i) + "\r\n"
			temp = temp.replace("!", "not");
			temp = temp.replace("Integer", "Int");
			temp = temp.replace("Double", "Real");
			temp = temp.replace("Boolean", "Bool");
			output.add(temp);
		}
//		output.add("(declare-const API_SYM_5 Int)\r\n");
//		output.add("(declare-const API_SYM_6 Int)\r\n");
//		output.add("(declare-const API_SYM_7 Int)\r\n");
//		output.add("(declare-const API_SYM_8 Int)\r\n");
//		output.add("(declare-const API_SYM_9 Int)\r\n");
//		output.add("(declare-const API_SYM_10 Int)\r\n");
		for(int i =0; i< negatedPC.size();i++)
		{
			//replace the format of Z3
			String temp = negatedPC.get(i) + "\r\n"
			temp = temp.replace("!", "not");
			temp = temp.replace("==", "=");
			temp = temp.replace("'", "\"");
			output.add(temp);
		}
		
		String tail = profileInfo + "(check-sat)\r\n(get-model)";
		output.add(tail);
		
		for(int i = 0; i< output.size();i++)
		{
			bw.write(output.get(i));
		}
		bw.close();
		fw.close();
		//println "Finished one SMT file!"
		
		
		negatedPC.clear();
		return filepath;
	}

}
