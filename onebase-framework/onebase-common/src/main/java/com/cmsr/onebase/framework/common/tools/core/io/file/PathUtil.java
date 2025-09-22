package com.cmsr.onebase.framework.common.tools.core.io.file;

import com.cmsr.onebase.framework.common.tools.core.io.IORuntimeException;
import com.cmsr.onebase.framework.common.tools.core.io.file.visitor.DelVisitor;
import com.cmsr.onebase.framework.common.tools.core.lang.Assert;

import java.io.IOException;
import java.nio.file.*;
import java.util.EnumSet;
import java.util.Set;

public class PathUtil {

    /**
     * 判断给定的目录是否为给定文件或文件夹的子目录
     *
     * @param parent 父目录
     * @param sub    子目录
     * @return 子目录是否为父目录的子目录
     * @since 5.5.5
     */
    public static boolean isSub(Path parent, Path sub) {
        return toAbsNormal(sub).startsWith(toAbsNormal(parent));
    }

    /**
     * 将Path路径转换为标准的绝对路径
     *
     * @param path 文件或目录Path
     * @return 转换后的Path
     * @since 5.5.5
     */
    public static Path toAbsNormal(Path path) {
        Assert.notNull(path);
        return path.toAbsolutePath().normalize();
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
     * 修改文件或目录的文件名，不变更路径，只是简单修改文件名<br>
     *
     * <pre>
     * FileUtil.rename(file, "aaa.jpg", false) xx/xx.png =》xx/aaa.jpg
     * </pre>
     *
     * @param path       被修改的文件
     * @param newName    新的文件名，包括扩展名
     * @param isOverride 是否覆盖目标文件
     * @return 目标文件Path
     * @since 5.4.1
     */
    public static Path rename(Path path, String newName, boolean isOverride) {
        return move(path, path.resolveSibling(newName), isOverride);
    }

    /**
     * 移动文件或目录到目标中，例如：
     * <ul>
     *     <li>如果src和target为同一文件或目录，直接返回target。</li>
     *     <li>如果src为文件，target为目录，则移动到目标目录下，存在同名文件则按照是否覆盖参数执行。</li>
     *     <li>如果src为文件，target为文件，则按照是否覆盖参数执行。</li>
     *     <li>如果src为文件，target为不存在的路径，则重命名源文件到目标指定的文件，如moveContent("/a/b", "/c/d"), d不存在，则b变成d。</li>
     *     <li>如果src为目录，target为文件，抛出{@link IllegalArgumentException}</li>
     *     <li>如果src为目录，target为目录，则将源目录及其内容移动到目标路径目录中，如move("/a/b", "/c/d")，结果为"/c/d/b"</li>
     *     <li>如果src为目录，target为不存在的路径，则重命名src到target，如move("/a/b", "/c/d")，结果为"/c/d/"，相当于b重命名为d</li>
     * </ul>
     *
     * @param src        源文件或目录路径
     * @param target     目标路径，如果为目录，则移动到此目录下
     * @param isOverride 是否覆盖目标文件
     * @return 目标文件Path
     */
    public static Path move(Path src, Path target, boolean isOverride) {
        return PathMover.of(src, target, isOverride).move();
    }

    /**
     * 删除文件或者文件夹，不追踪软链<br>
     * 注意：删除文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param path 文件对象
     * @return 成功与否
     * @throws IORuntimeException IO异常
     * @since 4.4.2
     */
    public static boolean del(Path path) throws IORuntimeException {
        if (Files.notExists(path)) {
            return true;
        }

        try {
            if (isDirectory(path)) {
                Files.walkFileTree(path, DelVisitor.INSTANCE);
            } else {
                delFile(path);
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return true;
    }

    /**
     * 删除文件或空目录，不追踪软链
     *
     * @param path 文件对象
     * @throws IOException IO异常
     * @since 5.7.7
     */
    protected static void delFile(Path path) throws IOException {
        try {
            Files.delete(path);
        } catch (AccessDeniedException e) {
            // 可能遇到只读文件，无法删除.使用 file 方法删除
            if (false == path.toFile().delete()) {
                throw e;
            }
        }
    }

    /**
     * 遍历指定path下的文件并做处理
     *
     * @param start    起始路径，必须为目录
     * @param maxDepth 最大遍历深度，-1表示不限制深度
     * @param visitor  {@link FileVisitor} 接口，用于自定义在访问文件时，访问目录前后等节点做的操作
     * @see Files#walkFileTree(Path, Set, int, FileVisitor)
     * @since 4.6.3
     */
    public static void walkFiles(final Path start, final int maxDepth, final FileVisitor<? super Path> visitor) {
        walkFiles(start, maxDepth, false, visitor);
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
     * 判断文件或目录是否存在
     *
     * @param path          文件，{@code null}返回{@code false}
     * @param isFollowLinks 是否跟踪软链（快捷方式）
     * @return 是否存在
     * @since 5.5.3
     */
    public static boolean exists(final Path path, final boolean isFollowLinks) {
        if (null == path) {
            return false;
        }
        return Files.exists(path, getLinkOptions(isFollowLinks));
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
     * 获取指定位置的最后一个子路径部分
     *
     * @param path 路径
     * @return 获取的最后一个子路径
     * @since 3.1.2
     */
    public static Path getLastPathEle(final Path path) {
        return getPathEle(path, path.getNameCount() - 1);
    }

    /**
     * 获取指定位置的子路径部分，支持负数，例如index为-1表示从后数第一个节点位置
     *
     * @param path  路径
     * @param index 路径节点位置，支持负数（负数从后向前计数）
     * @return 获取的子路径
     * @since 3.1.2
     */
    public static Path getPathEle(final Path path, final int index) {
        return subPath(path, index, index == -1 ? path.getNameCount() : index + 1);
    }

    /**
     * 获取指定位置的子路径部分，支持负数，例如起始为-1表示从后数第一个节点位置
     *
     * @param path      路径
     * @param fromIndex 起始路径节点（包括）
     * @param toIndex   结束路径节点（不包括）
     * @return 获取的子路径
     * @since 3.1.2
     */
    public static Path subPath(final Path path, int fromIndex, int toIndex) {
        if (null == path) {
            return null;
        }
        final int len = path.getNameCount();

        if (fromIndex < 0) {
            fromIndex = len + fromIndex;
            if (fromIndex < 0) {
                fromIndex = 0;
            }
        } else if (fromIndex > len) {
            fromIndex = len;
        }

        if (toIndex < 0) {
            toIndex = len + toIndex;
            if (toIndex < 0) {
                toIndex = len;
            }
        } else if (toIndex > len) {
            toIndex = len;
        }

        if (toIndex < fromIndex) {
            final int tmp = fromIndex;
            fromIndex = toIndex;
            toIndex = tmp;
        }

        if (fromIndex == toIndex) {
            return null;
        }
        return path.subpath(fromIndex, toIndex);
    }

    /**
     * 创建所给目录及其父目录
     *
     * @param dir 目录
     * @return 目录
     * @since 5.5.7
     */
    public static Path mkdir(final Path dir) {
        if (null != dir && !exists(dir, false)) {
            try {
                Files.createDirectories(dir);
            } catch (final IOException e) {
                throw new IORuntimeException(e);
            }
        }
        return dir;
    }

    /**
     * 判断是否为文件，如果file为null，则返回false
     *
     * @param path          文件
     * @param isFollowLinks 是否跟踪软链（快捷方式）
     * @return 如果为文件true
     * @see Files#isRegularFile(Path, LinkOption...)
     */
    public static boolean isFile(final Path path, final boolean isFollowLinks) {
        if (null == path) {
            return false;
        }
        return Files.isRegularFile(path, getLinkOptions(isFollowLinks));
    }
}
