import { Form, Switch } from '@arco-design/web-react';
import { useWorkbenchSignal, isPageConfig, PAGE_CONFIG_TYPE } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useState } from 'react';
import styles from './index.module.less';

const FormItem = Form.Item;

/**
 * 页面配置组件
 */
const PageConfig = () => {
  useSignals();

  const { curComponentSchema, setCurComponentSchema, setWbComponentSchemas } = useWorkbenchSignal();

  const [pageConfig, setPageConfig] = useState({
    showHeader: true,
    showSidebar: true
  });

  useEffect(() => {
    // 使用工具函数判断是否为页面配置
    if (isPageConfig(curComponentSchema)) {
      const config = curComponentSchema.config || {};
      const newConfig = {
        showHeader: config.showHeader ?? true,
        showSidebar: config.showSidebar ?? true
      };

      // 只有当配置真正变化时才更新状态
      if (newConfig.showHeader !== pageConfig.showHeader || newConfig.showSidebar !== pageConfig.showSidebar) {
        setPageConfig(newConfig);
      }
    }
  }, [curComponentSchema, curComponentSchema?.config?.showHeader, curComponentSchema?.config?.showSidebar]);

  const handleChange = (key: string, value: boolean) => {
    const newPageConfig = { ...pageConfig, [key]: value };
    setPageConfig(newPageConfig);

    // 使用当前 schema 的 id
    const pageConfigId = curComponentSchema?.id || 'page-config';

    const newCurComponentSchema = {
      ...curComponentSchema,
      id: pageConfigId,
      type: PAGE_CONFIG_TYPE,
      config: {
        ...curComponentSchema?.config,
        ...newPageConfig
      },
      editData: curComponentSchema?.editData || {}
    };

    // 同时更新当前选中的 schema 和 wbComponentSchemas 中的页面配置
    setCurComponentSchema(newCurComponentSchema);
    setWbComponentSchemas(pageConfigId, newCurComponentSchema);
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
