import AppIcon from '@/assets/images/app_icon.svg';
import activeFormDesignSVG from '@/assets/images/form_design_active_icon.svg';
import defaultFormDesignSVG from '@/assets/images/form_design_default_icon.svg';
import activeListDesignSVG from '@/assets/images/list_design_active_icon.svg';
import defaultListDesignSVG from '@/assets/images/list_design_default_icon.svg';
import { COMPONENT_TYPE_DISPLAY_NAME_MAP } from '@/components/Materials/template';
import { usePageEditorStore } from '@/hooks/useStore';
import { useAppStore, useBasicEditorStore, useFromEditorStore, useListEditorStore } from '@/store';
import { Button, Message, Tabs } from '@arco-design/web-react';
import { IconArrowLeft } from '@arco-design/web-react/icon';
import {
  loadPageSet,
  savePageSet,
  type ComponentConfig,
  type LoadPageSetReq,
  type PageSet,
  type SavePageSetReq
} from '@onebase/app';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { EDITOR_TYPES } from '../const';
import styles from './index.module.less';

const tabData = [
  {
    key: EDITOR_TYPES.FORM_EDITOR,
    title: '表单设计',
    alt: 'Form Design',
    defaultIcon: defaultFormDesignSVG,
    activeIcon: activeFormDesignSVG
  },
  {
    key: EDITOR_TYPES.LIST_EDITOR,
    title: '列表设计',
    alt: 'List Design',
    defaultIcon: defaultListDesignSVG,
    activeIcon: activeListDesignSVG
  },
  {
    key: EDITOR_TYPES.PAGE_SETTING,
    title: '页面设置',
    alt: ''
  }
  // {
  //     key: EDITOR_TYPES.METADATA_MANAGE,
  //     title: "元数据管理",
  //     alt: "Source Data",
  //     defaultIcon: defaultSourceDataSVG,
  //     activeIcon: activeSourceDataSVG,
  // },
];

export default function EditorHeader() {
  const { clearCurComponentID } = usePageEditorStore();
  const { components: fromComponents, pageComponentSchemas: fromPageComponentSchemas } = useFromEditorStore();
  const { components: listComponents, pageComponentSchemas: listPageComponentSchemas } = useListEditorStore();

  const { curAppId } = useAppStore();

  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('');
  const [pageSetCode, setPageSetCode] = useState('');

  const { setComponents: setFromComponents, setPageComponentSchemas: setFromPageComponentSchemas } =
    useFromEditorStore();
  const { setComponents: setListComponents, setPageComponentSchemas: setListPageComponentSchemas } =
    useListEditorStore();
  const { isEditMode, setIsEditMode } = useBasicEditorStore();

  useEffect(() => {
    // 根据当前 URL 动态设置 activeTab
    const hash = window.location.hash;
    if (hash.includes(EDITOR_TYPES.FORM_EDITOR)) {
      setActiveTab(EDITOR_TYPES.FORM_EDITOR);
      console.log('FormEditor isEditMode: ', isEditMode);
    } else if (hash.includes(EDITOR_TYPES.LIST_EDITOR)) {
      setActiveTab(EDITOR_TYPES.LIST_EDITOR);
      console.log('ListEditor isEditMode: ', isEditMode);
    } else if (hash.includes(EDITOR_TYPES.PAGE_SETTING)) {
      setActiveTab(EDITOR_TYPES.PAGE_SETTING);
    } else if (hash.includes(EDITOR_TYPES.METADATA_MANAGE)) {
      setActiveTab(EDITOR_TYPES.METADATA_MANAGE);
    }
  }, []);

  useEffect(() => {
    const hash = window.location.hash;
    const queryIndex = hash.indexOf('?');
    if (queryIndex !== -1) {
      const queryString = hash.substring(queryIndex + 1);
      const params = new URLSearchParams(queryString);
      const pSetCode = params.get('pageSetCode') || '';

      setPageSetCode(pSetCode);
    }
  }, []);

  useEffect(() => {
    if (!isEditMode && pageSetCode != '') {
      console.log(pageSetCode);
      loadPageSetInfo(pageSetCode);
      setIsEditMode(true);
    }
  }, [pageSetCode]);

  const loadPageSetInfo = async (pgsetCode: string) => {
    // 获取页面配置
    console.log(pgsetCode);

    const loadPageSetReq: LoadPageSetReq = {
      pageSetCode: pgsetCode
    };
    const pageSet = await loadPageSet(loadPageSetReq);
    console.log('res: ', pageSet);
    pageSet.pages.forEach((page: PageSet) => {
      let newComponents: any[] = [];
      let newPageComponentSchemas = new Map<string, any>();
      page.components.forEach((component: ComponentConfig) => {
        newComponents.push({
          id: component.componentCode,
          chosen: false,
          selected: false,
          type: component.componentType,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[component.componentType] || ''
        });
        newPageComponentSchemas.set(component.componentCode, {
          config: JSON.parse(component.config),
          editData: JSON.parse(component.editData)
        });
      });

      if (page.pageType === 'form') {
        setFromComponents(newComponents);
        newPageComponentSchemas.forEach((config, componentId) => {
          setFromPageComponentSchemas(componentId, config);
        });
      } else if (page.pageType === 'list') {
        setListComponents(newComponents);
        newPageComponentSchemas.forEach((config, componentId) => {
          setListPageComponentSchemas(componentId, config);
        });
      }
    });
  };

  const handleSaveApp = async () => {
    console.log(`save appid: ${curAppId}, pageSetCode: ${pageSetCode}`);

    console.log(fromComponents);
    console.log(fromPageComponentSchemas);

    console.log(listComponents);
    console.log(listPageComponentSchemas);

    const loadPageSetReq: LoadPageSetReq = {
      pageSetCode: pageSetCode
    };
    const loadPagesetResp = await loadPageSet(loadPageSetReq);
    console.log('res: ', loadPagesetResp);

    loadPagesetResp.pages.forEach((_page: PageSet, index: number) => {
      if (_page.pageType === 'form') {
        loadPagesetResp.pages[index].components = fromComponents.map((component) => {
          return {
            componentCode: component.id,
            componentType: component.type,
            config: JSON.stringify(fromPageComponentSchemas.get(component.id)?.config),
            editData: JSON.stringify(fromPageComponentSchemas.get(component.id)?.editData)
          };
        });
      } else if (_page.pageType === 'list') {
        loadPagesetResp.pages[index].components = listComponents.map((component) => {
          return {
            componentCode: component.id,
            componentType: component.type,
            config: JSON.stringify(listPageComponentSchemas.get(component.id)?.config),
            editData: JSON.stringify(listPageComponentSchemas.get(component.id)?.editData)
          };
        });
      }
    });

    console.log(loadPagesetResp);

    const savePageSetReq: SavePageSetReq = {
      pageSetCode: pageSetCode,
      pageSetName: '123',
      pages: loadPagesetResp.pages
    };
    const res = await savePageSet(savePageSetReq);
    console.log('res: ', res);
    if (res) {
      Message.success('保存成功');
    }
  };

  return (
    <div className={styles.editorHeader}>
      {/* 左侧 */}
      <div className={styles.left}>
        <Button
          shape="circle"
          type="default"
          size="small"
          onClick={() => {
            navigate(`/onebase/create-app/page-manager?appId=${curAppId}`);
          }}
          icon={<IconArrowLeft />}
        />

        <img src={AppIcon} style={{ width: 28, height: 28 }} />

        <span>新应用</span>
        <span>&gt;</span>
        <span>页面一</span>
      </div>

      {/* 中间 */}
      <div className={styles.center}>
        <Tabs
          type="line"
          activeTab={activeTab}
          onChange={(key) => {
            setActiveTab(key);
            clearCurComponentID();
            switch (key) {
              case EDITOR_TYPES.FORM_EDITOR:
                navigate(`/onebase/editor/${EDITOR_TYPES.FORM_EDITOR}?pageSetCode=${pageSetCode}`);
                break;
              case EDITOR_TYPES.LIST_EDITOR:
                navigate(`/onebase/editor/${EDITOR_TYPES.LIST_EDITOR}?pageSetCode=${pageSetCode}`);
                break;
              case EDITOR_TYPES.PAGE_SETTING:
                navigate(`/onebase/editor/${EDITOR_TYPES.PAGE_SETTING}?pageSetCode=${pageSetCode}`);
                break;
              case EDITOR_TYPES.METADATA_MANAGE:
                navigate(`/onebase/editor/${EDITOR_TYPES.METADATA_MANAGE}?pageSetCode=${pageSetCode}`);
                break;
              default:
                break;
            }
          }}
          size="large"
        >
          {tabData.map((tab) => (
            <Tabs.TabPane
              key={tab.key}
              title={
                <div className={styles.tabIcon}>
                  <img src={tab.key === activeTab ? tab.activeIcon : tab.defaultIcon} alt={tab.alt} />
                  {tab.title}
                </div>
              }
            />
          ))}
        </Tabs>
      </div>

      <div className={styles.right}>
        <div className={styles.editorStatus}>已保存，未发布</div>
        <Button
          onClick={() => {
            /* 预览逻辑 */
          }}
        >
          预览
        </Button>
        <Button
          type="primary"
          onClick={() => {
            handleSaveApp();
          }}
        >
          保存
        </Button>
      </div>
    </div>
  );
}
