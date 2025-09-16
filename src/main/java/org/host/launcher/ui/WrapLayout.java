package org.host.launcher.ui;


import java.awt.*;

public class WrapLayout extends FlowLayout {

    public WrapLayout() {
        super();
    }

    public WrapLayout(int align) {
        super(align);
    }

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            Dimension dim = new Dimension(0, 0);
            int nmembers = target.getComponentCount();
            boolean firstVisibleComponent = true;

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = m.getPreferredSize();
                    dim.width = Math.max(dim.width, d.width);

                    if (firstVisibleComponent) {
                        firstVisibleComponent = false;
                        dim.height = d.height;
                    } else {
                        dim.height += super.getVgap() + d.height; // ✅ 正确使用 vgap
                    }
                }
            }

            Insets insets = target.getInsets();
            dim.width += insets.left + insets.right + super.getHgap() * 2; // ✅ 正确使用 hgap
            dim.height += insets.top + insets.bottom + super.getVgap() * 2; // ✅ 正确使用 vgap

            return dim;
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        return preferredLayoutSize(target);
    }

    @Override
    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int maxwidth = target.getWidth() - (insets.left + insets.right + super.getHgap() * 2); // ✅ 正确
            int x = insets.left + super.getHgap(); // ✅ 正确
            int y = insets.top + super.getVgap();  // ✅ 正确
            int rowHeight = 0;

            for (Component comp : target.getComponents()) {
                if (!comp.isVisible()) continue;

                Dimension d = comp.getPreferredSize();
                if (x + d.width > maxwidth) {
                    x = insets.left + super.getHgap(); // ✅ 正确
                    y += rowHeight + super.getVgap();  // ✅ 正确
                    rowHeight = 0;
                }

                if (y + d.height > target.getHeight()) break;

                comp.setBounds(x, y, d.width, d.height);
                x += d.width + super.getHgap(); // ✅ 正确
                rowHeight = Math.max(rowHeight, d.height);
            }
        }
    }
}