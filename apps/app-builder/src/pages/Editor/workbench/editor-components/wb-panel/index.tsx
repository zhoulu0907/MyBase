import IconLayoutActive from '@/assets/images/edit_layout_active_icon.svg';
import IconLayout from '@/assets/images/edit_layout_icon.svg';
import IconCollapsed from '@/assets/images/collapsed_left_icon.svg';
import IconSearchForm from '@/assets/images/search_form_icon.svg';
import { Tabs, Tooltip, Input, Layout } from '@arco-design/web-react';
import { useState } from 'react';
import { WorkbenchPanelContent } from './workbench-panel-content';
import styles from './index.module.less';

const InputSearch = Input.Search;
const Sider = Layout.Sider;

/**
 * 工作台面板容器组件
 * 包含左侧标签页和右侧内容区域
 */
export default function WorkbenchPanel() {
  const [activeLeftTabKey, setActiveLeftTabKey] = useState<'material'>('material');
  const [childCollapsed, setChildCollapsed] = useState<'material' | undefined>('material');
  const [showSearchInput, setShowSearchInput] = useState<boolean>(false);
  const [keyword, setKeyword] = useState<string>('');

  return (
    <div
      className={styles.workbenchPanel}
      style={{
        transition: 'width 0.3s cubic-bezier(0.4, 0, 0.4, 1)'
      }}
    >
      <div className={styles.left}>
        <Tabs
          type="text"
          activeTab={activeLeftTabKey}
          onChange={(key) => {
            setActiveLeftTabKey(key as 'material');
          }}
          size="large"
          direction="vertical"
          onClickTab={(key: string) => {
            setChildCollapsed((prev) => (prev === key ? undefined : (key as 'material')));
          }}
        >
          <Tabs.TabPane
            key={'material'}
            title={
              <div className={styles.tabButton}>
                <Tooltip mini content="组件库" position="right">
                  <img
                    src={
                      childCollapsed === 'material' && activeLeftTabKey === 'material' ? IconLayoutActive : IconLayout
                    }
                  />
                </Tooltip>
              </div>
            }
          />
        </Tabs>
      </div>

      <div className={styles.right}>
        <Sider
          collapsed={!childCollapsed}
          collapsible
          collapsedWidth={0}
          trigger={null}
          width={270}
          className={styles.rightSider}
        >
          <div className={styles.rightHeader}>
            <div className={styles.title}>组件库</div>

            <div className={styles.right}>
              <div className={styles.search} onClick={() => setShowSearchInput(true)}>
                {!showSearchInput ? (
                  <img src={IconSearchForm} alt="search some component" />
                ) : (
                  <InputSearch
                    value={keyword}
                    autoFocus
                    allowClear
                    onBlur={() => setShowSearchInput(false)}
                    onChange={setKeyword}
                  />
                )}
              </div>
              <div className={styles.collapse} onClick={() => setChildCollapsed(undefined)}>
                <img src={IconCollapsed} alt="collapse" />
              </div>
            </div>
          </div>

          <div className={styles.rightBody}>
            <div className={styles.componentList}>
              <WorkbenchPanelContent keyword={keyword} />
            </div>
          </div>
        </Sider>
      </div>
    </div>
  );
}
