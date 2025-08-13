import { Form, TreeSelect } from '@arco-design/web-react';
import { getEntityFields, getEntityListByApp, type MetadataEntityField, type MetadataEntityPair } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from '../../index.module.less';

const FormItem = Form.Item;

export interface DynamicFieldConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
}

const DynamicFieldConfig: React.FC<DynamicFieldConfigProps> = ({ handlePropsChange, item, configs }) => {
  const [entityList, setEntityList] = useState<MetadataEntityPair[]>([]);
  const [fieldList, setFieldList] = useState<MetadataEntityField[]>([]);

  const [entityTree, setEntityTree] = useState([]);

  useEffect(() => {
    console.log(item);
    initTreeData();
  }, []);

  const getEntityList = async () => {
    const res = await getEntityListByApp('1');
    console.log('res: ', res);

    setEntityList(res);
    return res;
  };

  const getFieldList = async (entityId: string) => {
    const res = await getEntityFields({ entityId });
    console.log('res: ', res);
    setFieldList(res);

    return res;
  };

  const initTreeData = async () => {
    const entityList = await getEntityList();

    console.log(entityList);

    const newEntityTree = (entityList || []).map((entity: MetadataEntityPair) => ({
      key: entity.entityId,
      value: entity.entityId,
      title: entity.entityName
    }));

    setEntityTree(newEntityTree);
  };

  const loadFields = async (node: any, dataRef: any) => {
    console.log(node, dataRef);
    const fieldList = await getFieldList(dataRef.key);
    dataRef.children = fieldList
      .filter((field: MetadataEntityField) => !field.isSystemField)
      .map((field: MetadataEntityField) => ({
        key: field.id,
        value: field.fieldName,
        title: field.displayName,
        isLeaf: true
      }));
    setEntityTree([...entityTree]);
  };

  return (
    <FormItem layout="vertical" labelAlign="left" label={'数据字段配置'} className={styles.formItem}>
      <TreeSelect
        treeData={entityTree}
        loadMore={loadFields}
        value={configs[item.key]}
        onChange={(value) => {
          console.log(value);
          handlePropsChange(item.key, value);
        }}
      />
    </FormItem>
  );
};

export default DynamicFieldConfig;
