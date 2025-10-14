import React, { useEffect, useState } from 'react';
import { Form, Input, Modal, Button, type FormInstance } from '@arco-design/web-react';
import MenuComp from '@/components/MenuIcon';
import iconEditSVG from '@/assets/images/app_edit_black.svg';
import styles from './index.module.less';
import DynamicIcon from '@/components/DynamicIcon';
import { menuIconList } from '@/components/MenuIcon/const';

/**
 * RenameModal 组件
 * 用于页面管理器中重命名弹窗的占位组件
 * 实际弹窗逻辑在 PageManagerPage 中实现
 */
interface RenameModalProps {
  title: string;
  visible: boolean;
  setVisible: (visible: boolean) => void;
  handleRename: () => void;
  form: FormInstance;
}

const RenameModal: React.FC<RenameModalProps> = ({ title, visible, handleRename, setVisible, form }) => {
  const [menuIcon, setMenuIcon] = useState<string>();
  const [visibleMenuIcon, setVisibleMenuIcon] = useState<boolean>(false);

  useEffect(() => {
    menuIcon && form.setFieldValue('menuIcon', menuIcon);
  }, [menuIcon]);

  const handleCloseModal = () => {
    setMenuIcon('');
    setVisible(false);
  };

  return (
    <Modal
      className={styles.renameModal}
      title={title}
      visible={visible}
      onOk={handleRename}
      closable={!visibleMenuIcon}
      onCancel={handleCloseModal}
      autoFocus={false}
      focusLock={true}
      unmountOnExit={true}
      footer={
        <div style={{ textAlign: 'right', visibility: !visibleMenuIcon ? 'visible' : 'hidden' }}>
          <Button type="default" onClick={handleCloseModal} style={{ marginRight: 12 }}>
            取消
          </Button>
          <Button type="primary" onClick={handleRename}>
            更新
          </Button>
        </div>
      }
    >
      <div className={styles.renameContainer}>
        <Form
          className={styles.renameForm}
          layout="vertical"
          form={form}
          initialValues={{
            menuID: form.getFieldValue('menuId'),
            menuName: form.getFieldValue('menuName'),
            menuIcon: form.getFieldValue('menuIcon')
          }}
        >
          <Form.Item label="页面名称" field="menuName" rules={[{ required: true, message: '请输入页面名称' }]}>
            <Input placeholder="请输入页面名称" allowClear />
          </Form.Item>

          <Form.Item label={'菜单图标'} field="menuIcon" rules={[{ required: true, message: '请选择菜单图标' }]}>
            <div style={{ display: 'flex', alignItems: 'flex-end' }}>
              <div
                style={{
                  width: 32,
                  height: 32,
                  marginRight: 4,
                  borderRadius: 2,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  backgroundColor: '#F2F3F5'
                }}
              >
                <DynamicIcon
                  IconComponent={menuIconList.find(icon => icon.code === (menuIcon || form.getFieldValue('menuIcon')))?.icon}
                  theme="outline"
                  size="18"
                  fill="#333"
                />
              </div>
              <img
                src={iconEditSVG}
                alt="选择菜单图标"
                style={{ cursor: 'pointer' }}
                onClick={() => setVisibleMenuIcon(true)}
              />
            </div>
          </Form.Item>
        </Form>
        <MenuComp
          style={{ transform: visibleMenuIcon ? 'translateX(0)' : '' }}
          onSelected={setMenuIcon}
          handleBack={() => setVisibleMenuIcon(false)}
        />
      </div>
    </Modal>
  );
};

export default RenameModal;
