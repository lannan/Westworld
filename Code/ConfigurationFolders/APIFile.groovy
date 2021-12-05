//*************************************************Do NOT need to change APIs below when scan************************************************************
 
 def AddIntoMap(Map<String, List<Object>> programState, String string, String rely, String type)
 {
	 //add symbolic variable into state
	 List<Object> TypeValueList = new ArrayList<Object>();
	 //add label for symbolic or concrete
	 TypeValueList.add(true);
	 //add value
	 String symValue = "SYM" + string;
	 //find the qualified input sym variables
	 if(type == "number" || type == "enum" || type == "text" || type == "bool")
	 {
		symValue = symValue + "_Input";
	 }
	 else 
	 {
		 state.objEnv = state.objEnv  + "#" + string;
	 }
	 TypeValueList.add(symValue);
	 //add expression
	 TypeValueList.add(string);
	 //add rely
	 TypeValueList.add(rely);
	 //add rely
	 TypeValueList.add(type);
	 
	 //Add into Program State
	 programState.put(string, TypeValueList);
	 
	 String realType;
	 
	 if(type == "number") 
	 {
		 realType = "Integer"
	 }
	 else if(type == "enum") 
	 {
		 realType = "String"
	 }
	 else if(type == "bool")
	 {
		realType = "Boolean"
	 }
	 else if(type == "text") 
	 {
		 realType = "String"
	 }
	 
	 if(type == "number" || type == "enum" || type == "text"|| type == "bool") 
	 {
		 String strTemp = "(declare-const ${programState.get(string).getAt(1)} ${realType})";
		 if(!state.declare.contains(strTemp))
		 {
			 state.declare = state.declare + "@" + strTemp;
		 }
	 }
 }
 
 
 def AddPathCondition(String pathCondition, Map<String, List<Object>> programState, String CurrentCondition)
 {
 
	 if(ContainApi(programState, CurrentCondition))
	 {
		 String newCondition;
		 if(state.pathCondition == "" || state.pathCondition == null)
			{
			 newCondition = ReplaceValue(programState, CurrentCondition);
			 
			 state.pathCondition = newCondition;
			 String tempname = "temp_" + state.tag;
			 UpdateState(programState, tempname, CurrentCondition, newCondition, "not known")
			 
		 }
		 else
		 {
			 newCondition = ReplaceValue(programState, CurrentCondition);
			 state.pathCondition = state.pathCondition + "  @@@  " + newCondition;
			 String tempname = "temp_" + state.tag;
			 UpdateState(programState, tempname, CurrentCondition, newCondition, "not known")
			 
		 }
	 }
	 else
	 {
		 if(state.pathCondition == "" || state.pathCondition == null)
			{
			 state.pathCondition = ReplaceValue(programState, CurrentCondition);
		 }
		 else
		 {
			 state.pathCondition = state.pathCondition + "  @@@  " + ReplaceValue(programState, CurrentCondition);
		 }
	 }
	 
 }
 
 def AddSMTPathCondition(Map<String, List<Object>> programState, String CurrentCondition, String type)
 {
	 if(ContainApi(programState, CurrentCondition))
	 {
		 String newCondition;
		 if(state.pcSmt == "" || state.pcSmt == null)
			{
			 newCondition = ReplaceValueSMT(programState, CurrentCondition, type);
			 state.pcSmt = "@(assert " + newCondition + ")";
			 String tempname = "temp_" + state.tag;
			 UpdateState(programState, tempname, CurrentCondition, newCondition, "not known")
			 
		 }
		 else
		 {
			 newCondition = ReplaceValueSMT(programState, CurrentCondition, type);
			 state.pcSmt = state.pcSmt + "@(" + "assert " + newCondition + ")";
			 String tempname = "temp_" + state.tag;
			 UpdateState(programState, tempname, CurrentCondition, newCondition, "not known")
			 
		 }
	 }
	 else
	 {
		 if(state.pcSmt == "" || state.pcSmt == null)
			{
			 state.pcSmt =state.pcSmt + "@(assert " + ReplaceValueSMT(programState, CurrentCondition, type)+ ")";
		 }
		 else
		 {
			 state.pcSmt = state.pcSmt + "@(" + "assert " + ReplaceValueSMT(programState, CurrentCondition, type) + ")";
		 }
	 }
	 
 }
 
 
 //add update state
 def UpdateState(Map<String, List<Object>> programState, String leftVar, String rightExp, String Value, String type)
 {
 
	 //Update exist Variable
	 if(programState.containsKey(leftVar))
	 {
		 //Update value with rightExp
		 if(IsSymbolic(programState, rightExp))
		 {
			 //add symbolic variable into state
			 List<Object> TypeValueList = new ArrayList<Object>();
			 //add label for symbolic or concrete
			 TypeValueList.add(true);
			 //add value
			 //if value contains API
			 String stringReplaced = ReplaceValue(programState, rightExp);
			 TypeValueList.add(stringReplaced);
			 
			 //add expression
			 TypeValueList.add(rightExp);
			 //add rely
			 TypeValueList.add(FindRely(programState, leftVar, rightExp));
			 //add type
			 TypeValueList.add(type);
			 
			 //Add into Program State
			 programState.put(leftVar, TypeValueList)
		 }
		 //The new variable is concrete
		 else
		 {
			 //add concrete variable into state
			 List<Object> TypeValueList = new ArrayList<Object>();
			 //add label for symbolic or concrete
			 TypeValueList.add(false);
			 //add value(passed $value)
			 TypeValueList.add(Value);
			 //add expression
			 TypeValueList.add(rightExp);
			 //add rely
			 TypeValueList.add(FindRely(programState, leftVar, rightExp));
			 //add type
			 TypeValueList.add(type);
			 
			 //Add into Program State
			 programState.put(leftVar, TypeValueList)
		 }
		 
		 //the variable is env
		 if(rightExp.contains("EnvironmentGetValue"))
		 {
			 //add symbolic variable into state
			 List<Object> TypeValueList = new ArrayList<Object>();
			 //add label for symbolic or concrete
			 TypeValueList.add(true);
			 //add value
			 //if value contains API
			 String stringReplaced = "SYM" + leftVar + "pair_Input"
			 TypeValueList.add(stringReplaced);
			 //add expression
			 TypeValueList.add(rightExp);
			 //add rely
			 TypeValueList.add(FindRely(programState, leftVar, ""));
			 //add type
			 TypeValueList.add(type);
			 //Add into Program State
			 programState.put(leftVar, TypeValueList)
		 }
	 }
	 else
	 {
	 //The new variable is symbolic
		 if(IsSymbolic(programState, rightExp))
		 {
			 //add symbolic variable into state
			 List<Object> TypeValueList = new ArrayList<Object>();
			 //add label for symbolic or concrete
			 TypeValueList.add(true);
			 //add value
			 //if value contains API
			 String stringReplaced = ReplaceValue(programState, rightExp);
			 TypeValueList.add(stringReplaced);
			 
			 //add expression
			 TypeValueList.add(rightExp);
			 //add rely
			 TypeValueList.add(FindRely(programState, leftVar, rightExp));
			 //add type
			 TypeValueList.add(type);
			 
			 //Add into Program State
			 programState.put(leftVar, TypeValueList)
		 }
		 //The new variable is concrete
		 else
		 {
			 //add concrete variable into state
			 List<Object> TypeValueList = new ArrayList<Object>();
			 //add label for symbolic or concrete
			 TypeValueList.add(false);
			 //add value(passed $value)
			 TypeValueList.add(Value);
			 //add expression
			 TypeValueList.add(rightExp);
			 //add rely
			 TypeValueList.add(FindRely(programState, leftVar, rightExp));
			 //add type
			 TypeValueList.add(type);
			 
			 //Add into Program State
			 programState.put(leftVar, TypeValueList)
		 }
		 
		 //the new variable is environment
		 if(rightExp.contains("EnvironmentGetValue"))
		 {
			 //add symbolic variable into state
			 List<Object> TypeValueList = new ArrayList<Object>();
			 //add label for symbolic or concrete
			 TypeValueList.add(true);
			 //add value
			 //if value contains API
			 String stringReplaced = "SYM" + leftVar + "pair_Input"
			 TypeValueList.add(stringReplaced);
			 //add expression
			 TypeValueList.add(rightExp);
			 //add rely
			 TypeValueList.add(FindRely(programState, leftVar, ""));
			 //add type
			 TypeValueList.add(type);
			 //Add into Program State
			 programState.put(leftVar, TypeValueList)
		 }
	 }
	 state.tag = state.tag + 1;
 }
 
 //*************************************************Util************************************************************
 
 //check if the variable is symbolic
 boolean IsSymbolic(Map<String, List<Object>> programState, String rightExp)
 {
	 // a list contains tokens without OP
	 List<String> listWithoutOP = new ArrayList<String>();
	 
	 listWithoutOP = GetVariableList(false, rightExp);
	 
	 for(int i=0; i < listWithoutOP.size(); i++ )
	 {
		 //this is a symbolic Variable or Concrete Variable
		 if(programState.containsKey(listWithoutOP.get(i)))
		 {
			 boolean isSym = programState.get(listWithoutOP.get(i)).getAt(0);
			 if(isSym)
			 {
				 return true;
			 }
		 }
		 
	 }
	 return false;
 }
 
 //get the rely variable of the right epression
 String FindRely(Map<String, List<Object>> programState, String leftVar, String rightExp)
 {
	 // a list contains tokens without OP
	 List<String> listWithoutOP = new ArrayList<String>();
	 
	 listWithoutOP = GetVariableList(false, rightExp);
	 
	 String rely = "";
	 
	 for(int i=0; i < listWithoutOP.size(); i++ )
	 {
		 //this is a symbolic Variable or Concrete Variable
		 if(programState.containsKey(listWithoutOP.get(i)))
		 {
			 rely = rely + "@" + listWithoutOP.get(i);
		 }
		 
	 }
	 

	 state.relyInfo = state.relyInfo + "#" + leftVar +  rely
	 
	 return rely;
 }
 
 //replace the symbolic variables and concrete vaiables with their values
 String ReplaceValue(Map<String, List<Object>> programState, String orgString)
 {
		 
	 String newString = "";
	 // a list with all the tokens
	 List<String> listWithOP = new ArrayList<String>();
	 // a list contains tokens without OP
	 List<String> listWithoutOP = new ArrayList<String>();
	 
	 //fill them
	 listWithOP = GetVariableList(true, orgString);
	 listWithoutOP = GetVariableList(false, orgString);
	 //replace the symbolic or concrete variables
	 for(int i=0; i<listWithOP.size();i++)
	 {
		 String var = listWithOP.get(i);
		 if(programState.containsKey(var))
		 {
			 newString = newString  + programState.get(var).getAt(1);
		 }
		 else
		 {
			 newString = newString + listWithOP.get(i);
		 }
	 }
	 
	 //See if the string contains API
	 for(int i=0; i<listWithOP.size();i++)
	 {
		 String var = listWithOP.get(i);
		 //it contains API then the value shoud be SYM
		 if(!programState.containsKey(var) && !IsNumeric(var)&& !IsOp(var.charAt(0)))
		 {
			 if(i!=0 && i != listWithOP.size() - 1 )
			 {
				 if(listWithOP.get(i-1).charAt(0) != '\'' && listWithOP.get(i+1).charAt(0) != '\'')
				 {
					 String str2 = "API_SYM_" + state.tag;
			 
					 return str2;
				 }
			 }
			 else
			 {
				 String str2 = "API_SYM_" + state.tag;
			 
				 return str2;
			 }
			 
		 }
	 }
	 
	 //The string does not contain API
	 return newString;
	 
 }
 
 //correct the binary string to smt version
 String correctBinarytoSMT(ArrayList<String> listWithOP)
 {
	 int size = listWithOP.size();
	//log.debug listWithOP
	 if(listWithOP.contains("("))
	{
		String token = "";
		int start;
		int end;
		int i = 0;
		while(token != ")")
		{
			token = listWithOP.get(i);
			if(token == "(")
			{
			   start = i;
			}
			end = i;
			i = i+1;
	   }
	   int end2 = end + 2;
	   if(listWithOP.get(end+1)!= ")")
	   {
			end2 = end + 1;
	   }
	   ArrayList<String> tempList = new ArrayList<String>();
	   for(int j = start+1; j<end; j++)
	   {
			   tempList.add(listWithOP.get(j));
	   }
	   String afterCorrect = correctBinarytoSMT(tempList);
	   tempList.clear();
	   afterCorrect = "[" + afterCorrect + "]"
	   for(int k = 0; k < start; k++)
	   {
			   tempList.add(listWithOP.get(k))
	   }
	   
	   tempList.add(afterCorrect)
	   
	   for(int k = end2; k < listWithOP.size(); k++)
	   {
			   tempList.add(listWithOP.get(k))
	   }
	   
	   String finalstr = correctBinarytoSMT(tempList);
	   return finalstr;
	
	}
	else
	{
		if(size == 1)
		{
			return listWithOP.get(0);
		}
		else if(size == 2)
		{
			return listWithOP.get(1) + " " + listWithOP.get(0);
		}
		else if(size == 3)
		{
			return listWithOP.get(1) + " " + listWithOP.get(0) + " " + listWithOP.get(2);
		}
		else
		{
			String temp = "";
			for(int j = 0; j < listWithOP.size(); j++)
			{
				temp = temp + listWithOP.get(j);
			}
			return temp;
		}
	}
	
 }
 
  //replace the symbolic variables and concrete vaiables with their values AND ADD DECLARE
 String ReplaceValueSMT(Map<String, List<Object>> programState, String orgString, String type)
 {
		 
	 String newString = "";
	 // a list with all the tokens
	 ArrayList<String> listWithOP = new ArrayList<String>();
	 // a list contains tokens without OP
	 ArrayList<String> listWithoutOP = new ArrayList<String>();
	 
	 //fill them
	 listWithOP = GetVariableList(true, orgString);
	 listWithoutOP = GetVariableList(false, orgString);
	 //replace the symbolic or concrete variables
	 for(int i=0; i<listWithOP.size();i++)
	 {
		 String var = listWithOP.get(i);
		 if(programState.containsKey(var))
		 {
         	 List<String> listWithOP2 = GetVariableList(true, programState.get(var).getAt(1));
             //log.debug listWithOP2
             List<String> listWithOutOP2 = GetVariableList(false, programState.get(var).getAt(1));
             //log.debug listWithOutOP2
             
			 newString = newString  + correctBinarytoSMT(listWithOP2);
             newString = newString.replace("[","(");
             newString = newString.replace("]",")");
             //make declare
			 if(programState.get(var).getAt(0)==true)
             {
             	for(int j=0;j<listWithOutOP2.size();j++)
             	{
             		String tempVariableName = listWithOutOP2.get(j)
                    String tempStr = tempVariableName.replace("SYM", "");
                    tempStr = tempStr.replace("_Input", "");
                    tempStr = tempStr.replace("pair", "");
                    if(programState.containsKey(tempStr))
                    {
                    	String strTemp = "(declare-const ${tempVariableName} ${type})";
                    	if(!state.declare.contains(strTemp)) 
						{
							state.declare = state.declare + "@" + strTemp;
						}
                    	
                    }
             	}
             }
			 state.varInPC = state.varInPC + "#" + programState.get(var).getAt(1) + "@" + var
		 }
		 else
		 {
			 newString = newString + listWithOP.get(i);
		 }
	 }
	 
	 //The string does not contain API
	 return newString;
	 
 }
 
 
 //if wantOP is true return List with OP or return List without OP
 List<String> GetVariableList(Boolean wantOP, String orgString)
 {
			 
	 String newString = "";
	 
	 //List that contain OP
	 List<String> variableList = new ArrayList<String>();
	 //Without OP
	 List<String> listWithoutOP = new ArrayList<String>();
		 
	 String temp = "";
		 
	 for(int i=0; i<orgString.length();i++)
	 {
		 //	see if it is an OP
		 if(IsOp(orgString.charAt(i)))
		 {
			 //it is OP than we need to pop the value of temp
			 if(!temp.equals(""))
			 {
				 variableList.add(temp);
				 listWithoutOP.add(temp);
			 }
			 //add the OP into the list with OP
			 String str = String.valueOf(orgString.charAt(i));
			 variableList.add(str);
			 temp = "";
		 }
		 //It is not an OP, just add the char into the temp
		 else
		 {
			 String str = String.valueOf(orgString.charAt(i));
			 temp = temp + str;
			 temp = temp.replace(" ", "");
		 }
			 
			 
	 }
	 //Don't forget to add the last variable
	 variableList.add(temp);
	 listWithoutOP.add(temp);
	 
	 //see if we need OP
	 if(wantOP)
	 {
		 return variableList;
	 }
	 else
	 {
		 return listWithoutOP;
	 }
 }
 
 //Method to check if the string is a number
 boolean IsNumeric(String str)
 {
	 for(int i=0;i < str.length(); i++)
	 {
		 int chr=str.charAt(i);
		 if(chr<48 || chr>57)
		 {
			 return false;
		 }
	 }
	 return true;
 }
 
 //Method to check if a char is OP
 boolean IsOp(char ch)
 {
	 if(ch == '+'||ch == '-' || ch == '*' || ch == '/' || ch == '%' || ch == '=' || ch == '!' || ch == '>' || ch == '<' || ch == ' '|| ch == '('|| ch == ')'|| ch == '.' || ch == '{'|| ch == '}'|| ch == '|' || ch == '\''|| ch == '"'|| ch == ',')
	 {
		 return true
	 }
	 return false
 }
 
 
 //Method to check if a path condition contains API
 boolean ContainApi(Map<String, List<Object>> programState, String str)
 {
	 // a list contains tokens without OP
	 List<String> listWithOP = new ArrayList<String>();
	 
	 //fill them
	 listWithOP = GetVariableList(true, str);
	 
	 //See if the string contains API
	 for(int i=0; i<listWithOP.size();i++)
	 {
		 String var = listWithOP.get(i);
		 //it contains API then the value shoud be SYM
		 if(!programState.containsKey(var) && !IsNumeric(var) && !IsOp(var.charAt(0)))
		 {
			 if(i!=0)
			 {
				 if(listWithOP.get(i-1).charAt(0) != '\'' && listWithOP.get(i+1).charAt(0) != '\'')
				 {
					 return true;
				 }
			 }
			 else
			 {
				 return true;
			 }
			 
		 }
	 }
	 
	 return false;
	 
 }
 
 boolean ConditionIsSymbolic(Map<String, List<Object>> programState, String condition)
 {
	 // a list contains tokens without OP
	 List<String> listWithoutOP = new ArrayList<String>();
	 
	 //fill them
	 listWithoutOP = GetVariableList(false, condition);
	 
	 //See if the string contains Symbolic Variables
	 for(int i=0; i<listWithoutOP.size();i++)
	 {
		 String var = listWithoutOP.get(i);
		 if(programState.containsKey(var))
		 {
			 boolean isSym = programState.get(var).getAt(0);
			 if(isSym)
			 {
					return true;
			 }
		 }
	 }
	 return false;
 }
 
 
 
 String getType(def object)
 {
	 if (object instanceof Integer)
	 {
		 return "Integer"
	 }
	 else if (object instanceof String)
	 {
		 return "String"
	 }
	 else if (object instanceof Boolean)
	 {
		 return "Boolean"
	 }
     else if (object instanceof Double)
	 {
		 return "Double"
	 }
	 else
	 {
		 //most of the type will finally be compared as string
		 return "String"
	 }
 }
 
 def EnvironmentUpdate(Map<String, List<Object>> programState, Map<String, List<Object>> programEnvironment, String Name, String parameter, def value)
 {
	 if(IsSymbolic(programState, parameter))
	{
    	//add symbolic variable into state
	 	List<Object> tempList = new ArrayList<Object>();
        tempList.add(true);
        tempList.add(value);
		programEnvironment.put(Name, tempList)
	}
	else
	{
	 	List<Object> tempList = new ArrayList<Object>();
        tempList.add(false);
        tempList.add(value);
		programEnvironment.put(Name, tempList)
	}
 }
 
 def EnvironmentGetValue(Map<String, List<Object>> programState, Map<String, List<Object>> programEnvironment, String Name, String defineVariable, def value, def inputvalue)
 {
	if(programEnvironment.containsKey(Name))
	{
        List<Object> tempList = new ArrayList<Object>();
        
        tempList = programEnvironment.get(Name);
		def Value = tempList.get(1);
        
		//add concrete variable into state
		List<Object> TypeValueList = new ArrayList<Object>();
		//add label for symbolic or concrete
		TypeValueList.add(tempList.get(0));
		//add value(passed $value)
		TypeValueList.add(Value);
		//add expression
		TypeValueList.add(Name);
		//add rely
		TypeValueList.add(FindRely(programState, Name));
		//add type
		TypeValueList.add("not known");
		
		//Add into Program State
		programState.put(defineVariable, TypeValueList);
		
		return value;
	}
	else
	{
		
		//add concrete variable into state
		List<Object> TypeValueList = new ArrayList<Object>();
		//add label for symbolic or concrete
		TypeValueList.add(true);
		
		String symValue = "SYM" + Name + "pair_Input";
	 	TypeValueList.add(symValue);
		//add expression
		TypeValueList.add(Name);
		//add rely
		TypeValueList.add(FindRely(programState, defineVariable, Name));
		//add type
		TypeValueList.add("not known");
		
		//Add into Program State
		programState.put(defineVariable, TypeValueList)
		
		return inputvalue;
	}
 }
 
 
 def LazyInit(Map<String, List<Object>> programState, ArrayList<String> lazyList, String object, String parameter, String defineVariable, String flag)
 {
	 //if true the return is a SYM variable
	 if(IsSymbolic(programState, parameter) || IsSymbolic(programState, object) || lazyList.contains(object) )
	{
	
		AddIntoMap(programState, defineVariable, object, "UnKnown")
		
		//add symbolic variable into state
		List<Object> TypeValueList = new ArrayList<Object>();
		//add label for symbolic or concrete
		TypeValueList.add(true);
		//add value
		String symValue = "SYM" + defineVariable;
		
		TypeValueList.add(symValue);
		//add expression
		TypeValueList.add(defineVariable);
		//add rely
		TypeValueList.add(object);
		//add rely
		TypeValueList.add("UnKnown");
		
		//Add into Program State
		programState.put(defineVariable, TypeValueList);
		
		//the return value is an object
		if(flag == "true")
		{
			lazyList.add(defineVariable);
		}
		lazyList.add(object);
	}
	else
	{
		// do nothing
	}
 }
 
 String getConcreteVariable(Map<String, List<Object>> programState) 
 {
	 String out = "";
	 for (String key : programState.keySet()) {
		boolean isSym = programState.get(key).getAt(0);
		 if(!isSym)
		 {
			 out = out + "#" + key;
		 }
	}
	
	out = out + state.objEnv;
	return out;  
 }
 
def processLogString(String str) 
{
 	//state.logString = state.logString + "\n"+ "SPLIT" + str;
	state.logString = state.logString + "\n" + str;
}
 
def outputLog()
{
	
	if(state.logString == "") 
	{
		log.debug "NOLOG"
	}
//	String[] temp = state.logString.split("SPLIT");
//    state.logString = "";
//    int flag = 0;
//	for(int i =0; i<temp.size(); i++)
//    {
//    	String str = temp[i];
//    	state.logString = state.logString + str;
//        flag = flag + 1;
//        
//        if(flag == 11)
//        {
//			state.logString = state.logString + "DEBUGEND";
//        	log.debug "${state.logString}"
//            state.logString = "";
//            flag = 0;
//        }
//        if(i == temp.size()-1)
//        {
//			state.logString = state.logString + "DEBUGEND";
        	log.debug "${state.logString}"
//        }
//    }
}
 
 
 def getTypeAndReturnValue(def object, Map<String, String> pairMap, String key)
 {
	  String value = "";
	  if(pairMap.containsKey(key))
	 {
		 value = pairMap.get(key);
		 if (object instanceof Integer)
		 {
			 return Integer.parseInt(value);
		 }
		 else if (object instanceof String)
		 {
			 return value;
		 }
		 else if (object instanceof Boolean)
		 {
			 if(value == "true")
			{
				return "true"
			}
			return false;
		 }
		 // the object does not have an input so it is null. The only way to do is to return the value
		 else if(object == null) 
		 {
			 return value;
		 }
		 else 
		 {
			 //some strange type return string is the best way
			 return value;
		 }
	 }
	 else
	 {
		  return object;
	 }
 }
