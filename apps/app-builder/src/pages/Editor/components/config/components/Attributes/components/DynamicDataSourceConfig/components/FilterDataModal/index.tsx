import { Modal } from '@arco-design/web-react';
import React, { useEffect } from 'react';

import type { ConfitionField, EntityFieldValidationTypes } from '@onebase/app';
import ConditionEditor from '@/pages/CreateApp/pages/IntegratedManagement/triggerEditor/components/condition-editor';
import styles from '../../index.module.less';

interface FilterDataModalProps {
  visible: boolean;
  onCancel: any;
}

// mock up
const dataFilters: ConfitionField[] = [
  { label: '标题', value: 'title', fieldType: 'string' },
  { label: '单行文本', value: 'singleText', fieldType: 'string' },
  { label: '提交时间', value: 'submitTime', fieldType: 'string' },
  { label: '多行文本', value: 'multiText', fieldType: 'string' },
  { label: '单选按钮组', value: 'radioGroup', fieldType: 'string' }
];

const filterFieldCheckType: EntityFieldValidationTypes[] = [
  { fieldId: 'a', fieldTypeCode: 'a', validationTypes: [] },
  { fieldId: 'b', fieldTypeCode: 'b', validationTypes: [] },
  { fieldId: 'c', fieldTypeCode: 'c', validationTypes: [] },
  { fieldId: 'd', fieldTypeCode: 'd', validationTypes: [] }
];

const changeDataFilters = (value: any[]) => {
  console.log('changeDataFilters', value);
};

const FilterDataModal: React.FC<FilterDataModalProps> = ({ visible, onCancel }) => {
  // todo
  useEffect(() => {
    if (visible) {
    }
  }, [visible]);

  return (
    <>
      <Modal
        className={styles.filterDataModal}
        title={<span className={styles.modalTitleLeft}>添加过滤条件</span>}
        visible={visible}
        onCancel={onCancel}
        onOk={onCancel}
        autoFocus={false}
        focusLock={true}
        escToExit={false}
        maskClosable={false}
      >
        <div className={styles.popupContainer}>
          <span className={styles.titleSpan}>添加过滤条件来限定可选数据范围</span>
          {/* 添加过滤条件 */}
          <ConditionEditor
            onChange={changeDataFilters}
            data={[]}
            fields={dataFilters}
            entityFieldValidationTypes={filterFieldCheckType}
          />
        </div>
      </Modal>
    </>
  );
};

export default FilterDataModal;
