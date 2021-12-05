package Interact

import java.util.List

class LogProcessing {
	
	String declare = null;
	
	String SMT = null;
	
	ArrayList<String> dependence = new ArrayList<String>();
	
	ArrayList<String> profileInfoList = new ArrayList<String>();
	
	ArrayList<String> bugList = new ArrayList<String>();
	
	public int processingInstrumentLog(String log)
	{
		//three tree set to store the line number
		TreeSet VarInPcTree = new TreeSet();
		TreeSet ConcreteVariablesTree = new TreeSet();
		TreeSet RelyInfTree = new TreeSet();
		
		//three hashmap to store the line number and string
		HashMap<Double, String> VarInPcHash = new HashMap<Double, String>();
		HashMap<Double, String> ConcreteVariablesHash = new HashMap<Double, String>();
		HashMap<Double, String> RelyInfoHash = new HashMap<Double, String>();

		if(log.contains("TRUNCATED")) 
		{
			println "log too long sumsung cannot give it"
			System.exit(0);
		}
		
		if(log.contains("NOLOG"))
		{
			println "Finished! Only one path"
			return 2;
		}
		
		if(log.contains("ReportBUG"))
		{
			String[] tempbug = log.split(": ");
			String tempstr = tempbug[1];
			bugList.add(tempstr);
			println tempstr;
			System.exit(0);
		
		}
		
		//split each line
		//String[] everyLogDebug = log.split("DEBUGEND")
		
		//for(int y = 0; y < everyLogDebug.size(); y++) 
		//{
			String[] tempall = log.split("debug");
			
			String allLog = tempall[1];
			String[] temp = allLog.split("\n");
			
			//get each line info and store them
			for(int i = 0; i< temp.size(); i++)
			{
				String str = temp[i];
	//			String[] orignal = str.split("debug ");
	//			if(orignal.size()<2)
	//			{
	//				println "unknow bug error"
	//				return 1;
	//			}
	//			str = orignal[1];
				if(str.contains("PC in SMTLIB version"))
				{
					String[] tempSMT = str.split("PC in SMTLIB version: ");
					if(tempSMT.size()>1)
					{
						SMT = tempSMT[1];
					}
					else
					{
						println "No SMT"
						return 2;
					}
				}
				else if(str.contains("Declare:"))
				{
					String[] tempDeclare = str.split("Declare: ");
					if(tempDeclare.size()>1)
					{
						declare = tempDeclare[1];
					}
					else
					{
						println "No delcare"
						return 2;
					}
				}
				else if(str.contains("RelyInfo#"))
				{
					String[] tempRelyInfo = str.split(": ");
					String tempstr = tempRelyInfo[0];
					String[] temp2 = tempstr.split("#");
					int cp = temp2.size();
					if(!(cp>1))
					{
						println "No LineNumberInfo"
						return 2;
					}
					String lineAndI = temp2[1];
					
					lineAndI = lineAndI.replace("-",".");
					lineAndI = lineAndI.replace(":","");
					double linenumber = Double.parseDouble(lineAndI);
					
					str = str.replace(" ","");
					
					RelyInfTree.add(linenumber);
					RelyInfoHash.put(linenumber, str);
				}
				else if(str.contains("VarInPC"))
				{
					String[] tempVarInPC = str.split(": ");
					String tempstr = tempVarInPC[0];
					String[] temp2 = tempstr.split("#");
					int cp = temp2.size();
					if(!(cp>1))
					{
						println "No LineNumberInfo"
						return 2;
					}
					String lineAndI = temp2[1];
					
					lineAndI = lineAndI.replace("-",".");
					lineAndI = lineAndI.replace(":","");
					double linenumber = Double.parseDouble(lineAndI);
					
					str = str.replace(" ","");
						
					VarInPcTree.add(linenumber);
					VarInPcHash.put(linenumber, str);
				}
				else if(str.contains("ConcreteVariables"))
				{
					String[] tempConcreteVariables = str.split(": ");
					String tempstr = tempConcreteVariables[0];
					String[] temp2 = tempstr.split("#");
					int cp = temp2.size();
					if(!(cp>1))
					{
						println "No LineNumberInfo"
						return 2;
					}
					String lineAndI = temp2[1];
					
					lineAndI = lineAndI.replace("-",".");
					lineAndI = lineAndI.replace(":","");
					double linenumber = Double.parseDouble(lineAndI);
					
					str = str.replace(" ","");
							
					ConcreteVariablesTree.add(linenumber);
					ConcreteVariablesHash.put(linenumber, str);
				}
				
				else
				{
					println "unknown log"
				}
				
			}
		//}
		
		
		

		
		//check the dependence information
		int numberOfRely = RelyInfTree.size();
		int numberOfVar = VarInPcTree.size();
		int numberOfCon = ConcreteVariablesTree.size();
		
		//error the three trees are not equal
		if(numberOfRely != numberOfVar || numberOfRely != numberOfCon) 
		{
			println "IDE missing log1"
			return 1;
		}
		
		if(declare == null || SMT == null) 
		{
			println "IDE missing declare log or SMT log"
			return 1;
		}
		
		for(Iterator iter = RelyInfTree.descendingIterator(); iter.hasNext(); ) { 
			double linenumber = iter.next();
			//hashtable does not have that line number which means it is missed
			if(!RelyInfoHash.containsKey(linenumber)||!VarInPcHash.containsKey(linenumber)||!ConcreteVariablesHash.containsKey(linenumber)) 
			{
				println "IDE missing log2"
				return 1;
			}
			String str1 = RelyInfoHash.get(linenumber);
			String str2 = VarInPcHash.get(linenumber);
			String str3 = ConcreteVariablesHash.get(linenumber);
			
			dependence.add(str1);
			dependence.add(str2);
			dependence.add(str3);
		}
		//success
		return 0;
	}

	public ArrayList<String> getDependence()
	{
		return dependence;
	}
	
	public String getfirstTimeSolution()
	{
		String firstTimeSolution = declare + "#" + SMT;
		return firstTimeSolution;
	}
	
	
	public int processingProfileLog(String log)
	{		
		//split each line
		//String[] tempall = log.split("debug ");
		//String allLog = tempall[1]
		String[] temp = log.split("\n");
		
		//flag to know if we get any
		int count = 0;
		
		//get each line info and store the profile info
		for(int i = 0; i< temp.size(); i++)
		{
			String str = temp[i];
			if(str.contains("debug")) 
			{
				String[] orignal = str.split("debug ");
				if(orignal.size()<2) 
				{
					println "debug error!!!!!!!!dont know why the orignal string is " + orignal
					continue;
				}
				str = orignal[1];
				if(str.contains("Profiling Info for"))
				{
					profileInfoList.add(str);
					count++;
				}
				else
				{
					println "other logs"
				}
			}
			else 
			{
				println "other logs such as error"
			}
			
			
		}
		
		//error
		if(count == 0)
		{
			println "No profile info"
			profileInfoList.clear();
			return 1;
		}
		else if(count < 2) 
		{
			println "No profile info less than 2"
			profileInfoList.clear();
			return 1;
		}
		//success
		return 0;
	}
	
	public ArrayList<String> getProfileList()
	{
		return profileInfoList;
	}
	
	
	
	public HashMap<String, String> processMutiplePathLog(String log) 
	{
		HashMap<String, String> mapForLogs = new HashMap<String, String>();
		if(log.contains("TRUNCATED"))
		{
			println "log too long sumsung cannot give it"
			System.exit(0);
		}
			
		
			
		if(log.contains("ReportBUG"))
		{
			String[] everyline = log.split("\n");
			String[] tempbug = everyline[0].split(": ");
			String tempstr = tempbug[1];
			bugList.add(tempstr);
			println tempstr;
			System.exit(0);
		}
		
		String[] newResult = log.split("ENDOFTHISPATH");
		
		
		for(int i = 0; i< newResult.size(); i++) 
		{
			String wholeLogForPath = newResult[i];
			
			//String[] temp = wholeLogForPath.split("debug");
			
			//String LogForPath = temp[1];
			
			String[] temp2 = wholeLogForPath.split("PATHNUMBER");
			
			String realLog = temp2[0];
			
			String number = temp2[1];
			
			mapForLogs.put(number, realLog);
		}
		
		return mapForLogs;
	
	}
	
	public ArrayList<String> getBugList() 
	{
		return bugList;
	}
	
	
	}
