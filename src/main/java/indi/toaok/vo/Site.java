package indi.toaok.vo;

/**
 * @author TOAOK
 * @version 1.0  2018/1/10.
 */
public class Site {
    public final static String DINGDIAN_SITE = "顶点(8)";
    public final static String BIQUGE_SITE = "笔趣阁(0)";

    public final static String OTHER_SITE = "其他(0)";

    public final static String CHARSET_CSSQUERY = "head>meta";

    public final static String[] NAME_CSSQUERY = {
            "div.book>div.info>h2",
            "div.book>div.info>h1",
            "div#maininfo>div#info>h1"
    };

    public final static String[] CHAPTER_LIST_CSSQUERY = {
            "div.listmain>dl>dd>a",
            "div#list>dl>dd>a",
    };
}
