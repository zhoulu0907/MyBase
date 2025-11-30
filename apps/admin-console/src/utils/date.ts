import { Message } from '@arco-design/web-react';

/**
 * 将时间戳转换为格式化日期字符串
 * @param timestamp 时间戳
 * @returns 格式化后的日期字符串 'YYYY-MM-DD HH:mm:ss'
 */
export const formatTimestamp = (timestamp: string | number | null | undefined): string => {
  if (!timestamp) {
    return '--';
  }

  const date = new Date(timestamp);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

/**
 * 生成时间戳字符串，格式为 YYYYMMDDHHmmss
 * @returns 时间戳字符串
 */
export const generateTimestampString = (): string => {
  const now = new Date();
  const year = now.getFullYear();
  const month = (now.getMonth() + 1).toString().padStart(2, '0');
  const day = now.getDate().toString().padStart(2, '0');
  const hours = now.getHours().toString().padStart(2, '0');
  const minutes = now.getMinutes().toString().padStart(2, '0');
  const seconds = now.getSeconds().toString().padStart(2, '0');

  return `${year}${month}${day}${hours}${minutes}${seconds}`;
};

/** 复制 */
export const copyToClipboard = async (text: string) => {
  try {
    // 首先尝试使用现代 Clipboard API
    if (navigator.clipboard && window.isSecureContext) {
      await navigator.clipboard.writeText(text);
      Message.success('复制成功!');
    } else {
      // 降级到传统方法
      fallbackCopyToClipboard(text);
    }
  } catch (error) {
    console.error('复制失败:', error);
    Message.error('复制失败');
  }
};

export const fallbackCopyToClipboard = (text: string) => {
  const textArea = document.createElement('textarea');
  textArea.value = text;
  textArea.style.position = 'fixed';
  textArea.style.opacity = '0';
  textArea.style.top = '0';
  textArea.style.left = '0';
  document.body.appendChild(textArea);
  textArea.select();
  try {
    document.execCommand('copy');
    Message.success('复制成功!');
  } catch (err) {
    console.error('execCommand 失败:', err);
    Message.error('复制失败');
  }
  document.body.removeChild(textArea);
};

// 简化URL显示
export const simplifyUrl = (url: string) => {
  try {
    const urlObj = new URL(url);
    const host = urlObj.host;
    const protocol = urlObj.protocol;
    const hash = urlObj.hash;

    // 如果主机名很短，直接返回
    if (host.length <= 20) {
      return url;
    }

    // 省略主机名中间部分
    const simplifiedHost = `${host.substring(0, 16)}...`;
    return `${protocol}//${simplifiedHost}/${hash}`;
  } catch (e) {
    // 如果URL解析失败，返回原始URL
    return url;
  }
};
