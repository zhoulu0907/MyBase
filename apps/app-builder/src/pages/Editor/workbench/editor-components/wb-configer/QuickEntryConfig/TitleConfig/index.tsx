import { Form, Input, Switch } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';
import { useQuickEntrySection } from '../hooks/useQuickEntrySection';
import type { QuickEntryGroupConfig, QuickEntryTitleConfig } from '../types';
import styles from './index.module.less';

const FormItem = Form.Item;

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
      <Form layout="horizontal" labelAlign="left">
        <FormItem label="标题名称" layout="vertical">
          <Input
            value={titleConfig.titleName}
            onChange={(value) => handleTitleConfigChange({ titleName: value })}
            placeholder="请输入标题名称"
          />
        </FormItem>
        <FormItem label="显示标题" labelCol={{ span: 8 }} wrapperCol={{ span: 4, offset: 12 }}>
          <Switch checked={titleConfig.showTitle} onChange={(value) => handleTitleConfigChange({ showTitle: value })} />
        </FormItem>
        <FormItem label="查看更多" labelCol={{ span: 8 }} wrapperCol={{ span: 4, offset: 12 }}>
          <Switch checked={titleConfig.showMore} onChange={(value) => handleTitleConfigChange({ showMore: value })} />
        </FormItem>
        <FormItem label="分组" labelCol={{ span: 8 }} wrapperCol={{ span: 4, offset: 12 }}>
          <Switch checked={titleConfig.enableGroup} onChange={handleEnableGroupChange} />
        </FormItem>
      </Form>
    </div>
  );
};

export default TitleConfig;
