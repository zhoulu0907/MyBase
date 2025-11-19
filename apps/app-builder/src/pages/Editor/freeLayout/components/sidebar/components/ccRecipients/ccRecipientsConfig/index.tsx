import { Radio, Divider, Tooltip } from '@arco-design/web-react';
import { IconQuestionCircle } from '@arco-design/web-react/icon';
import styles from './index.module.less';
import { useEffect, useState } from 'react';
import SimpleMode from './SimpleMode';
const RadioGroup = Radio.Group;

export default function CcRecipientsConfig({ setCcRecipientsConfigData, copyReceiverConfig }) {
  const [configMode, setConfigMode] = useState<string>('simple');


  return (
    <div className={styles.approverConfig}>
      <div className={styles.configTitle}>抄送人设置</div>
      <div className={styles.configMode}>
        <span>配置模式：</span>
        <RadioGroup value={configMode} onChange={setConfigMode}>
          <Radio value="simple">简易模式</Radio>
          <Radio value="condition">
            条件模式
            <IconQuestionCircle />
          </Radio>
        </RadioGroup>
      </div>
      <Divider />
      <SimpleMode setCcRecipientsConfigData={setCcRecipientsConfigData} copyReceiverConfig={copyReceiverConfig} />
    </div>
  );
}
