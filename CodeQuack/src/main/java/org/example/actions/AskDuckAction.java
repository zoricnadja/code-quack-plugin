package org.example.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.example.DuckPanel;
import org.example.DuckService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AskDuckAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        if (project == null || editor == null) {
            return;
        }

        String selectedText = editor.getSelectionModel().getSelectedText();
        if (selectedText == null || selectedText.isEmpty()) {
            selectedText = editor.getDocument().getText();
        }

        String userQuestion = Messages.showInputDialog(
                project,
                "What do you want to ask the Code Quack?",
                "Ask Code Quack",
                Messages.getQuestionIcon(),
                "What's wrong with my code?",
                null
        );

        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            return;
        }

        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("CodeQuack");

        if (toolWindow != null) {
            String finalCode = selectedText;

            toolWindow.show(() -> {
            });


            DuckPanel windowInstance = DuckPanel.getInstanceForProject(project);
            if (windowInstance != null) {
                windowInstance.triggerQuestion(finalCode, userQuestion);
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(project != null && editor != null && editor.getSelectionModel().hasSelection());
    }
}
