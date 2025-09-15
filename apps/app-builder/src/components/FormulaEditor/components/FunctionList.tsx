import { Input, List, Button } from '@arco-design/web-react';
import { IconSearch, IconCheck, IconDown } from '@arco-design/web-react/icon';
import { useCallback, useState } from 'react';

import type { FunctionItem } from '../index';
import styles from './FunctionList.module.less';

interface FunctionListProps {
  functions: FunctionItem[];
  searchValue: string;
  onSearchChange: (value: string) => void;
  onInsertFunction: (func: FunctionItem) => void;
}

export function FunctionList({ functions, searchValue, onSearchChange, onInsertFunction }: FunctionListProps) {
  const [isExpanded, setIsExpanded] = useState(true);

  const handleSearchChange = useCallback(
    (value: string) => {
      onSearchChange(value);
    },
    [onSearchChange]
  );

  const handleFunctionClick = useCallback(
    (func: FunctionItem) => {
      onInsertFunction(func);
    },
    [onInsertFunction]
  );

  const toggleExpanded = useCallback(() => {
    setIsExpanded(!isExpanded);
  }, [isExpanded]);

  return (
    <div className={styles.functionList}>
      <div className={styles.searchSection}>
        <Input
          prefix={<IconSearch />}
          placeholder="Q 搜索函数"
          value={searchValue}
          onChange={handleSearchChange}
          className={styles.searchInput}
        />
      </div>

      <div className={styles.categorySection}>
        <div className={styles.categoryHeader}>
          <div className={styles.categoryTitle}>
            <IconCheck className={styles.categoryIcon} />
            <span>常用函数</span>
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
              <List.Item key={func.value} className={styles.functionItem} onClick={() => handleFunctionClick(func)}>
                <div className={styles.functionInfo}>
                  <div className={styles.functionName}>{func.name}</div>
                  <div className={styles.functionDesc}>{func.description}</div>
                </div>
              </List.Item>
            )}
          />
        </div>
      )}
    </div>
  );
}
