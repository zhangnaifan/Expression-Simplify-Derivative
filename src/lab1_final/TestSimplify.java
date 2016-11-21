/**
 * 
 */
package lab1_final;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author zhang.nf
 *
 */
public class TestSimplify {

	@Test
	public void test1() throws Exception {
		Expr expr = new Expr("x+y");
		String result = expr.Simplify("!simplify");
		assertEquals("","1.0*y+1.0*x",result);
	}
	
	@Test
	public void test2() throws Exception {
		Expr expr = new Expr("x+y");
		String result = expr.Simplify("!simplify ");
		assertEquals("","Error command!",result);
	}
	
	@Test
	public void test3() throws Exception {
		Expr expr = new Expr("x+y");
		String result = expr.Simplify("!simplify x=w");
		assertEquals("","Error command! Number presentation error.",result);
	}
	
	@Test
	public void test4() throws Exception {
		Expr expr = new Expr("x+y");
		String result = expr.Simplify("!simplify =0.2");
		assertEquals("","Error command! Variable name must not be empty!",result);
	}
	
	@Test
	public void test5() throws Exception {
		Expr expr = new Expr("x+y");
		String result = expr.Simplify("!simplify z=1");
		assertEquals("","Error command! Variable not found.",result);
	}
	
	@Test
	public void test6() throws Exception {
		Expr expr = new Expr("x+y");
		String result = expr.Simplify("!simplify x=2");
		assertEquals("","2.0+1.0*y",result);
	}
	
	@Test
	public void test7() throws Exception {
		Expr expr = new Expr("x*y");
		String result = expr.Simplify("!simplify x=2");
		assertEquals("","2.0*y",result);
	}
	
}
