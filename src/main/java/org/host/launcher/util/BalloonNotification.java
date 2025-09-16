package org.host.launcher.util;

import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class BalloonNotification {

    // 显示成功气泡提示
    public static void showSuccessBalloon(@NotNull JComponent component, String message) {
        showBalloon(component, message, UIUtil.getInformationIcon(), new Color(144, 238, 144));
    }

    // 显示错误气泡提示
    public static void showErrorBalloon(@NotNull JComponent component, String message) {
        showBalloon(component, message, UIUtil.getErrorIcon(), new Color(255, 182, 193));
    }

    // 显示警告气泡提示
    public static void showWarningBalloon(@NotNull JComponent component, String message) {
        showBalloon(component, message, UIUtil.getWarningIcon(), new Color(255, 215, 0));
    }

    // 显示信息气泡提示
    public static void showInfoBalloon(@NotNull JComponent component, String message) {
        showBalloon(component, message, UIUtil.getInformationIcon(), new Color(173, 216, 230));
    }

    private static void showBalloon(@NotNull JComponent component, String message, Icon icon, Color fillColor) {
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(message, icon, fillColor, null)
                .setFadeoutTime(3000)
                .createBalloon()
                .show(RelativePoint.getCenterOf(component), Balloon.Position.below);
    }

    // 在指定位置显示气泡
    public static void showBalloonAtPoint(Component parent, String message, int x, int y) {
        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(message, null,
                        new Color(173, 216, 230), null)
                .setFadeoutTime(2500)
                .createBalloon()
                .show(new RelativePoint(parent, new Point(x, y)), Balloon.Position.below);
    }
}