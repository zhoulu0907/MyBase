import React, { memo, useCallback, useRef } from 'react';
import { Button, Form, Modal, Spin } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { useFieldStore } from '@/store/store_field';
import FieldConfigPopover from './components/FieldConfigPopover';
import styles from './index.module.less';
import SortableTable from './components/SortableTable';
import TableColumns from './components/TableColumns';
import { useFieldManager } from './hooks/useFieldManager';
import type { ConfigFieldModalProps } from './types';
import { FIELD_TYPES_NEED_CONFIG } from './utils/const';

const ConfigFieldModal: React.FC<ConfigFieldModalProps> = memo(
  ({ visible, setVisible, entity, successCallback, gotoDictPage, entities }: ConfigFieldModalProps) => {
    // 使用统一的字段管理hook
    const fieldManager = useFieldManager(visible, entity, successCallback, setVisible);

    // 字段类型选项
    const fieldTypeOptions = useFieldStore.getState().fieldTypes.map((item) => ({
      label: item.displayName,
      value: item.fieldType
    }));

    // 渲染字段配置 popover 内容
    const renderFieldConfigContent = useCallback(
      (fieldType: string, fieldId: string) => {
        const field = fieldManager.getFieldById(fieldId);
        if (!field) return null;

        return (
          <FieldConfigPopover
            fieldType={fieldType}
            fieldId={fieldId}
            field={field}
            fields={fieldManager.originFields}
            onConfirm={fieldManager.handleConfigConfirm}
            onCancel={fieldManager.handleConfigCancel}
            gotoDictPage={gotoDictPage}
            entities={entities}
          />
        );
      },
      [
        fieldManager.getFieldById,
        fieldManager.originFields,
        fieldManager.handleConfigConfirm,
        fieldManager.handleConfigCancel
      ]
    );

    const handleAddField = useCallback(async () => {
      await fieldManager.addField();

      // 滚动到底部新增行
      const container = document.getElementById('field-config-container');
      const tableBody = container?.querySelector('.pc-table-body') as HTMLElement | null;
      if (tableBody) {
        tableBody.scrollTop = tableBody.scrollHeight;
      }
    }, [fieldManager]);

    // 表格列配置
    const columns = TableColumns({
      fieldTypeOptions,
      FIELD_TYPES_NEED_CONFIG,
      configPopoverVisible: fieldManager.configPopoverVisible,
      constraintsPopoverVisible: fieldManager.constraintsPopoverVisible,
      setConfigPopoverVisible: fieldManager.setConfigPopoverVisible,
      setConstraintsPopoverVisible: fieldManager.setConstraintsPopoverVisible,
      renderFieldConfigContent,
      externalErrors: fieldManager.errors,
      getFieldIndex: fieldManager.getFieldIndex,
      deleteField: fieldManager.deleteField,
      fields: fieldManager.fields,
      handleConfigConfirm: fieldManager.handleConfigConfirm,
      clearFieldError: fieldManager.clearFieldError
    });

    return (
      <Modal
        className={styles.configFieldModal}
        title="字段配置"
        visible={visible}
        onOk={fieldManager.handleSubmit}
        onCancel={fieldManager.handleCancel}
        okText="保存"
        cancelText="取消"
        confirmLoading={fieldManager.submitting}
        style={{ width: 1260 }}
        // 关闭自动聚焦与焦点锁
        autoFocus={false}
        focusLock={false}
      >
        <Spin loading={fieldManager.loading} style={{ width: '100%' }}>
          <Form form={fieldManager.form} initialValues={{ fields: fieldManager.activeFields }} id="field-config-form">
            <Form.List field="fields">
              {() => {
                return (
                  <div className={styles.fieldConfigContainer} id="field-config-container">
                    <SortableTable
                      data={fieldManager.activeFields}
                      columns={columns}
                      onSort={({ oldIndex, newIndex }) => fieldManager.moveField(oldIndex, newIndex)}
                    />

                    <div className={styles.addFieldSection}>
                      <Button
                        type="dashed"
                        icon={<IconPlus />}
                        onClick={handleAddField}
                        className={styles.addFieldButton}
                      >
                        新增字段
                      </Button>
                    </div>
                  </div>
                );
              }}
            </Form.List>
          </Form>
        </Spin>
      </Modal>
    );
  }
);

ConfigFieldModal.displayName = 'ConfigFieldModal';
export default ConfigFieldModal;
