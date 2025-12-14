package org.example;

import com.intellij.util.ui.JBUI;
import java.awt.*;
import javax.swing.*;

public class TextAreaSizeManager {
    private final ExpandableTextArea textArea;

    public TextAreaSizeManager(ExpandableTextArea textArea) {
        this.textArea = textArea;
    }

    public void updateSize() {
        try {
            FontMetrics fm = textArea.getFontMetrics(textArea.getFont());

            int lineCount = getWrappedLineCount();
            int rows = Math.max(textArea.getMinRows(), lineCount);

            int lineHeight = fm.getHeight();
            int textHeight = lineCount * lineHeight;

            int minHeight = textArea.getMinRows() * lineHeight;

            int horizontalPadding = JBUI.scale(10);

            int availableSpace = minHeight - textHeight;
            int verticalPadding = Math.max(JBUI.scale(4), availableSpace / 2);

            int newHeight = textHeight + (verticalPadding * 2);

            int width = textArea.getWidth();
            if (width <= 0) {
                width = JBUI.scale(200);
            }

            textArea.setMargin(JBUI.insets(verticalPadding, horizontalPadding, verticalPadding, horizontalPadding));

            Dimension newSize = new Dimension(width, newHeight);

            textArea.setPreferredSize(newSize);
            textArea.setMinimumSize(
                    new Dimension(width, minHeight + (verticalPadding * 2)));

            Container parent = textArea.getParent();
            if (parent instanceof JViewport) {
                textArea.setSize(newSize);
            }

            textArea.revalidate();
            textArea.repaint();

            if (parent != null) {
                parent.invalidate();
                parent.validate();

                Container currentParent = parent;
                while (currentParent != null) {
                    currentParent.invalidate();
                    currentParent.validate();
                    currentParent = currentParent.getParent();
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating text area size: " + e.getMessage());
        }
    }

    private int getWrappedLineCount() {
        try {
            if (textArea.getText().isEmpty()) {
                return 0;
            }

            int availableWidth =
                    textArea.getWidth() - textArea.getInsets().left - textArea.getInsets().right;
            if (availableWidth <= 0) {
                availableWidth =
                        JBUI.scale(200) - textArea.getInsets().left - textArea.getInsets().right;
            }

            FontMetrics fm = textArea.getFontMetrics(textArea.getFont());

            int totalLines = 0;
            String[] lines = textArea.getText().split("\n", -1);

            for (String line : lines) {
                if (line.isEmpty()) {
                    totalLines++;
                } else {
                    int lineWidth = fm.stringWidth(line);
                    int wrappedLines =
                            Math.max(1, (int) Math.ceil((double) lineWidth / availableWidth));
                    totalLines += wrappedLines;
                }
            }

            return totalLines;
        } catch (Exception e) {
            return textArea.getLineCount();
        }
    }
}