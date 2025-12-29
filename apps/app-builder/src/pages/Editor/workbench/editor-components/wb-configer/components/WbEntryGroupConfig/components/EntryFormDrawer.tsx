import { Button, Divider, Form, Input, Select } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { useCallback } from 'react';
import ConfigDrawer from '@/pages/Editor/workbench/components/configDrawer';
import MenuSelector from '@/pages/Editor/workbench/components/MenuSelector';
import type { EntryItem, SchemaGroup } from '../types';
import styles from '../index.module.less';

const FormItem = Form.Item;

interface EntryFormDrawerProps {
  visible: boolean;
  form: ReturnType<typeof Form.useForm<EntryItem>>[0];
  entryType: string;
  enableGroup: boolean;
  schemaGroups: SchemaGroup[];
  newGroupName: string;
  currentIconSrc: string;
  onClose: () => void;
  onValuesChange: (changedValues: Partial<EntryItem>) => void;
  onEditEntry: (entryId: string, field: string, value: string) => void;
  onAddGroup: () => void;
  onNewGroupNameChange: (value: string) => void;
  onChooseIcon: () => void;
}

export const EntryFormDrawer = ({
  visible,
  form,
  entryType,
  enableGroup,
  schemaGroups,
  newGroupName,
  currentIconSrc,
  onClose,
  onValuesChange,
  onEditEntry,
  onAddGroup,
  onNewGroupNameChange,
  onChooseIcon
}: EntryFormDrawerProps) => {
  const handleEditEntry = useCallback(
    (entryId: string, field: string, value: string) => {
      onEditEntry(entryId, field, value);
    },
    [onEditEntry]
  );

  return (
    <ConfigDrawer visible={visible} title="编辑入口" onClose={onClose}>
      <div className={styles.drawerContent}>
        <Form
          layout="vertical"
          className={styles.drawerForm}
          labelAlign="left"
          form={form}
          onValuesChange={onValuesChange}
        >
          <FormItem
            label="入口类型"
            field="entryType"
            layout="horizontal"
            labelCol={{ span: 8 }}
            wrapperCol={{ span: 6, offset: 10 }}
          >
            <div>{entryType === 'menu' ? '应用菜单' : '外部链接'}</div>
          </FormItem>
          <FormItem label="入口图标" field="entryIcon">
            <div className={styles.iconUploader}>
              <div className={styles.iconUploaderAvatar} onClick={onChooseIcon}>
                <img src={currentIconSrc} alt="entry icon" />
              </div>
            </div>
          </FormItem>
          <FormItem label="入口名称" field="entryName">
            <Input placeholder="请输入" />
          </FormItem>
          <FormItem label="辅助描述" field="entryDesc">
            <Input.TextArea placeholder="请输入" autoSize={{ minRows: 2, maxRows: 3 }} />
          </FormItem>
          {enableGroup && (
            <FormItem label="分组" field="group">
              <Select
                placeholder="请选择"
                dropdownRender={(menu) => (
                  <div>
                    {menu}
                    <Divider style={{ margin: 0 }} />
                    <div
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        padding: '10px 12px'
                      }}
                    >
                      <Input
                        size="small"
                        style={{ marginRight: 18 }}
                        value={newGroupName}
                        onChange={onNewGroupNameChange}
                      />
                      <Button style={{ fontSize: 14, padding: '0 6px' }} type="text" size="mini" onClick={onAddGroup}>
                        <IconPlus />
                        添加分组
                      </Button>
                    </div>
                  </div>
                )}
              >
                {schemaGroups.map((group) => (
                  <Select.Option key={group.groupName} value={group.groupName}>
                    {group.groupName}
                  </Select.Option>
                ))}
              </Select>
            </FormItem>
          )}
          <FormItem shouldUpdate>
            {(values) => {
              return values.entryType === 'menu' ? (
                <FormItem label="选择菜单">
                  <MenuSelector
                    value={values.menuUuid}
                    mode="single"
                    onChange={(value) =>
                      handleEditEntry(values.entryId, 'menuUuid', Array.isArray(value) ? value[0] : value)
                    }
                  />
                </FormItem>
              ) : (
                <FormItem label="链接地址" field="linkAddress">
                  <Input />
                </FormItem>
              );
            }}
          </FormItem>
        </Form>
      </div>
    </ConfigDrawer>
  );
};
