package com.cmsr.onebase.framework.common.tools.core.io;

import com.cmsr.onebase.framework.common.tools.core.collection.CollUtil;
import com.cmsr.onebase.framework.common.tools.core.io.file.FileNameUtil;
import com.cmsr.onebase.framework.common.tools.core.io.file.FileReader;
import com.cmsr.onebase.framework.common.tools.core.io.file.FileWriter;
import com.cmsr.onebase.framework.common.tools.core.io.file.PathUtil;
import com.cmsr.onebase.framework.common.tools.core.io.resource.ResourceUtil;
import com.cmsr.onebase.framework.common.tools.core.lang.Assert;
import com.cmsr.onebase.framework.common.tools.core.thread.ThreadUtil;
import com.cmsr.onebase.framework.common.tools.core.util.*;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 文件工具类
 *
 */
public class FileUtil extends PathUtil {

    /**
     * 绝对路径判断正则
     */
    private static final Pattern PATTERN_PATH_ABSOLUTE = Pattern.compile("^[a-zA-Z]:([/\\\\].*)?", Pattern.DOTALL);

    /**
     * 判断文件是否存在，如果file为null，则返回false
     *
     * @param file 文件
     * @return 如果存在返回true
     */
    public static boolean exist(File file) {
        return (null != file) && file.exists();
    }

    /**
     * 判断文件是否存在，如果path为null，则返回false
     *
     * @param path 文件路径
     * @return 如果存在返回true
     */
    public static boolean exist(String path) {
        return (null != path) && file(path).exists();
    }

    /**
     * 获取文件扩展名（后缀名），扩展名不带“.”
     *
     * @param file 文件
     * @return 扩展名
     * @see FileNameUtil#extName(File)
     */
    public static String extName(File file) {
        return FileNameUtil.extName(file);
    }

    /**
     * 根据文件扩展名获得MimeType
     *
     * @param filePath 文件路径或文件名
     * @return MimeType
     * @since 4.1.15
     */
    public static String getMimeType(String filePath) {
        if (StrUtil.isBlank(filePath)) {
            return null;
        }

        // 补充一些常用的mimeType
        if (StrUtil.endWithIgnoreCase(filePath, ".css")) {
            return "text/css";
        } else if (StrUtil.endWithIgnoreCase(filePath, ".js")) {
            return "application/x-javascript";
        } else if (StrUtil.endWithIgnoreCase(filePath, ".rar")) {
            return "application/x-rar-compressed";
        } else if (StrUtil.endWithIgnoreCase(filePath, ".7z")) {
            return "application/x-7z-compressed";
        } else if (StrUtil.endWithIgnoreCase(filePath, ".wgt")) {
            return "application/widget";
        } else if (StrUtil.endWithIgnoreCase(filePath, ".webp")) {
            // JDK8不支持
            return "image/webp";
        }

        String contentType = URLConnection.getFileNameMap().getContentTypeFor(filePath);
        if (null == contentType) {
            contentType = getMimeType(Paths.get(filePath));
        }

        return contentType;
    }

    /**
     * 获得文件的MimeType
     *
     * @param file 文件
     * @return MimeType
     * @see Files#probeContentType(Path)
     * @since 5.5.5
     */
    public static String getMimeType(Path file) {
        try {
            return Files.probeContentType(file);
        } catch (IOException ignore) {
            // issue#3179，使用OpenJDK可能抛出NoSuchFileException，此处返回null
            return null;
        }
    }
    
    /**
     * 修改文件或目录的文件名，不变更路径，只是简单修改文件名，不保留扩展名。<br>
     *
     * <pre>
     * FileUtil.rename(file, "aaa.png", true) xx/xx.png =》xx/aaa.png
     * </pre>
     *
     * @param file       被修改的文件
     * @param newName    新的文件名，如需扩展名，需自行在此参数加上，原文件名的扩展名不会被保留
     * @param isOverride 是否覆盖目标文件
     * @return 目标文件
     * @since 5.3.6
     */
    public static File rename(File file, String newName, boolean isOverride) {
        return rename(file, newName, false, isOverride);
    }

    /**
     * 修改文件或目录的文件名，不变更路径，只是简单修改文件名<br>
     * 重命名有两种模式：<br>
     * 1、isRetainExt为true时，保留原扩展名：
     *
     * <pre>
     * FileUtil.rename(file, "aaa", true) xx/xx.png =》xx/aaa.png
     * </pre>
     *
     * <p>
     * 2、isRetainExt为false时，不保留原扩展名，需要在newName中
     *
     * <pre>
     * FileUtil.rename(file, "aaa.jpg", false) xx/xx.png =》xx/aaa.jpg
     * </pre>
     *
     * @param file        被修改的文件
     * @param newName     新的文件名，可选是否包括扩展名
     * @param isRetainExt 是否保留原文件的扩展名，如果保留，则newName不需要加扩展名
     * @param isOverride  是否覆盖目标文件
     * @return 目标文件
     * @since 3.0.9
     */
    public static File rename(File file, String newName, boolean isRetainExt, boolean isOverride) {
        if (isRetainExt) {
            final String extName = FileUtil.extName(file);
            if (StrUtil.isNotBlank(extName)) {
                newName = newName.concat(".").concat(extName);
            }
        }
        return rename(file.toPath(), newName, isOverride).toFile();
    }
    
    
    /**
     * 获得一个输出流对象
     *
     * @param file 文件
     * @return 输出流对象
     * @throws IORuntimeException IO异常
     */
    public static BufferedOutputStream getOutputStream(File file) throws IORuntimeException {
        final OutputStream out;
        try {
            out = Files.newOutputStream(touch(file).toPath());
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
        return IoUtil.toBuffered(out);
    }

    /**
     * 删除文件或者文件夹<br>
     * 路径如果为相对路径，会转换为ClassPath路径！ 注意：删除文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param fullFileOrDirPath 文件或者目录的路径
     * @return 成功与否
     * @throws IORuntimeException IO异常
     */
    public static boolean del(String fullFileOrDirPath) throws IORuntimeException {
        return del(file(fullFileOrDirPath));
    }

    /**
     * 删除文件或者文件夹<br>
     * 注意：删除文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * <p>
     * 从5.7.6开始，删除文件使用{@link Files#delete(Path)}代替 {@link File#delete()}<br>
     * 因为前者遇到文件被占用等原因时，抛出异常，而非返回false，异常会指明具体的失败原因。
     * </p>
     *
     * @param file 文件对象
     * @return 成功与否
     * @throws IORuntimeException IO异常
     * @see Files#delete(Path)
     */
    public static boolean del(File file) throws IORuntimeException {
        if (file == null || false == file.exists()) {
            // 如果文件不存在或已被删除，此处返回true表示删除成功
            return true;
        }

        if (file.isDirectory()) {
            // 清空目录下所有文件和目录
            boolean isOk = clean(file);
            if (false == isOk) {
                return false;
            }
        }

        // 删除文件或清空后的目录
        final Path path = file.toPath();
        try {
            delFile(path);
        } catch (DirectoryNotEmptyException e) {
            // 遍历清空目录没有成功，此时补充删除一次（可能存在部分软链）
            del(path);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }

        return true;
    }

    /**
     * 清空文件夹<br>
     * 注意：清空文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param directory 文件夹
     * @return 成功与否
     * @throws IORuntimeException IO异常
     * @since 3.0.6
     */
    public static boolean clean(File directory) throws IORuntimeException {
        if (directory == null || directory.exists() == false || false == directory.isDirectory()) {
            return true;
        }

        final File[] files = directory.listFiles();
        if (null != files) {
            for (File childFile : files) {
                if (false == del(childFile)) {
                    // 删除一个出错则本次删除任务失败
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * 创建所给文件或目录的父目录
     *
     * @param file 文件或目录
     * @return 父目录
     */
    public static File mkParentDirs(File file) {
        if (null == file) {
            return null;
        }
        return mkdir(getParent(file, 1));
    }

    /**
     * 创建文件夹，会递归自动创建其不存在的父文件夹，如果存在直接返回此文件夹<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型<br>
     *
     * @param dir 目录
     * @return 创建的目录
     */
    public static File mkdir(File dir) {
        if (dir == null) {
            return null;
        }
        if (false == dir.exists()) {
            mkdirsSafely(dir, 5, 1);
        }
        return dir;
    }

    /**
     * 安全地级联创建目录 (确保并发环境下能创建成功)
     *
     * <pre>
     *     并发环境下，假设 test 目录不存在，如果线程A mkdirs "test/A" 目录，线程B mkdirs "test/B"目录，
     *     其中一个线程可能会失败，进而导致以下代码抛出 FileNotFoundException 异常
     *
     *     file.getParentFile().mkdirs(); // 父目录正在被另一个线程创建中，返回 false
     *     file.createNewFile(); // 抛出 IO 异常，因为该线程无法感知到父目录已被创建
     * </pre>
     *
     * @param dir         待创建的目录
     * @param tryCount    最大尝试次数
     * @param sleepMillis 线程等待的毫秒数
     * @return true表示创建成功，false表示创建失败
     * @author z8g
     * @since 5.7.21
     */
    public static boolean mkdirsSafely(File dir, int tryCount, long sleepMillis) {
        if (dir == null) {
            return false;
        }
        if (dir.isDirectory()) {
            return true;
        }
        for (int i = 1; i <= tryCount; i++) { // 高并发场景下，可以看到 i 处于 1 ~ 3 之间
            // 如果文件已存在，也会返回 false，所以该值不能作为是否能创建的依据，因此不对其进行处理
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
            if (dir.exists()) {
                return true;
            }
            ThreadUtil.sleep(sleepMillis);
        }
        return dir.exists();
    }

    /**
     * 获取指定层级的父路径
     *
     * <pre>
     * getParent(file("d:/aaa/bbb/cc/ddd", 0)) -》 "d:/aaa/bbb/cc/ddd"
     * getParent(file("d:/aaa/bbb/cc/ddd", 2)) -》 "d:/aaa/bbb"
     * getParent(file("d:/aaa/bbb/cc/ddd", 4)) -》 "d:/"
     * getParent(file("d:/aaa/bbb/cc/ddd", 5)) -》 null
     * </pre>
     *
     * @param file  目录或文件
     * @param level 层级
     * @return 路径File，如果不存在返回null
     * @since 4.1.2
     */
    public static File getParent(File file, int level) {
        if (level < 1 || null == file) {
            return file;
        }

        File parentFile;
        try {
            parentFile = file.getCanonicalFile().getParentFile();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        if (1 == level) {
            return parentFile;
        }
        return getParent(parentFile, level - 1);
    }

    /**
     * 写数据到文件中
     *
     * @param dest 目标文件
     * @param data 数据
     * @return 目标文件
     * @throws IORuntimeException IO异常
     */
    public static File writeBytes(byte[] data, File dest) throws IORuntimeException {
        return writeBytes(data, dest, 0, data.length, false);
    }

    /**
     * 写入数据到文件
     *
     * @param data     数据
     * @param dest     目标文件
     * @param off      数据开始位置
     * @param len      数据长度
     * @param isAppend 是否追加模式
     * @return 目标文件
     * @throws IORuntimeException IO异常
     */
    public static File writeBytes(byte[] data, File dest, int off, int len, boolean isAppend) throws IORuntimeException {
        return FileWriter.create(dest).write(data, off, len, isAppend);
    }


    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param file 文件对象
     * @return 文件，若路径为null，返回null
     * @throws IORuntimeException IO异常
     */
    public static File touch(File file) throws IORuntimeException {
        if (null == file) {
            return null;
        }
        if (false == file.exists()) {
            mkParentDirs(file);
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }
        return file;
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param path 相对ClassPath的目录或者绝对路径目录，使用POSIX风格
     * @return 文件，若路径为null，返回null
     * @throws IORuntimeException IO异常
     */
    public static File touch(String path) throws IORuntimeException {
        if (path == null) {
            return null;
        }
        return touch(file(path));
    }

    /**
     * 创建File对象，自动识别相对或绝对路径，相对路径将自动从ClassPath下寻找
     *
     * @param path 相对ClassPath的目录或者绝对路径目录
     * @return File
     */
    public static File file(String path) {
        if (null == path) {
            return null;
        }
        return new File(getAbsolutePath(path));
    }

    /**
     * 创建File对象<br>
     * 根据的路径构建文件，在Win下直接构建，在Linux下拆分路径单独构建
     * 此方法会检查slip漏洞，漏洞说明见http://blog.nsfocus.net/zip-slip-2/
     *
     * @param parent 父文件对象
     * @param path   文件路径
     * @return File
     */
    public static File file(File parent, String path) {
        if (StrUtil.isBlank(path)) {
            throw new NullPointerException("File path is blank!");
        }
        return checkSlip(parent, buildFile(parent, path));
    }

    /**
     * 检查父完整路径是否为自路径的前半部分，如果不是说明不是子路径，可能存在slip注入。
     * <p>
     * 见http://blog.nsfocus.net/zip-slip-2/
     *
     * @param parentFile 父文件或目录
     * @param file       子文件或目录
     * @return 子文件或目录
     * @throws IllegalArgumentException 检查创建的子文件不在父目录中抛出此异常
     */
    public static File checkSlip(File parentFile, File file) throws IllegalArgumentException {
        if (null != parentFile && null != file) {
            if (false == isSub(parentFile, file)) {
                throw new IllegalArgumentException("New file is outside of the parent dir: " + file.getName());
            }
        }
        return file;
    }

    /**
     * 判断给定的目录是否为给定文件或文件夹的子目录
     *
     * @param parent 父目录
     * @param sub    子目录
     * @return 子目录是否为父目录的子目录
     * @since 4.5.4
     */
    public static boolean isSub(File parent, File sub) {
        Assert.notNull(parent);
        Assert.notNull(sub);
        return isSub(parent.toPath(), sub.toPath());
    }

    /**
     * 获取绝对路径，相对于ClassPath的目录<br>
     * 如果给定就是绝对路径，则返回原路径，原路径把所有\替换为/<br>
     * 兼容Spring风格的路径表示，例如：classpath:config/example.setting也会被识别后转换
     *
     * @param path 相对路径
     * @return 绝对路径
     */
    public static String getAbsolutePath(String path) {
        return getAbsolutePath(path, null);
    }

    /**
     * 获取标准的绝对路径
     *
     * @param file 文件
     * @return 绝对路径
     */
    public static String getAbsolutePath(File file) {
        if (file == null) {
            return null;
        }

        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            return file.getAbsolutePath();
        }
    }

    /**
     * 获取绝对路径<br>
     * 此方法不会判定给定路径是否有效（文件或目录存在）
     *
     * @param path      相对路径
     * @param baseClass 相对路径所相对的类
     * @return 绝对路径
     */
    public static String getAbsolutePath(String path, Class<?> baseClass) {
        String normalPath;
        if (path == null) {
            normalPath = StrUtil.EMPTY;
        } else {
            normalPath = normalize(path);
            if (isAbsolutePath(normalPath)) {
                // 给定的路径已经是绝对路径了
                return normalPath;
            }
        }

        // 相对于ClassPath路径
        final URL url = ResourceUtil.getResource(normalPath, baseClass);
        if (null != url) {
            // 对于jar中文件包含file:前缀，需要去掉此类前缀，在此做标准化，since 3.0.8 解决中文或空格路径被编码的问题
            return FileUtil.normalize(URLUtil.getDecodedPath(url));
        }


        // 如果资源不存在，则返回一个拼接的资源绝对路径
        final String classPath = ClassUtil.getClassPath();
        if (null == classPath) {
            // throw new NullPointerException("ClassPath is null !");
            // 在jar运行模式中，ClassPath有可能获取不到，此时返回原始相对路径（此时获取的文件为相对工作目录）
            return path;
        }

        // 资源不存在的情况下使用标准化路径有问题，使用原始路径拼接后标准化路径
        return normalize(classPath.concat(Objects.requireNonNull(path)));
    }

    /**
     * 给定路径已经是绝对路径<br>
     * 此方法并没有针对路径做标准化，建议先执行{@link #normalize(String)}方法标准化路径后判断<br>
     * 绝对路径判断条件是：
     * <ul>
     *     <li>以/开头的路径</li>
     *     <li>满足类似于 c:/xxxxx，其中祖母随意，不区分大小写</li>
     *     <li>满足类似于 d:\xxxxx，其中祖母随意，不区分大小写</li>
     * </ul>
     *
     * @param path 需要检查的Path
     * @return 是否已经是绝对路径
     */
    public static boolean isAbsolutePath(String path) {
        if (StrUtil.isEmpty(path)) {
            return false;
        }

        // 给定的路径已经是绝对路径了
        return StrUtil.C_SLASH == path.charAt(0) || ReUtil.isMatch(PATTERN_PATH_ABSOLUTE, path);
    }

    /**
     * 修复路径<br>
     * 如果原路径尾部有分隔符，则保留为标准分隔符（/），否则不保留
     * <ol>
     * <li>1. 统一用 /</li>
     * <li>2. 多个 / 转换为一个 /</li>
     * <li>3. 去除左边空格</li>
     * <li>4. .. 和 . 转换为绝对路径，当..多于已有路径时，直接返回根路径</li>
     * </ol>
     * <p>
     * 栗子：
     *
     * <pre>
     * "/foo//" =》 "/foo/"
     * "/foo/./" =》 "/foo/"
     * "/foo/../bar" =》 "/bar"
     * "/foo/../bar/" =》 "/bar/"
     * "/foo/../bar/../baz" =》 "/baz"
     * "/../" =》 "/"
     * "foo/bar/.." =》 "foo"
     * "foo/../bar" =》 "bar"
     * "foo/../../bar" =》 "bar"
     * "//server/foo/../bar" =》 "/server/bar"
     * "//server/../bar" =》 "/bar"
     * "C:\\foo\\..\\bar" =》 "C:/bar"
     * "C:\\..\\bar" =》 "C:/bar"
     * "~/foo/../bar/" =》 "~/bar/"
     * "~/../bar" =》 普通用户运行是'bar的home目录'，ROOT用户运行是'/bar'
     * </pre>
     *
     * @param path 原路径
     * @return 修复后的路径
     */
    public static String normalize(String path) {
        if (path == null) {
            return null;
        }

        //兼容Windows下的共享目录路径（原始路径如果以\\开头，则保留这种路径）
        if (path.startsWith("\\\\")) {
            return path;
        }

        // 兼容Spring风格的ClassPath路径，去除前缀，不区分大小写
        String pathToUse = StrUtil.removePrefixIgnoreCase(path, URLUtil.CLASSPATH_URL_PREFIX);
        // 去除file:前缀
        pathToUse = StrUtil.removePrefixIgnoreCase(pathToUse, URLUtil.FILE_URL_PREFIX);

        // 识别home目录形式，并转换为绝对路径
        if (StrUtil.startWith(pathToUse, '~')) {
            pathToUse = getUserHomePath() + pathToUse.substring(1);
        }

        // 统一使用斜杠
        pathToUse = pathToUse.replaceAll("[/\\\\]+", StrUtil.SLASH);
        // 去除开头空白符，末尾空白符合法，不去除
        pathToUse = StrUtil.trimStart(pathToUse);
        // issue#IAB65V 去除尾部的换行符
        pathToUse = StrUtil.trim(pathToUse, 1, (c)->c == '\n' || c == '\r');

        String prefix = StrUtil.EMPTY;
        int prefixIndex = pathToUse.indexOf(StrUtil.COLON);
        if (prefixIndex > -1) {
            // 可能Windows风格路径
            prefix = pathToUse.substring(0, prefixIndex + 1);
            if (StrUtil.startWith(prefix, StrUtil.C_SLASH)) {
                // 去除类似于/C:这类路径开头的斜杠
                prefix = prefix.substring(1);
            }
            if (false == prefix.contains(StrUtil.SLASH)) {
                pathToUse = pathToUse.substring(prefixIndex + 1);
            } else {
                // 如果前缀中包含/,说明非Windows风格path
                prefix = StrUtil.EMPTY;
            }
        }
        if (pathToUse.startsWith(StrUtil.SLASH)) {
            prefix += StrUtil.SLASH;
            pathToUse = pathToUse.substring(1);
        }

        List<String> pathList;
        if (pathToUse.isEmpty()) {
            pathList = new ArrayList<>();
        } else {
            pathList = Arrays.asList(pathToUse.split("/"));
        }


        List<String> pathElements = new LinkedList<>();
        int tops = 0;
        String element;
        for (int i = pathList.size() - 1; i >= 0; i--) {
            element = pathList.get(i);
            // 只处理非.的目录，即只处理非当前目录
            if (false == StrUtil.DOT.equals(element)) {
                if (StrUtil.DOUBLE_DOT.equals(element)) {
                    tops++;
                } else {
                    if (tops > 0) {
                        // 有上级目录标记时按照个数依次跳过
                        tops--;
                    } else {
                        // Normal path element found.
                        pathElements.add(0, element);
                    }
                }
            }
        }

        // issue#1703@Github
        if (tops > 0 && StrUtil.isEmpty(prefix)) {
            // 只有相对路径补充开头的..，绝对路径直接忽略之
            while (tops-- > 0) {
                //遍历完节点发现还有上级标注（即开头有一个或多个..），补充之
                // Normal path element found.
                pathElements.add(0, StrUtil.DOUBLE_DOT);
            }
        }

        return prefix + CollUtil.join(pathElements, StrUtil.SLASH);
    }

    /**
     * 读取文件内容
     *
     * @param file    文件
     * @param charset 字符集
     * @return 内容
     * @throws IORuntimeException IO异常
     */
    public static String readString(File file, Charset charset) throws IORuntimeException {
        return FileReader.create(file, charset).readString();
    }

    /**
     * 将String写入文件，覆盖模式，字符集为UTF-8
     *
     * @param content 写入的内容
     * @param path    文件路径
     * @return 写入的文件
     * @throws IORuntimeException IO异常
     */
    public static File writeUtf8String(String content, String path) throws IORuntimeException {
        return writeString(content, path, StandardCharsets.UTF_8);
    }

    /**
     * 将String写入文件，覆盖模式，字符集为UTF-8
     *
     * @param content 写入的内容
     * @param file    文件
     * @return 写入的文件
     * @throws IORuntimeException IO异常
     */
    public static File writeUtf8String(String content, File file) throws IORuntimeException {
        return writeString(content, file, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 将String写入文件，覆盖模式
     *
     * @param content 写入的内容
     * @param path    文件路径
     * @param charset 字符集
     * @return 写入的文件
     * @throws IORuntimeException IO异常
     */
    public static File writeString(String content, String path, Charset charset) throws IORuntimeException {
        return writeString(content, touch(path), charset);
    }

    /**
     * 将String写入文件，覆盖模式
     *
     * @param content 写入的内容
     * @param file    文件
     * @param charset 字符集
     * @return 被写入的文件
     * @throws IORuntimeException IO异常
     */
    public static File writeString(String content, File file, Charset charset) throws IORuntimeException {
        return FileWriter.create(file, charset).write(content);
    }

    /**
     * 获取用户路径（绝对路径）
     *
     * @return 用户路径
     * @since 4.0.6
     */
    public static String getUserHomePath() {
        return System.getProperty("user.home");
    }

    /**
     * 递归遍历目录以及子目录中的所有文件<br>
     * 如果用户传入相对路径，则是相对classpath的路径<br>
     * 如："test/aaa"表示"${classpath}/test/aaa"
     *
     * @param path 相对ClassPath的目录或者绝对路径目录
     * @return 文件列表
     * @since 3.2.0
     */
    public static List<File> loopFiles(String path) {
        return loopFiles(file(path));
    }

    /**
     * 递归遍历目录以及子目录中的所有文件
     *
     * @param file 当前遍历文件
     * @return 文件列表
     */
    public static List<File> loopFiles(File file) {
        return loopFiles(file, null);
    }

    /**
     * 递归遍历目录以及子目录中的所有文件<br>
     * 如果提供file为文件，直接返回过滤结果
     *
     * @param file       当前遍历文件或目录
     * @param fileFilter 文件过滤规则对象，选择要保留的文件，只对文件有效，不过滤目录
     * @return 文件列表
     */
    public static List<File> loopFiles(File file, FileFilter fileFilter) {
        return loopFiles(file, -1, fileFilter);
    }

    /**
     * 递归遍历目录以及子目录中的所有文件<br>
     * 如果提供file为文件，直接返回过滤结果
     *
     * @param file       当前遍历文件或目录
     * @param maxDepth   遍历最大深度，-1表示遍历到没有目录为止
     * @param fileFilter 文件过滤规则对象，选择要保留的文件，只对文件有效，不过滤目录，null表示接收全部文件
     * @return 文件列表
     * @since 4.6.3
     */
    public static List<File> loopFiles(File file, int maxDepth, FileFilter fileFilter) {
        return loopFiles(file.toPath(), maxDepth, fileFilter);
    }

    /**
     * 递归遍历目录以及子目录中的所有文件<br>
     * 如果提供path为文件，直接返回过滤结果
     *
     * @param path       当前遍历文件或目录
     * @param maxDepth   遍历最大深度，-1表示遍历到没有目录为止
     * @param fileFilter 文件过滤规则对象，选择要保留的文件，只对文件有效，不过滤目录，null表示接收全部文件
     * @return 文件列表
     * @since 5.4.1
     */
    public static List<File> loopFiles(Path path, int maxDepth, FileFilter fileFilter) {
        return loopFiles(path, maxDepth, false, fileFilter);
    }

    /**
     * 递归遍历目录以及子目录中的所有文件<br>
     * 如果提供path为文件，直接返回过滤结果
     *
     * @param path          当前遍历文件或目录
     * @param maxDepth      遍历最大深度，-1表示遍历到没有目录为止
     * @param isFollowLinks 是否跟踪软链（快捷方式）
     * @param fileFilter    文件过滤规则对象，选择要保留的文件，只对文件有效，不过滤目录，null表示接收全部文件
     * @return 文件列表
     * @since 5.4.1
     */
    public static List<File> loopFiles(final Path path, final int maxDepth, final boolean isFollowLinks, final FileFilter fileFilter) {
        final List<File> fileList = new ArrayList<>();

        if (!exists(path, isFollowLinks)) {
            return fileList;
        } else if (!isDirectory(path, isFollowLinks)) {
            final File file = path.toFile();
            if (null == fileFilter || fileFilter.accept(file)) {
                fileList.add(file);
            }
            return fileList;
        }

        walkFiles(path, maxDepth, isFollowLinks, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(final Path path, final BasicFileAttributes attrs) {
                final File file = path.toFile();
                if (null == fileFilter || fileFilter.accept(file)) {
                    fileList.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return fileList;
    }

    /**
     * 判断文件或目录是否存在
     *
     * @param path          文件
     * @param isFollowLinks 是否跟踪软链（快捷方式）
     * @return 是否存在
     * @since 5.5.3
     */
    public static boolean exists(Path path, boolean isFollowLinks) {
        return Files.exists(path, getLinkOptions(isFollowLinks));
    }

    /**
     * 判断是否为目录，如果file为null，则返回false
     *
     * @param path          {@link Path}
     * @param isFollowLinks 是否追踪到软链对应的真实地址
     * @return 如果为目录true
     * @since 3.1.0
     */
    public static boolean isDirectory(Path path, boolean isFollowLinks) {
        if (null == path) {
            return false;
        }
        return Files.isDirectory(path, getLinkOptions(isFollowLinks));
    }

    /**
     * 判断是否为目录，如果file为null，则返回false
     *
     * @param file 文件
     * @return 如果为目录true
     */
    public static boolean isDirectory(File file) {
        return (null != file) && file.isDirectory();
    }

    /**
     * 构建是否追踪软链的选项
     *
     * @param isFollowLinks 是否追踪软链
     * @return 选项
     * @since 5.8.23
     */
    public static LinkOption[] getLinkOptions(final boolean isFollowLinks) {
        return isFollowLinks ? new LinkOption[0] : new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
    }

    /**
     * 遍历指定path下的文件并做处理
     *
     * @param start         起始路径，必须为目录
     * @param maxDepth      最大遍历深度，-1表示不限制深度
     * @param visitor       {@link FileVisitor} 接口，用于自定义在访问文件时，访问目录前后等节点做的操作
     * @param isFollowLinks 是否追踪到软链对应的真实地址
     * @see Files#walkFileTree(Path, Set, int, FileVisitor)
     * @since 5.8.23
     */
    public static void walkFiles(final Path start, int maxDepth, final boolean isFollowLinks, final FileVisitor<? super Path> visitor) {
        if (maxDepth < 0) {
            // < 0 表示遍历到最底层
            maxDepth = Integer.MAX_VALUE;
        }

        try {
            Files.walkFileTree(start, getFileVisitOption(isFollowLinks), maxDepth, visitor);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 构建是否追踪软链的选项
     *
     * @param isFollowLinks 是否追踪软链
     * @return 选项
     * @since 5.8.23
     */
    public static Set<FileVisitOption> getFileVisitOption(final boolean isFollowLinks) {
        return isFollowLinks ? EnumSet.of(FileVisitOption.FOLLOW_LINKS) :
                EnumSet.noneOf(FileVisitOption.class);
    }

    /**
     * 通过JDK7+的 Files#copy(Path, Path, CopyOption...) 方法拷贝文件
     *
     * @param src     源文件
     * @param dest    目标文件或目录，如果为目录使用与源文件相同的文件名
     * @param options {@link StandardCopyOption}
     * @return 目标文件
     * @throws IORuntimeException IO异常
     */
    public static File copyFile(File src, File dest, StandardCopyOption... options) throws IORuntimeException {
        // check
        Assert.notNull(src, "Source File is null !");
        if (false == src.exists()) {
            throw new IORuntimeException("File not exist: " + src);
        }
        Assert.notNull(dest, "Destination File or directiory is null !");
        if (equals(src, dest)) {
            throw new IORuntimeException("Files '{}' and '{}' are equal", src, dest);
        }
        return copyFile(src.toPath(), dest.toPath(), options).toFile();
    }
    
    /**
     * 通过JDK7+的 {@link Files#copy(Path, Path, CopyOption...)} 方法拷贝文件<br>
     * 此方法不支持递归拷贝目录，如果src传入是目录，只会在目标目录中创建空目录
     *
     * @param src     源文件路径，如果为目录只在目标中创建新目录
     * @param dest    目标文件或目录，如果为目录使用与源文件相同的文件名
     * @param options {@link StandardCopyOption}
     * @return 目标Path
     * @throws IORuntimeException IO异常
     */
    public static Path copyFile(Path src, Path dest, StandardCopyOption... options) throws IORuntimeException {
        return copyFile(src, dest, (CopyOption[]) options);
    }

    /**
     * 通过JDK7+的 {@link Files#copy(Path, Path, CopyOption...)} 方法拷贝文件<br>
     * 此方法不支持递归拷贝目录，如果src传入是目录，只会在目标目录中创建空目录
     *
     * @param src     源文件路径，如果为目录只在目标中创建新目录
     * @param target  目标文件或目录，如果为目录使用与源文件相同的文件名
     * @param options {@link StandardCopyOption}
     * @return 目标Path
     * @throws IORuntimeException IO异常
     * @since 5.4.1
     */
    public static Path copyFile(Path src, Path target, CopyOption... options) throws IORuntimeException {
        Assert.notNull(src, "Source File is null !");
        Assert.notNull(target, "Destination File or directory is null !");

        final Path targetPath = isDirectory(target) ? target.resolve(src.getFileName()) : target;
        // 创建级联父目录
        mkParentDirs(targetPath);
        try {
            return Files.copy(src, targetPath, options);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 创建所给文件或目录的父目录
     *
     * @param path 文件或目录
     * @return 父目录
     * @since 5.5.7
     */
    public static Path mkParentDirs(Path path) {
        return mkdir(path.getParent());
    }

    /**
     * 创建所给目录及其父目录
     *
     * @param dir 目录
     * @return 目录
     * @since 5.5.7
     */
    public static Path mkdir(Path dir) {
        if (null != dir && false == exists(dir, false)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
        return dir;
    }

    /**
     * 判断是否为目录，如果file为null，则返回false<br>
     * 此方法不会追踪到软链对应的真实地址，即软链被当作文件
     *
     * @param path {@link Path}
     * @return 如果为目录true
     * @since 5.5.1
     */
    public static boolean isDirectory(Path path) {
        return isDirectory(path, false);
    }

    /**
     * 检查两个文件是否是同一个文件<br>
     * 所谓文件相同，是指File对象是否指向同一个文件或文件夹
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 是否相同
     * @throws IORuntimeException IO异常
     */
    public static boolean equals(File file1, File file2) throws IORuntimeException {
        Assert.notNull(file1,"File must not be null");
        Assert.notNull(file2,"File must not be null");
        if (false == file1.exists() || false == file2.exists()) {
            // 两个文件都不存在判断其路径是否相同， 对于一个存在一个不存在的情况，一定不相同
            return false == file1.exists()//
                    && false == file2.exists()//
                    && pathEquals(file1, file2);
        }
        return equals(file1.toPath(), file2.toPath());
    }

    /**
     * 检查两个文件是否是同一个文件<br>
     * 所谓文件相同，是指Path对象是否指向同一个文件或文件夹
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 是否相同
     * @throws IORuntimeException IO异常
     * @see Files#isSameFile(Path, Path)
     * @since 5.4.1
     */
    public static boolean equals(Path file1, Path file2) throws IORuntimeException {
        try {
            return Files.isSameFile(file1, file2);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 文件路径是否相同<br>
     * 取两个文件的绝对路径比较，在Windows下忽略大小写，在Linux下不忽略。
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 文件路径是否相同
     * @since 3.0.9
     */
    public static boolean pathEquals(File file1, File file2) {
        if (isWindows()) {
            // Windows环境
            try {
                if (StrUtil.equalsIgnoreCase(file1.getCanonicalPath(), file2.getCanonicalPath())) {
                    return true;
                }
            } catch (Exception e) {
                if (StrUtil.equalsIgnoreCase(file1.getAbsolutePath(), file2.getAbsolutePath())) {
                    return true;
                }
            }
        } else {
            // 类Unix环境
            try {
                if (StrUtil.equals(file1.getCanonicalPath(), file2.getCanonicalPath())) {
                    return true;
                }
            } catch (Exception e) {
                if (StrUtil.equals(file1.getAbsolutePath(), file2.getAbsolutePath())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否为Windows环境
     *
     * @return 是否为Windows环境
     * @since 3.0.9
     */
    public static boolean isWindows() {
        return FileNameUtil.WINDOWS_SEPARATOR == File.separatorChar;
    }

    /**
     * 获取指定层级的父路径
     *
     * <pre>
     * getParent("d:/aaa/bbb/cc/ddd", 0) -》 "d:/aaa/bbb/cc/ddd"
     * getParent("d:/aaa/bbb/cc/ddd", 2) -》 "d:/aaa/bbb"
     * getParent("d:/aaa/bbb/cc/ddd", 4) -》 "d:/"
     * getParent("d:/aaa/bbb/cc/ddd", 5) -》 null
     * </pre>
     *
     * @param filePath 目录或文件路径
     * @param level    层级
     * @return 路径File，如果不存在返回null
     * @since 4.1.2
     */
    public static String getParent(String filePath, int level) {
        final File parent = getParent(file(filePath), level);
        try {
            return null == parent ? null : parent.getCanonicalPath();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 读取文件所有数据<br>
     * 文件的长度不能超过Integer.MAX_VALUE
     *
     * @param file 文件
     * @return 字节码
     * @throws IORuntimeException IO异常
     */
    public static byte[] readBytes(File file) throws IORuntimeException {
        return FileReader.create(file).readBytes();
    }

    /**
     * 读取文件所有数据<br>
     * 文件的长度不能超过Integer.MAX_VALUE
     *
     * @param filePath 文件路径
     * @return 字节码
     * @throws IORuntimeException IO异常
     * @since 3.2.0
     */
    public static byte[] readBytes(String filePath) throws IORuntimeException {
        return readBytes(file(filePath));
    }
    

    public static String getFileType(File file) {
        if (file.length() == 0) {
            return "";
        }

        try {
            return getFileTypeByHeader(file);
        } catch (IOException e) {
            // 如果通过文件头识别失败，则回退到通过文件扩展名识别
            String fileName = file.getName();
            int lastDotIndex = fileName.lastIndexOf('.');
            if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
                return fileName.substring(lastDotIndex + 1).toLowerCase();
            }
            return "";
        }
    }

    private static String getFileTypeByHeader(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] header = new byte[32];
            int read = fis.read(header);
            if (read <= 0) {
                return "";
            }

            // 检查文件头标识
            String hex = bytesToHex(header, read);

            // 常见文件类型的文件头标识
            if (hex.startsWith("FFD8FF")) {
                return "jpg";
            } else if (hex.startsWith("89504E47")) {
                return "png";
            } else if (hex.startsWith("47494638")) {
                return "gif";
            } else if (hex.startsWith("504B0304") || hex.startsWith("504B0506") || hex.startsWith("504B0708")) {
                return "zip";
            } else if (hex.startsWith("52617221")) {
                return "rar";
            } else if (hex.startsWith("377ABCAF271C")) {
                return "7z";
            } else if (hex.startsWith("25504446")) {
                return "pdf";
            } else if (hex.startsWith("49492A00") || hex.startsWith("4D4D002A")) {
                return "tif";
            } else if (hex.startsWith("424D")) {
                return "bmp";
            } else if (hex.startsWith("41433130")) {
                return "dwg";
            } else if (hex.startsWith("7B5C727466")) {
                return "rtf";
            } else if (hex.startsWith("38425053")) {
                return "psd";
            } else if (hex.startsWith("464C56")) {
                return "flv";
            } else if (hex.startsWith("000001BA") || hex.startsWith("000001B3")) {
                return "mpg";
            } else if (hex.startsWith("494433") || hex.startsWith("FFFB") || hex.startsWith("FFF3") || hex.startsWith("FFF2")) {
                return "mp3";
            } else if (hex.startsWith("52494646") && hex.substring(16, 32).equals("57415645")) {
                return "wav";
            } else if (hex.startsWith("52494646") && hex.substring(16, 32).equals("41564920")) {
                return "avi";
            }

            return "";
        }
    }

    /**
     * 获得一个打印写入对象，可以有print
     *
     * @param file     文件
     * @param charset  字符集
     * @param isAppend 是否追加
     * @return 打印对象
     * @throws IORuntimeException IO异常
     * @since 5.4.3
     */
    public static PrintWriter getPrintWriter(File file, Charset charset, boolean isAppend) throws IORuntimeException {
        return new PrintWriter(getWriter(file, charset, isAppend));
    }

    /**
     * 获得一个带缓存的写入对象
     *
     * @param file     输出文件
     * @param charset  字符集
     * @param isAppend 是否追加
     * @return BufferedReader对象
     * @throws IORuntimeException IO异常
     */
    public static BufferedWriter getWriter(File file, Charset charset, boolean isAppend) throws IORuntimeException {
        return FileWriter.create(file, charset).getWriter(isAppend);
    }

    private static String bytesToHex(byte[] bytes, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length && i < bytes.length; i++) {
            sb.append(String.format("%02X", bytes[i]));
        }
        return sb.toString();
    }


    /**
     * 获得输入流
     *
     * @param file 文件
     * @return 输入流
     * @throws IORuntimeException 文件未找到
     * @see IoUtil#toStream(File)
     */
    public static BufferedInputStream getInputStream(final File file) throws IORuntimeException {
        return IoUtil.toBuffered(IoUtil.toStream(file));
    }

    /**
     * 创建File对象
     *
     * @param url 文件URL
     * @return File
     */
    public static File file(final URL url) {
        return new File(URLUtil.toURI(url));
    }

    /**
     * 返回文件名<br>
     * <pre>
     * "d:/test/aaa" 返回 "aaa"
     * "/test/aaa.jpg" 返回 "aaa.jpg"
     * </pre>
     *
     * @param filePath 文件
     * @return 文件名
     * @see FileNameUtil#getName(String)
     * @since 4.1.13
     */
    public static String getName(String filePath) {
        return FileNameUtil.getName(filePath);
    }

    /**
     * 根据压缩包中的路径构建目录结构，在Win下直接构建，在Linux下拆分路径单独构建
     *
     * @param outFile  最外部路径
     * @param fileName 文件名，可以包含路径
     * @return 文件或目录
     * @since 5.0.5
     */
    private static File buildFile(File outFile, String fileName) {
        // 替换Windows路径分隔符为Linux路径分隔符，便于统一处理
        fileName = fileName.replace('\\', '/');
        if (false == isWindows()
                // 检查文件名中是否包含"/"，不考虑以"/"结尾的情况
                && fileName.lastIndexOf(CharUtil.SLASH, fileName.length() - 2) > 0) {
            // 在Linux下多层目录创建存在问题，/会被当成文件名的一部分，此处做处理
            // 使用/拆分路径（zip中无\），级联创建父目录
            final List<String> pathParts = StrUtil.split(fileName, '/', false, true);
            final int lastPartIndex = pathParts.size() - 1;//目录个数
            for (int i = 0; i < lastPartIndex; i++) {
                //由于路径拆分，slip不检查，在最后一步检查
                outFile = new File(outFile, pathParts.get(i));
            }
            //noinspection ResultOfMethodCallIgnored
            outFile.mkdirs();
            // 最后一个部分如果非空，作为文件名
            fileName = pathParts.get(lastPartIndex);
        }
        return new File(outFile, fileName);
    }
}
