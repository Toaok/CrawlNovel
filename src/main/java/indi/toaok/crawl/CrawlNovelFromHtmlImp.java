package indi.toaok.crawl;


import indi.toaok.utils.CrawlUtils;
import indi.toaok.utils.GeneratingTXTDocuments;
import indi.toaok.utils.ThreadPoolManager;
import indi.toaok.vo.Chapter;
import indi.toaok.vo.Site;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static indi.toaok.utils.CrawlUtils.*;

/**
 * Created by TOAOK on 2017/9/20.
 */

public class CrawlNovelFromHtmlImp implements CrawlNovelFromHtml {

    private static final int ONCE_SIZE = 200;
    private int chapterTaskCount;//已下载章节数
    private Document main;

    private final String url;//访问小说主页面的url

    private final Lock mLock;
    private final List<Chapter> mChapters;//章节信息对象数组
    private final List<Condition> mConditions;//线程锁
    private final int additionalChapter;//无效章节
    private int mCountChapter;//总章节数
    private Map<String, String> cookies;//请求cookie

    private GeneratingTXTDocuments mDocuments;//生成txt文件
    private String mCharset;//字符编码


    CrawlNovelFromHtmlImp(Builder builder) {

        this.url = builder.url;
        this.additionalChapter = builder.additionalChapterNumber;

//        rootUrl = getRootUrl(url);
        mLock = new ReentrantLock();
        mConditions = new ArrayList<>();//创建一个lock数组，为每个章节写入时加锁
        mChapters = new ArrayList<>();//创建一个章节数组，来存储所有章节
        chapterTaskCount = 0;//多线程访问章节
    }

    public static class Builder {

        String url;
        int additionalChapterNumber;

        public Builder url(String url) {
            if (url == null || url.equals("")) {
                throw new NoSuchElementException();
            }
            this.url = url;
            return this;
        }

        public Builder additionalChapterNumber(int additionalChapter) {
            this.additionalChapterNumber = additionalChapter;
            return this;
        }

        public CrawlNovelFromHtml builder() {
            return new CrawlNovelFromHtmlImp(this);
        }
    }


    /**
     * 解析html页面，获取章节目录和每个章节的url
     */
    private void parseHtml() {
        System.out.println("解析html,获取小说章节信息...");
        Elements chapters = null;
        for (String cssQuery : Site.CHAPTERS_LIST_CSS_QUERY) {
            chapters = main.select(cssQuery);
            if (chapters.size() > 0) {
                break;
            }
        }
        if (chapters == null || additionalChapter > chapters.size()) return;
        //实际章节数
        mCountChapter = chapters.size() - additionalChapter;

        for (int i = 0; i < mCountChapter; ++i) {

            Element e = chapters.get(i + additionalChapter);
            String chapterName = e.text();

            //为每个章节创建一个锁
            Condition condition = mLock.newCondition();
            mConditions.add(condition);
            //创建一个章节对象
            Chapter chapter = new Chapter();
            //章节名
            chapter.setChapterName(chapterName);
            //获取章节的url
            String chapterUrl = e.attr("href");
            chapter.setUrl(getChapterUrl(chapterUrl));
            //加入到章节信息列表
            mChapters.add(chapter);
            //获取章节类容
            ThreadPoolManager.getInstance().execute(new Task(i));
        }
        System.out.println("解析html,获取小说章节信息完成");
    }

    /**
     * 根据章节信息数组，重定向到每章获取每章内容
     *
     * @param number 章节数
     * @throws IOException OI异常
     */
    public void dispatcher(int number) throws IOException {
        StringBuilder chapterBuffer = new StringBuilder();
        //添加章节名
        chapterBuffer.append(mChapters.get(number).getChapterName());
        chapterBuffer.append(System.getProperty("line.separator"));
        chapterBuffer.append("\t");

        //爬取章节信息

        String chapterUrl = mChapters.get(number).getUrl();
        boolean hasNextPage =false;
        Document currentDoc = null;
        while (currentDoc == null || hasNextPage) {
            try {
                currentDoc = (mCharset != null && !mCharset.equals("")) ?
                        Jsoup.parse(new URL(chapterUrl).openStream(), mCharset, chapterUrl)
                        : Jsoup.parse(new URL(chapterUrl), 6 * 1000);
            } catch (Exception e) {
                //爬取章节信息
                if (cookies != null && cookies.size() > 0) {
                    currentDoc = Jsoup.connect(chapterUrl)
                            .method(Connection.Method.GET)
                            .headers(CrawlUtils.getHeader())
                            .userAgent(USER_AGENT[2])
                            .cookies(cookies)
                            .timeout(TIME_OUT)
                            .ignoreContentType(true)
                            .get();
                } else {
                    currentDoc = Jsoup.connect(chapterUrl)
                            .method(Connection.Method.GET)
                            .headers(CrawlUtils.getHeader())
                            .userAgent(USER_AGENT[0])
                            .timeout(TIME_OUT)
                            .ignoreContentType(true)
                            .get();
                }
            }
            Elements e = null;
            for (String cssQuery : Site.CHAPTER_CONTENT_CSS_QUERY) {
                e = currentDoc.select(cssQuery);
                if (!e.isEmpty()) {
                    break;
                }
            }
            if (e == null || e.isEmpty()) {
                System.out.println("章节内容获取失败!");
            }
            //获取章节正文，并格式化。
            String chapterContent = CrawlUtils.contentFormat(e.text());
            chapterBuffer.append(chapterContent);
            String nextPageUrl = nextUrl(currentDoc);
            hasNextPage = (nextPageUrl != null && !nextPageUrl.equals(""));
            chapterUrl = getChapterUrl(nextPageUrl);
        }
        chapterBuffer.append(System.getProperty("line.separator"));
        //获取章节信息对象
        Chapter chapter = mChapters.get(number);
        //将章节正文添加到章节信对象中
        chapter.setContent(chapterBuffer.toString());
    }


    public String nextUrl(Document doc) {
        String nextUrl = null;
        Element element = null;

        for (String cssQuery : Site.CHAPTER_NEXT_PAGE_CSS_QUERY) {
            element = doc.select(cssQuery).first();
            if (element != null) {
                break;
            }
        }
        if (element != null) {
            String nextText = element.text();
            if (nextText.trim().equals("下一页")) {
                nextUrl = element.attr("href");
            }
        }
        return nextUrl;
    }


    @Override
    public String getNovelName() {
        String novelName = "";

        Elements elements = null;

        Element meta = main.select("meta[property=og:novel:book_name]").first();
        if (meta != null) {
            novelName = meta.attr("content");
        }
        if ("".equals(novelName)) {
            for (String cssQuery : Site.NAME_CSS_QUERY) {
                elements = main.select(cssQuery);
                if (elements.size() > 0) {
                    break;
                }
            }
            if (elements != null && elements.size() > 0) {
                novelName = elements.first().text();
            }
        }

        return novelName;
    }

    @Override
    public void download(String filePath) {
        if (filePath == null || filePath.equals("")) {
            filePath = CrawlUtils.getDefaultPath();
        }
        System.out.println("文件路径：" + filePath);
        final String finalFilePath = filePath;
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    parseDocFromJava();
                } catch (Exception e) {
                    try {
                        parseDocFromUserAgent();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }


            private void parseDocFromJava() throws Exception {
                URL url = new URL(CrawlNovelFromHtmlImp.this.url);
                Document doc = Jsoup.parse(url, 6 * 1000);
                Elements e = doc.select(Site.CHARSET_CSS_QUERY);
                Iterator<Element> it = e.iterator();
                while (it.hasNext()) {
                    mCharset = CrawlUtils.matchCharset(it.next().toString());
                    if (Charset.defaultCharset().name().equals(mCharset)) {
                        main = doc;
                        mCharset = "";
                    } else {
                        main = Jsoup.parse(url.openStream(), mCharset, CrawlNovelFromHtmlImp.this.url);
                    }
                }
                mDocuments = new GeneratingTXTDocuments(finalFilePath, getNovelName() + ".txt");
                parseHtml();
            }

            private void parseDocFromUserAgent() throws Exception {
                main = Jsoup.connect(url)
                        .userAgent(USER_AGENT[0])
                        .cookie("Cookie:","jieqiAdminLogin=2; cp_speed_8x=66; cp_fontsize_8x=16; cp_bg_8x=%23E7F4FE; __gads=ID=94543dd6129121cf-22c3cb0ffce40024:T=1697939140:RT=1697942619:S=ALNI_MbOp7UbJa-GVrf-gjV49xqt-mkgNA; __gpi=UID=00000c6cf0cb2419:T=1697939140:RT=1697942619:S=ALNI_MadGr1sZ_RQM1E_lokpxpmPTX-eXw; cf_clearance=r.kaSRBa.tsgip97cSWO1tII358OW_0ntXWrCHqcmRo-1697942641-0-1-66627d49.14685442.12bbc6a1-0.2.1697942641")
                        .timeout(TIME_OUT)
                        .get();
                mDocuments = new GeneratingTXTDocuments(finalFilePath, getNovelName() + ".txt");
                parseHtml();
            }
        });

    }

    @Override
    public void download() {
        if (mDocuments != null) {
            try {
                mDocuments.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        download(CrawlUtils.getDefaultPath());
    }

    private String getChapterUrl(String chapterUrl) {
        if (chapterUrl == null || chapterUrl.equals("") || chapterUrl.startsWith("http") || chapterUrl.startsWith("https")) {
            return chapterUrl;
        } else {
            return getRootUrl(url) + (chapterUrl.contains(getPath(url))||chapterUrl.contains("/") ? chapterUrl : getPath(url) + chapterUrl);
        }
    }


    /**
     * 在该线程中执行的任务是：
     * ->首先通过重定向获取章节内容（这里要进行网络请求，比较耗时）
     * ->然后判断该章节的上一章是否已存储 [是]->开始存储本章节 [否]->等待上一章存储完成
     * 这里好像没什么用
     */
    private class Task implements Runnable {

        private final int number;

        public Task(int number) {
            this.number = number;
        }

        @Override
        public void run() {
            try {
                //重定向到章节内容，获取章节正文
                System.out.println("重定向到章节,获取" + mChapters.get(number).getChapterName() + "内容...");
                dispatcher(number);
                System.out.println("获取" + mChapters.get(number).getChapterName() + "内容完成");
            } catch (IOException e) {
                System.out.println("获取章节:" + mChapters.get(number).getChapterName() + "---异常");
                run();
                return;
            }

            //获取到内容后进行存储
            Chapter chapter = mChapters.get(number);

            mLock.lock();
            try {
                if (number > 0) {
                    if (!mChapters.get(number - 1).isSaved())
                        mConditions.get(number).await();
                }
                //number==0,存储第一章不需要等待
                if (chapter.getContent().length() > 0) {
                    mDocuments.writeDate(chapter.getContent());
                    chapter.setSaved(true);
                }
                if (number < mConditions.size() - 1)
                    mConditions.get(number + 1).signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mLock.unlock();
            }

            chapterTaskCount++;
            //小说下载进度
            float progress = chapterTaskCount / (float) (mCountChapter) * 100;
            if (progress < 100) {
                System.out.printf("已下载: %5.2f%%\r", progress);
            } else if (progress == 100) {
                System.out.printf("已下载: %5.2f%%\n", progress);
            }

            if (chapterTaskCount >= mCountChapter) {
                System.out.println("小说下载成功！！");
                try {
                    mDocuments.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ThreadPoolManager.getInstance().remove(this);
        }
    }
}
