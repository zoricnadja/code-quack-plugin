package org.example;

import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class DuckPanel extends JPanel {

    private final JPanel chatPanel;
    private final JScrollPane scrollPane;
    private final ExpandableTextArea inputField;
    private final TextAreaSizeManager sizeManager;
    private final DuckService duckService;
    private final Icon duckIcon;

    public DuckPanel() {
        this.duckService = new DuckService();
        setLayout(new BorderLayout());
        setBackground(UIUtil.getPanelBackground());

        duckIcon = IconLoader.getIcon("/META-INF/duck.svg", DuckPanel.class);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(UIUtil.getPanelBackground());
        headerPanel.setBorder(JBUI.Borders.empty(8));

        JLabel iconLabel = new JLabel(duckIcon);
        JLabel titleLabel = new JLabel("Rubber Duck Debugging");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createHorizontalStrut(8));
        headerPanel.add(titleLabel);

        add(headerPanel, BorderLayout.NORTH);

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(UIUtil.getPanelBackground());
        chatPanel.setBorder(JBUI.Borders.empty(10));

        scrollPane = new JBScrollPane(chatPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputContainer = new JPanel(new BorderLayout());
        inputContainer.setBackground(UIUtil.getPanelBackground());
        inputContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new JBColor(new Color(0xC4C4C4), new Color(0x323232))),
                JBUI.Borders.empty(12)
        ));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(UIUtil.getTextFieldBackground());
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new JBColor(new Color(0xC4C4C4), new Color(0x646464)), 1, true),
                JBUI.Borders.empty(8, 10)
        ));

        inputField = new ExpandableTextArea(CodeQuackBundle.message("area.placeholder"), 12, 12, 1);
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);
        inputField.setBorder(null);
        inputField.setBackground(UIUtil.getTextFieldBackground());
        inputField.setForeground(UIUtil.getTextFieldForeground());
        inputField.setCaretColor(UIUtil.getTextFieldForeground());
        inputField.setFont(UIUtil.getLabelFont());

        sizeManager = new TextAreaSizeManager(inputField);

        inputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                sizeManager.updateSize();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                sizeManager.updateSize();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                sizeManager.updateSize();
            }
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputContainer.add(inputPanel, BorderLayout.CENTER);

        add(inputContainer, BorderLayout.SOUTH);
        inputField.addActionListener(e -> sendMessage());

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());
        inputContainer.add(sendButton, BorderLayout.EAST);
    }

    private void sendMessage() {
        String userText = inputField.getText();
        if (userText.trim().isEmpty()) return;

        addMessage(userText, true);
        inputField.setText("");
        sizeManager.updateSize();

        JPanel typingIndicator = createTypingIndicator();
        chatPanel.add(typingIndicator);
        chatPanel.revalidate();
        scrollToBottom();

        new Thread(() -> {
            String response = duckService.askTheDuck(userText);

            SwingUtilities.invokeLater(() -> {
                chatPanel.remove(typingIndicator);
                addMessage(response, false);
            });
        }).start();
    }

    private void addMessage(String text, boolean isUser) {
        JPanel messageContainer = new JPanel(new BorderLayout(8, 0));
        messageContainer.setBackground(UIUtil.getPanelBackground());
        messageContainer.setBorder(JBUI.Borders.empty(4, 8));

        RoundedBubblePanel bubble = createMessageBubble(text, isUser);

        if (isUser) {
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.setBackground(UIUtil.getPanelBackground());
            rightPanel.add(bubble, BorderLayout.EAST);

            messageContainer.add(rightPanel, BorderLayout.CENTER);
        } else {
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.setBackground(UIUtil.getPanelBackground());
            leftPanel.add(bubble, BorderLayout.WEST);

            JLabel avatarLabel = new JLabel(duckIcon);
            avatarLabel.setVerticalAlignment(SwingConstants.TOP);
            messageContainer.add(avatarLabel, BorderLayout.WEST);
            messageContainer.add(leftPanel, BorderLayout.CENTER);
        }

        chatPanel.add(messageContainer);
        chatPanel.revalidate();
        scrollToBottom();
    }

    private RoundedBubblePanel createMessageBubble(String text, boolean isUser) {
        Color bgColor = isUser
                ? new JBColor(new Color(0x2B7DE1), new Color(0x3574C9))
                : new JBColor(new Color(0xEBECF0), new Color(0x3C3F41));

        Color textColor = isUser
                ? new JBColor(Color.WHITE, Color.WHITE)
                : UIUtil.getLabelForeground();

        RoundedBubblePanel bubble = new RoundedBubblePanel(bgColor, 16);
        bubble.setLayout(new BorderLayout());

        int horizontalPadding = 14;
        int verticalPadding = 10;
        bubble.setBorder(JBUI.Borders.empty(verticalPadding, horizontalPadding));

        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setForeground(textColor);
        textArea.setFont(UIUtil.getLabelFont());
        textArea.setBorder(null);

        int maxTextWidth = 350;
        textArea.setSize(new Dimension(maxTextWidth, Integer.MAX_VALUE));
        Dimension textPreferredSize = textArea.getPreferredSize();

        textArea.setPreferredSize(new Dimension(
                Math.min(maxTextWidth, textPreferredSize.width),
                textPreferredSize.height
        ));

        bubble.add(textArea, BorderLayout.CENTER);

        int bubbleWidth = textArea.getPreferredSize().width + (horizontalPadding * 2);
        int bubbleHeight = textArea.getPreferredSize().height + (verticalPadding * 2);
        bubble.setPreferredSize(new Dimension(bubbleWidth, bubbleHeight));
        bubble.setMaximumSize(new Dimension(bubbleWidth, bubbleHeight));

        return bubble;
    }

    private JPanel createTypingIndicator() {
        JPanel messageContainer = new JPanel(new BorderLayout(8, 0));
        messageContainer.setBackground(UIUtil.getPanelBackground());
        messageContainer.setBorder(JBUI.Borders.empty(4, 8));

        JLabel avatarLabel = new JLabel(duckIcon);
        avatarLabel.setVerticalAlignment(SwingConstants.TOP);

        Color bgColor = new JBColor(new Color(0xEBECF0), new Color(0x3C3F41));
        RoundedBubblePanel bubble = new RoundedBubblePanel(bgColor, 16);
        bubble.setBorder(JBUI.Borders.empty(10, 14));

        JLabel label = new JLabel("Duck is thinking...");
        label.setForeground(UIUtil.getLabelForeground());
        bubble.add(label);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(UIUtil.getPanelBackground());
        leftPanel.add(bubble, BorderLayout.WEST);

        messageContainer.add(avatarLabel, BorderLayout.WEST);
        messageContainer.add(leftPanel, BorderLayout.CENTER);

        return messageContainer;
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private static class RoundedBubblePanel extends JPanel {
        private final Color backgroundColor;
        private final int cornerRadius;

        public RoundedBubblePanel(Color bgColor, int radius) {
            super();
            this.backgroundColor = bgColor;
            this.cornerRadius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}