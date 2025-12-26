import { Tabs, Form, Input, Button, Toast, Popup } from '@arco-design/mobile-react';
import { IconArrowBack } from '@arco-design/mobile-react/esm/icon';

import styles from '../../index.module.less';

interface IConfirmInfoProps {
  visible: boolean;
  tenantId: string;
  appId: string;
  onOk: () => void;
  onCancel: () => void;
}

const ConfirmInfoForm: React.FC<IConfirmInfoProps> = ({ visible, appId, tenantId, onOk, onCancel }) => {
  return (
    <Popup visible={visible} close={onCancel} direction="bottom" maskClosable={false} className={styles.popup}>
      <div className={styles.popupContent}>
        <div className={styles.popupBack}>
          <IconArrowBack />
        </div>
        <div className={styles.popupMain}>
          <IconArrowBack />
        </div>
      </div>
    </Popup>
  );
};

export default ConfirmInfoForm;
