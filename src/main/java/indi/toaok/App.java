package indi.toaok;


import indi.toaok.main.NovelDownload;
import indi.toaok.utils.CrawlUtils;

/**
 * @author Toaok
 * @version 1.0  2019/3/17.
 */
public class App {

    public static void main(String[] args) {
        App app = new App();
        CrawlUtils.init(app);
        NovelDownload.init();
    }
}
