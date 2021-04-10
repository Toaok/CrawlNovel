package indi.crawl;

/**
 * @author TOAOK
 * @version 1.0  2018/1/3.
 */
public interface CrawlNovelFromHtml {
    /**
     * @param filePath 将获取的内容下载到该目录
     */
    String download(String filePath);
    /**
     * 开始下载
     */
    void download();

    /**
     * 获取小说名
     * @return novelName 小说名
     */
    String getNovelName();
}
