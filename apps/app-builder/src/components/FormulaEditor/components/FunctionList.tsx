import { Input, List, Button } from '@arco-design/web-react';
import { IconSearch, IconCheck, IconDown } from '@arco-design/web-react/icon';
import LightText from './LightText';
import { useCallback, useState } from 'react';
import type { FunctionItem } from '../utils/types';
import styles from './FunctionList.module.less';

interface FunctionListProps {
  functions: FunctionItem[];//函数项数组，包含所有可展示的函数
  searchValue: string; // 搜索框的值，用于过滤函数列表
  onSearchChange: (value: string) => void; // 搜索框值变化回调，用于更新搜索值
  onChooseFunction: (func: FunctionItem) => void; // 选择函数回调，用于将选中的函数传递给父组件
}

export function FunctionList({ functions, searchValue, onSearchChange, onChooseFunction }: FunctionListProps) {

  //控制函数列表是否展开/折叠
  const [isExpanded, setIsExpanded] = useState(true);
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

  /**
     * 切换函数列表的展开/折叠状态
     */
  const toggleExpanded = useCallback(() => {
    setIsExpanded(!isExpanded);
  }, [isExpanded]);

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

      <div className={styles.categorySection}>
        <div className={styles.categoryHeader}>
          <div className={styles.categoryTitle}>
            <IconCheck className={styles.categoryIcon} />
            {/* TODO: 需要根据类型显示 */}
            <span>{functions[0]?.type}</span>
          </div>
          <Button
            type="text"
            size="small"
            icon={isExpanded ? <IconDown /> : <IconDown style={{ transform: 'rotate(-90deg)' }} />}
            onClick={toggleExpanded}
            className={styles.expandButton}
          />
        </div>
      </div>

      {isExpanded && (
        <div className={styles.listSection}>
          <List
            dataSource={functions}
            render={(func) => (
              <List.Item
                key={func.id}
                className={`${styles.functionItem} ${selectedFunctionId === func.id ? styles.selected : ''}`}
                onClick={() => handleFunctionClick(func)}
              >
                <div className={styles.functionInfo}>
                  <div className={styles.functionName}>
                    <LightText text={func.name} searchValue={searchValue} />
                  </div>
                  <div className={styles.functionDesc}>{func.summary}</div>
                </div>
              </List.Item>
            )}
          />
        </div>
      )}
    </div>
  );
}
