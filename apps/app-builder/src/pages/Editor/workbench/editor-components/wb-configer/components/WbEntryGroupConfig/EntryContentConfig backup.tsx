import { Button, Divider, Dropdown, Form, Input, Menu, Message, Popconfirm, Select } from '@arco-design/web-react';
import { IconDelete, IconEdit, IconMenu, IconPlus, IconDragDotVertical } from '@arco-design/web-react/icon';
import { type ApplicationMenu } from '@onebase/app';
import { useCallback, useEffect, useRef, useState } from 'react';
import { isEqual } from 'lodash-es';
import { ReactSortable } from 'react-sortablejs';
import type { QuickEntryGroupConfig, QuickEntryGroupItemConfig } from '@onebase/ui-kit';
import IconEntry from '@/assets/workbench/quick-entry/entry1.svg';
import ConfigDrawer from '@/pages/Editor/workbench/components/configDrawer';
import SelectMenuModal from './selectMenuModal';
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

const normalizeGroupName = (group?: string) => group || DEFAULT_GROUP_NAME;

const entriesToGroups = (entries: EntryItem[]): QuickEntryGroupItemConfig[] => {
  const groupsMap = new Map<string, EntryItem[]>();
  entries.forEach((entry) => {
    const groupName = normalizeGroupName(entry.group);
    if (!groupsMap.has(groupName)) {
      groupsMap.set(groupName, []);
    }
    groupsMap.get(groupName)!.push(entry);
  });

  return Array.from(groupsMap.entries()).map(([groupName, entryList]) => ({
    groupName,
    entries: entryList.map((entry) => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const { group, id, ...rest } = entry;
      return rest;
    })
  }));
};

const flattenSchemaGroups = (groups: SchemaGroup[]): EntryItem[] => {
  const result: EntryItem[] = [];
  groups.forEach((group) => {
    const groupName = group?.groupName || DEFAULT_GROUP_NAME;
    (group?.entries || []).forEach((entry: EntryItem) => {
      const entryId = entry?.entryId || entry?.id || generateEntryId();
      result.push({
        ...entry,
        group: entry?.group || groupName,
        entryId,
        id: entryId
      });
    });
  });
  return result;
};

const toComparableEntries = (entries: EntryItem[]) =>
  entries.map((entry) => ({
    entryId: entry.entryId,
    group: normalizeGroupName(entry.group)
  }));

const isEntriesEqual = (prev: EntryItem[], next: EntryItem[]) => isEqual(toComparableEntries(prev), toComparableEntries(next));

const EntryContentConfig = ({ onChange, value }: EntryContentConfigProps) => {
  const [entries, setEntries] = useState<EntryItem[]>([]);
  const [showAddMenu, setShowAddMenu] = useState(false);
  const [drawerVisible, setDrawerVisible] = useState(false);
  const [selectMenuModalVisible, setSelectMenuModalVisible] = useState(false);
  const [entryType, setEntryType] = useState<string>('');
  const [newGroupName, setNewGroupName] = useState<string>('');
  const [currentEntry, setCurrentEntry] = useState<EntryItem>();
  const [enableGroup, setEnableGroup] = useState<boolean>(false);
  const initializingFormRef = useRef(false);
  const currentEntryRef = useRef<EntryItem>();
  const pendingEntryRef = useRef<EntryItem | null>(null);

  const [form] = Form.useForm<EntryItem>();

  const groupConfig = value;
  const schemaGroups = Array.isArray(groupConfig?.groups) ? (groupConfig.groups as SchemaGroup[]) : [];
  const groupOptions = schemaGroups;

  // 更新 groupConfig
  const updateGroupConfig = useCallback(
    (newEntries: EntryItem[]) => {
      if (!groupConfig) return;
      const groups = entriesToGroups(newEntries);
      onChange?.({ ...groupConfig, groups });
    },
    [groupConfig, onChange]
  );

  // 同步 currentEntry 到 ref
  useEffect(() => {
    currentEntryRef.current = currentEntry;
  }, [currentEntry]);

  // 初始化 entries 并同步 enableGroup
  useEffect(() => {
    if (!groupConfig) {
      setEntries([]);
      setEnableGroup(false);
      return;
    }

    setEnableGroup(Boolean(groupConfig.enableGroup));

    const nextEntries = schemaGroups.length > 0 ? flattenSchemaGroups(schemaGroups) : [];
    setEntries((prev) => (isEntriesEqual(prev, nextEntries) ? prev : nextEntries));

    // 如果存在缺失 entryId 的项，立即同步
    if (nextEntries.length > 0 && nextEntries.some((entry) => !entry.entryId)) {
      const groups = entriesToGroups(nextEntries);
      onChange?.({ ...groupConfig, groups });
    }
  }, [groupConfig, onChange, schemaGroups]);

  // 如果 value 不存在，不渲染
  if (!groupConfig) {
    return null;
  }

  // 新增入口，菜单类型需要先等待 menuId 选择
  const handleAddEntry = (type: 'menu' | 'link') => {
    setEntryType(type);

    if (type === 'link') {
      pendingEntryRef.current = null;
      const newEntry: EntryItem = {
        entryId: generateEntryId(),
        entryName: '新增链接' + (entries.length + 1),
        entryType: type,
        group: enableGroup ? entries[0]?.group || DEFAULT_GROUP_NAME : DEFAULT_GROUP_NAME
      };
      newEntry.id = newEntry.entryId;
      const newEntries = [...entries, newEntry];
      setEntries(newEntries);
      updateGroupConfig(newEntries);
      setShowAddMenu(false);
    } else {
      const pendingEntry: EntryItem = {
        entryId: generateEntryId(),
        entryName: '新增菜单' + (entries.length + 1),
        entryType: type,
        group: enableGroup ? entries[0]?.group || DEFAULT_GROUP_NAME : DEFAULT_GROUP_NAME,
        id: ''
      };
      pendingEntry.id = pendingEntry.entryId;
      pendingEntryRef.current = pendingEntry;
      setShowAddMenu(false);
      setSelectMenuModalVisible(true);
    }
  };

  const handleDeleteEntry = (entryId: string) => {
    const newEntries = entries.filter((entry) => entry.entryId !== entryId);
    setEntries(newEntries);
    updateGroupConfig(newEntries);
  };

  const handleOpenEditDrawer = (item: EntryItem) => {
    pendingEntryRef.current = null;
    const normalizedItem = { ...item, id: item.entryId };
    setEntryType(item.entryType || '');
    setCurrentEntry(normalizedItem);
    currentEntryRef.current = normalizedItem;
    initializingFormRef.current = true;
    form.setFieldsValue(normalizedItem);
    initializingFormRef.current = false;
    setDrawerVisible(true);
  };

  const handleEditEntry = (entryId: string, field: string, value: string) => {
    setEntries((prev) => {
      const newEntries = prev.map((entry) => {
        if (entry.entryId === entryId) {
          const nextEntry = { ...entry, [field]: value };
          nextEntry.id = nextEntry.entryId;
          return nextEntry;
        }
        return entry;
      });
      updateGroupConfig(newEntries);
      return newEntries;
    });
  };

  // 处理表单值变化，实时同步到workspace组件
  // 抽屉编辑采用 form.onValuesChange，排除初始化阶段
  const handleFormValuesChange = (changedValues: Partial<EntryItem>) => {
    if (initializingFormRef.current) {
      return;
    }
    const editingEntry = currentEntryRef.current;
    if (!editingEntry?.entryId) {
      return;
    }

    const updatedEntry = {
      ...editingEntry,
      ...changedValues
    } as EntryItem;
    updatedEntry.id = updatedEntry.entryId;

    setCurrentEntry(updatedEntry);
    currentEntryRef.current = updatedEntry;
    setEntries((prev) => {
      const newEntries = prev.map((entry) => (entry.entryId === updatedEntry.entryId ? updatedEntry : entry));
      updateGroupConfig(newEntries);
      return newEntries;
    });
  };

  const addGroup = () => {
    if (newGroupName.trim() === '') {
      Message.error('请输入分组名称');
      return;
    }
    const newGroups: QuickEntryGroupItemConfig[] = [
      ...groupOptions.map((g) => ({ groupName: g.groupName, entries: g.entries || [] })),
      { groupName: newGroupName, entries: [] }
    ];
    const nextGroupConfig: QuickEntryGroupConfig = {
      enableGroup: groupConfig?.enableGroup ?? false,
      groups: newGroups
    };
    onChange?.(nextGroupConfig);
    setNewGroupName('');
  };

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
          <div>{entryType === 'menu' ? '应用菜单' : '外部链接'}</div>
        </FormItem>
        <FormItem label="入口图标" field="entryIcon">
          <div className={styles.iconUploader}>
            <div className={styles.iconUploaderAvatar}>
              <img src={IconEntry} alt="entry icon" />
            </div>
            {/* <Button type="secondary">重新上传</Button> */}
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
                      onChange={(value) => setNewGroupName(value)}
                    />
                    <Button style={{ fontSize: 14, padding: '0 6px' }} type="text" size="mini" onClick={addGroup}>
                      <IconPlus />
                      添加分组
                    </Button>
                  </div>
                </div>
              )}
            >
              {groupOptions.map((group) => (
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
                  <Button type="text" icon={<IconEdit />} onClick={() => setSelectMenuModalVisible(true)} />
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

  return (
    <div className={styles.content}>
      <div className={styles.entryConfig}>
        <ReactSortable
          list={entries.map((entry) => ({ ...entry, id: entry.entryId }))}
          setList={(newList) => {
            const normalizedList = (newList as EntryItem[]).map((entry) => {
              const nextId = entry.entryId || entry.id || generateEntryId();
              return {
                ...entry,
                entryId: nextId,
                id: nextId
              };
            });
            setEntries((prev) => {
              if (isEntriesEqual(prev, normalizedList)) {
                return prev;
              }
              updateGroupConfig(normalizedList);
              return normalizedList;
            });
          }}
          handle=".drag-handle"
          animation={200}
        >
          {entries.map((entry) => (
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
          popupVisible={showAddMenu}
          onVisibleChange={setShowAddMenu}
        >
          <Button type="outline" className={styles.addEntryBtn} icon={<IconPlus />}>
            添加入口
          </Button>
        </Dropdown>

        <ConfigDrawer
          visible={drawerVisible}
          title="编辑入口"
          onClose={() => {
            setDrawerVisible(false);
            setCurrentEntry(undefined);
          }}
        >
          {renderDrawerContent()}
        </ConfigDrawer>

        <SelectMenuModal
          visible={selectMenuModalVisible}
          onCancel={() => {
            setSelectMenuModalVisible(false);
          }}
          onOk={(menus: ApplicationMenu[]) => {
            setSelectMenuModalVisible(false);
            // 如果是添加模式，将新entry添加到列表并打开编辑抽屉
            const pendingEntry = pendingEntryRef.current;
            if (pendingEntry) {
              const uniqueMenus = menus.filter((menu) => menu.id);
              if (uniqueMenus.length > 0) {
                const createdEntries = uniqueMenus.map((menu) => {
                  const nextEntryId = generateEntryId();
                  return {
                    ...pendingEntry,
                    entryId: nextEntryId,
                    id: nextEntryId,
                    menuId: menu.id,
                    entryName: menu.menuName || pendingEntry.entryName
                  };
                });
                const newEntries = [...entries, ...createdEntries];
                setEntries(newEntries);
                updateGroupConfig(newEntries);
                pendingEntryRef.current = null;
                return;
              }
              pendingEntryRef.current = null;
            }

            // 编辑模式下，仅同步第一项选择
            const editingEntry = currentEntryRef.current;
            if (editingEntry) {
              const targetMenu = menus.find((menu) => menu.id);
              if (targetMenu?.id) {
                handleFormValuesChange({ menuId: targetMenu.id, entryName: targetMenu.menuName });
              }
            }
          }}
        />
      </div>
    </div>
  );
};

export default EntryContentConfig;
