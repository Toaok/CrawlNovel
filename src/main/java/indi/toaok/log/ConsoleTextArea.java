package indi.toaok.log;

import indi.toaok.io.LoopedStreams;
import indi.toaok.utils.CrawlUtils;
import indi.toaok.utils.GeneratingTXTDocuments;

import javax.swing.*;
import javax.swing.text.Document;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleTextArea extends JTextArea {
    GeneratingTXTDocuments mLogTxt;
    public ConsoleTextArea(InputStream[] inStreams) {
        for (int i = 0; i < inStreams.length; ++i) {
            startConsoleReaderThread(inStreams[i]);
        }
    } // ConsoleTextArea()
    public ConsoleTextArea() throws IOException {
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        mLogTxt =new GeneratingTXTDocuments(CrawlUtils.getDefaultPath(),"log-"+format.format(new Date(System.currentTimeMillis())));
        final LoopedStreams ls = new LoopedStreams();
        // 重定向System.out和System.err
        PrintStream ps = new PrintStream(ls.getOutputStream());
        System.setOut(ps);
        System.setErr(ps);
        startConsoleReaderThread(ls.getInputStream());
    } // ConsoleTextArea()
    private void startConsoleReaderThread(
        InputStream inStream) {
        final BufferedReader br =
            new BufferedReader(new InputStreamReader(inStream));
        new Thread(new Runnable() {
            public void run() {
                StringBuffer sb = new StringBuffer();
                try {
                    String s;
                    Document doc = getDocument();
                    while((s = br.readLine()) != null) {
                        boolean caretAtEnd = false;
                        caretAtEnd = getCaretPosition() == doc.getLength();
                        sb.setLength(0);
                        String logStr=sb.append(s).append('\n').toString();
                        append(logStr);
                        if(mLogTxt !=null){
                            mLogTxt.writeDate(logStr);
                        }
                        if(caretAtEnd)
                            setCaretPosition(doc.getLength());
                    }
                }
                catch(IOException e) {
                    JOptionPane.showMessageDialog(null,
                        "从BufferedReader读取错误：" + e);
                    System.exit(1);
                }
            }
        }).start();
    } // startConsoleReaderThread()
} // ConsoleTextArea