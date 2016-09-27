
package lab1_final;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class parentheses {

	public static void main(String[] args){
		System.out.println("Testing! Please input something……Enter “END” to stop.");
		
		Expr my=null;
		boolean exist=false;
		
		String prompt;                                                   //the prompt message
		Scanner input = new Scanner(System.in) ;                         //input
		String line = input.nextLine();                                  //read input
                                                 
		while(!("END".equals(line)))                                     //loop until "END"
		{
			if (line.isEmpty())
			{
				line = input.nextLine();
				continue;
			}
			
			if(line.charAt(0)=='!')
			{
				if (exist == false )
				{
					prompt = "Please input an expression!";
				}
				else if (line.contains("d/d"))
				{
					prompt = my.Derivative(line);
				}
				else
				{
					prompt = my.Simplify(line);
				}
				System.out.println(prompt);
			}
			else
			{
				try{
					my = new Expr(line);
					System.out.println(my.toString());
					exist = true;
				}catch(Exception e){
					System.out.println("Please type in correct expression!");
				}
			}
			
			line = input.nextLine();
		}
		
		input.close();
		System.out.println("GOOD GAME!");                              // quit message
	}
}

class Expr{

	private ConcurrentHashMap<HashMap<String,Integer>, Double> myExpr;
	private HashMap<String,Integer> CONSTANT;
	
	public String toString(){
		
		DecimalFormat df = new DecimalFormat("#.0");
		String expr = "";
		//print the expression
		for (Map.Entry<HashMap<String, Integer>, Double> item : myExpr.entrySet()){
			Double coeff = item.getValue();
			if ((coeff<1e-6 && coeff>-1e-6))//0-constant item will not be shown
				continue;
			
			expr += df.format(coeff);
			
			for (Map.Entry<String, Integer> factor : item.getKey().entrySet()){
				if (factor.getKey().length()>0) // print factors
				{
					expr += "*"+factor.getKey();
					int expo = factor.getValue();
					if (expo>1+1e-6 || expo<1-1e-6)
						expr +="^"+expo;
				}
			}
			expr += "+";
		}

		if (expr.isEmpty())
			expr = "0.0";
		else
			expr = expr.substring(0,expr.length()-1);
		expr = expr.replace("+-", "-");
		
		return expr;
	}
	
	//输入字符串（带括号），生成表达式
	public Expr (String s)throws Exception{
		
		CONSTANT = new HashMap<>();
		CONSTANT.put("", 0);
		
		Stack<ConcurrentHashMap<HashMap<String, Integer>, Double>> num = new Stack<>();
		Stack<Character> op=new Stack<>();
		char pre = 'E';
		for (int i=0,j=0,len=s.length(); i<len;){
			
			for (; j<len && !isSeperator(s.charAt(j)); ++j);//j points to the next +-*()
			
			if (i==j && isSeperator(s.charAt(i)) || isSeperator(s.substring(i,j))!=-1){
				char ch;
				if (j==i){
					ch = s.charAt(i);
					++i;
					++j;
				}
				else{
					ch = s.charAt(i+isSeperator(s.substring(i,j)));
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
								num.push(calculate('-',num.pop()));
							else
								num.push(calculate(opr,num.pop(),num.pop()));
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
									num.push(calculate('-',num.pop()));
								else
									num.push(calculate(opr,num.pop(),num.pop()));
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
										num.push(calculate('-',num.pop()));
									else
										num.push(calculate(opr,num.pop(),num.pop()));
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
										num.push(calculate('-',num.pop()));
									else
										num.push(calculate(opr,num.pop(),num.pop()));
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
			else if (i==j && (s.charAt(i)==' ' || s.charAt(i)=='\t') || clean_spaces(s.substring(i,j)).equals("")){
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
								num.push(calculate('-',num.pop()));
							else
								num.push(calculate(opr,num.pop(),num.pop()));
						}
						op.push('*');
					}
				}
				num.push(getExpression(s.substring(i, j)));
					
				i=j;
				pre = 'N';
			}
		}
		
		for (char opr; !op.isEmpty();){
			opr = op.pop();
			if (opr=='-')
				num.push(calculate('-',num.pop()));
			else
				num.push(calculate(opr,num.pop(),num.pop()));
		}
		
		myExpr = num.peek();
	}
	
	//去除字符串两侧的空格
	private String clean_spaces(String s){
		int i=0,j=s.length();
		for (;i<j && (s.charAt(i)==' ' || s.charAt(i)=='\t'); ++i);
		for (;i<j && (s.charAt(j-1)==' ' || s.charAt(j-1)=='\t'); --j);
		return s.substring(i,j);
	}
	
	//执行表达式的+*^运算
	private ConcurrentHashMap<HashMap<String, Integer>, Double> calculate(char op, ConcurrentHashMap<HashMap<String, Integer>, Double> x,
			ConcurrentHashMap<HashMap<String, Integer>, Double> y)throws Exception {
		
		if (op=='+'){
			for (Map.Entry<HashMap<String, Integer>, Double> it:x.entrySet()){
				
				HashMap<String, Integer> item = it.getKey();
				double coeff = it.getValue();
				
				if (y.containsKey(item)){
					if (y.get(item)+coeff > 1e-6 || y.get(item)+coeff < -1e-6)
						y.replace(item, y.get(item)+coeff);
					else
						y.remove(item);
				}
				else	
					y.put(item, coeff);
			}
			if (y.isEmpty() || !y.containsKey(CONSTANT))
				y.put(CONSTANT, 0.0d);
			
			return y;
		}
		
		else if (op=='*'){
			ConcurrentHashMap<HashMap<String, Integer>, Double> z = new ConcurrentHashMap<>();
			
			for (Iterator<Map.Entry<HashMap<String, Integer>, Double>> itx = x.entrySet().iterator(); itx.hasNext();){
				Map.Entry<HashMap<String, Integer>, Double> X = itx.next();
				HashMap<String, Integer> itemx = X.getKey();
				double coeffx = X.getValue();
				
				if (coeffx>-1e-6 && coeffx<1e-6)
					continue;
				
				for (Iterator<Map.Entry<HashMap<String, Integer>, Double>> ity = y.entrySet().iterator(); ity.hasNext();){
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
			return z;
		}
		else{
			//calculate  x^y
			ConcurrentHashMap<HashMap<String, Integer>, Double> z = new ConcurrentHashMap<>(y);
			
			if (x.size()>1 || !x.containsKey(CONSTANT)){
				System.out.println("Exponents must be integers.");
				throw new Exception();
			}

			double times = Double.parseDouble(x.get(CONSTANT).toString());
			
			if (times<1e-6){
				System.out.println("Exponent is expected to be a positive integer!");
				throw new Exception();
			}
			while (--times>1e-6){
				z=calculate('*', z,y);
			}
			return z;
		}
	}
	
	//执行表达式的-运算
	private ConcurrentHashMap<HashMap<String, Integer>, Double> calculate(char op, ConcurrentHashMap<HashMap<String, Integer>, Double> x) {
		for (Map.Entry<HashMap<String, Integer>, Double> item:x.entrySet()){
			x.replace(item.getKey(),-1*item.getValue());
		}
		return x;
	}
	
	//输入数字+变量形式的字符串，返回表达式的数据结构
	private ConcurrentHashMap<HashMap<String,Integer>,Double> getExpression(String line) throws Exception
	{		
		
		//以下一大部分是合法性检查
		String temp = line.replace(" ", "");
		temp = temp.replace("	", "");
		temp = temp.toLowerCase();
		
		if(temp.length()==0)
		{
			System.out.println("Flag(null)");
			throw new Exception();
		}
		else if (temp.length()==1 && "abcdefghijklmnopqrstuvwxyz1234567890".indexOf(temp.charAt(0))==-1)
		{
			System.out.println("only one meaningless char.");
			throw new Exception();
		}
		else if ("abcdefghijklmnopqrstuvwxyz1234567890".indexOf(temp.charAt(temp.length()-1))==-1)
		{
			System.out.println("wrong end of expression!");
			throw new Exception();
		}
		else 
		{
			for(int i = 0; i<temp.length();i++)
			{
				if ("abcdefghijklmnopqrstuvwxyz1234567890+-*^.".indexOf(temp.charAt(i))==-1)
				{
					System.out.println("no " + temp.charAt(i) + "is allowed!");
					throw new Exception();
				}
			}
		}
		
		//create a new expression
		ConcurrentHashMap<HashMap<String,Integer> , Double> expr = new ConcurrentHashMap<>();		
		
		line = line.replaceAll("\\s+", " ");
		line = clean_spaces(line);
		
		String[] check = line.split(" |	");

		for(int a = 0; a < check.length -1 ; a++)
		{
			boolean fore = isInteger(check[a].substring(check[a].length()-1, check[a].length()));
			boolean nxt = isInteger(check[a+1].substring(0,1));
			
			//x x is not legal input, nether are x 2 nor 2 2.
			if(fore == true && nxt == true || fore==false && nxt==true || fore==false && fore==false)                                  //identify wrong space between two numbers
			{
				System.out.println("Wrong input!");
				throw new Exception();
			}
			else if (fore == true && ".".equals(check[a+1].substring(0,1)))  //wrong space between "." and a number
			{
				System.out.println("Wrong input!");
				throw new Exception();
			}
			else if (".".equals(check[a].substring(check[a].length()-1, check[a].length())) && nxt ==true )
			{
				System.out.println("Wrong input!");
				throw new Exception();
			}
		}	
		
		String return_msg = line.replace(" ", "");
		return_msg = return_msg.replace("	", "");
		
		//------------------------------------------------------------------------
		
		HashMap<String, Integer> item = new HashMap<>();
			
		//set coeff
		double coeff = 1.0d;
				
		String var = return_msg;
		
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
		else	
			item.put(varName, expo);
	
		//format requirement
		if (!item.containsKey(""))
			item.put("", 0);
	
		expr.put(item, coeff);
				
		return expr;
	}
	
	//返回news相对于olds的优先级
	private boolean isPriority(char olds, char news) {
		String order = "(+*-^";
		return order.indexOf(news) > order.indexOf(olds);
	}
	
	//判断字符是否是分隔符
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

	private boolean isInteger(String str)
	{
		boolean isint = true;
		try{
			int num = Integer.parseInt(str);
		}
		catch(NumberFormatException num){
			isint = false;
		}
		finally{
			return isint;
		}
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
	
	//求导
	public String Derivative(String line){
		
		String name = line.substring(1).replace(" ", "").replace("d/d", "");
		
		if ("".equals(name)){
			return "Please input one variable!";
		}
		
		boolean found = false;
		
		myExpr.replace(CONSTANT, 0.0d);
		
		Iterator<Map.Entry<HashMap<String, Integer>, Double>> it = myExpr.entrySet().iterator();
		
		while (it.hasNext()){
			
			Map.Entry<HashMap<String, Integer>, Double> item = it.next();
			HashMap<String, Integer> factors = item.getKey();
			double coeff = item.getValue();
			
			if (factors.containsKey(name)){
				
				found = true;
				int expo = factors.get(name);
				
				//deal with 3x
				if (isSingleVariableItem(item) && expo<1+1e-6 && expo>1-1e-6){
					
					it.remove();
					updateConstantItem(coeff * expo);
				}
				//deal with 3x*y
				else if (expo<1+1e-6 && expo>1-1e-6){
					
					HashMap<String, Integer> temp = new HashMap<>(factors);
					temp.remove(name);
					myExpr.put(temp, coeff*expo);
					it.remove();
				}
				//deal with 3x^2
				else{
					
					HashMap<String, Integer> temp = new HashMap<>(factors);
					temp.replace(name, expo-1);
					myExpr.put(temp, coeff*expo);
					it.remove();
				}

			}
		}
		return found ? toString()  : "Error! Variable not found.";
	}

	private void updateConstantItem(double add_this) {
		myExpr.replace(CONSTANT, myExpr.get(CONSTANT)+add_this);
	}
	
	private boolean isSingleVariableItem(Map.Entry<HashMap<String,Integer>, Double> item) {
		return item.getKey().size()==2 && item.getKey().containsKey("");
	}
	
	public String Simplify(String line)
	{
		if ("!simplify".equals(line))
		{
			return toString();
		}
		line = line.replace("!simplify ", "");
		
		String[] equations = line.split(" ");   

		for (String equ:equations){
			
			String[] var_val = equ.split("=");
			
			if (var_val.length != 2){
				return "Error command!";
			}
			
			else if (!isDouble(var_val[1])){
				return "Error command! Number presentation error.";
			}
			
			else if (var_val[0].equals("")){
				return "Error command! Variable name must not be empty!";
			}
			
			double base = Double.parseDouble(var_val[1]);
			String name = var_val[0];
			
			boolean found = false;
			
			Iterator<Map.Entry<HashMap<String, Integer>, Double>> it = myExpr.entrySet().iterator();
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
					}
					else{
						HashMap<String, Integer> temp = new HashMap<>(factors);
						temp.remove(name);
						myExpr.put(temp, add_this);
						it.remove();
					}
					//System.out.println(getExpression());
				}
			}
			if (!found)
				return "Error command! Variable not found.";
		}
		return toString();
	}
}