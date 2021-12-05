package Instrument

import org.codehaus.groovy.ast.expr.Expression

import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
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

class ProcessExpression {
	
	def static execute(Expression exp, ArrayList<String> listForDef, ArrayList<ArrayList<String>> listForInput, ArrayList<String> listEnv, HashMap<String, String> mapForDefined, String declareVariable, ArrayList<String> listForLazy, ArrayList<String> runtimeDataBase) {
		
		
		
		String program ="";
		
		
		//DeclarationExpression must be put before BinaryExpression 
		if (exp instanceof DeclarationExpression) {
			
			Expression leftExp = exp.leftExpression
			Expression rightExp = exp.rightExpression
				
			
			//Processing the expression
			
			String left = ProcessExpression.execute (leftExp, listForDef, listForInput, listEnv, mapForDefined, declareVariable, listForLazy, runtimeDataBase);
			
			declareVariable = left;

			left = "def " + left + " = "
			String right=ProcessExpression.execute(rightExp, listForDef, listForInput, listEnv, mapForDefined, declareVariable, listForLazy, runtimeDataBase)
			

			program = left + right +"\n"
			
			
			
			
		} 
		else if (exp instanceof BinaryExpression) {
 			
			
			Expression leftExp = exp.leftExpression
			Expression rightExp = exp.rightExpression
			 
			 
			String op = exp.operation.getText()

			String left = ProcessExpression.execute(leftExp, listForDef, listForInput, listEnv, mapForDefined, declareVariable, listForLazy, runtimeDataBase)
			
			String right = ProcessExpression.execute(rightExp, listForDef, listForInput, listEnv, mapForDefined, declareVariable, listForLazy, runtimeDataBase)
			
			//binary expression should not have \n inside
			left = left.replace("\n","");
			right = right.replace("\n","");
			
			program = "(" + left + op + right + ")";
			
		} 
		else if (exp instanceof ConstantExpression) {
			
			def obj=exp.getValue()
			if(obj instanceof String)
			{
				program = program + "\"" + exp.getText() + "\"";
			}
			else
			{
				program = program + exp.getText();
			}
			
		}
		else if (exp instanceof MethodCallExpression) {

			String str=exp.getText()
			if(str.contains("log")) 
			{
				program = program + "//" + str
			}
			else 
			{
				
				String strForMethod = ProcessMethodCall.execute(exp, listForDef, listForInput, listEnv, mapForDefined, declareVariable, listForLazy, runtimeDataBase)
				program = program + strForMethod

			}
			
		}
		else if(exp instanceof PropertyExpression)
		{
			def object = exp.getObjectExpression();
			def property = exp.getProperty();
			
							
			String objPro = ProcessExpression.execute(object, listForDef, listForInput, listEnv, mapForDefined, declareVariable, listForLazy, runtimeDataBase) + "." + ProcessExpression.execute(property, listForDef, listForInput, listEnv, mapForDefined, declareVariable, listForLazy, runtimeDataBase)
			
			//because AST treat property and objPro as String, so they will be surrounded by "" because our prior process
			objPro = objPro.replace("\"", "");
			
			
			//re define it
			String left = "tempVariable_" + property.columnNumber + property.lineNumber + " = "
			
			//check if the property has already been redefined
			//IF COUSURE THEN STILL NEED DIFINE
			if(mapForDefined.containsKey(objPro) && listForDef.size()>0 && listForDef.get(0) != "CLOSURE") 
			{
				// use the defined temp name of this variable
				String tempName = mapForDefined.get(objPro);
				program = program + tempName;
			}
			else
			{
				String tempName = "tempVariable_" + property.columnNumber + property.lineNumber;
				mapForDefined.put(objPro, tempName)
				left = left + objPro + "\n"
				left = "def " + left
				
				listForDef.add(left)
				
				program = program + "tempVariable_" + property.columnNumber + property.lineNumber;
				
				//Compare with environment database to see if it is environment variable
				CompareWithEnvironment cwe = new CompareWithEnvironment(object.getText(), property.getText(), listEnv)
				if(cwe.ReturnIsDirectAccessBasicValue())
				{
					// add input for it
					ArrayList<ArrayList<String>> tempList = new ArrayList<ArrayList<String>>();
					String name = "tempVariable_" + property.columnNumber + property.lineNumber
					String type = cwe.getType()
					String title = "The input value for " + objPro.replace("\"", "");
					
					String tempName2 = name + "pair";
					tempList.add(tempName2)
					tempList.add(type)
					tempList.add(title)
					
					listForInput.add(tempList)
					
					// to be finish add getEnvironment method
					
					String tempMethod = "";
					tempMethod = "set" + property.getText().substring(0, 1).toUpperCase() +  property.getText().substring(1, property.getText().length());
					String Name = object.getText() + "." + tempMethod;
					
					String update; 
					
					update = name + " = EnvironmentGetValue(programState, programEnvironment, "
					
					update = update + "\"" + Name + "\" ," + "\"" + name + "\"," + name + ", " + name + "pair);\n";
					
					listForDef.add(update)
					
					
				}
				
				//if is Env Object we need to add the declare variable into the envList
				if(cwe.ReturnIsDirectAccessObject())
				{
					if(declareVariable != null)
					{
						listEnv.add(declareVariable);
					}
					
					String flag = "true";
					
					String update = "LazyInit(programState, lazyList, "
					
					update = update + "\"" + object.getText() + "\", " + "\""  + "\", " + "\"" + tempName  + "\", " +  "\"" + flag + "\"" +");\n";
					
					program = program + "\n" + update + "\n";
				}
			}
			
		}
		else if (exp instanceof ConstructorCallExpression) {

			program = program + ProcessInstance.execute(exp, listForDef, listForInput, listEnv, mapForDefined, declareVariable, listForLazy, runtimeDataBase)		
		}
		else if (exp instanceof VariableExpression) {
			

			String name=exp.getText()
			
			//if is Env variable we need to add the declare variable into the envList
			if(listEnv.contains(name)) {
				if(declareVariable != null)
				{
					listEnv.add(declareVariable);
				}
			}

			
			program = program + name
		}
		else if(exp instanceof NotExpression) 
		{

			String str=exp.getText()

			
			program = program + " ! " + str
		}
		else if(exp instanceof ClosureExpression)
		{
				
			def codeStmts = exp.getCode().statements
			
			ArrayList<String> listForDefTemp = new ArrayList<String>();
			listForDefTemp.add("CLOSURE");
			
			codeStmts.each {exp1 ->
			
				program = program  + ProcessExpression.execute(exp1.expression, listForDefTemp, listForInput, listEnv, mapForDefined, declareVariable, listForLazy, runtimeDataBase)
			}
			
			String define = "";
			
			for(int i = 1; i< listForDefTemp.size();i++)
			{
				
				String defstr = listForDefTemp.get(i);
				//closure should not have an update API or program crashed
				if(defstr.length()>7 && defstr.substring(0, 7) == "CLOSURE")
				{
					define = define + defstr.substring(7, defstr.length());
					define = define + "\n";
				}
				else if(defstr.length()>4 && defstr.substring(0, 4) == "METD")
				{
					define = define + defstr.substring(4, defstr.length());
					define = define + "\n";
				}
				else
				{
					define = define + defstr
					define = define + ProcessExpressionAPI.executeStatement(null, defstr, "") + "\n"
				}
			}
				
			program = "{ " + "\n"+ define + program + " }"

		}
		else if(exp instanceof ClassExpression)
		{
			program = program + exp.getText();
		}
		else if(exp instanceof GStringExpression) 
		{
			program = program + "\"" + exp.getText() + "\"";
		}
		else {
			println ("exp: pay attention other expressions!!! : " + exp.toString())
			String str=exp.getText()
				
			program = program + str
		}
		
		return program
	}

}
