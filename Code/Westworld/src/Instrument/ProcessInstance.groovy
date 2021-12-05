package Instrument
import org.codehaus.groovy.ast.expr.Expression

import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
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


class ProcessInstance {
	def static execute(def exp ,ArrayList<String> listForDef, ArrayList<ArrayList<String>> listForInput, ArrayList<String> listEnv, HashMap<String, String> mapForDefined, String declareVariable, ArrayList<String> listForLazy, ArrayList<String> runtimeDataBase)
	{
		
		//list for storing expressions in arguments
		ArrayList<String> list = new ArrayList<String>();
		
		//list for storing the arguments temp name
		ArrayList<String> listTemp = new ArrayList<String>();
		
		String program = "";
		int tag = 0;
		
		def arguments = exp.getArguments().expressions;
		
		arguments.each { a ->

			String left = "def tempVariable_" + a.columnNumber + a.lineNumber + " = "
			
			String tempName = "tempVariable_" +  a.columnNumber + a.lineNumber
			
			listTemp.add(tag, tempName)
			
			left = left + ProcessExpression.execute(a, listForDef, listForInput, listEnv, mapForDefined, declareVariable, listForLazy, runtimeDataBase) + "\n"
			
			listForDef.add(left)
			
			tag = tag + 1;
		}
		
		
		def type = exp.getType().getName();
		

		
		program = program  + " new " + type + "("
		
		if(tag == 0)
		{
			program = program + ")" + "\n"
		}
		else
		{
			tag = tag - 1;
			for(int i = 0; i< tag; i++)
			{
				program = program + listTemp.get(i) + ", "
			}
	
			program = program + listTemp.get(tag)
			program = program + ")" + "\n"
		}
		
		
		return program;
		
	}

}
