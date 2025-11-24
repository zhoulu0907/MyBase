import { Button, Form, Input, InputNumber, Menu, Message, Select, Switch } from '@arco-design/web-react';
import {
  batchUpdateSecurityConfigs,
  getSecurityConfigCategories,
  getSecurityConfigItems
} from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

const MenuItem = Menu.Item;

interface WorkspaceSecurityProps {}

const WorkspaceSecurity: React.FC<WorkspaceSecurityProps> = ({}) => {
  const [form] = Form.useForm();

  const [categories, setCategories] = useState<any[]>([]);
  const [itemsData, setItemsData] = useState<any[]>([]);

  const [activeMenuItem, setActiveMenuItem] = useState<string>('');
  const handleClickMenuItem = (id: string) => {
    setActiveMenuItem(id);
  };

  const handleUpdate = () => {
    handleUpdateSecurityConfigs();
  };

  useEffect(() => {
    handleGetSecurityConfigCategories();
  }, []);

  const handleGetSecurityConfigCategories = async () => {
    const res = await getSecurityConfigCategories();

    setCategories(res);
    if (res.length > 0) {
      setActiveMenuItem(res[0].id);
    }
  };

  const handleUpdateSecurityConfigs = async () => {
    // 将 form 的字段对象转成数组 [{ configKey, configValue }]
    const fieldsObject = form.getFieldsValue();
    const configsArray = Object.entries(fieldsObject).map(([configKey, configValue]) => ({
      configKey,
      configValue: Array.isArray(configValue) ? configValue.join(', ') : configValue.toString()
    }));

    try {
      const res = await batchUpdateSecurityConfigs({
        configs: configsArray
      });
      if (res) {
        Message.success('更新配置成功');
      }
    } catch (error) {
      console.error('更新配置失败', error);
    }
  };

  useEffect(() => {
    if (activeMenuItem) {
      handleGetSecurityConfigItems(activeMenuItem);
    }
  }, [activeMenuItem]);

  const handleGetSecurityConfigItems = async (id: string) => {
    const res = await getSecurityConfigItems(id);

    const tmpItemsData = (Array.isArray(res) ? res : []).reduce((acc: Record<string, any>, item: any) => {
      if (item.widgetType === 'MULTISELECT') {
        acc[item.configKey] = item.configValue
          ? String(item.configValue)
              .split(',')
              .map((s: string) => s.trim())
          : [];
      } else if (item.widgetType === 'SWITCH') {
        acc[item.configKey] = item.configValue === 'true';
      } else {
        if (item.configKey !== undefined) {
          acc[item.configKey] = item.configValue;
        }
      }

      return acc;
    }, {});

    form.setFieldsValue(tmpItemsData);

    setItemsData(res);
  };

  return (
    <div className={styles.workspaceSecurityPage}>
      <div className={styles.sider}>
        <Menu style={{ width: 200 }} mode="pop" onClickMenuItem={handleClickMenuItem}>
          {categories.map((category) => (
            <MenuItem key={category.id}>{category.categoryName}</MenuItem>
          ))}
        </Menu>
      </div>
      <div className={styles.content}>
        <div className={styles.contentHeader}>
          <div className={styles.contentTitle}>配置项</div>
          <Button type="primary" onClick={handleUpdate}>
            更新配置
          </Button>
        </div>
        <div className={styles.contentBody}>
          <div className={styles.contentBodyItem}>
            <Form form={form}>
              {activeMenuItem &&
                itemsData.map((item: any) => (
                  <div key={item.configKey} className={styles.contentBodyItemContent}>
                    {item.widgetType === 'NUMBER' && (
                      <Form.Item
                        field={item.configKey}
                        label={item.configName}
                        extra={item.description}
                        rules={[
                          {
                            required: item.required == 'true',
                            message: `请输入${item.description}`
                          },
                          {
                            type: 'number',
                            min: Number(item.minValue),
                            max: Number(item.maxValue)
                          }
                        ]}
                      >
                        <InputNumber />
                      </Form.Item>
                    )}
                    {item.widgetType === 'INPUT' && item.options == null && (
                      <Form.Item
                        field={item.configKey}
                        label={item.configName}
                        extra={item.description}
                        rules={[{ required: item.required === 'true', message: `请输入${item.description}` }]}
                      >
                        <Input />
                      </Form.Item>
                    )}
                    {item.widgetType === 'PASSWORD' && item.options == null && (
                      <Form.Item
                        field={item.configKey}
                        label={item.configName}
                        extra={item.description}
                        rules={[{ required: item.required === 'true', message: `请输入${item.description}` }]}
                      >
                        <Input.Password />
                      </Form.Item>
                    )}

                    {item.widgetType === 'SWITCH' && (
                      <Form.Item
                        field={item.configKey}
                        label={item.configName}
                        extra={item.description}
                        rules={[{ required: item.required === 'true', message: `请输入${item.description}` }]}
                        triggerPropName="checked"
                      >
                        <Switch />
                      </Form.Item>
                    )}
                    {item.widgetType === 'SELECT' && item.options != null && (
                      <Form.Item
                        field={item.configKey}
                        label={item.configName}
                        extra={item.description}
                        rules={[{ required: item.required === 'true', message: `请输入${item.description}` }]}
                      >
                        <Select
                          options={
                            typeof item.options === 'string'
                              ? Object.entries(JSON.parse(item.options)).map(([value, label]) => ({
                                  label: String(label),
                                  value
                                }))
                              : []
                          }
                        />
                      </Form.Item>
                    )}
                    {item.widgetType === 'SELECT' && item.options == null && (
                      <Form.Item
                        field={item.configKey}
                        label={item.configName}
                        extra={item.description}
                        rules={[{ required: item.required === 'true', message: `请输入${item.description}` }]}
                      >
                        <Select
                          options={Array.from(
                            { length: Math.abs(Number(item.maxValue) - Number(item.minValue)) + 1 },
                            (_, i) => ({
                              label: (Number(item.maxValue) > Number(item.minValue)
                                ? Number(item.minValue) + i
                                : Number(item.minValue) - i
                              ).toString(),
                              value: (Number(item.maxValue) > Number(item.minValue)
                                ? Number(item.minValue) + i
                                : Number(item.minValue) - i
                              ).toString()
                            })
                          )}
                        />
                      </Form.Item>
                    )}

                    {item.widgetType === 'MULTISELECT' && item.options != null && (
                      <Form.Item
                        field={item.configKey}
                        label={item.configName}
                        extra={item.description}
                        rules={[{ required: item.required === 'true', message: `请输入${item.description}` }]}
                      >
                        <Select
                          mode="multiple"
                          options={
                            typeof item.options === 'string'
                              ? Object.entries(JSON.parse(item.options)).map(([value, label]) => ({
                                  label: String(label),
                                  value
                                }))
                              : []
                          }
                        />
                      </Form.Item>
                    )}

                    {item.widgetType === 'MULTISELECT' && item.options == null && (
                      <Form.Item
                        field={item.configKey}
                        label={item.configName}
                        extra={item.description}
                        rules={[{ required: item.required === 'true', message: `请输入${item.description}` }]}
                      >
                        <Select
                          mode="multiple"
                          options={
                            // 生成从 item.maxValue 到 item.minValue 的所有整数选项
                            Array.from(
                              { length: Math.abs(Number(item.maxValue) - Number(item.minValue)) + 1 },
                              (_, i) => ({
                                label: (Number(item.maxValue) > Number(item.minValue)
                                  ? Number(item.minValue) + i
                                  : Number(item.minValue) - i
                                ).toString(),
                                value: (Number(item.maxValue) > Number(item.minValue)
                                  ? Number(item.minValue) + i
                                  : Number(item.minValue) - i
                                ).toString()
                              })
                            )
                          }
                        />
                      </Form.Item>
                    )}
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
