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

        duckIcon = IconLoader.getIcon("/META-INF/duck.png", DuckPanel.class);
        Icon sendIcon = IconLoader.getIcon("/META-INF/boat_dark.svg", DuckPanel.class);

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
                JBUI.Borders.empty(12, 12, 12, 8)
        ));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(UIUtil.getTextFieldBackground());
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new JBColor(new Color(0xC4C4C4), new Color(0x646464)), 1, true),
                JBUI.Borders.empty(8, 10)
        ));

        inputField = new ExpandableTextArea("Type here...", 1);
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
            public void insertUpdate(DocumentEvent e) { sizeManager.updateSize(); }
            @Override
            public void removeUpdate(DocumentEvent e) { sizeManager.updateSize(); }
            @Override
            public void changedUpdate(DocumentEvent e) { sizeManager.updateSize(); }
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputContainer.add(inputPanel, BorderLayout.CENTER);

        JButton sendButton = new JButton(sendIcon);
        sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(JBUI.Borders.empty(4));
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        sendButton.addActionListener(e -> sendMessage());
        inputContainer.add(sendButton, BorderLayout.EAST);

        add(inputContainer, BorderLayout.SOUTH);

        inputField.addActionListener(e -> sendMessage());
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
        chatPanel.repaint();
        scrollToBottom();
    }

    private RoundedBubblePanel createMessageBubble(String text, boolean isUser) {
        Color bgColor = isUser
                ? new JBColor(new Color(0x2B7DE1), new Color(0x3574C9))
                : new JBColor(new Color(0xEBECF0), new Color(0x3C3F41));

        Color textColor = isUser
                ? new JBColor(Color.WHITE, Color.WHITE)
                : UIUtil.getLabelForeground();

        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setForeground(textColor);
        textArea.setFont(UIUtil.getLabelFont());
        textArea.setBorder(null);
        textArea.setColumns(0);

        RoundedBubblePanel bubble = new RoundedBubblePanel(bgColor, 16, textArea);
        bubble.setBorder(JBUI.Borders.empty(10, 14));

        return bubble;
    }

    private JPanel createTypingIndicator() {
        JPanel messageContainer = new JPanel(new BorderLayout(8, 0));
        messageContainer.setBackground(UIUtil.getPanelBackground());
        messageContainer.setBorder(JBUI.Borders.empty(4, 8));

        JLabel avatarLabel = new JLabel(duckIcon);
        avatarLabel.setVerticalAlignment(SwingConstants.TOP);

        JTextArea textArea = new JTextArea("Duck is thinking...");
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setForeground(UIUtil.getLabelForeground());
        textArea.setFont(UIUtil.getLabelFont());
        textArea.setBorder(null);

        Color bgColor = new JBColor(new Color(0xEBECF0), new Color(0x3C3F41));
        RoundedBubblePanel bubble = new RoundedBubblePanel(bgColor, 16, textArea);
        bubble.setBorder(JBUI.Borders.empty(10, 14));

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
        private final JTextArea textArea;

        public RoundedBubblePanel(Color bgColor, int radius, JTextArea textArea) {
            super(new BorderLayout());
            this.backgroundColor = bgColor;
            this.cornerRadius = radius;
            this.textArea = textArea;
            setOpaque(false);
            add(textArea, BorderLayout.CENTER);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension dim = super.getPreferredSize();
            Container parent = getParent();

            if (parent == null) {
                return dim;
            }

            int availableWidth = parent.getWidth();
            if (availableWidth <= 0) availableWidth = 300;

            int maxBubbleWidth = (int) (availableWidth * 0.85);
            if (maxBubbleWidth < 100) maxBubbleWidth = 200;

            Insets insets = getInsets();
            int contentWidthAvailable = maxBubbleWidth - insets.left - insets.right;

            textArea.setSize(contentWidthAvailable, Short.MAX_VALUE);

            Dimension textPrefSize = textArea.getPreferredSize();

            int finalContentWidth = Math.min(contentWidthAvailable, textPrefSize.width);

            return new Dimension(
                    finalContentWidth + insets.left + insets.right,
                    textPrefSize.height + insets.top + insets.bottom
            );
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