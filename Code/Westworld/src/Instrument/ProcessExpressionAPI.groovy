package Instrument

import org.codehaus.groovy.ast.expr.Expression

import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
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

class ProcessExpressionAPI {
	//expression is for decide if this statement is declaration or binary. Code is the final string we insert. program is the output
	def static executeStatement(Expression exp, String code, String program) {
		
		//if the expression is null, then the declaration is inserted by us
		if(exp == null) 
		{
			//Get the string from code
			String expression = code;
			
			//split it using = to get left and right expression
			String[] temp = expression.split("=");
			
			String left = temp[0];
			//remove the def in the left
			left = left.replace("def","");
			
			left = left.replace(" ","");
			
			//find if there are multiple =
			String right = ""
			if(temp.size()>2)
			{
				for(int i = 1; i< temp.size();i++)
				{
					right = right + "=" + temp[i];
				}
			}
			else
			{
				right = temp[1]
			}
			
			//replace the " using ' and ; using null
			right = right.replace("\"","'")
			right = right.replace(";","")
			
			right = right.replace("\n","");
			
			//delete the " " in the front
			if(right.charAt(0) == " ")
			{
				right = right.substring(1);
			}
			
			//println ("left : $left right : $right")
			program = program + "UpdateState(programState, \"$left\", \"$right\", \"\$$left\", getType($left) );" + "\n";
			
		}
		//DeclarationExpression must be put before BinaryExpression
		else if (exp instanceof DeclarationExpression) {
			
			//Using AST to get left expression because the source code contains def and other codes
			Expression leftExp = exp.leftExpression
			String left=leftExp.getText()
			
			//Get the string from code
			String expression = code;
			//split it using = to get left and right expression
			String[] temp = expression.split("=");
			//find if there are multiple =
			String right = ""
			if(temp.size()>2) 
			{
				for(int i = 1; i< temp.size();i++) 
				{
					right = right + "=" + temp[i];
				}
			}
			else 
			{
				right = temp[1]
			}
			
			//replace the " using ' and ; using null
			right = right.replace("\"","'")
			right = right.replace(";","")
			
			right = right.replace("\n","");
			
			//delete the " " in the front
			if(right.charAt(0) == " ") 
			{
				right = right.substring(1);
			}
			
			//println ("left : $left right : $right")
			program = program + "UpdateState(programState, \"$left\", \"$right\", \"\$$left\", getType($left) );"
			
			
		} 
		else if (exp instanceof BinaryExpression) {
			if(exp.operation.getText().equals("=")) 
			{
			//Using AST to get left expression because the source code contains def and other codes
			Expression leftExp = exp.leftExpression
			String left=leftExp.getText()
			
			//Get the string from code
			String expression = code;
			//split it using = to get right expression
			String[] temp = expression.split("=")
			
			//find if there are multiple =
			String right = ""
			if(temp.size()>2) 
			{
				for(int i = 1; i< temp.size();i++) 
				{
					right = right + "=" + temp[i];
				}
			}
			else 
			{
				right = temp[1]
			}
			
			//replace the useless symbol
			right = right.replace("\"","'")
			right = right.replace(";","")
			
			right = right.replace("\n","");
			
			//delete the " " in the front
			if(right.charAt(0) == " ")
			{
				right = right.substring(1);
			}
				
			program = program + "UpdateState(programState, \"$left\", \"$right\", \"\$$left\", getType($left) );" 

			}	
		}	
		else {
			println ("APIexp: pay attention other expressions!!! : " + exp.toString())
		}
		return program
	}

	
}
