import { Form, Input, Tooltip } from '@arco-design/web-react';
import { IconCopy } from '@arco-design/web-react/icon';
import { getComponentSchema, hasComponentSchema, usePageEditorSignal } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useState } from 'react';
import styles from './index.module.less';
import { renderConfigItem } from './registry';
import { resolveEditData } from './mapping';

const FormItem = Form.Item;

/**
 * 属性配置面板组件
 * @param props.cpID 组件唯一ID
 */
interface ConfigsProps {
  cpID: string;
}

const Attributes = ({ cpID }: ConfigsProps) => {
  useSignals();
  const {
    curComponentID,
    curComponentSchema,
    setCurComponentSchema,
    setPageComponentSchemas,
    subTableComponents,
    pageComponentSchemas
  } = usePageEditorSignal();

  const [editData, setEditData] = useState<any>([]);
  const [configs, setConfigs] = useState<any>({});
  const [isInSubTable, setIsInSubTable] = useState<boolean>(false);

  const resolveType = () => {
    const t1 = curComponentSchema?.type;
    if (t1 && hasComponentSchema(t1)) return t1 as any;
    const prefix = (cpID || '').split('-')[0];
    if (prefix && hasComponentSchema(prefix)) return prefix as any;
    return null;
  };

  const updateSchema = (configUpdates?: Record<string, any>, layoutUpdates?: Record<string, any>) => {
    const next = {
      id: cpID,
      type: resolveType(),
      editData: curComponentSchema.editData,
      config: {
        ...curComponentSchema.config,
        ...(configUpdates || {})
      },
      layout: layoutUpdates ? { ...curComponentSchema.layout, ...layoutUpdates } : curComponentSchema.layout
    };
    setCurComponentSchema(next);
    setPageComponentSchemas(cpID, next);
  };

  useEffect(() => {
    if (!cpID) {
      return;
    }
    // 优先从 pageComponentSchemas 中获取组件配置，确保使用正确的组件配置
    // 如果 pageComponentSchemas 中没有，再使用 curComponentSchema（作为回退）
    const componentSchema = pageComponentSchemas[cpID] || curComponentSchema;
    const type = resolveType();
    const defaultSchema = type ? getComponentSchema(type as any) : { editData: componentSchema?.editData || [] };
    const rawEditData = defaultSchema.editData || [];
    setEditData(resolveEditData(rawEditData));
    // 使用当前组件的配置，确保切换组件时显示正确的配置
    // 创建新的对象引用，确保 React 能检测到变化
    const newConfigs = componentSchema?.config || {};
    setConfigs({ ...newConfigs });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [cpID, curComponentID, pageComponentSchemas[cpID]?.config, curComponentSchema?.config]);

  useEffect(() => {
    const keys = Object.keys(subTableComponents) || [];
    let inSubTable = false;
    for (let key of keys) {
      for (let item of subTableComponents[key] || []) {
        if (item.id === curComponentID) {
          inSubTable = true;
        }
      }
    }
    setIsInSubTable(inSubTable);
  }, [curComponentID]);

  const handlePropsChange = (key: string, value: any) => {
    console.log(`更新了属性: ${key} 值为: `, value);
    updateSchema({ [key]: value });
  };

  const handleConfigsChange = (config: any) => {
    console.log(`更新了属性: config值为: `, config);
    updateSchema({ ...config });
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

    updateSchema({ ...updatesObj });
  };

  const handleLayoutChange = (key: string, value: string) => {
    console.log(`更新了布局属性: ${key} 值为: ${value}`);
    const mappedKey = key === 'width' ? 'w' : key === 'height' ? 'h' : key;
    updateSchema({ [key]: value }, { [mappedKey]: value });
  };

  const renderEditItem = (item: any, index: number) => {
    const renderConfig =  renderConfigItem({
      id: cpID,
      item,
      index,
      configs,
      isInSubTable,
      handlePropsChange,
      handleConfigsChange,
      handleMultiPropsChange,
      handleLayoutChange
    });
    return renderConfig;
  };

  // 可根据 id 获取/设置对应组件的属性，这里暂时未实现具体逻辑
  return (
    <div className={styles.attributes}>
      {cpID && (
        <Form autoComplete="off" layout="vertical">
          {editData
            .filter((item: any) => !item.advanced)
            .map((item: any, index: number) => (
              <div key={index}>{renderEditItem(item, index)}</div>
            ))}

          <FormItem label="组件ID" labelCol={{ span: 5 }}>
            <Input
              value={cpID}
              suffix={
                <Tooltip content="复制">
                  <IconCopy style={{ cursor: 'pointer' }} />
                </Tooltip>
              }
            />
          </FormItem>
        </Form>
      )}
    </div>
  );
};

export default Attributes;
