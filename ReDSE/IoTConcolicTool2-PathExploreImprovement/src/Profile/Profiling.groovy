package Profile

class Profiling {
	
	public String lineNumber = 0;
	
	//String for relyInfo
	private String relyInfo = ""

	//String for the variables and their SYM value appears in PC
	private String 	varInPC = ""
	
	private SYMName = "";
	
	// two maps to store information
	private HashMap<String, ArrayList<String>> dependencyMap = new HashMap<String, ArrayList<String>>();
	private HashMap<String, String> variableMap = new HashMap<String, String>();
	
	// array to store concrete variables at this if
	ArrayList<String> concreteState = new  ArrayList<String>();
	
	
	//String for output
	public String output = ""
	
	
	public Profiling(String relyInfo, String varInPC, String lineNumber, String concreteVariable) 
	{
		this.relyInfo = relyInfo;
		this.varInPC = varInPC;
		this.lineNumber = lineNumber;
		
		
		//get concrete variable state
		if(concreteVariable != "")
		{
			String[] concrete =  concreteVariable.split("#");
			for(int i = 1; i < concrete.size(); i++)
			{
				String temp = concrete[i];
				concreteState.add(temp);
			}
		}
		
		//get each dependency of the variables
		String[] dependency = relyInfo.split("#");
		
		for(int i = 1; i < dependency.size(); i++) 
		{
			ArrayList<String> dependsOn = new ArrayList<String>();
			
			String[] temp = dependency[i].split("@");
			
			for(int j = 1; j < temp.size(); j++) 
			{
				dependsOn.add(temp[j]);
			}
			if(dependencyMap.containsKey(temp[0])) 
			{
				ArrayList<String> tempList = new ArrayList<String>();
				tempList = dependencyMap.get(temp[0]);
				
				//the state of this variable change from sym to concrete
				int numberOfConcrete = 0;
				//the variable depends on itself and other variables
				int self = 0;
				
				for(int k = 0 ; k < dependsOn.size(); k++) 
				{
					if(concreteState.contains(dependsOn.get(k)))
					{
						numberOfConcrete = numberOfConcrete + 1;
					}
					
					if(dependsOn.get(k) == temp[0]) 
					{
						self = 1;
					}
				}
				
				//the variable is update with ifself and other symbolic variables
				if(self == 1) 
				{
					//the variable only depends on concrete variable at this definenation
					if(numberOfConcrete == dependsOn.size())
					{
						dependencyMap.put(temp[0], dependsOn);
					}
					else 
					{
						tempList.addAll(dependsOn);
						dependencyMap.put(temp[0], tempList);
					}
				}
				else 
				{
					dependencyMap.put(temp[0], dependsOn);
				}
				
			}
			else 
			{
				dependencyMap.put(temp[0], dependsOn);
			}
		}
		
	}
	
	
	public def FindRely() 
	{
		//get the variables in PC
		String[] variables = varInPC.split("#");
		
		//the returnList contains outputlist that contain 1. linenumber 2.target symvalue name 3.target name 3. variables in for
		ArrayList<ArrayList<String>> returnList = new ArrayList<ArrayList<String>>();
		
		
		//i starts from 1 because the first element  before # is "" ( null )
		for(int i = 1; i < variables.size(); i++)
		{
			ArrayList<String> outputList = new ArrayList<String>();
			//add linenumber
			outputList.add(lineNumber);
			
			String[] temp = variables[i].split("@");
			
			outputList.add(temp[0]);
			outputList.add(temp[1]);
			SYMName = temp[0];
			
			String[] falg = SYMName.split("_")
			if(falg.size()>1 && (falg[falg.size()-1].contains("Input"))) 
			{
				break;
			}
			
			//there are no relations for this variable
			if(!dependencyMap.containsKey(temp[1])) 
			{
				output = output + "||" + temp[1] + " depends on Nothing"
				
			}
			else 
			{
				ArrayList<String> dependsOn = new ArrayList<String>();
				
				dependsOn = dependencyMap.get(temp[1]);
				
				//there are no dependence
				if(dependsOn.size() == 0) 
				{
					output = output + "||" + temp[1] + " depends on Nothing"
				}
				
				for(int j = 0; j < dependsOn.size(); j++) 
				{
					output = output + "||" + temp[1] + " depends on "
					ArrayList<String> tempList = new ArrayList<String>();
					tempList = find(dependsOn.get(j));
					for(int k = 0; k< tempList.size(); k++)
					{
						//if the variable is not input then we should put concrete here
						if(!concreteState.contains(tempList.get(k))) 
						{
							output = output + tempList.get(k) + ","
							outputList.add(tempList.get(k));
						}
					}
				}
			}
			Set<String> variablesInFor = new HashSet<String>();
			
			for(int k = 3; k < outputList.size(); k++)
			{
				String tempStr = outputList.get(k);
				variablesInFor.add(tempStr);
			}
			
			outputList.clear();
			outputList.add(lineNumber);
			outputList.add(temp[0]);
			outputList.add(temp[1]);
			
			for(int kk = 0; kk < variablesInFor.size(); kk++) 
			{
				outputList.add(variablesInFor.getAt(kk));
			}
			
			returnList.add(outputList);
		}
		//println output;
		return returnList;
	}
	
	private ArrayList<String> find(String variable)
	{
		
		ArrayList<String> dependsOn = new ArrayList<String>();
		
		//a string for return
		ArrayList<String> returnList = new ArrayList<String>();
		
		// this variable depends on nothing, so it is a root return it( usually this situation happens when SYM INPUT)
		if(!dependencyMap.containsKey(variable))
		{
			returnList.add(variable)
		}
		else 
		{
			dependsOn = dependencyMap.get(variable);
		
			//there are no more dependence for this variable (usually this happens when it is concrete)
			if(dependsOn.size() == 0)
			{
				returnList.add(variable);
			}
			else 
			{
				//it depends on itself
				if(dependsOn.size() == 1 && dependsOn.get(0)==variable)
				{
					returnList.add(variable);
				}
				else 
				{
					for(int j = 0; j < dependsOn.size(); j++)
					{
						ArrayList<String> temp = new ArrayList<String>();
						if(dependsOn.get(j)==variable) 
						{
							returnList.add(variable);
						}
						else 
						{
							temp = find(dependsOn.get(j));
							for(int i = 0; i< temp.size(); i++)
							{
								returnList.add(temp.get(i))
							}
						}
							
					}	
				}
				
			}
		}
		
		return returnList;		
		
	}
	
	
}
