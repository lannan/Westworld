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
	
	String ProcessMethods(def methodNode, def file, def isEntry) {
		
		
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
		
		
		
		//-------------------------------insert API-------------------------------------
		program = program + "//***********************************************One String for path Condition******************************************* \n"
		//Only init the PC when the method is entry method (appeared in subscribe)
		if(isEntry)
		{
			program = program + "state.pathCondition = \"\" \n";
		}
		//------------------------------------end--------------------------------------

		//find statements includes assign and if else
		def stmts = methodNode.getCode().statements
		
		ArrayList<Integer> ifcountList = new ArrayList<Integer>();
		ifcountList.add(0);
		//Process each statement 
		stmts.each {stmt ->
			program = program + "\n"
			program = ProcessStatement.execute(stmt, program , file, ifcountList) + "\n"
		}
		
		if(isEntry)
		{
			program = program + "log.debug \"PC:  \${state.pathCondition}\" " + "\n"
		}
	
		
		program = program + "}" + "\n";
		
		
		return program;

	}

}
