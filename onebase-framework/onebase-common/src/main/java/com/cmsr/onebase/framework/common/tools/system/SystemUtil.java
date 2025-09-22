package com.cmsr.onebase.framework.common.tools.system;


import com.cmsr.onebase.framework.common.tools.core.convert.Convert;
import com.cmsr.onebase.framework.common.tools.core.lang.Singleton;
import com.cmsr.onebase.framework.common.tools.core.util.StrUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.*;
import java.util.Enumeration;

/**
 * Java的System类封装工具类。<br>
 * 参考：http://blog.csdn.net/zhongweijian/article/details/7619383
 *
 */
public class SystemUtil {

    /**
     * 获取当前进程 PID
     *
     * @return 当前进程 ID
     */
    public static long getCurrentPID() {
        return Long.parseLong(getRuntimeMXBean().getName().split("@")[0]);
    }

    /**
     * 返回Java虚拟机运行时系统相关属性
     *
     * @return {@link RuntimeMXBean}
     * @since 4.1.4
     */
    public static RuntimeMXBean getRuntimeMXBean() {
        return ManagementFactory.getRuntimeMXBean();
    }

    /**
     * 取得Host的信息。
     *
     * @return {@link HostInfo}对象
     */
    public static HostInfo getHostInfo() {
        return Singleton.get(HostInfo.class);
    }

    /**
     * 获取当前主机的 IPv4 地址，优先返回非回环且已启用的网卡地址；若无法获取，回退到 InetAddress.getLocalHost()；
     * 最终兜底为 127.0.0.1
     *
     * @return 主机 IPv4 地址字符串
     */
    public static String getHostAddress() {
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            while (nets != null && nets.hasMoreElements()) {
                NetworkInterface netint = nets.nextElement();
                try {
                    if (netint.isLoopback() || !netint.isUp()) {
                        continue;
                    }
                } catch (SocketException ignored) {
                    continue;
                }
                Enumeration<InetAddress> addrs = netint.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException ignored) {
            // 忽略并尝试后备方案
        }

        try {
            InetAddress local = InetAddress.getLocalHost();
            if (local != null && local.getHostAddress() != null) {
                return local.getHostAddress();
            }
        } catch (UnknownHostException ignored) {
            // 忽略，使用兜底地址
        }

        return "127.0.0.1";
    }

    /**
     * 获取当前进程 PID（JDK 9+ 提供）
     *
     * @return 当前进程 PID
     */
    public static long getCurrentPid() {
        return ProcessHandle.current().pid();
    }

    /**
     * 输出到{@link StringBuilder}。
     *
     * @param builder {@link StringBuilder}对象
     * @param caption 标题
     * @param value   值
     */
    protected static void append(StringBuilder builder, String caption, Object value) {
        builder.append(caption).append(StrUtil.nullToDefault(Convert.toStr(value), "[n/a]")).append("\n");
    }

}
