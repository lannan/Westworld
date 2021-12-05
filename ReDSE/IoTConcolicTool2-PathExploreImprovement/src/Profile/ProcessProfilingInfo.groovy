package Profile


class ProcessProfilingInfo {
	
	HashMap<String, ArrayList<String>> profileInfoMap = new HashMap<Integer, ArrayList<String>>();
	
	ArrayList<String> processedProfileInfo = new ArrayList<String>();
	ArrayList<String> ifcountList = new ArrayList<String>();
	
	
	public ProcessProfilingInfo(ArrayList<String> profileInfo) 
	{
		processAndList(profileInfo);		
	}
	
	public ArrayList<String> getSMTProfilingInfo() 
	{
		
		for(String key : profileInfoMap.keySet()) 
		{
			ArrayList<String> andList = profileInfoMap.get(key);
			
			String output;
			output = "(or " + andList.get(0) + " " + andList.get(1) +")";
			for(int i = 2; i < andList.size(); i++)
			{
				output = "(or " + output + " " + andList.get(i) + ")";
			}
			output = "(assert " + output + ")\r\n"
			
			processedProfileInfo.add(output)
		}
		
		return processedProfileInfo;
		
	}
	
	public ArrayList<String> getIfCount() 
	{
		
		for(String key : profileInfoMap.keySet())
		{
			
			ifcountList.add(key)
		}
		
		return ifcountList;
	}
	
	private void processAndList(ArrayList<String> profileInfo) 
	{
		
		for(int i = 0; i<profileInfo.size();i++) 
		{
			ArrayList<String> andList = new ArrayList<String>();
			
			String orgStr = profileInfo.get(i);
			
			String[] temp1 = orgStr.split("Profiling Info for ");
			
			String ifcount = temp1[0];
			
			String pairOfValues = temp1[1];
			
			String[] temp2 = pairOfValues.split(" and ");
			
			String name1 = temp2[0];
			
			String finalAndString = "";
			
			for(int j = 1; j < temp2.size(); j++) 
			{
				String name2AndValues = temp2[j];
				
				String[] temp3 = name2AndValues.split(": ");
				String name2 = temp3[0];
				
				String values = temp3[1];
				
				String[] temp4 = values.split("@");
				
				String value1 = temp4[0];
				String value2 = temp4[1];
				
				String and = "(and (= " + name1 + " " + value1 + ") (= " + name2 + " " + value2 + "))";
				
				if(finalAndString != "") 
				{
					finalAndString = "(and " + finalAndString + " " + and + ")"
				}
				else
				{
					finalAndString = and;
				}
				
			}
			
			andList.add(finalAndString);
			
			
			if(profileInfoMap.containsKey(ifcount)) 
			{
				ArrayList<String> tempAndList = profileInfoMap.get(ifcount);
				tempAndList.addAll(andList)
				profileInfoMap.put(ifcount, tempAndList);
			}
			else 
			{
				profileInfoMap.put(ifcount, andList);
			}
//			String[] temp1 = orgStr.split(": ");
//			String value = temp1[1];
//			String name = temp1[0];
//			
//			temp1 = value.split("@");
//			String value1 = temp1[0];
//			String value2 = temp1[1];
//			
//			temp1 = name.split(" and ");
//			String name2 = temp1[1];
//			
//			String[] temp2 = temp1[0].split("Profiling Info for "); 
//			
//			this.ifcount = temp2[0];
//			
//			String name1 = temp2[1];
//			
//			String and = "(and (= " + name1 + " " + value1 + ") (= " + name2 + " " + value2 + "))";
//			
//			andList.add(and);
			
		}
	}
	
	
}
