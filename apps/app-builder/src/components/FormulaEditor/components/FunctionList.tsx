import { Input, List, Button } from '@arco-design/web-react';
import { IconSearch, IconCheck, IconDown } from '@arco-design/web-react/icon';
import { useCallback, useState } from 'react';

import type { FunctionItem } from '../utils/types';
import styles from './FunctionList.module.less';

interface FunctionListProps {
  functions: FunctionItem[];
  searchValue: string;
  onSearchChange: (value: string) => void;
  onChooseFunction: (func: FunctionItem) => void;
}

export function FunctionList({ functions, searchValue, onSearchChange, onChooseFunction }: FunctionListProps) {
  const [isExpanded, setIsExpanded] = useState(true);
  const [selectedFunctionId, setSelectedFunctionId] = useState<string | null>(null);

  const handleSearchChange = useCallback(
    (value: string) => {
      onSearchChange(value);
    },
    [onSearchChange]
  );

  const handleFunctionClick = useCallback(
    (func: FunctionItem) => {
      setSelectedFunctionId(func.id);
      onChooseFunction(func);
    },
    [onChooseFunction]
  );

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
                  <div className={styles.functionName}>{func.name}</div>
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
