import MenuComp from '@/components/MenuIcon';
import { Button, Form, Input, Modal, Select, TreeSelect, type FormInstance } from '@arco-design/web-react';
import { PageType, RootParentPage } from '@onebase/app';
import { webMenuIcons } from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

interface CreateModalProps {
  title: string;
  handleCreate: () => void;
  onCancel: () => void;
  form: FormInstance;
  pageSetTypeOptions: { label: string; value: any }[];
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
  pageSetTypeOptions,
  visibleCreateForm,
  initValue,
  treeData,
  entityListOptions
}) => {
  const allWebMenuIcons = webMenuIcons.map((ele) => ele.children).reduce((acc, current) => acc.concat(current), []);

  const [menuIcon, setMenuIcon] = useState<string>();
  const [visibleMenuIcon, setVisibleMenuIcon] = useState<boolean>(false);

  useEffect(() => {
    if (menuIcon) {
      form.setFieldValue('menuIcon', menuIcon);
    } else {
      form.setFieldValue('menuIcon', 'FormPage');
    }
  }, [menuIcon, visibleCreateForm]);

  const nameMap = {
    page: '页面',
    group: '分组',
    workbench: '页面'
  };

  const iconMap = {
    page: 'page',
    group: 'seo-folder',
    workbench: 'page'
  };

  const handleCloseModal = () => {
    setMenuIcon('');
    onCancel();
  };

  const getPageSetTypeOptions = () => {
    const wb = PageType.WORKBENCH;
    if (visibleCreateForm !== 'workbench') {
      return pageSetTypeOptions.filter((opt) => opt.value !== wb);
    } else {
      form.setFieldValue('pageSetType', wb);
      return pageSetTypeOptions.filter((opt) => opt.value === wb);
    }
  };

  return (
    <Modal
      title={title}
      visible={visibleCreateForm !== ''}
      onOk={handleCreate}
      onCancel={handleCloseModal}
      closable={!visibleMenuIcon}
      autoFocus={false}
      focusLock={true}
      unmountOnExit={true}
      className={styles.createModal}
      footer={
        <div style={{ textAlign: 'right', visibility: !visibleMenuIcon ? 'visible' : 'hidden' }}>
          <Button type="default" onClick={handleCloseModal} style={{ marginRight: 12 }}>
            取消
          </Button>
          <Button type="primary" onClick={handleCreate}>
            创建
          </Button>
        </div>
      }
    >
      <div className={styles.createContainer}>
        <Form
          className={styles.createForm}
          layout="vertical"
          form={form}
          initialValues={{
            pageType: initValue.pageType,
            menuName: initValue.menuName,
            parentId: form.getFieldValue('parentId') || RootParentPage.id
          }}
          style={{
            transform: visibleMenuIcon ? 'translateX(-100%)' : ''
          }}
        >
          {visibleCreateForm !== 'group' && (
            <Form.Item
              label="页面类型"
              field="pageSetType"
              rules={[{ required: true, message: '请选择页面类型' }]}
              disabled={visibleCreateForm === 'workbench'}
            >
              <Select options={getPageSetTypeOptions()} placeholder="请选择页面类型" allowClear />
            </Form.Item>
          )}

          <Form.Item
            label={nameMap[visibleCreateForm as keyof typeof nameMap]}
            field="menuName"
            rules={[
              { required: true, message: `请输入${nameMap[visibleCreateForm as keyof typeof nameMap]}名称` },
              { maxLength: 20, message: '页面名称不能超过20个字符' }
            ]}
          >
            <Input
              maxLength={20}
              placeholder={`请输入${nameMap[visibleCreateForm as keyof typeof nameMap]}名称，不超过20个字符`}
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
                  backgroundColor: '#F2F3F5',
                  cursor: 'pointer'
                }}
                onClick={() => setVisibleMenuIcon(true)}
              >
                {menuIcon ? (
                  <img
                    style={{ width: 'auto', height: '18px', fill: '#333' }}
                    src={allWebMenuIcons.find((ele) => ele.code === menuIcon)?.icon}
                    alt=""
                  />
                ) : (
                  <img
                    style={{ width: 'auto', height: '18px', fill: '#333' }}
                    src={allWebMenuIcons.find((ele) => ele.code === 'FormPage')?.icon}
                    alt=""
                  />
                )}
              </div>
            </div>
          </Form.Item>
          <Form.Item label="父级页面" field="parentId">
            <TreeSelect treeData={treeData} placeholder="请选择父级页面" allowClear />
          </Form.Item>
          {visibleCreateForm === 'page' && (
            <Form.Item label="数据资产" field="entityUuid" rules={[{ required: true, message: '请选择数据资产' }]}>
              <Select options={entityListOptions} placeholder="请选择数据资产" allowClear />
            </Form.Item>
          )}
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

export default CreateModal;
