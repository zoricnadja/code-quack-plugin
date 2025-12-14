package org.example;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;

public class ExpandableTextArea extends JBTextArea {

    private final String placeholder;
    private final List<ActionListener> actionListeners = new ArrayList<>();
    private final int minRows;
    private final int rowHeight;

    public static final String SUBMIT = "submit";
    public static final String NEWLINE = "newline";

    public ExpandableTextArea(String placeholder,int minRows) {
        super(minRows, 0);
        this.placeholder = placeholder;
        this.minRows = minRows;
        this.rowHeight = getFontMetrics(getFont()).getHeight();

        TextAreaSizeManager sizeManager = new TextAreaSizeManager(this);

        setOpaque(true);
        setAutoscrolls(true);
        setLineWrap(true);
        setWrapStyleWord(true);

        Border paddingBorder = JBUI.Borders.empty(10);
        setBorder(paddingBorder);

        setBackground(JBColor.background());

        setMargin(JBUI.insets(4));
        setFont(getFont().deriveFont((float) JBUI.scale(13)));
        setupKeyBindings(this);

        sizeManager.updateSize();
    }

    public List<ActionListener> getActionListeners() {
        return actionListeners;
    }

    public int getMinRows() {
        return minRows;
    }

    @Override
    public int getRowHeight() {
        return rowHeight;
    }

    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    public void removeActionListener(ActionListener listener) {
        actionListeners.remove(listener);
    }

    void notifyActionListeners(String text) {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, text);
        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(event);
        }
    }

    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);

        if (placeholder == null || placeholder.isEmpty() || !this.getText().isEmpty()) {
            return;
        }

        final Graphics2D g = (Graphics2D) pG;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(com.intellij.util.ui.UIUtil.getInactiveTextColor());

        g.setFont(getFont());

        Insets insets = getInsets();
        FontMetrics fm = g.getFontMetrics();

        int x = insets.left;
        int y = insets.top + fm.getAscent();

        g.drawString(placeholder, x, y);
    }

    private void setupKeyBindings(ExpandableTextArea textArea) {
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = textArea.getActionMap();

        KeyStroke shiftEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

        inputMap.put(enter, SUBMIT);
        inputMap.put(shiftEnter, NEWLINE);

        actionMap.put(
                SUBMIT,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!textArea.getText().trim().isEmpty()) {
                            textArea.notifyActionListeners(textArea.getText());
                        }
                    }
                });

        actionMap.put(
                NEWLINE,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        textArea.replaceSelection("\n");
                    }
                });
    }
}

