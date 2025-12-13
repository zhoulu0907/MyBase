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

  const { curComponentSchema, setCurComponentSchema, setWbComponentSchemas } = useWorkbenchSignal();

  const schema = (curComponentSchema || {}) as WorkbenchComponentSchema;
  const cpID = schema.id;
  const componentType = schema.type;

  const [editData, setEditData] = useState<WorkbenchEditItem[]>([]);
  const [configs, setConfigs] = useState<WorkbenchConfig>(schema.config || {});

  const isSchemaReady = useMemo(
    () => Boolean(componentType && hasWorkbenchComponentSchema(componentType)),
    [componentType]
  );

  useEffect(() => {
    if (!componentType || !hasWorkbenchComponentSchema(componentType)) {
      setEditData([]);
      return;
    }
    const defaultSchema = getWorkbenchComponentSchema(componentType) as unknown as WorkbenchComponentSchema;
    setEditData(defaultSchema.editData || []);
    setConfigs((curComponentSchema as WorkbenchComponentSchema)?.config || defaultSchema.config || {});
  }, [componentType, curComponentSchema]);

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
