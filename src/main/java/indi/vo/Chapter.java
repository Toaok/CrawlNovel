package indi.vo;

/**
 * @author TOAOK
 * @version 1.0  2018/1/1.
 */
public class Chapter {
    private String chapterName;
    private String content;
    private String url;
    private boolean isSaved;

    public Chapter() {
        isSaved=false;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chapter chapter = (Chapter) o;

        if (isSaved != chapter.isSaved) return false;
        if (!chapterName.equals(chapter.chapterName)) return false;
        if (!content.equals(chapter.content)) return false;
        return url.equals(chapter.url);
    }

    @Override
    public int hashCode() {
        int result = chapterName.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + url.hashCode();
        result = 31 * result + (isSaved ? 1 : 0);
        return result;
    }
}
