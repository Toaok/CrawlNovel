package indi.main;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;

class IntegerDocument extends PlainDocument {

    public void insertString(int offset, String s,
                             AttributeSet attributeSet) throws BadLocationException {
        try {
            Integer.parseInt(s);
        } catch (Exception ex) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        super.insertString(offset, s, attributeSet);

    }

}