import { Button, Input, Message, Modal } from '@arco-design/web-react';
import { IconInfoCircleFill } from '@arco-design/web-react/icon';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import styles from '../index.module.less';

export interface VerifyModalProps {
  verifyType: string;
  visible: boolean;
  onCancel: () => void;
  onOk: (values: any) => void;
}

// localStorage key
const getStorageKey = (verifyType: string) => `verify_code_countdown_${verifyType}`;
const COUNTDOWN_DURATION = 60; // 倒计时时长（秒）

/**
 * 验证弹窗组件，初始化时可执行传入的初始化方法
 */
export const VerifyModal: React.FC<VerifyModalProps> = ({ verifyType, visible, onCancel, onOk }) => {
  const [countdown, setCountdown] = useState(0);
  const [loading, setLoading] = useState(false);
  const [loginLoading, setLoginLoading] = useState(false);
  const [verifyCode, setVerifyCode] = useState('');
  const timerRef = useRef<NodeJS.Timeout | null>(null);

  // 从 localStorage 恢复倒计时
  const restoreCountdown = useCallback(() => {
    const storageKey = getStorageKey(verifyType);
    const savedEndTime = localStorage.getItem(storageKey);

    if (savedEndTime) {
      const endTime = parseInt(savedEndTime, 10);
      const now = Math.floor(Date.now() / 1000);
      const remaining = endTime - now;

      if (remaining > 0) {
        setCountdown(remaining);
        return true;
      } else {
        // 倒计时已过期，清除存储
        localStorage.removeItem(storageKey);
        setCountdown(0);
        return false;
      }
    }
    return false;
  }, [verifyType]);

  // 保存倒计时结束时间到 localStorage
  const saveCountdown = useCallback(() => {
    const storageKey = getStorageKey(verifyType);
    const endTime = Math.floor(Date.now() / 1000) + COUNTDOWN_DURATION;
    localStorage.setItem(storageKey, endTime.toString());
  }, [verifyType]);

  // 清除倒计时存储
  const clearCountdownStorage = useCallback(() => {
    const storageKey = getStorageKey(verifyType);
    localStorage.removeItem(storageKey);
  }, [verifyType]);

  // 组件初始化时恢复倒计时
  useEffect(() => {
    restoreCountdown();
  }, [restoreCountdown]);

  // 弹窗打开时恢复倒计时
  useEffect(() => {
    if (visible) {
      restoreCountdown();
    }
  }, [visible, restoreCountdown]);

  // 清理定时器
  useEffect(() => {
    return () => {
      if (timerRef.current) {
        clearInterval(timerRef.current);
      }
    };
  }, []);

  // 倒计时逻辑
  useEffect(() => {
    if (countdown > 0) {
      timerRef.current = setInterval(() => {
        setCountdown((prev) => {
          if (prev <= 1) {
            if (timerRef.current) {
              clearInterval(timerRef.current);
            }
            clearCountdownStorage();
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    }

    return () => {
      if (timerRef.current) {
        clearInterval(timerRef.current);
      }
    };
  }, [countdown, clearCountdownStorage]);

  // 获取验证码
  const handleGetVerifyCode = async () => {
    if (countdown > 0) {
      return;
    }

    try {
      setLoading(true);
      // TODO(mickey): 调用获取验证码API
      // const api = verifyType === 'phone' ? getPhoneVerifyCodeApi : getEmailVerifyCodeApi;
      // await api();

      // 模拟API调用
      await new Promise((resolve) => setTimeout(resolve, 500));

      Message.success(verifyType === 'phone' ? '手机验证码已发送' : '邮箱验证码已发送');
      setCountdown(COUNTDOWN_DURATION);
      saveCountdown();
    } catch (error) {
      console.error('获取验证码失败:', error);
      Message.error('获取验证码失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  // 处理验证并登录
  const handleVerifyAndLogin = async () => {
    if (!verifyCode.trim()) {
      Message.warning('请输入验证码');
      return;
    }

    try {
      setLoginLoading(true);
      await onOk({ verifyCode: verifyCode.trim(), verifyType });
    } catch (error) {
      console.error('登录失败:', error);
    } finally {
      setLoginLoading(false);
    }
  };

  // 弹窗关闭时重置验证码
  useEffect(() => {
    if (!visible) {
      setVerifyCode('');
    }
  }, [visible]);

  return (
    <Modal
      title={
        <div className={styles.verifyModalTitle}>
          <IconInfoCircleFill style={{ color: '#FF7D00' }} />
          账号安全验证
        </div>
      }
      visible={visible}
      onCancel={onCancel}
      footer={
        <div className={styles.verifyModalFooter}>
          <Button type="default" onClick={onCancel}>
            取消登录
          </Button>
          <Button type="primary" onClick={handleVerifyAndLogin} loading={loginLoading}>
            验证并登录
          </Button>
        </div>
      }
    >
      <div className={styles.verifyModalContent}>
        <div className={styles.verifyModalContentText}>
          为保护你和企业业务数据安全，平台已开启每次登录验证，验证码已发送至绑定的手机号 / 邮箱，输入后即可安全登录
        </div>
        {verifyType === 'phone' ? (
          <Input
            placeholder="请输入手机验证码"
            value={verifyCode}
            onChange={(value) => setVerifyCode(value)}
            suffix={
              <Button
                type="text"
                size="small"
                disabled={countdown > 0}
                loading={loading}
                onClick={handleGetVerifyCode}
                className={styles.getVerifyCodeBtn}
              >
                {countdown > 0 ? `${countdown}秒后重新获取` : '获取手机验证码'}
              </Button>
            }
          />
        ) : (
          <Input
            placeholder="请输入邮箱验证码"
            value={verifyCode}
            onChange={(value) => setVerifyCode(value)}
            suffix={
              <Button
                type="text"
                size="small"
                disabled={countdown > 0}
                loading={loading}
                onClick={handleGetVerifyCode}
                className={styles.getVerifyCodeBtn}
              >
                {countdown > 0 ? `${countdown}秒后重新获取` : '获取邮箱验证码'}
              </Button>
            }
          />
        )}
      </div>
    </Modal>
  );
};
