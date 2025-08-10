import AppIcon from '@/assets/images/app_icon.svg';
import activeFormDesignSVG from '@/assets/images/form_design_active_icon.svg';
import defaultFormDesignSVG from '@/assets/images/form_design_default_icon.svg';
import activeListDesignSVG from '@/assets/images/list_design_active_icon.svg';
import defaultListDesignSVG from '@/assets/images/list_design_default_icon.svg';
import { useI18n } from '@/hooks/useI18n';
import { usePageEditorStore } from '@/hooks/useStore';
import { useAppStore, useBasicEditorStore, useFromEditorStore, useListEditorStore } from '@/store';
import { Button, Message, Tabs } from '@arco-design/web-react';
import { IconArrowLeft } from '@arco-design/web-react/icon';
import { getAppIdByPageSetCode } from '@onebase/app';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { startLoadPageSet, startSavePageSet, type SavePageSetParams } from '../../utils/app_resource';
import { EDITOR_TYPES } from '../../utils/const';
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
  const { t } = useI18n();
  const { clearCurComponentID } = usePageEditorStore();
  const {
    components: fromComponents,
    pageComponentSchemas: fromPageComponentSchemas,
    colComponentsMap: fromColComponentsMap,
    clearColComponentsMap: clearFromColComponentsMap,
    clearComponents: clearFromComponents,
    clearPageComponentSchemas: clearFromPageComponentSchemas
  } = useFromEditorStore();
  const {
    components: listComponents,
    pageComponentSchemas: listPageComponentSchemas,
    colComponentsMap: listColComponentsMap,
    clearColComponentsMap: clearListColComponentsMap,
    clearComponents: clearListComponents,
    clearPageComponentSchemas: clearListPageComponentSchemas
  } = useListEditorStore();

  const { curAppId } = useAppStore();

  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('');
  const [pageSetCode, setPageSetCode] = useState('');

  const {
    setComponents: setFromComponents,
    setPageComponentSchemas: setFromPageComponentSchemas,
    setColComponentsMap: setFromColComponentsMap
  } = useFromEditorStore();
  const {
    setComponents: setListComponents,
    setPageComponentSchemas: setListPageComponentSchemas,
    setColComponentsMap: setListColComponentsMap
  } = useListEditorStore();
  const { isEditMode, setIsEditMode } = useBasicEditorStore();

  useEffect(() => {
    // 根据当前 URL 动态设置 activeTab
    const hash = window.location.hash;
    if (hash.includes(EDITOR_TYPES.FORM_EDITOR)) {
      setActiveTab(EDITOR_TYPES.FORM_EDITOR);
    } else if (hash.includes(EDITOR_TYPES.LIST_EDITOR)) {
      setActiveTab(EDITOR_TYPES.LIST_EDITOR);
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
      loadPageSetInfo(pageSetCode);
      setIsEditMode(true);
    }
  }, [pageSetCode]);

  const loadPageSetInfo = async (pgsetCode: string) => {
    startLoadPageSet({
      pageSetCode: pgsetCode,
      setFromComponents: setFromComponents,
      setFromPageComponentSchemas: setFromPageComponentSchemas,
      setListComponents: setListComponents,
      setListPageComponentSchemas: setListPageComponentSchemas,
      setFromColComponentsMap: setFromColComponentsMap,
      setListColComponentsMap: setListColComponentsMap
    });
  };

  const handleSavePageSet = async () => {
    console.log(`save appid: ${curAppId}, pageSetCode: ${pageSetCode}`);

    const savePageSetParams: SavePageSetParams = {
      pageSetCode: pageSetCode,
      fromComponents: fromComponents,
      listComponents: listComponents,
      fromPageComponentSchemas: fromPageComponentSchemas,
      listPageComponentSchemas: listPageComponentSchemas,
      fromColComponentsMap: fromColComponentsMap,
      listColComponentsMap: listColComponentsMap
    };

    startSavePageSet(savePageSetParams);
  };

  const clearAllData = () => {
    clearFromColComponentsMap();
    clearListColComponentsMap();
    clearFromComponents();
    clearListComponents();
    clearFromPageComponentSchemas();
    clearListPageComponentSchemas();
  };

  const backToPageManager = async () => {
    const appId = await getAppIdByPageSetCode({ code: pageSetCode });
    if (!appId) {
      Message.error('获取应用ID失败');
      return;
    }

    clearAllData();

    navigate(`/onebase/create-app/page-manager?appId=${appId}`);
  };

  const toPreview = () => {
    navigate(`/onebase/app/preview?pageSetCode=${pageSetCode}&pageType=${activeTab}`);
  };

  return (
    <div className={styles.editorHeader}>
      {/* 左侧 */}
      <div className={styles.left}>
        <Button shape="circle" type="default" size="small" onClick={backToPageManager} icon={<IconArrowLeft />} />

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
        <Button onClick={toPreview}>{t('editor.preview')}</Button>
        <Button
          type="primary"
          onClick={() => {
            handleSavePageSet();
          }}
        >
          保存
        </Button>
      </div>
    </div>
  );
}
