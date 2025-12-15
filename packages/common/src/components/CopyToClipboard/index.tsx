import { Message } from '@arco-design/web-react';

const fallbackCopyToClipboard = (text: string) => {
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
