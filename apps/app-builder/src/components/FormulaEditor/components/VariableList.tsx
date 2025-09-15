import { Input, List, Tag, Button } from '@arco-design/web-react';
import { IconSearch, IconFolder, IconSwap } from '@arco-design/web-react/icon';
import { useCallback } from 'react';

import type { Variable } from '../index';
import styles from './VariableList.module.less';

interface VariableListProps {
  variables: Variable[];
  searchValue: string;
  onSearchChange: (value: string) => void;
  onInsertVariable: (variable: Variable) => void;
}

export function VariableList({ variables, searchValue, onSearchChange, onInsertVariable }: VariableListProps) {
  const handleSearchChange = useCallback(
    (value: string) => {
      onSearchChange(value);
    },
    [onSearchChange]
  );

  const handleVariableClick = useCallback(
    (variable: Variable) => {
      onInsertVariable(variable);
    },
    [onInsertVariable]
  );

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
          placeholder="Q 搜索变量"
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
                <div className={styles.variableName}>{variable.name}</div>
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
