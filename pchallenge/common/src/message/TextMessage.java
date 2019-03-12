package message;

import com.simplejcode.commons.net.csbase.Message;

public class TextMessage extends Message {

    private String text;

    public TextMessage(Object sender, String text) {
        super(sender);
        setText(text);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }
}
