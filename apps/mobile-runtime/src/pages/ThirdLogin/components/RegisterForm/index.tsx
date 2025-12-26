import { Tabs, Form, Input, Button, Toast, Popup } from '@arco-design/mobile-react';
import styles from '../../index.module.less';

interface IRegisterProps {
  visible: boolean;
  appId: string;
  tenantId: string;
  mobile: string;
  onOk: () => void;
  onCancel: () => void;
}

const RegisterForm: React.FC<IRegisterProps> = ({ visible, appId, tenantId, mobile, onOk, onCancel }) => {
  return (
    <Popup visible={visible} close={onCancel} direction="bottom" maskClosable={false}>
      <div style={{ height: 330, width: 290 }}>
        <div className="popup-demo-title">Title</div>
        <div className="popup-demo-content">Content area</div>
      </div>
    </Popup>
  );
};

export default RegisterForm;
