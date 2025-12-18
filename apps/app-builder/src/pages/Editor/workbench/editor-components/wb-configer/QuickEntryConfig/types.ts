export interface EntryItem {
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

export interface SchemaGroup {
  groupName: string;
  entries?: EntryItem[];
}

export interface QuickEntryStyleConfig {
  theme?: string;
}

export interface QuickEntryTitleConfig {
  showTitle?: boolean;
  titleName?: string;
  showMore?: boolean;
  enableGroup?: boolean;
}

export interface QuickEntryGroupConfig {
  enableGroup?: boolean;
  groups?: SchemaGroup[];
}

export interface QuickEntryOtherConfig {
  [key: string]: unknown;
}

export interface QuickEntryProps {
  styleConfig?: QuickEntryStyleConfig;
  entryTitleConfig?: QuickEntryTitleConfig;
  groupConfig?: QuickEntryGroupConfig;
}
