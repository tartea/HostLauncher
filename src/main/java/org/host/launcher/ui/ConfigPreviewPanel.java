package org.host.launcher.ui;


import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import org.host.launcher.action.FileContentDialog;
import org.host.launcher.dto.Item;
import org.host.launcher.host.HostsFileWriter;
import org.host.launcher.state.MultiTextConfigState;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;

public class ConfigPreviewPanel extends JPanel implements Disposable {

    private final MultiTextConfigState configState;
    private final ButtonGroup buttonGroup;
    private final JPanel radioPanel; // 横向平铺容器
    private final Project project; // 保存项目引用用于通知

    public ConfigPreviewPanel(@NotNull Project project) {
        super(new BorderLayout());
        this.project = project;
        this.configState = ServiceManager.getService(MultiTextConfigState.class);
        this.buttonGroup = new ButtonGroup();

        // ==================== 顶部按钮区域 ====================
        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        // Clear Host按钮
        JButton clearButton = createStyledButton("Clear Host", new Color(70, 130, 180));
        clearButton.addActionListener(e -> {
            configState.setSelectedItemName(null);
            refreshRadioList();
        });

        // 读取文件按钮
        JButton readFileButton = createStyledButton("查看host内容", new Color(60, 179, 113));
        readFileButton.addActionListener(e -> displayFileContent());

        topButtonPanel.add(clearButton);
        topButtonPanel.add(readFileButton);

        add(topButtonPanel, BorderLayout.NORTH);

        // ==================== 横向按钮容器 ====================
        radioPanel = new JPanel();
        radioPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 8, 8));
        radioPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(radioPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        add(scrollPane, BorderLayout.CENTER);

        // 初始化加载
        refreshRadioList();
        // 注册销毁钩子
        Disposer.register(project, this);
    }


    // 初始化
    private void refreshRadioList() {
        radioPanel.removeAll(); // 清空旧按钮

        List<Item> items = configState.getItems();
        if (items.isEmpty()) {
            JLabel emptyLabel = new JLabel("暂无配置项，请前往 tools > HostLauncher");
            emptyLabel.setFont(new Font("Dialog", Font.ITALIC, 12));
            emptyLabel.setForeground(Color.GRAY);
            radioPanel.add(emptyLabel);
            revalidate();
            return;
        }

        String selectedItemName = configState.getSelectedItemName();

        JRadioButton clickButton = null;

        for (Item item : items) {
            String name = item.name != null ? item.name : "未知项";
            JRadioButton rb = new JRadioButton(name);
            rb.setFont(new Font("Monospaced", Font.PLAIN, 13));
            rb.setFocusPainted(false);
            rb.setContentAreaFilled(false);
            rb.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

            if (Objects.nonNull(selectedItemName) && name.equals(selectedItemName)) {
                clickButton = rb;
            }

            // 点击切换选中/取消，支持“零选中”
            rb.addActionListener(e -> {
                if (rb.isSelected()) {

                    rb.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                            BorderFactory.createEmptyBorder(4, 8, 4, 8)
                    ));
                    rb.setForeground(Color.WHITE);

                    configState.setSelectedItemName(rb.getText());
                }
            });
            buttonGroup.add(rb);
            radioPanel.add(rb);
        }
        revalidate();
        repaint();
        if (Objects.nonNull(clickButton)) {
            clickButton.doClick();
        }
    }

    //实现 Disposable 接口，安全释放资源
    @Override
    public void dispose() {

    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Dialog", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);

        // 添加悬停效果
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setCursor(Cursor.getDefaultCursor());
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void displayFileContent() {
        // 创建内容显示对话框
        ApplicationManager.getApplication().invokeLater(() -> {
            FileContentDialog dialog = new FileContentDialog(project, HostsFileWriter.readCurrentHosts());
            dialog.show();
        });
    }
}