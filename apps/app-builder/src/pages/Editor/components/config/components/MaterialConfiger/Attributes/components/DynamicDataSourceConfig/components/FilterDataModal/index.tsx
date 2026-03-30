import { Form, Modal } from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';
import type { TreeSelectDataType } from '@arco-design/web-react/es/TreeSelect/interface';

import { getFieldCheckTypeApi, getEntityListWithFields, type AppEntityField, type EntityFieldValidationTypes } from '@onebase/app';
import { useAppEntityStore } from '@onebase/ui-kit';
import styles from '../../index.module.less';
import ConditionEditor from '@/pages/CreateApp/pages/IntegratedManagement/triggerEditor/components/condition-editor';

interface FilterDataModalProps {
  visible: boolean;
  item: any;
  configs: any;
  onCancel: any;
  onOk: any;
}

const PROPSNAME = {
  DISPLAYFIELDSOPTIONS: 'displayFieldsOptions',
  FILTERCONDITION: 'filterCondition',
  SELECTEDDATASOURCE: 'selectedDataSource'
};

// 构建变量选项（当前表单的主表和关联表字段）
function buildVariableOptions(mainEntity: any, subEntities: any): TreeSelectDataType[] {
  const options: TreeSelectDataType[] = [];
  const currentNode: TreeSelectDataType = {
    key: 'currentForm',
    title: '当前表单',
    disabled: true,
    children: []
  };

  if (mainEntity?.fields?.length > 0) {
    currentNode.children!.push({
      key: mainEntity.tableName,
      title: mainEntity.entityName || '主表',
      disabled: true,
      children: mainEntity.fields.map((field: any) => ({
        key: `${mainEntity.tableName}.${field.fieldName}`,
        title: field.displayName || field.fieldName,
        fieldType: field.fieldType
      }))
    });
  }

  if (subEntities?.entities?.length > 0) {
    subEntities.entities.forEach((entity: any) => {
      if (entity.fields?.length > 0) {
        currentNode.children!.push({
          key: entity.tableName,
          title: entity.entityName || '关联表',
          disabled: true,
          children: entity.fields.map((field: any) => ({
            key: `${entity.tableName}.${field.fieldName}`,
            title: field.displayName || field.fieldName,
            fieldType: field.fieldType
          }))
        });
      }
    });
  }

  options.push(currentNode);
  return options;
}

const FilterDataModal: React.FC<FilterDataModalProps> = ({ visible, item, configs, onCancel, onOk }) => {
  const { mainEntity, subEntities } = useAppEntityStore();
  const [payloadForm] = Form.useForm();
  const [dataFilters, setDataFilters] = useState<any[]>([]);
  const [validationTypes, setValidationTypes] = useState<any[]>([]);
  const [variableOptions, setVariableOptions] = useState<TreeSelectDataType[]>([]);

  useEffect(() => {
    if (visible) {
      initialData();
    }
  }, [visible]);

  const initialData = async () => {
    const displayFieldsOptions = configs[PROPSNAME.DISPLAYFIELDSOPTIONS];
    const tableName = configs[PROPSNAME.SELECTEDDATASOURCE].tableName;
    const [dataFilters, fieldIds] = (displayFieldsOptions as any[]).reduce(
      (acc, item: any) => {
        const [dataFilters, fieldIds] = acc;
        dataFilters.push({
          key: `${tableName}.${item.fieldName}`,
          title: item.displayName,
          fieldType: item.fieldType
        });
        fieldIds.push(item.fieldId);
        return acc;
      },
      [[], []]
    );
    setDataFilters([
      {
        key: configs[item.key].entityUuid,
        title: configs[item.key].entityName,
        children: dataFilters
      }
    ]);
    if (fieldIds?.length) {
      const newValidationTypes = await getFieldCheckTypeApi(fieldIds);
      newValidationTypes.forEach((item: EntityFieldValidationTypes) => {
        const fieldName =
          displayFieldsOptions.find((field: AppEntityField) => field.fieldId == item.fieldId)?.fieldName || '';
        item.fieldKey = `${tableName}.${fieldName}`;
      });
      setValidationTypes(newValidationTypes);
    }

    // 构建变量选项 - 如果 store 为空，从 API 获取
    let finalMainEntity = mainEntity;
    let finalSubEntities = subEntities;

    if (!mainEntity?.fields?.length) {
      try {
        const entityUuid = mainEntity?.entityUuid || configs[item.key].entityUuid;
        const entityList = await getEntityListWithFields({ entityUuids: [entityUuid] });
        if (entityList?.[0]) {
          const entity = entityList[0];
          finalMainEntity = { ...mainEntity, fields: entity.fields || [] };
          finalSubEntities = {
            entities: entity.childEntities?.map((child: any) => ({
              tableName: child.childTableName,
              entityName: child.childEntityName,
              fields: child.childFields || []
            })) || []
          };
        }
      } catch (e) {
        console.error('Failed to fetch entity data:', e);
      }
    }

    const varOptions = buildVariableOptions(finalMainEntity, finalSubEntities);
    setVariableOptions(varOptions);

    setTimeout(() => {
      payloadForm.setFieldValue(PROPSNAME.FILTERCONDITION, configs[PROPSNAME.FILTERCONDITION]);
    }, 0);
  };

  const handleOkModal = () => {
    const data = payloadForm.getFieldValue(PROPSNAME.FILTERCONDITION);
    onOk(data);
  };

  return (
    <>
      <Modal
        className={styles.filterDataModal}
        style={{ width: '800px' }}
        title={<span className={styles.modalTitleLeft}>添加过滤条件</span>}
        visible={visible}
        onCancel={onCancel}
        onOk={handleOkModal}
        autoFocus={false}
        focusLock={true}
        escToExit={false}
        maskClosable={false}
      >
        <div className={styles.popupContainer}>
          <span className={styles.titleSpan}>添加过滤条件来限定可选数据范围</span>
          <Form layout="vertical" form={payloadForm}>
            {/* 添加过滤条件 */}
            <ConditionEditor
              nodeId={configs[item.key].entityUuid}
              label="添加过滤条件"
              required
              form={payloadForm}
              fields={dataFilters}
              entityFieldValidationTypes={validationTypes}
              variableOptions={variableOptions}
            />
          </Form>
        </div>
      </Modal>
    </>
  );
};

export default FilterDataModal;
