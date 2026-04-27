import { useEffect, useState } from 'react';

/**
 * 记住我功能 Hook
 * 处理账号信息的持久化存储
 */
export const useRememberMe = () => {
  const [rememberMe, setRememberMe] = useState(false);
  const [savedAccount, setSavedAccount] = useState('');

  // 初始化时检查记住我状态
  useEffect(() => {
    try {
      const remembered = localStorage.getItem('onebase_remember_me');
      if (remembered === 'true') {
        setRememberMe(true);
        const account = localStorage.getItem('onebase_account');
        if (account) {
          setSavedAccount(account);
        }
      }
    } catch (error) {
      console.error('获取记住我状态失败:', error);
    }
  }, []);

  // 保存记住我状态和账号
  const saveRememberMe = (account: string, remember: boolean) => {
    try {
      if (remember) {
        localStorage.setItem('onebase_remember_me', 'true');
        localStorage.setItem('onebase_account', account);
      } else {
        localStorage.removeItem('onebase_remember_me');
        localStorage.removeItem('onebase_account');
      }
      setRememberMe(remember);
      setSavedAccount(account);
    } catch (error) {
      console.error('保存记住我状态失败:', error);
    }
  };

  // 清除记住我状态
  const clearRememberMe = () => {
    try {
      localStorage.removeItem('onebase_remember_me');
      localStorage.removeItem('onebase_account');
      setRememberMe(false);
      setSavedAccount('');
    } catch (error) {
      console.error('清除记住我状态失败:', error);
    }
  };

  return {
    rememberMe,
    savedAccount,
    saveRememberMe,
    clearRememberMe
  };
};
