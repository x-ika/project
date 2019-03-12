package gui;

import com.simplejcode.commons.net.csbase.*;
import com.simplejcode.commons.net.sockets.SocketConnection;
import com.simplejcode.commons.gui.ChatPanel;
import message.TextMessage;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import api.Client;

public class ChatWindow extends BaseListener {

    public static final String SEND_MESSAGE = "Send Message";
    public static final String ATTACH_FILE = "Attach File";


    private ChatPanel chatPanel;

    public ChatWindow(SocketConnection<Message> socketConnection, String title) {
        super(socketConnection, title);
    }

    private JPanel createContent() {
        return chatPanel = new ChatPanel();
    }

    public void writeText(String s, Color color) {
        chatPanel.writeText(s, color);
    }

    public void sendFile(File file) {
        chatPanel.sendFile(file);
    }

    public void clearHistory() {
        chatPanel.clearHistory();
    }

    public JButton getAttachFileButton() {
        return chatPanel.getAttachFileButton();
    }

    public JButton getSendMessageButton() {
        return chatPanel.getSendMessageButton();
    }

    //-----------------------------------------------------------------------------------

    public void sendMessage() {
        if (socketConnection != null) {
            socketConnection.sendMessage(new TextMessage(
                    this, Client.getInstance().getTeamName() + ": " + chatPanel.getInputText() + '\n'));
        }
    }

    public void load() {
        loadContent(createContent());
    }

    //-----------------------------------------------------------------------------------

    public void messageSent(SocketConnection<Message> source, Message message) {
        if (message.getSender() != this || !(message instanceof TextMessage)) {
            return;
        }
        chatPanel.setInputText("");
        chatPanel.writeText(((TextMessage) message).getText(), Color.black);
    }

    public void messageReceived(SocketConnection<Message> source, Message message) {
        if (message instanceof TextMessage) {
            chatPanel.writeText(((TextMessage) message).getText(), Color.blue);
        }
    }

}
