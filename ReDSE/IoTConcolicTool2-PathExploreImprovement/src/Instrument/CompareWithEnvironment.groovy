package Instrument

class CompareWithEnvironment {
	String object;
	
	String method;
	
	String type;
	
	String returned;
	
	//HasReturn means the method or object method has a return value
	String Valuefolder = "E:\\ConcolicExecuter\\Environment\\HasReturn\\BasicValue\\"
	
	String Objectfolder = "E:\\ConcolicExecuter\\Environment\\HasReturn\\Object\\"
	
	String NoReturnfolder = "E:\\ConcolicExecuter\\Environment\\NoReturn\\"
	
	//two file paths for PropertyExpression
	String DirectAccessValueFile = "E:\\ConcolicExecuter\\Environment\\DirectAccess\\Primitive.txt"
	
	String DirectAcccessObjectFile = "E:\\ConcolicExecuter\\Environment\\DirectAccess\\Object.txt"
	

	
	boolean isEnv = false;
	
	HashMap<String, String> ReturnBasicValueHash = new HashMap<String, String>();
	
	HashMap<String, String> ReturnObjectHash = new HashMap<String, String>();
	
	ArrayList<String> noReturnMethod = new ArrayList<String>();
	
	ArrayList<String> listEnv = new ArrayList<String>();
	
	
	//for property expression
	HashMap<String, String> DirectAccessValueHash = new HashMap<String, String>();
	HashMap<String, String> DirectAcccessObjectHash = new HashMap<String, String>();
	
	public CompareWithEnvironment(String object, String method, ArrayList<String> listEnv) 
	{
		this.object = object;
		
		this.method = method;
		
		this.listEnv = listEnv;
		
		
		new File(Valuefolder).eachFile { file ->
			
			String VaraiableName = file.getName();
			
			FileReader fileReader = new FileReader(file);
			LineNumberReader reader = new LineNumberReader(fileReader);

			String text = "";
		
			while (text != null) {
				
				text = reader.readLine();
				
				if(text != null)
				{
					String[] temp = text.split(" ");
				
					ReturnBasicValueHash.put(temp[0], temp[1])
				}
				
			}			
		}
		
		new File(Objectfolder).eachFile { file ->
			
			String VaraiableName = file.getName();
			
			FileReader fileReader = new FileReader(file);
			LineNumberReader reader = new LineNumberReader(fileReader);

			String text = "";
		
			while (text != null) {
				
				text = reader.readLine();
				
				if(text != null)
				{
					String[] temp = text.split(" ");
				
					ReturnObjectHash.put(temp[0], temp[1])
				}
				
			}
		}
		
		new File(NoReturnfolder).eachFile { file ->
			
			FileReader fileReader = new FileReader(file);
			LineNumberReader reader = new LineNumberReader(fileReader);

			String text = "";
		
			while (text != null) {
				
				text = reader.readLine();
				
				if(text != null)
				{
					noReturnMethod.add(text);
				}
				
			}
		}
		
		
		FileReader fileReader = new FileReader(DirectAccessValueFile);
		LineNumberReader reader = new LineNumberReader(fileReader);

		String text = "";
	
		while (text != null) {
			
			text = reader.readLine();
			
			if(text != null)
			{
				String[] temp = text.split(" ");
			
				DirectAccessValueHash.put(temp[0], temp[1])
			}
			
		}
		
		FileReader fileReader2 = new FileReader(DirectAcccessObjectFile);
		LineNumberReader reader2 = new LineNumberReader(fileReader2);

		String text2 = "";
	
		while (text2 != null) {
			
			text2 = reader2.readLine();
			
			if(text2 != null)
			{
				String[] temp = text2.split(" ");
			
				DirectAcccessObjectHash.put(temp[0], temp[1])
			}
			
		}
	}
	
	public boolean ReturnIsEnvironmenObject() 
	{
		if(listEnv.contains(object)) 
		{
			if(ReturnObjectHash.containsKey(method)) 
			{
				this.type = ReturnObjectHash.get(method);
				this.returned = this.type;
				return true;
			}
		}
		return false;
	}
	
	public boolean ReturnIsDirectAccessObject()
	{
		if(listEnv.contains(object))
		{
			if(DirectAcccessObjectHash.containsKey(method))
			{
				this.type = DirectAcccessObjectHash.get(method);
				this.returned = this.type;
				return true;
			}
		}
		return false;
	}
	
	
	
	public boolean ReturnIsEnvironmentBasicValue() 
	{
		if(listEnv.contains(object)) 
		{
			if(ReturnBasicValueHash.containsKey(method)) 
			{
				isEnv = true;
				this.type = ReturnBasicValueHash.get(method);
				this.returned = this.type;
			}
		}
		return isEnv;
	}
	
	public boolean ReturnIsDirectAccessBasicValue()
	{
		if(listEnv.contains(object))
		{
			if(DirectAccessValueHash.containsKey(method))
			{
				isEnv = true;
				this.type = DirectAccessValueHash.get(method);
				this.returned = this.type;
			}
		}
		return isEnv;
	}
	
	public String getType() 
	{
		if(this.type == "Unknown") 
		{
			this.type = "String";
		}
		if(this.type == "Number")
		{
			this.type = "number";
		}
		if(this.type == "String")
		{
			this.type = "text";
		}
		return type;
	}
	
	public boolean IsNoReturnMethod()
	{
		if(listEnv.contains(object))
		{
			if(noReturnMethod.contains(method))
			{
				return true;
			}
		}
		return false;
	}

}
