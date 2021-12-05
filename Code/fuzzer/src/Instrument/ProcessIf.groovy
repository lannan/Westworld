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
	def static execute (IfStatement ifStmt , String program, def file, ArrayList<Integer> ifcountList) {
		
		//def location=ifStmt.getLineNumber()
		String condition = ifStmt.booleanExpression.expression.getText();
		
		int ifcount = ifcountList.get(0);
		
		ifcount = ifcount  + 1;
		
		ifcountList.clear();
		ifcountList.add(ifcount);


		program = program + "if("
		

		program = program + condition + "){" + "\n"
		
		
		program = program + "state.pathCondition = state.pathCondition + \"" + ifcount +  "\"+ \"if\"; \n\n"
		
		//Arraylist to store the expressions
		ArrayList<String> expSet = new ArrayList<String>();
		
		
		def ifBlock = ifStmt.ifBlock
		def elseBlock = ifStmt.elseBlock

		def ifStmts = ifBlock.statements
		
		ifStmts.each {stmt ->
			
			program = ProcessStatement.execute(stmt, program, file, ifcountList)
		}
		
		program = program + "}" + "\n"
		
		def elseStmt
		if(elseBlock instanceof BlockStatement)
		{
			ifcount = ifcount  + 1;
			ifcountList.clear();
			ifcountList.add(ifcount);
			program = program + "else{ \n"
			
			program = program + "state.pathCondition = state.pathCondition + \"" + ifcount +  "\"+ \"else\"; \n\n"

			elseStmt = elseBlock.statements
			elseStmt.each {stmt ->
				
				program = ProcessStatement.execute(stmt, program, file, ifcountList)

			}
			
			program = program + "}" + "\n"
		}
		else 
		{
			ifcount = ifcount  + 1;
			ifcountList.clear();
			ifcountList.add(ifcount);
			println ("No else block")
			program = program + "else{ \n"
			
			program = program + "state.pathCondition = state.pathCondition + \"" + ifcount +  "\"+ \"else\"; \n\n"
			
			program = program + "}" + "\n"
		}
		
		return program;
	}

}
