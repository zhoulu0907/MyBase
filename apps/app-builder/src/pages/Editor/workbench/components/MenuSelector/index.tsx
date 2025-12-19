import { useState, useMemo, useEffect } from 'react';
import { Input, Tree, TreeSelect } from '@arco-design/web-react';
import { IconDown } from '@arco-design/web-react/icon';
import { listApplicationMenu, type ApplicationMenu } from '@onebase/app';
import { listToTree, treeFilter } from '@onebase/common';
import { useAppStore } from '@/store/store_app';
import styles from './index.module.less';

export interface MenuSelectorProps {
  /** 单选或多选模式，默认多选 */
  mode?: 'single' | 'multiple';
  /** 选中的 keys */
  value?: string | string[];
  /** 选中变化回调 */
  onChange?: (value: string | string[], selectedMenus: ApplicationMenu | ApplicationMenu[]) => void;
  /** 搜索占位符 */
  searchPlaceholder?: string;
  /** 是否可搜索，默认 true */
  searchable?: boolean;
  /** 自定义样式 */
  className?: string;
  /** 树的高度 */
  height?: string | number;
}

const MenuSelector = ({
  mode = 'multiple',
  value,
  onChange,
  searchPlaceholder = '搜索菜单',
  searchable = true,
  className,
  height = '400px'
}: MenuSelectorProps) => {
  const { curAppId } = useAppStore();
  const [searchValue, setSearchValue] = useState('');
  const [menuList, setMenuList] = useState<ApplicationMenu[]>([]);

  // 过滤后的菜单列表
  const filteredMenuList = useMemo(() => {
    if (!searchValue) {
      return menuList;
    }
    return treeFilter(menuList, searchValue, {
      children: 'children',
      label: 'menuName'
    }) as ApplicationMenu[];
  }, [menuList, searchValue]);

  // 将 value 转换为数组格式（方便统一处理）
  const selectedKeys = useMemo(() => {
    if (!value) return [];
    return Array.isArray(value) ? value : [value];
  }, [value]);

  // 递归查找菜单项
  const findMenuById = (menus: ApplicationMenu[], menuUuid: string): ApplicationMenu | null => {
    for (const menu of menus) {
      if (menu.menuUuid === menuUuid) {
        return menu;
      }
      if (menu.children && menu.children.length > 0) {
        const found = findMenuById(menu.children, menuUuid);
        if (found) return found;
      }
    }
    return null;
  };

  // 处理选中变化
  const handleCheck = (checkedKeys: string[]) => {
    if (!onChange) return;

    if (mode === 'single') {
      // 单选
      const newKey = checkedKeys[checkedKeys.length - 1] || '';
      const selectedMenu = newKey ? findMenuById(menuList, newKey) : null;
      onChange(newKey, selectedMenu!);
    } else {
      // 多选
      const selectedMenus = checkedKeys.map((key) => findMenuById(menuList, key)).filter(Boolean) as ApplicationMenu[];
      onChange(checkedKeys, selectedMenus);
    }
  };

  // 处理单选模式的选择
  const handleSelect = (selectedKeys: string[]) => {
    if (mode === 'single' && onChange) {
      const newKey = selectedKeys[0] || '';
      const selectedMenu = newKey ? findMenuById(menuList, newKey) : null;
      onChange(newKey, selectedMenu!);
    }
  };

  // 处理 TreeSelect 的选中变化
  const handleTreeSelectChange = (selectedValue: string) => {
    if (!onChange) return;

    const selectedMenu = selectedValue ? findMenuById(menuList, selectedValue) : null;
    onChange(selectedValue || '', selectedMenu!);
  };

  useEffect(() => {
    listApplicationMenu({ applicationId: curAppId }).then((res) => {
      const treeData = listToTree(res, {
        key: 'menuUuid',
        children: 'children',
        label: 'menuName'
      });
      setMenuList(treeData as ApplicationMenu[]);
    });
  }, [curAppId]);

  return (
    <div className={className}>
      {mode === 'multiple' && (
        <>
          {searchable && (
            <div className={styles.searchWrapper}>
              <Input.Search placeholder={searchPlaceholder} onChange={setSearchValue} />
            </div>
          )}
          <div className={styles.treeWrapper} style={{ height }}>
            <Tree
              treeData={filteredMenuList}
              checkable={mode === 'multiple'}
              checkedKeys={selectedKeys}
              onCheck={handleCheck}
              icons={{
                switcherIcon: <IconDown />
              }}
            />
          </div>
        </>
      )}

      {mode === 'single' && (
        <TreeSelect
          treeData={menuList}
          value={Array.isArray(value) ? value[0] : value}
          onChange={handleTreeSelectChange}
          placeholder={searchPlaceholder}
          allowClear
          showSearch={searchable}
          filterTreeNode={(inputValue, treeNode) => {
            return treeNode.menuName?.toLowerCase().includes(inputValue.toLowerCase()) || false;
          }}
          fieldNames={{
            key: 'menuUuid',
            title: 'menuName',
            children: 'children'
          }}
          className={styles.treeSelect}
        />
      )}
    </div>
  );
};

export default MenuSelector;
