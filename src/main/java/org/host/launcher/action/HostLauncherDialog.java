package org.host.launcher.action;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.host.launcher.ui.ConfigPreviewPanel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;


/**
 * 主插件对话框：展示 Hosts 配置预览面板
 */
public class HostLauncherDialog extends DialogWrapper {

    private final Project project;
    private ConfigPreviewPanel configPreviewPanel;

    public HostLauncherDialog(@Nullable Project project) {
        super(project, true); // modal = true
        this.project = project;
        setTitle("Host Launcher - 配置管理");

        // 👇 关键：禁止用户调整窗口大小
        setResizable(false);


        // 👇 关键：设置固定窗口尺寸（根据你的 UI 内容调整）
        setSize(800,450); // 宽 × 高

        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        if (project == null) {
            JLabel label = new JLabel("❌ 未检测到有效项目，请打开一个项目后再试。", SwingConstants.CENTER);
            label.setFont(new Font("Dialog", Font.ITALIC, 14));
            label.setForeground(Color.RED);
            return label;
        }

        // 创建配置预览面板（已实现 Disposable）
        configPreviewPanel = new ConfigPreviewPanel(project);

        // 设置背景色以增强视觉一致性（可选）
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.add(configPreviewPanel, BorderLayout.CENTER);

        return container;
    }
    @Override
    protected Action[] createActions() {
        // 返回空数组，不创建任何按钮
        return new Action[0];
    }

    // 可选：如果你需要完全控制按钮的创建
    @Override
    protected JComponent createSouthPanel() {
        // 返回null或不重写此方法，将不会创建底部按钮面板
        return null;
    }

    /**
     * 安全释放资源（关键！）
     */
    private void disposeInternal() {
        if (configPreviewPanel != null) {
            configPreviewPanel.dispose(); // 调用 ConfigPreviewPanel 的 dispose()
        }
    }

    /**
     * 重写 dispose 方法，确保父类和子资源都被清理
     */
    @Override
    public void dispose() {
        disposeInternal();
        super.dispose();
    }


}