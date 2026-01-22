import { Form, Radio } from '@arco-design/web-react';
import React, { useState } from 'react';
import styles from './components.module.less';
import TagInput from './TagInput';

const BasicSettings: React.FC = () => {
  const [titleType, setTitleType] = useState('default');
  const [textValue, setTextValue] = useState('');

  const handleTextChange = (value: string) => {
    setTextValue(value);
  };

  return (
    <div className={styles.settingsPage}>
      <div className={styles.section}>
        <h2 className={styles.sectionTitle}>常用设置</h2>
        <Form layout="vertical" className={styles.form}>
          <Form.Item label="数据标题" required>
            <Radio.Group defaultValue="default" onChange={(value) => setTitleType(value)}>
              <Radio value="default">默认标题</Radio>
              <Radio value="custom">自定义标题</Radio>
            </Radio.Group>
            {titleType === 'default' ? (
              <>
                <div className={styles.rule}>
                  <span className={styles.ruleTitle}>规则：</span>
                  <span className={styles.ruleItem}>发起人</span>
                  <span className={styles.ruleItem}>发起的</span>
                  <span className={styles.ruleItem}>页面名称</span>
                </div>
                <div className={styles.example}>
                  <span className={styles.exampleTitle}>示例：</span>
                  <span className={styles.exampleContent}>小贝发起的111工时填报</span>
                </div>
              </>
            ) : (
              <div className={styles.customTitle}>
                <TagInput
                  value={textValue}
                  onChange={handleTextChange}
                  placeholder="请输入文字或添加字段，至少添加一个字段"
                />
              </div>
            )}
          </Form.Item>

          <Form.Item label="表单提交后跳转页面">
            <Radio.Group defaultValue="list">
              <Radio value="list">列表页</Radio>
              <Radio value="create">展示【是否创建下一条】窗口</Radio>
              <Radio value="record">打开刚才创建的记录</Radio>
            </Radio.Group>
          </Form.Item>
        </Form>
      </div>
    </div>
  );
};

export default BasicSettings;
