package indi.toaok;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Toaok
 * @version 1.0  2021/7/18.
 */
public class Test {
    public static void main(String[] args) {
        //查看当前系统的字符编码方式
        System.out.println(Charset.defaultCharset().name());
        //查看当前系统的编码方式
        System.out.println(System.getProperty("file.encoding"));
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        System.out.println(format.format(new Date(System.currentTimeMillis())));

    }
}
