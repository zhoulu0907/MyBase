import { Form } from '@arco-design/web-react';
import { cloneDeep } from 'lodash-es';
import React, { useCallback, useEffect, useState } from 'react';
import { getComponentConfig, getComponentImpl, getComponentDescriptor, isPluginComponentType } from 'src/components/Materials';

/**
 * 组件渲染的通用属性
 */
interface PreviewRenderProps {
  /** 组件ID */
  cpId: string;
  /** 组件类型 */
  cpType: string;
  /** 组件schema映射 */
  pageComponentSchema: any;

  /** 运行状态 */
  runtime: boolean;

  /** 预览状态 */
  preview?: boolean;

  // 详情视图
  detailMode?: boolean;

  showFromPageData?: Function;

  refresh?: number;
  pageType?: string;

  // 自定义视图规则
  cpState?: any;

  // 表格数据id
  recordId?: string;
}

const PreviewRender: React.FC<PreviewRenderProps> = ({
  cpId,
  cpType,
  pageComponentSchema,
  runtime,
  preview,
  detailMode,
  showFromPageData,
  refresh,
  pageType,
  cpState,
  recordId
}) => {
  // 获取组件配置，使用深拷贝确保每次都是新对象
  const [componentConfig, setComponentConfig] = useState(() =>
    cloneDeep(getComponentConfig(pageComponentSchema, cpType))
  );

  const { form } = Form.useFormContext();

  // 处理组件配置的更新
  useEffect(() => {
    if (cpState) {
      // 使用函数式更新，基于当前状态创建新对象，避免依赖 componentConfig
      setComponentConfig((prevConfig: any) => {
        // 创建新对象，而不是直接修改原对象
        const updatedConfig = {
          ...prevConfig,
          status: cpState.status !== undefined ? cpState.status : prevConfig.status,
          verify: {
            ...prevConfig.verify,
            required: cpState.required !== undefined ? cpState.required : prevConfig.verify.required
          }
        };
        return updatedConfig;
      });
    } else {
      // 使用深拷贝确保每次都是新对象，避免引用共享问题
      const newComponentConfig = cloneDeep(getComponentConfig(pageComponentSchema, cpType));

      setComponentConfig(newComponentConfig);
    }
  }, [cpState, pageComponentSchema, cpType]);

  // 单独处理表单值的更新，使用 queueMicrotask 确保在渲染后执行
  useEffect(() => {
    if (cpState?.value && pageComponentSchema?.config?.dataField && pageComponentSchema.config.dataField.length > 1) {
      const fieldName = pageComponentSchema.config.dataField[pageComponentSchema.config.dataField.length - 1];
      // 检查当前表单值是否与目标值不同
      const currentValue = form.getFieldValue(fieldName);
      if (currentValue !== cpState.value) {
        // 使用 queueMicrotask 延迟执行，确保不在渲染期间更新表单
        queueMicrotask(() => {
          form.setFieldValue(fieldName, cpState.value);
        });
      }
    }
  }, [cpState?.value, pageComponentSchema?.config?.dataField, form]);

  // 基于视图规则渲染

  const renderComponent = useCallback(() => {
    const descriptor = getComponentDescriptor(cpType as any);
    const Impl: any = getComponentImpl(cpType as any);
    if (!Impl) return <div>未知组件类型: {cpType}</div>;

    const baseProps: any = { cpName: cpId, id: cpId, ...componentConfig };

    if (descriptor.template.category === 'form' || descriptor.template.category === 'show') {
      baseProps.runtime = runtime;
      baseProps.detailMode = detailMode;
    }

    if (descriptor.template.category === 'layout') {
      baseProps.pageType = pageType;
      baseProps.detailMode = detailMode;
    }

    if (descriptor.template.category === 'list') {
      baseProps.runtime = runtime;
      baseProps.preview = preview;
      baseProps.showFromPageData = showFromPageData;
      baseProps.refresh = refresh;
    }

    if (isPluginComponentType(cpType)) {
      // 预留：插件组件特定扩展点（若未来需要按插件增强 props，可在此统一处理）
    }

    return <Impl {...baseProps} />;
  }, [componentConfig, refresh]);

  return <>{renderComponent()}</>;
};

export default PreviewRender;
