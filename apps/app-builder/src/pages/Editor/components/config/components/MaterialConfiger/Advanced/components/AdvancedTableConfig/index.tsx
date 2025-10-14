import { Button, Form, Modal, Select, Switch } from '@arco-design/web-react';
import { IconEdit } from '@douyinfe/semi-icons';
import type { PageView } from '@onebase/app';
import { RedirectMethod, usePageViewEditorSignal } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useState } from 'react';
import styles from '../../index.module.less';

export interface AdvancedTableConfigProps {
  handleMultiPropsChange: (updates: { key: string; value: string | number | boolean | any[] }[]) => void;
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

const openTypeOptions = [
  { label: '侧边栏', value: RedirectMethod.DRAWER },
  { label: '新页签', value: RedirectMethod.NEW_TAB }
];

const redirectPageId = 'redirectPageId';
const redirectMethod = 'redirectMethod';

const AdvancedTableConfig: React.FC<AdvancedTableConfigProps> = ({
  handleMultiPropsChange,
  handlePropsChange,
  item,
  configs,
  id
}) => {
  useSignals();

  const { pageViews } = usePageViewEditorSignal;
  useEffect(() => {
    console.log(configs);
    console.log(item);
    console.log(pageViews.value);
  }, []);

  const [modalVisible, setModalVisible] = useState(false);

  const [form] = Form.useForm();

  const handleOpenModal = () => {
    setModalVisible(true);
    form.setFieldsValue({
      redirectPageId: configs[redirectPageId],
      redirectMethod: configs[redirectMethod]
    });
  };

  const handleCloseModal = () => {
    setModalVisible(false);
  };

  const handleOnModal = () => {
    form.validate().then((values) => {
      try {
        console.log(values);
        handleMultiPropsChange([
          { key: redirectPageId, value: values.redirectPageId },
          { key: redirectMethod, value: values.redirectMethod }
        ]);
      } catch (e: any) {
        console.error(e.errors);
      } finally {
        handleCloseModal();
      }
    });
  };

  return (
    <>
      <Form.Item
        label={
          <div
            style={{
              textAlign: 'left'
            }}
          >
            <span>{item.name}</span>
          </div>
        }
        labelCol={{
          span: 20
        }}
        wrapperCol={{
          span: 2
        }}
        layout="horizontal"
      >
        <Switch
          size="small"
          checked={configs[item.key]}
          onChange={(value) => {
            console.log(value);
            handlePropsChange(item.key, value);

            if (!value) {
              handleMultiPropsChange([
                { key: item.key, value: value },
                { key: redirectPageId, value: '' },
                { key: redirectMethod, value: '' }
              ]);
            }
          }}
        />
      </Form.Item>

      <Form.Item
        labelCol={{
          span: 10
        }}
        wrapperCol={{
          span: 14
        }}
        layout="horizontal"
        labelAlign="left"
        label={'跳转至'}
        hidden={!configs[item.key]}
      >
        <div style={{ width: '100%', textAlign: 'right' }}>
          <Button type="secondary" onClick={handleOpenModal}>
            {configs[redirectPageId] ? pageViews.value[configs[redirectPageId]]?.pageName : '请选择视图'}
            <IconEdit style={{ marginLeft: '8px' }} />
          </Button>
        </div>
      </Form.Item>

      <Modal title="行点击跳转" visible={modalVisible} onCancel={handleCloseModal} onOk={handleOnModal}>
        <Form layout="inline" form={form} className={styles.rowNavModal}>
          <Form.Item
            layout="vertical"
            label="跳转页面"
            field={redirectPageId}
            style={{ flex: 1 }}
            rules={[{ required: true, message: '请选择跳转页面' }]}
          >
            <Select
              // TODO(mickey): id保存完之后需要替换
              options={(Object.values(pageViews.value) as PageView[])
                .filter((item: PageView) => item.detailViewMode)
                .map((item: PageView) => ({
                  label: item.pageName,
                  value: item.id
                }))}
            ></Select>
          </Form.Item>

          <Form.Item
            layout="vertical"
            label="打开方式"
            field={redirectMethod}
            style={{ flex: 1 }}
            rules={[{ required: true, message: '请选择打开方式' }]}
          >
            <Select options={openTypeOptions}></Select>
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
};

export default AdvancedTableConfig;
