package gov.moandor.androidweibo.util;

import android.media.MediaScannerConnection;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import gov.moandor.androidweibo.concurrency.ImageDownloadTaskCache;
import gov.moandor.androidweibo.concurrency.ImageDownloader;

public class FileUtils {
    public static final String CACHE_SD = GlobalContext.getSdCacheDir();
    public static final String LOGS = CACHE_SD + File.separator + "logs";
    private static final String ACCOUNT_AVATARS = CACHE_SD + File.separator + "account_avatars";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File createFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        } else {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        try {
            if (file.createNewFile()) {
                return file;
            }
        } catch (IOException e) {
            Logger.logException(e);
        }
        return null;
    }

    public static String getImagePathFromUrl(String url, ImageDownloader.ImageType type) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        String fileName = url.replace(":", "").replace("/", "");
        String path;
        switch (type) {
            case AVATAR_SMALL:
            case AVATAR_LARGE:
                path = ConfigManager.getAvatarCacheDir();
                break;
            case PICTURE_SMALL:
            case PICTURE_MEDIUM:
            case PICTURE_LARGE:
                path = ConfigManager.getPictureCacheDir();
                break;
            default:
                throw new IllegalStateException("Wrong image type");
        }
        return path + File.separator + fileName;
    }

    public static String getAccountAvatarPathFromUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        String fileName = url.replace(":", "").replace("/", "");
        return ACCOUNT_AVATARS + File.separator + fileName;
    }

    public static boolean savePicture(String url, ImageDownloader.ImageType type) {
        ImageDownloadTaskCache.waitForPictureDownload(url, null, type);
        String sourcePath = getImagePathFromUrl(url, type);
        File sourceFile = new File(sourcePath);
        String sourceName = sourceFile.getName();
        String extension = sourceName.substring(sourceName.lastIndexOf(""));
        if (!extension.equalsIgnoreCase(".png") && !extension.equalsIgnoreCase(".gif")
                && !extension.equalsIgnoreCase(".jpg")) {
            extension = ".jpg";
        }
        String targetPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                        + File.separator + Long.toHexString(System.currentTimeMillis()) + extension;
        File targetFile = createFile(targetPath);
        if (targetFile != null && copy(sourceFile, targetFile)) {
            MediaScannerConnection.scanFile(GlobalContext.getInstance(), new String[]{targetFile.getAbsolutePath()},
                    null, null);
            return true;
        }
        return false;
    }

    private static boolean copy(File source, File target) {
        FileInputStream fis = null;
        BufferedInputStream in = null;
        FileOutputStream fos = null;
        BufferedOutputStream out = null;
        try {
            fis = new FileInputStream(source);
            in = new BufferedInputStream(fis);
            fos = new FileOutputStream(target);
            out = new BufferedOutputStream(fos);
            byte[] buffer = new byte[8 * 1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            return true;
        } catch (IOException e) {
            Logger.logException(e);
            return false;
        } finally {
            Utilities.closeSilently(out);
            Utilities.closeSilently(fos);
            Utilities.closeSilently(in);
            Utilities.closeSilently(fis);
        }
    }
}
