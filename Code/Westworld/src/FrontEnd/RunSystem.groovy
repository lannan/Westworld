package FrontEnd


import Interact.LogProcessing
import Interact.selenium2
import Interact.selenium2_20s
import Interact.selenium2_ten_run
import PE.*
import PEI.PathExploreImprovement
import Profile.ProcessProfilingInfo

import java.util.List



class RunSystem {
	
	//file paths for instrument
	String testAppsPath;
	
	String APIpath;
	
	String outputPathForIns;
	
	//file paths for profile	
	String outputFileForPro;
	
	//the path for mutiple solutions files
	String outputPathForMutiplePathFiles;
	
	ArrayList<HashMap<String, String>> allPathInputs = new ArrayList<ArrayList<String>>();


	
	public RunSystem(testAppsPath, APIpath, outputPathForIns, outputFileForPro, outputPathForMutiplePathFiles) 
	{
		this.testAppsPath = testAppsPath;
		this.APIpath = APIpath;
		this.outputPathForIns = outputPathForIns;
		this.outputFileForPro = outputFileForPro;
		this.outputPathForMutiplePathFiles = outputPathForMutiplePathFiles;
		
	}
	
	//0 means success, 1 means failed at online part
	public int run()
	{
		long startTime=System.currentTimeMillis();
		//*********************************************************Instrument all the files in the Dir*******************************************************************************
		//variables and their titles and subtitles
		HashMap<String, String> titleMap;
		
		ArrayList<String> bugList = new ArrayList<String>();
		
		SimpleFile sf = new SimpleFile()
		sf.processAllApps(testAppsPath, APIpath, outputPathForIns);
		titleMap = sf.getTitleMap();
		
		//Delete all the temp files
		new File(outputPathForIns).eachFile { file ->
			println "deleting ${file.getName()}"
			
			if(file.getName().contains("temp_"))
			{
				file.delete();
			}
		}
		
		
		//*********************************************************run all the files in that Dir*******************************************************************************
		new File(outputPathForIns).eachFile { file ->
			println "run ${file.getName()}"
			
			String codePath = file.getPath();
			
			ArrayList<String> preferencesInputs = new ArrayList<String>();
			ArrayList<String> simulatorsInputs = new ArrayList<String>();
			
			String log = "";
			int numberOfRun = 0;
			int numberOfRun2 = 0;
			
			//dependence from first time run
			ArrayList<String> dependence = new ArrayList<String>();
			//first time solution
			String firstTimeSolution;
			
			int flag = 1;
			
			preferencesInputs.add("PCbound:#PCBOUND@empty");
			//preferencesInputs.add("Environment variables:@empty");
			
			while(flag != 0) 
			{
				while(log == "")
				{
					if(numberOfRun == 10)
					{
						println "The log part failed 10 times and may have a problem1"
						return 1;
							
					}
					try {
						//def se = new selenium2(preferencesInputs, simulatorsInputs, codePath, "50");
						def se = new selenium2_20s(preferencesInputs, simulatorsInputs, codePath, "50");
						se.test();
						log = se.getLog();
					}
					catch(Exception e)
					{
						println "A problem happened in runtimecode"
					}
					numberOfRun++;
				}
				
				println "numberOfRun: " + numberOfRun;
				
				numberOfRun = 0;
		
				//process the log
				LogProcessing lp = new LogProcessing();
				flag = lp.processingInstrumentLog(log);
				bugList.addAll(lp.getBugList());
				
				//no path
				if(flag == 2) 
				{
					println "Finished! Only one path. No more paths to explore"
					long endTime=System.currentTimeMillis();
					println "runtime: " + (endTime-startTime) + "ms";
					System.exit(0);
				}
					
				firstTimeSolution = lp.getfirstTimeSolution();
				dependence = lp.getDependence();
					
				log = "";
				numberOfRun2++;
			}

					
			//ProfileInfo Database
			HashMap<String, ArrayList<String>> profileDatabase = new HashMap<String, ArrayList<String>>();
			
			//get declare
			PCPackage pcktemp = new PCPackage(firstTimeSolution, null, null);
			
			ArrayList<String> tempDeclareList = pcktemp.getDeclareStatements();
			
			ProfileFile pf = new ProfileFile();
			pf.processApp(file, outputFileForPro, file.getName(), dependence, profileDatabase, tempDeclareList)
			
			//*********************************************************Get Profile Info*******************************************************************************
			ArrayList<String> profileInfoList = new ArrayList<String>();
			
			profileInfoList.addAll(pf.getProfileList());
			
			new File(outputFileForPro).eachFile { file2 ->
				println "profiling  ${file2.getName()}"
				
				String[] tempPathName = file2.getName().split(".groovy")
				
				String pathName = tempPathName[0];
				
				String codePath2 = file2.getPath();
				log = "";
				numberOfRun = 0;
				numberOfRun2 = 0;
				
				flag = 1;
				//prepare input
				preferencesInputs.clear();
				preferencesInputs.add("PCbound:#PCBOUND@empty");
				
				
				while(flag != 0)
				{
					while(log == "")
					{
						if(numberOfRun == 10)
						{
							println "The log part failed 10 times and may have a problem"
							return 1;
								
						}
						try {
							def se = new selenium2(preferencesInputs, simulatorsInputs, codePath2, "50");
							se.test();
							log = se.getLog();
						}
						catch(Exception e)
						{
							println "A problem happened in runtimecode"
						}
						numberOfRun++;
					}
					
					println "numberOfRun: " + numberOfRun;
					
					numberOfRun = 0;
			
					//process the log
					LogProcessing lp2 = new LogProcessing();
					flag = lp2.processingProfileLog(log);
					bugList.addAll(lp2.getBugList());
					
					
					profileInfoList.addAll(lp2.getProfileList());
					
					profileDatabase.put(pathName, lp2.getProfileList());

					log = "";
					numberOfRun2++;
				}
				
			}			
			
			//clear all the files in the profile dir
			println "deleting profile files"
			new File(outputFileForPro).eachFile { file3 ->
					//println "deleting profile files"
					file3.delete();
			}
			
			//profileInfoList.add("1Profiling Info for API_SYM_9 and SYMhumidity1_Input: 0@0");
			//profileInfoList.add("1Profiling Info for API_SYM_9 and SYMhumidity1_Input: 0@10");
			//profileInfoList.add("1Profiling Info for API_SYM_9 and SYMhumidity1_Input: 0@20");
			//profileInfoList.add("1Profiling Info for API_SYM_9 and SYMhumidity1_Input: 0@30");
			//profileInfoList.add("1Profiling Info for API_SYM_9 and SYMhumidity1_Input: 0@40");
			//profileInfoList.add("1Profiling Info for API_SYM_9 and SYMhumidity1_Input: 0@50");
//			firstTimeSolution = "@(declare-const SYMaa_Input Double)@(declare-const SYMbb_Input Double)@(declare-const SYMcc_Input Double)@(declare-const SYMdd_Input Double)@(declare-const API_SYM_7 Integer)@(declare-const API_SYM_8 Integer)@(declare-const API_SYM_9 Integer)#@(assert (! (> API_SYM_7 55)))@(assert (! (> API_SYM_8 55)))@(assert (> API_SYM_9 55))@(assert (! (> 20 55)))@(assert (! (> 40 55)))#null";
//			
//			ArrayList<String> profileInfoList = new ArrayList<String>();
//			profileInfoList.add("0Profiling Info for API_SYM_7 and SYMaa_Input: 50@50 and SYMbb_Input: 50@20");
//			profileInfoList.add("0Profiling Info for API_SYM_7 and SYMaa_Input: 80@40 and SYMbb_Input: 80@80");
//			profileInfoList.add("0Profiling Info for API_SYM_7 and SYMaa_Input: 60@40 and SYMbb_Input: 60@60");
//			profileInfoList.add("0Profiling Info for API_SYM_7 and SYMaa_Input: 40@40 and SYMbb_Input: 40@0");
//			profileInfoList.add("0Profiling Info for API_SYM_7 and SYMaa_Input: 70@30 and SYMbb_Input: 70@70");
//			profileInfoList.add("0Profiling Info for API_SYM_7 and SYMaa_Input: 90@40 and SYMbb_Input: 90@90");
//			profileInfoList.add("1Profiling Info for API_SYM_8 and SYMaa_Input: 45@40 and SYMbb_Input: 45@10");
//			profileInfoList.add("1Profiling Info for API_SYM_8 and SYMaa_Input: 10@30 and SYMbb_Input: 10@90");
//			profileInfoList.add("1Profiling Info for API_SYM_8 and SYMaa_Input: 35@30 and SYMbb_Input: 35@30");
//			profileInfoList.add("1Profiling Info for API_SYM_8 and SYMaa_Input: 10@10 and SYMbb_Input: 10@70");
//			profileInfoList.add("2Profiling Info for API_SYM_9 and SYMaa_Input: 20@40 and SYMbb_Input: 20@90");
//			profileInfoList.add("2Profiling Info for API_SYM_9 and SYMaa_Input: 55@40 and SYMbb_Input: 55@40");
//			profileInfoList.add("2Profiling Info for API_SYM_9 and SYMaa_Input: 45@30 and SYMbb_Input: 45@10");
//			profileInfoList.add("1Profiling Info for API_SYM_8 and SYMaa_Input: 55@50 and SYMbb_Input: 55@0");
//			profileInfoList.add("1Profiling Info for API_SYM_8 and SYMaa_Input: 35@30 and SYMbb_Input: 35@30");
//			profileInfoList.add("2Profiling Info for API_SYM_9 and SYMaa_Input: 20@20 and SYMbb_Input: 20@90");
//			profileInfoList.add("2Profiling Info for API_SYM_9 and SYMaa_Input: 55@20 and SYMbb_Input: 55@40");
//			profileInfoList.add("2Profiling Info for API_SYM_9 and SYMaa_Input: 35@20 and SYMbb_Input: 35@10");
//			profileInfoList.add("2Profiling Info for API_SYM_9 and SYMaa_Input: 20@10 and SYMbb_Input: 20@70");
//			profileInfoList.add("2Profiling Info for API_SYM_9 and SYMaa_Input: 65@0 and SYMbb_Input: 65@50");
//			profileInfoList.add("2Profiling Info for API_SYM_9 and SYMaa_Input: 45@0 and SYMbb_Input: 45@30");
//			profileInfoList.add("2Profiling Info for API_SYM_9 and SYMaa_Input: 55@0 and SYMbb_Input: 55@40");
//			profileInfoList.add("2Profiling Info for API_SYM_9 and SYMaa_Input: 65@0 and SYMbb_Input: 65@50");
//			profileInfoList.add("2Profiling Info for API_SYM_9 and SYMaa_Input: 45@30 and SYMbb_Input: 45@0");
//			profileInfoList.add("1Profiling Info for API_SYM_8 and SYMaa_Input: 45@20 and SYMbb_Input: 45@40");
//			profileInfoList.add("1Profiling Info for API_SYM_8 and SYMaa_Input: 10@40 and SYMbb_Input: 10@80");
//			profileInfoList.add("1Profiling Info for API_SYM_8 and SYMaa_Input: 55@40 and SYMbb_Input: 55@50");
			
			//make the profiling info to SMT2
			ArrayList<String> processedProfileInfo = new ArrayList<String>();
			ArrayList<String> ifCountList = new ArrayList<String>();
			
			String profileInfo = ""
			int ifcount = -1;
			if(profileInfoList.size()>0) {
				ProcessProfilingInfo ppi = new ProcessProfilingInfo(profileInfoList);
				
				processedProfileInfo = ppi.getSMTProfilingInfo();
				ifCountList = ppi.getIfCount();
			}
			else 
			{
				processedProfileInfo.add(profileInfo);
				ifCountList.add(ifcount);
			}
			
			
			PCPackage pck = new PCPackage(firstTimeSolution, processedProfileInfo, ifCountList);
			ArrayList<PCPackage> workList = new  ArrayList<PCPackage>();
			workList.add(pck);
			
			int pathNumber = 0;
			
			Set<String> unSATSet = new HashSet<String>();
			
			while(workList.size()!=0)
			{
				
				PathExplore pe = new PathExplore(workList, unSATSet);
				ArrayList<SolutionPackage> solutionList = pe.explorePath();
				pathNumber = pathNumber + workList.size();
				workList.clear();
				
				
				
				//a list to store all the paths and their solutions
				ArrayList<HashMap<String, String>> pathInputs = new ArrayList<ArrayList<String>>();
				
				//store all the new logs for these solutions
				HashMap<String, String> mapForLogs = new HashMap<String, String>();
				
				int solutionNumber = solutionList.size();
				
				println solutionNumber + " solutions in a turn"
				
				//decide if there are more solutions 
				if(solutionNumber == 0) 
				{
					continue;
				}
					
				//process the solutions and put them into the list
				for(int i = 0; i< solutionNumber; i++)
				{
				
					//Parse the solutionPackage to input format
					SolutionPackage sp = solutionList.get(i);
					String str = sp.getSolution();
					String[] tempSolution = str.split("#");
					
					HashMap<String, String> pairSolution = new HashMap<String, String>();
					
					for(int j = 1; j<tempSolution.size(); j++) 
					{
						String pair = tempSolution[j];
						String[] pairtemp = pair.split("@");
						String name = pairtemp[0];
						String value = pairtemp[1];
						
						//solution < 0
						if(value.contains("("))
						{
							value = value.replace("(","");
							value = value.replace(" ","");
						}
						
						pairSolution.put(name, value);
					}
					String pathbound = String.valueOf(sp.getPathBound());
					pairSolution.put("PCBOUND", pathbound);
					
					pathInputs.add(pairSolution);
					allPathInputs.add(pairSolution);
				}
				
				//make the mutiple path solutions file
				PathExploreImprovement pei = new PathExploreImprovement(pathInputs, outputPathForMutiplePathFiles, file);
				pei.processMutiplePathApp();
				String codePathofMutiple = pei.getCodepath();
				//Two lists for inputs
				//format: title@subtitle@value
				preferencesInputs.clear();
				//format:#title@value
				simulatorsInputs.clear();
				
				//delete all the SMT file
				println "deleting SMT files"
				new File("E:\\OutPut\\SMTFile\\").eachFile { file4 ->
					file4.delete();
				}
				
				
					
				//dependence in loop
				ArrayList<String> dependenceInloop = new ArrayList<String>();
				//solution in loop
				String solutionInLoop = "";
					
				log = "";
					
				int flagInLoop = 1;
				while(flagInLoop != 0)
				{
					while(log == "")
					{
						if(numberOfRun == 10)
						{
							println "The log part failed 10 times and may have a problem3"
							return 1;
										
						}
						try {
							def se = new selenium2_ten_run(3, preferencesInputs, simulatorsInputs, codePathofMutiple, "50");
							se.test();
							log = se.getLog();
						}
						catch(Exception e)
						{
							println "A problem happened in runtimecode"
						}
						numberOfRun++;
					}
					println "numberOfRun: " + numberOfRun;

					numberOfRun = 0;
					//process the log
					LogProcessing lp = new LogProcessing();
					mapForLogs = lp.processMutiplePathLog(log);
					
					//we miss some logs
					if(mapForLogs.size() != solutionNumber) 
					{
						flagInLoop = 1;
					}
					else 
					{
						flagInLoop = 0;
					}
					log = "";
				}
				
				//delete mutiple path file
				println "deleting mutiple path files"
				new File(outputPathForMutiplePathFiles).eachFile { file5 ->
					file5.delete();
				}

				
				for(String key : mapForLogs.keySet()) 
				{
					String logOfAPath = mapForLogs.get(key);
					
					//process the log
					LogProcessing lp = new LogProcessing();
					flagInLoop = lp.processingInstrumentLog(logOfAPath);
					
					bugList.addAll(lp.getBugList());
								
					solutionInLoop = lp.getfirstTimeSolution();
					dependenceInloop = lp.getDependence();
					
					//get declare
					PCPackage pcktempInLoop = new PCPackage(solutionInLoop, null, null);
					
					ArrayList<String> tempDeclareListInLoop = pcktempInLoop.getDeclareStatements();
								
					ProfileFile pfInLoop = new ProfileFile();
					pfInLoop.processApp(file, outputFileForPro, file.getName(), dependenceInloop, profileDatabase, tempDeclareListInLoop);
					
					
					//*********************************************************Get Profile Info*******************************************************************************
					 ArrayList<String> profileInfoListInLoop = new ArrayList<String>();
					 new File(outputFileForPro).eachFile { file2 ->
						 //println "profiling  ${file2.getName()}"
						 
						 //String[] tempPathName = file2.getName().split(".")
						 String[] tempPathName = file2.getName().split(".groovy")
						 String pathName = tempPathName[0];
						 
						 
						 String codePath2 = file2.getPath();
						 log = "";
						 numberOfRun = 0;
						 numberOfRun2 = 0;
						 
						 flag = 1;
						 
						 while(flag != 0)
						 {
							 while(log == "")
							 {
								 if(numberOfRun == 10)
								 {
									 println "The log part failed 10 times and may have a problem4"
									 return 1;
										 
								 }
								 try {
									 def se = new selenium2(preferencesInputs, simulatorsInputs, codePath2, "50");
									 se.test();
									 log = se.getLog();
								 }
								 catch(Exception e)
								 {
									 println "A problem happened in runtimecode"
								 }
								 numberOfRun++;
							 }
							 
							 println "numberOfRun: " + numberOfRun;
							 
							 numberOfRun = 0;
					 
							 //process the log
							 LogProcessing lp2 = new LogProcessing();
							 flag = lp2.processingProfileLog(log);
							 profileInfoListInLoop.addAll(lp2.getProfileList());
							 
							 profileDatabase.put(pathName, lp2.getProfileList());
		 
							 log = "";
							 numberOfRun2++;
						 }
						 
					 }
					 
					 //clear all the files in the profile dir
					 println "deleting profile files"
					 new File(outputFileForPro).eachFile { file3 ->
							 file3.delete();
					 }
					 

					 //make the profiling info to SMT2
					 //String profileInfoInLoop = ""
					 //int ifcountInLoop = -1;
					 //if(profileInfoListInLoop.size()>0) {
						 //ProcessProfilingInfo ppiInLoop = new ProcessProfilingInfo(profileInfoListInLoop);
						 //profileInfoInLoop = ppiInLoop.getSMTProfilingInfo();
						 //ifcountInLoop = ppiInLoop.getIfCount();
					// }
					 
					 //make the profiling info to SMT2
					 ArrayList<String> processedProfileInfoInLoop = new ArrayList<String>();
					 ArrayList<String> ifCountListInLoop = new ArrayList<String>();
					 
					 String profileInfoInLoop = ""
					 int ifcountInLoop = -1;
					 if(profileInfoListInLoop.size()>0) {
						 ProcessProfilingInfo ppiInLoop = new ProcessProfilingInfo(profileInfoListInLoop);
						 
						 processedProfileInfoInLoop = ppiInLoop.getSMTProfilingInfo();
						 ifCountListInLoop = ppiInLoop.getIfCount();
					 }
					 else
					 {
						 processedProfileInfoInLoop.add(profileInfoInLoop);
						 ifCountListInLoop.add(ifcountInLoop);
					 }
					 
					 PCPackage pckInLoop = new PCPackage(solutionInLoop, processedProfileInfoInLoop, ifCountListInLoop);
 
					 workList.add(pckInLoop);
					 
					 println "Finish one turn"
					 
					 
				}
			}
			
			println "Finish the file  $file, explore $pathNumber paths";
			
			println "all the paths are: \n" + allPathInputs;
			
			if(unSATSet.size()>0)
			{
				String temp = "Except the init paths, " + unSATSet.size() + " paths did not reach which are:\n";
				Iterator<String> it = unSATSet.iterator();
				while (it.hasNext()) {
						String str = it.next();
						temp = temp + str + "\n";
					}
				println(temp);
			}
			
			long endTime=System.currentTimeMillis();
			println "runtime: " + (endTime-startTime) + "ms";
			
			
						
		}
		
		new File("E:\\OutPut\\SMTFile\\").eachFile { file7 ->
			file7.delete();
		}
				
		return 0;
	}
	
	
}



