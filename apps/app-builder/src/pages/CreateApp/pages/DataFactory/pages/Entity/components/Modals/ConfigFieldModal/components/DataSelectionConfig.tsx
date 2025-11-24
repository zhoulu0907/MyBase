import React, { useState, useEffect } from 'react';
import { Form, Select, Button, Tooltip } from '@arco-design/web-react';
import { IconQuestionCircle } from '@arco-design/web-react/icon';
import { useGraphEntitytore } from '@onebase/ui-kit';
import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import styles from '../index.module.less';

interface dataSelectionConfig {
  targetEntityId: string;
  targetFieldId: string;
}

interface DataSelectionConfigProps {
  entities?: EntityNode[];
  onCancel: () => void;
  onConfirm: (config: dataSelectionConfig) => void;
}

export const DataSelectionConfig: React.FC<DataSelectionConfigProps> = ({ onCancel, onConfirm, entities }) => {
  const [form] = Form.useForm();
  const [entityOptions, setEntityOptions] = useState<{ label: string; value: string }[]>([]);
  const [fieldOptions, setFieldOptions] = useState<{ label: string; value: string }[]>([]);
  const { curEntityId } = useGraphEntitytore();

  const onEntityChange = (val: string) => {
    const filterEn = entities?.filter((item) => item.entityId === val);
    const fields =
      filterEn?.[0]?.fields?.map((item) => {
        return {
          label: item.displayName,
          value: item.fieldId
        };
      }) || [];
    setFieldOptions(fields);
  };

  const getEntities = () => {
    const enOptions =
      entities
        ?.filter((item) => item.entityId !== curEntityId)
        ?.map((item) => {
          return {
            value: item.entityId,
            label: item.entityName
          };
        }) || [];
    setEntityOptions(enOptions);
  };

  const handleCancel = () => {
    if (onCancel) {
      onCancel();
    }
  };

  const handleConfirm = () => {
    const config = form.getFieldsValue();
    console.log('handleConfirm', config);
    onConfirm(config);
  };

  useEffect(() => {
    getEntities();
  }, []);

  return (
    <>
      <Form form={form} layout="vertical">
        <Form.Item field="targetEntityId" label="关联资产">
          <Select placeholder="请选择" options={entityOptions} onChange={onEntityChange}></Select>
        </Form.Item>

        <Form.Item
          field="targetFieldId"
          label={
            <>
              <span>显示字段</span>
              <Tooltip content="用户选择关联记录后，表单中将显示此字段的值">
                <IconQuestionCircle />
              </Tooltip>
            </>
          }
        >
          <Select placeholder="请选择" options={fieldOptions}></Select>
        </Form.Item>
      </Form>

      <div className={styles.fieldTypeConfigFooter}>
        <Button type="outline" size="small" onClick={handleCancel}>
          取消
        </Button>
        <Button type="primary" size="small" onClick={handleConfirm}>
          确定
        </Button>
      </div>
    </>
  );
};
