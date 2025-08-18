import iconEditSVG from '@/assets/images/app_edit_black.svg';
import { Form, Input, Modal, Select, TreeSelect, type FormInstance } from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';
import MenuModal from '../MenuModal';

interface CreateModalProps {
  title: string;
  handleCreate: () => void;
  onCancel: () => void;
  form: FormInstance;
  pageTypeOptions: { label: string; value: any }[];
  visibleCreateForm: string;
  initValue: { pageType: number; menuName: string; parentId: string };
  treeData: any[];
  entityListOptions: { label: string; value: any }[];
}

const CreateModal: React.FC<CreateModalProps> = ({
  title,
  handleCreate,
  onCancel,
  form,
  pageTypeOptions,
  visibleCreateForm,
  initValue,
  treeData,
  entityListOptions
}) => {
  const [visibleMenuIcon, setVisibleMenuIcon] = useState<boolean>(false);
  const [menuIcon, setMenuIcon] = useState<string>();

  useEffect(() => {
    if (menuIcon) {
      form.setFieldValue('menuIcon', menuIcon);
    } else {
      form.setFieldValue('menuIcon', visibleCreateForm === 'page' ? 'icon-13' : 'icon-folder');
    }
  }, [menuIcon, visibleCreateForm]);

  return (
    <Modal
      title={title}
      visible={visibleCreateForm !== ''}
      onOk={handleCreate}
      onCancel={onCancel}
      autoFocus={false}
      focusLock={true}
      unmountOnExit={true}
    >
      <Form
        layout="vertical"
        form={form}
        initialValues={{
          pageType: initValue.pageType,
          menuName: initValue.menuName,
          parentId: initValue.parentId
        }}
      >
        <Form.Item
          label="页面类型"
          field="pageType"
          hidden={visibleCreateForm === 'group'}
          rules={[{ required: true, message: '请选择页面类型' }]}
        >
          <Select options={pageTypeOptions} placeholder="请选择页面类型" allowClear />
        </Form.Item>

        <Form.Item
          label={visibleCreateForm === 'page' ? '页面名称' : '分组名称'}
          field="menuName"
          rules={[
            { required: true, message: '请输入页面名称' },
            { maxLength: 20, message: '页面名称不能超过20个字符' }
          ]}
        >
          <Input
            maxLength={20}
            placeholder="请输入页面名称，不超过20个字符"
            allowClear
            onChange={(value) => {
              form.setFieldValue('menuName', value);
            }}
          />
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
              {menuIcon ? (
                <i className={`iconfont ${menuIcon}`} style={{ fontSize: 16 }} />
              ) : (
                <i
                  className={`iconfont ${visibleCreateForm === 'page' ? 'icon-13' : 'icon-folder'}`}
                  style={{ fontSize: 16 }}
                />
              )}
            </div>
            <img
              src={iconEditSVG}
              alt="选择菜单图标"
              style={{ cursor: 'pointer' }}
              onClick={() => setVisibleMenuIcon(true)}
            />
          </div>
        </Form.Item>
        <Form.Item label="父级页面" field="parentId">
          <TreeSelect treeData={treeData} placeholder="请选择父级页面" allowClear />
        </Form.Item>

        {visibleCreateForm === 'page' && (
          <Form.Item label="业务实体" field="entityId" rules={[{ required: true, message: '请选择业务实体' }]}>
            <Select options={entityListOptions} placeholder="请选择业务实体" allowClear />
          </Form.Item>
        )}
      </Form>
      <MenuModal
        visible={visibleMenuIcon}
        handleSelect={(val) => setMenuIcon(val)}
        onCancel={() => setVisibleMenuIcon(false)}
      />
    </Modal>
  );
};

export default CreateModal;
