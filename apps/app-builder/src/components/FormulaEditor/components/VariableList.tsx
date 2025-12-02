import { Input, List, Tag, Button, Dropdown, Menu, Spin } from '@arco-design/web-react';
import { IconFolder, IconSearch } from '@arco-design/web-react/icon';
import { useCallback, useState, type ReactNode } from 'react';
import LightText from './LightText';
import styles from './VariableList.module.less';
import type { VariablesList, ChildVariablesField } from '@onebase/app';
import { cloneDeep } from 'lodash-es';
import { FIELD_TAG_TYPE, FIELD_TYPE } from '@onebase/ui-kit';

interface VariableListProps {
  variables: VariablesList[]; //变量数组，包含所有可展示的变量
  searchValue: string; // 搜索框的值，用于过滤变量列表
  onSearchChange: (value: string) => void; // 搜索框值变化回调，用于更新搜索值
  onInsertVariable: (variable: ChildVariablesField) => void; // 插入变量回调，用于将选中的变量插入到公式编辑器中
}

export function VariableList({ variables, searchValue, onSearchChange, onInsertVariable }: VariableListProps) {
  const [filteredVariables, setFilteredVariables] = useState<VariablesList[]>([]);

  // 文件、图片、位置、关联关系、密码、加密字段类型的不适合在函数公式中进行计算
  const disableTagTypes = [
    FIELD_TAG_TYPE.FILE.VALUE,
    FIELD_TAG_TYPE.IMAGE.VALUE,
    FIELD_TAG_TYPE.GEOGRAPHY.VALUE,
    FIELD_TAG_TYPE.RELATION.VALUE,
    FIELD_TAG_TYPE.PASSWORD.VALUE,
    FIELD_TAG_TYPE.ENCRYPTED.VALUE
  ];
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
    (variable: ChildVariablesField) => {
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
    for (let ele in FIELD_TAG_TYPE) {
      const item = FIELD_TAG_TYPE[ele as keyof typeof FIELD_TAG_TYPE];
      if (item.VALUE === type) {
        return item.COLOR;
      }
    }
    return '#1979FF';
  }, []);

  // 字段列表中标签类型名称展示中文
  const getTypeName = useCallback((variable: ChildVariablesField) => {
    for (let ele in FIELD_TAG_TYPE) {
      const item = FIELD_TAG_TYPE[ele as keyof typeof FIELD_TAG_TYPE];
      if (item.VALUE === variable.fieldType) {
        return item.LABEL;
      }
    }
    return variable.fieldType;
  }, []);

  //点击字段列表中的切换按钮调用该函数
  const handleChangeVariables = (variableId: string) => {
    const copyVariables = cloneDeep(variables);
    const selectedVariable = copyVariables?.filter((item) => item?.variableId === variableId) || [];
    setFilteredVariables(selectedVariable);
  };

  //渲染当前的变量列表
  const variablesList = (): ReactNode => {
    if (!variables.length) return null;
    return (
      <div style={{ minWidth: '200px', maxHeight: '215px', overflowY: 'auto' }}>
        <Menu
          onClickMenuItem={(key: string) => {
            handleChangeVariables(key);
          }}
        >
          {variables.map((item) => {
            return <Menu.Item key={item?.variableId}>{item?.variableName}</Menu.Item>;
          })}
        </Menu>
      </div>
    );
  };

  return (
    <>
      <Input
        prefix={<IconSearch />}
        placeholder="搜索变量"
        value={searchValue}
        onChange={handleSearchChange}
        className={styles.searchInput}
      />
      {!variables.length ? (
        <div className={styles.loadingVariables}>
          <Spin size={18} tip="加载变量列表..."></Spin>
        </div>
      ) : (
        <div className={styles.categorySection}>
          <div className={styles.categoryHeader}>
            <Dropdown.Button
              trigger={'hover'}
              type="text"
              position="bl"
              triggerProps={{ autoAlignPopupWidth: true }}
              droplist={variablesList()}
              icon={
                <Button type="text" color="success">
                  切换
                </Button>
              }
            >
              <IconFolder className={styles.categoryIcon} />
              <span className={styles.categoryEntityName}>
                {filteredVariables?.[0]?.variableName || variables?.[0]?.variableName || ''}
              </span>
            </Dropdown.Button>
          </div>
          <List
            size="small"
            className={styles.listSection}
            dataSource={(filteredVariables?.[0]?.fields || variables?.[0]?.fields || []).filter(
              (ele) => !disableTagTypes.includes(ele.fieldType) && ele.isSystemField !== FIELD_TYPE.SYSTEM
            )}
            render={(variable, index) => (
              <List.Item key={index} className={styles.variableItem} onClick={() => handleVariableClick(variable)}>
                <div className={styles.variableInfo}>
                  <div className={styles.variableName}>
                    <LightText text={variable?.displayName} searchValue={searchValue} />
                  </div>
                  <Tag
                    style={{
                      color: getTypeColor(variable?.fieldType),
                      backgroundColor: `${getTypeColor(variable?.fieldType)}22`
                    }}
                    size="small"
                  >
                    {getTypeName(variable)}
                  </Tag>
                </div>
              </List.Item>
            )}
          />
        </div>
      )}
    </>
  );
}
