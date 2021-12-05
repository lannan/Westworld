package Profile







class ProfileExecutor {

	
	//Map for the whole program    I put it here because it will be used in different objects of ProcessStatement (if,else,for also have blocks of statements)
	HashMap<Integer, String> programHash = new HashMap<Integer, String>();
	HashMap<String, ArrayList<String>> finalOutput = new HashMap<String, ArrayList<String>>();
	
	ArrayList<Integer> forStart = new ArrayList<Integer>();
	ArrayList<Integer> forAPI = new ArrayList<Integer>();
	ArrayList<Integer> forAPIEnd = new ArrayList<Integer>();
	ArrayList<Integer> forloopStart = new ArrayList<Integer>();
	ArrayList<Integer> forloopEnd = new ArrayList<Integer>();
	TreeSet<Integer> ts = new TreeSet<Integer>();
	

	
	public ProfileExecutor(def file)
	{

		FileReader fileReader = new FileReader(file);
		LineNumberReader reader = new LineNumberReader(fileReader);
		int line = 1;
		String text = "";
		String program = "";
		
		ReadFile.StoreWholeFileInHash(file,programHash);
		
		
		//Find where is for start and if there are any apis
		for(int i = 0; i < programHash.size(); i++)
		{
			if(programHash.get(i+1) == "//INSERT_FOR_START") 
			{
				forStart.add(i+1)
				ts.add(i+1);
			}
			else if((programHash.get(i+1) == "//INSERT_FOR_API") ) 
			{
				forAPI.add(i+1)
				ts.add(i+1);
			}
			else if((programHash.get(i+1) == "//INSERT_FOR_API_END") )
			{
				forAPIEnd.add(i+1)
			}
			else if(programHash.get(i+1) == "//INSERT_FOR_LOOP_END")
			{
				forloopEnd.add(i+1);
			}
			else if(programHash.get(i+1) == "//FOR_LOOP_START") 
			{
				forloopStart.add(i+1);
			}			
				
		}
	
	}
	
	def addProfileCode(ArrayList<String> dependence, HashMap<String, ArrayList<String>> profileDatabase, TreeSet<Double> linenumberInLine, ArrayList<String> profiledInfo, HashMap<String, String> typeMap) 
	{
		
		//A flag to decide if the if is in the entry
		String flag = "ENTRY";
		int entryLine = 0
		
		for(int i = 0; i<dependence.size(); i++ ) 
		{
			//String for for, for's end and log
			String forCode = "";
			String tail = ""
			String log = ""
			
			ArrayList<String> output = new ArrayList<String>();
			
			
			String[] temp1 = dependence.get(i).split(":");
			String[] temp2 = dependence.get(i+1).split(":");
			String[] temp4 = dependence.get(i+2).split(":");
			i = i+2;

			String[] temp3 = temp1[0].split("#");
			
			String[] linenumberAndI = temp3[1].split("-");
			
			
			String lineNumberAndIDouble = temp3[1].replace("-",".");
			//get line number
			double linenumberAndI2 = Double.parseDouble(lineNumberAndIDouble);
			
			//get for iter number
			int forIteration = Integer.parseInt(linenumberAndI[1]);
			
			String pathName = "";
			//get the path
			for(Iterator iter = linenumberInLine.iterator(); iter.hasNext(); ) { 
				double intNow = iter.next();
				if(intNow != linenumberAndI2) 
				{
					pathName = pathName +"@"+ intNow;
				}
				else 
				{
					pathName = pathName +"@"+ intNow;
					break;
				}			
			}
			
			//see if the path has been profiled
			if(profileDatabase.containsKey(pathName)) 
			{
				//the profile database has the profile infomation
				profiledInfo.addAll(profileDatabase.get(pathName));
				continue;
			}
			else 
			{
				//sometimes there are no concrete variables
				String concreteVars = "";
				if(temp4.size()==2) 
				{
					concreteVars = temp4[1];
				}
				
				Profiling pro = new Profiling(temp1[1], temp2[1], linenumberAndI[0], concreteVars);
				
				ArrayList<ArrayList<String>> allVariablesForInfo = pro.FindRely();
				
				
				for(int m = 0; m < allVariablesForInfo.size(); m++)
				{
					ArrayList<String> forInfo = allVariablesForInfo.get(m);
					
					String lineAndI = temp3[1];
					
					String[] temp5 = lineAndI.split("-");
					
					String line = temp5[0];
					
					int linenumber = Integer.parseInt(line);
					
					//find this if is contained in entry method or api
					for(int j = ts.size()-1; j >= 0; j--)
					{
						if (linenumber > ts.getAt(j))
						{
							if(forStart.contains(ts.getAt(j)))
							{
								flag = "ENTRY"
								entryLine = ts.getAt(j);
							}
							else
							{
								flag = "API"
							}
							break;
						}
					}
					String target = forInfo.get(1);
					String targetName = forInfo.get(2);
					
					if(forInfo.size() == 3)
					{
						//doing nothing,just keep finish the file
					}
					//need profiling
					else
					{
						//record this path which is also the file name we already had it 
						pathName = pathName;
						
						
						log = "log.debug \"\${state.ifCount}Profiling Info for " + target
						for(int k = 3; k < forInfo.size(); k++)
						{
							String var = forInfo.get(k);
							if(targetName!=var) 
							{
								String var1 = "SYM" + var +"_Input"
								String var2 = "SYM" + var +"pair_Input"
								if(typeMap.containsKey(var1)||typeMap.containsKey(var2)) 
								{
									if(typeMap.get(var1) == "Integer"||typeMap.get(var2) == "Integer") 
									{
										forCode = forCode + "for(int  " + forInfo.get(k) + "= 0; " + forInfo.get(k) + "< 100; " + forInfo.get(k) + " = " + forInfo.get(k) + " + 10){\n"
									}
									else if(typeMap.get(var1) == "Boolean"||typeMap.get(var2) == "Boolean") 
									{
										String arrayListStr = "ArrayList<Boolean> forBooleanList = new ArrayList<Boolean>();\nforBooleanList.add(true);\nforBooleanList.add(false);\n"
										forCode = forCode + arrayListStr + "for(int iforBL = 0; iforBL<forBooleanList.size(); iforBL++){\ndef " + forInfo.get(k) + " = forBooleanList.get(iforBL);\n\n"
									}
									else if(typeMap.get(var1) == "String"||typeMap.get(var2) == "String")
									{
										println "!!!!!!!!!!!INPUT IS STRING CANNOT WORK IN THIS WAY NEED A RANGE!!!!!!!!!!!!!!"
										System.exit(0);
									}
								}
								else 
								{
									println "!!!!!!!!!!!Some strange error happened!!!!!!!!!!!!!!"
								}
								
								
								
								tail = tail + "}\n"
								log = log + " and SYM" + forInfo.get(k) + "_Input:" + " \${" + targetName + "}" + "@\${" + forInfo.get(k) + "}"
							}	
						}
						
						forCode = forCode + "state.ifCount = 0 \n"
						
						log = log + "\" " + "\n"
						
						if(forIteration!= 0) 
						{
							String logSurrondIf = "\nif(forCountI == " + forIteration + "){\n"
							log = logSurrondIf + log + "}\n"
						}
							
						if(flag == "API")
							{
								for(int l = 1; l < programHash.size()+1; l++)
								{
									if(forStart.contains(l))
									{
										output.add(forCode)
										String str = programHash.get(l);
										if(!str.contains("log.debug"))
										{
											output.add(str + "\n");
										}
											
									}
									else if(forAPIEnd.contains(l))
									{
										output.add(tail)
										String str = programHash.get(l);
										if(!str.contains("log.debug"))
										{
											output.add(str + "\n");
										}
									}
									else if(linenumber == l)
									{
										output.add(log)
										String str = programHash.get(l);
										if(!str.contains("log.debug"))
										{
											output.add(str + "\n");
										}
									}
									else
									{
										String str = programHash.get(l);
										if(!str.contains("log.debug"))
										{
											output.add(str + "\n");
										}
									}
										
								}
								
							}
							else
							{
								int flagForaddTail = 0;
								for(int l = 1; l < programHash.size()+1; l++)
								{
									if(entryLine == l)
									{
										output.add(forCode)
										String str = programHash.get(l);
										if(!str.contains("log.debug"))
										{
											output.add(str + "\n");
										}
									}
									else if(linenumber == l)
									{
										output.add(log)
										if(forloopEnd.size() == 0) 
										{
											output.add(tail)
										}
										else 
										{
											for(int ii = 0; ii< forloopEnd.size(); ii++) 
											{	
												int start = forloopStart.get(ii);
												int end = forloopEnd.get(ii);
												if(l>start && l<end) 
												{
													flagForaddTail = 1;
												}
											}
											if(flagForaddTail == 0) 
											{
												output.add(tail)
											}
										}
										String str = programHash.get(l);
										if(!str.contains("log.debug"))
										{
											output.add(str + "\n");
										}
										//output.add("}\n");
										//break;
									}
									else if(forloopEnd.contains(l) && flagForaddTail == 1)
									{
										output.add(tail)
										String str = programHash.get(l);
										if(!str.contains("log.debug"))
										{
											output.add(str + "\n");
										}
										flagForaddTail = 0;
									}
									else
									{
										String str = programHash.get(l);
										if(!str.contains("log.debug"))
										{
											output.add(str + "\n");
										}
									}
									}
							}
							
							finalOutput.put(pathName,output);
						}
					
				}
			}
			
			
		}
		
		
	}
	
	TreeSet<Double> inline(ArrayList<String> dependence) 
	{
		TreeSet<Double> set = new TreeSet<Double>();
		for(int i = 0; i<dependence.size(); i++ )
		{
			String[] temp1 = dependence.get(i).split(":");
			String[] temp3 = temp1[0].split("#");
			
			
			String lineAndI = temp3[1];
			
			lineAndI = lineAndI.replace("-",".");
			lineAndI = lineAndI.replace(":","");
				
			double linenumber = Double.parseDouble(lineAndI);
			
			set.add(linenumber);
		}
		
		return set;
	}
	
	def getOutput()
	{

		return finalOutput;
	}
	

}
