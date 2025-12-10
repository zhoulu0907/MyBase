import { ICON_Map_By_Type } from '@/components/MaterialCard/icons';
import { usePageEditorSignal } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import pageIcon from '@/assets/workbench/page_icon.svg';
import PageConfig from './PageConfig';
import QuickEntryConfig from './QuickEntryConfig';
import TodoCenterConfig from './TodoCenterConfig';
import RichTextEditorWorkbenchConfig from './RichTextEditorWorkbenchConfig';
import CarouselWorkbenchConfig from './CarouselWorkbenchConfig';
import styles from './index.module.less';

/**
 * 工作台配置面板组件
 * 根据组件类型加载不同的配置组件
 */
const WorkbenchConfiger = () => {
  useSignals();

  const { curComponentID, curComponentSchema } = usePageEditorSignal();
  console.log('curComponentSchema', curComponentSchema);

  // 判断是否为页面配置
  const isPageConfig = curComponentSchema?.type === 'page' || !curComponentID;

  // 根据组件类型渲染不同的配置组件
  const renderComponentConfig = () => {
    if (isPageConfig) {
      return <PageConfig />;
    }

    const componentType = curComponentSchema?.type;

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
      default:
        // 默认显示快捷入口配置（兼容处理）
        return <QuickEntryConfig />;
    }
  };

  return (
    <div className={styles.workbenchConfigs}>
      <div className={styles.componentName}>
        <div className={styles.icon}>
          {isPageConfig ? (
            <img src={pageIcon} alt="页面配置" className={styles.pageIcon} />
          ) : (
            ICON_Map_By_Type[curComponentSchema?.type]
          )}
        </div>
        {isPageConfig
          ? '页面配置'
          : curComponentSchema?.displayName || curComponentSchema?.config?.cpName || '工作台组件'}
      </div>
      <div className={styles.componentInfo}>{renderComponentConfig()}</div>
    </div>
  );
};

export default WorkbenchConfiger;
