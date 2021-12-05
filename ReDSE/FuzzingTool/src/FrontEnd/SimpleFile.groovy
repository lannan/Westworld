package FrontEnd

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase

import Instrument.*

class SimpleFile {
	
	//Process all the files in the dir
	void processAllApps(def dir, def outputFilePath) {

		def outputFile = ""
		new File(dir).eachFile { file ->
			println "processing ${file.getName()}"
			
			outputFile = outputFilePath + "Fuzzing_" + file.getName() ;
			
			processApp(file, outputFile)

			
		}
		
		
	}

	//Process each one of them
	def processApp(def file, def outputFile) {
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
		
		
		//Process all the method
		ConcolicExecutor ce= new ConcolicExecutor(MethodNodes, visitor, file)
		
		
		
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
}
