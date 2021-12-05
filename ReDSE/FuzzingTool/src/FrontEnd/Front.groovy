package FrontEnd

import Interact.Selenium
import Interact.selenium2



class Front {
	
	static void main(args) {
		/*My Ubuntu dir*/
		def UbuDir="/home/Documents/SmartApp/TestFiles"
		/*My Windows dir*/
		//dir for source files
		def winDir = "E:\\ConcolicExecuter\\test"
		//dir for output files
		def outputFileForIns = "E:\\OutPut\\FuzzingFile\\"


		new File(outputFileForIns).eachFile { file6 ->
			file6.delete();
		}
		
		RunSystem sys = new RunSystem(winDir, outputFileForIns);
		sys.run();

	}

}

