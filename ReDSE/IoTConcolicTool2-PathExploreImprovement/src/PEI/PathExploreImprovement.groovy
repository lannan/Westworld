package PEI

import Profile.*

class PathExploreImprovement {
	
	//path input format key@value
	ArrayList<HashMap<String, String>> pathInputs = new ArrayList<ArrayList<String>>();
	//each line is a path solution
	ArrayList<String> inputList = new ArrayList<String>();
	
	//a set to store the variables
	HashSet<String> variableSet = new HashSet<String>();
	
	//the whole program
	HashMap<Integer, String> programHash = new HashMap<Integer, String>();
	
	//the flags to put in the head    Format should be //PEI_HEAD   Also, they should be founded in entry methods
	ArrayList<Integer> heads = new ArrayList<Integer>();
	
	//the flags to put in the tails   Format should be //PEI_TAIL   Also, they should be founded in entry methods
	ArrayList<Integer> tails = new ArrayList<Integer>();
	
	//output filepath
	String outputFilePath = "";
	
	String fileName;
	

	//The strings for the head and the tail
	String theHead = "";
	
	String theTail = "";
	
	String APIBlock = "";
	
	public PathExploreImprovement(ArrayList<HashMap<String, String>> pathInputs, String outputFilePath, def file) 
	{
		this.pathInputs = pathInputs;
		
		this.outputFilePath = outputFilePath;
		
		fileName = file.getName();
		
		ReadFile.StoreWholeFileInHash(file, programHash);
		
		//find the flags
		for(int i = 0; i < programHash.size(); i++) 
		{
			if(programHash.get(i) == "//PEI_HEAD") 
			{
				heads.add(i);
			}
			else if((programHash.get(i) == "//PEI_TAIL") ) 
			{
				tails.add(i);
			}
		}
	}
	
	public def processMutiplePathApp() 
	{
		this.processInputList();
		this.processHeadAndTail();
		this.processAPIBlock();
		this.processFile();
	}
	
	
	//process the strings that contain the solutions and will be put into the arraylist in the app
	private def processInputList() 
	{
		
		for(int i = 0; i < pathInputs.size(); i++)
		{
			HashMap<String, String> pairSolution = new HashMap<String, String>();
			
			pairSolution = pathInputs.get(i);
			
			//one path solution format #var1@value1#var2@value2#var3@value3
			String solutionOfOnePath = "";
			
			
			for(String key:pairSolution.keySet()) 
			{
				String valueStr = pairSolution.get(key);
				
				if(key.contains("_Input"))
				{
					//delete SYM AND _INPUT
					key = key.replace("SYM", "");
					key = key.replace("_Input", "");
				}
				
				variableSet.add(key);
				
				//one pair of path input format key@value
				String onePair = key + "@" + valueStr;
				
				solutionOfOnePath = solutionOfOnePath + "#" + onePair;
			}
			
			inputList.add(solutionOfOnePath);
		}
		
	}
	
	//make the tail part and the head part in entry methods of the new file
	private def processHeadAndTail()
	{
		//Head
		theHead = theHead + "ArrayList<String> solutionList = new ArrayList<String>(); \n"
		theHead = theHead + "fillInSolutionList(solutionList); \n";
		theHead = theHead + "for(int solutionI = 0; solutionI < solutionList.size(); solutionI++) \n";
		theHead = theHead + "{\n";
		theHead = theHead + "	String pathSolution = solutionList.get(solutionI);\n\n";
		theHead = theHead + "	String[] temp = pathSolution.split(\"#\");\n";
		theHead = theHead + "	String[] pair;\n";
		theHead = theHead + "	Map<String, String> pairMap = new HashMap<String, String>();\n"
		theHead = theHead + "	for(int pairNum = 1; pairNum<temp.size(); pairNum++)\n";
		theHead = theHead + "	{\n";
		theHead = theHead + "		pair = temp[pairNum].split(\"@\");\n";
		theHead = theHead + "		pairMap.put(pair[0], pair[1]);\n"
		theHead = theHead + "	}\n\n";
		
		for(String varName : variableSet) 
		{
			String tempVariable = varName + "Temp";
			theHead = theHead + "	def " + tempVariable + " = " + varName + ";\n";
			theHead = theHead + "	def " + varName + " = " + "getTypeAndReturnValue(" + tempVariable + ", pairMap, \"" + varName + "\");\n\n"	
		}
		
		
		theTail = theTail + "state.logString = state.logString + \"PATHNUMBER\" + solutionI + \"ENDOFTHISPATH\";\n\n"
		
		
		theTail = theTail + "log.debug \"\${state.logString}\"\n\n"
		
		theTail = theTail + "}\n\n"
		
	}
	
	private def processAPIBlock()
	{
		APIBlock = APIBlock + "\ndef fillInSolutionList(ArrayList<String> solutionList)\n"
		APIBlock = APIBlock + "{\n";
		
		for(int i = 0; i<inputList.size(); i++) 
		{
			APIBlock = APIBlock + "solutionList.add(\"" + inputList.get(i) + "\");\n"
		}
		APIBlock = APIBlock + "}\n\n";
	}
	
	//find the flag in file(entry methods) and put the code in
	private def processFile() 
	{
		//output file
		ArrayList<String> output = new ArrayList<String>();
		
		for(int i = 1; i < programHash.size()+1; i++)
		{
			if(heads.contains(i))
			{
				String str = programHash.get(i);
				output.add(str + "\n");
				output.add(theHead);		
			}
			else if(tails.contains(i))
			{
				String str = programHash.get(i);
				output.add(str + "\n");
				output.add(theTail);	
			}
			else 
			{
				String str = programHash.get(i);
				if(!str.contains("log.debug")) 
				{
					output.add(str + "\n");
				}	
			}
		}
		output.add("\n\n");
		output.add(APIBlock);
		
		this.makeTheFile(output);
		
	}
	
	private def makeTheFile(ArrayList<String> output) 
	{
		
		String outputFile = outputFilePath + fileName;
		
		//write to file
		FileWriter fw = new FileWriter(outputFile, true);
		BufferedWriter bw = new BufferedWriter(fw);
		
		for(int j = 0; j< output.size();j++)
		{
			bw.write(output.get(j));
		}
		bw.close();
		fw.close();
	}
	
	public getCodepath() 
	{
		String outputFile = outputFilePath + fileName;;
		return outputFile;
	}
	
	
	
	
}
