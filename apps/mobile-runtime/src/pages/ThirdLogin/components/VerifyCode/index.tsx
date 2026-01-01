import safeIcon from '@/assets/images/login/safe.svg';
import { Tabs, Form, Input, Button, Toast } from '@arco-design/mobile-react';
import React, { useState, useEffect, useCallback, useRef } from 'react';
import styles from './index.module.less';

interface VerifyInputProps {
  value?: string;
  userMobile?: string;
  verifyType: string;
  sendVerifyCode: Function;
  onChange?: (value: string) => void;
}

interface SendVerifyCodeRequest {
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

const VerifyCode: React.FC<VerifyInputProps> = ({ value, userMobile, verifyType, sendVerifyCode, onChange }) => {
  const handleChange = (newValue: string) => {
    onChange && onChange(newValue);
  };

  const COUNTDOWN_DURATION = 60; // 倒计时时长（秒）
  const storageKey = `mobile_verify_code_countdown_${verifyType}`;

  // 发送短信接口loading
  const [loading, setLoading] = useState(false);
  // 短信倒计时
  const [countdown, setCountdown] = useState(0);
  const timerRef = useRef<NodeJS.Timeout | null>(null);

  // 恢复倒计时
  useEffect(() => {
    restoreCountdown();
  }, []);

  // 清理定时器
  useEffect(() => {
    return () => {
      if (timerRef.current) {
        clearTimeout(timerRef.current);
      }
    };
  }, []);

  // 倒计时逻辑
  useEffect(() => {
    if (countdown <= 0) {
      clearCountdownStorage();
      return;
    }
    timerRef.current = setTimeout(() => {
      setCountdown((prev) => prev - 1);
    }, 1000);
  }, [countdown]);

  // 保存倒计时结束时间到 localStorage
  const saveCountdown = () => {
    const endTime = Math.floor(Date.now() / 1000) + COUNTDOWN_DURATION;
    localStorage.setItem(storageKey, endTime.toString());
  };

  // 从 localStorage 恢复倒计时
  const restoreCountdown = () => {
    const savedEndTime = localStorage.getItem(storageKey);
    if (savedEndTime) {
      const endTime = parseInt(savedEndTime, 10);
      const now = Math.floor(Date.now() / 1000);
      const remaining = endTime - now;

      if (remaining > 0) {
        setCountdown(remaining);
      } else {
        // 倒计时已过期，清除存储
        localStorage.removeItem(storageKey);
        setCountdown(0);
      }
    }
  };

  // 清除倒计时存储
  const clearCountdownStorage = () => {
    localStorage.removeItem(storageKey);
  };

  // 获取验证码
  const handleGetVerifyCode = async () => {
    if (countdown > 0 || !userMobile) {
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

      Toast.success(verifyType === 'mobile' ? '手机验证码已发送' : '邮箱验证码已发送');
      setCountdown(COUNTDOWN_DURATION);
      saveCountdown();
    } catch (error) {
      console.error('获取验证码失败:', error);
      Toast.error('获取验证码失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Input
      className={styles.verifyCode}
      label={<img src={safeIcon} alt="logo" className={styles.loginFormIcon} />}
      suffix={
        <Button
          type="ghost"
          size="small"
          disabled={countdown > 0}
          loading={loading}
          borderColor="transparent"
          className={styles.loginFormSuffix}
          onClick={handleGetVerifyCode}
        >
          {countdown > 0 ? `${countdown}秒后重新获取` : '获取验证码'}
        </Button>
      }
      value={value}
      placeholder="请输入短信验证码"
      onChange={(_, value) => {
        handleChange(value);
      }}
    />
  );
};

export default VerifyCode;
