import { Button, Input, Message } from '@arco-design/web-react';
import React, { useCallback, useEffect, useRef, useState } from 'react';

export interface SendVerifyCodeRequest {
  /**
   * 用户名
   */
  userName?: string;
  /**
   * 用户手机号
   */
  userMobile?: string;

  sendType: string;
}

export interface VerifyInputProps {
  verifyType: string;
  userMobile?: string;
  sendVerifyCode: Function;
}

// localStorage key
const getStorageKey = (verifyType: string) => `verify_code_countdown_${verifyType}`;
const COUNTDOWN_DURATION = 60; // 倒计时时长（秒）

/**
 * 验证弹窗组件，初始化时可执行传入的初始化方法
 */
export const VerifyInput: React.FC<VerifyInputProps> = ({ userMobile, verifyType, sendVerifyCode }) => {
  const [countdown, setCountdown] = useState(0);
  const [loading, setLoading] = useState(false);
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
      const sendVerifyCodeRequest: SendVerifyCodeRequest = {
        userMobile: userMobile,
        sendType: verifyType
      };
      await sendVerifyCode(sendVerifyCodeRequest);

      // 模拟API调用
      await new Promise((resolve) => setTimeout(resolve, 500));

      Message.success(verifyType === 'mobile' ? '手机验证码已发送' : '邮箱验证码已发送');
      setCountdown(COUNTDOWN_DURATION);
      saveCountdown();
    } catch (error) {
      console.error('获取验证码失败:', error);
      Message.error('获取验证码失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  return (
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
          style={{
            whiteSpace: 'nowrap',
            padding: '0 4px',
            fontSize: '14px'
          }}
        >
          {countdown > 0 ? `${countdown}秒后重新获取` : '获取手机验证码'}
        </Button>
      }
    />
  );
};
