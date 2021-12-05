package Z3;

import com.microsoft.z3.*;


public class STPSolver {
	
	String filepath;
	
	public STPSolver(String filepath)
	{
		this.filepath = filepath;
	}
	
	class TestFailedException extends Exception
	{
		public TestFailedException()
		{
			super("Check FAILED");
		}
	};

	public String getSolution() throws TestFailedException
	{
		//System.out.println("GetSolution");
		
		String solution;

		Context ctx = new Context();

		solution = smt2File(filepath);

		ctx.close();

		
		return solution;
	}

	Model check(Context ctx, BoolExpr f, Status sat) throws TestFailedException
	{
		Solver s = ctx.mkSolver();
		s.add(f);
		if (s.check() != sat) 
		{
			//System.out.println("No Solution");
			return null;
		}
		if (sat == Status.SATISFIABLE)
			return s.getModel();
		else
			return null;
	}
	
	String smt2File(String filename) throws TestFailedException
	{
		//Date before = new Date();

	   // System.out.println("SMT2 File test ");
		//System.gc();
  
		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		Context ctx = new Context(cfg);
		BoolExpr a = ctx.mkAnd(ctx.parseSMTLIB2File(filename, null, null, null, null));
			
		Model m = check(ctx, a, Status.SATISFIABLE);
		
		if(m == null) 
		{
			return "No Solution";
		}
			
		String str = m.toString();
			
			
		//System.out.println(str);
		//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!TO BE CONTINUED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		//this solution is not for variables but for symbolic tokens
		return str;
			
			
/*            BoolExpr[] f = ctx.parseSMTLIB2File(filename, null, null, null, null);
			System.out.println(f);

			long t_diff = ((new Date()).getTime() - before.getTime()) / 1000;

			System.out.println("SMT2 file read time: " + t_diff + " sec");

			// Iterate over the formula.

			LinkedList<Expr> q = new LinkedList<Expr>();
			q.add(a);
			int cnt = 0;
			while (q.size() > 0)
			{
				AST cur = (AST) q.removeFirst();
				cnt++;

				if (cur.getClass() == Expr.class)
					if (!(cur.isVar()))
						for (Expr c : ((Expr) cur).getArgs())
							q.add(c);
			}
			System.out.println(cnt + " ASTs");*/
	}

		//long t_diff = ((new Date()).getTime() - before.getTime()) / 1000;
		//System.out.println("SMT2 file test took " + t_diff + " sec");
	
}
	
