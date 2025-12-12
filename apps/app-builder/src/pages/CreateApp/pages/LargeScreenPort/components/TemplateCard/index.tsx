import { useEffect, type FC } from 'react';
import { IconEdit } from '@arco-design/web-react/icon';
import { Space, Button } from '@arco-design/web-react';
import screenTemplate from '@/assets/images/screen/screenTemplate.png';
import styles from './index.module.less';
import eye from '@/assets/images/screen/eye.png';
interface dataList {
  id: string;
  name: string;
  desc: string;
}
interface CardProps {
  item: {
    id: string;
    name: string;
    desc: string;
  };
  title: string;
  onEditTemplate: (item: dataList) => void;
  onEdit: (item: dataList) => void;
  onPreview: (item: dataList) => void;
}
const ScreenCard: FC<CardProps> = ({ item, title, onEditTemplate, onEdit, onPreview }) => {
  useEffect(() => {}, []);

  return (
    <div className={styles.appCard}>
      <div className={styles.appCardImg}>
        <img src={screenTemplate} alt="" />
      </div>
      <div className={`${styles.appCardFooter} ${styles.cardName}`}>
        <div>
          {item.name}
          <IconEdit
            fontSize={16}
            style={{ marginLeft: 4 }}
            onClick={(e) => {
              e.stopPropagation();
              onEditTemplate(item);
            }}
          />
        </div>
        <div className={styles.cardState}>{title}</div>
      </div>
      <div className={styles.dec}>描述信息描述信息描述信息描述信息描述信息描述信息描述信息。</div>

      <div className={styles.footer}>
        <Space size={'medium'}>
          <div
            className={styles.footerRight}
            onClick={(e) => {
              e.stopPropagation();
              onPreview(item);
            }}
          >
            <img src={eye} alt="" width="15px" height="10px" />
            <span style={{ marginLeft: 8 }}>预览</span>
          </div>
          <Button
            type="primary"
            size="mini"
            onClick={(e) => {
              e.stopPropagation();
              onEdit(item);
            }}
          >
            编辑
          </Button>
        </Space>
      </div>
    </div>
  );
};
export default ScreenCard;
