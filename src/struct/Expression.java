package struct;

import java.text.DecimalFormat;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Expression {
	private ConcurrentHashMap<HashMap<String, Integer>,Double> expr;
	private  boolean exist = false;
	public Variables vset = new Variables();
	
	public static void main(String[] args) throws Exception {
		Expression expr = new Expression();
		Variables vset = new Variables();
		expr.setExpr("1+-2x+3y^2");
		System.out.println(expr.getExpression());
		for (String i : vset.get()) {
			System.out.println(i);
		}
		expr.simplify("x", 2);
		System.out.println(expr.getExpression());
		expr.derivative("y");
		System.out.println(expr.getExpression());
	}
	
	public boolean simplify(String name, double base)
	{	
		boolean found = false;
		Iterator<Map.Entry<HashMap<String, Integer>, Double>> it = expr.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<HashMap<String, Integer>, Double> item = it.next();
			HashMap<String, Integer> factors = item.getKey();
			double coeff = item.getValue();
			if (factors.containsKey(name)){
				//把 name^expo 乘到系数上
				//删除name
				//如果去掉变量之后item为常数项，若Expr无常数项，则补上常数项；否则将系数加到常数项上
				found = true;
				double add_this = coeff* Math.pow(base, factors.get(name));
				if (isSingleVariableItem(item)){
					item.setValue(add_this);
					it.remove();
					updateConstantItem(add_this);
				} else {
					HashMap<String, Integer> temp = new HashMap<>(factors);
					temp.remove(name);
					expr.put(temp, add_this);
					it.remove();
				}
				//System.out.println(getExpression());
			}
		}
		return found;
	}
	
	private void updateConstantItem(double add_this) {
		HashMap<String, Integer> CONSTANT = new HashMap<>();
		CONSTANT.put("", 0);
		expr.replace(CONSTANT, expr.get(CONSTANT)+add_this);
	}
	
	private boolean isSingleVariableItem(Map.Entry<HashMap<String,Integer>, Double> item) {
		return item.getKey().size()==2 && item.getKey().containsKey("");
	}
	
	public String derivative(String input)
	{
		HashMap<String, Integer> CONSTANT = new HashMap<>();
		CONSTANT.put("", 0);
		expr.replace(CONSTANT, 0.0d);
		Iterator<Map.Entry<HashMap<String, Integer>, Double>> it = expr.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry<HashMap<String, Integer>, Double> item = it.next();
			HashMap<String, Integer> factors = item.getKey();
			double coeff = item.getValue();
			if (factors.containsKey(input)){
				int expo = factors.get(input);
				//deal with 3x
				if (isSingleVariableItem(item) && expo<1+1e-6 && expo>1-1e-6){
					it.remove();
					updateConstantItem(coeff * expo);
				}
				//deal with 3x*y
				else if (expo<1+1e-6 && expo>1-1e-6){
					HashMap<String, Integer> temp = new HashMap<>(factors);
					temp.remove(input);
					expr.put(temp, coeff*expo);
					it.remove();
				}
				//deal with 3x^2
				else{
					HashMap<String, Integer> temp = new HashMap<>(factors);
					temp.replace(input, expo-1);
					expr.put(temp, coeff*expo);
					it.remove();
				}
			} else if (factors.size() > 1) {
				it.remove();
			}
		}
		return getExpression();
	}
	
	
	public Expression(ConcurrentHashMap<HashMap<String, Integer>,Double> expr) {
		this.expr = expr;
	}
	
	private ConcurrentHashMap<HashMap<String, Integer>,Double> get() {
		return expr;
	}
	
	public Expression() {}
	
	public String getExpression()
	{
		DecimalFormat df = new DecimalFormat("#.0");
		String str = "";
		//print the expression
		for (Map.Entry<HashMap<String, Integer>, Double> item : expr.entrySet()){
			Double coeff = item.getValue();
			if ((coeff<1e-6 && coeff>-1e-6))//0-constant item will not be shown
				continue;	
			str += df.format(coeff);
			for (Map.Entry<String, Integer> factor : item.getKey().entrySet()){
				if (factor.getKey().length()>0) // print factors
				{
					str += "*"+factor.getKey();
					int expo = factor.getValue();
					if (expo>1+1e-6 || expo<1-1e-6)
						str +="^"+expo;
				}
			}
			str += "+";
		}
		if (str.isEmpty())
			str = "0.0";
		else
			str = str.substring(0,str.length()-1);
		str = str.replace("+-", "-");
		return str;
	}
	
	public boolean setExpr(String input) throws Exception
	{
		HashMap<String,Integer> CONSTANT = new HashMap<String, Integer>();
		CONSTANT.put("", 0);
		
		Stack<Expression> num = new Stack<>();
		Stack<Character> op=new Stack<>();
		char pre = 'E';
		for (int i=0,j=0,len=input.length(); i<len;){
			
			for (; j<len && !isSeperator(input.charAt(j)); ++j);//j points to the next +-*()
			
			if (i==j && isSeperator(input.charAt(i)) || isSeperator(input.substring(i,j))!=-1){
				char ch;
				if (j==i){
					ch = input.charAt(i);
					++i;
					++j;
				}
				else{
					ch = input.charAt(i+isSeperator(input.substring(i,j)));
					i=j;
				}
				switch (ch) {
				
					case ')':
						if (pre!='N' && pre!=')'){
							throw new Exception();
						}
						pre = ')';
							
						for (char opr; (opr=op.pop())!='(';){
							if (opr=='-')
								num.push(num.pop().minus());
							else if (opr=='*')
								num.push(num.pop().mul(num.pop()));
							else if (opr=='+')
								num.push(num.pop().add(num.pop()));
							else {
								Expression expo = num.pop();
								num.push(num.pop().pow(expo));
							}
						}
						
					break;
					
					case '+':
					case '*':
					case '^':
						if (pre!='N' && pre!=')')
							throw new Exception();
						
						pre = '2';
						
						//运算符直接入栈
						if (op.isEmpty() || isPriority(op.peek(), ch)){
							op.push(ch);
						}
						else{
							while (!op.isEmpty() && !isPriority(op.peek(), ch)){
								char opr = op.pop();
								if (opr=='-')
									num.push(num.pop().minus());
								else if (opr=='*')
									num.push(num.pop().mul(num.pop()));
								else if (opr=='+')
									num.push(num.pop().add(num.pop()));
								else {
									Expression expo = num.pop();
									num.push(num.pop().pow(expo));
								}
							}
							op.push(ch);
						}
						
					break;
					
					case '-':
						if (pre=='N' || pre==')')//减号前补上加号
						{
							if (op.isEmpty())
								op.push('+');
							else{
								while (!op.isEmpty() && !isPriority(op.peek(), '+')){
									char opr = op.pop();
									if (opr=='-')
										num.push(num.pop().minus());
									else if (opr=='*')
										num.push(num.pop().mul(num.pop()));
									else if (opr=='+')
										num.push(num.pop().add(num.pop()));
									else {
										Expression expo = num.pop();
										num.push(num.pop().pow(expo));
									}
								}
								op.push('+');
							}
						}
						op.push('-');
						pre='-';
					
					break;
					
					case '(':
						if (pre=='N' || pre==')')//省略乘号的情形
						{
							if (op.isEmpty() || isPriority(op.peek(), '*')){
								op.push('*');
							}
							else{
								while (!op.isEmpty() && !isPriority(op.peek(), '*')){
									char opr = op.pop();
									if (opr=='-')
										num.push(num.pop().minus());
									else if (opr=='*')
										num.push(num.pop().mul(num.pop()));
									else if (opr=='+')
										num.push(num.pop().add(num.pop()));
									else {
										Expression expo = num.pop();
										num.push(num.pop().pow(expo));
									}
								}
								op.push('*');
							}
						}
						op.push('(');
						pre='(';
					break;
					
					default:
					break;
				}
			}
			else if (i==j && (input.charAt(i)==' ' || input.charAt(i)=='\t') || clean_spaces(input.substring(i,j)).equals("")){
				i=j;
			}
			else{
				if (pre=='N' || pre==')')//省略乘号的情形
				{
					if (op.isEmpty() || isPriority(op.peek(), '*')){
						op.push('*');
					}
					else{
						while (!op.isEmpty() && !isPriority(op.peek(), '*')){
							char opr = op.pop();
							if (opr=='-')
								num.push(num.pop().minus());
							else if (opr=='*')
								num.push(num.pop().mul(num.pop()));
							else if (opr=='+')
								num.push(num.pop().add(num.pop()));
							else {
								Expression expo = num.pop();
								num.push(num.pop().pow(expo));
							}
						}
						op.push('*');
					}
				}
				num.push(getE(input.substring(i, j)));
					
				i=j;
				pre = 'N';
			}
		}
		
		for (char opr; !op.isEmpty();){
			opr = op.pop();
			if (opr=='-')
				num.push(num.pop().minus());
			else if (opr=='*')
				num.push(num.pop().mul(num.pop()));
			else if (opr=='+')
				num.push(num.pop().add(num.pop()));
			else {
				Expression expo = num.pop();
				num.push(num.pop().pow(expo));
			}
		}
		
		expr = num.peek().get();
		exist = true;
		return true;
	}
	
	private boolean isSeperator(char ch) {
		return ch=='+' || ch=='-' || ch=='*' || ch=='^' || ch=='(' || ch==')';
	}
	
	//判断字符串是否是分隔符+两侧空格
	private int isSeperator(String s){
		int i=0,j=s.length();
		for (;i<j && (s.charAt(i)==' ' || s.charAt(i)=='\t'); ++i);
		for (;i<j && (s.charAt(j-1)==' ' || s.charAt(j-1)=='\t'); --j);
		return j-i==1 && isSeperator(s.charAt(i))?i:-1;
	}
	
	//去除字符串两侧的空格
	private String clean_spaces(String s){
		int i=0,j=s.length();
		for (;i<j && (s.charAt(i)==' ' || s.charAt(i)=='\t'); ++i);
		for (;i<j && (s.charAt(j-1)==' ' || s.charAt(j-1)=='\t'); --j);
		return s.substring(i,j);
	}
	
	//返回news相对于olds的优先级
	private boolean isPriority(char olds, char news) {
		String order = "(+*-^";
		return order.indexOf(news) > order.indexOf(olds);
	}
	
	//返回变量名第一个字母的下标
	private int getFirstChar(String var) {
		int first_char=-1;
		for (;first_char+1<var.length() && isInNumber(var.charAt(first_char+1));++first_char);
		++first_char;
		return first_char;
	}
	
	private boolean isInNumber(Character ch){
		return ch>='0' && ch<='9' || ch=='.';
	}
	
	private Expression getE (String line) throws Exception {
		ConcurrentHashMap<HashMap<String,Integer> , Double> expr = new ConcurrentHashMap<>();		
		HashMap<String, Integer> item = new HashMap<>();
		//set coeff
		double coeff = 1.0d;
		String var = line;
		int expo = 1;
		String varName = "";
		//get the variable's first appearance index
		int first_char=getFirstChar(var);
		if (first_char>0)
			coeff *= Double.parseDouble(var.substring(0,first_char));
		varName = var.substring(first_char);
		String var_t = varName;
		String allow_n = "abcdefghijklmnopqrstuvwxyz";
		for(int var_check = 0;var_check < var_t.length();var_check++)
		{
			if(allow_n.indexOf(var_t.charAt(var_check)) == -1){
				System.out.println("No numbers in names of variables!");
				throw new Exception();
			}
		}
		if (varName.length()==0)
			expo=0;
		//------------------------------------------------------------------add to item
		if (item.containsKey(varName))
			item.replace(varName, item.get(varName)+expo);
		else {
			vset.add(varName);
			item.put(varName, expo);
		}
		//format requirement
		if (!item.containsKey(""))
			item.put("", 0);
		expr.put(item, coeff);
		return new Expression(expr);
	}

	public boolean getExist()
	{
		return exist;
	}
	
	public Expression add (Expression y) {
		HashMap<String, Integer> CONSTANT = new HashMap<>();
		CONSTANT.put("", 0);
		for (Map.Entry<HashMap<String, Integer>, Double> it:expr.entrySet()){
			HashMap<String, Integer> item = it.getKey();
			double coeff = it.getValue();
			if (y.get().containsKey(item)){
				if (y.get().get(item)+coeff > 1e-6 || y.get().get(item)+coeff < -1e-6)
					y.get().replace(item, y.get().get(item)+coeff);
				else
					y.get().remove(item);
			}
			else	
				y.get().put(item, coeff);
		}
		if (y.get().isEmpty() || !y.get().containsKey(CONSTANT))
			y.get().put(CONSTANT, 0.0d);
		return y;
	}
	
	public Expression mul (Expression y) {
		HashMap<String, Integer> CONSTANT = new HashMap<>();
		CONSTANT.put("", 0);
		ConcurrentHashMap<HashMap<String, Integer>, Double> z = new ConcurrentHashMap<>();
		for (Iterator<Map.Entry<HashMap<String, Integer>, Double>> itx = expr.entrySet().iterator(); itx.hasNext();){
			Map.Entry<HashMap<String, Integer>, Double> X = itx.next();
			HashMap<String, Integer> itemx = X.getKey();
			double coeffx = X.getValue();
			
			if (coeffx>-1e-6 && coeffx<1e-6)
				continue;
			
			for (Iterator<Map.Entry<HashMap<String, Integer>, Double>> ity = y.get().entrySet().iterator(); ity.hasNext();){
				Map.Entry<HashMap<String, Integer>, Double> Y = ity.next();
				HashMap<String, Integer> itemy = Y.getKey();
				double coeffy = Y.getValue();

				if (coeffy>-1e-6 && coeffy<1e-6)
					continue;
				
				double new_coeff = coeffx * coeffy;
				HashMap<String, Integer> new_item = new HashMap<>(itemy);
				
				for (Map.Entry<String, Integer> entry : itemx.entrySet()){
					String varName = entry.getKey();
					int expo = entry.getValue();
					
					if (new_item.containsKey(varName))
						new_item.replace(varName, new_item.get(varName)+expo);
					else	
						new_item.put(varName, expo);
				}
				
				if (z.containsKey(new_item)){
					if (z.get(new_item)+new_coeff > 1e-6 || z.get(new_item)+new_coeff < -1e-6)
						z.replace(new_item, z.get(new_item)+new_coeff);
					else
						z.remove(new_item);
				}
				else	
					z.put(new_item, new_coeff);
			}
		}
		if (z.isEmpty() || !z.containsKey(CONSTANT))
			z.put(CONSTANT, 0.0d);
		return new Expression(z);
	}
	
	public Expression minus () {
		for (Map.Entry<HashMap<String, Integer>, Double> item:expr.entrySet()){
			expr.replace(item.getKey(),-1*item.getValue());
		}
		return new Expression(expr);
	}
	
	public Expression pow(Expression y) throws Exception {
		HashMap<String, Integer> CONSTANT = new HashMap<>();
		CONSTANT.put("", 0);
		//calculate  x^y
		Expression z = new Expression(expr);
		
		if (y.get().size()>1 || !y.get().containsKey(CONSTANT)){
			System.out.println("Exponents must be integers.");
			throw new Exception();
		}

		double times = Double.parseDouble(y.get().get(CONSTANT).toString());
		
		if (times<1e-6){
			System.out.println("Exponent is expected to be a positive integer!");
			throw new Exception();
		}
		while (--times>1e-6){
			z = z.mul(new Expression(expr));
			//z=calculate('*', z,y);
		}
		return z;
	}
}
