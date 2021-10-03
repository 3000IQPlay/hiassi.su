package me.hollow.sputnik.client.managers;

import me.hollow.sputnik.api.util.DisplayUtil;
import me.hollow.sputnik.api.util.NoStackTraceThrowable;
import me.hollow.sputnik.api.util.SystemUtil;
import me.hollow.sputnik.api.util.URLReader;

import java.util.ArrayList;
import java.util.List;

// was lazy to delete it before putting on github just ignore this pls

public class HWIDManager {

    public static final String pastebinURL = "";

    public static List<String> hwids = new ArrayList<>();

    public static void hwidCheck() {
        hwids = URLReader.readURL();
        boolean isHwidPresent = hwids.contains(SystemUtil.getSystemInfo());
        if (!isHwidPresent) {
            DisplayUtil.Display();
            throw new NoStackTraceThrowable("");
        }
    }
}
