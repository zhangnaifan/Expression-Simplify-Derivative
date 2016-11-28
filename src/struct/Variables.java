package struct;

import java.util.HashSet;
import java.util.Set;

public class Variables {
	private Set<String> vars = new HashSet<String>();
	public Set<String> get() {
		return vars;
	}
	public boolean clean()
	{
		vars.clear();
		return false;
	}
	public boolean add(String input)
	{
		vars.add(input);
		return false;
	}
	public boolean remove(String input)
	{
		vars.remove(input);
		return false;
	}
	public boolean check(String input)
	{
		return vars.contains(input);
	}
	public static void main(String[] args) {
		Set<String> vars = new HashSet<String>();
		vars.add("good");
		if (vars.contains("good")) {
			System.out.println("inside");
		}
	}
}
