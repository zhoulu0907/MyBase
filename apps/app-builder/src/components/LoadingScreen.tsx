import { Spin } from '@arco-design/web-react';
import React from 'react';

interface LoadingScreenProps {
  message?: string;
}

/**
 * 加载屏幕组件
 * 在检查登录状态时显示
 */
const LoadingScreen: React.FC<LoadingScreenProps> = ({ message = '正在检查登录状态...' }) => {
  return (
    <div
      style={{
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        backgroundColor: '#f6f8fa',
        zIndex: 9999
      }}
    >
      <div
        style={{
          textAlign: 'center',
          padding: '24px'
        }}
      >
        <Spin size={40} />
        <div
          style={{
            marginTop: '16px',
            color: '#666',
            fontSize: '14px'
          }}
        >
          {message}
        </div>
      </div>
    </div>
  );
};

export default LoadingScreen;
