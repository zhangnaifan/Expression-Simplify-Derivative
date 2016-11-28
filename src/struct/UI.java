package struct;

import java.util.Scanner;

public class UI {

	public static void main(String[] args) throws Exception {
		System.out.println("Welcome!!! Please input an expression:");
		// TODO Auto-generated method stub
		Expression expr = new Expression();
		Variables vset = new Variables();
		
		Scanner inp = new Scanner(System.in) ; 
		
		String input = inp.nextLine();
		String output = "";
		
		
		while(!input.equals("END"))
		{
			if(input.equals(""))
			{
				input = inp.nextLine();
				continue;
			}

			if(input.charAt(0)=='!')
			{
				if (input.contains("!d/d "))
				{
					Derivative n1 = new Derivative();
					n1.expr = expr;
					n1.vset = vset;
					output = n1.startderi(input);
					expr = n1.expr;
					vset = n1.vset;
					System.out.println(output); 
				}
				else if (input.contains("!simplify "))
				{
					Simplify n1 = new Simplify();
					n1.expr = expr;
					n1.vset = vset;
					output = n1.startsimp(input);
					expr = n1.expr;
					vset = n1.vset;
					System.out.println(output); 
				}
				else
				{
					output = "Wrong command!";
					System.out.println(output); 
				}
			}
			else
			{
				NewExpr n1 = new NewExpr();
				n1.expr = expr;
				n1.vset = vset;
				output = n1.startexpr(input);
				expr = n1.expr;
				vset = n1.vset;
				System.out.println(output); 
			}
			input = inp.nextLine();
			continue;
		}
		inp.close();
		System.out.println("GOOD GAME!");  
	}

}
