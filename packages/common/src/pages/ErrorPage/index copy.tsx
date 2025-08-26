import { Button } from '@arco-design/web-react';
import type { FallbackProps } from 'react-error-boundary';

export function ErrorPage({ error }: FallbackProps) {
  return (
    <div
      style={{
        width: '100vw',
        height: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #e0f2ff 0%, #9fd4fd 50%, #ffffff 100%)',
        backgroundSize: '200% 200%',
        animation: 'gradientBG 10s ease-in-out infinite'
      }}
    >
      <div
        style={{
          textAlign: 'center',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'flex-start'
        }}
      >
        <div
          style={{
            fontSize: '150px',
            fontWeight: 500
          }}
        >
          500
        </div>
        <p
          style={{
            fontSize: '25px',
            color: 'white'
          }}
        >
          出错啦
        </p>
        <pre style={{ color: 'transparent' }}>{error.message}</pre>
        <Button type="secondary" onClick={() => (window.location.href = '/onebase/my-app')}>
          返回首页
        </Button>
      </div>
      <style>{`
        @keyframes gradientBG {
          0% { background-position: 0% 50%; }
          50% { background-position: 100% 50%; }
          100% { background-position: 0% 50%; }
        }
      `}</style>
    </div>
  );
}
