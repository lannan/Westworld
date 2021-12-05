package Instrument

import java.util.ArrayList
import java.util.Map
import java.util.Set
import java.util.Stack

import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.BreakStatement
import org.codehaus.groovy.ast.stmt.CaseStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.SwitchStatement
import org.codehaus.groovy.ast.stmt.WhileStatement
import org.codehaus.groovy.control.CompilerConfiguration

class ConcolicExecutor {

	//HashSet to store all the methods which are not entry methods
	public static Set<String> notEntryMethod=new HashSet<String>()

	//stores the variables come from input method
	Map<String,String> Input
	//stores the entry methods name
	Set<String> EntryMethodsName
	
	//ArrayList for output
	ArrayList<String> output = new ArrayList<String>();
	
	public ConcolicExecutor(def allMethodNodes, def visitor, def file, def apiFile, ArrayList<String> listEnv, HashMap<String, String> titleMap) 
	{
		
		
		//get the environment variables of Input
		Input=visitor.varNames
		
		//Get the entryMethods
		EntryMethodsName = visitor.allEntryMethods
		
		//Put the method we dont want to analyze into notEntryMethod
		notEntryMethod.add("main");
		notEntryMethod.add("run");
		notEntryMethod.add("installed");
		notEntryMethod.add("updated");
		notEntryMethod.add("initialize");
		

		println ("**********************")
		/*symbolic execute a method if it is an entry method*/
		//flag to record if this is the first method we process
		int flag = 0;
		//process all the entry methods
		allMethodNodes.each { node->
			//only process entry method
			if(node.lineNumber!=-1 && !(notEntryMethod.contains(node.getName()))) {
				//read the head of the file before read the entry methods
				if(flag == 0) 
				{
					String beforeEntry = ReadFile.ReadBeforeLine(file, node.lineNumber)
					output.add(beforeEntry)
				}
				//then entry methods
				ProcessEntryMethods PM = new ProcessEntryMethods()
				String entryMethod
				if(EntryMethodsName.contains(node.getName())) 
				{
					entryMethod = PM.ProcessMethods(node, file, apiFile, listEnv, true, titleMap)
				}
				else 
				{
					entryMethod = PM.ProcessMethods(node, file, apiFile, listEnv, false, titleMap)
				}
				
				String prefer = PM.GetPreferString() 
				output.add(prefer)
				output.add(entryMethod)
				flag = flag + 1;
				println ("**********************")
			}
		}
		//-------------------------------insert PCBound-------------------------------
		String preference = "preferences {\n"
		preference = preference + "section(\"PCbound:\") {\n"
		
		preference = preference + "input " + "\"PCBOUND\"" + ", \"number\", title: \"PCBOUND\"" + ", required: false \n"
		preference = preference + "}\n}\n\n"
		output.add(preference)
		
		//-------------------------------insert API-------------------------------------
		//Add dynamic API
		String dynamicAPI = "//*************************************************APIs WE ADD************************************************************\n\n"
		dynamicAPI = dynamicAPI +"def AddInitSymbolicValue(Map<String, List<Object>> programState){ \n";
		dynamicAPI = dynamicAPI + "//Add into Program State \n";
		for (Map.Entry<String, String> entry : Input.entrySet()) {
			//println ("Key = ${entry.getKey()}  Value =  ${entry.getValue()}")
			
			dynamicAPI = dynamicAPI + "AddIntoMap(programState, \"${entry.getKey()}\",\"${entry.getValue()}\",\"${entry.getValue()}\");" + "\n";
		}
		dynamicAPI = dynamicAPI + "} \n\n";
		//-------------------------------end---------------------------------------------
		output.add(dynamicAPI)
		
		
		//-------------------------------insert API-------------------------------------
		//add static API

		File concolicFile = new File(apiFile);
		String staticAPI = ReadFile.ReadAllFile(concolicFile)
		//-------------------------------end---------------------------------------------
		output.add(staticAPI)
		
	
	}
	
	def GetOutput() 
	{
		return output;
	}

}
