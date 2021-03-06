package gov.moandor.androidweibo.util;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import gov.moandor.androidweibo.R;

public class ConfigManager {
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int PICTURE_SMALL = 0;
    public static final int PICTURE_MEDIUM = 1;
    public static final int PICTURE_LARGE = 2;
    public static final int AVATAR_AUTO = 0;
    public static final int AVATAR_SMALL = 1;
    public static final int AVATAR_LARGE = 2;
    public static final int FONT_SIZE_MODE_SMALL = 0;
    public static final int FONT_SIZE_MODE_MEDIUM = 1;
    public static final int FONT_SIZE_MODE_LARGE = 2;
    public static final int COMMENT_REPOST_LIST_AVATAR_AUTO = 0;
    public static final int COMMENT_REPOST_LIST_AVATAR_ENABLED = 1;
    public static final int COMMENT_REPOST_LIST_AVATAR_DISABLED = 2;
    public static final int THREE_MINUTES = 0;
    public static final int FIFTEEN_MINUTES = 1;
    public static final int HALF_HOUR = 2;
    public static final int LOAD_WEIBO_COUNT_FEWER = 25;
    public static final int LOAD_WEIBO_COUNT_MORE = 100;
    public static final int ORIENTATION_USER = 0;
    public static final int ORIENTATION_LANDSCAPE = 1;
    public static final int ORIENTATION_PORTRAIT = 2;
    public static final float FONT_SIZE_SMALL = 13.0F;
    public static final float FONT_SIZE_MEDIUM = 15.0F;
    public static final float FONT_SIZE_LARGE = 17.0F;
    public static final String PREFERENCE_VERSION_KEY = "preference_version";
    public static final String THEME = "theme";
    public static final String CURRENT_ACCOUNT_INDEX = "current_account_index";
    public static final String WEIBO_GROUP = "weibo_group";
    public static final String PICTURE_QUALITY = "picture_quality";
    public static final String PICTURE_WIFI_QUALITY = "picture_wifi_quality";
    public static final String ATME_FILTER = "atme_filter";
    public static final String COMMENT_FILTER = "comment_filter";
    public static final String FAST_SCROLL_ENABLED = "fast_scroll_enabled";
    public static final String SWIPE_BACK_ENABLED = "swipe_back_enabled";
    public static final String FONT_SIZE_MODE = "font_size_mode";
    public static final String AVATAR_QUALITY = "avatar_quality";
    public static final String NO_PICTURE_MODE = "no_picture_mode";
    public static final String LOAD_WEIBO_COUNT_MODE = "load_weibo_count_mode";
    public static final String COMMENT_REPOST_LIST_AVATAR_MODE = "comment_repost_list_avatar_mode";
    public static final String NOTIFICATION_ENABLED = "notification_enabled";
    public static final String NOTIFICATION_FREQUENCY = "notification_frequency";
    public static final String NOTIFICATION_FREQUENCY_WIFI = "notification_frequency_wifi";
    public static final String NOTIFICATION_MENTION_WEIBO_ENABLED = "notification_mention_weibo_enabled";
    public static final String NOTIFICATION_COMMENT_ENABLED = "notification_comment_enabled";
    public static final String NOTIFICATION_MENTION_COMMENT_ENABLED = "notification_mention_comment_enabled";
    public static final String NOTIFICATION_DM_ENABLED = "notification_dm_enabled";
    public static final String NOTIFICATION_VIBRATE_ENABLED = "notification_vibrate_enabled";
    public static final String NOTIFICATION_LED_ENABLED = "notification_led_enabled";
    public static final String NOTIFICATION_RINGTONE = "notification_ringtone";
    public static final String NOTIFICATION_ENABLED_AFTER_EXIT = "notification_enabled_after_exit";
    public static final String WIFI_AUTO_DOWNLOAD_PIC_ENABLED = "wifi_auto_download_pic_enabled";
    public static final String LIST_HW_ACCEL_ENABLED = "list_hw_accel_enabled";
    public static final String PIC_HW_ACCEL_ENABLED = "pic_hw_accel_enabled";
    public static final String SCREEN_ORIENTATION = "screen_orientation";
    public static final String IGNORING_UNFOLLOWED_ENABLED = "ignoring_unfollowing_enabled";
    //public static final String BM_ENABLED = "bm_enabled";
    public static final String PICTURE_CACHE_DIR = "picture_cache_dir";
    public static final String AVATAR_CACHE_DIR = "avatar_cache_dir";
    public static final String IGNORE_SINA_AD = "ignore_sina_ad";
    private static final int PREFERENCE_VERSION = 5;
    private static final String DEFAULT_CACHE_SD = GlobalContext.getSdCacheDir() + File.separator
            + "weibo";
    private static final String DEFAULT_PICTURE_CACHE_DIR = DEFAULT_CACHE_SD + File.separator +
            "weibo_pictures";
    private static final String DEFAULT_AVATAR_CACHE_DIR = DEFAULT_CACHE_SD + File.separator +
            "weibo_avatars";

    static {
        SharedPreferences sharedPreferences = getPreferences();
        int version = sharedPreferences.getInt(PREFERENCE_VERSION_KEY, 0);
        if (version < PREFERENCE_VERSION) {
            onUpgrade(version, PREFERENCE_VERSION, sharedPreferences);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(PREFERENCE_VERSION_KEY, PREFERENCE_VERSION);
            editor.commit();
        }
        if (sharedPreferences.getString(PICTURE_CACHE_DIR, null) == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PICTURE_CACHE_DIR, DEFAULT_PICTURE_CACHE_DIR);
            editor.commit();
        }
        if (sharedPreferences.getString(AVATAR_CACHE_DIR, null) == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(AVATAR_CACHE_DIR, DEFAULT_AVATAR_CACHE_DIR);
            editor.commit();
        }
    }

    private static void onUpgrade(int oldVersion, int newVersion, SharedPreferences preferences) {
        if (oldVersion == 5 && newVersion == 6) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(IGNORE_SINA_AD, preferences.getBoolean(IGNORING_UNFOLLOWED_ENABLED,
                    true));
            editor.commit();
            return;
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        PreferenceManager.setDefaultValues(GlobalContext.getInstance(), R.xml.prefs, true);
        PreferenceManager.setDefaultValues(GlobalContext.getInstance(),
                R.xml.prefs_notifications, true);
        PreferenceManager.setDefaultValues(GlobalContext.getInstance(), R.xml.prefs_bm, true);
    }

    public static int getAppTheme() {
        return Integer.parseInt(getPreferences().getString(THEME, String.valueOf(THEME_LIGHT)));
    }

    public static int getCurrentAccountIndex() {
        return getPreferences().getInt(CURRENT_ACCOUNT_INDEX, 0);
    }

    public static void setCurrentAccountIndex(int value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(CURRENT_ACCOUNT_INDEX, value);
        apply(editor);
    }

    public static int getWeiboGroup(long accountId) {
        String json = getPreferences().getString(WEIBO_GROUP, null);
        if (!TextUtils.isEmpty(json)) {
            Gson gson = new Gson();
            Map<Long, Integer> map = gson.fromJson(json, new TypeToken<Map<Long, Integer>>() {
            }.getType());
            if (map != null) {
                Integer result = map.get(accountId);
                if (result != null) {
                    return result;
                }
            }
        }
        return 0;
    }

    @SuppressLint("UseSparseArrays") // use map because of serialization
    public static void setWeiboGroup(int value, long accountId) {
        String json = getPreferences().getString(WEIBO_GROUP, null);
        Map<Long, Integer> map;
        Gson gson = new Gson();
        if (json != null) {
            map = gson.fromJson(json, new TypeToken<Map<Long, Integer>>() {
            }.getType());
            map.put(accountId, value);
        } else {
            map = new HashMap<>();
            map.put(accountId, value);
        }
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(WEIBO_GROUP, gson.toJson(map));
        apply(editor);
    }

    public static int getPictureQuality() {
        return Integer.parseInt(getPreferences().getString(PICTURE_QUALITY, String.valueOf(PICTURE_SMALL)));
    }

    public static int getPictureWifiQuality() {
        return Integer.parseInt(getPreferences().getString(PICTURE_WIFI_QUALITY, String.valueOf(PICTURE_LARGE)));
    }

    public static int getAtmeFilter() {
        return getPreferences().getInt(ATME_FILTER, 0);
    }

    public static void setAtmeFilter(int value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(ATME_FILTER, value);
        apply(editor);
    }

    public static int getCommentFilter() {
        return getPreferences().getInt(COMMENT_FILTER, 0);
    }

    public static void setCommentFilter(int value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(COMMENT_FILTER, value);
        apply(editor);
    }

    public static boolean isFastScrollEnabled() {
        return getPreferences().getBoolean(FAST_SCROLL_ENABLED, true);
    }

    public static boolean isSwipeBackEnabled() {
        return getPreferences().getBoolean(SWIPE_BACK_ENABLED, true);
    }

    public static int getFontSizeMode() {
        return Integer.parseInt(getPreferences().getString(FONT_SIZE_MODE, String.valueOf(FONT_SIZE_MODE_MEDIUM)));
    }

    public static int getAvatarQuality() {
        return Integer.parseInt(getPreferences().getString(AVATAR_QUALITY, String.valueOf(AVATAR_AUTO)));
    }

    public static boolean isNoPictureMode() {
        return getPreferences().getBoolean(NO_PICTURE_MODE, false);
    }

    public static int getLoadWeiboCountMode() {
        return Integer.parseInt(getPreferences().getString(LOAD_WEIBO_COUNT_MODE, "0"));
    }

    public static int getCommentRepostListAvatarMode() {
        return Integer.parseInt(getPreferences().getString(COMMENT_REPOST_LIST_AVATAR_MODE,
                String.valueOf(COMMENT_REPOST_LIST_AVATAR_AUTO)));
    }

    public static boolean isNotificationEnabled() {
        return getPreferences().getBoolean(NOTIFICATION_ENABLED, false);
    }

    public static int getNotificationFrequency() {
        return Integer.parseInt(getPreferences().getString(NOTIFICATION_FREQUENCY, String.valueOf(FIFTEEN_MINUTES)));
    }

    public static int getNotificationWifiFrequency() {
        return Integer.parseInt(getPreferences().getString(NOTIFICATION_FREQUENCY_WIFI, String.valueOf(THREE_MINUTES)));
    }

    public static boolean isNotificationMentionWeiboEnabled() {
        return getPreferences().getBoolean(NOTIFICATION_MENTION_WEIBO_ENABLED, true);
    }

    public static boolean isNotificationCommentEnabled() {
        return getPreferences().getBoolean(NOTIFICATION_COMMENT_ENABLED, true);
    }

    public static boolean isNotificationMentionCommentEnabled() {
        return getPreferences().getBoolean(NOTIFICATION_MENTION_COMMENT_ENABLED, true);
    }

    public static boolean isNotificationDmEnabled() {
        return getPreferences().getBoolean(NOTIFICATION_DM_ENABLED, true);
    }

    public static boolean isNotificationVibrateEnabled() {
        return getPreferences().getBoolean(NOTIFICATION_VIBRATE_ENABLED, true);
    }

    public static boolean isNotificationLedEnabled() {
        return getPreferences().getBoolean(NOTIFICATION_LED_ENABLED, true);
    }

    public static String getNotificationRingtone() {
        return getPreferences().getString(NOTIFICATION_RINGTONE, null);
    }

    public static void setNotificationRingtone(String value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(NOTIFICATION_RINGTONE, value);
        apply(editor);
    }

    public static boolean isWifiAutoDownloadPicEnabled() {
        return getPreferences().getBoolean(WIFI_AUTO_DOWNLOAD_PIC_ENABLED, true);
    }

    public static boolean isListHwAccelEnabled() {
        return getPreferences().getBoolean(LIST_HW_ACCEL_ENABLED, true);
    }

    public static boolean isPicHwAccelEnabled() {
        return getPreferences().getBoolean(PIC_HW_ACCEL_ENABLED, true);
    }

    public static int getScreenOrientation() {
        return getPreferences().getInt(SCREEN_ORIENTATION, ORIENTATION_USER);
    }

    public static void setScreenOrientation(int value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(SCREEN_ORIENTATION, value);
        apply(editor);
    }

    public static boolean isNotificationEnabledAfterExit() {
        return getPreferences().getBoolean(NOTIFICATION_ENABLED_AFTER_EXIT, true);
    }

    public static boolean isIgnoringUnfollowedEnabled() {
        return getPreferences().getBoolean(IGNORING_UNFOLLOWED_ENABLED, true);
    }

    public static boolean isBmEnabled() {
        return true;
    }

    public static String getPictureCacheDir() {
        return getPreferences().getString(PICTURE_CACHE_DIR, DEFAULT_PICTURE_CACHE_DIR);
    }

    public static String getAvatarCacheDir() {
        return getPreferences().getString(AVATAR_CACHE_DIR, DEFAULT_AVATAR_CACHE_DIR);
    }

    public static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(GlobalContext.getInstance());
    }

    private static void apply(SharedPreferences.Editor editor) {
        CompatUtils.applySharedPreferences(editor);
    }
}
