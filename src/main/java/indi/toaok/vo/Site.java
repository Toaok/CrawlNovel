package indi.toaok.vo;

/**
 * @author TOAOK
 * @version 1.0  2018/1/10.
 */
public class Site {

    public final static String CHARSET_CSS_QUERY = "head>meta";

    //
    public final static String[] NAME_CSS_QUERY = {
            "div.book>div.info>h2",
            "div.book>div.info>h1",
            "div#maininfo>div#info>h1",
            "div#maininfo>div#info>div.booktitle.cf>h1"
    };

    public final static String[] CHAPTER_LIST_CSS_QUERY = {
            "div.listmain>dl>dd>a",
            "div#list>dl>dd>a",
    };
}
