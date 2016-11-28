package struct;

public class NewExpr {
	
	public Expression expr = new Expression();
	public Variables vset = new Variables();
	public String startexpr(String input) throws Exception
	{
		expr.setExpr(input);
		vset = expr.vset;
		return expr.getExpression();
	}
}
