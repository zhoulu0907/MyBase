import { useEffect, type FC } from 'react';
import { IconEdit } from '@arco-design/web-react/icon';

import styles from './index.module.less';
import screen1 from '@/assets/images/screen/screen1.png';
interface CardProps {
  item: {
    id: string;
    name: string;
    state: string;
  };
}
const ScreenCard: FC<CardProps> = ({ item }) => {
  useEffect(() => {}, []);
  const onEdit = () => {};
  return (
    <div className={styles.appCard}>
      <div className={styles.appCardImg}>
        <img src={screen1} alt="" />
      </div>
      <div className={`${styles.appCardFooter} ${styles.cardName}`}>
        <div>
          {item.name} <IconEdit fontSize={16} onClick={() => onEdit(item.id)} />
        </div>
        <div className={styles.cardState}>{item.state}</div>
      </div>
      <div className={`${styles.appCardFooter} ${styles.cardRemark}`}>
        <div>
          更新于:<span>2025-11-11</span>
        </div>
        <div></div>
      </div>
    </div>
  );
};
export default ScreenCard;
