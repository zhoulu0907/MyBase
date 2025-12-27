import { useEffect, type FC } from 'react';
import { IconEdit, IconMoreVertical, IconDelete } from '@arco-design/web-react/icon';
import { Space, Button, Menu, Dropdown } from '@arco-design/web-react';
import screenTemplate from '@/assets/images/screen/screenTemplate.png';
import styles from './index.module.less';
import eye from '@/assets/images/screen/eye.png';
import { getFileUrlById } from '@onebase/platform-center';
interface dataList {
  id: string;
  templateName: string;
  templateType: string;
  remarks: string;
  indexImage: string;
}
interface CardProps {
  item: {
    remarks: string;
    id: string;
    templateName: string;
    templateType: string;
    indexImage: string;
  };
  onEditTemplate: (item: dataList) => void;
  onEdit: (item: dataList) => void;
  onPreview: (item: dataList) => void;
  onDelete: (item: dataList) => void;
}
const ScreenCard: FC<CardProps> = ({ item, onEditTemplate, onEdit, onPreview, onDelete }) => {
  const menu = (
    <Menu style={{ borderRadius: 10 }}>
      <Menu.Item
        key="1"
        onClick={(e) => {
          e.stopPropagation();
          onDelete(item);
        }}
        style={{ color: 'red' }}
      >
        <div className={styles.menuItem}>
          <IconDelete style={{ marginRight: 4 }} fontSize={18} />
          <span>删除</span>
        </div>
      </Menu.Item>
    </Menu>
  );

  return (
    <div className={styles.appCard}>
      <div className={styles.appCardImg}>
        <img src={getFileUrlById(item.indexImage)} alt="" />
      </div>
      <div className={`${styles.appCardFooter} ${styles.cardName}`}>
        <div>
          {item.templateName}
          <IconEdit
            fontSize={16}
            style={{ marginLeft: 4 }}
            onClick={(e) => {
              e.stopPropagation();
              onEditTemplate(item);
            }}
          />
        </div>
        <div className={styles.cardState}>{item.templateType === 'app' ? '应用模板' : '系统模板'}</div>
      </div>
      <div className={styles.dec}>{item.remarks}</div>

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
          <Dropdown droplist={menu} trigger="click" position="bottom">
            <IconMoreVertical className={styles.operationIcon} fontSize={14} style={{ color: '#272e3b' }} />
          </Dropdown>
        </Space>
      </div>
    </div>
  );
};
export default ScreenCard;
