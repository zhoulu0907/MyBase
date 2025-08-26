import { Button } from '@arco-design/web-react';
import { useNavigate } from 'react-router-dom';

export function NotFoundPage() {
  const navigate = useNavigate();
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
          404
        </div>
        <p
          style={{
            fontSize: '25px',
            color: 'white'
          }}
        >
          页面未找到
        </p>
        <Button type="secondary" onClick={() => navigate('/')}>
          返回首页
        </Button>
      </div>
    </div>
  );
}
