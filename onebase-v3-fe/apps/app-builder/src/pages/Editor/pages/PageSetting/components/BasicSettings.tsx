import { Form, Radio } from '@arco-design/web-react';
import React from 'react';
import { useSignals } from '@preact/signals-react/runtime';
import { usePageSettingSignal } from '@onebase/ui-kit';
import styles from './components.module.less';
import TagInput from './TagInput';

const BasicSettings: React.FC = () => {
  useSignals();

  const { dataTitleType, redirectType, dataTitle, setDataTitleType, setRedirectType, setDataTitle } = usePageSettingSignal;

  return (
    <div className={styles.settingsPage}>
      <div className={styles.section}>
        <h2 className={styles.sectionTitle}>常用设置</h2>
        <Form layout="vertical" className={styles.form}>
          <Form.Item label="数据标题" required>
            <Radio.Group value={dataTitleType.value} onChange={(value) => setDataTitleType(value)}>
              <Radio value={1}>默认标题</Radio>
              <Radio value={2}>自定义标题</Radio>
            </Radio.Group>
            {dataTitleType.value === 1 ? (
              <div className={styles.defaultTitleInfo}>
                <p>
                  <span className={styles.title}>规则：</span>
                  <span className={styles.label}>发起人</span>
                  <span>发起的</span>
                  <span className={styles.label}>页面名称</span>
                </p>
                <p>
                  <span className={styles.title}>示例：</span>
                  <span>小贝发起的工时填报</span>
                </p>
              </div>
            ) : (
              <div className={styles.customTitle}>
                <TagInput value={dataTitle.value} onChange={setDataTitle} />
              </div>
            )}
          </Form.Item>

          <Form.Item label="表单提交后跳转页面">
            <Radio.Group value={redirectType.value} onChange={(value) => setRedirectType(value)} direction="vertical">
              <Radio value={1}>列表页</Radio>
              <Radio value={2}>展示【是否创建下一条】窗口</Radio>
              <Radio value={3}>打开刚才创建的记录</Radio>
            </Radio.Group>
          </Form.Item>
        </Form>
      </div>
    </div>
  );
};

export default BasicSettings;
