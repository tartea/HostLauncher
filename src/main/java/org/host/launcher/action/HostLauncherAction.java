package org.host.launcher.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class HostLauncherAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // 弹出你的插件主界面（这里用 DialogWrapper 举例）
        new HostLauncherDialog(e.getProject()).show();
    }

    // 可选：设置图标、文本等（IDEA UI 中显示）
    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        // 可根据上下文禁用/启用，例如只在 Java 文件中启用
        e.getPresentation().setText("Host Launcher");
        e.getPresentation().setDescription("打开配置host弹框");
    }
}