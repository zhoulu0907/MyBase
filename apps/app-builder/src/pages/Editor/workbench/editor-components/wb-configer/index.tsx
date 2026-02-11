import { useMemo } from 'react';
import { ICON_Map_By_Type } from '@/components/MaterialCard/icons';
import { useWorkbenchSignal, isPageConfig } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import pageIcon from '@/assets/workbench/page_icon.svg';
import * as ComponentConfig from './ConfigsByComp';
import styles from './index.module.less';

/**
 * 工作台配置面板组件
 * 根据组件类型加载不同的配置组件
 */
const WorkbenchConfiger = () => {
  useSignals();

  const { curComponentID, curComponentSchema } = useWorkbenchSignal();

  const componentType = useMemo(() => curComponentSchema?.type, [curComponentSchema?.type]);
  // 使用工具函数判断是否为页面配置
  const isPageConfigType = useMemo(
    () => isPageConfig(curComponentSchema) || !curComponentID,
    [curComponentSchema, curComponentID]
  );

  const configComponent = useMemo(() => {
    if (isPageConfigType) {
      return <ComponentConfig.PageConfig />;
    }

    // 根据组件类型动态加载对应的配置组件，例如：XQuickEntry -> QuickEntryConfig
    if (!componentType) {
      return <ComponentConfig.QuickEntryConfig />;
    }

    const configName = `${componentType.replace(/^X/, '')}Config`;
    const ConfigComponent = ComponentConfig[configName as keyof typeof ComponentConfig];
    return ConfigComponent ? <ConfigComponent /> : <ComponentConfig.QuickEntryConfig />;
  }, [isPageConfigType, componentType]);

  // 显示名称
  const displayName = useMemo(() => {
    if (isPageConfigType) return '页面配置';
    return curComponentSchema?.displayName || curComponentSchema?.config?.cpName || '工作台组件';
  }, [isPageConfigType, curComponentSchema?.displayName, curComponentSchema?.config?.cpName]);

  // 图标
  const icon = useMemo(() => {
    if (isPageConfigType) {
      return <img src={pageIcon} alt="页面配置" className={styles.pageIcon} />;
    }
    return ICON_Map_By_Type[componentType];
  }, [isPageConfigType, componentType]);

  return (
    <div className={styles.workbenchConfigs}>
      <div className={styles.componentName}>
        <div className={styles.icon}>{icon}</div>
        {displayName}
      </div>
      <div className={styles.componentInfo}>{configComponent}</div>
    </div>
  );
};

export default WorkbenchConfiger;
