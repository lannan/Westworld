package FrontEnd


import Interact.LogProcessing
import Interact.selenium2
import Interact.selenium2_ten_run

import java.util.List



class RunSystem {
	
	//file paths for instrument
	String testAppsPath;
	
	
	String outputPathForIns;


	
	public RunSystem(testAppsPath, outputPathForIns) 
	{
		this.testAppsPath = testAppsPath;
		this.outputPathForIns = outputPathForIns;	
	}
	
	//0 means success, 1 means failed at online part
	public int run()
	{
		long startTime=System.currentTimeMillis();
		
		//*********************************************************Instrument all the files in the Dir*******************************************************************************
		SimpleFile sf = new SimpleFile()
		sf.processAllApps(testAppsPath, outputPathForIns);
		
		
		//*********************************************************run all the files in that Dir*******************************************************************************
		new File(outputPathForIns).eachFile { file ->
			println "run ${file.getName()}"
			
			String codePath = file.getPath();
			
			ArrayList<String> preferencesInputs = new ArrayList<String>();
			ArrayList<String> simulatorsInputs = new ArrayList<String>();
			
			HashSet<String> logSet = new HashSet<String>();
			
			String log = "";

			
			preferencesInputs.add("PCbound:#PCBOUND@empty");
			
			
			one(preferencesInputs, simulatorsInputs, logSet, log, codePath);
			
			//two(preferencesInputs, simulatorsInputs, logSet, log, codePath);
			//two(preferencesInputs, simulatorsInputs, logSet, log, codePath);
			
			//four(preferencesInputs, simulatorsInputs, logSet, log, codePath);

			println "Fuzzing " + logSet.size() + " paths"
			long endTime=System.currentTimeMillis();
			println "runtime: " + (endTime-startTime) + "ms";
		}
	}
	
	public def four(ArrayList<String> preferencesInputs, ArrayList<String> simulatorsInputs, HashSet<String> logSet, String log, String codePath) 
	{
		
		for(int i = 0; i < 100; i = i + 30)
			{
				ArrayList<String> preferencesInputs1 = new ArrayList<String>();
				String str1 = "aa:#integer ?@" + i
				preferencesInputs1.add(str1);
				for(int j = 0; j < 100; j = j + 30)
				{
					ArrayList<String> preferencesInputs2 = new ArrayList<String>();
					preferencesInputs2.addAll(preferencesInputs1)
					String str2 = "bb:#integer ?@" + j
					preferencesInputs2.add(str2);
					for(int k = 0; k < 100; k = k + 30)
					{
						ArrayList<String> preferencesInputs3 = new ArrayList<String>();
						preferencesInputs3.addAll(preferencesInputs2)
						String str3 = "cc:#integer ?@" + k
						preferencesInputs3.add(str3);
						for(int l = 0; l < 100; l = l + 30)
						{
							ArrayList<String> preferencesInputs4 = new ArrayList<String>();
							preferencesInputs4.addAll(preferencesInputs3)
							String str4 = "dd:#integer ?@" + l
							preferencesInputs4.add(str4);
							preferencesInputs.addAll(preferencesInputs4);
							def se = new selenium2(preferencesInputs, simulatorsInputs, codePath, "50");
							se.test();
							log = se.getLog();
							String[] temp = log.split("debug PC: ");
							if(temp.size()>1)
							{
								logSet.add(temp[1]);
							}
							
							preferencesInputs4.clear();
							preferencesInputs.clear();
							
//							if(logSet.size() == 8)
//							{
//								println "Fuzzing " + logSet.size() + " paths"
//								long endTime=System.currentTimeMillis();
//								println "runtime: " + (endTime-startTime) + "ms";
//								System.exit(0);
//							}
							//long currentTime=System.currentTimeMillis();
							//long minus = currentTime-startTime;
//							if(minus>54000000)
//							{
//								println "reaching 30 mins"
//								println "Fuzzing " + logSet.size() + " paths"
//								long endTime=System.currentTimeMillis();
//								println "runtime: " + (endTime-startTime) + "ms";
//								System.exit(0);
//							}
								
						}
						preferencesInputs3.clear();
					}
					preferencesInputs2.clear();
				}
				
				preferencesInputs1.clear();
			}
	}
	
	
	
	public def two(ArrayList<String> preferencesInputs, ArrayList<String> simulatorsInputs, HashSet<String> logSet, String log, String codePath)
	{
		
		for(int i = 0; i < 100; i = i + 30)
			{
				ArrayList<String> preferencesInputs1 = new ArrayList<String>();
				String str1 = "number0#input a number@" + i
				preferencesInputs1.add(str1);
				for(int j = 0; j < 100; j = j + 30)
				{
					ArrayList<String> preferencesInputs2 = new ArrayList<String>();
					preferencesInputs2.addAll(preferencesInputs1)
					String str2 = "number1#input a number@" + j
					preferencesInputs2.add(str2);
					
					preferencesInputs.addAll(preferencesInputs2);
					def se = new selenium2(preferencesInputs, simulatorsInputs, codePath, "50");
					se.test();
					log = se.getLog();
					String[] temp = log.split("debug PC: ");
					if(temp.size()>1)
					{
						logSet.add(temp[1]);
					}
					
					preferencesInputs2.clear();
					preferencesInputs.clear();
				}
				
				preferencesInputs1.clear();
			}
	}
	
	public def one(ArrayList<String> preferencesInputs, ArrayList<String> simulatorsInputs, HashSet<String> logSet, String log, String codePath)
	{
		
		for(int i = 0; i < 100; i = i + 30)
			{
				ArrayList<String> preferencesInputs1 = new ArrayList<String>();
				String str1 = "input1:#integer ?@" + i
				preferencesInputs1.add(str1);
				
				preferencesInputs.addAll(preferencesInputs1);
				def se = new selenium2(preferencesInputs, simulatorsInputs, codePath, "50");
				se.test();
				log = se.getLog();
				String[] temp = log.split("debug PC: ");
				if(temp.size()>1)
				{
					logSet.add(temp[1]);
				}
				preferencesInputs.clear();
				
				preferencesInputs1.clear();
			}
	}
	
	
	
	
}



