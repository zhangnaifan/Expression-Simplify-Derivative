package struct;


public class Simplify {
	
	public Expression expr = null;
	public Variables vset = null;
	
	public String startsimp(String input)
	{
		String output = "";
		boolean valid = check(input);
		if(!valid)
		{
			output = "Wrong command!";
		}
		else
		{
			input = input.replace("!simplify ", "");
			String[] kvs = input.split(" ");
			for(int i = 0; i<kvs.length; i++)
			{
				String[] t = kvs[i].split("=");
				expr.simplify(t[0],Double.parseDouble(t[1]));
				vset.remove(t[0]);
			}
			output = expr.getExpression();
		}
		
		return output;
	}
	private boolean check(String input)
	{
		input = input.replace("!simplify ", "");
		String[] in = input.split(" ");
		for(int i=0;i<in.length;i++)
		{
			if(in[i].equals(""))
			{
				return false;
			}
			String[] temp = in[i].split("=");
			if(temp.length!=2)
			{
				return false;
			}
			else
			{
				if(vset.check(temp[0])==false || isDouble(temp[1]) == false )
					return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings("finally")
	private boolean isDouble(String str)
	{
		boolean isdouble = true;
		try{
			double num = Double.parseDouble(str);
		}
		catch(NumberFormatException num){
			isdouble = false;
		}
		finally{
			return isdouble;
		}
	}
	
}
