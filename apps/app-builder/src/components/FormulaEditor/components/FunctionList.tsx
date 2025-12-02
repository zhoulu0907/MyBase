import { Input, Menu, Spin, Typography } from '@arco-design/web-react';
import { IconSearch } from '@arco-design/web-react/icon';
import LightText from './LightText';
import { useCallback, useState } from 'react';
import type { FunctionItem } from '../utils/types';
import styles from './FunctionList.module.less';

const MenuItem = Menu.Item;
const SubMenu = Menu.SubMenu;

interface FunctionListProps {
  functionLoading: boolean;
  functions: FunctionItem[]; //函数项数组，包含所有可展示的函数
  searchValue: string; // 搜索框的值，用于过滤函数列表
  onSearchChange: (value: string) => void; // 搜索框值变化回调，用于更新搜索值
  onChooseFunction: (func: FunctionItem) => void; // 选择函数回调，用于将选中的函数传递给父组件
}

export function FunctionList({ functions, functionLoading, searchValue, onSearchChange, onChooseFunction }: FunctionListProps) {
  const functionCategoryList = ['常用函数'];
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
  // const toggleExpanded = useCallback(() => {
  //   setIsExpanded(!isExpanded);
  // }, [isExpanded]);

  const getSubMenu = () => {
    if (!functionCategoryList.length) return null;
    return functionCategoryList.map((category, index) => {
      return (
        <SubMenu
          key={index.toString()}
          className={styles.categoryContent}
          title={<span className={styles.categoryTitle}>{category}</span>}
        >
          {getMenuItem(index)}
        </SubMenu>
      );
    });
  };

  const getMenuItem = (index: number) => {
    if (!functions.length) return null;
    return functions.map((func) => {
      return (
        <MenuItem
          key={`${index}_${func.id}`}
          onClick={() => handleFunctionClick(func)}
          className={`${styles.functionItem} ${selectedFunctionId === func.id ? styles.selected : ''}`}
        >
          <div className={styles.functionInfo}>
            <div className={styles.functionName}>
              <LightText text={func.name} searchValue={searchValue} />
            </div>
            <Typography.Ellipsis showTooltip className={styles.functionDesc}>
              {func.summary}
            </Typography.Ellipsis>
          </div>
        </MenuItem>
      );
    });
  };

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
      {isExpanded && (
        <div className={styles.listSection}>
          {functionLoading ? (
            <div className={styles.loadingFunction}>
              <Spin size={18} tip="加载函数列表..."></Spin>
            </div>
          ) : (
            <Menu defaultOpenKeys={['0']} defaultSelectedKeys={[`0_${functions?.[0]?.id}`]}>
              {getSubMenu()}
            </Menu>
          )}
        </div>
      )}
    </div>
  );
}
