package lll69.fasthub;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

public class DarkModeUtil {
    public static boolean isDarkMode(Resources res) {
        return (res.getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }
}
