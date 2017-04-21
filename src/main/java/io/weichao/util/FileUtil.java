package io.weichao.util;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileUtil {
	public static void rename(String fileDir, String suffix) {
		File file = new File(fileDir);
		File[] listFiles = file.listFiles();
		for (int i = 0; i < listFiles.length; i++) {
			File f = listFiles[i];
			String absolutePath = f.getAbsolutePath();
			File newfile = new File(absolutePath + suffix);
			f.renameTo(newfile);
		}
	}

    /**
     * 获取指定路径(相对路径)下的文件
     *
     * @param filePath 相对路径
     * @return 如果文件不存在或文件是文件夹时, 返回null;否则,返回file
     */
    public static File getExternalStorageFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }

        String path = Environment.getExternalStorageDirectory().getPath() + File.separator + filePath;
        File file = new File(path);
        File fileDir = file.getParentFile();
        if (!fileDir.exists() || !fileDir.isDirectory()) {
            fileDir.mkdirs();
        }
        return file;
    }

    public static void deleteExternalStorageFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }

        String path = Environment.getExternalStorageDirectory().getPath() + File.separator + filePath;
        File file = new File(path);
        if (!file.isDirectory() && file.exists()) {
            file.delete();
        }
    }

    /**
     * 获取指定文件夹内的所有特征文件(不包括文件夹)
     *
     * @param fileDir 文件夹
     * @return 如果fileDir存在且是文件夹, 返回所有特征文件(不包括文件夹);否则,返回null
     */
    public static File[] getImageFilesBySuffix(File fileDir) {
        if (fileDir == null) {
            return null;
        }

        if (!fileDir.exists() || !fileDir.isDirectory()) {
            fileDir.mkdirs();
            return null;
        }

        return fileDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String fileName) {
                // 只接受形如：image_000000.jpg 文件名的文件
                if (!new File(dir.getAbsolutePath() + File.separator + fileName).isDirectory() && fileName.matches("^image_[0-9]{6}.jpg$")) {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 获取指定文件夹内的所有特征文件(不包括文件夹)
     *
     * @param fileDir 文件夹
     * @return 如果fileDir存在且是文件夹, 返回所有特征文件(不包括文件夹);否则,返回null
     */
    public static File[] getVideoFilesBySuffix(File fileDir) {
        if (fileDir == null) {
            return null;
        }

        if (!fileDir.exists() || !fileDir.isDirectory()) {
            fileDir.mkdirs();
            return null;
        }

        return fileDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String fileName) {
                // 只接受形如：video_000000.mp4 文件名的文件
                if (!new File(dir.getAbsolutePath() + File.separator + fileName).isDirectory() && fileName.matches("^video_[0-9]{6}.mp4$")) {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 获取指定文件夹内的所有特征文件(不包括文件夹)
     *
     * @param fileDirPath 文件夹路径
     * @return 如果fileDirPath存在且fileDir是文件夹, 返回所有特征文件(不包括文件夹);否则,返回null
     */
    public static File[] getImageFilesBySuffix(String fileDirPath) {
        if (TextUtils.isEmpty(fileDirPath)) {
            return null;
        }

        return getImageFilesBySuffix(new File(fileDirPath));
    }

    /**
     * 获取指定文件夹内的所有特征文件(不包括文件夹)
     *
     * @param fileDirPath 文件夹路径
     * @return 如果fileDirPath存在且fileDir是文件夹, 返回所有特征文件(不包括文件夹);否则,返回null
     */
    public static File[] getVideoFilesBySuffix(String fileDirPath) {
        if (TextUtils.isEmpty(fileDirPath)) {
            return null;
        }

        return getVideoFilesBySuffix(new File(fileDirPath));
    }

    /**
     * 获取按最后修改时间进行排序的文件列表
     *
     * @param fileDirPath 文件夹路径
     * @return 返回按最后修改时间进行排序的文件列表
     */
    public static File[] getImageFilesBySuffixInLastModifiedOrder(String fileDirPath) {
        if (TextUtils.isEmpty(fileDirPath)) {
            return null;
        }

        List<File> fileLists = Arrays.asList(getImageFilesBySuffix(fileDirPath));
        if (fileLists == null || fileLists.size() == 0) {
            return null;
        }

        Collections.sort(fileLists, new Comparator<File>() {
            public int compare(File file, File newFile) {
                if (file.lastModified() < newFile.lastModified()) {
                    return 1;
                } else if (file.lastModified() == newFile.lastModified()) {
                    return 0;
                } else {
                    return -1;
                }

            }
        });
        return (File[]) fileLists.toArray();
    }

    /**
     * 获取按文件名倒序排序的文件列表
     *
     * @param fileDirPath 文件夹路径
     * @return 返回按文件名倒序排序的文件列表
     */
    public static File[] getImageFilesBySuffixInNameReverseOrder(String fileDirPath) {
        if (TextUtils.isEmpty(fileDirPath)) {
            return null;
        }

        List<File> fileLists = Arrays.asList(getImageFilesBySuffix(fileDirPath));
        if (fileLists == null || fileLists.size() == 0) {
            return null;
        }

        Collections.sort(fileLists, new Comparator<File>() {
            public int compare(File file, File newFile) {
                String fileName = file.getName();
                String newFileName = newFile.getName();
                return Integer.parseInt(newFileName.substring(newFileName.indexOf("_") + 1, newFileName.lastIndexOf("."))) - Integer.parseInt(fileName.substring(fileName.indexOf("_") + 1, fileName.lastIndexOf(".")));
            }
        });
        return (File[]) fileLists.toArray();
    }

    /**
     * 获取按文件名倒序排序的文件列表
     *
     * @param fileDirPath 文件夹路径
     * @return 返回按文件名倒序排序的文件列表
     */
    public static File[] getVideoFilesBySuffixInNameReverseOrder(String fileDirPath) {
        if (TextUtils.isEmpty(fileDirPath)) {
            return null;
        }

        List<File> fileLists = Arrays.asList(getVideoFilesBySuffix(fileDirPath));
        if (fileLists == null || fileLists.size() == 0) {
            return null;
        }

        Collections.sort(fileLists, new Comparator<File>() {
            public int compare(File file, File newFile) {
                String fileName = file.getName();
                String newFileName = newFile.getName();
                return Integer.parseInt(newFileName.substring(newFileName.indexOf("_") + 1, newFileName.lastIndexOf("."))) - Integer.parseInt(fileName.substring(fileName.indexOf("_") + 1, fileName.lastIndexOf(".")));
            }
        });
        return (File[]) fileLists.toArray();
    }

    /**
     * 获取指定文件夹内的所有特征文件的名字(不包括文件夹)
     *
     * @param fileDir 文件夹
     * @param suffix  后缀名
     * @return 如果fileDir存在且是文件夹, 返回所有特征文件的名字(不包括文件夹);否则,返回null
     */
    public static String[] getFileNamesBySuffix(File fileDir, final String suffix) {
        if (fileDir == null || TextUtils.isEmpty(suffix)) {
            return null;
        }

        if (!fileDir.exists() || !fileDir.isDirectory()) {
            fileDir.mkdirs();
            return null;
        }

        return fileDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (!new File(dir.getAbsolutePath() + File.separator + filename).isDirectory() && filename.endsWith(suffix)) {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 获取指定文件夹内的所有特征文件的名字(不包括文件夹)
     *
     * @param fileDirPath 文件夹路径
     * @param suffix      后缀名
     * @return 如果fileDir存在且是文件夹, 返回所有特征文件的名字(不包括文件夹);否则,返回null
     */
    public static String[] getFileNamesBySuffix(String fileDirPath, String suffix) {
        if (TextUtils.isEmpty(fileDirPath) || TextUtils.isEmpty(suffix)) {
            return null;
        }

        return getFileNamesBySuffix(new File(fileDirPath), suffix);
    }
}
