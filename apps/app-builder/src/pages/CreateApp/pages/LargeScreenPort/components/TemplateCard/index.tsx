import { useEffect, type FC } from 'react';
import styles from './index.module.less';
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
      <div>{item.id}</div>
      <div>{item.name}</div>
    </div>
  );
};
export default ScreenCard;
