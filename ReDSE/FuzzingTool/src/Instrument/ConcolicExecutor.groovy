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
	
	public ConcolicExecutor(def allMethodNodes, def visitor, def file) 
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
					entryMethod = PM.ProcessMethods(node, file, true)
				}
				else 
				{
					entryMethod = PM.ProcessMethods(node, file, false)
				}
				
				output.add(entryMethod)
				flag = flag + 1;
				println ("**********************")
			}
		}
		
	
	}
	
	def GetOutput() 
	{
		return output;
	}

}
