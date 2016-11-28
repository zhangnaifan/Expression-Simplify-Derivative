package struct;

public class Derivative {
	
	public Expression expr = null;
	public Variables vset = null;
	public String startderi(String input)
	{
		input = input.replace(" ", "").replace("!d/d", "");//forgot this at first, so...
		boolean ivalid = check(input);
		boolean evalid = expr.getExist();
		String output;
		if(evalid == false)
		{
			output = "invalid input";
		}
		else if(ivalid == false)
		{
			output = "Please input an expression!"; 
		}
		else
		{
			String var = input.replace("!d/d", "");
			output = expr.derivative(var);	
		}
		return output;		
	}
	
	private boolean check(String input)
	{
		String temp = input.replace("!d/d", "");
		if(temp.length()==0 || vset.check(temp)==false)
		{
			return false;
		}
		return true;
	}
}
