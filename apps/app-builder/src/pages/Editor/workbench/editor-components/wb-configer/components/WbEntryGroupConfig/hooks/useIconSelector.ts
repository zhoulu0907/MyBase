import { useCallback, useMemo } from 'react';
import type { FormInstance } from '@arco-design/web-react';
import { webMenuIcons } from '@onebase/ui-kit';
import IconEntry from '@/assets/workbench/quick-entry/entry1.svg';
import type { EntryItem } from '../types';

interface UseIconSelectorProps {
  form: FormInstance<EntryItem>;
  currentEntry?: EntryItem;
  onEditEntry: (entryId: string, field: string, value: string) => void;
  onClose: () => void;
}

export const useIconSelector = ({ form, currentEntry, onEditEntry, onClose }: UseIconSelectorProps) => {
  // 获取当前选中的图标
  const entryIconValue = form.getFieldValue('entryIcon');
  const currentIconSrc = useMemo(() => {
    if (!entryIconValue) return IconEntry;
    const allIcons = webMenuIcons.map((ele) => ele.children).reduce((acc, current) => acc.concat(current), []);
    const icon = allIcons.find((item) => item.code === entryIconValue);
    return icon?.icon || IconEntry;
  }, [entryIconValue]);

  const handleMenuIconSelected = useCallback(
    (iconCode: string) => {
      // 更新表单中的图标
      if (currentEntry?.entryId) {
        onEditEntry(currentEntry.entryId, 'entryIcon', iconCode);
        form.setFieldValue('entryIcon', iconCode);
      }
      onClose();
    },
    [currentEntry, onEditEntry, form, onClose]
  );

  return {
    currentIconSrc,
    handleMenuIconSelected
  };
};
