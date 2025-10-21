import { Input, List, Tag, Button, Dropdown, Menu } from '@arco-design/web-react';
import { IconFolder, IconSearch } from '@arco-design/web-react/icon';
import { useCallback, useState, type ReactNode } from 'react';
import LightText from './LightText';
import styles from './VariableList.module.less';
import type {  ChildEntityField, VariablesEntity} from '@onebase/app';
import { cloneDeep } from 'lodash-es';

interface VariableListProps {
  variables: VariablesEntity[]; //变量数组，包含所有可展示的变量
  searchValue: string;  // 搜索框的值，用于过滤变量列表
  onSearchChange: (value: string) => void; // 搜索框值变化回调，用于更新搜索值
  onInsertVariable: (variable: ChildEntityField) => void;  // 插入变量回调，用于将选中的变量插入到公式编辑器中
}

export function VariableList({ variables, searchValue, onSearchChange, onInsertVariable }: VariableListProps) {
  const [filteredVariables, setFilteredVariables] = useState<VariablesEntity[]>(variables);
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
    (variable: ChildEntityField) => {
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
      TEXT: 'blue',
      NUMBER: 'green',
      VARCHAR: 'orange',
      INT: 'purple',
      TIMESTAMP: 'pink'
    };
    return colorMap[type] || 'default';
  }, []);

  //点击字段列表中的切换按钮调用该函数
  const handleChangeVariables = (entityId: string) => {
    const copyVariables = cloneDeep(variables);
    const selectedVariable = copyVariables?.filter(item => item?.entityId === entityId) || [];
    setFilteredVariables(selectedVariable);
  }

  //渲染当前的变量列表
  const variablesList = (): ReactNode => {
    if (!variables.length) return null;
    return (
      <div style={{minWidth: '200px', maxHeight:"215px", overflowY:"auto"}}>
        <Menu onClickMenuItem={(key: string)=>{
          handleChangeVariables(key)
        }}>
          {variables.map((item) => {
            return <Menu.Item key={item?.entityId}>{item?.entityName}</Menu.Item>
          })}
        </Menu>
      </div>
    )
  }

  return (
    <>
      <Input
        prefix={<IconSearch />}
        placeholder="搜索变量"
        value={searchValue}
        onChange={handleSearchChange}
        className={styles.searchInput}
      />
      <div className={styles.categorySection}>
        <div className={styles.categoryHeader}>
          <Dropdown.Button trigger={"hover"} type='text' position='bl' 
            triggerProps={{autoAlignPopupWidth: true}}
            droplist={variablesList()} 
            icon={<Button type='text' color='success'>切换</Button>}>
            <IconFolder className={styles.categoryIcon} />
            <span className={styles.categoryEntityName}>{filteredVariables?.[0]?.entityName || ""}</span>
          </Dropdown.Button>
        </div>
        <List
          size='small'
          className={styles.listSection}
          dataSource={filteredVariables?.[0]?.fields || []}
          render={(variable, index) => (
            <List.Item
              key={index}
              className={styles.variableItem}
              onClick={() => handleVariableClick(variable)}
            >
              <div className={styles.variableInfo}>
                <div className={styles.variableName}>
                  <LightText text={variable?.displayName} searchValue={searchValue} />
                </div>
                <Tag color={getTypeColor(variable?.fieldType)} size="small">
                  {variable?.fieldType}
                </Tag>
              </div>
            </List.Item>
          )}
        />
      </div>
    </>
  );
}
