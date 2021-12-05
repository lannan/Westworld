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

class ProcessExpressionSMTLIB {
	
	def static executeExpression(Expression exp, ArrayList<String> expSet, String condition) {
		
		//DeclarationExpression must be put before BinaryExpression
		if (exp instanceof VariableExpression) {
			String name= condition 
			//the first char may be (
			String condition2 = condition;
			if(condition.getAt(0) == "(") 
			{
				condition2 = condition.substring(1, condition.length());
			}
			expSet.add(condition2);
			
			return name
		}
		else if(exp instanceof ConstantExpression)
		{
			String str
			//the first char may be (
			String condition2 = condition;
			if(condition.getAt(0) == "(") 
			{
				condition2 = condition.substring(1, condition.length());
			}
			expSet.add(condition2);
			
			def obj=exp.getValue()
			if(obj instanceof String) 
			{
				str = "\"" + obj + "\""
			}
			else 
			{
				str = condition
			}
			
			return str
		}
		else if(exp instanceof MethodCallExpression) 
		{
			String str=condition
			//the first char may be (
			String condition2 = condition;
			if(condition.getAt(0) == "(") 
			{
				condition2 = condition.substring(1, condition.length());
			}
			expSet.add(condition2);
			
			return str
		}
		else if(exp instanceof NotExpression) 
		{
			condition = condition.replace("!","");
			String str = ProcessExpressionSMTLIB.executeExpression(exp.expression, expSet, condition);
			String SMT = "( " + "not" + " " + "( " + str + " )"+ ")"
			return SMT
		}
		else if (exp instanceof BinaryExpression) {
			Expression leftExp = exp.leftExpression
			Expression rightExp = exp.rightExpression
			String op = exp.getOperation().getText()
			
			//we need to split the original string using op
			String[] temp = condition.split(op);
			
			String leftCondition = temp[0].replace("(", "");
			String rightCondition = temp[1].replace(")", "");
			
			String left = ProcessExpressionSMTLIB.executeExpression(leftExp, expSet, leftCondition);
			String right = ProcessExpressionSMTLIB.executeExpression(rightExp, expSet, rightCondition);
			String SMT;
			if(op.equals("!=")) 
			{
				SMT = "("+ "not " + "( " + "=" + " " + left + " " + right + "))"
			}
			else 
			{
				SMT = "(" + op + " " + left + " " + right + ")"
			}
			return SMT

		}
		else 
		{
			String str = condition
			//the first char may be (
			String condition2 = condition;
			if(condition.getAt(0) == "(") 
			{
				condition2 = condition.substring(1, condition.length());
			}
			expSet.add(condition2);
			return str
			
		}
	}

	
}
