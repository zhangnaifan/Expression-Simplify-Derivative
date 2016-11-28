package struct;

import java.util.HashMap;
import java.util.Map;

public class partExpression {
	private HashMap<String,Integer> pexpr;
	public String getPExpr()//将项转化为String
	{
		String str = "";
		for (Map.Entry<String, Integer> factor : pexpr.entrySet()){
			if (factor.getKey().length()>0) // print factors
			{
				str += "*"+factor.getKey();
				int expo = factor.getValue();
				if (expo>1+1e-6 || expo<1-1e-6)
					str +="^"+expo;
			}
		}
		return str;
	}
	public boolean setPExpr(String input)
	{
		return false;
	}
	public boolean pSimplify()
	{
		return false;
	}
	public String pDerivative()
	{
		return "";
	}


}
