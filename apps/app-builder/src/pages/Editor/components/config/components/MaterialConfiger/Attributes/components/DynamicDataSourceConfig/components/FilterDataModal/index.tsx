import { Form, Modal } from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';

import { getFieldCheckTypeApi } from '@onebase/app';
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
  FILTERCONDITION: 'filterCondition'
};

const FilterDataModal: React.FC<FilterDataModalProps> = ({ visible, item, configs, onCancel, onOk }) => {
  const [payloadForm] = Form.useForm();
  const [dataFilters, setDataFilters] = useState<any[]>([]);
  const [validationTypes, setValidationTypes] = useState<any[]>([]);

  useEffect(() => {
    if (visible) {
      initialData();
    }
  }, [visible]);

  const initialData = async () => {
    const [dataFilters, fieldIds] = (configs[PROPSNAME.DISPLAYFIELDSOPTIONS] as any[]).reduce(
      (acc, item: any) => {
        const [dataFilters, fieldIds] = acc;
        dataFilters.push({
          key: item.fieldId,
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
        key: configs[item.key].entityId,
        title: configs[item.key].entityName,
        children: dataFilters
      }
    ]);
    if (fieldIds?.length) {
      const newValidationTypes = await getFieldCheckTypeApi(fieldIds);
      setValidationTypes(newValidationTypes);
    }
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
              nodeId={configs[item.key].entityId}
              label="添加过滤条件"
              required
              form={payloadForm}
              fields={dataFilters}
              entityFieldValidationTypes={validationTypes}
            />
          </Form>
        </div>
      </Modal>
    </>
  );
};

export default FilterDataModal;
