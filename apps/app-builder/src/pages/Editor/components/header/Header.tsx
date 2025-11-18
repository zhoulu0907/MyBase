import editPageNameSVG from '@/assets/images/edit_page_name_icon.svg';
import activeFormDesignSVG from '@/assets/images/form_design_active_icon.svg';
import defaultFormDesignSVG from '@/assets/images/form_design_default_icon.svg';
import activeListDesignSVG from '@/assets/images/list_design_active_icon.svg';
import defaultListDesignSVG from '@/assets/images/list_design_default_icon.svg';
import activePageSettingSVG from '@/assets/images/page_setting_active_icon.svg';
import defaultPageSettingSVG from '@/assets/images/page_setting_default_icon.svg';
import previewSVG from '@/assets/images/preview_icon.svg';
import { appIconMap, useAppEntityStore } from '@onebase/ui-kit';
import { getEntityFields } from '@onebase/app';
import DynamicIcon from '@/components/DynamicIcon';
import { useI18n } from '@/hooks/useI18n';
import RenameModal from '@/pages/CreateApp/pages/PageManager/components/Modals/RenameModal';
import VersionModal from '@/pages/CreateApp/pages/PageManager/components/Modals/VersionModal';
import { useBasicEditorStore } from '@/store';
import { useFlowEditorStor } from '@/store/index';
import { useAppStore } from '@/store/store_app';
import { Breadcrumb, Button, Form, Message, Tabs, Select } from '@arco-design/web-react';
import { IconArrowLeft, IconSettings } from '@arco-design/web-react/icon';
import { getFlowPreview } from '@onebase/app/src/services';
import type { WorkflowJSON } from './headerType';
import {
  PageType,
  AppStatus,
  ENTITY_TYPE,
  getAppIdByPageSetId,
  getApplication,
  getDatasourceList,
  getEntityFieldsWithChildren,
  getPageSetMetaData,
  updateApplicationMenu,
  fetchPublish,
  save,
  type ChildEntity,
  type GetApplicationReq,
  type UpdateApplicationMenuNameReq
} from '@onebase/app';
import { getHashQueryParam, pagesRuntimeSignal } from '@onebase/common';
import {
  EDITOR_TYPES,
  startLoadPageSet,
  startSavePageSet,
  useFormEditorSignal,
  useListEditorSignal,
  usePageEditorSignal,
  usePageViewEditorSignal,
  useFlowPageEditorSignal,
  type SavePageSetParams
} from '@onebase/ui-kit';
import { cloneDeep } from 'lodash-es';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PartPreview from '../partPreview';
import FlowView from '../flowView';
import styles from './index.module.less';
import { useResourceStore } from '@/store/store_resource';
import { useInitDefaultVersion } from './useInitDefaultVersion';
import { VersionStatus } from '../constants';

const Option = Select.Option;
const BreadcrumbItem = Breadcrumb.Item;
const sourceNodeIDMap = new Map();
const baseTabData = [
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
    key: EDITOR_TYPES.FLOW_EDITOR,
    title: '流程设计',
    alt: 'flow Design',
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
  const currentVersionList = useInitDefaultVersion();
  const { curPage } = pagesRuntimeSignal;
  const { t } = useI18n();
  const [renameForm] = Form.useForm();
  const { clearCurComponentID } = usePageEditorSignal();
  const { curViewId } = usePageViewEditorSignal;
  const { flowId, setFlowId } = useFlowPageEditorSignal;
  const { isEditMode, setIsEditMode } = useBasicEditorStore();

  const {
    components: formComponents,
    pageComponentSchemas: formPageComponentSchemas,
    clearComponents: clearFormComponents,
    clearPageComponentSchemas: clearFromPageComponentSchemas,
    layoutSubComponents: fromLayoutSubComponents,
    clearLayoutSubComponents: clearFromLayoutSubComponents,
    subTableComponents: fromSubTableComponents,
    clearSubTableComponents: clearFromSubTableComponents
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

  const { setCurDataSourceId } = useResourceStore();

  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('');
  const [pageSetId, setPageSetId] = useState('');

  const [appName, setAppName] = useState('未命名应用');
  const [appIcon, setAppIcon] = useState('');
  const [iconColor, setIconColor] = useState('');
  const [appStatus, setAppStatus] = useState(0);
  const [tabData, setTabData] = useState(baseTabData);
  // 重命名弹窗
  const [visibleRenameForm, setVisibleRenameForm] = useState(false);
  const [partPreviewVisible, setPartPreviewVisible] = useState(false);
  const [manageVisible, setManageVisible] = useState(false);
  const [flowViewVisible, setFlowViewVisible] = useState(false);
  const [preViewData, setPreviewData] = useState<any>({});

  const sessionData = sessionStorage.getItem('EDITOR_PAGE_INFO') || '{}';
  const pageInfo = JSON.parse(sessionData);
  const { currentFlowId, setCurrnetFlowId, editorRef, flowData, configData } = useFlowEditorStor();
  const onFlowSave = async (isCreate?: boolean) => {
    const appId = await getAppIdByPageSetId({ pageSetId });
    const data = editorRef?.document.toJSON();
    const currentJsonData = normalizeNodes(data);
    const { id, flowCode, flowName, version, versionAlias, versionStatus, businessId } = flowData;
    const params = {
      id: isCreate ? '' : id || '',
      flowCode: flowCode || '',
      flowName: flowName || '',
      version: version || '',
      versionAlias: versionAlias || '',
      versionStatus: versionStatus || '',
      businessId: businessId || pageSetId,
      appId,
      bpmDefJson: JSON.stringify(currentJsonData),
      globalConfig: configData
    };
    save(params).then((res: any) => {
      setFlowId(res);
      Message.success(isCreate ? '创建成功' : '保存成功');
      if (isCreate) {
        setCurrnetFlowId(res);
      }
    });
  };

  const normalizeNodes = (obj: WorkflowJSON | undefined) => {
    obj?.edges.forEach((item) => {
      if (item?.type) {
        sourceNodeIDMap.set(item.sourceNodeID + item.targetNodeID, item.type);
      } else {
        item.type = sourceNodeIDMap.get(item.sourceNodeID + item.targetNodeID) || 'PASS';
      }
    });
    const newNodes = obj?.nodes.map((node) => {
      if ('name' in node) {
        return { ...node, data: { ...(node.data || {}), name: node.name } };
      } else if (node.data && 'name' in node.data) {
        return { ...node, name: node.data.name };
      }
      return node;
    });
    return { ...obj, nodes: newNodes };
  };

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
    } else if (hash.includes(EDITOR_TYPES.FLOW_EDITOR)) {
      setActiveTab(EDITOR_TYPES.FLOW_EDITOR);
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

    // 获取数据源ID
    const params = {
      appId: appId
    };
    const res = await getDatasourceList(params);
    if (res?.length > 0) {
      const dataSource = res?.[0];
      // 将数据源ID存储到store中
      setCurDataSourceId(dataSource.id.toString());
    } else {
      console.warn('getAppResources - 未获取到数据源列表');
    }
  };

  // 获取主表对应的主实体信息
  const getMainMetaData = async (pageSetId: string) => {
    const mainMetaData = await getPageSetMetaData({ pageSetId: pageSetId });
    console.log('mainMetaData: ', mainMetaData);

    const entityWithChildren = await getEntityFieldsWithChildren(mainMetaData);

    // 主表数据
    const parentFields = await getEntityFields({ entityId: entityWithChildren.entityId });

    if (entityWithChildren) {
      setMainEntity({
        entityId: entityWithChildren.entityId,
        entityName: entityWithChildren.entityName,
        entityType: ENTITY_TYPE.MAIN,
        fields: parentFields.map((item: any) => ({ ...item, fieldId: item.id }))
      });

      if (entityWithChildren.childEntities && entityWithChildren.childEntities.length > 0) {
        // 返回新Promise对象，当所有输入Promise成功时返回结果数组（顺序与输入一致）
        const allChildFields = await Promise.all(
          entityWithChildren.childEntities.map(async (entity: ChildEntity) => {
            const childFields = await getEntityFields({ entityId: entity.childEntityId });
            return childFields.map((item: any) => ({ ...item, fieldId: item.id }));
          })
        );
        const subEntities = entityWithChildren.childEntities.map((entity: ChildEntity, index: number) => ({
          entityId: entity.childEntityId,
          entityName: entity.childEntityName,
          entityType: ENTITY_TYPE.SUB,
          fields: allChildFields[index]
        }));

        setSubEntities({
          entities: subEntities
        });
      }
    }
  };

  const handleSavePageSet = async () => {
    if (activeTab === EDITOR_TYPES.FLOW_EDITOR) {
      onFlowSave();
      return;
    }
    console.log(`save appid: ${curAppId}, pageSetId: ${pageSetId}`);
    console.log('curViewId: ', curViewId.value);

    const savePageSetParams: SavePageSetParams = {
      pageSetId: pageSetId,
      formComponents: formComponents.value,
      formPageComponentSchemas: cloneDeep(formPageComponentSchemas.value),
      fromColComponentsMap: cloneDeep(fromLayoutSubComponents.value),
      fromSubTableComponentsMap: cloneDeep(fromSubTableComponents.value),
      listComponents: listComponents.value,
      listPageComponentSchemas: new Map(Object.entries(cloneDeep(listPageComponentSchemas.value))),
      listColComponentsMap: { colComponents: new Map(Object.entries(cloneDeep(listLayoutSubComponents.value))) }
    };

    console.log('savePageSetParams: ', savePageSetParams);

    startSavePageSet(savePageSetParams, () => setAppStatus(AppStatus.PUBLISHED));
  };
  const handleExecTask = async () => {
    try {
      const res = await fetchPublish({ id: flowId });
      Message.success('发布成功');
    } catch (error) {}
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

  const changeCurrentFlow = (value: string) => {
    if (value !== VersionStatus.MANAGE) {
      setCurrnetFlowId(value);
    } else {
      setManageVisible(true);
    }
  };
  const flowPreview = () => {
    setFlowViewVisible(true);
  };

  useEffect(() => {
    if (curPage?.value?.pageSetType === PageType.NORMAL) {
      setTabData(baseTabData.filter((tab) => tab.key !== EDITOR_TYPES.FLOW_EDITOR));
    } else {
      setTabData(baseTabData);
    }
  }, [curPage?.value?.pageSetType]);

  return (
    <div className={styles.editorHeader}>
      {/* 左侧 */}
      <div className={styles.left}>
        <Button shape="square" type="default" size="small" onClick={backToPageManager} icon={<IconArrowLeft />} />

        <div className={styles.myAppIcon} style={{ backgroundColor: iconColor }}>
          <DynamicIcon
            IconComponent={appIconMap[appIcon as keyof typeof appIconMap]}
            theme="outline"
            size="14"
            fill="#F2F3F5"
          />
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
              case EDITOR_TYPES.FLOW_EDITOR:
                navigate(`/onebase/editor/${EDITOR_TYPES.FLOW_EDITOR}?pageSetId=${pageSetId}`);
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
        {activeTab === EDITOR_TYPES.FLOW_EDITOR && (
          <Select
            placeholder="选择流程版本"
            style={{ width: 154 }}
            triggerProps={{
              autoAlignPopupWidth: false,
              autoAlignPopupMinWidth: true,
              position: 'bl'
            }}
            value={currentFlowId}
            arrowIcon={null}
            className={styles.versionSelect}
            onChange={(value) => changeCurrentFlow(value)}
          >
            {currentVersionList.map((item) => (
              <Option key={item.id} value={item.id}>
                <div className={styles.versionOption}>
                  <span className={styles.versionName}>
                    {item.versionAlias || '未命名'}
                    {item.version}
                  </span>
                  <span
                    className={`${styles.versionStatus} ${
                      item.versionStatus === VersionStatus.DESIGNING
                        ? styles.designing
                        : item.versionStatus === VersionStatus.PUBLISHED
                          ? styles.published
                          : styles.history
                    }`}
                  >
                    {item.versionStatus}
                  </span>
                </div>
              </Option>
            ))}
            <Option key="manage" value={VersionStatus.MANAGE} className={styles.manageOption}>
              <IconSettings /> 流程版本管理
            </Option>
          </Select>
        )}

        {appStatus === AppStatus.DEVELOPING && <div className={styles.editorStatusDeveloping}>未保存</div>}
        {appStatus === AppStatus.PUBLISHED && <div className={styles.editorStatusPublished}>已保存</div>}
        {appStatus === AppStatus.EDITING_AFTER_PUBLISH && (
          <div className={styles.editorStatusEditAfterPublished}>未保存</div>
        )}
        {/* 预览 */}
        <Button
          //  onClick={toPreview}
          className={styles.previewButton}
          onClick={flowPreview}
        >
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
        {activeTab === EDITOR_TYPES.FLOW_EDITOR && (
          <Button
            type="primary"
            onClick={() => {
              handleExecTask();
            }}
          >
            发布
          </Button>
        )}
        {activeTab === EDITOR_TYPES.FLOW_EDITOR && (
          <Button
            type="primary"
            onClick={() => {
              onFlowSave(true);
            }}
          >
            创建流程
          </Button>
        )}
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

      <VersionModal visible={manageVisible} setVisible={setManageVisible} />
      <FlowView visible={flowViewVisible} setVisible={setFlowViewVisible} businessId={flowData?.businessId} />
    </div>
  );
}
