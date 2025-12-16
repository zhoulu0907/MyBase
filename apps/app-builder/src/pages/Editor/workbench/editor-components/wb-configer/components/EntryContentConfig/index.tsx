import { useCallback, useMemo } from 'react';
import { registerConfigRenderer } from '../../registry';
import { WORKBENCH_CONFIG_TYPES, type IQuickEntryConfigType, type QuickEntryPropsConfig } from '@onebase/ui-kit';
import EntryContent from './EntryContentConfig';

interface Props {
  handlePropsChange: (key: string, value: unknown) => void;
  item: IQuickEntryConfigType;
  configs: Record<string, unknown>;
}

const EntryContentConfig = ({ handlePropsChange, item, configs }: Props) => {
  const currentValue = useMemo(() => {
    const nextValue = configs?.[item.key] as QuickEntryPropsConfig | undefined;
    return nextValue;
  }, [configs, item.key]);

  const handleEntryChange = useCallback(
    (value: QuickEntryPropsConfig) => {
      handlePropsChange(item.key, value);
    },
    [handlePropsChange, item.key]
  );

  return (
    <>
      <EntryContent value={currentValue} onChange={handleEntryChange} />
    </>
  );
};

export default EntryContentConfig;

registerConfigRenderer(WORKBENCH_CONFIG_TYPES.QUICK_ENTRY, ({ handlePropsChange, item, configs }) => (
  <EntryContentConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
