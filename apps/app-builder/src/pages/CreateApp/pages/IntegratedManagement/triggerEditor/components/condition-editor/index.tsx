import { Button, Input, Select } from '@arco-design/web-react';
import { IconClose } from '@douyinfe/semi-icons';
import type { Condition, ConfitionField } from '@onebase/app';
import { nanoid } from 'nanoid';
import React, { useState } from 'react';
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

  data?: Condition;
  onChange: (value: Condition) => void;
  fieldOperatorMapping: { [key: string]: string[] };
}

/**
 * 条件编辑器组件初始化
 */
const ConditionEditor: React.FC<ConditionEditorProps> = ({ data, onChange, fields, fieldOperatorMapping }) => {
  const [condition, setCondition] = useState<Condition>({
    id: '',
    condition: 'and',
    parentId: '',
    fieldId: '',
    op: '',
    opCode: '',
    operators: [],
    rules: []
  });

  const [fieldOperator, setFieldOperator] = useState<{ [key: string]: string[] }>({});

  //   useEffect(() => {
  //     if (data) {
  //       setCondition(data);
  //     }
  //   }, []);

  //   useEffect(() => {
  //     onChange(condition);
  //   }, [condition]);

  const addCondition = (pid: string) => {
    const newCondition = { ...condition };
    if (pid == '') {
      newCondition.rules?.push({
        id: nanoid(),
        parentId: newCondition.id,
        rules: [
          {
            id: nanoid(),
            parentId: pid
          }
        ]
      });
      setCondition(newCondition);
      return;
    }

    if (newCondition.rules) {
      for (let i = 0; i < newCondition.rules?.length; i++) {
        const subItem = newCondition.rules[i];
        if (subItem.id === pid) {
          subItem.rules?.push({ id: nanoid(), parentId: pid });
          break;
        }
      }
    }
    setCondition(newCondition);
  };

  const deleteCondition = (id: string) => {
    const newCondition = { ...condition };
    if (newCondition.rules) {
      for (let i = 0; i < newCondition.rules?.length; i++) {
        const subItem = newCondition.rules[i];
        if (subItem.rules) {
          for (let j = 0; j < subItem.rules?.length; j++) {
            if (subItem.rules[j].id === id) {
              subItem.rules.splice(j, 1);
              if (subItem.rules.length === 0) {
                newCondition.rules.splice(i, 1);
              }
              setCondition(newCondition);
              break;
            }
          }
        }
      }
    }
  };

  const renderItemWrapper = (cond: Condition) => {
    return cond.rules && cond.rules.length > 0 ? (
      <div className={styles.items} key={cond.id}>
        {cond.rules?.map((item) => {
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
          style={{ width: '80px' }}
          onChange={() => {
            const newFieldOperator = fieldOperator;
            const fieldType = fields.find((field) => field.value === cond.fieldId)?.fieldType;
            newFieldOperator[cond.fieldId!] = fieldOperatorMapping[fieldType!];

            setFieldOperator(newFieldOperator);
          }}
        >
          {fields.map((field) => (
            <Option key={field.value} value={field.value}>
              {field.label}
            </Option>
          ))}
        </Select>

        <Select className={styles.itemSelect} style={{ width: '80px' }}>
          {(fieldOperator[cond.fieldId!] || []).map((operator) => (
            <Option key={operator} value={operator}>
              {operator}
            </Option>
          ))}
        </Select>

        <Select className={styles.itemSelect} style={{ width: '90px' }}>
          {opCodeOptions}
        </Select>

        <Input style={{ width: '100px', marginRight: '10px', backgroundColor: 'white' }} />

        <IconClose
          style={{ fontSize: '13px', color: '#4E5969' }}
          onClick={() => {
            console.log('close', cond.id);
            deleteCondition(cond.id);
          }}
        />
      </div>
    );
  };

  return (
    <div>
      <div className={styles.conditionWrapper}>
        {condition.rules?.map((item, index) => {
          return (
            <div key={item.id}>
              {renderItemWrapper(item)}
              {index !== (condition.rules || [])?.length - 1 && <div>或者</div>}
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
