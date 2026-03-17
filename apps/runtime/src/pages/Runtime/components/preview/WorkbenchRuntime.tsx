import {
  getWorkbenchComponentWidth,
  PreviewRender,
  startLoadWorkbenchPageSet,
  useWorkbenchEditorSignal,
  getOrCreatePageConfig,
  pageLayoutSignal,
  STATUS_OPTIONS,
  STATUS_VALUES,
  type GridItem,
  type WorkbenchComponentType
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { Spin } from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';
import DevelopEmpty from '@/assets/images/develop_empty.svg';
import styles from './index.module.less';

// ==================== Grid 布局工具 ====================

const WB_GRID_CONFIG = {
  rowHeight: 32,
  gap: 16,
  columns: 12
} as const;

function percentageToColSpan(widthStr: string): number {
  const percentage = Number.parseFloat(String(widthStr).replace('%', '')) || 100;
  const raw = Math.round((percentage / 100) * WB_GRID_CONFIG.columns);
  return Math.min(Math.max(raw, 3), WB_GRID_CONFIG.columns);
}

const FLOATING_COMPONENT_TYPES = ['XChatbot'];
const isFloatingComponent = (type: string) => FLOATING_COMPONENT_TYPES.includes(type);

interface WorkbenchRuntimeProps {
  pageSetId: string;
  runtime: boolean;
}

// 设置元素背景样式
const setBackgroundStyle = (
  element: HTMLElement | null,
  config: { color?: string; image?: string; transparent?: boolean }
) => {
  if (!element?.style) return;

  const { color, image, transparent } = config;

  if (transparent) {
    element.style.backgroundColor = 'transparent';
    return;
  }

  element.style.backgroundColor = color || '';

  if (image) {
    Object.assign(element.style, {
      backgroundImage: `url(${image})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center',
      backgroundRepeat: 'no-repeat'
    });
  } else {
    Object.assign(element.style, {
      backgroundImage: '',
      backgroundSize: '',
      backgroundPosition: '',
      backgroundRepeat: ''
    });
  }
};

// 清除元素背景样式
const clearBackgroundStyle = (element: HTMLElement | null) => {
  setBackgroundStyle(element, {});
};

const WorkbenchRuntime: React.FC<WorkbenchRuntimeProps> = ({ pageSetId, runtime }) => {
  useSignals();

  const { workbenchComponents, wbComponentSchemas, clearWorkbenchComponents, clearWbComponentSchemas } =
    useWorkbenchEditorSignal;
  const { setPageLayout, resetPageLayout } = pageLayoutSignal;
  const [loading, setLoading] = useState(false);

  // 组件挂载时清理旧数据和背景样式
  useEffect(() => {
    clearWorkbenchComponents();
    clearWbComponentSchemas();

    const runtimeContentEle = document.getElementById('runtime-content');
    const contentBodyEle = document.getElementById('runtime-content-body');

    clearBackgroundStyle(runtimeContentEle);
    clearBackgroundStyle(contentBodyEle);
  }, []);

  useEffect(() => {
    // 先清空旧数据和背景样式
    clearWorkbenchComponents();
    clearWbComponentSchemas();

    // 重置页面布局配置
    resetPageLayout();

    const runtimeContentEle = document.getElementById('runtime-content');
    const contentBodyEle = document.getElementById('runtime-content-body');
    clearBackgroundStyle(runtimeContentEle);
    clearBackgroundStyle(contentBodyEle);

    // 加载新页面数据
    if (pageSetId) {
      const loadData = async () => {
        setLoading(true);
        try {
          await startLoadWorkbenchPageSet({ pageSetId });
        } finally {
          setTimeout(() => {
            setLoading(false);
          }, 100);
        }
      };

      loadData();
    }
  }, [pageSetId]);

  // 应用页面配置（背景样式和布局配置）
  useEffect(() => {
    const runtimeContentEle = document.getElementById('runtime-content');
    const contentBodyEle = document.getElementById('runtime-content-body');

    // 如果没有配置数据，清除背景样式并重置布局
    if (!wbComponentSchemas.value || Object.keys(wbComponentSchemas.value).length === 0) {
      clearBackgroundStyle(runtimeContentEle);
      clearBackgroundStyle(contentBodyEle);
      resetPageLayout();
      return;
    }

    const [, pageConfigSchema] = getOrCreatePageConfig(wbComponentSchemas.value);
    const { pageBgColor, pageBgImg, showHeader, showSidebar } = pageConfigSchema.config;

    // 设置背景样式
    setBackgroundStyle(runtimeContentEle, {
      color: pageBgColor || '#F2F3F5',
      image: pageBgImg
    });
    setBackgroundStyle(contentBodyEle, { transparent: true });

    // 设置页面布局配置
    setPageLayout({ showHeader, showSidebar });

    return () => {
      clearBackgroundStyle(runtimeContentEle);
      clearBackgroundStyle(contentBodyEle);
      resetPageLayout();
    };
  }, [wbComponentSchemas.value]);

  const normalComponents = workbenchComponents.value.filter((cp: GridItem) => !isFloatingComponent(cp.type));
  const floatingComponents = workbenchComponents.value.filter((cp: GridItem) => isFloatingComponent(cp.type));

  return (
    <>
      {loading ? (
        <div className={styles.loading}>
          <Spin size={40} tip="加载中..." />
        </div>
      ) : workbenchComponents.value.length > 0 ? (
        <>
          {/* 普通组件 - grid 布局 */}
          <div className={styles.wbGrid}>
            {normalComponents.map((cp: GridItem) => {
              const schema = wbComponentSchemas.value[cp.id];
              if (!schema) return null;
              if (schema.config?.status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]) return null;
              const widthStr = getWorkbenchComponentWidth(schema, cp.type as WorkbenchComponentType);
              const colSpan = percentageToColSpan(widthStr);
              const rowSpan = (schema?.config?.gridLayout as { rowSpan?: number } | undefined)?.rowSpan ?? 1;
              return (
                <div
                  key={cp.id}
                  className={styles.componentItem}
                  style={{ gridColumn: `span ${colSpan}`, gridRow: `span ${rowSpan}` }}
                >
                  <PreviewRender
                    cpId={cp.id}
                    cpType={cp.type}
                    pageComponentSchema={schema}
                    runtime={runtime}
                    preview={false}
                  />
                </div>
              );
            })}
          </div>

          {/* 浮动组件 */}
          {floatingComponents.map((cp: GridItem) => {
            const schema = wbComponentSchemas.value[cp.id];
            if (!schema) return null;
            const floatingConfig = schema?.config?.floatingConfig as
              | { right?: number; bottom?: number; width?: number; height?: number }
              | undefined;
            return (
              <div
                key={cp.id}
                style={{
                  position: 'fixed',
                  right: floatingConfig?.right ?? 80,
                  bottom: floatingConfig?.bottom ?? 80,
                  width: floatingConfig?.width ?? 80,
                  height: floatingConfig?.height ?? 80,
                  zIndex: 100
                }}
              >
                <PreviewRender
                  cpId={cp.id}
                  cpType={cp.type}
                  pageComponentSchema={schema}
                  runtime={true}
                  preview={false}
                />
              </div>
            );
          })}
        </>
      ) : (
        <div className={styles.noData}>
          <img src={DevelopEmpty} alt="暂无数据" />
        </div>
      )}
    </>
  );
};

export default WorkbenchRuntime;
