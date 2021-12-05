package FrontEnd

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase

import java.util.List

import Profile.ProfileExecutor

class ProfileFile {
	
	ArrayList<String> profiledInfo;
	
	ProfileFile()
	{
		profiledInfo = new ArrayList<ArrayList<String>>();
	}
	


	//Process each one of them
	def processApp(def file , def outputFilePath, def fileName, ArrayList<String> dependence, HashMap<String, ArrayList<String>> profileDatabase, ArrayList<String> declareList) {
		
		
		//Process all the method
		ProfileExecutor pe= new ProfileExecutor(file)
		
		//sort the linenumbers
		TreeSet<Double> linenumberInLine = new TreeSet<Double>(); 
		linenumberInLine = pe.inline(dependence);
		
		
		//find the type of the inputs
		HashMap<String, String> typeMap = new HashMap<String, String>();
		for(int i = 0; i < declareList.size(); i++) 
		{
			String orgStr = declareList.get(i);
			orgStr = orgStr.replace("(","");
			orgStr = orgStr.replace(")","");
			
			String[] tempPair = orgStr.split(" ");
			
			String variable = tempPair[1];
			String type = tempPair[2];
			
			typeMap.put(variable, type);
			
		}
		
		//********************************************Get the output String and store it to file*******************************************
		HashMap<String, ArrayList<String>> finalOutput = new ArrayList<ArrayList<String>>();
		pe.addProfileCode(dependence, profileDatabase, linenumberInLine, profiledInfo, typeMap);
		finalOutput = pe.getOutput();
		
		for(String filename : finalOutput.keySet()) {
			
			String outputFile = outputFilePath + filename;
			
			ArrayList<String> output = new ArrayList<String>();
			output = finalOutput.get(filename);
			
			//output = deleteNoNeed(output);
			
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
		
		println "Finished one file!"
	}
	
	def ArrayList<String> getProfileList()
	{
		return profiledInfo;
	}
	
	static ArrayList<String> deleteNoNeed(ArrayList<String> input) 
	{
		ArrayList<String> output = new ArrayList<String>();
		
		for(int i = 0; i< input.size();i++)
		{
			String str = input.get(i);
			if(str.contains("UpdateState")||str.contains("LazyInit")||str.contains("AddSMTPathCondition")||str.contains("AddPathCondition")||str.contains("processLogString")||str.contains("AddInitSymbolicValue")||str.contains("AddIntoMap")) 
			{
				
			}
			else if(str.contains("APIs WE ADD")) 
			{
				break;
			}
			else 
			{
				output.add(str);
			}
		}
		return output;
	}
}
