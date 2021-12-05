package FrontEnd

import Interact.Selenium
import Interact.selenium2
import PE.*
import Profile.ProcessProfilingInfo
import Z3.STPSolver


class ConcolicToolFront {
	
	static void main(args) {
		/*My Ubuntu dir*/
		def UbuDir="/home/Documents/SmartApp/TestFiles"
		/*My Windows dir*/
		//dir for source files
		def winDir = "E:\\ConcolicExecuter\\test"
		//dir for Concolic API
		def apiFile = "E:\\ConcolicExecuter\\APIFile.groovy"
		//dir for output files
		def outputFileForIns = "E:\\OutPut\\ConcolicFile\\"
		//SimpleFile sf = new SimpleFile()
		//sf.processAllApps(winDir, apiFile, outputFileForIns)
		
		def mutiplePathFilepath = "E:\\OutPut\\MutiplePathFile\\"
		
		
		//dir for source files
		def source = "E:\\OutPut\\ConcolicFile"
		//dir for output files
		def outputFileForPro = "E:\\OutPut\\ProfiledFile\\";
		
		new File(outputFileForIns).eachFile { file6 ->
			file6.delete();
		}
		
		//ProfileFile pf = new ProfileFile()
		//pf.processAllApps(source, outputFileForPro)
			
//
//		ArrayList<String> profileInfoList = new ArrayList<String>();
//		profileInfoList.add("1Profiling Info for API_SYM_9 and SYMhumidity1_Input: 0@0");
//		profileInfoList.add("1Profiling Info for API_SYM_9 and SYMhumidity1_Input: 0@10");
//		profileInfoList.add("1Profiling Info for API_SYM_9 and SYMhumidity1_Input: 0@20");
//		profileInfoList.add("1Profiling Info for API_SYM_9 and SYMhumidity1_Input: 0@30");
//		profileInfoList.add("1Profiling Info for API_SYM_9 and SYMhumidity1_Input: 0@40");
//		profileInfoList.add("1Profiling Info for API_SYM_9 and SYMhumidity1_Input: 0@50");
//
//
//		ProcessProfilingInfo ppi = new ProcessProfilingInfo(profileInfoList);
//		String profileInfo = ppi.getSMTProfilingInfo();
//		int ifcount = ppi.getIfCount();
//
//
//		PCPackage pck = new PCPackage("@(declare-const API_SYM_9 Integer)@(declare-const SYMhumidity1_Input Integer)#@(assert (! (> API_SYM_9 5)))@(assert (! (> SYMhumidity1_Input 20)))@(assert (> 50 40))#0", profileInfo, ifcount);
//		//ArrayList<Integer> arrayList = new ArrayList<Integer>();
//		//ArrayList<String> arrayList = new ArrayList<String>();
//		//List<Object> arrayList = new ArrayList<Object>();
//		ArrayList<PCPackage> pcPackageList = new ArrayList<PCPackage>();
//		pcPackageList.add(pck);
//
//		Set<String> unSATSet = new HashSet<String>();
//
//		PathExplore pe = new PathExplore(pcPackageList, unSATSet);
//		pe.explorePath();
//
//		if(unSATSet.size()>0)
//		{
//			String temp = unSATSet.size() + " paths did not reach which are:\n"
//			Iterator<String> it = unSATSet.iterator();
//			while (it.hasNext()) {
//			  String str = it.next();
//			  temp = temp + str + "\n";
//			}
//			println(temp);
//		}
		
		//STPSolver stp = new STPSolver("E:\\OutPut\\SMTFile\\query1.smt2");
		//stp.getSolution();
		
		RunSystem sys = new RunSystem(winDir, apiFile, outputFileForIns, outputFileForPro, mutiplePathFilepath);
		sys.run();

	}

}

