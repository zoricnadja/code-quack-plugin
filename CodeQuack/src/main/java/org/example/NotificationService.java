package org.example;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

@Service(Service.Level.PROJECT)
public final class NotificationService {
    private static final Logger LOG = Logger.getInstance(NotificationService.class);

    private final @NotNull Project project;

    public NotificationService(@NotNull Project project) {
        this.project = project;
    }

    public static NotificationService getInstance(@NotNull Project project) {
        return project.getService(NotificationService.class);
    }


    public void notifyInfo(@NotNull String message) {
        notify(message, NotificationType.INFORMATION);
    }

    public void notifyWarning(@NotNull String message) {
        notify(message, NotificationType.WARNING);
    }

    public void notifyError(@NotNull String message) {
        notify(message, NotificationType.ERROR);
    }

    public void notifyWelcome() {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(CodeQuackBundle.message("notification.group"))
                .createNotification(
                        CodeQuackBundle.message("plugin.title"),
                        CodeQuackBundle.message("notification.welcome"),
                        NotificationType.INFORMATION)
                .notify(project);
    }

    public void notify(@NotNull String message, @NotNull NotificationType type) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(CodeQuackBundle.message("notification.group"))
                .createNotification(
                        CodeQuackBundle.message("plugin.title"), message, type)
                .notify(project);
    }
}