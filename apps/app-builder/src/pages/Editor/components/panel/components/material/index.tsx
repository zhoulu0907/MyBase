import IconCollapsedDown from '@/assets/images/collapse_down_icon.svg';
import IconCollapsed from '@/assets/images/collapsed_left_icon.svg';
import IconSearchForm from '@/assets/images/search_form_icon.svg';
import MaterialCard from '@/components/MaterialCard';
import { useI18n } from '@/hooks/useI18n';
import { useSignals } from '@preact/signals-react/runtime';
import { Collapse, Input, Layout, Tabs } from '@arco-design/web-react';
import { CATEGORY_TYPE } from '@onebase/app';
import { buildTemplate, loadMaterialsPlugin, COMPONENT_GROUP_NAME, EDITOR_TYPES, type EditorType, listPluginComponentTypes, COMPONENT_REGISTRY } from '@onebase/ui-kit';
import plugin from 'ob-plugin-template/src/index';

loadMaterialsPlugin({
  id: plugin.meta.name,
  components: [
    {
      type: (plugin.components as any).PluginInputText.type,
      schema: (plugin.components as any).PluginInputText.schema,
      template: { ...(plugin.components as any).PluginInputText.template, category: 'form' },
      fieldMap: (plugin.components as any).PluginInputText.fieldMap,
      entityMap: (plugin.components as any).PluginInputText.entityMap,
      component: (plugin.components as any).PluginInputText.component
    }
  ]
});
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { v4 as uuidv4 } from 'uuid';
import styles from './index.module.less';

const Sider = Layout.Sider;
const InputSearch = Input.Search;

// 定义类型
const CATEGORY_KEYS = [
  CATEGORY_TYPE.NAVIGATE,
  CATEGORY_TYPE.LAYOUT,
  CATEGORY_TYPE.FORM,
  CATEGORY_TYPE.LIST,
  CATEGORY_TYPE.SHOW
] as const;
type CategoryKey = (typeof CATEGORY_KEYS)[number];

interface MaterialContainerProps {
  activeTab: EditorType;
  childCollapsed: string | undefined;
  setChildCollapsed: () => void;
}

const MaterialContainer: React.FC<MaterialContainerProps> = ({ activeTab, childCollapsed, setChildCollapsed }) => {
  useSignals();
  const { t } = useI18n();

  const [activeComponentTab, setActiveComponentTab] = useState('base-component');

  const [baseItems, setBaseItems] = useState<{ key: CategoryKey; items: any[] }[]>([]);
  const [customItems, setCustomItems] = useState<any[]>([]);
  const [showSearchInput, setShowSearchInput] = useState<boolean>(false);
  const [keyword, setKeyword] = useState<string>(''); // 搜索关键词
  const [components, setComponents] = useState<{ key: CategoryKey; items: any[] }[]>([]); // 关键词过滤后的组件

  // 按 category 分类，分成 3 个 items
  //   const baseNavigateItems = allTemplate.base.find((cat) => cat.category === CATEGORY_TYPE.NAVIGATE)?.items || [];
  const template = buildTemplate();
  console.log('MaterialContainer template:', template);
  const baseLayoutItems = template.base.find((cat: any) => cat.category === CATEGORY_TYPE.LAYOUT)?.items || [];
  const baseFormItems = template.base.find((cat: any) => cat.category === CATEGORY_TYPE.FORM)?.items || [];
  const baseListItems = template.base.find((cat: any) => cat.category === CATEGORY_TYPE.LIST)?.items || [];
  const baseShowItems = template.base.find((cat: any) => cat.category === CATEGORY_TYPE.SHOW)?.items || [];

  // category 对应的国际化 key
  const categoryI18nMap: Record<CategoryKey, string> = {
    navigate: t('editor.navigate', '导航'),
    layout: t('editor.layout', '布局'),
    form: t('editor.form', '表单组件'),
    list: t('editor.list', '列表组件'),
    show: t('editor.show', '展示组件')
  };

  const baseCategories: { key: CategoryKey; items: any[] }[] = [
    // { key: CATEGORY_TYPE.NAVIGATE, items: baseNavigateItems },
    { key: CATEGORY_TYPE.LAYOUT, items: baseLayoutItems },
    { key: CATEGORY_TYPE.FORM, items: baseFormItems },
    { key: CATEGORY_TYPE.LIST, items: baseListItems },
    { key: CATEGORY_TYPE.SHOW, items: baseShowItems }
  ];

  const templateStr = JSON.stringify(baseCategories);

  useEffect(() => {
    const lowerKeyword = keyword.toLowerCase();

    const newBaseItems = baseCategories
      .map((cat) => {
        // 对每个分类的 items 先过滤，再映射
        const filteredItems = cat.items
          .filter((item) => item.displayName?.toLowerCase().includes(lowerKeyword))
          .map((item) => {
            const cpID = `${item.type}-${uuidv4()}`;
            return {
              type: item.type,
              displayName: item.displayName,
              id: cpID,
              icon: item.icon
            };
          });

        return {
          ...cat,
          items: filteredItems
        };
      })
      .filter((cat) => cat.items.length > 0); // 去掉空的分类

    setBaseItems(newBaseItems);

    // 处理自定义组件
    const pluginTypes = listPluginComponentTypes();
    const newCustomItems = pluginTypes
      .map((type) => {
        const desc = COMPONENT_REGISTRY[type];
        if (!desc) return null;
        if (keyword && !desc.template.displayName.toLowerCase().includes(lowerKeyword)) return null;
        return {
          type: desc.type,
          displayName: desc.template.displayName,
          id: `${desc.type}-${uuidv4()}`,
          icon: desc.template.icon
        };
      })
      .filter((item) => item !== null);
    setCustomItems(newCustomItems);
  }, [keyword, templateStr]);

  useEffect(() => {
    if (!keyword) return setComponents(baseCategories); // 没关键词直接返回原数据

    const lowerKeyword = keyword.toLowerCase();
    const filterData = baseCategories
      .map((category) => {
        // 过滤 items
        const filteredItems = category.items.filter(
          (item) => item.displayName && item.displayName.includes(lowerKeyword)
        );
        return { ...category, items: filteredItems };
      })
      .filter((category) => category.items.length > 0); // 移除没有匹配项的分类

    setComponents(filterData);
  }, [keyword, templateStr]);

  return (
    <div>
      <Sider collapsed={!childCollapsed} collapsible collapsedWidth={0} trigger={null} width={270}>
        <div className={styles.rightHeader}>
          <div className={styles.title}>{t('editor.material')}</div>

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
            <div className={styles.collapse} onClick={setChildCollapsed}>
              <img src={IconCollapsed} alt="collapse" />
            </div>
          </div>
        </div>

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
              <Collapse
                defaultActiveKey={baseCategories.map((c) => c.key)}
                accordion={false}
                bordered={false}
                expandIconPosition="right"
                expandIcon={<img src={IconCollapsedDown} alt="" />}
              >
                {components.map((cat) => {
                  if (activeTab === EDITOR_TYPES.LIST_EDITOR && cat.key === CATEGORY_TYPE.FORM) {
                    return null;
                  }
                  if (activeTab === EDITOR_TYPES.FORM_EDITOR && cat.key === CATEGORY_TYPE.LIST) {
                    return null;
                  }

                  return (
                    <Collapse.Item
                      header={categoryI18nMap[cat.key]}
                      name={cat.key}
                      key={cat.key}
                      style={{ border: 'none' }}
                      contentStyle={{ backgroundColor: '#fff', border: 'none', paddingLeft: 13 }}
                    >
                      <div>
                        {cat.items.length === 0 ? (
                          <div className={styles.emptyTip}>{t('editor.empty')}</div>
                        ) : (
                          <ReactSortable
                            list={baseItems.find((c) => c.key === cat.key)?.items || []}
                            setList={() => { }}
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

            {activeComponentTab === 'custom-component' && (
              <div style={{ padding: '0 12px' }}>
                {customItems.length === 0 ? (
                  <div className={styles.emptyTip}>{t('editor.empty')}</div>
                ) : (
                  <ReactSortable
                    list={customItems}
                    setList={() => { }}
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
                      const cpType = e.item.getAttribute('data-cp-type');
                      e.item.id = `${cpType}-${uuidv4()}`;
                      setCustomItems((prev) =>
                        prev.map((item) =>
                          item.type === cpType ? { ...item, id: `${e.item.id}` } : item
                        )
                      );
                    }}
                  >
                    {customItems.map((item) => (
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
            )}
          </div>
        </div>
      </Sider>
    </div>
  );
};

export default MaterialContainer;
