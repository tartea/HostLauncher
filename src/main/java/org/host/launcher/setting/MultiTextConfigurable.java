package org.host.launcher.setting;


import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.host.launcher.state.MultiTextConfigState;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MultiTextConfigurable implements Configurable {

    private MultiTextConfigPanel panel;
    private MultiTextConfigState configState;

    public MultiTextConfigurable() {
        this.configState = ServiceManager.getService(MultiTextConfigState.class);
        this.panel = new MultiTextConfigPanel(configState);
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Multi-Text Configurator";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return panel;
    }

    @Override
    public boolean isModified() {
        // 由于我们实时保存，这里可以简单返回 false
        // 更严谨的做法是记录初始状态并对比
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        // 实时保存，无需额外操作
    }

    @Override
    public void reset() {
        // 重置为当前状态（无特殊逻辑）
    }

    @Override
    public void disposeUIResources() {
        panel = null;
    }
}
