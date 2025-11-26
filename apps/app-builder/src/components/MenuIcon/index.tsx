import { Button, Input, Empty } from '@arco-design/web-react';
import { IconSync, IconLeft } from '@arco-design/web-react/icon';
import { useEffect, useState } from 'react';
import { webMenuIcons } from '@onebase/ui-kit';
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
  const allWebMenuIcons = webMenuIcons.map((ele) => ele.children).reduce((acc, current) => acc.concat(current), []);
  const [data, setData] = useState<any[]>(allWebMenuIcons);
  const [activeMenu, setActiveMenu] = useState<string>('all');
  const [activeIcon, setActiveIcon] = useState<string>();
  const [inputValue, setInputValue] = useState<string>('');

  const menuIconType = [{ name: '全部', type: 'all' }, ...webMenuIcons];

  useEffect(() => {
    if (inputValue) {
      const result = webMenuIcons
        .filter((item) => item.name.includes(inputValue))
        .filter((item) => item.type === activeMenu || activeMenu === 'all');

      setData(result);
    } else {
      const result = activeMenu === 'all' ? allWebMenuIcons : webMenuIcons.find((v) => v.type === activeMenu)?.children;
      setData(result || []);
    }
  }, [inputValue, activeMenu]);

  const handleReset = () => {
    setData(allWebMenuIcons);
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
          {menuIconType.map((item) => (
            <div
              className={styles.menuItem}
              key={item.type}
              style={{
                color: activeMenu === item.type ? '#2468F2' : '#111',
                backgroundColor: activeMenu === item.type ? '#E6F0FF' : '#FFF'
              }}
              onClick={() => setActiveMenu(item.type)}
            >
              {item.name}
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
                    handleBack();
                  }}
                >
                  <img src={item.icon} style={{ width: '32px', height: '32px', color: '#333' }} alt="" />
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
