import { useCallback, useEffect, useMemo, useState } from 'react';
import { Form, Message } from '@arco-design/web-react';
import { type ApplicationMenu } from '@onebase/app';
import { getNextIndex } from '@/pages/Editor/workbench/utils/edit-data';
import type { QuickEntryGroupConfig } from '@onebase/ui-kit';
import type { EntryItem, SchemaGroup } from '../types';
import { generateEntryId, entriesToGroups, flattenSchemaGroups } from '../utils';

const DEFAULT_GROUP_NAME = '默认分组';

interface UseEntryManagementProps {
  value?: QuickEntryGroupConfig;
  onChange?: (value: QuickEntryGroupConfig) => void;
}

interface EntryManagementState {
  entries: EntryItem[];
  showAddMenu: boolean;
  drawerVisible: boolean;
  selectMenuModalVisible: boolean;
  entryType: string;
  newGroupName: string;
  currentEntry?: EntryItem;
  enableGroup: boolean;
  pendingEntry: EntryItem | null;
  formInitialized: boolean;
  visibleMenuIcon: boolean;
  menuIcon: string;
}

export const useEntryManagement = ({ value, onChange }: UseEntryManagementProps) => {
  const [state, setState] = useState<EntryManagementState>({
    entries: [],
    showAddMenu: false,
    drawerVisible: false,
    selectMenuModalVisible: false,
    entryType: '',
    newGroupName: '',
    currentEntry: undefined,
    enableGroup: false,
    pendingEntry: null,
    formInitialized: false,
    visibleMenuIcon: false,
    menuIcon: ''
  });

  const [form] = Form.useForm<EntryItem>();

  const normalizedValue = useMemo(() => value || { enableGroup: false, groups: [] }, [value]);
  const schemaGroups = useMemo(() => normalizedValue.groups || [], [normalizedValue]);

  // 同步entries
  useEffect(() => {
    if (!value) return;

    const initialEntries = flattenSchemaGroups(schemaGroups as SchemaGroup[]);
    const initialEnableGroup = Boolean(value.enableGroup);

    setState((prev) => ({
      ...prev,
      entries: initialEntries,
      enableGroup: initialEnableGroup
    }));
  }, [value, schemaGroups]);

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
    [state.currentEntry, state.entries, state.formInitialized, normalizedValue, onChange]
  );

  const handleEditEntry = useCallback(
    (entryId: string, field: string, value: string) => {
      setState((prev) => {
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

        onChange?.({ ...normalizedValue, groups: entriesToGroups(newEntries) });

        return { ...prev, entries: newEntries };
      });
    },
    [normalizedValue, onChange]
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
    [state, normalizedValue, onChange]
  );

  const handleDeleteEntry = useCallback(
    (entryId: string) => {
      const newEntries = state.entries.filter((entry) => entry.entryId !== entryId);
      onChange?.({ ...normalizedValue, groups: entriesToGroups(newEntries) });
      setState((prev) => ({ ...prev, entries: newEntries }));
    },
    [state.entries, normalizedValue, onChange]
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

    const existingGroups = (schemaGroups as SchemaGroup[]).map((g) => ({
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
                menuUuid: menu.menuUuid,
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
              menuUuid: targetMenu.id,
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
    [normalizedValue, onChange, state.entries]
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

      const entriesChanged = normalizedList.some(
        (newEntry, index) =>
          state.entries[index]?.entryId !== newEntry.entryId || state.entries[index]?.group !== newEntry.group
      );

      if (entriesChanged) {
        onChange?.({ ...normalizedValue, groups: entriesToGroups(normalizedList) });
        setState((prev) => ({ ...prev, entries: normalizedList }));
      }
    },
    [state.entries, normalizedValue, onChange]
  );

  return {
    form,
    state,
    setState,
    schemaGroups: schemaGroups as SchemaGroup[],
    handleFormValuesChange,
    handleEditEntry,
    handleAddEntry,
    handleDeleteEntry,
    handleOpenEditDrawer,
    addGroup,
    handleSelectMenuOk,
    handleSortListChange
  };
};
