import { useMemo, useEffect, useState } from 'react';
import { createForm, onFormValuesChange } from '@formily/core';
import { createSchemaField, FormProvider, type ISchema } from '@formily/react';
import { Button, Message, Spin } from '@arco-design/web-react';
import { useConnectorWizardStore } from '../store';
import { componentMap, FormilyFormItem } from '../../../../../../../../components/DynamicForm/componentMapper';
import styles from '../index.module.less';

interface DynamicConfigStepProps {
  schemaType: 'conn_config' | 'action_config';
  stepIndex: number;
  title: string;
}

const DynamicConfigStep: React.FC<DynamicConfigStepProps> = ({ schemaType, title }) => {
  const { schemas, formData, updateFormData, nextStep, prevStep, ui, envList, fetchEnvList } = useConnectorWizardStore();
  const [isEnvListLoading, setIsEnvListLoading] = useState(false);

  // 获取对应的 schema
  const schema = schemas[schemaType] as ISchema;

  // 创建 Formily 表单实例
  const form = useMemo(
    () =>
      createForm({
        values: formData[schemaType] || {},
        effects() {
          // 监听表单值变化，同步到 store
          onFormValuesChange((form) => {
            updateFormData({ [schemaType]: form.values });
          });
        },
      }),
    [schemaType, updateFormData]
  );

  // 监听 envMode 字段变化，动态加载环境列表
  useEffect(() => {
    if (schemaType === 'conn_config') {
      const envModeValue = formData[schemaType]?.envMode;
      if (envModeValue === 'select' && envList.length === 0) {
        setIsEnvListLoading(true);
        fetchEnvList().finally(() => {
          setIsEnvListLoading(false);
        });
      }
    }
  }, [formData, schemaType, envList.length, fetchEnvList]);

  // 当环境列表更新时，动态更新 existingEnvId 字段的选项
  useEffect(() => {
    if (schemaType === 'conn_config' && form && envList.length > 0) {
      form.setFieldState('existingEnvId', (state) => {
        state.componentProps = {
          ...state.componentProps,
          options: envList.map((env) => ({
            label: env.name,
            value: env.id,
          })),
        };
      });
    }
  }, [form, schemaType, envList]);

  // 设置 existingEnvId 字段的加载状态
  useEffect(() => {
    if (schemaType === 'conn_config' && form) {
      form.setFieldState('existingEnvId', (state) => {
        state.componentProps = {
          ...state.componentProps,
          loading: isEnvListLoading,
        };
      });
    }
  }, [form, schemaType, isEnvListLoading]);

  // 创建 SchemaField 组件
  const SchemaField = useMemo(
    () =>
      createSchemaField({
        components: {
          ...componentMap,
          FormItem: FormilyFormItem,
        },
      }),
    []
  );

  const handleNext = async () => {
    try {
      // Formily 自动校验
      await form.validate();
      // 提交前更新表单数据
      updateFormData({ [schemaType]: form.values });
      nextStep();
    } catch (error) {
      console.error('表单校验失败:', error);
      Message.error('请检查表单填写是否完整');
    }
  };

  if (ui.isLoading || !schema) {
    return (
      <div className={styles.stepContainer}>
        <Spin loading={true} tip="加载配置中...">
          <div style={{ height: 400 }} />
        </Spin>
      </div>
    );
  }

  if (ui.error) {
    return (
      <div className={styles.stepContainer}>
        <h3>{title}</h3>
        <div style={{ color: 'rgb(var(--danger-6))' }}>{ui.error}</div>
        <div className={styles.stepFooter}>
          <Button onClick={prevStep}>上一步</Button>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.stepContainer}>
      <h3>{title}</h3>

      <FormProvider form={form}>
        <SchemaField schema={schema} />
      </FormProvider>

      <div className={styles.stepFooter}>
        <Button onClick={prevStep}>上一步</Button>
        <Button type="primary" onClick={handleNext}>
          下一步
        </Button>
      </div>
    </div>
  );
};

export default DynamicConfigStep;
