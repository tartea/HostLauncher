package org.host.launcher.action;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class FileContentDialog extends DialogWrapper {
    private final String content;

    public FileContentDialog(@Nullable Project project, String content) {
        super(project, true);
        this.content = content;
        setTitle("Hosts文件内容");
        setSize(600, 400);
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 内容显示区域
        JTextArea contentArea = new JTextArea(content);
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    @Override
    protected Action[] createActions() {
        return new Action[]{getOKAction()};
    }
}
