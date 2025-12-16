import editPageNameSVG from '@/assets/images/edit_page_name_icon.svg';
import activeFlowDesignSVG from '@/assets/images/flow-active-icon.svg';
import defaultFlowDesignSVG from '@/assets/images/flow-default-icon.svg';
import activeFormDesignSVG from '@/assets/images/form_design_active_icon.svg';
import defaultFormDesignSVG from '@/assets/images/form_design_default_icon.svg';
import activeListDesignSVG from '@/assets/images/list_design_active_icon.svg';
import defaultListDesignSVG from '@/assets/images/list_design_default_icon.svg';
import activePageSettingSVG from '@/assets/images/page_setting_active_icon.svg';
import defaultPageSettingSVG from '@/assets/images/page_setting_default_icon.svg';
import previewSVG from '@/assets/images/preview_icon.svg';
import activeWorkbenchDesignSVG from '@/assets/images/workbench_design_active_icon.svg';
import DynamicIcon from '@/components/DynamicIcon';
import { useI18n } from '@/hooks/useI18n';
import RenameModal from '@/pages/CreateApp/pages/PageManager/components/Modals/RenameModal';
import VersionModal from '@/pages/CreateApp/pages/PageManager/components/Modals/VersionModal';
import { useBasicEditorStore } from '@/store';
import { useFlowEditorStor } from '@/store/index';
import { useAppStore } from '@/store/store_app';
import { useResourceStore } from '@/store/store_resource';
import { Breadcrumb, Button, Form, Message, Modal, Tabs } from '@arco-design/web-react';
import { IconArrowLeft, IconInfoCircleFill } from '@arco-design/web-react/icon';
import {
  AppStatus,
  ENTITY_TYPE,
  fetchPublish,
  getAppIdByPageSetId,
  getApplication,
  getDatasourceList,
  getEntityFieldsWithChildren,
  getPageSetMetaData,
  listApplicationMenu,
  menuSignal,
  PageType,
  save,
  updateApplicationMenu,
  type ChildEntity,
  type GetApplicationReq,
  type ListApplicationMenuReq,
  type UpdateApplicationMenuNameReq
} from '@onebase/app';
import { getHashQueryParam, pagesRuntimeSignal } from '@onebase/common';
import {
  appIconMap,
  EDITOR_TYPES,
  startLoadPageSet,
  startSavePageSet,
  useAppEntityStore,
  useFlowPageEditorSignal,
  useFormEditorSignal,
  useListEditorSignal,
  usePageEditorSignal,
  usePageViewEditorSignal,
  type SavePageSetParams
} from '@onebase/ui-kit';
import { cloneDeep } from 'lodash-es';
import { useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { VersionStatus } from '../constants';
import FlowView from '../flowView';
import PartPreview from '../partPreview';
import type { WorkflowJSON } from './headerType';
import styles from './index.module.less';
import { VersionListSelect } from './versionList';

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
    defaultIcon: defaultFlowDesignSVG,
    activeIcon: activeFlowDesignSVG
  },
  {
    key: EDITOR_TYPES.WORKBENCH_EDITOR,
    title: '工作台设计',
    alt: 'workbench Setting',
    defaultIcon: defaultPageSettingSVG, // TODO: 待UI补充后替换
    activeIcon: activeWorkbenchDesignSVG
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

interface VersionListSelectRef {
  getVersionMgmtData: () => void;
}

export default function EditorHeader() {
  const location = useLocation();
  const selectRef = useRef<VersionListSelectRef>(null);
  const { curPage } = pagesRuntimeSignal;
  const { t } = useI18n();
  const [renameForm] = Form.useForm();
  const { clearCurComponentID } = usePageEditorSignal();
  const { curViewId } = usePageViewEditorSignal;
  const { flowId, setFlowId } = useFlowPageEditorSignal;
  const { isEditMode, setIsEditMode } = useBasicEditorStore();

  const [exitModalVisible, setExitModalVisible] = useState(false);

  const { tenantId } = useParams();

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
  const { curMenu, setCurMenu } = menuSignal;
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
    const data = editorRef?.document.toJSON();
    const currentJsonData = normalizeNodes(data);
    currentJsonData.edges?.forEach((item) => {
      if (item.data) {
        item.name = item.data.name;
      }
    });
    const { id, flowCode, flowName, bpmVersionAlias, businessUuid } = flowData;
    const params = {
      id: isCreate ? '' : id || '',
      flowCode: flowCode || '',
      flowName: flowName || '',
      bpmVersionAlias: bpmVersionAlias || '',
      businessUuid: businessUuid || curMenu.value.menuUuid,
      bpmDefJson: JSON.stringify(currentJsonData),
      globalConfig: configData
    };

    console.log(flowData);

    return save(params).then((res: any) => {
      setFlowId(res);
      Message.success(isCreate ? '创建成功' : '保存成功');
      if (isCreate) {
        setCurrnetFlowId(res);
      }
    });
  };

  const getMenuList = async (keywords?: string) => {
    const searchParams = new URLSearchParams(location.search);
    const appId = searchParams.get('appId') || '';
    const req: ListApplicationMenuReq = {
      applicationId: appId,
      name: keywords
    };
    const res = await listApplicationMenu(req);
    res.forEach((item: any) => {
      if (item.menuName === pageInfo?.name) {
        setCurMenu(item);
      }
    });
  };

  const getVersonList = () => {
    selectRef.current && selectRef.current.getVersionMgmtData();
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
    } else if (hash.includes(EDITOR_TYPES.WORKBENCH_EDITOR)) {
      setActiveTab(EDITOR_TYPES.WORKBENCH_EDITOR);
    }
  }, []);

  useEffect(() => {
    const pageSetId = getHashQueryParam('pageSetId');
    if (pageSetId) {
      setPageSetId(pageSetId);
    }
    getMenuList();
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
    if (pageSetId != '') {
      handleGetAppInfo(pageSetId);
      // 工作台设计页不获取主表数据
      if (activeTab !== EDITOR_TYPES.WORKBENCH_EDITOR) {
        getMainMetaData(pageSetId);
      }

      loadPageSetInfo(pageSetId);

      if (!isEditMode) {
        setIsEditMode(true);
      }
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
    const res = await getDatasourceList({
      applicationId: appId
    });
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

    console.log('entityWithChildren: ', entityWithChildren);

    // 主表数据

    const parentFields = entityWithChildren.parentFields;

    console.log('parentFields: ', parentFields);

    if (entityWithChildren) {
      setMainEntity({
        entityId: entityWithChildren.entityId,
        entityUuid: entityWithChildren.entityUuid,
        tableName: entityWithChildren.tableName,
        entityName: entityWithChildren.entityName,
        entityType: ENTITY_TYPE.MAIN,

        fields: parentFields
      });

      if (entityWithChildren.childEntities && entityWithChildren.childEntities.length > 0) {
        // 返回新Promise对象，当所有输入Promise成功时返回结果数组（顺序与输入一致）
        const allChildFields = await Promise.all(
          entityWithChildren.childEntities.map(async (entity: ChildEntity) => {
            return entity.childFields;
          })
        );
        const subEntities = entityWithChildren.childEntities.map((entity: ChildEntity, index: number) => ({
          entityId: entity.childEntityId,
          entityUuid: entity.childEntityUuid,
          tableName: entity.childTableName,
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

  const handleSavePageSet = async (exit?: boolean) => {
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
    if (exit) {
      backToPageManager();
    }
  };
  const handleExecTask = async () => {
    try {
      if (activeTab === EDITOR_TYPES.FLOW_EDITOR) {
        await onFlowSave();
      }
      const res = await fetchPublish({ id: flowId });
      getVersonList();
      Message.success('发布成功');
    } catch (error) {
      Message.error('发布失败');
    }
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

    // 如果当前有选中的菜单，将菜单ID作为URL参数传递，以便返回时恢复选中状态
    const menuId = curMenu.value?.id;
    const menuIdParam = menuId ? `&menuId=${menuId}` : '';
    navigate(`/onebase/${tenantId}/home/create-app/page-manager?appId=${appId}${menuIdParam}`);
  };
  const handleExit = () => {
    if (appStatus === AppStatus.DEVELOPING) {
      setExitModalVisible(true);
    } else {
      backToPageManager();
    }
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
    const pageType = curPage?.value?.pageSetType;

    const shouldKeepTab = (key: string) => {
      if (pageType === PageType.NORMAL) {
        return key !== EDITOR_TYPES.FLOW_EDITOR && key !== EDITOR_TYPES.WORKBENCH_EDITOR;
      }
      if (pageType === PageType.WORKBENCH) {
        return key === EDITOR_TYPES.WORKBENCH_EDITOR || key === EDITOR_TYPES.PAGE_SETTING;
      }
      return key !== EDITOR_TYPES.WORKBENCH_EDITOR;
    };

    setTabData(baseTabData.filter((tab) => shouldKeepTab(tab.key)));
  }, [curPage?.value?.pageSetType]);

  return (
    <div className={styles.editorHeader}>
      {/* 左侧 */}
      <div className={styles.left}>
        <Button shape="square" type="default" size="small" onClick={handleExit} icon={<IconArrowLeft />} />

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
                navigate(
                  `/onebase/${tenantId}/editor/${EDITOR_TYPES.FORM_EDITOR}?pageSetId=${pageSetId}&appId=${curAppId}`
                );
                break;
              case EDITOR_TYPES.LIST_EDITOR:
                navigate(
                  `/onebase/${tenantId}/editor/${EDITOR_TYPES.LIST_EDITOR}?pageSetId=${pageSetId}&appId=${curAppId}`
                );
                break;
              case EDITOR_TYPES.PAGE_SETTING:
                navigate(
                  `/onebase/${tenantId}/editor/${EDITOR_TYPES.PAGE_SETTING}?pageSetId=${pageSetId}&appId=${curAppId}`
                );
                break;
              case EDITOR_TYPES.METADATA_MANAGE:
                navigate(
                  `/onebase/${tenantId}/editor/${EDITOR_TYPES.METADATA_MANAGE}?pageSetId=${pageSetId}&appId=${curAppId}`
                );
                break;
              case EDITOR_TYPES.FLOW_EDITOR:
                navigate(
                  `/onebase/${tenantId}/editor/${EDITOR_TYPES.FLOW_EDITOR}?pageSetId=${pageSetId}&appId=${curAppId}`
                );
                break;
              case EDITOR_TYPES.WORKBENCH_EDITOR:
                navigate(
                  `/onebase/${tenantId}/editor/${EDITOR_TYPES.WORKBENCH_EDITOR}?pageSetId=${pageSetId}&appId=${curAppId}`
                );
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
          <VersionListSelect menuUuid={curMenu.value.menuUuid} ref={selectRef} setManageVisible={setManageVisible} />
        )}

        {appStatus === AppStatus.DEVELOPING && <div className={styles.editorStatusDeveloping}>未保存</div>}
        {appStatus === AppStatus.PUBLISHED && <div className={styles.editorStatusPublished}>已保存</div>}
        {appStatus === AppStatus.EDITING_AFTER_PUBLISH && (
          <div className={styles.editorStatusEditAfterPublished}>未保存</div>
        )}
        {/* 预览 */}
        <Button onClick={toPreview} className={styles.previewButton}>
          <img src={previewSVG} />
          {t('editor.preview')}
        </Button>
        {activeTab === EDITOR_TYPES.FLOW_EDITOR && (
          <Button type="primary" onClick={flowPreview}>
            测试
          </Button>
        )}
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
      <FlowView visible={flowViewVisible} setVisible={setFlowViewVisible} businessUuid={flowData?.businessUuid} />
      <VersionModal
        visible={manageVisible}
        setVisible={setManageVisible}
        changeCurrentFlow={changeCurrentFlow}
        currentFlowId={currentFlowId}
        getVersonList={getVersonList}
        businessUuid={flowData?.businessUuid}
      />

      <Modal
        title={null}
        okText="退出"
        cancelText="取消"
        visible={exitModalVisible}
        onCancel={() => {
          setExitModalVisible(false);
        }}
        onOk={() => {
          setExitModalVisible(false);
        }}
        style={{
          width: 350
        }}
        footer={
          <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' }}>
            <div>
              <Button
                type="default"
                status="danger"
                onClick={() => {
                  backToPageManager();
                }}
              >
                不保存
              </Button>
            </div>

            <div
              style={{
                display: 'flex',
                flexDirection: 'row',
                alignItems: 'center',
                justifyContent: 'space-between',
                gap: 10
              }}
            >
              <Button
                type="default"
                onClick={() => {
                  setExitModalVisible(false);
                }}
              >
                取消
              </Button>
              <Button
                type="primary"
                onClick={() => {
                  handleSavePageSet(true);
                }}
              >
                保存并离开
              </Button>
            </div>
          </div>
        }
      >
        <div
          style={{
            display: 'flex',
            flexDirection: 'row',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: 16,
            fontWeight: 500,
            height: 50,
            paddingTop: 20
          }}
        >
          <IconInfoCircleFill style={{ fontSize: 24, marginRight: 8, color: '#ff7d00' }} />
          <span>即将离开当前页面，是否保存更改</span>
        </div>
      </Modal>
    </div>
  );
}
