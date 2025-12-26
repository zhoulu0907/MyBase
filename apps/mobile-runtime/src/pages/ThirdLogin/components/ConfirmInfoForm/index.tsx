import { Tabs, Form, Input, Button, Toast, Popup } from '@arco-design/mobile-react';
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
    <Popup visible={visible} close={onCancel} direction="bottom" maskClosable={false}>
      <div style={{ height: 330, width: 290 }}>
        <div className="popup-demo-title">Title</div>
        <div className="popup-demo-content">Content area</div>
      </div>
    </Popup>
  );
};

export default ConfirmInfoForm;
