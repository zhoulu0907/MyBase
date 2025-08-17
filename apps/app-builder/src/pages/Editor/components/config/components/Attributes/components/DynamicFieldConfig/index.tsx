import { useAppEntityStore } from '@/store/store_entity';
import { Cascader, Form } from '@arco-design/web-react';
import type { AppEntityField } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from '../../index.module.less';

const FormItem = Form.Item;

export interface DynamicFieldConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
}

const DynamicFieldConfig: React.FC<DynamicFieldConfigProps> = ({ handlePropsChange, item, configs }) => {
  const { mainEntity } = useAppEntityStore();

  const [entityTree, setEntityTree] = useState<any[]>([]);

  useEffect(() => {
    if (mainEntity) {
      //   console.log(mainEntity);
      initTreeData();
    }
  }, [mainEntity]);

  const initTreeData = async () => {
    const newEntityTree = mainEntity.fields
      .filter((field: AppEntityField) => field.isSystemField == 1)
      .map((field: AppEntityField) => ({
        value: field.fieldID,
        label: field.fieldName
      }));

    setEntityTree([
      {
        value: mainEntity.entityID,
        label: mainEntity.entityName,
        children: newEntityTree
      }
    ]);
  };

  return (
    <FormItem layout="vertical" labelAlign="left" label={'数据字段配置'} className={styles.formItem}>
      <Cascader
        value={configs[item.key]}
        placeholder="请选择数据字段"
        showEmptyChildren
        animation={false}
        unmountOnExit={false}
        style={{
          width: '100%'
        }}
        options={entityTree}
        onChange={(value) => {
          console.log(value);
          handlePropsChange(item.key, value);
        }}
      />
    </FormItem>
  );
};

export default DynamicFieldConfig;
