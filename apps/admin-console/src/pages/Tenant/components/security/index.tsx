import { Button, Form, InputNumber, Menu } from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

const MenuItem = Menu.Item;

interface WorkspaceSecurityProps {}

const mockCategories = [
  {
    id: '1',
    categoryName: '密码强度',
    items: [
      {
        id: '1-1',
        configKey: 'minLength',
        configName: '最小密码长度',
        dataType: 'INTEGER',
        configValue: 8,
        description: '最小密码长度为',
        sortOrder: 1
      },
      {
        id: '1-2',
        configKey: 'maxLength',
        configName: '最大密码长度',
        dataType: 'INTEGER',
        configValue: 16,
        description: '最大密码长度为',
        sortOrder: 2
      }
    ]
  },
  {
    id: '2',
    categoryName: '类别2'
  },
  {
    id: '3',
    categoryName: '类别3'
  }
];

const WorkspaceSecurity: React.FC<WorkspaceSecurityProps> = ({}) => {
  const [form] = Form.useForm();

  const [activeMenuItem, setActiveMenuItem] = useState<string>('');
  const handleClickMenuItem = (key: string) => {
    setActiveMenuItem(key);
  };

  //   TODO(mickey): 联调接口，获取配置项目和配置项
  const handleSave = () => {
    console.log('保存');
  };

  useEffect(() => {
    const res = mockCategories
      .find((category) => category.id === activeMenuItem)
      ?.items?.reduce(
        (acc, item) => {
          acc[item.configKey] = item.configValue;
          return acc;
        },
        {} as Record<string, any>
      );
    console.log('res', res);
    form.setFieldsValue(res);
  }, [activeMenuItem]);

  return (
    <div className={styles.workspaceSecurityPage}>
      <div className={styles.sider}>
        <Menu style={{ width: 200 }} mode="pop" onClickMenuItem={handleClickMenuItem}>
          {mockCategories.map((category) => (
            <MenuItem key={category.id}>{category.categoryName}</MenuItem>
          ))}
        </Menu>
      </div>
      <div className={styles.content}>
        <div className={styles.contentHeader}>
          <div className={styles.contentTitle}>
            {mockCategories.find((category) => category.id === activeMenuItem)?.categoryName}
          </div>
          <Button type="primary" onClick={handleSave}>
            更新配置
          </Button>
        </div>
        <div className={styles.contentBody}>
          <div className={styles.contentBodyItem}>
            <Form form={form}>
              {activeMenuItem &&
                mockCategories
                  .find((category) => category.id === activeMenuItem)
                  ?.items?.map((item) => (
                    <div key={item.configKey} className={styles.contentBodyItemContent}>
                      <Form.Item field={item.configKey} label={item.configName} extra={item.description}>
                        <InputNumber />
                      </Form.Item>
                    </div>
                  ))}
            </Form>
          </div>
        </div>
        <div className={styles.contentFooter}></div>
      </div>
    </div>
  );
};

export default WorkspaceSecurity;
