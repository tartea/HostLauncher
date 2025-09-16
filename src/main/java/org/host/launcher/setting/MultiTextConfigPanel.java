package org.host.launcher.setting;


import org.host.launcher.state.MultiTextConfigState;
import org.host.launcher.dto.Item;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Objects;

public class MultiTextConfigPanel extends JPanel {

    private final JList<String> leftList;
    private final DefaultListModel<String> listModel;
    private final JTextArea hostTextArea;      // 第一个文本域
    private final JTextArea javaTextArea;     // 第二个文本域
    private final JButton addButton;
    private final JButton deleteButton;
    private final JButton confirmButton;
    private final MultiTextConfigState configState;

    // 编辑相关
    private JTextField editField;
    private int editingIndex = -1;

    public MultiTextConfigPanel(MultiTextConfigState configState) {
        this.configState = configState;

        setLayout(new BorderLayout());

        // ==================== 左侧面板：列表 + 操作按钮 ====================
        JPanel leftPanel = new JPanel(new BorderLayout());
        listModel = new DefaultListModel<>();
        leftList = new JList<>(listModel);
        leftList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        leftList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leftList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane leftScroll = new JScrollPane(leftList);
        leftScroll.setPreferredSize(new Dimension(200, 400));

        // 创建操作按钮行：新增 和 删除
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        addButton = new JButton("新增");
        addButton.addActionListener(e -> addNewItem());

        deleteButton = new JButton("删除");
        deleteButton.addActionListener(e -> deleteSelectedItem());
        deleteButton.setEnabled(false); // 默认禁用，无选中项时不可点

        buttonRow.add(addButton);
        buttonRow.add(deleteButton);

        leftPanel.add(leftScroll, BorderLayout.CENTER);
        leftPanel.add(buttonRow, BorderLayout.SOUTH);

        // 监听按钮变化
        listenerButton();


        // ==================== 右侧面板：双文本域 + 确定按钮 ====================
        JPanel rightPanel = new JPanel(new BorderLayout());
        hostTextArea = new JTextArea();
        hostTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        hostTextArea.setLineWrap(true);
        hostTextArea.setWrapStyleWord(true);
        hostTextArea.setBorder(BorderFactory.createTitledBorder("Host内容"));

        javaTextArea = new JTextArea();
        javaTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        javaTextArea.setLineWrap(true);
        javaTextArea.setWrapStyleWord(true);
        javaTextArea.setBorder(BorderFactory.createTitledBorder("Java启动参数"));

        JPanel textAreasPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        textAreasPanel.add(new JScrollPane(hostTextArea));
        textAreasPanel.add(new JScrollPane(javaTextArea));

        confirmButton = new JButton("确定");
        confirmButton.addActionListener(e -> saveCurrentItem());

        rightPanel.add(textAreasPanel, BorderLayout.CENTER);
        rightPanel.add(confirmButton, BorderLayout.SOUTH);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 主布局
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // 加载配置数据
        reloadListFromState();
    }

    /**
     * 监听左侧按钮的变化
     */
    private void listenerButton() {

        // 监听列表选中变化，动态启用/禁用删除按钮
        leftList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // 删除按钮
                boolean hasSelection = leftList.getSelectedIndex() != -1;
                deleteButton.setEnabled(hasSelection);

                // 单击加载内容
                leftList.setSelectedIndex(leftList.getSelectedIndex());
                loadSelectedItem(leftList.getSelectedIndex());
            }
        });

        // 双击或回车/F2 编辑名称
        leftList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = leftList.getSelectedIndex();
                    if (index != -1) {
                        startEditing(index);
                    }
                }
            }
        });

        leftList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_F2)
                        && leftList.getSelectedIndex() != -1) {
                    startEditing(leftList.getSelectedIndex());
                }
            }
        });

    }

    // 新增项
    private void addNewItem() {
        int index = configState.getItemCount();
        String name = "Item " + (index + 1);

        List<Item> items = configState.getItems();
        for (Item item : items) {
            if (item.name.equals(name)) {
                name = name + " (Duplicate)";
                break;
            }
        }
        configState.addItem(name, "", "");
        listModel.addElement(name);
        leftList.setSelectedIndex(index); // 自动选中
        javaTextArea.setText("");
        javaTextArea.setText("");
    }

    // 删除当前选中项
    private void deleteSelectedItem() {
        int selectedIndex = leftList.getSelectedIndex();
        if (selectedIndex == -1) return;

        boolean result = configState.removeItem(selectedIndex,this);
        // 未删除成功
        if (!result) {
            return;
        }

        listModel.remove(selectedIndex);
        javaTextArea.setText("");
        javaTextArea.setText("");

        // 选中下一个（如果存在），否则清空
        if (listModel.getSize() > 0) {
            int nextIndex = Math.min(selectedIndex, listModel.getSize() - 1);
            leftList.setSelectedIndex(nextIndex);
        } else {
            leftList.clearSelection();
        }
    }

    // 保存当前选中项的两个文本域内容
    private void saveCurrentItem() {
        int selectedIndex = leftList.getSelectedIndex();
        if (selectedIndex != -1) {
            String hostContent = hostTextArea.getText();
            String javaContent = javaTextArea.getText();
            configState.updateItem(selectedIndex, hostContent, javaContent);
        }
    }

    // 加载选中项的内容
    private void loadSelectedItem(int index) {
        Item item = configState.getItem(index);
        if (item != null) {
            hostTextArea.setText(item.hostContent);
            javaTextArea.setText(item.javaContent);
        } else {
            hostTextArea.setText("");
            javaTextArea.setText("");
        }
    }

    // 开始编辑名称
    private void startEditing(int index) {
        if (editingIndex != -1) return;

        String currentName = listModel.getElementAt(index);
        editingIndex = index;

        editField = new JTextField(currentName);
        editField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        editField.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        Rectangle rect = leftList.getCellBounds(index, index);
        if (rect == null) return;

        editField.setBounds(rect.x, rect.y, rect.width, rect.height);

        leftList.add(editField);
        leftList.repaint();

        editField.requestFocusInWindow();
        editField.selectAll();

        editField.addActionListener(e -> finishEditing());
        editField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                finishEditing();
            }
        });
    }

    // 结束编辑
    private void finishEditing() {
        if (editingIndex == -1 || editField == null) return;

        String newName = editField.getText().trim();
        if (newName.isEmpty()) {
            newName = "Item " + (editingIndex + 1);
        }

        if (Objects.equals(configState.getItems().get(editingIndex).name, configState.getSelectedItemName())) {
            newName =  configState.getSelectedItemName();
        }else {
            newName = configState.updateItemName(editingIndex, newName);
        }
        listModel.set(editingIndex, newName);

        leftList.remove(editField);
        editField = null;
        editingIndex = -1;
        leftList.repaint();
    }

    // 加载配置数据
    private void reloadListFromState() {
        listModel.clear();
        List<Item> items = configState.getItems();
        for (Item item : items) {
            listModel.addElement(item.name);
        }
        if (!items.isEmpty()) {
            leftList.setSelectedIndex(0);
            loadSelectedItem(0);
        }
    }
}