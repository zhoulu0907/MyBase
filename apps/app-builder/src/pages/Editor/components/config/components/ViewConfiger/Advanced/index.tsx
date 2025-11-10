import { Form } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';
import { useState } from 'react';
import styles from './index.module.less';

const FormItem = Form.Item;

interface AdvancedProps {}

const Advanced = ({}: AdvancedProps) => {
  useSignals();

  const [count, setCount] = useState(0);

  return (
    <div className={styles.configAdvanced}>
      <div className={styles.header}>
        <div>交互规则</div>
        <div>{`已开启${count}条交互规则`}</div>
      </div>
    </div>
  );
};

export default Advanced;
