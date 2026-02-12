import {
  getWorkbenchComponentWidth,
  PreviewRender,
  startLoadWorkbenchPageSet,
  useWorkbenchEditorSignal,
  getOrCreatePageConfig,
  type GridItem,
  type WorkbenchComponentType
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { Fragment, useEffect } from 'react';
import styles from './index.module.less';

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
    console.log('workbench runtime pageSetId: ', pageSetId);

    // 先清空旧数据和背景样式
    clearWorkbenchComponents();
    clearWbComponentSchemas();

    const runtimeContentEle = document.getElementById('runtime-content');
    const contentBodyEle = document.getElementById('runtime-content-body');
    clearBackgroundStyle(runtimeContentEle);
    clearBackgroundStyle(contentBodyEle);

    // 加载新页面数据
    if (pageSetId) {
      startLoadWorkbenchPageSet({ pageSetId });
    }
  }, [pageSetId]);

  // 设置页面背景样式
  useEffect(() => {
    const runtimeContentEle = document.getElementById('runtime-content');
    const contentBodyEle = document.getElementById('runtime-content-body');

    const [, pageConfigSchema] = getOrCreatePageConfig(wbComponentSchemas.value);
    const { pageBgColor, pageBgImg } = pageConfigSchema.config;

    setBackgroundStyle(runtimeContentEle, {
      color: pageBgColor || '#F2F3F5',
      image: pageBgImg
    });

    setBackgroundStyle(contentBodyEle, { transparent: true });

    // 组件卸载时清除样式
    return () => {
      clearBackgroundStyle(runtimeContentEle);
      clearBackgroundStyle(contentBodyEle);
    };
  }, [wbComponentSchemas.value]);

  return (
    <>
      {workbenchComponents.value.map((cp: GridItem) => {
        const schema = wbComponentSchemas.value[cp.id];
        const sanitizedSchema = {
          ...schema
        };
        // console.log('cp: ', sanitizedSchema);
        return (
          <Fragment key={cp.id}>
            <div
              className={styles.componentItem}
              style={{
                width: `calc(${getWorkbenchComponentWidth(sanitizedSchema, cp.type as WorkbenchComponentType)} - 8px)`,
                margin: '8px'
              }}
            >
              <PreviewRender
                cpId={cp.id}
                cpType={cp.type}
                pageComponentSchema={sanitizedSchema}
                runtime={runtime}
                preview={false}
              />
            </div>
          </Fragment>
        );
      })}
    </>
  );
};

export default WorkbenchRuntime;
