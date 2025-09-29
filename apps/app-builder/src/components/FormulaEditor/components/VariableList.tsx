import { Input, List, Tag, Button } from '@arco-design/web-react';
import { IconSearch, IconFolder, IconSwap } from '@arco-design/web-react/icon';
import { useCallback } from 'react';
import LightText from './LightText';
import type { Variable } from '../utils/types';
import styles from './VariableList.module.less';

interface VariableListProps {
  variables: Variable[]; //变量数组，包含所有可展示的变量
  searchValue: string;  // 搜索框的值，用于过滤变量列表
  onSearchChange: (value: string) => void; // 搜索框值变化回调，用于更新搜索值
  onInsertVariable: (variable: Variable) => void;  // 插入变量回调，用于将选中的变量插入到公式编辑器中
}

export function VariableList({ variables, searchValue, onSearchChange, onInsertVariable }: VariableListProps) {

  /**
   * 处理搜索框值变化
   * @param value - 搜索框输入的值
   */
  const handleSearchChange = useCallback(
    (value: string) => {
      onSearchChange(value);
    },
    [onSearchChange]
  );

  /**
   * 处理变量项点击
   * @param variable - 点击的变量项
   */
  const handleVariableClick = useCallback(
    (variable: Variable) => {
      onInsertVariable(variable);
    },
    [onInsertVariable]
  );

  /**
   * 根据变量类型返回对应的颜色
   * @param type - 变量的类型
   * @returns 颜色名称
   */
  const getTypeColor = useCallback((type: string) => {
    const colorMap: Record<string, string> = {
      文本: 'blue',
      用户: 'green',
      时间戳: 'orange',
      地址: 'purple'
    };
    return colorMap[type] || 'default';
  }, []);

  return (
    <div className={styles.variableList}>
      <div className={styles.searchSection}>
        <Input
          prefix={<IconSearch />}
          placeholder="搜索变量"
          value={searchValue}
          onChange={handleSearchChange}
          className={styles.searchInput}
        />
      </div>

      <div className={styles.categorySection}>
        <div className={styles.categoryHeader}>
          <div className={styles.categoryTitle}>
            <IconFolder className={styles.categoryIcon} />
            <span>订单管理</span>
          </div>
          <Button type="text" size="small" icon={<IconSwap />} className={styles.switchButton}>
            切换
          </Button>
        </div>
      </div>

      <div className={styles.listSection}>
        <List
          dataSource={variables}
          render={(variable) => (
            <List.Item
              key={variable.value}
              className={styles.variableItem}
              onClick={() => handleVariableClick(variable)}
            >
              <div className={styles.variableInfo}>
                <div className={styles.variableName}>
                  <LightText text={variable.name} searchValue={searchValue} />
                </div>
                <Tag color={getTypeColor(variable.type)} size="small">
                  {variable.type}
                </Tag>
              </div>
            </List.Item>
          )}
        />
      </div>
    </div>
  );
}
