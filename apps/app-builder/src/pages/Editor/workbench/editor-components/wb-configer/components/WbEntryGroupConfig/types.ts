import type { QuickEntryGroupConfig } from '@onebase/ui-kit';

export interface EntryContentConfigProps {
  value?: QuickEntryGroupConfig;
  onChange?: (value: QuickEntryGroupConfig) => void;
}

export interface EntryItem {
  entryName: string;
  entryIcon?: string;
  entryType?: 'menu' | 'link';
  menuUuid?: string;
  linkAddress?: string;
  group?: string;
  entryId: string;
  id?: string;
  entryDesc?: string;
  [key: string]: unknown;
}

export interface SchemaGroup {
  groupName: string;
  entries?: EntryItem[];
}
