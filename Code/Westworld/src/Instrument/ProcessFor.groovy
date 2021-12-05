package Instrument
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.stmt.BreakStatement
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codehaus.groovy.ast.stmt.Statement

class ProcessFor {

	def static execute(def forStmt, String program, def file, def programHash, ArrayList<ArrayList<String>> listForInput, ArrayList<String> listEnv, HashMap<String, String> mapForDefined) {
		
		//String for declartion
		String declareVariable = null;
		
		ArrayList<String> listForDef = new ArrayList();
		
		// a list to store lazy initialization
		ArrayList<String> listForLazy = new ArrayList();
		
		//a list to store the profiling information
		ArrayList<String> runtimeDataBase = new ArrayList();
		
		
		//final output for string
		String finalForBlock = "";
		
		//get some for information
		def loopstmts=forStmt.loopBlock.statements
		def loopCondition;
		int lineNumber = forStmt.lineNumber;
		String collection = forStmt.getCollectionExpression().getText();
		String parameter = forStmt.getVariable().getName();
		
		println ("parameter : $parameter collection : $collection")
		
		//get for condition
		String condition = ReadFile.ReadLine(file, lineNumber);
		
		String[] temp = condition.split("\\(|\\)")
		
		condition = temp[1]
		
		//if the for condition contains symbolic variable
		finalForBlock = finalForBlock + "if(ConditionIsSymbolic(programState, \"$condition\")){" + "\n";
		
		finalForBlock = finalForBlock + "for(" + condition + "){" + "\n"
		
		loopstmts.each {stmt ->
			
			//every redefine statement will be add into programstate as api, and the parameter is the symbolic variables in condition
		}
		
		finalForBlock = finalForBlock + "\n}\n"
		
		finalForBlock = finalForBlock + "\n" + "}\n"
		
		//if the for condition does not contain symbolic variable
		finalForBlock = finalForBlock + "else\n{\n"
		
		finalForBlock = finalForBlock + "forCountI = 1;\n"
		
		finalForBlock = finalForBlock + "for(" + condition + "){" + "\n"
		
		loopstmts.each {stmt ->
			
			finalForBlock = ProcessStatement.execute(stmt, finalForBlock, file, programHash, listEnv, listForInput, mapForDefined)
		}
		finalForBlock = finalForBlock.replace("//INSERT_FOR_API_END","");
		
		finalForBlock = finalForBlock + "\nforCountI = forCountI + 1\n";
		

		finalForBlock = finalForBlock + "\n}\n";
		
		
		finalForBlock = finalForBlock + "\n}\n";
		
		finalForBlock = finalForBlock + "\n" + "forCountI = 0;\n"

		finalForBlock = finalForBlock + "//INSERT_FOR_LOOP_END" + "\n"
		
		finalForBlock = "//FOR_LOOP_START" + "\n" + finalForBlock
		
		
		program = program + finalForBlock;
		
		return program;
	}
}
