import { Button, Input, Empty } from '@arco-design/web-react';
import { IconSync, IconLeft } from '@arco-design/web-react/icon';
import { useEffect, useState } from 'react';
import DynamicIcon from '../DynamicIcon';
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
  const [data, setData] = useState<MenuItem[]>(menuIconList);
  const [activeMenu, setActiveMenu] = useState<string>('all');
  const [activeIcon, setActiveIcon] = useState<string>();
  const [inputValue, setInputValue] = useState<string>('');

  useEffect(() => {
    if (inputValue) {
      const result = menuIconList
        .filter((item) => item.name.includes(inputValue))
        .filter((item) => item.type === activeMenu || activeMenu === 'all');

      setData(result);
    } else {
      const result = activeMenu === 'all' ? menuIconList : menuIconList.filter((v) => v.type === activeMenu);
      setData(result);
    }
  }, [inputValue, activeMenu]);

  const handleReset = () => {
    setData(menuIconList);
    setInputValue('');
  };

  return (
    <div className={styles.menuIconPage} style={style}>
      <div className={styles.nav}>
        <IconLeft className={styles.navIcon} onClick={handleBack} />
        菜单图标选择
      </div>
      <div className={styles.header}>
        <InputSearch
          className={styles.iconSearch}
          allowClear
          value={inputValue}
          placeholder="请输入图标名称"
          onChange={setInputValue}
        />
        <Button type="default" icon={<IconSync />} onClick={handleReset} />
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
          {data.length === 0 ? (
            <div className={styles.empty}>
              <Empty />
            </div>
          ) : (
            <>
              {data.map((item) => (
                <div
                  className={`${styles.iconItem} ${activeIcon === item.name ? styles.activeIcon : ''}`}
                  key={item.code}
                  onClick={() => {
                    onSelected(item.code);
                    setActiveIcon(item.name);
                  }}
                >
                  <DynamicIcon
                    IconComponent={item.icon}
                    theme="outline"
                    size="32"
                    fill="#333"
                  />
                  <div className={styles.name}>{item.name}</div>
                </div>
              ))}
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default MenuIcon;
