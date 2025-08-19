import { Button, Input } from '@arco-design/web-react';
import { IconSync, IconLeft } from '@arco-design/web-react/icon';
import { useEffect, useState } from 'react';
import { menuIconList, menuIconType, type MenuItem } from './const';
import styles from './index.module.less';

const InputSearch = Input.Search;

interface IProps {
  style: React.CSSProperties;
  handleBack: () => void;
  onSelected: (val: string) => void;
}

// 菜单图标
const MenuIcon = (props: IProps) => {
  const { style, onSelected, handleBack } = props;
  const [data, setData] = useState<MenuItem[]>([]);
  const [activeMenu, setActiveMenu] = useState<string>('all');
  const [activeIcon, setActiveIcon] = useState<string>();

  useEffect(() => {
    setData(menuIconList);
  }, []);

  useEffect(() => {
    const newData = activeMenu === 'all' ? menuIconList : menuIconList.filter((v) => v.type === activeMenu);
    setData(newData);
  }, [activeMenu]);

  return (
    <div className={styles.menuIconPage} style={style}>
      <div className={styles.nav}>
        <IconLeft className={styles.navIcon} onClick={handleBack} />
        菜单图标选择
      </div>
      <div className={styles.header}>
        <InputSearch className={styles.iconSearch} allowClear placeholder="请输入图标名称" />
        <Button type="default" icon={<IconSync />} />
      </div>

      <div className={styles.menuContainer}>
        <div className={styles.menuType}>
          {menuIconType.map((type) => (
            <div
              className={styles.menuItem}
              key={type.key}
              style={{
                color: activeMenu === type.key ? '#2468F2' : '#111',
                backgroundColor: activeMenu === type.key ? '#E6F0FF' : '#FFF'
              }}
              onClick={() => setActiveMenu(type.key)}
            >
              {type.name}
            </div>
          ))}
        </div>

        <div className={styles.iconContainer}>
          {data.map((item, index) => (
            <div
              className={`${styles.iconItem} ${activeIcon === item.name ? styles.activeIcon : ''}`}
              key={index}
              onClick={() => {
                onSelected(item.icon);
                setActiveIcon(item.name);
              }}
            >
              <i className={`iconfont ${item.icon} ${styles.icon}`} />
              <div className={styles.name}>{item.name}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default MenuIcon;
