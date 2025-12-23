import { ICON_Map_By_Type } from '@/components/MaterialCard/icons';
import { useWorkbenchSignal } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import pageIcon from '@/assets/workbench/page_icon.svg';
import {
  PageConfig,
  QuickEntryConfig,
  TodoCenterConfig,
  RichTextEditorWorkbenchConfig,
  CarouselWorkbenchConfig,
  ButtonWorkbenchConfig
} from './ConfigsByComp';
import styles from './index.module.less';
import { useMemo } from 'react';

/**
 * 工作台配置面板组件
 * 根据组件类型加载不同的配置组件
 */
const WorkbenchConfiger = () => {
  useSignals();

  const { curComponentID, curComponentSchema } = useWorkbenchSignal();

  const componentType = useMemo(() => curComponentSchema?.type, [curComponentSchema?.type]);
  const isPageConfig = useMemo(() => componentType === 'page' || !curComponentID, [componentType, curComponentID]);

  const configComponent = useMemo(() => {
    if (isPageConfig) {
      return <PageConfig />;
    }

    // 根据组件类型加载对应的配置组件
    switch (componentType) {
      case 'XQuickEntry':
        return <QuickEntryConfig />;
      case 'XTodoCenter':
        return <TodoCenterConfig />;
      case 'XRichTextEditorWorkbench':
        return <RichTextEditorWorkbenchConfig />;
      case 'XCarouselWorkbench':
        return <CarouselWorkbenchConfig />;
      case 'XButtonWorkbench':
        return <ButtonWorkbenchConfig />;
      default:
        return <QuickEntryConfig />;
    }
  }, [isPageConfig, componentType]);

  // 显示名称
  const displayName = useMemo(() => {
    if (isPageConfig) return '页面配置';
    return curComponentSchema?.displayName || curComponentSchema?.config?.cpName || '工作台组件';
  }, [isPageConfig, curComponentSchema?.displayName, curComponentSchema?.config?.cpName]);

  // 图标
  const icon = useMemo(() => {
    if (isPageConfig) {
      return <img src={pageIcon} alt="页面配置" className={styles.pageIcon} />;
    }
    return ICON_Map_By_Type[componentType];
  }, [isPageConfig, componentType]);

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
