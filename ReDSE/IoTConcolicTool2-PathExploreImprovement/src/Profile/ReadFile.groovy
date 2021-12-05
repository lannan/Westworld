package Profile

class ReadFile {
	
	
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
