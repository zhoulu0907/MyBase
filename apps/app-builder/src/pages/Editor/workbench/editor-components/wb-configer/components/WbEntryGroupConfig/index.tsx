import { useCallback, useMemo } from 'react';
import { registerConfigRenderer } from '../../registry';
import { WORKBENCH_CONFIG_TYPES, type IEntryGroupConfigType, type QuickEntryGroupConfig } from '@onebase/ui-kit';
import SelectMenuModal from './components/selectMenuModal';
import { EntryList } from './components/EntryList';
import { EntryFormDrawer } from './components/EntryFormDrawer';
import { IconSelectorModal } from './components/IconSelectorModal';
import { useEntryManagement } from './hooks/useEntryManagement';
import { useIconSelector } from './hooks/useIconSelector';
import type { EntryItem } from './types';
import styles from './index.module.less';

interface Props {
  handlePropsChange: (key: string, value: unknown) => void;
  item: IEntryGroupConfigType;
  configs: Record<string, unknown>;
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

const WbEntryGroupConfig = ({ handlePropsChange, item, configs }: Props) => {
  const currentValue = useMemo(() => {
    const nextValue = configs?.[item.key] as QuickEntryGroupConfig | undefined;
    return nextValue;
  }, [configs, item.key]);

  const handleEntryChange = useCallback(
    (value: QuickEntryGroupConfig) => {
      handlePropsChange(item.key, value);
    },
    [handlePropsChange, item.key]
  );

  const {
    form,
    state,
    setState,
    schemaGroups,
    handleFormValuesChange,
    handleEditEntry,
    handleAddEntry,
    handleDeleteEntry,
    handleOpenEditDrawer,
    addGroup,
    handleSelectMenuOk,
    handleSortListChange
  } = useEntryManagement({ value: currentValue, onChange: handleEntryChange });

  const { currentIconSrc, handleMenuIconSelected } = useIconSelector({
    form,
    currentEntry: state.currentEntry,
    onEditEntry: handleEditEntry,
    onClose: () => setState((prev: EntryManagementState) => ({ ...prev, visibleMenuIcon: false }))
  });

  const handleChooseIcon = useCallback(() => {
    setState((prev: typeof state) => ({ ...prev, visibleMenuIcon: true }));
  }, [setState]);

  const handleCloseDrawer = useCallback(() => {
    setState((prev: typeof state) => ({ ...prev, drawerVisible: false, currentEntry: undefined }));
  }, [setState]);

  if (!currentValue) {
    return null;
  }

  return (
    <div className={styles.content}>
      <div className={styles.entryConfig}>
        <EntryList
          entries={state.entries}
          showAddMenu={state.showAddMenu}
          onSortChange={handleSortListChange}
          onEdit={handleOpenEditDrawer}
          onDelete={handleDeleteEntry}
          onAddEntry={handleAddEntry}
          onEditEntry={handleEditEntry}
          onShowAddMenuChange={(visible) =>
            setState((prev: EntryManagementState) => ({ ...prev, showAddMenu: visible }))
          }
        />

        <EntryFormDrawer
          visible={state.drawerVisible}
          form={form}
          entryType={state.entryType}
          enableGroup={state.enableGroup}
          schemaGroups={schemaGroups}
          newGroupName={state.newGroupName}
          currentIconSrc={currentIconSrc}
          onClose={handleCloseDrawer}
          onValuesChange={handleFormValuesChange}
          onEditEntry={handleEditEntry}
          onAddGroup={addGroup}
          onNewGroupNameChange={(value) => setState((prev: EntryManagementState) => ({ ...prev, newGroupName: value }))}
          onChooseIcon={handleChooseIcon}
        />

        <SelectMenuModal
          visible={state.selectMenuModalVisible}
          onCancel={() => setState((prev: typeof state) => ({ ...prev, selectMenuModalVisible: false }))}
          onOk={handleSelectMenuOk}
        />

        <IconSelectorModal
          visible={state.visibleMenuIcon}
          onClose={() => setState((prev: typeof state) => ({ ...prev, visibleMenuIcon: false }))}
          onSelected={handleMenuIconSelected}
        />
      </div>
    </div>
  );
};

export default WbEntryGroupConfig;

registerConfigRenderer(WORKBENCH_CONFIG_TYPES.WB_ENTRY_GROUP, ({ handlePropsChange, item, configs }) => (
  <WbEntryGroupConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
