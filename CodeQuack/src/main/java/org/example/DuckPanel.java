package org.example;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

import com.intellij.openapi.project.Project;

public class DuckPanel extends JPanel {
    private Project project;

    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private DuckService duckService;
    private Icon duckIcon;
    private Icon userIcon;
    private JTextArea textArea;

    private static Map<Project, DuckPanel> instances = new HashMap<>();

    public DuckPanel(Project project) {
        this.project = project;
        instances.put(project, this);
        this.duckService = new DuckService();
        setLayout(new BorderLayout());
        setBackground(UIUtil.getPanelBackground());

        // Load icons
        duckIcon = IconLoader.getIcon("/META-INF/duck.svg", DuckPanel.class);
        // You can use a default user icon or load a custom one
        userIcon = UIManager.getIcon("OptionPane.questionIcon"); // Placeholder, replace with your icon

        // Header with duck icon
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

        // Chat area with messages
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(UIUtil.getPanelBackground());
        chatPanel.setBorder(JBUI.Borders.empty(10));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Input panel with clear separation
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

        inputField = new JTextField();
        inputField.putClientProperty("JTextField.placeholderText", "Describe your problem or ask a question...");
        inputField.setBorder(null);
        inputField.setBackground(UIUtil.getTextFieldBackground());
        inputField.setForeground(UIUtil.getTextFieldForeground());
        inputField.setCaretColor(UIUtil.getTextFieldForeground());
        inputField.setFont(UIUtil.getLabelFont());

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputContainer.add(inputPanel, BorderLayout.CENTER);

        add(inputContainer, BorderLayout.SOUTH);

        // Action on ENTER
        inputField.addActionListener(e -> {
            String userText = inputField.getText();
            if (userText.trim().isEmpty()) return;
            sendMessage(getSelectedCodeFromEditor(),userText);
        });
    }

    private void sendMessage(String code, String userText) {

        addMessage(userText, true);
        inputField.setText("");

        // Show typing indicator
        JPanel typingIndicator = createTypingIndicator();
        chatPanel.add(typingIndicator);
        chatPanel.revalidate();
        scrollToBottom();

        duckService.askTheDuck(code, userText)
                .thenAccept(response -> {
                    SwingUtilities.invokeLater(() -> {
                        chatPanel.remove(typingIndicator);
                        addMessage(response, false);

                        chatPanel.revalidate();
                        chatPanel.repaint();
                    });
                })
                .exceptionally(ex -> {
                    SwingUtilities.invokeLater(() -> {
                        chatPanel.remove(typingIndicator);
                        addMessage("Communication error: " + ex.getMessage(), false);
                    });
                    return null;
                });
    }

    private void addMessage(String text, boolean isUser) {
        JPanel messageContainer = new JPanel(new BorderLayout(8, 0));
        messageContainer.setBackground(UIUtil.getPanelBackground());
        messageContainer.setBorder(JBUI.Borders.empty(4, 8));

        // Profile icon
        JLabel avatarLabel = new JLabel(isUser ? userIcon : duckIcon);
        avatarLabel.setVerticalAlignment(SwingConstants.TOP);

        // Message bubble
        RoundedBubblePanel bubble = createMessageBubble(text, isUser);

        // Add components based on sender
        if (isUser) {
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.setBackground(UIUtil.getPanelBackground());
            rightPanel.add(bubble, BorderLayout.EAST);

            messageContainer.add(rightPanel, BorderLayout.CENTER);
            messageContainer.add(avatarLabel, BorderLayout.EAST);
        } else {
            JPanel leftPanel = new JPanel(new BorderLayout());
            leftPanel.setBackground(UIUtil.getPanelBackground());
            leftPanel.add(bubble, BorderLayout.WEST);

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

        textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setForeground(textColor);
        textArea.setFont(UIUtil.getLabelFont());
        textArea.setBorder(null);

        // Set a maximum width for text wrapping
        int maxTextWidth = 350;
        textArea.setSize(new Dimension(maxTextWidth, Integer.MAX_VALUE));
        Dimension textPreferredSize = textArea.getPreferredSize();

        // Set the text area size
        textArea.setPreferredSize(new Dimension(
                Math.min(maxTextWidth, textPreferredSize.width),
                textPreferredSize.height
        ));

        bubble.add(textArea, BorderLayout.CENTER);

        // Set bubble size including padding
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

    // Custom panel with rounded corners
    private static class RoundedBubblePanel extends JPanel {
        private Color backgroundColor;
        private int cornerRadius;

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

    private String getSelectedCodeFromEditor() {
        FileEditorManager editorManager = FileEditorManager.getInstance(project);

        Editor editor = editorManager.getSelectedTextEditor();

        if (editor == null) {
            return "";
        }

        SelectionModel selectionModel = editor.getSelectionModel();

        String selectedText = selectionModel.getSelectedText();

        if (selectedText != null && !selectedText.isEmpty()) {
            return selectedText;
        } else {
            return "";
        }
    }

    public static DuckPanel getInstanceForProject(Project project) {
        return instances.get(project);
    }

    public void triggerQuestion(String code, String question) {
        // Popuni UI
        textArea.setText(question);

        sendMessage(code, question);
    }
}