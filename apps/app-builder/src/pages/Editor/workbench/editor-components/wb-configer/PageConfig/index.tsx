import { Form, Switch } from '@arco-design/web-react';
import { usePageEditorSignal } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useState } from 'react';
import styles from './index.module.less';

const FormItem = Form.Item;

/**
 * 页面配置组件
 */
const PageConfig = () => {
  useSignals();

  const { curComponentSchema, setCurComponentSchema } = usePageEditorSignal();

  const [pageConfig, setPageConfig] = useState({
    showHeader: true,
    showSidebar: true
  });

  useEffect(() => {
    if (curComponentSchema?.type === 'page') {
      const config = curComponentSchema.config || {};
      setPageConfig({
        showHeader: config.showHeader ?? true,
        showSidebar: config.showSidebar ?? true
      });
    }
  }, [curComponentSchema]);

  const handleChange = (key: string, value: boolean) => {
    const newPageConfig = { ...pageConfig, [key]: value };
    setPageConfig(newPageConfig);

    const newCurComponentSchema = {
      ...curComponentSchema,
      type: 'page',
      config: {
        ...curComponentSchema?.config,
        ...newPageConfig
      }
    };

    setCurComponentSchema(newCurComponentSchema);
  };

  return (
    <div className={styles.pageConfig}>
      <div className={styles.pageConfigTitle}>布局配置</div>
      <Form autoComplete="off" layout="vertical">
        <FormItem
          label={
            <div style={{ textAlign: 'left' }}>
              <span>显示顶栏</span>
            </div>
          }
          labelCol={{ span: 21 }}
          wrapperCol={{ span: 1 }}
          layout="horizontal"
          className={styles.formItem}
        >
          <Switch
            size="small"
            checked={pageConfig.showHeader}
            onChange={(value) => {
              handleChange('showHeader', value);
            }}
          />
        </FormItem>
        <FormItem
          label={
            <div style={{ textAlign: 'left' }}>
              <span>显示侧边栏</span>
            </div>
          }
          labelCol={{ span: 21 }}
          wrapperCol={{ span: 1 }}
          layout="horizontal"
          className={styles.formItem}
        >
          <Switch
            size="small"
            checked={pageConfig.showSidebar}
            onChange={(value) => {
              handleChange('showSidebar', value);
            }}
          />
        </FormItem>
      </Form>
    </div>
  );
};

export default PageConfig;
