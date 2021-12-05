package Instrument

import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codehaus.groovy.ast.stmt.Statement

class ProcessIf {
	def static execute (IfStatement ifStmt , String program, def file, def programHash, ArrayList<ArrayList<String>> listForInput, ArrayList<String> listEnv, HashMap<String, String> mapForDefined) {
		
		//String for declartion
		String declareVariable = null;
		
		//def location=ifStmt.getLineNumber()
		def booleanExp=ifStmt.booleanExpression.expression
		
		ArrayList<String> listForDef = new ArrayList();
		
		// a list to store lazy initialization
		ArrayList<String> listForLazy = new ArrayList();
		
		//a list to store the profiling information
		ArrayList<String> runtimeDataBase = new ArrayList();
		
		String condition  = ProcessExpression.execute (booleanExp, listForDef, listForInput, listEnv, mapForDefined, declareVariable, listForLazy, runtimeDataBase);
		
		
		for(int i = 0; i< listForDef.size();i++)
		{

			String defstr = listForDef.get(i);
			//closure should not have an update API or program crashed
			if(defstr.length()>7 && defstr.substring(0, 7) == "CLOSURE")
			{
				program = program + defstr.substring(7, defstr.length());
				program = program + "";
			}
			else if(defstr.length()>4 && defstr.substring(0, 4) == "METD")
			{
				program = program + defstr.substring(4, defstr.length());
				program = program + "\n";
			}
			else
			{
				program = program + defstr
				program = program + ProcessExpressionAPI.executeStatement(null, defstr, "") + "\n"
			}
				
		}
		
		
		//int l = ifStmt.lineNumber
		//program = program + "log.debug \"RelyInfo for if on " + l + ":  \${state.relyInfo}\"\n"
		//program = program + "state.varInPC = \"\" \n\n"
		program = program + "//INSERT_RELY_FOR_IF \n"
		// output all the concrete variables 
		//program = program + "log.debug \"\${getConcreteVariable(programState)}\" \n"
		
		program = program + "if("
		

		program = program + condition + "){" + "\n"
		
		program = program + "state.ifCount = state.ifCount + 1; \n\n"
		
		//Arraylist to store the expressions
		ArrayList<String> expSet = new ArrayList<String>();
		
		//-------------------------------insert API-------------------------------------
		//replace the " in condition
		String condition2 = condition.replace("\"","'")
		program = program + "AddPathCondition(state.pathCondition, programState, \"$condition2\");" + "\n"
		
		
		String SMT = ProcessExpressionSMTLIB.executeExpression(booleanExp, expSet, condition);
		//replace the " in SMT LIB formula
		SMT = SMT.replace("\"","'")
		//String for a variable that involve the condition, we can use it to get the type of every variables
		println (condition)
		String typeVariable = "getType(" + expSet.get(0) + " )"
		program = program + "AddSMTPathCondition(programState, " + "\"" + SMT + "\"," + typeVariable + "  )\n\n"
		
		//-------------------------------end-------------------------------------
		
		def ifBlock = ifStmt.ifBlock
		def elseBlock = ifStmt.elseBlock

		def ifStmts = ifBlock.statements
		
		ifStmts.each {stmt ->
			
			program = ProcessStatement.execute(stmt, program, file, programHash, listEnv, listForInput, mapForDefined)
		}
		
		//still for collecting profiling information
		program = program + "//INSERT_VARINPC_FOR_IF \n\n"
		
		program = program + "}" + "\n"
		
		def elseStmt
		if(elseBlock instanceof BlockStatement)
		{
			program = program + "else{ \n"
			program = program + "state.ifCount = state.ifCount + 1; \n\n"

			//-------------------------------insert API-------------------------------------
			//insert path condition
			program = program + "AddPathCondition(state.pathCondition, programState, \"!  $condition2\");" + "\n"
			
			program = program + "AddSMTPathCondition(programState, " + "\"(! " + SMT + ")\"," + typeVariable + ")\n\n"
			//-------------------------------end-------------------------------------
			elseStmt = elseBlock.statements
			elseStmt.each {stmt ->
				
				program = ProcessStatement.execute(stmt, program, file, programHash, listEnv, listForInput, mapForDefined)

			}
			//still for collecting profiling information
			program = program + "//INSERT_VARINPC_FOR_IF \n\n"
			
			program = program + "}" + "\n"
		}
		else 
		{
			println ("No else block")
			program = program + "else{ \n"
			program = program + "state.ifCount = state.ifCount + 1; \n\n"
			//-------------------------------insert API-------------------------------------
			//insert path condition
			program = program + "AddPathCondition(state.pathCondition, programState, \"!  $condition2\");" + "\n"
			program = program + "AddSMTPathCondition(programState, " + "\"(! " + SMT + ")\"," + typeVariable + ")\n\n"
			//-------------------------------end-------------------------------------
			
			//still for collecting profiling information
			program = program + "//INSERT_VARINPC_FOR_IF \n\n"
			
			program = program + "}" + "\n"
		}
		
		return program;
		

	}

}
