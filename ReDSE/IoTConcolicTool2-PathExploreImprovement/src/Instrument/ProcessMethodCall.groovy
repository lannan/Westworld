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



class ProcessMethodCall {
	
	
	def static execute(def exp , ArrayList<String> listForDef, ArrayList<ArrayList<String>> listForInput, ArrayList<String> listEnv, HashMap<String, String> mapForDefined, String declareVariable, ArrayList<String> listForLazy, ArrayList<String> runtimeDataBase) 
	{	
	
		//list for storing the arguments temp name
		ArrayList<String> listTemp = new ArrayList<String>();
		
		
		//A string for update 
		String update = "";
		
		
		String program = "";
		int tag = 0;
		
		def arguments = exp.getArguments().expressions;
		
		arguments.each { a ->

			String left = "def tempVariable_" + a.columnNumber + a.lineNumber + " = "
			
			String tempName = "tempVariable_" +  a.columnNumber + a.lineNumber
			
			listTemp.add(tag, tempName)
			
			String arg = ProcessExpression.execute(a, listForDef, listForInput, listEnv, mapForDefined, declareVariable, listForLazy, runtimeDataBase)
			
			left = left + arg + "\n"
			
			//closure expression should not have get type API(it has no type, or the original program crashed) So we need to add a tag for closure to stop adding api for it
			if(a instanceof ClosureExpression) 
			{
				left = "CLOSURE" + left;
			}
			
			listForDef.add(left)
			
			tag = tag + 1;
		}
		
		def object = exp.getObjectExpression();
		def method = exp.getMethod();
		
		def add = object.getText()
		
		//CompareWithEnvironment.compare(object.getText(),property.getText(),listForInput);
		
		String objectStr = ProcessExpression.execute(object, listForDef, listForInput, listEnv, mapForDefined, declareVariable, listForLazy, runtimeDataBase);
		String methodStr = ProcessExpression.execute(method, listForDef, listForInput, listEnv, mapForDefined, declareVariable, listForLazy, runtimeDataBase);
		
		objectStr = objectStr.replace("\"", "");
		methodStr = methodStr.replace("\"", "");
		
		//this is a real method call statement from the system method; So we dont need to get it out
		if(objectStr == "this") 
		{
			String str  = methodStr + "("
			
			if(tag == 0)
			{
				str = str + ")"
			}
			else
			{
				tag = tag - 1;
				for(int i = 0; i< tag; i++)
				{
					str = str + listTemp.get(i) + ", "
				}
			
				str = str + listTemp.get(tag)
				str = str + ")"
			}
			str = str + "\n"
			
			program = program + str;
			return program;
		}

		
		String name = "tempVariable_" + object.columnNumber + object.lineNumber + method.columnNumber + method.lineNumber
		
		String define = name + " = "
		
		define = define + objectStr + "." + methodStr + "("
		
		if(tag == 0) 
		{
			define = define + ")"
		}
		else 
		{
			tag = tag - 1;
			for(int i = 0; i< tag; i++)
			{
				define = define + listTemp.get(i) + ", "
			}
	
			define = define + listTemp.get(tag)
			define = define + ")"
		}
		define = define + "\n"
		
		//Compare with environment database to see if it is environment variable
		CompareWithEnvironment cwe = new CompareWithEnvironment(objectStr, methodStr, listEnv)
		//check if the environment method returns a basic value and added it into input
		if(cwe.ReturnIsEnvironmentBasicValue())
		{
			ArrayList<ArrayList<String>> tempList = new ArrayList<ArrayList<String>>();

			String type = cwe.getType()
			String title = "The input value for " + objectStr + "." + methodStr
			String name2 = name +"#";
			tempList.add(name2)
			tempList.add(type)
			tempList.add(title)
				
			listForInput.add(tempList)
		}
		//if is Env Object we need to add the declare variable into the envList
		if(cwe.ReturnIsEnvironmenObject()) 
		{
			if(declareVariable != null) 
			{
				listEnv.add(declareVariable);
			}
			
		}
		
		//for API handling we need to discuss if the method is a environment API and if it has return value
		//the file that contains no return methods are already environment methods, so we don't need to decide if the method is a ENV method here
		if(cwe.IsNoReturnMethod()) 
		{
			//list to store the arguments
			ArrayList<String> listArg = new ArrayList<String>();
			
			//the name of the envVariable
			String Name = objectStr + "." + methodStr;
			//find all the arguments
			arguments.each { a ->
							listArg.add(a.getText());
							}
						
			String parameter = "";
			int size = listArg.size();
			for(int i = 0; i< size-1; i++)
			{
				parameter = parameter + listArg.get(i) + ",";
			}
			if(size > 0) 
			{
				parameter = parameter + listArg.get(size - 1)
			}
			else 
			{
				parameter = ""
			}
			update = "EnvironmentUpdate(programState, programEnvironment, "
			//"thethermostat.HeatingSetpoint", "level", $level);"
			if(size == 1) 
			{
				update = update + "\"" + Name + "\", " + "\"" + parameter  + "\", "  + parameter  +");\n";
			}
			else 
			{
				update = update + "\"" + Name + "\", " + "\"" + parameter  + "\", " + "\" mutiple para \"" + ");\n";
			}
			update = "METD" + update
		}		
		//the method is a Env method and it has returned value, the returned value is an object/the method belongs to an object
		else if(cwe.ReturnIsEnvironmenObject() || (cwe.ReturnIsEnvironmentBasicValue() && object.getText() != "this"))
		{
			String VarName = "NULL"
			
			if(declareVariable != null)
			{
				VarName = name;
			}
			
			
			//list to store the arguments
			ArrayList<String> listArg = new ArrayList<String>();

			arguments.each { a ->
				listArg.add(a.getText());
			}
			
			String parameter = "";
			int size = listArg.size();
			for(int i = 0; i< size-1; i++)
			{
				parameter = parameter + listArg.get(i) + ",";
			}
			//if size = 0, get(-1) will crash
			//the method has arguments
			if(size > 0) 
			{
				parameter = parameter + listArg.get(size - 1)
				
				String flag = "";
				//if is the return value is an object, during runtime it need to be added into the list
				if(cwe.ReturnIsEnvironmenObject())
				{
					flag = "true";
					
					update = "LazyInit(programState, lazyList, "
					
					update = update + "\"" + objectStr + "\", " + "\"" + parameter  + "\", " + "\"" + VarName  + "\", " +  "\"" + flag + "\"" +");\n";
					update = "METD" + update
				}
				//the return value is not an object but a basic value and it has a parameter so we need profiling
				else
				{
					
				}
				
			}
			else if(cwe.ReturnIsEnvironmenObject()) 
			{
				String flag = "true";
				
				update = "LazyInit(programState, lazyList, "
				
				update = update + "\"" + objectStr + "\", " + "\"" + parameter  + "\", " + "\"" + VarName  + "\", " +  "\"" + flag + "\"" +");\n";
				update = "METD" + update
			}
			//there are no parameter, so we treat it as the value in the env object or some new SYM values
			//exp. a.size(), a.getPoint()
			else 
			{
				//environmentGet
				if(methodStr.length()>3 && methodStr.substring(0, 3) == "get") 
				{
					//the name of the envVariable
					String tempMethod = "";
					tempMethod = "set" + methodStr.substring(3, methodStr.length());
					String Name = objectStr + "." + tempMethod;
					
					
					update = name + " = EnvironmentGetValue(programState, programEnvironment, "
					
					update = update + "\"" + Name + "\" ," + "\"" + VarName + "\"," + VarName + ", " + VarName + "#);\n";
					
					update = "METD" + update
				}
				//profiling exp. a.size()
				else 
				{
				}
				
			}		

		}
		//it is not environment object but a defined object
		else 
		{			
			String VarName = "NULL"
			
			if(declareVariable != null)
			{
				VarName = name;
			}
			
			ArrayList<String> listArg = new ArrayList<String>();
			
			arguments.each { a ->
				listArg.add(a.getText());
			}
						
			String parameter = "";
			int size = listArg.size();
			for(int i = 0; i< size-1; i++)
			{
				parameter = parameter + listArg.get(i) + ",";
			}
			
			String flag = "true";
			
			update = "LazyInit(programState, lazyList, "
			
			update = update + "\"" + objectStr + "\", " + "\"" + parameter  + "\", " + "\"" + VarName  + "\", " +  "\"" + flag + "\"" +");\n";
			update = "METD" + update
		}
		
		
		define = "def " + define
		
		listForDef.add(define)
		
		//the api for lazy has been moved to listForDef, so listForLazy is not used.
		if(update != "") 
		{
			listForDef.add(update)
		}
		
		
		program = program + name
		
		program = program
		
		return program;
		
	}

}
