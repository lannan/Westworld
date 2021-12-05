package PE

class PCPackage {
	
	//(declare-const API_SYM_1 Int)(declare-const SYMhumidity1 Int)(declare-const API_SYM_8 Bool)(declare-const SYMhumidity2 Int)
	String declare;
	String assertStr;
	
	//one path condition should has an assert
	ArrayList<String> pathConditions = new ArrayList<String>();
	ArrayList<String> declareStatements = new ArrayList<String>();
	
	String pathBound;
	
	ArrayList<String> profilingInfo;
	ArrayList<String> ifCount;
	
	public PCPackage(String input, ArrayList<String> profilingInfo, ArrayList<String> ifCount)
	{
		this.profilingInfo = profilingInfo;
		this.ifCount = ifCount;
		
		//get the log and split it into declare, asserts and pathbound
		String[] temp = input.split("#");
		this.declare = temp[0];
		this.assertStr = temp[1];
		this.pathBound = temp[2];
		String[] tempAssert = assertStr.split("@");
		String[] tempDeclare = declare.split("@");
		
		
		for(int i = 1; i< tempAssert.size(); i++)
		{
			pathConditions.add(tempAssert[i]);
		}
		for(int i = 1; i< tempDeclare.size(); i++)
		{
			declareStatements.add(tempDeclare[i]);
		}
		
	}
	
	
	
	public ArrayList<String> getDeclare()
	{
		return declareStatements;
	}
	
	
	public ArrayList<String> getPC()
	{
		return pathConditions;
	}
	
	public String getPathBound()
	{
		return pathBound;
	}
	
	public ArrayList<String> getProfilingInfo()
	{
		return profilingInfo;
	}
	
	public ArrayList<String> getIfCount() 
	{
		return ifCount;
	}

}
