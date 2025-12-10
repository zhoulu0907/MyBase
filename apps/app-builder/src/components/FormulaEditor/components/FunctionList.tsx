import { Input, Spin, Collapse, List, Typography } from '@arco-design/web-react';
import { IconSearch } from '@arco-design/web-react/icon';
import LightText from './LightText';
import { useCallback, useState } from 'react';
import type { FunctionItem, FunctionListProps } from '../utils/types';
import styles from './FunctionList.module.less';
import { functionType, funtionGroupList } from '../utils/formula';
const CollapseItem = Collapse.Item;

export function FunctionList({
  activeKey,
  functions,
  functionLoading,
  searchValue,
  setActiveKey,
  onSearchChange,
  onChooseFunction
}: FunctionListProps) {
  // 记录当前选中的函数ID，用于列表项的选中状态展示
  const [selectedFunctionId, setSelectedFunctionId] = useState<string | null>(null);

  /**
   * 处理搜索框值变化
   * @param value - 搜索框输入的值
   */
  const handleSearchChange = useCallback(
    (value: string) => {
      console.log(value, '>>>>>>>>search');
      onSearchChange(value);
    },
    [onSearchChange]
  );

  /**
   * 处理函数项点击
   * @param func - 点击的函数项
   */
  const handleFunctionClick = useCallback(
    (func: FunctionItem) => {
      console.log(func, 'func');
      setSelectedFunctionId(func.id);
      onChooseFunction(func);
    },
    [onChooseFunction]
  );

  const getFunctionGroupLabel = (type: functionType) => {
    return funtionGroupList?.find((item) => item.type === type)?.label || '';
  };

  const handleChange = (key: string, keys: string[]) => {
    setActiveKey(keys);
  }

  return (
    <div className={styles.functionList}>
      <div className={styles.searchSection}>
        <Input
          prefix={<IconSearch />}
          placeholder="搜索函数"
          value={searchValue}
          onChange={handleSearchChange}
          className={styles.searchInput}
        />
      </div>
      <div className={styles.listSection}>
        {functionLoading ? (
          <div className={styles.loadingFunction}>
            <Spin size={18} tip="加载函数列表..."></Spin>
          </div>
        ) : (
          <Collapse activeKey={activeKey} onChange={handleChange} accordion>
            {functions?.map((group, index) => (
              <CollapseItem
                key={index}
                name={group.type}
                header={<>{getFunctionGroupLabel(group.type)}</>}
                className={styles.collapseItem}
              >
                <List
                  size="small"
                  bordered={false}
                  dataSource={group.functions}
                  virtualListProps={{
                    height: 150
                  }}
                  render={(item, index) => (
                    <List.Item
                      key={index}
                      onClick={() => handleFunctionClick(item)}
                      className={`${styles.functionItem} ${selectedFunctionId === item.id ? styles.selected : ''}`}
                    >
                      <div className={styles.functionInfo}>
                        <div className={styles.functionName}>
                          <LightText text={item.name} searchValue={searchValue} />
                        </div>
                        <Typography.Ellipsis showTooltip className={styles.functionDesc}>
                          {item.summary}
                        </Typography.Ellipsis>
                      </div>
                    </List.Item>
                  )}
                />
              </CollapseItem>
            ))}
          </Collapse>
        )}
      </div>
    </div>
  );
}
