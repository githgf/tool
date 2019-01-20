package cn.hans.common.utils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Created by usj-zhh on 2015/8/6.
 */
public class NumberUtils {

    public static Double format(double data,int scope){
        //10的位数次方 如保留2位则 tempDouble=100
        double tempDouble=Math.pow(10, scope);
        //原始数据先乘tempDouble再转成整型，作用是去小数点
        data=data*tempDouble;
        int tempInt=(int) data;
        //返回去小数之后再除tempDouble的结果
        return tempInt/tempDouble;
    }

	public static boolean isNumeric1(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public static double doubleValue(BigDecimal decimal) {
    	return decimal == null ? 0.0:decimal.doubleValue();
	}
	
	public static boolean checkoutExpress(String expressNo){
		Integer i = Calendar.getInstance().get(Calendar.YEAR);
		if(expressNo.indexOf(i.toString())==0){
			return false;
		}
		return true;
	}

	/**
	 * 将两位int数字相除结果保留任意位小数
	 * @param divisor 		除数
	 * @param dividend 		被除数
	 * @param lastNum 		保留小数位
	 * @return				double
	 * @throws Exception 	RuntimeException
	 */
	public static  double getDoubleResult(int divisor,int dividend,int lastNum) throws Exception{

		if (dividend == 0){throw new RuntimeException();}

		double v = (double)divisor/dividend;
		return formatSmallNum(v,lastNum);
	}

	/**
	 * 	将double保留指定位数的有效数字
	 * @param source		double类型值
	 * @param lastNum		小数点后几位
	 * @return
	 */
	public static double formatSmallNum(double source,int lastNum){
		int num = 1;
		for (int i = 0; i < lastNum; i++) {

			num = num * 10;
		}
		source = (double)(int)(source * num + 0.5);
		return source / num;
	}

}
