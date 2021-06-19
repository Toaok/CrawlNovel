package indi.toaok.main;


import indi.toaok.crawl.CrawlNovelFromHtml;
import indi.toaok.crawl.CrawlNovelFromHtmlImp;
import indi.toaok.log.ConsoleTextArea;
import indi.toaok.main.layoutmanager.VerticalFlowLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * @author TOAOK
 * @version 1.0  2018/1/8.
 */
class MainFrame extends JFrame {

    private CrawlNovelFromHtml crawlNovelFromHtml;

    private final static int DEFAULT_WIDTH = 600;
    private final static int DEFAULT_HEIGHT = 400;
    private final static int DEFAULT_MAIN_HEIGHT = DEFAULT_HEIGHT / 12;
    private final static int DEFAULT_LOG_HEIGHT = DEFAULT_HEIGHT / 12*7;

    MainFrame() throws HeadlessException {
        init();
    }

    private void init() {
        initFrame();
        initMainView();
        initLogView();
    }

    private void initFrame() {
        //设置窗口属性
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP));
        setResizable(false);

        //使窗口居中
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

        setBounds((int) ((dimension.width - DEFAULT_WIDTH) * 0.5), (int) ((dimension.height - DEFAULT_HEIGHT) * 0.4), DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private void initMainView() {
        //主画板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new VerticalFlowLayout(FlowLayout.LEFT));

        //预留章节数行
        JPanel additionalChapterPanel = new JPanel();
        additionalChapterPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_MAIN_HEIGHT));
        additionalChapterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        additionalChapterPanel.add(new JLabel("预留章节数:"));
        final JTextField additionalChapterValue = new JTextField();
        int additionalChapterWidth = DEFAULT_WIDTH / 5;
        additionalChapterValue.setPreferredSize(new Dimension(additionalChapterWidth, DEFAULT_MAIN_HEIGHT));
        additionalChapterPanel.add(additionalChapterValue);

        //下载地址行
        JPanel downloadUrlPanel = new JPanel();
        downloadUrlPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_MAIN_HEIGHT));
        downloadUrlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        downloadUrlPanel.add(new JLabel("下载地址:"));
        final JTextField downloadUrlValue = new JTextField();
        downloadUrlValue.setPreferredSize(new Dimension(DEFAULT_WIDTH * 7 / 10, DEFAULT_MAIN_HEIGHT));
        downloadUrlPanel.add(downloadUrlValue);

        //下载按钮行
        JPanel downloadButtonPanel = new JPanel();
        downloadButtonPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_MAIN_HEIGHT));
        downloadButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton downloadButton = new JButton();
        downloadButton.setText("下载");
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = downloadUrlValue.getText();
                int additionalChapterNumber = 0;
                try {
                    additionalChapterNumber = Integer.parseInt(additionalChapterValue.getText());
                } catch (NumberFormatException numberFormatException) {
                    numberFormatException.printStackTrace();
                }
                if (url != null && !url.equals("")) {
                    System.out.println("下载链接: " + url);
                    System.out.println("预留章节数:" + additionalChapterNumber);
                    crawlNovelFromHtml = new CrawlNovelFromHtmlImp.Builder()
                            .url(url)
                            .additionalChapterNumber(additionalChapterNumber)
                            .builder();
                    crawlNovelFromHtml.download();
                } else {
                    System.out.println("请输入小说的主页url!");
                }

            }
        });
        downloadButtonPanel.add(downloadButton);

        mainPanel.add(additionalChapterPanel);
        mainPanel.add(downloadUrlPanel);
        mainPanel.add(downloadButtonPanel);

        this.add(mainPanel);
    }

    private void initLogView() {
        JPanel logPanel = new JPanel();
        logPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH,DEFAULT_LOG_HEIGHT));
        logPanel.setLayout(new BorderLayout());
        ConsoleTextArea logTextArea = null;
        try {
            logTextArea = new ConsoleTextArea();
        }
        catch(IOException e) {
            System.err.println(
                    "不能创建LoopedStreams：" + e);
            System.exit(1);
        }
        logTextArea.setFont(Font.decode("monospaced"));
//        logTextArea.setPreferredSize(new Dimension(DEFAULT_WIDTH,DEFAULT_LOG_HEIGHT));
        logPanel.add(new JScrollPane(logTextArea),BorderLayout.CENTER);
        this.add(logPanel);

    }


}
