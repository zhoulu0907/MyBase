package com.cmsr.onebase.framework.common.tools.extra.ssh;

import com.cmsr.onebase.framework.common.tools.core.lang.Assert;
import com.cmsr.onebase.framework.common.tools.core.util.StrUtil;
import com.jcraft.jsch.*;

/**
 * Jsch工具类<br>
 * Jsch是Java Secure Channel的缩写。JSch是一个SSH2的纯Java实现。<br>
 * 它允许你连接到一个SSH服务器，并且可以使用端口转发，X11转发，文件传输等。<br>
 *
 * @author Looly
 * @since 4.0.0
 */
public class JschUtil {

    /**
     * 打开SFTP连接
     *
     * @param session Session会话
     * @param timeout 连接超时时长，单位毫秒
     * @return {@link ChannelSftp}
     * @since 5.3.3
     */
    public static ChannelSftp openSftp(Session session, int timeout) {
        return (ChannelSftp) openChannel(session, ChannelType.SFTP, timeout);
    }

    /**
     * 打开Channel连接
     *
     * @param session     Session会话
     * @param channelType 通道类型，可以是shell或sftp等，见{@link ChannelType}
     * @param timeout     连接超时时长，单位毫秒
     * @return {@link Channel}
     * @since 5.3.3
     */
    public static Channel openChannel(Session session, ChannelType channelType, int timeout) {
        final Channel channel = createChannel(session, channelType);
        try {
            channel.connect(Math.max(timeout, 0));
        } catch (JSchException e) {
            throw new JschRuntimeException(e);
        }
        return channel;
    }

    /**
     * 创建Channel连接
     *
     * @param session     Session会话
     * @param channelType 通道类型，可以是shell或sftp等，见{@link ChannelType}
     * @return {@link Channel}
     * @since 4.5.2
     */
    public static Channel createChannel(Session session, ChannelType channelType) {
        Channel channel;
        try {
            if (false == session.isConnected()) {
                session.connect();
            }
            channel = session.openChannel(channelType.getValue());
        } catch (JSchException e) {
            throw new JschRuntimeException(e);
        }
        return channel;
    }

    /**
     * 获得一个SSH会话，重用已经使用的会话
     *
     * @param sshHost 主机
     * @param sshPort 端口
     * @param sshUser 用户名
     * @param sshPass 密码
     * @return SSH会话
     */
    public static Session getSession(String sshHost, int sshPort, String sshUser, String sshPass) {
        return JschSessionPool.INSTANCE.getSession(sshHost, sshPort, sshUser, sshPass);
    }

    /**
     * 打开一个新的SSH会话
     *
     * @param sshHost 主机
     * @param sshPort 端口
     * @param sshUser 用户名
     * @param sshPass 密码
     * @return SSH会话
     */
    public static Session openSession(String sshHost, int sshPort, String sshUser, String sshPass) {
        return openSession(sshHost, sshPort, sshUser, sshPass, 0);
    }

    /**
     * 打开一个新的SSH会话
     *
     * @param sshHost        主机
     * @param sshPort        端口
     * @param sshUser        用户名
     * @param privateKeyPath 私钥的路径
     * @param passphrase     私钥文件的密码，可以为null
     * @return SSH会话
     */
    public static Session openSession(String sshHost, int sshPort, String sshUser, String privateKeyPath, byte[] passphrase) {
        return openSession(sshHost, sshPort, sshUser, privateKeyPath, passphrase, 0);
    }

    /**
     * 打开一个新的SSH会话
     *
     * @param sshHost        主机
     * @param sshPort        端口
     * @param sshUser        用户名
     * @param privateKeyPath 私钥的路径
     * @param passphrase     私钥文件的密码，可以为null
     * @param timeOut        超时时间，单位毫秒
     * @return SSH会话
     * @since 5.8.4
     */
    public static Session openSession(String sshHost, int sshPort, String sshUser, String privateKeyPath, byte[] passphrase, int timeOut) {
        final Session session = createSession(sshHost, sshPort, sshUser, privateKeyPath, passphrase);
        try {
            session.connect(timeOut);
        } catch (JSchException e) {
            throw new JschRuntimeException(e);
        }
        return session;
    }

    /**
     * 新建一个新的SSH会话，此方法并不打开会话（既不调用connect方法）
     *
     * @param sshHost        主机
     * @param sshPort        端口
     * @param sshUser        用户名，如果为null，默认root
     * @param privateKeyPath 私钥的路径
     * @param passphrase     私钥文件的密码，可以为null
     * @return SSH会话
     * @since 5.0.0
     */
    public static Session createSession(String sshHost, int sshPort, String sshUser, String privateKeyPath, byte[] passphrase) {
        Assert.notEmpty(privateKeyPath, "PrivateKey Path must be not empty!");

        final JSch jsch = new JSch();
        try {
            jsch.addIdentity(privateKeyPath, passphrase);
        } catch (JSchException e) {
            throw new JschRuntimeException(e);
        }

        return createSession(jsch, sshHost, sshPort, sshUser);
    }

    /**
     * 打开一个新的SSH会话
     *
     * @param sshHost 主机
     * @param sshPort 端口
     * @param sshUser 用户名
     * @param sshPass 密码
     * @param timeout Socket连接超时时长，单位毫秒
     * @return SSH会话
     * @since 5.3.3
     */
    public static Session openSession(String sshHost, int sshPort, String sshUser, String sshPass, int timeout) {
        final Session session = createSession(sshHost, sshPort, sshUser, sshPass);
        try {
            session.connect(timeout);
        } catch (JSchException e) {
            throw new JschRuntimeException(e);
        }
        return session;
    }

    /**
     * 打开一个新的SSH会话
     *
     * @param sshHost    主机
     * @param sshPort    端口
     * @param sshUser    用户名
     * @param privateKey 私钥内容
     * @param passphrase 私钥文件的密码，可以为null
     * @return SSH会话
     * @since 5.8.18
     */
    public static Session openSession(String sshHost, int sshPort, String sshUser, byte[] privateKey, byte[] passphrase) {
        return openSession(sshHost, sshPort, sshUser, privateKey, passphrase, 0);
    }

    /**
     * 打开一个新的SSH会话
     *
     * @param sshHost    主机
     * @param sshPort    端口
     * @param sshUser    用户名
     * @param privateKey 私钥内容
     * @param passphrase 私钥文件的密码，可以为null
     * @param timeOut    超时时长
     * @return SSH会话
     * @since 5.8.18
     */
    public static Session openSession(String sshHost, int sshPort, String sshUser, byte[] privateKey, byte[] passphrase, int timeOut) {
        final Session session = createSession(sshHost, sshPort, sshUser, privateKey, passphrase);
        try {
            session.connect(timeOut);
        } catch (JSchException e) {
            throw new JschRuntimeException(e);
        }
        return session;
    }

    /**
     * 新建一个新的SSH会话，此方法并不打开会话（既不调用connect方法）
     *
     * @param sshHost    主机
     * @param sshPort    端口
     * @param sshUser    用户名，如果为null，默认root
     * @param privateKey 私钥内容
     * @param passphrase 私钥文件的密码，可以为null
     * @return SSH会话
     * @since 5.8.18
     */
    public static Session createSession(String sshHost, int sshPort, String sshUser, byte[] privateKey, byte[] passphrase) {
        Assert.isTrue(privateKey != null && privateKey.length > 0, "PrivateKey must be not empty!");

        final JSch jsch = new JSch();
        final String identityName = StrUtil.format("{}@{}:{}", sshUser, sshHost, sshPort);
        try {
            jsch.addIdentity(identityName, privateKey, null, passphrase);
        } catch (JSchException e) {
            throw new JschRuntimeException(e);
        }

        return createSession(jsch, sshHost, sshPort, sshUser);
    }

    /**
     * 新建一个新的SSH会话，此方法并不打开会话（既不调用connect方法）
     *
     * @param sshHost 主机
     * @param sshPort 端口
     * @param sshUser 用户名，如果为null，默认root
     * @param sshPass 密码
     * @return SSH会话
     * @since 4.5.2
     */
    public static Session createSession(String sshHost, int sshPort, String sshUser, String sshPass) {
        final JSch jsch = new JSch();
        final Session session = createSession(jsch, sshHost, sshPort, sshUser);

        if (StrUtil.isNotEmpty(sshPass)) {
            session.setPassword(sshPass);
        }

        return session;
    }

    /**
     * 创建一个SSH会话，重用已经使用的会话
     *
     * @param jsch    {@link JSch}
     * @param sshHost 主机
     * @param sshPort 端口
     * @param sshUser 用户名，如果为null，默认root
     * @return {@link Session}
     * @since 5.0.3
     */
    public static Session createSession(JSch jsch, String sshHost, int sshPort, String sshUser) {
        Assert.notEmpty(sshHost, "SSH Host must be not empty!");
        Assert.isTrue(sshPort > 0, "SSH port must be > 0");

        // 默认root用户
        if (StrUtil.isEmpty(sshUser)) {
            sshUser = "root";
        }

        if (null == jsch) {
            jsch = new JSch();
        }

        Session session;
        try {
            session = jsch.getSession(sshUser, sshHost, sshPort);
        } catch (JSchException e) {
            throw new JschRuntimeException(e);
        }

        // 设置第一次登录的时候提示，可选值：(ask | yes | no)
        session.setConfig("StrictHostKeyChecking", "no");

        // 设置登录认证方式，跳过Kerberos身份验证
        session.setConfig("PreferredAuthentications","publickey,keyboard-interactive,password");

        return session;
    }

    /**
     * 关闭会话通道
     *
     * @param channel 会话通道
     * @since 4.0.3
     */
    public static void close(Channel channel) {
        if (channel != null && channel.isConnected()) {
            channel.disconnect();
        }
    }

    /**
     * 关闭SSH连接会话
     *
     * @param session SSH会话
     */
    public static void close(Session session) {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
        JschSessionPool.INSTANCE.remove(session);
    }
}