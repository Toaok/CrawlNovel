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

    public final static String[] CHAPTERS_LIST_CSS_QUERY = {
            "div.listmain>dl>dd>a",
            "div#list>dl>dd>a",
            "div.box_con>dl>dd>a",
            "ul#chapter_list.section-list.fix>li>a"
    };

    public final static String[] CHAPTER_NEXT_PAGE_CSS_QUERY = {
            "div#wrapper>div.content_read>div.box_con>div.bottem>a#next_url"
    };

    public final static String[] CHAPTER_CONTENT_CSS_QUERY={
            "div#content",
            "div#htmlContent"
    };
}
