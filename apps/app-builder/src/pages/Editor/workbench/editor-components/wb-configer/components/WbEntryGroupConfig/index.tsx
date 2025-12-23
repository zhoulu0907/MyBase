import { useCallback, useMemo } from 'react';
import { registerConfigRenderer } from '../../registry';
import { WORKBENCH_CONFIG_TYPES, type IEntryGroupConfigType, type QuickEntryGroupConfig } from '@onebase/ui-kit';
import EntryContent from './EntryContentConfig';

interface Props {
  handlePropsChange: (key: string, value: unknown) => void;
  item: IEntryGroupConfigType;
  configs: Record<string, unknown>;
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

  return (
    <>
      <EntryContent value={currentValue} onChange={handleEntryChange} />
    </>
  );
};

export default WbEntryGroupConfig;

registerConfigRenderer(WORKBENCH_CONFIG_TYPES.WB_ENTRY_GROUP, ({ handlePropsChange, item, configs }) => (
  <WbEntryGroupConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
