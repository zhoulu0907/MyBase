import type { QuickEntryGroupItemConfig } from '@onebase/ui-kit';
import type { EntryItem, SchemaGroup } from './types';

const DEFAULT_GROUP_NAME = '默认分组';

export const generateEntryId = () => `entry-${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 8)}`;

/**
 * 将 entries 数组转换为分组结构
 */
export const entriesToGroups = (entries: EntryItem[]): QuickEntryGroupItemConfig[] => {
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
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const { group, id, ...rest } = entry;
      return rest;
    })
  }));
};

/**
 * 将分组结构扁平化为 entries 数组
 */
export const flattenSchemaGroups = (groups: SchemaGroup[]): EntryItem[] => {
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
};
