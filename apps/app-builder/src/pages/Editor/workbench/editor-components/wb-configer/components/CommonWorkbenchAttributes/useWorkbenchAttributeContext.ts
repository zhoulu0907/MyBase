import { getWorkbenchComponentSchema, hasWorkbenchComponentSchema, useWorkbenchSignal } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useMemo, useState } from 'react';
import { renderConfigItem } from '../../registry';

type WorkbenchConfig = Record<string, unknown>;
type WorkbenchEditItem = Record<string, unknown>;

interface WorkbenchComponentSchema {
  id?: string;
  type?: string;
  config?: WorkbenchConfig;
  editData?: WorkbenchEditItem[];
}

export interface WorkbenchAttributeContext {
  cpID?: string;
  componentType?: string;
  editData: WorkbenchEditItem[];
  configs: WorkbenchConfig;
  isSchemaReady: boolean;
  handlePropsChange: (key: string, value: unknown) => void;
  handleConfigsChange: (config: WorkbenchConfig) => void;
  handleMultiPropsChange: (updates: { key: string; value: unknown }[]) => void;
  handleLayoutChange: (key: string, value: unknown) => void;
  renderEditItem: (item: WorkbenchEditItem, index: number) => JSX.Element | null;
}

export function useWorkbenchAttributeContext(): WorkbenchAttributeContext {
  useSignals();

  const { curComponentID, curComponentSchema, setCurComponentSchema, setWbComponentSchemas, wbComponentSchemas } =
    useWorkbenchSignal();

  const schema = (curComponentSchema || {}) as WorkbenchComponentSchema;
  const cpID = schema.id || curComponentID;
  const componentType = schema.type;

  const [editData, setEditData] = useState<WorkbenchEditItem[]>([]);
  const [configs, setConfigs] = useState<WorkbenchConfig>(schema.config || {});

  const isSchemaReady = useMemo(
    () => Boolean(componentType && hasWorkbenchComponentSchema(componentType)),
    [componentType]
  );

  useEffect(() => {
    if (!cpID) {
      return;
    }

    // 优先从 wbComponentSchemas 中获取组件配置，确保使用正确的组件配置
    // 如果 wbComponentSchemas 中没有，再使用 curComponentSchema（作为回退）
    const componentSchema = wbComponentSchemas[cpID] || curComponentSchema;
    const type = componentSchema?.type || componentType;

    if (!type || !hasWorkbenchComponentSchema(type)) {
      setEditData([]);
      return;
    }

    const defaultSchema = getWorkbenchComponentSchema(type) as unknown as WorkbenchComponentSchema;
    setEditData(defaultSchema.editData || []);

    // 使用当前组件的配置，确保切换组件时显示正确的配置
    const newConfigs = componentSchema?.config || {};
    setConfigs({ ...newConfigs });
  }, [cpID, curComponentID, wbComponentSchemas[cpID]?.config, curComponentSchema?.config]);

  const persistConfig = (nextConfig: WorkbenchConfig) => {
    if (!cpID) {
      return;
    }
    const nextSchema = {
      ...schema,
      config: nextConfig
    };
    setCurComponentSchema(nextSchema);
    setWbComponentSchemas(cpID, nextSchema);
    setConfigs(nextConfig);
  };

  const handlePropsChange = (key: string, value: unknown) => {
    persistConfig({
      ...configs,
      [key]: value
    });
  };

  const handleConfigsChange = (config: WorkbenchConfig) => {
    persistConfig({
      ...configs,
      ...config
    });
  };

  const handleMultiPropsChange = (updates: { key: string; value: unknown }[]) => {
    const updatesObj: WorkbenchConfig = {};
    updates.forEach((update) => {
      updatesObj[update.key] = update.value;
    });
    persistConfig({
      ...configs,
      ...updatesObj
    });
  };

  const handleLayoutChange = (key: string, value: unknown) => {
    handlePropsChange(key, value);
  };

  const renderEditItem = (item: WorkbenchEditItem, index: number) =>
    renderConfigItem({
      id: cpID || '',
      item,
      index,
      configs,
      isInSubTable: false,
      handlePropsChange,
      handleConfigsChange,
      handleMultiPropsChange,
      handleLayoutChange
    });

  return {
    cpID,
    componentType,
    editData,
    configs,
    isSchemaReady,
    handlePropsChange,
    handleConfigsChange,
    handleMultiPropsChange,
    handleLayoutChange,
    renderEditItem
  };
}
