import { useEffect, type FC } from 'react';
import { IconEdit, IconDelete, IconMoreVertical } from '@arco-design/web-react/icon';
import { Dropdown, Space, Menu } from '@arco-design/web-react';
import styles from './index.module.less';
import screen1 from '@/assets/images/screen/screen1.png';
import eye from '@/assets/images/screen/eye.png';
import write from '@/assets/images/screen/write.png';
import template from '@/assets/images/screen/template.png';
interface dataList {
  id: string;
  name: string;
  state: string;
  desc: string;
}
interface CardProps {
  item: dataList;
  onDelete: (item: dataList) => void;
  onSaveAs: (item: dataList) => void;
  onEditScreen: (item: dataList) => void;
  onEdit: (item: dataList) => void;
  onPreview: (item: dataList) => void;
}
const ScreenCard: FC<CardProps> = ({ item, onDelete, onSaveAs, onEditScreen, onEdit, onPreview }) => {
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
      <Menu.Item
        key="2"
        onClick={(e) => {
          e.stopPropagation();
          onSaveAs(item);
        }}
      >
        <div className={styles.menuItem}>
          <img src={template} alt="访问应用" style={{ marginRight: 4, marginTop: 2, width: '16px', height: '16px' }} />
          <span>另存为模板</span>
        </div>
      </Menu.Item>
    </Menu>
  );

  useEffect(() => {}, []);

  return (
    <div className={styles.appCard}>
      <div className={styles.appCardImg}>
        <img src={screen1} alt="" />
      </div>
      <div className={`${styles.appCardFooter} ${styles.cardName}`}>
        <div>
          {item.name}
          <IconEdit
            fontSize={16}
            style={{ marginLeft: 4 }}
            onClick={(e) => {
              e.stopPropagation();
              onEditScreen(item);
            }}
          />
        </div>
        <div className={styles.cardState}>
          <div className={styles.cicile}></div>
          <div>{item.state}</div>
        </div>
      </div>
      <div className={`${styles.appCardFooter} ${styles.cardRemark}`}>
        <div>
          更新于: <span>2025-11-11</span>
        </div>
        <div>
          <Space size={'medium'}>
            <div
              className={styles.footerRight}
              onClick={(e) => {
                e.stopPropagation();
                onPreview(item);
              }}
            >
              <img src={eye} alt="" width="15px" height="10px" />
              <span>预览</span>
            </div>
            <div
              className={styles.footerRight}
              onClick={(e) => {
                e.stopPropagation();
                onEdit(item);
              }}
            >
              <img src={write} alt="" width="16px" height="16px" />
              <span>编辑</span>
            </div>
            <Dropdown droplist={menu} trigger="click" position="bottom">
              <IconMoreVertical className={styles.operationIcon} fontSize={14} style={{ color: '#272e3b' }} />
            </Dropdown>
          </Space>
        </div>
      </div>
    </div>
  );
};
export default ScreenCard;
