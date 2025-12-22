import IconEntry1 from '@/assets/workbench/quick-entry/entry1.svg';
import IconEntry2 from '@/assets/workbench/quick-entry/entry2.svg';
import IconEntry3 from '@/assets/workbench/quick-entry/entry3.svg';
import IconEntry4 from '@/assets/workbench/quick-entry/entry4.svg';
import IconEntry5 from '@/assets/workbench/quick-entry/entry5.svg';
import IconEntry6 from '@/assets/workbench/quick-entry/entry6.svg';

const ICON_ENTRIES = [IconEntry1, IconEntry2, IconEntry3, IconEntry4, IconEntry5, IconEntry6];

export const getDefaultIcon = (index: number): string => {
  return ICON_ENTRIES[index % ICON_ENTRIES.length];
};