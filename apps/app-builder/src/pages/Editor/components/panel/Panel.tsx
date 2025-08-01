import MaterialCard from '@/components/MaterialCard';
import allTemplate from '@/components/Materials/template';
import { Collapse, Tabs } from '@arco-design/web-react';
import { IconBranch, IconLayout, IconSend, IconStorage } from '@arco-design/web-react/icon';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { ReactSortable } from 'react-sortablejs';
import { COMPONENT_GROUP_NAME } from '../const';
import styles from './index.module.less';

// 定义类型
const CATEGORY_KEYS = ['navigate', 'layout', 'form', 'list'] as const;
type CategoryKey = typeof CATEGORY_KEYS[number];

interface IProps {
    onDragComponents: (val: boolean) => void;
}
export default function EditorPanel(props: IProps) {
    const [activeTab, setActiveTab] = useState('form-design');
    const [activeComponentTab, setActiveComponentTab] = useState('base-component');
    const { t } = useTranslation();

    // 按 category 分类，分成 3 个 items
    const baseNavigateItems = allTemplate.base.find(cat => cat.category === 'navigate')?.items || [];
    const baseLayoutItems = allTemplate.base.find(cat => cat.category === 'layout')?.items || [];
    const baseFormItems = allTemplate.base.find(cat => cat.category === 'form')?.items || [];
    const baseListItems = allTemplate.base.find(cat => cat.category === 'list')?.items || [];

    // category 对应的国际化 key
    const categoryI18nMap: Record<CategoryKey, string> = {
        navigate: t('formEditor.navigate', '导航'),
        layout: t('formEditor.layout', '布局'),
        form: t('formEditor.form', '表单组件'),
        list: t('formEditor.list', '列表组件'),
    };

    const baseCategories: { key: CategoryKey, items: any[] }[] = [
        { key: 'navigate', items: baseNavigateItems },
        { key: 'layout', items: baseLayoutItems },
        { key: 'form', items: baseFormItems },
        { key: 'list', items: baseListItems },
    ];

    const [baseItems, setBaseItems] = useState<{ key: CategoryKey, items: any[] }[]>([]);

    useEffect(()=>{
        // 初始化，为每个组件分配配置和默认值
        const newBaseItems = baseCategories.map(cat => ({
            ...cat,
            items: cat.items.map(item => {
                const cpID = `${item.type}-${Date.now()}`;

                return {
                    type: item.type,
                    displayName: item.displayName,
                    id: cpID,
                }
            })
        }));
        setBaseItems(newBaseItems);
    }, []);

    return (
      <div className={styles.formEditorPanel}>
        <div className={styles.left}>
          <Tabs
              type="text"
              activeTab={activeTab}
              onChange={(key) => {
                setActiveTab(key);
              }}
              size="large"
              direction="vertical"
              inkBarSize={{ width: 20, height: 3 }}
          >
              <Tabs.TabPane key="form-design" title={<IconLayout fontSize={20}/>} />
              <Tabs.TabPane key="list-design" title={<IconBranch fontSize={20}/>} />
              <Tabs.TabPane key="page-setting" title={<IconSend fontSize={20}/>} />
              <Tabs.TabPane key="metadata-manage" title={<IconStorage fontSize={20}/>} />
          </Tabs>
        </div>

        <div className={styles.right}>
            <div className={styles.rightHeader}>
                {t('formEditor.component')}
            </div>

            <div className={styles.rightBody}>
                <div className={styles.componentTabs}>
                    <Tabs
                        type="capsule"
                        activeTab={activeComponentTab}
                        onChange={(key) => {
                            setActiveComponentTab(key);
                        }}
                        size='default'
                    >
                        <Tabs.TabPane
                            key="base-component"
                            title={<div className={styles.componentTabTitle}>{t('formEditor.baseComponent')}</div>}
                        />
                        <Tabs.TabPane key="template-component"
                            title={<div className={styles.componentTabTitle}>{t('formEditor.templateComponent')}</div>}
                        />
                        <Tabs.TabPane key="custom-component"
                            title={<div className={styles.componentTabTitle}>{t('formEditor.customComponent')}</div>}
                        />
                    </Tabs>
                </div>
                <div className={styles.componentList}>
                    {activeComponentTab === 'base-component' && (
                        <Collapse defaultActiveKey={baseCategories.map(c => c.key)}
                            accordion={false}
                            bordered={false}
                        >
                            {baseCategories.map(cat => (
                                <Collapse.Item
                                    header={categoryI18nMap[cat.key]} name={cat.key} key={cat.key}
                                    style={{ border: 'none' }}
                                    contentStyle={{ backgroundColor: '#fff', border: 'none' }}
                                >
                                    <div>
                                    {cat.items.length === 0
                                        ?
                                        <div className={styles.emptyTip}>
                                            {t('formEditor.empty')}
                                        </div>
                                        :
                                        <ReactSortable
                                            list={baseItems.find(c => c.key === cat.key)?.items || []}
                                            setList={()=>{}}
                                            group={{
                                                name: COMPONENT_GROUP_NAME,
                                                pull: "clone",
                                                put: false,
                                            }}
                                            sort={false}
                                            className={styles.componentCollapseContent}
                                            forceFallback={true}
                                            animation={150}
                                            onClone={(e)=>{
                                                // console.log("onClone", e);

                                                // 每次拖拽组件到面板时重新分配ID
                                                const cpType = e.item.getAttribute('data-cp-type')
                                                e.item.id = `${cpType}-${Date.now()}`;

                                                const newBaseItems = baseItems.map(c => ({
                                                    ...c,
                                                    items: c.items.map(item => ({ ...item, id: `${item.type}-${Date.now()}` }))
                                                }));
                                                setBaseItems(newBaseItems);
                                            }}
                                            onMove={(e) => {
                                                // 若拖拽目标是编辑区（蒙版所在容器），则隐藏蒙版
                                                if (e.to?.id === 'workspace-content') {
                                                    props.onDragComponents(false);
                                                }
                                                return e.to !== e.from;
                                            }}
                                        >
                                            {
                                                cat.items.map(item => (
                                                    <MaterialCard
                                                        key={item.type}
                                                        id={item.id || `${item.type}-${Date.now()}`}
                                                        displayName={item.displayName}
                                                        type={item.type}
                                                        icon={item.icon}
                                                    />
                                                ))
                                            }
                                        </ReactSortable>
                                    }
                                    </div>
                                </Collapse.Item>
                            ))}
                        </Collapse>
                    )}
                </div>
            </div>
        </div>
      </div>
    );
}