import { Form, Button } from '@arco-design/web-react';
import { IconSettings } from '@arco-design/web-react/icon';
import { useCallback, useMemo, useState } from 'react';
import { registerConfigRenderer } from '../../registry';
import { WORKBENCH_CONFIG_TYPES, type IWbRichTextContentConfigType } from '@onebase/ui-kit';
import ContentDrawer from './contentDrawer';
import styles from '../../index.module.less';

interface Props {
  handlePropsChange: (key: string, value: unknown) => void;
  item: IWbRichTextContentConfigType;
  configs: Record<string, unknown>;
}

const WbRichTextContentConfig = ({ handlePropsChange, item, configs }: Props) => {
  const [contentDrawerVisible, setContentDrawerVisible] = useState(false);

  const currentValue = useMemo(() => {
    const nextValue = configs?.[item.key];
    return typeof nextValue === 'string' ? nextValue : '';
  }, [configs, item.key]);

  const handleContentChange = useCallback(
    (html: string) => {
      handlePropsChange(item.key, html);
    },
    [handlePropsChange, item.key]
  );

  return (
    <>
      <Form.Item className={styles.formItem} layout="horizontal" labelAlign="left" wrapperCol={{ span: 24 }}>
        <Button
          type="outline"
          onClick={() => setContentDrawerVisible(true)}
          icon={<IconSettings />}
          className={styles.longButton}
        >
          配置内容
        </Button>
      </Form.Item>

      <ContentDrawer
        visible={contentDrawerVisible}
        onClose={() => setContentDrawerVisible(false)}
        value={currentValue}
        onChange={handleContentChange}
        handlePropsChange={handlePropsChange}
      />
    </>
  );
};

export default WbRichTextContentConfig;

registerConfigRenderer(WORKBENCH_CONFIG_TYPES.WB_RICH_TEXT_CONTENT, ({ handlePropsChange, item, configs }) => (
  <WbRichTextContentConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
