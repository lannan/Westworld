package FrontEnd

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase

import Instrument.*

class SimpleFile {
	HashMap<String, String> titleMap = new HashMap<String, String>();
	
	//Process all the files in the dir
	void processAllApps(def dir, def apiFile, def outputFilePath) {

		def outputFile = ""
		new File(dir).eachFile { file ->
			println "processing ${file.getName()}"
			
			outputFile = outputFilePath + "temp_" + file.getName() ;
			
			processApp(file,  apiFile,  outputFile)

			
		}
		
		new File(outputFilePath).eachFile { file ->
			println "processing ${file.getName()}"
			
			outputFile = outputFilePath + file.getName().replace("temp_", "");
			
			replaceIf(file, outputFile)

			
		}
		
	}

	//Process each one of them
	def processApp(def file , def apiFile, def outputFile) {
		//generate AST for this file
		//Don't care how it works
		def astClassNodes = new AstBuilder().buildFromString(CompilePhase.SEMANTIC_ANALYSIS, false, file.text).find { it.class == ClassNode.class }
		
		
		//new a visitor to traverse the AST
		//*******************we use this visitor to get input variables****************************
		InsnVisitor visitor = new InsnVisitor()
		
		//active the visitor
		astClassNodes.visitContents(visitor)
		
		//Get all the method nodes in AST
		def MethodNodes = astClassNodes.getMethods()
		
		//a list to store input environments
		ArrayList<String> listEnv = new ArrayList<String>();
		
		listEnv = visitor.getEnvList();
		
		//add other environment objects that did not appear in preferences
		listEnv.add("location");
		
		titleMap = visitor.getTitleMap();
		
		//Process all the method
		ConcolicExecutor ce= new ConcolicExecutor(MethodNodes, visitor, file , apiFile, listEnv, titleMap)
		
		
		
		//********************************************Get the output String and store it to file*******************************************
		ArrayList<String> output = new ArrayList<String>();
		output = ce.GetOutput();
		
		
		//write to file
		FileWriter fw = new FileWriter(outputFile, true);
		BufferedWriter bw = new BufferedWriter(fw);
		
		for(int i = 0; i< output.size();i++) 
		{
			bw.write(output.get(i));
		}
		bw.close();
		fw.close();
		println "Finished one file!"
	}
	
	def getTitleMap() 
	{
		return titleMap;
	}
	
	//insert replyInfo with line number before if
	def replaceIf(def file, def outputFile) 
	{
		ArrayList<String> output = new ArrayList<String>();
		
		HashMap<Integer, String> programHash = new HashMap<Integer, String>();
		
		//Read the file and store it in hash
		ReadFile.StoreWholeFileInHash(file,programHash);
		
		//l is for the line number
		int l = 0;
		//because each time we add codes for if, the linenumber will increase by 2, so we need this flag
		int increase = 0;
		
		Stack<Integer> stack = new Stack<Integer>();
		
		for(int i = 1; i < programHash.size()+1; i++)
		{
			String line = programHash.get(i);
			
			if(line == "//INSERT_RELY_FOR_IF ") 
			{
				l = i + 3 + increase
				stack.push(l);
				stack.push(l);
				
				//String str = "//log.debug \"RelyInfo#" + l + ": \${state.relyInfo}\"\n"
				String str = "";
				str = str + "processLogString(" + "\"RelyInfo#" + l + "-\${forCountI}" + ": \${state.relyInfo}\"" + ")\n"
				
				str = str + "state.varInPC = \"\" \n"
				//str = str + "//log.debug \"ConcreteVariables#" + l + ": \${getConcreteVariable(programState)}\" \n\n"
				str = str + "processLogString(" + "\"ConcreteVariables#" + l + "-\${forCountI}" + ": \${getConcreteVariable(programState)}\"" + ")\n"
				
				output.add(str);
			}
			else if(line == "//INSERT_VARINPC_FOR_IF ") 
			{
				int l2 = stack.pop();
				//String str = "//log.debug \"VarInPC#" + l2 + ": \${state.varInPC}\"\n"
				String str = "";
				str = str + "processLogString(" + "\"VarInPC#" + l2 + "-\${forCountI}" + ": \${state.varInPC}\"" + ")\n"
				output.add(str)
				increase = increase + 1
			}
			else 
			{
				output.add(line + "\n");
			}
			
		}
		
		//write to file
		FileWriter fw = new FileWriter(outputFile, true);
		BufferedWriter bw = new BufferedWriter(fw);
		
		for(int i = 0; i< output.size();i++)
		{
			bw.write(output.get(i));
		}
		bw.close();
		fw.close();
		println "Finished one file!"
	}
}
