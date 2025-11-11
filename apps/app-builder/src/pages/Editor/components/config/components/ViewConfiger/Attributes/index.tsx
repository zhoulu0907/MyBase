import circleSVG from '@/assets/images/circle.svg';
import defaultTemplateDetailSVG from '@/assets/images/default_template_detail.svg';
import defaultTemplateEditSVG from '@/assets/images/default_template_edit.svg';
import defaultTemplateThumbSVG from '@/assets/images/default_template_thumb.svg';
import tickBlackSVG from '@/assets/images/tick_black.svg';
import tickGreenSVG from '@/assets/images/tick_green.svg';
import { Button, Checkbox, Form, Image, Space, Tag } from '@arco-design/web-react';
import { usePageViewEditorSignal } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useState } from 'react';
import styles from './index.module.less';

const { useForm } = Form;

interface ViewAttributesProps {}

const ViewAttributes = ({}: ViewAttributesProps) => {
  useSignals();

  const [previewTemplateImageVisible, setPreviewTemplateImageVisible] = useState(false);
  const { pageViews, curViewId, updatePageView } = usePageViewEditorSignal;

  const [form] = useForm();

  const editViewMode = Form.useWatch('editViewMode', form);
  const detailViewMode = Form.useWatch('detailViewMode', form);
  const isDefaultEditViewMode = Form.useWatch('isDefaultEditViewMode', form);
  const isDefaultDetailViewMode = Form.useWatch('isDefaultDetailViewMode', form);

  // 取消勾选的话，设置为非默认
  useEffect(() => {
    editViewMode == 0 && form.setFieldValue('isDefaultEditViewMode', 0);
  }, [editViewMode]);

  useEffect(() => {
    detailViewMode == 0 && form.setFieldValue('isDefaultDetailViewMode', 0);
  }, [detailViewMode]);

  useEffect(() => {
    if (curViewId.value) {
      const view = pageViews.value[curViewId.value];

      if (view) {
        form.setFieldsValue({
          editViewMode: view.editViewMode ? 1 : 0,
          detailViewMode: view.detailViewMode ? 1 : 0,
          isDefaultEditViewMode: view.isDefaultEditViewMode ? 1 : 0,
          isDefaultDetailViewMode: view.isDefaultDetailViewMode ? 1 : 0
        });
      }
    }
  }, [curViewId.value]);

  const handleOnValuesChange = (changeValue: any, values: any) => {
    let curPageView = pageViews.value[curViewId.value];
    if (curPageView && curPageView.id) {
      const newPageView = {
        ...curPageView,
        editViewMode: form.getFieldValue('editViewMode') ? 1 : 0,
        detailViewMode: form.getFieldValue('detailViewMode') ? 1 : 0,
        isDefaultEditViewMode: form.getFieldValue('isDefaultEditViewMode') ? 1 : 0,
        isDefaultDetailViewMode: form.getFieldValue('isDefaultDetailViewMode') ? 1 : 0
      };

      updatePageView(newPageView);
    }
  };

  const handleSetDefaultEditViewMode = () => {
    form.setFieldValue('isDefaultEditViewMode', 1);
  };

  const handleSetDefaultDetailViewMode = () => {
    form.setFieldValue('isDefaultDetailViewMode', 1);
  };

  return (
    <div className={styles.attributes}>
      <Form form={form} onValuesChange={handleOnValuesChange}>
        <div className={styles.itemLabel}>视图模式</div>
        <div className={styles.viewModeWrapper}>
          <Form.Item field="editViewMode" triggerPropName="checked">
            <Checkbox
              disabled={
                Object.values(pageViews.value || {}).filter(
                  (pv: any) => pv.isDefaultEditViewMode === 1 && pv.id == curViewId.value
                ).length == 1 ||
                (editViewMode === 1 && detailViewMode === 0)
              }
            ></Checkbox>
          </Form.Item>

          <Space>
            <div className={styles.checkboxContent}>
              <div className={styles.checkboxContentTitle}>编辑模式</div>
              <div className={styles.checkboxContentDesc}>支持编辑和修改内容</div>
            </div>
          </Space>
        </div>

        <div className={styles.viewModeWrapper}>
          <Form.Item field="detailViewMode" triggerPropName="checked">
            <Checkbox
              disabled={
                Object.values(pageViews.value || {}).filter(
                  (pv: any) => pv.isDefaultDetailViewMode === 1 && pv.id == curViewId.value
                ).length == 1 ||
                (editViewMode === 0 && detailViewMode === 1)
              }
            ></Checkbox>
          </Form.Item>

          <Space>
            <div className={styles.checkboxContent}>
              <div className={styles.checkboxContentTitle}>详情模式</div>
              <div className={styles.checkboxContentDesc}>以只读方式展示内容详情</div>
            </div>
          </Space>
        </div>

        <div className={styles.itemLabel}>默认视图配置</div>

        {editViewMode == 1 && (
          <div className={styles.defaultWrapper}>
            <div className={styles.header}>
              <div className={styles.viewTitle}>默认编辑视图</div>
              {isDefaultEditViewMode ? (
                <Tag color="green">
                  <div className={styles.viewTag}>
                    <img src={tickGreenSVG} alt="基础设置" style={{ marginRight: '8px' }} />
                    <div>默认</div>
                  </div>
                </Tag>
              ) : (
                <Tag color="gray">
                  <div className={styles.viewTag}>
                    <img src={circleSVG} alt="基础设置" style={{ marginRight: '8px' }} />
                    <div>非默认</div>
                  </div>
                </Tag>
              )}
            </div>
            <div className={styles.description}>列表页点击「编辑」按钮时，默认跳转到该视图</div>
            {isDefaultEditViewMode ? (
              <Button size="small" long>
                <img src={tickBlackSVG} alt="基础设置" style={{ marginRight: '8px' }} />
                已设为默认编辑视图
              </Button>
            ) : (
              <Button size="small" type="primary" long onClick={handleSetDefaultEditViewMode}>
                设为默认编辑视图
              </Button>
            )}
          </div>
        )}

        {detailViewMode == 1 && (
          <div className={styles.defaultWrapper}>
            <div className={styles.header}>
              <div className={styles.viewTitle}>默认详情视图</div>
              {isDefaultDetailViewMode ? (
                <Tag color="green">
                  <div className={styles.viewTag}>
                    <img src={tickGreenSVG} alt="基础设置" style={{ marginRight: '8px' }} />
                    <div>默认</div>
                  </div>
                </Tag>
              ) : (
                <Tag color="gray">
                  <div className={styles.viewTag}>
                    <img src={circleSVG} alt="基础设置" style={{ marginRight: '8px' }} />
                    <div>非默认</div>
                  </div>
                </Tag>
              )}
            </div>
            <div className={styles.description}>列表页点击某个数据项时，默认跳转到该视图</div>

            {isDefaultDetailViewMode ? (
              <Button size="small" long>
                <img src={tickBlackSVG} alt="基础设置" style={{ marginRight: '8px' }} />
                已设为默认详情视图
              </Button>
            ) : (
              <Button size="small" type="primary" long onClick={handleSetDefaultDetailViewMode}>
                设为默认详情视图
              </Button>
            )}
          </div>
        )}

        <div className={styles.itemLabel}>视图布局</div>
        <div className={styles.templateWrapper}>
          <Image
            src={defaultTemplateThumbSVG}
            alt="默认模板"
            onClick={() => setPreviewTemplateImageVisible(true)}
            preview={false}
          />
          <Image.Preview
            src={editViewMode == 1 ? defaultTemplateEditSVG : defaultTemplateDetailSVG}
            visible={previewTemplateImageVisible}
            onVisibleChange={setPreviewTemplateImageVisible}
          />
        </div>

        <Form.Item field="isDefaultEditViewMode" triggerPropName="checked" hidden>
          <Checkbox disabled></Checkbox>
        </Form.Item>
        <Form.Item field="isDefaultDetailViewMode" triggerPropName="checked" hidden>
          <Checkbox disabled></Checkbox>
        </Form.Item>
      </Form>
    </div>
  );
};

export default ViewAttributes;
