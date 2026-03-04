import { Button, Form, Modal } from '@arco-design/web-react';
import { IconFilter } from '@arco-design/web-react/icon';
import { useEffect, useState } from 'react';
import { CONFIG_TYPES, useAppEntityStore } from '@onebase/ui-kit';
import {
  getFieldCheckTypeApi,
  getEntityFields,
  type MetadataEntityField,
  type EntityFieldValidationTypes
} from '@onebase/app';
import { registerConfigRenderer } from '../../registry';
import ConditionEditor from '@/pages/CreateApp/pages/IntegratedManagement/triggerEditor/components/condition-editor';
import styles from '../../index.module.less';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicFilterConditionConfig = ({ handlePropsChange, item, configs }: Props) => {
  const filterConditionKey = 'filterCondition';
  const { mainEntity, subEntities } = useAppEntityStore();

  const [filterVisible, setFilterVisible] = useState(false); //添加过滤条件
  const [dataFilters, setDataFilters] = useState<any[]>([]);
  const [validationTypes, setValidationTypes] = useState<any[]>([]);
  const [payloadForm] = Form.useForm();

  useEffect(() => {
    if (filterVisible) {
      getFieldList();
    }
  }, [filterVisible]);

  // 获取字段列表
  const getFieldList = async () => {
    const res = await getEntityFields({ entityUuid: configs.metaData });
    const dataFilters = res?.map((item: MetadataEntityField) => {
      return {
        key: `${configs.tableName}.${item.fieldName}`,
        title: item.displayName,
        fieldType: item.fieldType
      };
    });
    let title = '';
    if (configs.tableName === mainEntity.tableName) {
      title = mainEntity.entityName;
    } else {
      const subEntity = subEntities.entities.find((ele) => ele.tableName === configs.tableName);
      title = subEntity?.entityName || '';
    }
    setDataFilters([
      {
        key: configs.metaData,
        title: title,
        children: dataFilters
      }
    ]);
    const fieldIds = res?.map((ele: any) => ele.id);
    getValidationTypes(fieldIds, res);
  };

  const getValidationTypes = async (fieldIds: string[], fields: MetadataEntityField[]) => {
    if (fieldIds && fieldIds.length > 0) {
      const newValidationTypes = await getFieldCheckTypeApi(fieldIds);
      newValidationTypes.forEach((item: EntityFieldValidationTypes) => {
        const fieldName = fields.find((field) => field.id == item.fieldId)?.fieldName || '';
        item.fieldKey = `${configs.tableName}.${fieldName}`;
      });
      setValidationTypes(newValidationTypes);
    } else {
      setValidationTypes([]);
    }

    // 防止历史数据不是数组
    const value = Array.isArray(configs[filterConditionKey]) ? configs[filterConditionKey] : [];
    payloadForm.setFieldValue(filterConditionKey, value);
  };

  const handleOkModal = () => {
    const values = payloadForm.getFieldValue(filterConditionKey);
    handlePropsChange(filterConditionKey, values);
    setFilterVisible(false);
  };

  return (
    <>
      <Form.Item className={styles.formItem} label="数据过滤">
        <Button long onClick={() => setFilterVisible(true)} disabled={!configs.tableName}>
          {configs[filterConditionKey] && configs[filterConditionKey].length > 0 ? (
            <>
              <IconFilter />
              <span>编辑过滤条件</span>
            </>
          ) : (
            <>
              <IconFilter />
              <span>添加过滤条件</span>
            </>
          )}
        </Button>
      </Form.Item>
      <Modal
        style={{ width: '800px' }}
        title="添加过滤条件"
        visible={filterVisible}
        onCancel={() => setFilterVisible(false)}
        onOk={handleOkModal}
        autoFocus={false}
        focusLock={true}
        escToExit={false}
        maskClosable={false}
        unmountOnExit={true}
      >
        <Form layout="vertical" form={payloadForm}>
          {/* 添加过滤条件 */}
          <ConditionEditor
            nodeId={configs.metaData}
            label="添加过滤条件"
            required
            form={payloadForm}
            fields={dataFilters}
            entityFieldValidationTypes={validationTypes}
          />
        </Form>
      </Modal>
    </>
  );
};

export default DynamicFilterConditionConfig;

registerConfigRenderer(CONFIG_TYPES.DATA_FILTER, ({ handlePropsChange, item, configs }) => (
  <DynamicFilterConditionConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
