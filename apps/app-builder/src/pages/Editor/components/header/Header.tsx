import activeFormDesignSVG from '@/assets/images/form_design_active_icon.svg';
import defaultFormDesignSVG from '@/assets/images/form_design_default_icon.svg';
import activeListDesignSVG from '@/assets/images/list_design_active_icon.svg';
import defaultListDesignSVG from '@/assets/images/list_design_default_icon.svg';
import activePageSettingSVG from '@/assets/images/page_setting_active_icon.svg';
import defaultPageSettingSVG from '@/assets/images/page_setting_default_icon.svg';
import editPageNameSVG from '@/assets/images/edit_page_name_icon.svg';
import previewSVG from '@/assets/images/preview_icon.svg';
import { useI18n } from '@/hooks/useI18n';
import { useBasicEditorStore } from '@/store';
import { useAppStore } from '@/store/store_app';
import { useAppEntityStore } from '@/store/store_entity';
import { Breadcrumb, Button, Message, Tabs, Form } from '@arco-design/web-react';
import { IconArrowLeft } from '@arco-design/web-react/icon';
import RenameModal from '@/pages/CreateApp/pages/PageManager/components/Modals/RenameModal';

import {
  AppStatus,
  ENTITY_TYPE,
  getAppIdByPageSetId,
  getApplication,
  getEntityFieldsWithChildren,
  getPageSetMetaData,
  updateApplicationMenu,
  type ChildEntity,
  type GetApplicationReq,
  type UpdateApplicationMenuNameReq
} from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import {
  EDITOR_TYPES,
  startLoadPageSet,
  startSavePageSet,
  useFormEditorSignal,
  useListEditorSignal,
  usePageEditorSignal,
  type SavePageSetParams
} from '@onebase/ui-kit';
import { cloneDeep } from 'lodash-es';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PartPreview from '../partPreview';
import styles from './index.module.less';

const BreadcrumbItem = Breadcrumb.Item;

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
    alt: 'Page Setting',
    defaultIcon: defaultPageSettingSVG,
    activeIcon: activePageSettingSVG
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
  const [renameForm] = Form.useForm();

  const { clearCurComponentID } = usePageEditorSignal();

  const { isEditMode, setIsEditMode } = useBasicEditorStore();

  const {
    components: formComponents,
    pageComponentSchemas: formPageComponentSchemas,
    clearComponents: clearFormComponents,
    clearPageComponentSchemas: clearFromPageComponentSchemas,
    layoutSubComponents: fromLayoutSubComponents,
    clearLayoutSubComponents: clearFromLayoutSubComponents
  } = useFormEditorSignal;

  const {
    components: listComponents,
    pageComponentSchemas: listPageComponentSchemas,
    clearComponents: clearListComponents,
    clearPageComponentSchemas: clearListPageComponentSchemas,
    layoutSubComponents: listLayoutSubComponents,
    clearLayoutSubComponents: clearListLayoutSubComponents
  } = useListEditorSignal;

  const { setMainEntity, /* setAppEntities, */ setSubEntities } = useAppEntityStore();

  const { curAppId, setCurAppId } = useAppStore();

  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('');
  const [pageSetId, setPageSetId] = useState('');

  const [appName, setAppName] = useState('未命名应用');
  const [appIcon, setAppIcon] = useState('');
  const [iconColor, setIconColor] = useState('');
  const [appStatus, setAppStatus] = useState(0);

  // 重命名弹窗
  const [visibleRenameForm, setVisibleRenameForm] = useState(false);

  const [partPreviewVisible, setPartPreviewVisible] = useState(false);

  const sessionData = sessionStorage.getItem('EDITOR_PAGE_INFO') || '{}';
  const pageInfo = JSON.parse(sessionData);

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
    const pageSetId = getHashQueryParam('pageSetId');
    if (pageSetId) {
      setPageSetId(pageSetId);
    }
  }, []);

  useEffect(() => {
    if (pageInfo) {
      renameForm.setFieldsValue({
        menuId: pageInfo.id,
        menuName: pageInfo.name,
        menuIcon: pageInfo.icon
      });
    }
  }, [pageInfo]);

  useEffect(() => {
    if (!isEditMode && pageSetId != '') {
      loadPageSetInfo(pageSetId);
      setIsEditMode(true);
      handleGetAppInfo(pageSetId);
      getMainMetaData(pageSetId);
    }
  }, [pageSetId]);

  const loadPageSetInfo = async (pagesetId: string) => {
    startLoadPageSet({ pageSetId: pagesetId });
  };

  const handleGetAppInfo = async (pdId: string) => {
    const appId = await getAppIdByPageSetId({ pageSetId: pdId });
    setCurAppId(appId);

    const appReq: GetApplicationReq = {
      id: appId
    };

    const appResp = await getApplication(appReq);
    if (appResp) {
      if (appResp.iconName) {
        setAppIcon(appResp.iconName);
      }
      if (appResp.iconColor) {
        setIconColor(appResp.iconColor);
      }
      if (appResp.appName) {
        setAppName(appResp.appName);
      }
      if (appResp.appStatusText) {
        setAppStatus(appResp.appStatus);
      }
    }
    console.log('appResp: ', appResp);

    // handleGetAppEntities(appId);
  };

  // 获取主表对应的主实体信息
  const getMainMetaData = async (pageSetId: string) => {
    const mainMetaData = await getPageSetMetaData({ pageSetId: pageSetId });
    console.log('mainMetaData: ', mainMetaData);

    const entityWithChildren = await getEntityFieldsWithChildren(mainMetaData);

    if (entityWithChildren) {
      setMainEntity({
        entityID: entityWithChildren.entityId,
        entityName: entityWithChildren.entityName,
        entityType: ENTITY_TYPE.MAIN,
        fields: entityWithChildren.parentFields
      });

      if (entityWithChildren.childEntities && entityWithChildren.childEntities.length > 0) {
        const subEntities = entityWithChildren.childEntities.map((entity: ChildEntity) => ({
          entityID: entity.childEntityId,
          entityName: entity.childEntityName,
          entityType: ENTITY_TYPE.SUB,
          fields: entity.childFields
        }));

        setSubEntities({
          entities: subEntities
        });
      }
    }
  };

  //   const handleGetAppEntities = async (appId: string) => {
  //     const res = await getAppEntities(appId);
  //     console.log('appEntities: ', res);
  //     if (res) {
  //       setAppEntities(res.entities);
  //       const mainEntity = res.entities.filter(
  //         (entity: AppEntity) => entity.entityType === ENTITY_TYPE.MAIN || entity.entityType === ENTITY_TYPE.INDEP
  //       );
  //       if (mainEntity.length > 0) {
  //         setMainEntity(mainEntity[0]);
  //       }
  //       const subEntities = res.entities.filter((entity: AppEntity) => entity.entityType === ENTITY_TYPE.SUB);
  //       if (subEntities.length > 0) {
  //         setSubEntities({ entities: subEntities });
  //       }
  //     }
  //     return res;
  //   };

  const handleSavePageSet = async () => {
    console.log(`save appid: ${curAppId}, pageSetId: ${pageSetId}`);

    const savePageSetParams: SavePageSetParams = {
      pageSetId: pageSetId,
      formComponents: formComponents.value,
      listComponents: listComponents.value,
      formPageComponentSchemas: new Map(Object.entries(cloneDeep(formPageComponentSchemas.value))),
      listPageComponentSchemas: new Map(Object.entries(cloneDeep(listPageComponentSchemas.value))),
      fromColComponentsMap: { colComponents: new Map(Object.entries(cloneDeep(fromLayoutSubComponents.value))) },
      listColComponentsMap: { colComponents: new Map(Object.entries(cloneDeep(listLayoutSubComponents.value))) }
    };

    startSavePageSet(savePageSetParams);
  };

  const clearAllData = () => {
    clearFromLayoutSubComponents();
    clearListLayoutSubComponents();
    clearFormComponents();
    clearListComponents();
    clearFromPageComponentSchemas();
    clearListPageComponentSchemas();
  };

  const backToPageManager = async () => {
    const appId = await getAppIdByPageSetId({ pageSetId: pageSetId });
    if (!appId) {
      Message.error('获取应用ID失败');
      return;
    }

    clearAllData();

    navigate(`/onebase/create-app/page-manager?appId=${appId}`);
  };

  const toPreview = () => {
    setPartPreviewVisible(true);
  };

  const handleRename = async () => {
    if (!renameForm.getFieldValue('menuId')) {
      Message.error('请选择要重命名的菜单');
      return;
    }
    const id = renameForm.getFieldValue('menuID');
    const menuName = renameForm.getFieldValue('menuName');
    const menuIcon = renameForm.getFieldValue('menuIcon');

    const req: UpdateApplicationMenuNameReq = {
      id,
      menuName,
      menuIcon
    };
    const res = await updateApplicationMenu(req);
    if (res) {
      Message.success('重命名成功');
      sessionStorage.setItem('EDITOR_PAGE_INFO', JSON.stringify({ ...pageInfo, name: menuName, icon: menuIcon }));
    }
    setVisibleRenameForm(false);
  };

  return (
    <div className={styles.editorHeader}>
      {/* 左侧 */}
      <div className={styles.left}>
        <Button shape="square" type="default" size="small" onClick={backToPageManager} icon={<IconArrowLeft />} />

        <div className={styles.myAppIcon} style={{ backgroundColor: iconColor }}>
          <i className={`iconfont ${appIcon || 'icon-box'}`} />
        </div>

        <Breadcrumb>
          <BreadcrumbItem className={styles.appName}>{appName}</BreadcrumbItem>
          <BreadcrumbItem className={styles.pageName}>
            {pageInfo?.name || '未命名页面'}
            <div className={styles.editIcon} onClick={() => setVisibleRenameForm(true)}>
              <img src={editPageNameSVG} alt="edit page name" />
            </div>
          </BreadcrumbItem>
        </Breadcrumb>
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
                navigate(`/onebase/editor/${EDITOR_TYPES.FORM_EDITOR}?pageSetId=${pageSetId}`);
                break;
              case EDITOR_TYPES.LIST_EDITOR:
                navigate(`/onebase/editor/${EDITOR_TYPES.LIST_EDITOR}?pageSetId=${pageSetId}`);
                break;
              case EDITOR_TYPES.PAGE_SETTING:
                navigate(`/onebase/editor/${EDITOR_TYPES.PAGE_SETTING}?pageSetId=${pageSetId}`);
                break;
              case EDITOR_TYPES.METADATA_MANAGE:
                navigate(`/onebase/editor/${EDITOR_TYPES.METADATA_MANAGE}?pageSetId=${pageSetId}`);
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
        {appStatus === AppStatus.DEVELOPING && <div className={styles.editorStatusDeveloping}>未保存</div>}
        {appStatus === AppStatus.PUBLISHED && <div className={styles.editorStatusPublished}>已保存</div>}
        {appStatus === AppStatus.EDITING_AFTER_PUBLISH && (
          <div className={styles.editorStatusEditAfterPublished}>未保存</div>
        )}

        <Button onClick={toPreview} className={styles.previewButton}>
          <img src={previewSVG} />
          {t('editor.preview')}
        </Button>
        <Button
          type="primary"
          onClick={() => {
            handleSavePageSet();
          }}
        >
          保存
        </Button>

        <PartPreview
          pageType={activeTab}
          visible={partPreviewVisible}
          setVisible={() => setPartPreviewVisible(false)}
        />
      </div>

      {/* 重命名弹窗 */}
      <RenameModal
        title={'重命名'}
        visible={visibleRenameForm}
        handleRename={handleRename}
        setVisible={setVisibleRenameForm}
        form={renameForm}
      />
    </div>
  );
}
