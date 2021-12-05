package Instrument

class ReadFile {
	
	//Read the string on linenumber
	def static ReadLine(def file, def lineNumber) 
	{
		FileReader fileReader = new FileReader(file);
		LineNumberReader reader = new LineNumberReader(fileReader);
		int lines = 0;
		String text = "";
		
		while (text != null) {
			lines++;
			text = reader.readLine();
			if (lines == lineNumber) {
				break;
			}
		}
		reader.close();
		fileReader.close();
		return text;
	}
	
	//read all the lines before line number
	def static ReadBeforeLine(def file, def lineNumber) 
	{
		FileReader fileReader = new FileReader(file);
		LineNumberReader reader = new LineNumberReader(fileReader);
		int lines = 0;
		String text = "";
		String program = "";
		
		while (lines != lineNumber) {
			lines++;
			if (lines == lineNumber) {
				break;
			}
			//text = reader.readLine();
			program = program + reader.readLine() + "\n";
			
		}
		reader.close();
		fileReader.close();
		return program;
	}
	
	//read all file
	def static ReadAllFile(def file)
	{
		FileReader fileReader = new FileReader(file);
		LineNumberReader reader = new LineNumberReader(fileReader);

		String text = "";
		String program = "";
		
		while (text != null) {

			text = reader.readLine();
			if(text != null) 
			{
				program = program + text + "\n";
			}	
		}
		reader.close();
		fileReader.close();
		return program;
	}
	
	
	def static StoreWholeFileInHash(def file, HashMap<Integer, String> ht) 
	{
		FileReader fileReader = new FileReader(file);
		LineNumberReader reader = new LineNumberReader(fileReader);
		int line = 1;
		String text = "";
		String program = "";
		
		while (text != null) {

			text = reader.readLine();
			ht.put(line, text)
			line = line + 1;
		}
		line = line -1;
		ht.remove(line);
		reader.close();
		fileReader.close();
	}
	
}
