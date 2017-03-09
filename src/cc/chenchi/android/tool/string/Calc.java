package cc.chenchi.android.tool.string;

import com.sun.org.apache.xpath.internal.operations.Div;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by reall on 2017/1/24.
 */
public class Calc {
    /*
    String cal = "(((((5+4)*2-(((((5+4)*2)-2)/(5-2*(((((5+4)*2)-2)/(5-2)-(4*3-2))+6.2))-(4*3-2))+6.2))-2)/(5-2*(((((5+4)*2)-2)/(5-2)-(4*3-2))+6.2))-(4*3-2))+6.2)*6";
    double ret = new Calc().result(cal);
    System.out.println("Ret:\t" + ret);
    */

    public double result(String str) {
        str = str.replace(" ", "");
        List<Element> elements = getElement(str);
        return getResult(elements);
    }

    List<Element> getElement(String str){
        List<Element> elements = new ArrayList<>();
        while (str.length() > 0) {
            char nextC = str.charAt(0);
            Element nextElement = null;
            if (nextC == '(') {
                nextElement = new KhOperator();
            } else if (nextC == '+') {
                nextElement = new AddOperator();
            } else if (nextC == '-') {
                nextElement = new MinusOperator();
            } else if (nextC == '*') {
                nextElement = new MultOperator();
            } else if (nextC == '/') {
                nextElement = new DivOperator();
            }else if (isNumber(nextC)){
                nextElement = new DataNum();
            }else{
                System.out.println("ERROR");
                continue;
            }
            str = nextElement.parse(str);
            elements.add(nextElement);
        }

        return elements;
    }

    private boolean isNumber(char c){
        return ((c <= '9' && c >= '0') || c== '.');
    }

    abstract class Element {
        abstract String parse(String str);
    }

    class DataNum extends Element {
        double data;

        String parse(String str) {
            int pos = 0;
            while (isNumber(str, ++pos)) ;
            data = Double.parseDouble(str.substring(0, pos));
            return str.substring(pos);
        }

        private boolean isNumber(String str, int pos) {
            if (pos >= str.length()) return false;
            char c = str.charAt(pos);
            if ((c <= '9' && c >= '0') || c== '.') {
                return true;
            } else {
                return false;
            }
        }


        public String toString(){
            return "" + data;
        }
    }

    abstract class Operator extends Element{
        abstract DataNum cal(DataNum left, DataNum right);
    }

    class KhOperator extends DataNum {
        @Override
        String parse(String str) {
            int pos = 0;
            int khNum = 0;
            for(int i = 0; i < str.length(); ++i){
                char c = str.charAt(i);
                if (c == '('){
                    khNum ++;
                }else if (c == ')'){
                    khNum --;
                    if (khNum == 0){
                        pos = i + 1;
                        break;
                    }
                }
            }
            String khStr = str.substring(1, pos - 1);
            String ret = str.substring(pos);
            List<Element> elements = getElement(khStr);
            data = getResult(elements);
            return ret;
        }
    }

    double getResult(List<Element> elements){
        if (elements.size() == 1){
            return ((DataNum)elements.get(0)).data;
        }

        boolean done = false;
        for(int i = 0; i < elements.size(); ++i){
            Element e = elements.get(i);
            if (e instanceof SupOperator){
                done = true;
                elements = doEle(elements, i);
                break;
            }
        }
        if (!done){
            for(int i = 0; i < elements.size(); ++i){
                Element e = elements.get(i);
                if (e instanceof Operator){
                    elements = doEle(elements, i);
                    break;
                }
            }
        }
        return getResult(elements);
    }

    List<Element> doEle(List<Element> elements, int idx){
        Operator operator = (Operator)elements.get(idx);
        DataNum dataLeft = (DataNum)elements.get(idx - 1);
        DataNum dataRight = (DataNum)elements.get(idx + 1);
        DataNum result = operator.cal(dataLeft, dataRight);
        elements.set(idx, result);
        elements.remove(idx + 1);
        elements.remove(idx - 1);
        return elements;
    }

    abstract class SupOperator extends Operator{

    }

    class DivOperator extends SupOperator {
        @Override
        String parse(String str){
            return str.substring(1);
        }

        @Override
        DataNum cal(DataNum left, DataNum right) {
            DataNum ret = new DataNum();
            ret.data = left.data / right.data;
            return ret;
        }
        public String toString(){
            return "\\";
        }
    }

    class MultOperator extends SupOperator {
        @Override
        String parse(String str) {
            return str.substring(1);
        }

        @Override
        DataNum cal(DataNum left, DataNum right) {
            DataNum ret = new DataNum();
            ret.data = left.data * right.data;
            return ret;
        }
    }

    class MinusOperator extends Operator {
        @Override
        String parse(String str) {
            return str.substring(1);
        }

        @Override
        DataNum cal(DataNum left, DataNum right) {
            DataNum ret = new DataNum();
            ret.data = left.data - right.data;
            return ret;
        }
    }

    class AddOperator extends Operator {
        @Override
        String parse(String str) {
            return str.substring(1);
        }

        @Override
        DataNum cal(DataNum left, DataNum right) {
            DataNum ret = new DataNum();
            ret.data = left.data + right.data;
            return ret;
        }
    }
}
