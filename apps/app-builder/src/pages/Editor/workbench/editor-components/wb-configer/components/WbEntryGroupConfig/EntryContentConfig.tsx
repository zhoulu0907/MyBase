import { Button, Divider, Dropdown, Form, Input, Menu, Message, Popconfirm, Select } from '@arco-design/web-react';
import { IconDelete, IconEdit, IconMenu, IconPlus, IconDragDotVertical } from '@arco-design/web-react/icon';
import { type ApplicationMenu } from '@onebase/app';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import type { QuickEntryGroupConfig, QuickEntryGroupItemConfig } from '@onebase/ui-kit';
import IconEntry from '@/assets/workbench/quick-entry/entry1.svg';
import ConfigDrawer from '@/pages/Editor/workbench/components/configDrawer';
import SelectMenuModal from './selectMenuModal';
import { getNextIndex } from '@/pages/Editor/workbench/utils/edit-data';
import styles from './EntryContentConfig.module.less';

export interface EntryContentConfigProps {
  value?: QuickEntryGroupConfig;
  onChange?: (value: QuickEntryGroupConfig) => void;
}

interface EntryItem {
  entryName: string;
  entryIcon?: string;
  entryType?: 'menu' | 'link';
  menuId?: string;
  linkAddress?: string;
  group?: string;
  entryId: string;
  id?: string;
  entryDesc?: string;
  [key: string]: unknown;
}

interface SchemaGroup {
  groupName: string;
  entries?: EntryItem[];
}

const FormItem = Form.Item;
const DEFAULT_GROUP_NAME = '默认分组';

const generateEntryId = () => `entry-${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 8)}`;

const EntryContentConfig = ({ onChange, value }: EntryContentConfigProps) => {
  const [state, setState] = useState({
    entries: [] as EntryItem[],
    showAddMenu: false,
    drawerVisible: false,
    selectMenuModalVisible: false,
    entryType: '',
    newGroupName: '',
    currentEntry: undefined as EntryItem | undefined,
    enableGroup: false,
    pendingEntry: null as EntryItem | null,
    formInitialized: false
  });

  const [form] = Form.useForm<EntryItem>();

  const normalizedValue = useMemo(() => value || { enableGroup: false, groups: [] }, [value]);
  const schemaGroups = useMemo(() => normalizedValue.groups || [], [normalizedValue]);

  // 转换函数
  const entriesToGroups = useCallback((entries: EntryItem[]): QuickEntryGroupItemConfig[] => {
    const groupsMap = new Map<string, EntryItem[]>();
    entries.forEach((entry) => {
      const groupName = entry.group || DEFAULT_GROUP_NAME;
      if (!groupsMap.has(groupName)) {
        groupsMap.set(groupName, []);
      }
      groupsMap.get(groupName)!.push(entry);
    });

    return Array.from(groupsMap.entries()).map(([groupName, entryList]) => ({
      groupName,
      entries: entryList.map((entry) => {
        const { group, id, ...rest } = entry;
        return rest;
      })
    }));
  }, []);

  // 扁平化 schemaGroups 为 entries
  const flattenSchemaGroups = useCallback((groups: SchemaGroup[]): EntryItem[] => {
    return groups.flatMap((group) => {
      const groupName = group?.groupName || DEFAULT_GROUP_NAME;
      return (group?.entries || []).map((entry) => {
        const entryId = entry?.entryId || entry?.id || generateEntryId();
        return {
          ...entry,
          group: groupName,
          entryId,
          id: entryId
        };
      });
    });
  }, []);

  // 同步entries
  useEffect(() => {
    if (!value) return;

    const initialEntries = flattenSchemaGroups(schemaGroups);
    const initialEnableGroup = Boolean(value.enableGroup);

    setState((prev) => ({
      ...prev,
      entries: initialEntries,
      enableGroup: initialEnableGroup
    }));
  }, [value, schemaGroups, flattenSchemaGroups]);

  const handleFormValuesChange = useCallback(
    (changedValues: Partial<EntryItem>) => {
      if (state.formInitialized) return;
      if (!state.currentEntry?.entryId) return;

      const updatedEntry = {
        ...state.currentEntry,
        ...changedValues
      } as EntryItem;
      updatedEntry.id = updatedEntry.entryId;

      const newEntries = state.entries.map((entry) => (entry.entryId === updatedEntry.entryId ? updatedEntry : entry));

      setState((prev) => ({ ...prev, currentEntry: updatedEntry }));
      onChange?.({ ...normalizedValue, groups: entriesToGroups(newEntries) });
    },
    [state.currentEntry, state.entries, state.formInitialized, normalizedValue, onChange, entriesToGroups]
  );

  const handleEditEntry = useCallback(
    (entryId: string, field: string, value: string) => {
      setState((prev) => {
        // 检查是否真的需要更新
        const existingEntry = prev.entries.find((e) => e.entryId === entryId);
        if (existingEntry && existingEntry[field as keyof EntryItem] === value) {
          return prev;
        }

        const newEntries = prev.entries.map((entry) => {
          if (entry.entryId === entryId) {
            return { ...entry, [field]: value, id: entry.entryId };
          }
          return entry;
        });

        // 更新父组件，但不更新本地状态（由useEffect处理同步）
        onChange?.({ ...normalizedValue, groups: entriesToGroups(newEntries) });

        return { ...prev, entries: newEntries };
      });
    },
    [normalizedValue, onChange, entriesToGroups]
  );

  const handleAddEntry = useCallback(
    (type: 'menu' | 'link') => {
      if (type === 'link') {
        const nextIndex = getNextIndex(state.entries, 'entryName', '新增链接');
        const newEntry: EntryItem = {
          entryId: generateEntryId(),
          entryName: `新增链接${nextIndex}`,
          entryType: type,
          group: state.enableGroup ? state.entries[0]?.group || DEFAULT_GROUP_NAME : DEFAULT_GROUP_NAME
        };

        const newEntries = [...state.entries, { ...newEntry, id: newEntry.entryId }];
        setState((prev) => ({ ...prev, showAddMenu: false }));
        onChange?.({ ...normalizedValue, groups: entriesToGroups(newEntries) });
      } else {
        const nextIndex = getNextIndex(state.entries, 'entryName', '新增菜单');
        const pendingEntry: EntryItem = {
          entryId: generateEntryId(),
          entryName: `新增菜单${nextIndex}`,
          entryType: type,
          group: state.enableGroup ? state.entries[0]?.group || DEFAULT_GROUP_NAME : DEFAULT_GROUP_NAME,
          id: ''
        };

        setState({
          ...state,
          showAddMenu: false,
          selectMenuModalVisible: true,
          pendingEntry: { ...pendingEntry, id: pendingEntry.entryId }
        });
      }
    },
    [state, normalizedValue, onChange, entriesToGroups]
  );

  const handleDeleteEntry = useCallback(
    (entryId: string) => {
      const newEntries = state.entries.filter((entry) => entry.entryId !== entryId);
      onChange?.({ ...normalizedValue, groups: entriesToGroups(newEntries) });
      setState((prev) => ({ ...prev, entries: newEntries }));
    },
    [state.entries, normalizedValue, onChange, entriesToGroups]
  );

  const handleOpenEditDrawer = useCallback(
    (item: EntryItem) => {
      const normalizedItem = { ...item, id: item.entryId };

      form.setFieldsValue(normalizedItem);
      setState({
        ...state,
        drawerVisible: true,
        currentEntry: normalizedItem,
        entryType: item.entryType || '',
        formInitialized: true
      });

      // 在下一个渲染周期后重置formInitialized
      setTimeout(() => {
        setState((prev) => ({ ...prev, formInitialized: false }));
      }, 0);
    },
    [state, form]
  );

  const addGroup = useCallback(() => {
    if (state.newGroupName.trim() === '') {
      Message.error('请输入分组名称');
      return;
    }

    const existingGroups = schemaGroups.map((g) => ({
      groupName: g.groupName,
      entries: g.entries || []
    }));

    const newGroups = [...existingGroups, { groupName: state.newGroupName, entries: [] }];

    onChange?.({ ...normalizedValue, groups: newGroups });
    setState((prev) => ({ ...prev, newGroupName: '' }));
  }, [state.newGroupName, schemaGroups, normalizedValue, onChange]);

  const handleSelectMenuOk = useCallback(
    (menus: ApplicationMenu[]) => {
      setState((prev) => {
        // 添加模式
        if (prev.pendingEntry) {
          const uniqueMenus = menus.filter((menu) => menu.id);
          if (uniqueMenus.length > 0) {
            const createdEntries = uniqueMenus.map((menu) => {
              const nextEntryId = generateEntryId();
              return {
                ...prev.pendingEntry!,
                entryId: nextEntryId,
                id: nextEntryId,
                menuId: menu.id,
                entryName: menu.menuName || prev.pendingEntry!.entryName
              };
            });

            const newEntries = [...prev.entries, ...createdEntries];
            onChange?.({ ...normalizedValue, groups: entriesToGroups(newEntries) });

            return {
              ...prev,
              selectMenuModalVisible: false,
              pendingEntry: null,
              entries: newEntries
            };
          }
          return { ...prev, selectMenuModalVisible: false, pendingEntry: null };
        }

        // 编辑模式
        if (prev.currentEntry) {
          const targetMenu = menus.find((menu) => menu.id);
          if (targetMenu?.id) {
            const updatedEntry = {
              ...prev.currentEntry,
              menuId: targetMenu.id,
              entryName: targetMenu.menuName
            };

            const newEntries = prev.entries.map((entry) =>
              entry.entryId === updatedEntry.entryId ? updatedEntry : entry
            );

            onChange?.({ ...normalizedValue, groups: entriesToGroups(newEntries) });

            return {
              ...prev,
              selectMenuModalVisible: false,
              currentEntry: updatedEntry,
              entries: newEntries
            };
          }
        }

        return { ...prev, selectMenuModalVisible: false };
      });
    },
    [normalizedValue, onChange, entriesToGroups]
  );

  const renderDrawerContent = () => (
    <div className={styles.drawerContent}>
      <Form
        layout="vertical"
        className={styles.drawerForm}
        labelAlign="left"
        form={form}
        onValuesChange={handleFormValuesChange}
      >
        <FormItem
          label="入口类型"
          field="entryType"
          layout="horizontal"
          labelCol={{ span: 8 }}
          wrapperCol={{ span: 6, offset: 10 }}
        >
          <div>{state.entryType === 'menu' ? '应用菜单' : '外部链接'}</div>
        </FormItem>
        <FormItem label="入口图标" field="entryIcon">
          <div className={styles.iconUploader}>
            <div className={styles.iconUploaderAvatar}>
              <img src={IconEntry} alt="entry icon" />
            </div>
          </div>
        </FormItem>
        <FormItem label="入口名称" field="entryName">
          <Input placeholder="请输入" />
        </FormItem>
        <FormItem label="辅助描述" field="entryDesc">
          <Input.TextArea placeholder="请输入" autoSize={{ minRows: 2, maxRows: 3 }} />
        </FormItem>
        {state.enableGroup && (
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
                      value={state.newGroupName}
                      onChange={(value) => setState((prev) => ({ ...prev, newGroupName: value }))}
                    />
                    <Button style={{ fontSize: 14, padding: '0 6px' }} type="text" size="mini" onClick={addGroup}>
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
                <div className={styles.menuPicker}>
                  <Input readOnly value={values.menuId || '请选择菜单'} />
                  <Button
                    type="text"
                    icon={<IconEdit />}
                    onClick={() => setState((prev) => ({ ...prev, selectMenuModalVisible: true }))}
                  />
                </div>
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
  );

  const addEntry = (
    <Menu>
      <Menu.Item key="menu" onClick={() => handleAddEntry('menu')}>
        <IconMenu style={{ marginRight: 8 }} />
        应用菜单
      </Menu.Item>
      <Menu.Item key="link" onClick={() => handleAddEntry('link')}>
        <IconMenu style={{ marginRight: 8 }} />
        外部链接
      </Menu.Item>
    </Menu>
  );

  const handleSortListChange = useCallback(
    (newList: EntryItem[]) => {
      const normalizedList = newList.map((entry) => {
        const nextId = entry.entryId || entry.id || generateEntryId();
        return {
          ...entry,
          entryId: nextId,
          id: nextId
        };
      });

      // 检查是否真的需要更新
      const entriesChanged = normalizedList.some(
        (newEntry, index) =>
          state.entries[index]?.entryId !== newEntry.entryId || state.entries[index]?.group !== newEntry.group
      );

      if (entriesChanged) {
        onChange?.({ ...normalizedValue, groups: entriesToGroups(normalizedList) });
        setState((prev) => ({ ...prev, entries: normalizedList }));
      }
    },
    [state.entries, normalizedValue, onChange, entriesToGroups]
  );

  if (!value) {
    return null;
  }

  return (
    <div className={styles.content}>
      <div className={styles.entryConfig}>
        <ReactSortable
          list={state.entries.map((entry) => ({ ...entry, id: entry.entryId }))}
          setList={handleSortListChange}
          handle=".drag-handle"
          animation={200}
        >
          {state.entries.map((entry) => (
            <div key={entry.entryId} className={styles.entryItem}>
              <div className={`${styles.dragHandle} drag-handle`}>
                <IconDragDotVertical />
              </div>
              <div className={styles.entryContent}>
                <Input
                  value={entry.entryName}
                  onChange={(value) => handleEditEntry(entry.entryId, 'entryName', value)}
                  placeholder="请输入入口名称"
                />
              </div>
              <div className={styles.entryActions}>
                <IconEdit className={styles.actionIcon} onClick={() => handleOpenEditDrawer(entry)} />
                <Popconfirm title="确定要删除这个入口吗？" onOk={() => handleDeleteEntry(entry.entryId)}>
                  <IconDelete className={styles.actionIcon} />
                </Popconfirm>
              </div>
            </div>
          ))}
        </ReactSortable>
        <Dropdown
          droplist={addEntry}
          trigger="click"
          position="bl"
          popupVisible={state.showAddMenu}
          onVisibleChange={(visible) => setState((prev) => ({ ...prev, showAddMenu: visible }))}
        >
          <Button type="outline" className={styles.addEntryBtn} icon={<IconPlus />}>
            添加入口
          </Button>
        </Dropdown>

        <ConfigDrawer
          visible={state.drawerVisible}
          title="编辑入口"
          onClose={() => setState((prev) => ({ ...prev, drawerVisible: false, currentEntry: undefined }))}
        >
          {renderDrawerContent()}
        </ConfigDrawer>

        <SelectMenuModal
          visible={state.selectMenuModalVisible}
          onCancel={() => setState((prev) => ({ ...prev, selectMenuModalVisible: false }))}
          onOk={handleSelectMenuOk}
        />
      </div>
    </div>
  );
};

export default EntryContentConfig;
