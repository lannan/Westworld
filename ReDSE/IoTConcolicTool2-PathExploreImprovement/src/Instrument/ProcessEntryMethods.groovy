package Instrument

import java.util.LinkedList;

import java.util.Queue;
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.BreakStatement
import org.codehaus.groovy.ast.stmt.CaseStatement
import org.codehaus.groovy.ast.stmt.EmptyStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.SwitchStatement
import org.codehaus.groovy.ast.stmt.WhileStatement

class ProcessEntryMethods {
	
	//String for the output program
	String program ="";
	
	//String for preference we add
	String prefer = "";
	
	String ProcessMethods(def methodNode, def file, def apiFile, ArrayList<String> listEnv, def isEntry, HashMap<String, String> titleMap) {

		//Map for the whole program    I put it here because it will be used in different objects of ProcessStatement (if,else,for also have blocks of statements)
		HashMap<Integer, String> programHash = new HashMap<Integer, String>();
		//Read the file and store it in hash
		ReadFile.StoreWholeFileInHash(file,programHash);
		
		//String to store the name of evt parameter
		String evtName = "";
		
		//Get Method name
		def methodName =methodNode.getName()
		println "Method Name : ${methodName}"
		
		//put it into string 
		program = program + "def " + methodName + "(";
		
		//find parameters and put them into string
		def parameters=methodNode.parameters
		
		if(isEntry) 
		{
			parameters.each { p ->
				println "Contains parameters : ${p.name}"
				//evt doesn't have def but others have
				//whatever the entry method should have one parameter (evt) ONLY
				if(p.name == "evt")
				{
					program = program + p.name + ",";
					evtName = p.name;
				}
				else
				{
					program = program + p.name + ",";
					evtName = p.name;
				}
			}
		}
		else 
		{
			//The other methods should not have evt as parameter
			parameters.each { p ->
				println "Contains parameters : ${p.name}"
				program = program + "def " + p.name + ",";
				
			}
		}
		
		//delete the last "," we added
		if(parameters.size()!=0) 
		{
			program = program.substring(0, program.length()-1)
		}
		
		//Add the begin { into string 
		program = program + "){ \n"
		
		if(isEntry) 
		{
			program = program + "//PEI_HEAD\n\n"
			program = program + "\n//INSERT_FOR_START" + "\n\n"
		}
		else
		{
			program = program + "//INSERT_FOR_API" + "\n"
		}
		
		
		//-------------------------------insert API-------------------------------------
		program = program + "//***********************************************One String for path Condition******************************************* \n"
		//Only init the PC when the method is entry method (appeared in subscribe)
		if(isEntry)
		{
			program = program + "state.pathCondition = \"\" \n";
			program = program + "state.pcSmt = \"\" \n";
			program = program + "state.declare = \"\" \n";
			program = program + "state.tag = 1 \n\n";
			//string for relations between symbolic variables
			program = program + "state.relyInfo = \"\" \n"
			program = program + "state.varInPC = \"\" \n"
			program = program + "state.objEnv = \"\" \n"
			program = program + "state.ifCount = 0 \n"
			program = program + "state.logString = \"\" \n"
			program = program + "int forCountI = 0;\n"
			 
		}
		else
		{
			program = program + "int forCountI = 0;\n"
		}
		
		program = program + "//new map for state \n Map<String, List<Object>> programState = new HashMap<String, List<Object>>(); \n //new map for environment \n Map<String, Object> programEnvironment = new HashMap<String, List<Object>>(); \n //new map for profiling \n Map<String, String> profilingInfo = new HashMap<String, String>(); \n //new list for lazy initialization \n ArrayList<String> lazyList = new ArrayList<String>(); \n";
		program = program + "//*********************************************Add all the symbolic value from input***************************************** \n AddInitSymbolicValue(programState); \n"
		//inser1t parameter into state
		program = program + "//For function involve parameter, we need to add parameter variable \n"
		parameters.each { p ->
			//for evt we need to add evt.name
			if(p.name == "evt") 
			{
				program = program + "AddIntoMap(programState, \"" + p.name + "\""  +  ",\"" + "\${" +  p.name + ".name}\" , \"evt\"); \n\n"
			}
			else 
			{
				program = program + "AddIntoMap(programState, \"" + p.name + "\""  +  ",\"" + "\$" +  p.name + "\", getType(${p.name}) ); \n\n"
			}
			
			
		}
		
		
		
		
		
		//------------------------------------end--------------------------------------
		
		//add a list to store the input of preference
		ArrayList<ArrayList<String>> listForInput = new ArrayList();
		
		//add a map to store the defined variable and temp name
		HashMap<String, String> mapForDefined = new HashMap<String, String>();

		//find statements includes assign and if else
		def stmts = methodNode.getCode().statements
		
		//put evtName into environment
		listEnv.add(evtName);
		
		//Process each statement 
		stmts.each {stmt ->
			program = program + "\n"
			program = ProcessStatement.execute(stmt, program ,file, programHash, listEnv, listForInput, mapForDefined) + "\n"
		}
		
		program = program + "\n\n//******************************************************Output Data we get**************************************************\n"
		program = program + "//log.debug \"\$programState\" "  + "\n"
		program = program + "//log.debug \"\$programEnvironment\" "  + "\n"
		program = program + "//log.debug \"\$lazyList\" "  + "\n"
		program = program + "//log.debug \"\$profilingInfo\" "  + "\n"
		
		program = program + "processLogString(" + "\"\$profilingInfo\"" + ")\n"
		if(isEntry)
		{
			program = program + "//log.debug \"PC:  \${state.pathCondition}\" " + "\n"
			program = program + "processLogString(" + "\"PC:  \${state.pathCondition}\"" + ")\n"
			
			program = program + "//log.debug \"Declare:  \${state.declare}\" " + "\n"
			program = program + "processLogString(" + "\"Declare:  \${state.declare}\"" + ")\n"
			
			program = program + "state.pcSmt = state.pcSmt + \"#\" + PCBOUND" + "\n"
			program = program + "//log.debug \"PC in SMTLIB version:  \${state.pcSmt}\" " + "\n"
			program = program + "processLogString(" + "\"PC in SMTLIB version:  \${state.pcSmt}\"" + ")\n"
			
			program = program + "//log.debug \"RelyInfo:  \${state.relyInfo}\" " + "\n"
			program = program + "//log.debug \"varInPC:  \${state.varInPC}\" " + "\n"
			
			program = program + "//log.debug \"\${state.logString}\"" + "\n"
			
			program = program + "outputLog();" + "\n"
			
			program = program + "//PEI_TAIL\n"
		}
	
		
		program = program + "}" + "\n";
		
		//println "$program"
		
		//-------------------------------insert API Preference----------------------------------
		
		String preference = ""
		preference = preference + "preferences {\n"
		preference = preference + "section(\"Environment variables:\") {\n"
		
		for (int i = 0; i<listForInput.size();i++) 
		{
			preference = preference + "input " + "\"" + listForInput.get(i).get(0) + "\"" + ", " + "\"" + listForInput.get(i).get(1) + "\", " + "title: " + "\""  + listForInput.get(i).get(2) + "\"" + ", required: false \n"
			
			//add into title map
			String section = "Environment variables of" + methodName + ":";
			String subtitle = listForInput.get(i).get(2);
			
			String varName = listForInput.get(i).get(0);
			
			String title = section + "#" + subtitle
			
			titleMap.put(varName, title);
			
			
		}
		preference = preference + "}\n}\n\n"
		
		//------------------------------------end--------------------------------------
		
		
		
		if(listForInput.size() != 0)
		{
			program = preference + program;
			return program;
		}
		else 
		{
			return program;
		}
		
	}
	
	def GetPreferString() 
	{
		return prefer;
	}

}
