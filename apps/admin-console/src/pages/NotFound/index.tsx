import { Button } from "@arco-design/web-react";

import { useNavigate } from 'react-router-dom';
// import './index.less';
import styles from './index.module.less';

export default function NotFound() {
  const navigate = useNavigate();
  return (
    <div className={styles.notfoundPage}>
      <div className={styles.content}>
        <div className={styles.contentTitle} >404</div>
        <p >页面未找到</p>
        <Button type="secondary" onClick={() => navigate('/')}>返回首页</Button>
      </div>
    </div>
  );
}