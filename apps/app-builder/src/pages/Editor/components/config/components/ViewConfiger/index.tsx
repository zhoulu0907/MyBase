import { Checkbox, Form, Space } from '@arco-design/web-react';
import { usePageViewEditorSignal } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect } from 'react';
import styles from './index.module.less';

const { useForm } = Form;

/**
 * 视图配置面板
 */
interface ViewConfigerProps {}

const ViewConfiger = ({}: ViewConfigerProps) => {
  useSignals();

  const { pageViews, curViewId, updatePageView } = usePageViewEditorSignal;

  useEffect(() => {
    if (curViewId.value) {
      const view = pageViews.value[curViewId.value];

      if (view) {
        form.setFieldsValue({
          editViewMode: view.editViewMode ? true : false,
          detailViewMode: view.detailViewMode ? true : false,
          isDefaultEditViewMode: view.isDefaultEditViewMode ? true : false,
          isDefaultDetailViewMode: view.isDefaultDetailViewMode ? true : false
        });
      }
    }
  }, [curViewId.value]);

  const [form] = useForm();

  const editViewMode = Form.useWatch('editViewMode', form);
  const detailViewMode = Form.useWatch('detailViewMode', form);

  useEffect(() => {
    !editViewMode && form.setFieldValue('isDefaultEditViewMode', false);
  }, [editViewMode]);

  useEffect(() => {
    !detailViewMode && form.setFieldValue('isDefaultDetailViewMode', false);
  }, [detailViewMode]);

  const handleOnValuesChange = (changeValue: any, values: any) => {
    let curPageView = pageViews.value[curViewId.value];
    if (curPageView && curPageView.id) {
      const newPageView = {
        ...curPageView,
        ...form.getFieldsValue()
      };

      updatePageView(newPageView);
    }
  };

  return (
    <div className={styles.configs}>
      <Form form={form} onValuesChange={handleOnValuesChange}>
        <div className={styles.title}>视图配置</div>
        <div className={styles.content}>
          <div className={styles.itemLabel}>视图模式</div>
          <div className={styles.checkboxWrapper}>
            <Form.Item field="editViewMode" triggerPropName="checked">
              <Checkbox></Checkbox>
            </Form.Item>

            <Space>
              <div className={styles.checkboxContent}>
                <div className={styles.checkboxContentTitle}>编辑模式</div>
                <div className={styles.checkboxContentDesc}>支持编辑和修改内容</div>
              </div>
            </Space>
          </div>

          <div className={styles.checkboxWrapper}>
            <Form.Item field="detailViewMode" triggerPropName="checked">
              <Checkbox></Checkbox>
            </Form.Item>

            <Space>
              <div className={styles.checkboxContent}>
                <div className={styles.checkboxContentTitle}>详情模式</div>
                <div className={styles.checkboxContentDesc}>以只读方式展示内容详情</div>
              </div>
            </Space>
          </div>

          <div className={styles.itemLabel}>是否默认视图</div>
          <div className={styles.checkboxWrapper}>
            <Form.Item field="isDefaultEditViewMode" triggerPropName="checked">
              <Checkbox disabled={!editViewMode}></Checkbox>
            </Form.Item>

            <Space>
              <div className={styles.checkboxContent}>
                <div className={styles.checkboxContentTitle}>默认编辑视图</div>
              </div>
            </Space>
          </div>

          <div className={styles.checkboxWrapper}>
            <Form.Item field="isDefaultDetailViewMode" triggerPropName="checked">
              <Checkbox disabled={!detailViewMode}></Checkbox>
            </Form.Item>

            <Space>
              <div className={styles.checkboxContent}>
                <div className={styles.checkboxContentTitle}>默认详情视图</div>
              </div>
            </Space>
          </div>
        </div>
      </Form>
    </div>
  );
};

export default ViewConfiger;
