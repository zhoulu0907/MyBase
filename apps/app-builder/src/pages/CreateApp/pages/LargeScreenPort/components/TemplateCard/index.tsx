import { useEffect, type FC } from 'react';
import { IconEdit } from '@arco-design/web-react/icon';
import { Space, Button } from '@arco-design/web-react';
import screenTemplate from '@/assets/images/screen/screenTemplate.png';
import styles from './index.module.less';
import eye from '@/assets/images/screen/eye.png';

interface CardProps {
  item: {
    id: string;
    name: string;
  };
}
const ScreenCard: FC<CardProps> = ({ item }) => {
  useEffect(() => {}, []);
  return (
    <div className={styles.appCard}>
      <div className={styles.appCardImg}>
        <img src={screenTemplate} alt="" />
      </div>
      <div className={`${styles.appCardFooter} ${styles.cardName}`}>
        <div>
          {item.name} <IconEdit fontSize={16} onClick={() => onEdit(item.id)} />
        </div>
        <div className={styles.cardState}>应用模板</div>
      </div>
      <p className={styles.dec}>描述信息描述信息描述信息描述信息描述信息描述信息描述信息。</p>

      <div className={styles.footer}>
        <Space size={'medium'}>
          <img src={eye} alt="" width="15px" height="10px" />
          <span>预览</span>
          <Button type="primary" size="mini">
            编辑
          </Button>
        </Space>
      </div>
    </div>
  );
};
export default ScreenCard;
