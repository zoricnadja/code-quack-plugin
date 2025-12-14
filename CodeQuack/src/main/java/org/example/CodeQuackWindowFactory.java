package org.example;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class CodeQuackWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        DuckPanel duckPanel = new DuckPanel(project);
        Content content = ContentFactory.getInstance().createContent(duckPanel, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
