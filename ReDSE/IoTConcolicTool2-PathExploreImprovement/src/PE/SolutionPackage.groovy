package PE

class SolutionPackage {
	
	String solution;
	
	int pathBound;
	
	public SolutionPackage(String solution, int pathBound)
	{
		this.solution = solution;
		this.pathBound = pathBound;
	}
	
	public String getSolution()
	{
		return solution;
	}
	
	public int getPathBound()
	{
		return pathBound;
	}

}
