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

const DEFAULT_TITLE_CONFIG: QuickEntryTitleConfig = {
  showTitle: true,
  titleName: '快捷入口',
  showMore: true,
  enableGroup: false
};

const DEFAULT_GROUP_CONFIG: QuickEntryGroupConfig = {
  enableGroup: false,
  groups: []
};

const WbEntryTitleConfig = ({ handlePropsChange, item, configs }: Props) => {
  const currentTitleConfig = useMemo(() => {
    const value = configs?.[item.key] as QuickEntryTitleConfig | undefined;
    return value || DEFAULT_TITLE_CONFIG;
  }, [configs, item.key]);

  const currentGroupConfig = useMemo(() => {
    const value = configs?.['groupConfig'] as QuickEntryGroupConfig | undefined;
    return value || DEFAULT_GROUP_CONFIG;
  }, [configs]);

  const handleTitleConfigChange = useCallback(
    (patch: Partial<QuickEntryTitleConfig>) => {
      const nextValue = {
        ...currentTitleConfig,
        ...patch
      };
      handlePropsChange(item.key, nextValue);
    },
    [currentTitleConfig, handlePropsChange, item.key]
  );

  const handleEnableGroupChange = useCallback(
    (value: boolean) => {
      // 同时更新titleConfig和groupConfig的enableGroup
      handleTitleConfigChange({ enableGroup: value });
      const nextGroupConfig = {
        ...currentGroupConfig,
        enableGroup: value
      };
      handlePropsChange('groupConfig', nextGroupConfig);
    },
    [currentGroupConfig, handleTitleConfigChange, handlePropsChange]
  );

  return (
    <div className={styles.entryTitleConfig}>
      <div className={styles.formItem}>
        <label>标题名称</label>
        <Input
          value={currentTitleConfig.titleName}
          onChange={(value) => handleTitleConfigChange({ titleName: value })}
          placeholder="请输入标题名称"
        />
      </div>
      <div className={styles.formItem}>
        <label>显示标题</label>
        <Switch
          checked={currentTitleConfig.showTitle}
          onChange={(value) => handleTitleConfigChange({ showTitle: value })}
        />
      </div>
      <div className={styles.formItem}>
        <label>查看更多</label>
        <Switch
          checked={currentTitleConfig.showMore}
          onChange={(value) => handleTitleConfigChange({ showMore: value })}
        />
      </div>
      <div className={styles.formItem}>
        <label>分组</label>
        <Switch checked={currentTitleConfig.enableGroup || false} onChange={handleEnableGroupChange} />
      </div>
    </div>
  );
};

export default WbEntryTitleConfig;

registerConfigRenderer(WORKBENCH_CONFIG_TYPES.WB_ENTRY_TITLE, ({ handlePropsChange, item, configs }) => (
  <WbEntryTitleConfig handlePropsChange={handlePropsChange} item={item as IEntryTitleConfigType} configs={configs} />
));
