import { Input, Switch } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';
import { useQuickEntrySection } from '../hooks/useQuickEntrySection';
import type { QuickEntryGroupConfig, QuickEntryTitleConfig } from '../types';
import styles from './index.module.less';

interface TitleConfigProps {
  cpID: string;
}

const DEFAULT_TITLE_CONFIG: QuickEntryTitleConfig = {
  showTitle: true,
  titleName: '快捷入口',
  showMore: true,
  enableGroup: false
};

const DEFAULT_GROUP_CONFIG: QuickEntryGroupConfig = {
  enableGroup: false
};

const TitleConfig = ({ cpID }: TitleConfigProps) => {
  useSignals();

  const [titleConfig, updateTitleConfig] = useQuickEntrySection(cpID, 'titleConfig', DEFAULT_TITLE_CONFIG);
  const [, updateGroupConfig] = useQuickEntrySection(cpID, 'groupConfig', DEFAULT_GROUP_CONFIG);

  const handleTitleConfigChange = (patch: Partial<QuickEntryTitleConfig>) => {
    updateTitleConfig(patch);
  };

  const handleEnableGroupChange = (value: boolean) => {
    updateTitleConfig({ enableGroup: value });
    updateGroupConfig({ enableGroup: value });
  };

  return (
    <div className={styles.titleConfig}>
      <div className={styles.formItem}>
        <label>标题名称</label>
        <Input
          value={titleConfig.titleName}
          onChange={(value) => handleTitleConfigChange({ titleName: value })}
          placeholder="请输入标题名称"
        />
      </div>
      <div className={styles.formItem}>
        <label>显示标题</label>
        <Switch checked={titleConfig.showTitle} onChange={(value) => handleTitleConfigChange({ showTitle: value })} />
      </div>
      <div className={styles.formItem}>
        <label>查看更多</label>
        <Switch checked={titleConfig.showMore} onChange={(value) => handleTitleConfigChange({ showMore: value })} />
      </div>
      <div className={styles.formItem}>
        <label>分组</label>
        <Switch checked={titleConfig.enableGroup} onChange={handleEnableGroupChange} />
      </div>
    </div>
  );
};

export default TitleConfig;
