import { Input, Switch } from '@arco-design/web-react';
import { useMemo, useCallback } from 'react';
import { registerConfigRenderer } from '../../registry';
import {
  WORKBENCH_CONFIG_TYPES,
  type IEntryTitleConfigType,
  type QuickEntryTitleConfig,
  type QuickEntryGroupConfig
} from '@onebase/ui-kit';
import styles from './index.module.less';

interface Props {
  handlePropsChange: (key: string, value: unknown) => void;
  item: IEntryTitleConfigType;
  configs: Record<string, unknown>;
}

const WbEntryTitleConfig = ({ handlePropsChange, item, configs }: Props) => {
  const currentTitleConfig = useMemo(() => {
    return (configs?.[item.key] as QuickEntryTitleConfig | undefined) || undefined;
  }, [configs, item.key]);

  const currentGroupConfig = useMemo(() => {
    return (configs?.['groupConfig'] as QuickEntryGroupConfig | undefined) || undefined;
  }, [configs]);

  const handleTitleConfigChange = useCallback(
    (patch: Partial<QuickEntryTitleConfig>) => {

      const latestTitleConfig = configs?.[item.key] as QuickEntryTitleConfig | undefined;
      const nextValue = {
        ...(latestTitleConfig || {}),
        ...patch
      };
      handlePropsChange(item.key, nextValue);
    },
    [configs, handlePropsChange, item.key]
  );

  const handleEnableGroupChange = useCallback(
    (value: boolean) => {

      const latestTitleConfig = configs?.[item.key] as QuickEntryTitleConfig | undefined;
      const latestGroupConfig = configs?.['groupConfig'] as QuickEntryGroupConfig | undefined;

      // 同时更新titleConfig和groupConfig的enableGroup
      const nextTitleConfig = {
        ...(latestTitleConfig || {}),
        enableGroup: value
      };
      handlePropsChange(item.key, nextTitleConfig);

      if (latestGroupConfig) {
        const nextGroupConfig = {
          ...latestGroupConfig,
          enableGroup: value
        };
        handlePropsChange('groupConfig', nextGroupConfig);
      } else {
        handlePropsChange('groupConfig', {
          enableGroup: value,
          groups: []
        });
      }
    },
    [configs, handlePropsChange, item.key]
  );

  // 如果配置不存在，不渲染
  if (!currentTitleConfig) {
    return null;
  }

  return (
    <div className={styles.entryTitleConfig}>
      <div className={styles.formItem}>
        <label>标题名称</label>
        <Input
          value={currentTitleConfig.titleName || ''}
          onChange={(value) => handleTitleConfigChange({ titleName: value })}
          placeholder="请输入标题名称"
        />
      </div>
      <div className={styles.formItem}>
        <label>显示标题</label>
        <Switch
          checked={currentTitleConfig.showTitle ?? true}
          onChange={(value) => handleTitleConfigChange({ showTitle: value })}
        />
      </div>
      <div className={styles.formItem}>
        <label>查看更多</label>
        <Switch
          checked={currentTitleConfig.showMore ?? true}
          onChange={(value) => handleTitleConfigChange({ showMore: value })}
        />
      </div>
      <div className={styles.formItem}>
        <label>分组</label>
        <Switch
          checked={currentGroupConfig?.enableGroup ?? currentTitleConfig?.enableGroup ?? false}
          onChange={handleEnableGroupChange}
        />
      </div>
    </div>
  );
};

export default WbEntryTitleConfig;

registerConfigRenderer(WORKBENCH_CONFIG_TYPES.WB_ENTRY_TITLE, ({ handlePropsChange, item, configs }) => (
  <WbEntryTitleConfig handlePropsChange={handlePropsChange} item={item as IEntryTitleConfigType} configs={configs} />
));
