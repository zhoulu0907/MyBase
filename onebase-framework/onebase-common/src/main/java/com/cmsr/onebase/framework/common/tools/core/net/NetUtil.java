package com.cmsr.onebase.framework.common.tools.core.net;

import com.cmsr.onebase.framework.common.tools.core.lang.Filter;
import com.cmsr.onebase.framework.common.tools.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;

import java.net.*;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 网络相关工具
 */
public class NetUtil {

    /**
     * 本地主机名称
     */
    public static String localhostName;

    /**
     * 判断是否为非回环的IPv4地址
     *
     * @param address InetAddress对象
     * @return 是否为非回环的IPv4地址
     */
    public static boolean isNotLoopbackAndIsIPv4(InetAddress address) {
        return address != null
                && !address.isLoopbackAddress()
                // 需为IPV4地址
                && address instanceof Inet4Address;
    }

    /**
     * 获取主机名称，一次获取会缓存名称<br>
     * 注意此方法会触发反向DNS解析，导致阻塞，阻塞时间取决于网络！
     *
     * @return 主机名称
     * @since 5.4.4
     */
    public static String getLocalHostName() {
        if (StrUtil.isNotBlank(localhostName)) {
            return localhostName;
        }

        final InetAddress localhost = getLocalhost();

        if (null != localhost) {
            String name = localhost.getHostName();
            if (StrUtil.isEmpty(name)) {
                name = localhost.getHostAddress();
            }
            localhostName = name;
        }

        return localhostName;
    }

    /**
     * 获取本机网卡IP地址，规则如下：
     *
     * <pre>
     * 1. 查找所有网卡地址，必须非回路（loopback）地址、非局域网地址（siteLocal）、IPv4地址
     * 2. 如果无满足要求的地址，调用 {@link InetAddress#getLocalHost()} 获取地址
     * </pre>
     * <p>
     * 此方法不会抛出异常，获取失败将返回{@code null}<br>
     * <p>
     * 见：https://github.com/dromara/hutool/issues/428
     *
     * @return 本机网卡IP地址，获取失败返回{@code null}
     * @since 3.0.1
     */
    public static InetAddress getLocalhost() {
        final LinkedHashSet<InetAddress> localAddressList = localAddressList(address -> {
            // 非loopback地址，指127.*.*.*的地址
            return !address.isLoopbackAddress()
                    // 需为IPV4地址
                    && address instanceof Inet4Address;
        });

        if (localAddressList != null && !localAddressList.isEmpty()) {
            InetAddress address2 = null;
            for (InetAddress inetAddress : localAddressList) {
                if (!inetAddress.isSiteLocalAddress()) {
                    // 非地区本地地址，指10.0.0.0 ~ 10.255.255.255、172.16.0.0 ~ 172.31.255.255、192.168.0.0 ~ 192.168.255.255
                    return inetAddress;
                } else if (null == address2) {
                    address2 = inetAddress;
                }
            }

            if (null != address2) {
                return address2;
            }
        }

        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            // ignore
        }

        return null;
    }

    /**
     * 获取所有满足过滤条件的本地IP地址对象
     *
     * @param addressFilter 过滤器，null表示不过滤，获取所有地址
     * @return 过滤后的地址对象列表
     * @since 4.5.17
     */
    public static LinkedHashSet<InetAddress> localAddressList(Filter<InetAddress> addressFilter) {
        return localAddressList(null, addressFilter);
    }

    /**
     * 获取所有满足过滤条件的本地IP地址对象
     *
     * @param addressFilter          过滤器，null表示不过滤，获取所有地址
     * @param networkInterfaceFilter 过滤器，null表示不过滤，获取所有网卡
     * @return 过滤后的地址对象列表
     */
    public static LinkedHashSet<InetAddress> localAddressList(Filter<NetworkInterface> networkInterfaceFilter, Filter<InetAddress> addressFilter) {
        Enumeration<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            // 替换 Hutool 的 UtilException
            throw new RuntimeException("Get network interface error!", e);
        }

        if (networkInterfaces == null) {
            // 替换 Hutool 的 UtilException
            throw new RuntimeException("Get network interface error!");
        }

        final LinkedHashSet<InetAddress> ipSet = new LinkedHashSet<>();

        while (networkInterfaces.hasMoreElements()) {
            final NetworkInterface networkInterface = networkInterfaces.nextElement();
            if (networkInterfaceFilter != null && !networkInterfaceFilter.accept(networkInterface)) {
                continue;
            }
            final Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                final InetAddress inetAddress = inetAddresses.nextElement();
                if (inetAddress != null && (null == addressFilter || addressFilter.accept(inetAddress))) {
                    ipSet.add(inetAddress);
                }
            }
        }

        return ipSet;
    }

    /**
     * 检测给定字符串是否为未知，多用于检测HTTP请求相关<br>
     *
     * @param checkString 被检测的字符串
     * @return 是否未知
     * @since 5.2.6
     */
    public static boolean isUnknown(String checkString) {
        return StrUtil.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
    }

    /**
     * 从多级反向代理中获得第一个非unknown IP地址
     *
     * @param ip 获得的IP地址
     * @return 第一个非unknown IP地址
     * @since 4.4.1
     */
    public static String getMultistageReverseProxyIp(String ip) {
        // 多级反向代理检测
        if (ip != null && StringUtils.indexOf(ip, ',') > 0) {
            final List<String> ips = StrUtil.splitTrim(ip, ',');
            for (final String subIp : ips) {
                if (!isUnknown(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }
        return ip;
    }

}
