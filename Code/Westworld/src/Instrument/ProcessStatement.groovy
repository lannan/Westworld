package Instrument

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

class ProcessStatement {

	def static execute(def stmt, String program, def file, HashMap<Integer, String> programHash, ArrayList<String> listEnv, ArrayList<ArrayList<String>> listForInput, HashMap<String, String> mapForDefined) {

		    String declareVariable = null;
					
			if(stmt instanceof ExpressionStatement) {
				println ("stmt: ExpressionStatement")
				ArrayList<String> listForDef = new ArrayList();
				
				// a list to store lazy initialization
				ArrayList<String> listForLazy = new ArrayList();
				
				
				//a list to store the profiling information
				ArrayList<String> runtimeDataBase = new ArrayList();
				
				String exps  = ProcessExpression.execute (stmt.expression, listForDef, listForInput, listEnv, mapForDefined, declareVariable, listForLazy, runtimeDataBase);

				//pass the profiling information
				for(int i = 0; i< runtimeDataBase.size();i++) 
				{
					String temp = "AddIntoProfilingMap(profilingInfo," + "\"" + runtimeDataBase.get(i) + "\"" + ") \n"
					program = program + temp;
				}
				
				
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
				
				program = program + exps + "\n"
				//-------------------------------insert API-------------------------------------
				//we cannot add API in processExpression because some expression are in if condition and binary expression
				//we need to add API here for statement. To add Api, we need information of both statement and expression
				//However, in here, we can get little information about the expression So, we have to make a new class to handle it
				
				program = program + ProcessExpressionAPI.executeStatement(stmt.expression, exps, "") + "\n"		
				
				//add API For lazy initialization
				//for(int i = 0; i < listForLazy.size(); i++) 
				//{
					//program = program + listForLazy.get(i) + "\n";
				//}
				//-------------------------------end-------------------------------------
						
			} else if (stmt instanceof IfStatement){
				println ("stmt: IfStatement")
				//add code to get the infotmation of profiling
				
				//修改这个到define后面并输入记录符号，在全部输出时计算行号
				//int l = stmt.lineNumber
				//program = program + "log.debug \"RelyInfo for if on " + l + ":  \${state.relyInfo}\"\n"
				//program = program + "state.varInPC = \"\" \n\n"
				
				program = ProcessIf.execute(stmt, program, file, programHash, listForInput, listEnv, mapForDefined)
				
				
				
			} else if (stmt instanceof ForStatement){
				println ("stmt: ForStatement")
				program = ProcessFor.execute (stmt, program, file, programHash, listForInput, listEnv, mapForDefined)
			}
			//*******************************************to be continue****************************************
			else if (stmt instanceof WhileStatement){
				println ("stmt: WhileStatement")
				//WhileStmt.execute (stmt, currentPath, pathesToProcess, stackedPath)
				
			} else if (stmt instanceof SwitchStatement){
				println ("stmt: SwitchStatement")
				//SwitchStmt.execute (stmt, currentPath, pathesToProcess, stackedPath)
				
			}
			else if(stmt instanceof ReturnStatement) 
			{
				program = program + stmt.getText();
				program = program + "\n"
			}
			else {
				println ("stmt: pay attention other statement!!! : " + stmt.toString())
			}
			
			println("finish this statement" + "\n")
			
			return program;
		}
}
