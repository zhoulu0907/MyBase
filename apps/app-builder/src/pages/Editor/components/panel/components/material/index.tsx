import MaterialCard from '@/components/MaterialCard';
import { useI18n } from '@/hooks/useI18n';
import { COMPONENT_GROUP_NAME, EDITOR_TYPES, type EditorType } from '@/pages/Editor/utils/const';
import { Collapse, Tabs } from '@arco-design/web-react';
import { allTemplate } from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { v4 as uuidv4 } from 'uuid';
import styles from './index.module.less';

// 定义类型
const CATEGORY_KEYS = ['navigate', 'layout', 'form', 'list', 'show'] as const;
type CategoryKey = (typeof CATEGORY_KEYS)[number];

interface MaterialContainerProps {
  activeTab: EditorType;
}

const MaterialContainer: React.FC<MaterialContainerProps> = ({ activeTab }) => {
  const { t } = useI18n();
  const [activeComponentTab, setActiveComponentTab] = useState('base-component');

  const [baseItems, setBaseItems] = useState<{ key: CategoryKey; items: any[] }[]>([]);

  // 按 category 分类，分成 3 个 items
  //   const baseNavigateItems = allTemplate.base.find((cat) => cat.category === 'navigate')?.items || [];
  const baseLayoutItems = allTemplate.base.find((cat) => cat.category === 'layout')?.items || [];
  const baseFormItems = allTemplate.base.find((cat) => cat.category === 'form')?.items || [];
  const baseListItems = allTemplate.base.find((cat) => cat.category === 'list')?.items || [];
  const baseShowItems = allTemplate.base.find((cat) => cat.category === 'show')?.items || [];

  // category 对应的国际化 key
  const categoryI18nMap: Record<CategoryKey, string> = {
    navigate: t('editor.navigate', '导航'),
    layout: t('editor.layout', '布局'),
    form: t('editor.form', '表单组件'),
    list: t('editor.list', '列表组件'),
    show: t('editor.show', '展示组件')
  };

  const baseCategories: { key: CategoryKey; items: any[] }[] = [
    // { key: 'navigate', items: baseNavigateItems },
    { key: 'layout', items: baseLayoutItems },
    { key: 'form', items: baseFormItems },
    { key: 'list', items: baseListItems },
    { key: 'show', items: baseShowItems }
  ];

  useEffect(() => {
    // 初始化，为每个组件分配配置和默认值
    const newBaseItems = baseCategories.map((cat) => ({
      ...cat,
      items: cat.items.map((item) => {
        const cpID = `${item.type}-${uuidv4()}`;

        return {
          type: item.type,
          displayName: item.displayName,
          id: cpID
        };
      })
    }));
    setBaseItems(newBaseItems);
  }, []);

  return (
    <div>
      <div className={styles.rightHeader}>{t('editor.material')}</div>

      <div className={styles.rightBody}>
        <div className={styles.componentTabs}>
          <Tabs
            type="capsule"
            activeTab={activeComponentTab}
            onChange={(key) => {
              setActiveComponentTab(key);
            }}
            size="default"
          >
            <Tabs.TabPane
              key="base-component"
              title={<div className={styles.componentTabTitle}>{t('editor.baseComponent')}</div>}
            />
            <Tabs.TabPane
              key="template-component"
              title={<div className={styles.componentTabTitle}>{t('editor.templateComponent')}</div>}
            />
            <Tabs.TabPane
              key="custom-component"
              title={<div className={styles.componentTabTitle}>{t('editor.customComponent')}</div>}
            />
          </Tabs>
        </div>
        <div className={styles.componentList}>
          {activeComponentTab === 'base-component' && (
            <Collapse defaultActiveKey={baseCategories.map((c) => c.key)} accordion={false} bordered={false}>
              {baseCategories.map((cat) => {
                if (activeTab === EDITOR_TYPES.LIST_EDITOR && cat.key === 'form') {
                  return null;
                }
                if (activeTab === EDITOR_TYPES.FORM_EDITOR && (cat.key === 'list' || cat.key === 'show')) {
                  return null;
                }

                return (
                  <Collapse.Item
                    header={categoryI18nMap[cat.key]}
                    name={cat.key}
                    key={cat.key}
                    style={{ border: 'none' }}
                    contentStyle={{ backgroundColor: '#fff', border: 'none' }}
                  >
                    <div>
                      {cat.items.length === 0 ? (
                        <div className={styles.emptyTip}>{t('editor.empty')}</div>
                      ) : (
                        <ReactSortable
                          list={baseItems.find((c) => c.key === cat.key)?.items || []}
                          setList={() => {}}
                          group={{
                            name: COMPONENT_GROUP_NAME,
                            pull: 'clone',
                            put: false
                          }}
                          sort={false}
                          className={styles.componentCollapseContent}
                          forceFallback={true}
                          animation={150}
                          onClone={(e) => {
                            // console.log('onClone', e);

                            // 每次拖拽组件到面板时重新分配ID
                            const cpType = e.item.getAttribute('data-cp-type');
                            e.item.id = `${cpType}-${uuidv4()}`;
                            console.log('e.item.id', e.item.id);

                            // 拖动前ID保持不变，在下次拖动后重新生成
                            setBaseItems((prev) =>
                              prev.map((c) =>
                                c.key === cat.key
                                  ? {
                                      ...c,
                                      items: c.items.map((item) =>
                                        item.type === cpType ? { ...item, id: `${e.item.id}` } : item
                                      )
                                    }
                                  : c
                              )
                            );
                          }}
                        >
                          {cat.items.map((item) => (
                            <MaterialCard
                              key={item.type}
                              id={item.id}
                              displayName={item.displayName}
                              type={item.type}
                              icon={item.icon}
                            />
                          ))}
                        </ReactSortable>
                      )}
                    </div>
                  </Collapse.Item>
                );
              })}
            </Collapse>
          )}
        </div>
      </div>
    </div>
  );
};

export default MaterialContainer;
