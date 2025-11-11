import { Button, Form } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';
import { useState } from 'react';
import InteractionRuleModal from '../InteractionRuleModal';
import styles from './index.module.less';

const FormItem = Form.Item;

interface AdvancedProps {}

const ViewAdvanced = ({}: AdvancedProps) => {
  useSignals();

  const [count, setCount] = useState(0);

  const [interactionRuleModalVisible, setInteractionRuleModalVisible] = useState(false);

  const handleOpenInteractionRuleModal = () => {
    setInteractionRuleModalVisible(true);
  };

  const handleCancel = () => {
    setInteractionRuleModalVisible(false);
  };
  const handleOk = () => {
    setInteractionRuleModalVisible(false);
  };

  return (
    <div className={styles.configAdvanced}>
      <div className={styles.header}>
        <div>交互规则</div>
        <div>{`已开启${count}条交互规则`}</div>
      </div>
      <div className={styles.desc}>可根据条件控制字段的显隐、是否可编辑和是否必填，并支持为字段赋值</div>

      <Button type="primary" style={{ width: '100%' }} onClick={handleOpenInteractionRuleModal}>
        配置交互规则
      </Button>

      <InteractionRuleModal visible={interactionRuleModalVisible} onCancel={handleCancel} onOk={handleOk} />
    </div>
  );
};

export default ViewAdvanced;
