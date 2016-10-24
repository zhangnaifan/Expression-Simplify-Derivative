package lab1_final;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

public class Parentheses {
  /**
  * .
  */
  public static void main(String[] args) {
    System.out.println("Testing! Please input something……Enter “END” to stop.");
    Expr my = null;

    boolean exist = false;
    String prompt;                                                 //the prompt message
    Scanner input = new Scanner(System.in) ;                       //input
    String line = input.nextLine();                                //read input
                                                 
    while (!("END".equals(line))) {                                  //loop until "END"   
      if (line.isEmpty()) {
        line = input.nextLine();
        continue;
      }
      if (line.charAt(0) == '!') {
        if (exist == false ) {
          prompt = "Please input an expression!";
        } else if (line.contains("d/d")) {
          prompt = my.deRivative(line);
        } else {
          prompt = my.siMplify(line);
        }
        System.out.println(prompt);
      } else {
        try {
          my = new Expr(line);
          System.out.println(my.toString());
          exist = true;
        } catch (Exception eeE) {
          System.out.println("Please type in correct expression!");
        }
      }
      line = input.nextLine();
    }
    
    input.close();
    System.out.println("GOOD GAME!");                              // quit message
  }
}

class Expr {

  private ConcurrentHashMap<HashMap<String,Integer>, Double> myExpr;
  private HashMap<String,Integer> aaCaOaNaSaTaAaNaTa;

  public String toString() {

    DecimalFormat df = new DecimalFormat("#.0");
    String expr = ""; 
    //print the expression 
    for (Entry<HashMap<String, Integer>, Double> item : myExpr.entrySet()) {
      Double coeff = item.getValue();
      if ((coeff < 1e-6 && coeff > -1e-6)) { //0-constant item will not be shown
        continue;
      }
      expr += df.format(coeff);
      for (Map.Entry<String, Integer> factor : item.getKey().entrySet()) {
        if (factor.getKey().length() > 0) { // print factors       
          expr += "*" + factor.getKey();
          int expo = factor.getValue();
          if (expo > 1 + 1e-6 || expo < 1 - 1e-6) {
            expr += "^" + expo; 
          }
        }
      }
      expr += "+";
    }

    if (expr.isEmpty()) {
      expr = "0.0";
    } else {
      expr = expr.substring(0,expr.length() - 1);
    }
    expr = expr.replace("+-", "-");

    return expr;
  }

  //输入字符串（带括号），生成表达式
  public Expr(String ssS)throws Exception {

    aaCaOaNaSaTaAaNaTa = new HashMap<>();
    aaCaOaNaSaTaAaNaTa.put("", 0);
    Stack<ConcurrentHashMap<HashMap<String, Integer>, Double>> num = new Stack<>();
    Stack<Character> op = new Stack<>();
    char pre = 'E';
    for (int i = 0,j = 0,len = ssS.length(); i < len;) {
      for (; j < len && !isSeperator(ssS.charAt(j)); ++j) { //j points to the next +-*()
        if (i == j && isSeperator(ssS.charAt(i)) || isSeperator(ssS.substring(i,j)) != -1) {
          char ch;
          if (j == i) {
            ch = ssS.charAt(i);
            ++i;
            ++j;
          } else {
            ch = ssS.charAt(i + isSeperator(ssS.substring(i,j)));
            i = j;
          }
          switch (ch) {
            case ')':
              if (pre != 'N' && pre != ')') {
                throw new Exception();
              }
              pre = ')';

              for (char opr; (opr = op.pop()) != '(';) {
                if (opr == '-') {
                  num.push(calculate('-',num.pop()));
                } else {
                  num.push(calculate(opr,num.pop(),num.pop()));
                } 
                break;
              }
              break;
              
            case '+':
              break;
            
            case '*':
              break;
              
            case '^':
              if (pre != 'N' && pre != ')') {
                throw new Exception();
              }
              pre = '2';
              //运算符直接入栈
              if (op.isEmpty() || isPriority(op.peek(), ch)) {
                op.push(ch);
              } else {
                while (!op.isEmpty() && !isPriority(op.peek(), ch)) {
                  char opr = op.pop();
                  if (opr == '-') {
                    num.push(calculate('-',num.pop()));
                  } else {
                    num.push(calculate(opr,num.pop(),num.pop()));
                  }
                  op.push(ch);
                }
                break;
              }
              break;

            case '-':
              if (pre == 'N' || pre == ')') { //减号前补上加号
              
                if (op.isEmpty()) {
                  op.push('+');
                } else {
                  while (!op.isEmpty() && !isPriority(op.peek(), '+')) {
                    char opr = op.pop();
                    if (opr == '-') {
                      num.push(calculate('-',num.pop()));
                    } else {
                      num.push(calculate(opr,num.pop(),num.pop()));
                    }
                  }
                  op.push('+');
                }
              }
              op.push('-');
              pre = '-';
              break;

            case '(':
              if (pre == 'N' || pre == ')') { //省略乘号的情形        
                if (op.isEmpty() || isPriority(op.peek(), '*')) {
                  op.push('*');
                } else {
                  while (!op.isEmpty() && !isPriority(op.peek(), '*')) {
                    char opr = op.pop();
                    if (opr == '-') {
                      num.push(calculate('-',num.pop()));
                    } else {
                      num.push(calculate(opr,num.pop(),num.pop())); 
                    }
                  }
                  op.push('*');
                }
              }
              op.push('(');
              pre = '(';
              break;

            default:
              break;
          }
        } else if (i == j && (ssS.charAt(i) == ' '
            || ssS.charAt(i) == '\t') || clean_spaces(ssS.substring(i,j)).equals("")) {
          i = j;
        } else {
          if (pre == 'N' || pre == ')') { //省略乘号的情形
            if (op.isEmpty() || isPriority(op.peek(), '*') ) {
              op.push('*');
            } else {
              while (!op.isEmpty() && !isPriority(op.peek(), '*')) {
                char opr = op.pop();
                if (opr == '-') {
                  num.push(calculate('-',num.pop()));
                } else {
                  num.push(calculate(opr,num.pop(),num.pop()));
                }
              }
              op.push('*');
            } 
          }
          num.push(getExpression(ssS.substring(i, j)));

          i = j;
          pre = 'N';
        }
      }

      for (char opr; !op.isEmpty();) {
        opr = op.pop();
        if (opr == '-') {
          num.push(calculate('-',num.pop()));
        } else {
          num.push(calculate(opr,num.pop(),num.pop()));
        }
      }

      myExpr = num.peek() ;
    }
  }

  //去除字符串两侧的空格
  private String clean_spaces(String ssS) {
    int iiI = 0;
    int jjJ = ssS.length();
    for (;iiI < jjJ && (ssS.charAt(iiI) == ' ' || ssS.charAt(iiI) == '\t'); ++iiI) {
      for (;iiI < jjJ && (ssS.charAt(jjJ - 1) == ' ' || ssS.charAt(jjJ - 1) == '\t'); --jjJ) {
      }
    }
    return ssS.substring(iiI,jjJ);
  }

  //执行表达式的+*^运算
  private ConcurrentHashMap<HashMap<String, Integer>, 
        Double> calculate(char op, ConcurrentHashMap<HashMap<String, Integer>, Double> xxX,
        ConcurrentHashMap<HashMap<String, Integer>, Double> yyY)throws Exception {

    if (op == '+') {
      for (Map.Entry<HashMap<String, Integer>, Double> it:xxX.entrySet()) {

        HashMap<String, Integer> item = it.getKey();
        double coeff = it.getValue();

        if (yyY.containsKey(item)) {
          if (yyY.get(item) + coeff > 1e-6 || yyY.get(item) + coeff < -1e-6) {
            yyY.replace(item, yyY.get(item) + coeff);
          } else {
            yyY.remove(item);
          }
        } else {
          yyY.put(item, coeff);
        }
      }
      if (yyY.isEmpty() || !yyY.containsKey(aaCaOaNaSaTaAaNaTa)) {
        yyY.put(aaCaOaNaSaTaAaNaTa, 0.0d);
      }

      return yyY;
    } else if (op == '*') {
      ConcurrentHashMap<HashMap<String, Integer>, Double> zzZ = new ConcurrentHashMap<>();

      for (Iterator<Map.Entry<HashMap<String, Integer>, Double>> 
          itx = xxX.entrySet().iterator(); itx.hasNext();) {
        Map.Entry<HashMap<String, Integer>, Double> qqQ = itx.next();
        HashMap<String, Integer> itemx = qqQ.getKey();
        double coeffx = qqQ.getValue();

        if (coeffx > -1e-6 && coeffx < 1e-6) {
          continue;
        }
        for (Iterator<Map.Entry<HashMap<String, Integer>, Double>> 
            ity = yyY.entrySet().iterator(); ity.hasNext();) {
          Map.Entry<HashMap<String, Integer>, Double> aaY = ity.next();
          HashMap<String, Integer> itemy = aaY.getKey();
          double coeffy = aaY.getValue();

          if (coeffy > -1e-6 && coeffy < 1e-6) {
            continue;
          }

          double neW = coeffx * coeffy;
          HashMap<String, Integer> neI = new HashMap<>(itemy);

          for (Map.Entry<String, Integer> entry : itemx.entrySet()) {
            String varName = entry.getKey();
            int expo = entry.getValue();

            if (neI.containsKey(varName)) {
              neI.replace(varName, neI.get(varName) + expo);
            } else {
              neI.put(varName, expo);
            }
          }
          if (zzZ.containsKey(neI)) {
            if (zzZ.get(neI) + neW > 1e-6 || zzZ.get(neW) + neW < -1e-6) {
              zzZ.replace(neI, zzZ.get(neI) + neW);
            } else {
              zzZ.remove(neI);
            }
          } else {
            zzZ.put(neI, neW);
          }
        }
      }
      if (zzZ.isEmpty() || !zzZ.containsKey(aaCaOaNaSaTaAaNaTa)) {
        zzZ.put(aaCaOaNaSaTaAaNaTa, 0.0d);
      }
      return zzZ;
    } else {
      //calculate  xxX^yyY
      ConcurrentHashMap<HashMap<String, Integer>, Double> zzZ = new ConcurrentHashMap<>(yyY);
      if (xxX.size() > 1 || !xxX.containsKey(aaCaOaNaSaTaAaNaTa)) {
        System.out.println("Exponents must be integers.");
        throw new Exception();
      }
      double times = Double.parseDouble(xxX.get(aaCaOaNaSaTaAaNaTa).toString());

      if (times < 1e-6) {
        System.out.println("Exponent is expected to be a positive integer!");
        throw new Exception();
      }
      while (--times > 1e-6) {
        zzZ = calculate('*', zzZ,yyY);
      }
      return zzZ;
    }
  }
  //执行表达式的-运算
  
  private ConcurrentHashMap<HashMap<String, Integer>,
      Double> calculate(char op, ConcurrentHashMap<HashMap<String, Integer>, Double> xxX) {
    for (Map.Entry<HashMap<String, Integer>, Double> item:xxX.entrySet()) {
      xxX.replace(item.getKey(),-1 * item.getValue());
    }
    return xxX;
  }
  //输入数字+变量形式的字符串，返回表达式的数据结构
  
  private ConcurrentHashMap<HashMap<String,Integer>,
      Double> getExpression(String line) throws Exception {
    //以下一大部分是合法性检查
    String temp = line.replace(" ", "");
    temp = temp.replace(" ", "");
    temp = temp.toLowerCase();
    if (temp.length() == 0) {
      System.out.println("Flag(null)");
      throw new Exception();
    } else if (temp.length() == 1 
        && "abcdefghijklmnopqrstuvwxyz1234567890".indexOf(temp.charAt(0)) == -1) {
      System.out.println("only one meaningless char.");
      throw new Exception();
    } else if ("abcdefghijklmnopqrstuvwxyz1234567890".indexOf(
        temp.charAt(temp.length() - 1)) == -1) {
      System.out.println("wrong end of expression!");
      throw new Exception();
    } else {
      for (int iiI = 0; iiI < temp.length();iiI++) {
        if ("abcdefghijklmnopqrstuvwxyz1234567890+-*^.".indexOf(temp.charAt(iiI)) == -1) {
          System.out.println("no " + temp.charAt(iiI) + "is allowed!");
          throw new Exception();
        }
      }
    }
    //create a new expression
    ConcurrentHashMap<HashMap<String,Integer>,
        Double> expr = new ConcurrentHashMap<>();

    line = line.replaceAll("\\s+", " ");
    line = clean_spaces(line);
        
    String[] check = line.split(" | ");

    for (int a = 0; a < check.length - 1 ; a++) { 
      boolean fore = isInteger(check[a].substring(check[a].length() - 1, check[a].length()));
      boolean nxt = isInteger(check[a + 1].substring(0,1));
      //x x is not legal input, nether are x 2 nor 2 2.
      if (fore == true && nxt == true || fore == false && nxt == true 
          || fore == false && fore == false) {
        //identify wrong space between two numbers
  
        System.out.println("Wrong input!");
        throw new Exception();
      } else if (fore == true && ".".equals(check[a + 1].substring(0,1))) {
        //wrong space between "." and a number
        System.out.println("Wrong input!");
        throw new Exception();
      } else if (".".equals(check[a].substring(check[a].length() - 1, check[a].length())) 
             && nxt == true ) {
        System.out.println("Wrong input!");
        throw new Exception();
      }
    }
    String reTurnmsg = line.replace(" ", "");
    reTurnmsg = reTurnmsg.replace("   ", "");
    //------------------------------------------------------------------------
    HashMap<String, Integer> item = new HashMap<>();  //set coeff
    double coeff = 1.0d;
    String var = reTurnmsg;
    int expo = 1;
    String varName = "";
    //get the variable's first appearance index
    int fiRstchar = getFirstChar(var);
    if (fiRstchar > 0) {
      coeff *= Double.parseDouble(var.substring(0,fiRstchar));
    }
    varName = var.substring(fiRstchar);
    String vaRt = varName;
    String alLown = "abcdefghijklmnopqrstuvwxyz";
    for (int vaRcheck = 0;vaRcheck < vaRt.length();vaRcheck++) {
      if (alLown.indexOf(vaRt.charAt(vaRcheck)) == -1) {
        System.out.println("No numbers in names of variables!");
        throw new Exception();
      }
    }
    if (varName.length() == 0) {
      expo = 0;
      //------------------------------------------------------------------add to item
    }
    if (item.containsKey(varName)) {
      item.replace(varName, item.get(varName) + expo); 
    } else {
      item.put(varName, expo);
      //format requirement
    }
    if (!item.containsKey("")) {
      item.put("", 0);
    }
    expr.put(item, coeff);
    return expr;
  }
  //返回news相对于olds的优先级
  
  private boolean  isPriority(char olds, char news) {
    String order = "(+*-^";
    return order.indexOf(news) > order.indexOf(olds);
  }
  //判断字符是否是分隔符
  
  private boolean  isSeperator(char ch) {
    return ch == '+' || ch == '-' || ch == '*' || ch == '^' || ch == '(' || ch == ')';
  }
  //判断字符串是否是分隔符+两侧空格
  
  private int  isSeperator(String ssA) {
    int iiA = 0;
    int jjA = ssA.length();
    for (;iiA < jjA && (ssA.charAt(iiA) == ' ' || ssA.charAt(iiA) == '\t'); ++iiA) {
  
    }
    for (;iiA < jjA && (ssA.charAt(jjA - 1) == ' ' || ssA.charAt(jjA - 1) == '\t'); --jjA) {
    }
    return jjA - iiA == 1 && isSeperator(ssA.charAt(iiA)) ? iiA : - 1;
  }
  
  private boolean  isDouble(String str) {
    boolean isdouble = true;
    try { 
      double num = Double.parseDouble(str); 
    } catch (NumberFormatException num) {
      isdouble = false;
    } finally {
      return isdouble;
    }
  }

  private boolean isInteger(String str) {
    boolean isint = true;
    try { 
      int num = Integer.parseInt(str);
    } catch (NumberFormatException num) {
      isint = false;
    } finally {
      return isint;
    }
  }
  //返回变量名第一个字母的下标
  
  private int getFirstChar(String var) {
    int fiRstchar = - 1;
    for (;fiRstchar + 1 < var.length()
        && isInNumber(var.charAt(fiRstchar + 1));++fiRstchar) {
      ++fiRstchar;
    }
    
    return fiRstchar;
  }
  
  private boolean isInNumber(Character ch) {
    return ch >= '0' && ch <= '9' || ch == '.';
  }
  //求导

  public String deRivative(String liNe) {
    String name = liNe.substring(1).replace(" ", "").replace("d/d", "");
    if ("".equals(name)) {
      return "Please input one variable!";
    }

    boolean found = false;
    myExpr.replace(aaCaOaNaSaTaAaNaTa, 0.0d);

    Iterator<Map.Entry<HashMap<String, Integer>, Double>> it = myExpr.entrySet().iterator();

    while (it.hasNext()) {

      Map.Entry<HashMap<String, Integer>, Double> item = it.next();
      HashMap<String, Integer> factors = item.getKey();
      double coeff = item.getValue();

      if (factors.containsKey(name)) {
        found = true;
        int expo = factors.get(name);
        //deal with 3x
        if (isSingleVariableItem(item) && expo < 1 + 1e-6 && expo > 1 - 1e-6) {
          it.remove();
          updateConstantItem(coeff * expo);
          //deal with 3x*y
        } else if (expo < 1 + 1e-6 && expo > 1 - 1e-6) {
          HashMap<String, Integer> temp = new HashMap<>(factors);
          temp.remove(name);
          myExpr.put(temp, coeff * expo);
          it.remove();
          //deal with 3x^2
        } else {
          HashMap<String, Integer> temp = new HashMap<>(factors);
          temp.replace(name, expo - 1);
          myExpr.put(temp, coeff * expo);
          it.remove();
        }
      }
    }
    return found ? toString()  : "Error! Variable not found.";
  }

  private void updateConstantItem(double adDthis) {
    myExpr.replace(aaCaOaNaSaTaAaNaTa, myExpr.get(aaCaOaNaSaTaAaNaTa) + adDthis);
  }

  private boolean isSingleVariableItem(Map.Entry<HashMap<String,Integer>, Double> item) {
    return item.getKey().size() == 2 && item.getKey().containsKey("");
  }

  public String siMplify(String line) {
    if ("!simplify".equals(line)) {
      return toString();
    }
    line = line.replace("!simplify ", "");

    String[] equations = line.split(" ");   

    for (String equ:equations) {

      String[] vaRval = equ.split("=");

      if (vaRval.length != 2) {
        return "Error command!";
      } else if (!isDouble(vaRval[1])) {
        return "Error command! Number presentation error."; 
      } else if (vaRval[0].equals("")) {
        return "Error command! Variable name must not be empty!";
      }

      double base = Double.parseDouble(vaRval[1]);
      String name = vaRval[0];

      boolean found = false;

      Iterator<Map.Entry<HashMap<String, Integer>, Double>> it = myExpr.entrySet().iterator();
      while (it.hasNext()) {
        Map.Entry<HashMap<String, Integer>, Double> item = it.next();
        HashMap<String, Integer> factors = item.getKey();
        double coeff = item.getValue();

        if (factors.containsKey(name)) {
          //把 name^expo 乘到系数上
          //删除name
          //如果去掉变量之后item为常数项，若Expr无常数项，则补上常数项；否则将系数加到常数项上
          found = true;
          double adDthis = coeff * Math.pow(base, factors.get(name));
          if (isSingleVariableItem(item)) {
            item.setValue(adDthis);
            it.remove();
            updateConstantItem(adDthis);
          } else {
            HashMap<String, Integer> temp = new HashMap<>(factors);
            temp.remove(name);
            myExpr.put(temp, adDthis);
            it.remove();
          }
          //System.out.println(getExpression());
        }
      }
      if (!found) {
        return "Error command! Variable not found.";
      }
    }
    return toString();
  }

}