import { Button } from '@arco-design/web-react';
import { useNavigate } from 'react-router-dom';
import './index.css';

export function NotFoundPage() {
  const navigate = useNavigate();
  return (
    <div className="notfoundPage">
      <div className="content">
        <div className="contentTitle">404</div>
        <p>页面未找到</p>
        <Button type="secondary" onClick={() => navigate('/')}>
          返回首页
        </Button>
      </div>
    </div>
  );
}
