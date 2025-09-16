package org.host.launcher.dto;

import java.util.ArrayList;
import java.util.List;

public class MultiState {
    public List<Item> items = new ArrayList<>();

    /**
     * 当前选中的项
     */
    public String selectedItemName;
}