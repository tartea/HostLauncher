package org.host.launcher.state;


import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import org.host.launcher.dto.Item;
import org.host.launcher.dto.MultiState;
import org.host.launcher.host.HostsFileWriter;
import org.host.launcher.setting.MultiTextConfigPanel;
import org.host.launcher.util.BalloonNotification;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.Objects;

@com.intellij.openapi.components.State(
        name = "MultiTextConfigState", // 组件名称，必须唯一
        storages = @Storage("multitextconfig.xml")
)
public final class MultiTextConfigState implements PersistentStateComponent<MultiState> {


    private MultiState multiState = new MultiState();
    // 在类内部添加：


    @Override
    public @Nullable MultiState getState() {
        return multiState;
    }

    @Override
    public void loadState(@NotNull MultiState multiState) {
        this.multiState = multiState;
    }

    public List<Item> getItems() {
        return multiState.items;
    }

    public void setItems(List<Item> items) {
        multiState.items = items;
    }

    public String getSelectedItemName() {
        return multiState.selectedItemName;
    }

    public String getSelectedJavaContent() {
        if (Objects.isNull(multiState.selectedItemName)) {
            return "";
        }
        for (Item item : multiState.items) {
            if (Objects.equals(item.name, multiState.selectedItemName)) {
                return item.javaContent;
            }
        }
        return "";
    }

    public void setSelectedItemName(String selectedItemName) {
        multiState.selectedItemName = selectedItemName;
        if (Objects.isNull(selectedItemName)) {
            HostsFileWriter.clearIdentifierBlock();
        } else {
            // 设置host文件
            getItems().forEach(item -> {
                if (Objects.equals(item.name, selectedItemName)) {
                    HostsFileWriter.appendToHostsFileWithIdentifier(item.hostContent);
                }
            });
        }
    }

    public void addItem(String name, String hostContent, String javaContent) {
        Item item = new Item();
        item.name = name;
        item.hostContent = hostContent;
        item.javaContent = javaContent;
        multiState.items.add(item);

    }

    public void updateItem(int index, String hostContent, String javaContent) {
        if (index >= 0 && index < multiState.items.size()) {
            Item item = multiState.items.get(index);
            item.hostContent = hostContent;
            item.javaContent = javaContent;
            if (Objects.nonNull(multiState.selectedItemName) && Objects.equals(item.name, multiState.selectedItemName)) {
                HostsFileWriter.appendToHostsFileWithIdentifier(hostContent);
            }
        }
    }

    public Item getItem(int index) {
        if (index >= 0 && index < multiState.items.size()) {
            return multiState.items.get(index);
        }
        return null;
    }


    public boolean removeItem(int index, MultiTextConfigPanel multiTextConfigPanel) {
        if (index >= 0 && index < multiState.items.size()) {

            Item item = getItem(index);
            if (Objects.equals(item.name, multiState.selectedItemName)) {
                BalloonNotification.showWarningBalloon(multiTextConfigPanel, "当前选项已经被选中");
                return false;
            }
            multiState.items.remove(index);
        }
        return true;
    }

    public int getItemCount() {
        return multiState.items.size();
    }

    public String updateItemName(int editingIndex, String newName) {
        List<Item> items = getItems();
        for (Item item : items) {
            if (item.name.equals(newName)) {
                newName = newName + " (Duplicate)";
                break;
            }
        }
        getItems().get(editingIndex).name = newName;
        return newName;
    }
}