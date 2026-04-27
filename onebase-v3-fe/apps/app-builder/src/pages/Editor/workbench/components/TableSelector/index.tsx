import { useState, useMemo, useEffect } from 'react';
import { Input, Tree, Spin } from '@arco-design/web-react';
import { IconDown } from '@arco-design/web-react/icon';
import { getTablesByAppId, type ApplicationMenu } from '@onebase/app';
import { treeFilter } from '@onebase/common';
import { useAppStore } from '@/store/store_app';
import styles from './index.module.less';

export interface TableSelectorProps {
  /** 选中的 keys */
  value?: string | string[];
  /** 选中变化回调 */
  onChange?: (value: string, selectedMenus: object | undefined) => void;
  /** 搜索占位符 */
  searchPlaceholder?: string;
  /** 是否可搜索，默认 true */
  searchable?: boolean;
  /** 自定义样式 */
  className?: string;
  /** 树的高度 */
  height?: string | number;
}

const TableSelector = ({
  value,
  onChange,
  searchPlaceholder = '搜索菜单',
  searchable = true,
  className,
  height = '400px'
}: TableSelectorProps) => {
  const { curAppId } = useAppStore();
  const [searchValue, setSearchValue] = useState('');
  const [tableList, setTableList] = useState([]);
  const [checkedKeys, setCheckedKeys] = useState<string[]>([]);
  const [loading, setLoading] = useState<boolean>(false);

  // 过滤后的菜单列表
  const filteredMenuList = useMemo(() => {
    if (!searchValue) {
      return tableList;
    }
    return treeFilter(tableList, searchValue) as ApplicationMenu[];
  }, [tableList, searchValue]);

  useEffect(() => {
    if (value) {
      setCheckedKeys(Array.isArray(value) ? value : [value]);
    }
  }, [value]);

  // 处理选中变化(保持单选)
  const handleCheck = (checkedKeys: string[]) => {
    if (!onChange) return;

    const checkedOne = checkedKeys.length > 1 ? checkedKeys.slice(-1) : checkedKeys;
    setCheckedKeys(checkedOne);
    const data = tableList.find((item: { key: string }) => item.key === checkedOne[0]);
    onChange(checkedKeys[0], data);
  };

  useEffect(() => {
    setLoading(true);
    getTablesByAppId({ applicationId: curAppId })
      .then((res) => {
        if (res?.list?.length > 0) {
          const list = res.list.map((item) => {
            const config = JSON.parse(item?.config);
            return {
              key: config.id,
              title: config?.label?.text || config?.cpName,
              componentId: config.id,
              tableName: config?.tableName,
              metaData: config?.metaData,
              columns: config?.columns
            };
          });
          setTableList(list);
        }
      })
      .catch((err) => console.log(err))
      .finally(() => {
        setLoading(false);
      });
  }, [curAppId]);

  return (
    <div className={className}>
      {searchable && (
        <div className={styles.searchWrapper}>
          <Input.Search placeholder={searchPlaceholder} onChange={setSearchValue} />
        </div>
      )}
      <Spin loading={loading} style={{ width: '100%' }}>
        <div className={styles.treeWrapper} style={{ height }}>
          <Tree
            treeData={filteredMenuList}
            checkable={true}
            selectable={true}
            multiple={false}
            checkedKeys={checkedKeys}
            onCheck={handleCheck}
            icons={{
              switcherIcon: <IconDown />
            }}
          />
        </div>
      </Spin>
    </div>
  );
};

export default TableSelector;
