package FrontEnd

import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.control.SourceUnit

class InsnVisitor extends ClassCodeVisitorSupport {
	//one set to store all the handler methods in the code
	Set<String> allEntryMethods
	Map<String,String> varNames//stores the first element of input method
	
	//array list to store input environment
	ArrayList<String> listEnv = new ArrayList<String>();
	
	HashMap<String, String> titleMap = new HashMap<String, String>();
	
	public InsnVisitor()
	{
		//consts = new HashSet<String>()
		allEntryMethods = new HashSet<String>()
		varNames = new HashMap<String,String>()
	}
	
	@Override
	void visitMethodCallExpression(MethodCallExpression mce)
	{
		if(mce.getMethodAsString().equals("section")) 
		{
			def args = mce.getArguments();
			String sectionName = args[0].getText();
			String varName = "";
			String subtitle = "";
			
			BlockStatement blockStatement = args[1].code;
			def statements = blockStatement.getStatements();
			
			for(int i = 0; i < statements.size(); i++) 
			{
				MethodCallExpression mc = statements.get(i).expression;
				def argums = mc.getArguments();
				if(mc.getMethodAsString().equals("input")) 
				{
					int flag = 0;
					
					for(int j = 0; j < argums.size(); j++) 
					{
						Expression exp = argums[j];
						
						if(exp instanceof MapExpression) 
						{
							exp.mapEntryExpressions.each{ entry->
								if(entry.keyExpression.value.toString().equals("title"))
								{
									subtitle = entry.valueExpression.getText();
								}	
							}
						}
						else if(exp instanceof ConstantExpression) 
						{
							if(flag == 0) 
							{
								varName = exp.getText();
							}
							flag = 1; 
						}
					}
					String title;
					if(subtitle != "") 
					{
						title = sectionName + "#" + subtitle;
					}
					else 
					{
						title = sectionName;
					}
					
					
					titleMap.put(varName,title);
					
				}
				else 
				{
					continue;
				}
				
			}
			
			
		}
		if(mce.getMethodAsString().equals("input"))
		{
			def args = mce.getArguments()
			List cexp = new ArrayList()
			
			args.each { arg ->
				if(arg instanceof ConstantExpression)
				{
					cexp.add((ConstantExpression) arg)
					listEnv.add(arg.getText())
				}
				else if(arg instanceof GStringExpression)
				{
					cexp.add(arg)
				}
				else if(arg instanceof NamedArgumentListExpression)
				{
					arg.mapEntryExpressions.each{ entry->
						if(entry.keyExpression.value.toString().equals("name"))
						{
							cexp.add(entry.valueExpression)
						}
						else if(entry.keyExpression.value.toString().equals("type"))
						{
							cexp.add(entry.valueExpression)
						}
						
					}
				}
				else if(arg instanceof MapExpression)
				{
					arg.mapEntryExpressions.each{ entry->
						if(entry.keyExpression.value.toString().equals("name"))
						{
							cexp.add(entry.valueExpression)
						}
						else if(entry.keyExpression.value.toString().equals("type"))
						{
							cexp.add(entry.valueExpression)
						}
						
					}
				}
			}
			
			//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			varNames.put(cexp[0].getText(), cexp[1].getText());			
		}
		if(mce.getMethodAsString().equals("subscribe")||mce.getMethodAsString().equals("subscribeToCommand"))
		{
			if(mce.getArguments().size()>0) 
			{
				def args = mce.getArguments()
				List vexp = new ArrayList()
				def counter=0
				args.each { arg ->
					if(arg instanceof VariableExpression)
					{
						vexp.add((VariableExpression) arg)
					}else if(arg instanceof ConstantExpression)
					{
						counter++
						if(counter==2)
						{
							vexp.add((ConstantExpression) arg)
						}
					}
				}
				allEntryMethods.add(vexp[1].getText());
				if(!varNames.containsKey(vexp[0].getText()))
				{
					varNames.put(vexp[0].getText(), "subscribe");
				}
			}
		}
		if(mce.getMethodAsString().equals("schedule"))
		{
			def args = mce.getArguments()
			args.each { arg ->
				if((arg instanceof VariableExpression)&&(!varNames.containsKey(arg.getText())))
				{
					varNames.put(arg.getText(), "schedule")
				}
				else if(arg instanceof ConstantExpression)
				{
					allEntryMethods.add(arg.getText());
				}
			}	
		}
		if(mce.getMethodAsString().equals("runDaily")||mce.getMethodAsString().equals("runIn"))
			{
				def args = mce.getArguments()
				List vexp = new ArrayList()
				def counter=0
				args.each { arg ->
							if(arg instanceof VariableExpression)
							{
								counter++
								if(counter==2)
								{
									allEntryMethods.add(arg.getText());
								}
							}
				}				
			}
		super.visitMethodCallExpression(mce)
	}
	
	@Override
	void visitPropertyExpression(PropertyExpression pe)
	{
		//calledProps.add(pe.getPropertyAsString())
		
		super.visitPropertyExpression(pe)
	}
	@Override
	protected SourceUnit getSourceUnit() {
		return null;
	}
	
	public ArrayList<String> getEnvList()
	{
		return listEnv;
	}
	
	public HashMap<String, String> getTitleMap()
	{
		return titleMap;
	}

}

