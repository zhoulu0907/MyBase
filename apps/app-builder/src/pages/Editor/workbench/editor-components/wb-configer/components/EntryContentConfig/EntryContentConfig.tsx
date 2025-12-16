import { Button, Divider, Dropdown, Form, Input, Menu, Message, Popconfirm, Select } from '@arco-design/web-react';
import { IconDelete, IconEdit, IconMenu, IconPlus, IconDragDotVertical } from '@arco-design/web-react/icon';
import { type ApplicationMenu } from '@onebase/app';
import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import type { QuickEntryPropsConfig, QuickEntryGroupConfig } from '@onebase/ui-kit';
import IconEntry from '@/assets/workbench/quick-entry/entry1.svg';
import ConfigDrawer from '@/pages/Editor/workbench/components/configDrawer';
import SelectMenuModal from './selectMenuModal';
import styles from './EntryContentConfig.module.less';

export interface EntryContentConfigProps {
  value?: QuickEntryPropsConfig;
  onChange?: (value: QuickEntryPropsConfig) => void;
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

const DEFAULT_GROUP_CONFIG: QuickEntryGroupConfig = {
  enableGroup: false,
  groups: []
};

const EntryContentConfig = ({ onChange, value }: EntryContentConfigProps) => {
  const normalizedValue = useMemo(() => {
    return (
      value || {
        titleConfig: { showTitle: true, titleName: '快捷入口', showMore: true },
        styleConfig: { theme: 'theme-one' },
        groupConfig: DEFAULT_GROUP_CONFIG
      }
    );
  }, [value]);

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
  const entriesInitializedRef = useRef(false);
  const entriesRef = useRef<EntryItem[]>([]);
  const syncTimerRef = useRef<number | null>(null);
  const pendingSyncEntriesRef = useRef<EntryItem[] | null>(null);

  const groupConfig = normalizedValue.groupConfig || DEFAULT_GROUP_CONFIG;
  const schemaGroups = groupConfig.groups as SchemaGroup[] | undefined;
  const groupOptions = Array.isArray(schemaGroups) ? schemaGroups : [];

  const [form] = Form.useForm<EntryItem>();

  // 将最新 entries 存入 ref，供 effect 做浅比较使用
  useEffect(() => {
    entriesRef.current = entries;
  }, [entries]);

  // 更新 groupConfig 并触发 onChange
  const updateGroupConfig = useCallback(
    (newEntries: EntryItem[]) => {
      if (!entriesInitializedRef.current) {
        return;
      }
      const groupsMap = new Map<string, EntryItem[]>();
      newEntries.forEach((entry) => {
        const groupName = entry.group || DEFAULT_GROUP_NAME;
        if (!groupsMap.has(groupName)) {
          groupsMap.set(groupName, []);
        }
        groupsMap.get(groupName)!.push({ ...entry });
      });

      // 每次同步时重建 groups，避免引用残留导致的样式回退
      const groups = Array.from(groupsMap.entries()).map(([groupName, entryList]) => ({
        groupName,
        entries: entryList.map((entryItem) => {
          const rest = { ...entryItem };
          delete rest.group;
          delete rest.id;
          return rest;
        })
      }));

      const nextGroupConfig: QuickEntryGroupConfig = {
        ...groupConfig,
        groups
      };

      const nextValue: QuickEntryPropsConfig = {
        ...normalizedValue,
        groupConfig: nextGroupConfig
      };

      onChange?.(nextValue);
    },
    [groupConfig, normalizedValue, onChange]
  );

  // 300ms 防抖写回 schema，避免每个字符都触发 store 更新
  const scheduleUpdateGroupConfig = useCallback(
    (newEntries: EntryItem[]) => {
      entriesInitializedRef.current = true;
      pendingSyncEntriesRef.current = newEntries;
      if (syncTimerRef.current) {
        window.clearTimeout(syncTimerRef.current);
      }
      syncTimerRef.current = window.setTimeout(() => {
        if (pendingSyncEntriesRef.current) {
          updateGroupConfig(pendingSyncEntriesRef.current);
        }
      }, 300);
    },
    [updateGroupConfig]
  );

  // 卸载时清理定时器
  useEffect(
    () => () => {
      if (syncTimerRef.current) {
        window.clearTimeout(syncTimerRef.current);
      }
    },
    []
  );

  useEffect(() => {
    if (!Array.isArray(schemaGroups)) {
      setEntries([]);
      return;
    }

    let needsIdSync = false;
    const nextEntries: EntryItem[] = [];
    // 扁平化 schema 中的分组数据，并补齐缺失的 entryId
    schemaGroups.forEach((group) => {
      const groupName = group?.groupName || DEFAULT_GROUP_NAME;
      (group?.entries || []).forEach((entry: EntryItem) => {
        const normalizedId = entry?.entryId || entry?.id || generateEntryId();
        if (!entry?.entryId) {
          needsIdSync = true;
        }
        nextEntries.push({
          ...entry,
          group: entry?.group || groupName,
          entryId: normalizedId,
          id: normalizedId
        });
      });
    });

    // 如果扁平化结果与当前 entries 完全一致，就不再触发本地更新，避免循环
    const prev = entriesRef.current;
    const isSame =
      prev.length === nextEntries.length &&
      prev.every(
        (item, index) =>
          item.entryId === nextEntries[index].entryId &&
          (item.group || DEFAULT_GROUP_NAME) === (nextEntries[index].group || DEFAULT_GROUP_NAME)
      );
    if (isSame) {
      return;
    }

    setEntries(nextEntries);
    entriesInitializedRef.current = true;

    if (needsIdSync && nextEntries.length > 0) {
      updateGroupConfig(nextEntries);
    }
    // 这里只关注 schemaGroups 变化，updateGroupConfig 通过闭包获取最新引用
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [schemaGroups]);

  useEffect(() => {
    currentEntryRef.current = currentEntry;
  }, [currentEntry]);

  useEffect(() => {
    setEnableGroup(Boolean(groupConfig.enableGroup));
  }, [groupConfig.enableGroup]);

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
      scheduleUpdateGroupConfig(newEntries);
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
    scheduleUpdateGroupConfig(newEntries);
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

  // 拖拽列表内编辑时，保持指定 entry 独立更新
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
      scheduleUpdateGroupConfig(newEntries);
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
      scheduleUpdateGroupConfig(newEntries);
      return newEntries;
    });
  };

  const addGroup = () => {
    if (newGroupName.trim() === '') {
      Message.error('请输入分组名称');
      return;
    }
    const newGroups = [...groupOptions, { groupName: newGroupName, entries: [] }];
    const nextGroupConfig: QuickEntryGroupConfig = {
      ...groupConfig,
      groups: newGroups
    };
    const nextValue: QuickEntryPropsConfig = {
      ...normalizedValue,
      groupConfig: nextGroupConfig
    };
    onChange?.(nextValue);
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
            setEntries(normalizedList);
            scheduleUpdateGroupConfig(normalizedList);
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
