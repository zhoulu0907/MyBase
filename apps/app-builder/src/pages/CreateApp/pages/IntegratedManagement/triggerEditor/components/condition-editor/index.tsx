import { Button, Input, Select } from '@arco-design/web-react';
import { IconClose } from '@douyinfe/semi-icons';
import type { Condition, ConfitionField, EntityFieldValidationTypes, ValidationTypeItem } from '@onebase/app';
import { nanoid } from 'nanoid';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

const Option = Select.Option;

const opCodeOptions = [
  <Option key="formula" value="formula">
    公式
  </Option>,
  <Option key="value" value="value">
    静态值
  </Option>,
  <Option key="variable" value="variable">
    变量
  </Option>
];

/**
 * ConditionEditor 组件的 props 类型定义
 */
export interface ConditionEditorProps {
  fields: ConfitionField[];
  data?: Condition[];
  onChange: (value: Condition[]) => void;
  entityFieldValidationTypes: EntityFieldValidationTypes[];
}

/**
 * 条件编辑器组件初始化
 */
const ConditionEditor: React.FC<ConditionEditorProps> = ({ data, onChange, fields, entityFieldValidationTypes }) => {
  const [conditions, setConditions] = useState<Condition[]>([]);

  useEffect(() => {
    if (data) {
      setConditions(data);
    }
  }, []);

  useEffect(() => {
    onChange(conditions);
  }, [conditions]);

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
      for (let i = 0; i < newConditions.length; i++) {
        const subItem = newConditions[i];
        if (subItem.id === pid) {
          subItem.rules?.push({ id: nanoid(), parentId: pid });
          break;
        }
      }
    }

    setConditions(newConditions);
    return;
  };

  const deleteCondition = (id: string) => {
    const newConditions = [...conditions];
    for (let i = 0; i < newConditions.length; i++) {
      const subItem = newConditions[i];
      if (subItem.rules) {
        for (let j = 0; j < subItem.rules?.length; j++) {
          if (subItem.rules[j].id === id) {
            subItem.rules.splice(j, 1);
            if (subItem.rules.length === 0) {
              newConditions.splice(i, 1);
            }
            setConditions(newConditions);
            break;
          }
        }
      }
    }
  };

  const renderItemWrapper = (cond: Condition) => {
    return cond.rules && cond.rules.length > 0 ? (
      <div className={styles.items} key={cond.id}>
        {cond.rules?.map((item: Condition) => {
          return renderItem(item);
        })}
        {cond.rules && cond.rules.length > 0 && (
          <Button type="default" onClick={() => addCondition(cond.id)}>
            + 并且
          </Button>
        )}
      </div>
    ) : null;
  };

  const renderItem = (cond: Condition) => {
    return (
      <div className={styles.item} key={cond.id}>
        <Select
          className={styles.itemSelect}
          style={{ width: '150px' }}
          value={cond.fieldId || ''}
          onChange={(value) => {
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
            const newConditions = [...conditions];

            for (let i = 0; i < newConditions.length; i++) {
              const subItem = newConditions[i];
              if (subItem.rules) {
                for (let j = 0; j < subItem.rules?.length; j++) {
                  if (subItem.rules[j].id === cond.id) {
                    newConditions[i]!.rules![j].operatorType = value;
                    break;
                  }
                }
              }
            }
            setConditions(newConditions);
          }}
        >
          {opCodeOptions}
        </Select>

        <Input
          style={{ width: '170px', marginRight: '10px', backgroundColor: 'white' }}
          value={cond.value?.[0] || ''}
          onChange={(value) => {
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
          }}
        />

        <IconClose
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
              {index !== (conditions || [])?.length - 1 && <div>或者</div>}
            </div>
          );
        })}
      </div>
      <Button type="outline" size="small" onClick={() => addCondition('')}>
        + 或者
      </Button>
    </div>
  );
};

export default ConditionEditor;
