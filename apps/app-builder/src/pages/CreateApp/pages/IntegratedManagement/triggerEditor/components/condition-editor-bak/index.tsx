import { Button, Divider, Input, Select } from '@arco-design/web-react';
import { IconDelete } from '@arco-design/web-react/icon';
import type { Condition, ConfitionField, EntityFieldValidationTypes, ValidationTypeItem } from '@onebase/app';
import { nanoid } from 'nanoid';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

const Option = Select.Option;

const opCodeOptions = [
  {
    label: '公式',
    value: 'formula'
  },
  {
    label: '静态值',
    value: 'value'
  },
  {
    label: '变量',
    value: 'variable'
  }
];

/**
 * ConditionEditor 组件的 props 类型定义
 */
export interface ConditionEditorProps {
  // 可以下拉选择的字段列表
  fields: ConfitionField[];
  // 具体值
  data: Condition[];
  // 字段变更回调函数
  onConditionChange: (value: Condition[]) => void;
  // 字段验证类型列表
  entityFieldValidationTypes: EntityFieldValidationTypes[];
}

/**
 * 条件编辑器组件初始化
 */
const ConditionEditor: React.FC<ConditionEditorProps> = ({
  data,
  onConditionChange,
  fields,
  entityFieldValidationTypes
}) => {
  const [conditions, setConditions] = useState<Condition[]>(data);

  useEffect(() => {
    console.log('data: ', data);
    if (data) {
      setConditions(data);
    }
  }, [data]);

  useEffect(() => {
    console.log(conditions);
    onConditionChange && onConditionChange(conditions);
  }, [conditions]);

  // 新增条件
  const addCondition = (pid: string) => {
    const newConditions = [...conditions];
    if (pid == '') {
      pid = nanoid();
      newConditions.push({
        id: pid,
        parentId: '',
        rules: [
          {
            id: nanoid(),
            parentId: pid
          }
        ]
      });
    } else {
      const target = newConditions.find((item) => item.id === pid);
      target?.rules?.push({ id: nanoid(), parentId: pid });
    }

    setConditions(newConditions);

    return;
  };

  // 删除条件
  const deleteCondition = (id: string) => {
    const newConditions = [...conditions];
    for (let i = 0; i < newConditions.length; i++) {
      const subItem = newConditions[i];
      if (subItem.rules) {
        const ruleIndex = subItem.rules.findIndex((rule) => rule.id === id);
        if (ruleIndex !== -1) {
          subItem.rules.splice(ruleIndex, 1);
          if (subItem.rules.length === 0) {
            newConditions.splice(i, 1);
          }
          setConditions(newConditions);
          break;
        }
      }
    }
  };

  // 渲染条件组
  const renderItemWrapper = (cond: Condition) => {
    return cond.rules && cond.rules.length > 0 ? (
      <div className={styles.items} key={cond.id}>
        <div className={styles.tag}>且</div>

        {cond.rules?.map((item: Condition) => {
          return renderItem(item);
        })}
        {cond.rules && cond.rules.length > 0 && (
          <Button type="text" onClick={() => addCondition(cond.id)}>
            + 添加且条件
          </Button>
        )}
      </div>
    ) : null;
  };

  const handleFieldIdChange = (value: string, cond: Condition) => {
    const newConditions = [...conditions];
    for (let i = 0; i < newConditions.length; i++) {
      const subItem = newConditions[i];
      if (subItem.rules) {
        for (let j = 0; j < subItem.rules?.length; j++) {
          if (subItem.rules[j].id === cond.id) {
            newConditions[i]!.rules![j].fieldId = value;
            break;
          }
        }
      }
    }
    setConditions(newConditions);
  };

  const handleOpChange = (value: string, cond: Condition) => {
    const newConditions = [...conditions];

    for (let i = 0; i < newConditions.length; i++) {
      const subItem = newConditions[i];
      if (subItem.rules) {
        for (let j = 0; j < subItem.rules?.length; j++) {
          if (subItem.rules[j].id === cond.id) {
            newConditions[i]!.rules![j].op = value;
            break;
          }
        }
      }
    }
    setConditions(newConditions);
  };

  const handleOperatorTypeChange = (value: string, cond: Condition) => {
    const newConditions = [...conditions];

    for (let i = 0; i < newConditions.length; i++) {
      const subItem = newConditions[i];
      if (subItem.rules) {
        for (let j = 0; j < subItem.rules?.length; j++) {
          if (subItem.rules[j].id === cond.id) {
            newConditions[i]!.rules![j].operatorType = value;
            newConditions[i]!.rules![j].value = [];
            break;
          }
        }
      }
    }

    setConditions(newConditions);
  };

  const handleValueChange = (value: string, cond: Condition) => {
    const newConditions = [...conditions];

    for (let i = 0; i < newConditions.length; i++) {
      const subItem = newConditions[i];
      if (subItem.rules) {
        for (let j = 0; j < subItem.rules?.length; j++) {
          if (subItem.rules[j].id === cond.id) {
            newConditions[i]!.rules![j].value = [value];
            break;
          }
        }
      }
    }
    setConditions(newConditions);
  };

  //   渲染条件
  const renderItem = (cond: Condition) => {
    return (
      <div className={styles.item} key={cond.id}>
        <Select
          className={styles.itemSelect}
          style={{ width: '150px' }}
          value={cond.fieldId || ''}
          onChange={(value) => {
            handleFieldIdChange(value, cond);
          }}
        >
          {fields.map((field) => (
            <Option key={field.value} value={field.value}>
              {field.label}
            </Option>
          ))}
        </Select>

        <Select
          className={styles.itemSelect}
          style={{ width: '100px' }}
          value={cond.op || ''}
          onChange={(value) => {
            handleOpChange(value, cond);
          }}
        >
          {(entityFieldValidationTypes.find((item) => item.fieldId === cond.fieldId)?.validationTypes || []).map(
            (operator: ValidationTypeItem) => (
              <Option key={operator.code} value={operator.code}>
                {operator.name}
              </Option>
            )
          )}
        </Select>

        <Select
          className={styles.itemSelect}
          style={{ width: '100px' }}
          value={cond.operatorType || ''}
          onChange={(value) => {
            handleOperatorTypeChange(value, cond);
          }}
          options={opCodeOptions}
        ></Select>

        <Input
          style={{ width: '140px', marginRight: '10px' }}
          value={cond.value?.[0] || ''}
          placeholder="请输入"
          onChange={(value) => {
            handleValueChange(value, cond);
          }}
        />

        <IconDelete
          style={{ fontSize: '13px', color: '#4E5969' }}
          onClick={() => {
            deleteCondition(cond.id);
          }}
        />
      </div>
    );
  };

  return (
    <div>
      <div className={styles.conditionWrapper}>
        {conditions.map((item, index) => {
          return (
            <div key={item.id}>
              {renderItemWrapper(item)}
              {index !== (conditions || [])?.length - 1 && (
                <Divider
                  orientation="center"
                  style={{
                    marginTop: '5px',
                    marginBottom: '0px',
                    marginLeft: '10px',
                    marginRight: '10px'
                  }}
                >
                  <div className={styles.dividerText}>或</div>
                </Divider>
              )}
            </div>
          );
        })}
      </div>
      <Button type="text" size="small" onClick={() => addCondition('')}>
        + 添加或条件
      </Button>
    </div>
  );
};

export default ConditionEditor;
