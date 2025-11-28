import { Form, Switch } from '@arco-design/web-react';
import { CONFIG_TYPES, usePageEditorSignal, getComponentSchema, hasComponentSchema } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useState } from 'react';
import AdvancedTableConfig from './components/AdvancedTableConfig';
import styles from './index.module.less';

const FormItem = Form.Item;

interface AdvancedProps {
  cpID: string;
}

const Advanced = ({ cpID }: AdvancedProps) => {
  useSignals();
  const { curComponentSchema, setCurComponentSchema, setPageComponentSchemas, components, pageComponentSchemas } = usePageEditorSignal();

  const [editData, setEditData] = useState<any>([]);
  const [configs, setConfigs] = useState<any>({});

  const handlePropsChange = (key: string, value: any) => {
    console.log(`更新了属性: ${key} 值为: `, value);

    const newCurComponentSchema = {
      id: cpID,
      type: curComponentSchema.type || resolveType(),
      editData: curComponentSchema.editData,
      config: {
        ...curComponentSchema.config,
        [key]: value
      },
      layout: curComponentSchema.layout
    };

    setCurComponentSchema(newCurComponentSchema);
    setPageComponentSchemas(cpID, newCurComponentSchema);
  };

  const handleMultiPropsChange = (updates: { key: string; value: string | number | boolean | any[] }[]) => {
    console.log(`更新了属性: ${updates}`);

    // 将 updates 数组中的每个 key-value 展开到 config 中
    const updatesObj = updates.reduce(
      (acc, cur) => {
        acc[cur.key] = cur.value;
        return acc;
      },
      {} as Record<string, any>
    );

    const newCurComponentSchema = {
      id: cpID,
      type: curComponentSchema.type || resolveType(),
      editData: curComponentSchema.editData,
      config: {
        ...curComponentSchema.config,
        ...updatesObj
      },
      layout: curComponentSchema.layout
    };

    setCurComponentSchema(newCurComponentSchema);
    setPageComponentSchemas(cpID, newCurComponentSchema);
  };

  const resolveType = () => {
    const t1 = curComponentSchema?.type;
    if (t1 && hasComponentSchema(t1)) return t1 as any;
    const t2 = components?.find((c: any) => c.id === cpID)?.type;
    if (t2 && hasComponentSchema(t2)) return t2 as any;
    const t3 = pageComponentSchemas?.[cpID]?.type;
    if (t3 && hasComponentSchema(t3)) return t3 as any;
    const prefix = (cpID || '').split('-')[0];
    if (prefix && hasComponentSchema(prefix)) return prefix as any;
    return null;
  };

  useEffect(() => {
    if (!cpID) {
      return;
    }
    const type = resolveType();
    const defaultSchema = type ? getComponentSchema(type as any) : { editData: curComponentSchema.editData || [] };
    setEditData(defaultSchema.editData);
    setConfigs(curComponentSchema.config);
  }, [cpID, curComponentSchema]);

  return (
    <div className={styles.configAdvanced}>
      {cpID && (
        <Form autoComplete="off" layout="vertical">
          {editData
            .filter((item: any) => item.advanced)
            .map((item: any, index: number) => {
              if (item.type === CONFIG_TYPES.SWITCH_INPUT) {
                return (
                  <FormItem
                    key={index}
                    label={
                      <div
                        style={{
                          textAlign: 'left'
                        }}
                      >
                        <span>{item.name}</span>
                      </div>
                    }
                    labelCol={{
                      span: 21
                    }}
                    wrapperCol={{
                      span: 1
                    }}
                    className={styles.formItem}
                  >
                    {item.type === CONFIG_TYPES.SWITCH_INPUT && (
                      <Switch
                        size="small"
                        checked={configs[item.key]}
                        onChange={(value) => {
                          handlePropsChange(item.key, value);
                        }}
                      />
                    )}
                  </FormItem>
                );
              }
              if (item.type === CONFIG_TYPES.TABLE_DATA) {
                return (
                  <AdvancedTableConfig
                    key={index}
                    id={cpID}
                    handleMultiPropsChange={handleMultiPropsChange}
                    handlePropsChange={handlePropsChange}
                    item={item}
                    configs={configs}
                  />
                );
              }
            })}
        </Form>
      )}
    </div>
  );
};

export default Advanced;
