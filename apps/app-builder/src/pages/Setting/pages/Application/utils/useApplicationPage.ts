import { useAppStore } from '@/store/store_app';
import { Form } from '@arco-design/web-react';
import { type Application, type DatasourceSaveReqDTO, type DeleteApplicationReq, type PageParam } from '@onebase/app';
import { TENANT_APP_PERMISSION as ACTIONS, UserPermissionManager } from '@onebase/common';
import { debounce } from 'lodash-es';
import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { getCommonPaginationList, getRuntimeURL } from '@onebase/common';
import { createApplication, deleteApplication, listApplication } from '@onebase/app';

export function useApplicationPage() {
  const [form] = Form.useForm();
  const navigate = useNavigate();

  const { tenantId } = useParams();

  const [pageSize, setPageSize] = useState<number>();
  const [pageNo, setPageNo] = useState(1);
  const [dataList, setDataList] = useState<Application[]>();
  const [total, setTotal] = useState(0);
  const [name, setName] = useState('');
  const [loading, setLoading] = useState(false);
  const [ownerTag, setOwnerTag] = useState<0 | 1>(0);
  const [orderByTime, setOrderByTime] = useState<'create' | 'update'>('create');
  const [status, setStatus] = useState<number | string>('');

  const [appName, setAppName] = useState<string>('');
  const [createType, setCreateType] = useState<'app' | 'datasource'>('app');
  const [deleteApp, setDeleteApp] = useState<Application>();
  const [datasource, setDdtasource] = useState<DatasourceSaveReqDTO | undefined>();
  const [deleteVisible, setDeleteVisible] = useState<boolean>(false);
  const [createVisible, setCreateVisible] = useState<boolean>(false);
  const [createLoading, setCreateLoading] = useState<boolean>(false);
  const [deleteLoading, setDeleteLoading] = useState<boolean>(false);

  const [applicationEmpty, setAapplicationEmpty] = useState<boolean>(false);
  const [applicationFilterEmpty, setAapplicationFilterEmpty] = useState<boolean>(false);

  const [currentStep, setCurrentStep] = useState<number>(1);
  const [dbTypeSelect, setDbTypeSelect] = useState<string>('');

  const [isIframe, setIsIframe] = useState(false);

  const { setCurAppId } = useAppStore();

  const createDatasourceRef = useRef<any>(null);
  const appContainerRef = useRef<HTMLDivElement>(null);

  const [optionVisibleId, setOptionVisibleId] = useState('');
  const userPermissionInfo = UserPermissionManager.getUserPermissionInfo();
  const [importVisible, setImportVisible] = useState(false);

  useEffect(() => {
    setIsIframe(window.self !== window.top);
  }, []);

  const getApplicationList = useCallback(async () => {
    try {
      setLoading(true);
      const req: PageParam = {
        pageNo,
        pageSize: pageSize || 8,
        name,
        ownerTag,
        orderByTime,
        status: status === '' ? null : Number(status)
      };
      const res = await getCommonPaginationList(listApplication, req, setPageNo);
      if (res) {
        setDataList(res.list || []);
        setTotal(res.total || 0);
        setLoading(false);
      }
    } catch (error) {
      console.log('error', error);
    } finally {
      setLoading(false);
    }
  }, [pageNo, pageSize, name, orderByTime, status, ownerTag]);

  const debouncedUpdate = useCallback(
    debounce((value) => {
      setName(value);
      setPageNo(1);
    }, 500),
    []
  );

  useEffect(() => {
    return () => debouncedUpdate.cancel();
  }, [debouncedUpdate]);

  const handleSearchChange = (value: string) => {
    debouncedUpdate(value);
  };

  const handleCreateApp = useCallback(async () => {
    try {
      const values = await form.validate();
      setCreateLoading(true);
      const { appCode, appName, iconColor, iconName, description, tagIds, themeColor, publishModel } = values;

      const params = {
        appCode,
        appMode: 'classic',
        appName,
        description,
        iconColor,
        iconName,
        tagIds: tagIds?.map((t: any) => t.value),
        themeColor,
        datasourceSaveReq: datasource,
        publishModel: publishModel ? publishModel : 'inner'
      };
      const res = await createApplication(params);
      setCreateVisible(false);
      import('@arco-design/web-react').then(({ Message }) => Message.success('应用创建成功'));
      form.resetFields();
      navigate(`/onebase/${tenantId}/home/create-app/data-factory?appId=${res.id}`);
    } catch (error) {
      return null;
    } finally {
      setCreateLoading(false);
    }
  }, [form, datasource, navigate, tenantId]);

  const handleDeleteApp = useCallback(async () => {
    if (appName !== deleteApp?.appName) {
      import('@arco-design/web-react').then(({ Message }) => Message.warning('请输入正确的应用名称'));
      return;
    }
    try {
      setDeleteLoading(true);
      const params: DeleteApplicationReq = {
        id: deleteApp?.id,
        name: appName
      };
      const res = await deleteApplication(params);
      if (res) {
        import('@arco-design/web-react').then(({ Message }) => Message.success('删除成功'));
        getApplicationList();
      }
    } finally {
      setAppName('');
      setDeleteLoading(false);
      setDeleteVisible(false);
    }
  }, [appName, deleteApp, getApplicationList]);

  const getBaseUrl = () => {
    const baseUrl = window.location.href.replace(/setting.*$/, '');
    return baseUrl;
  };

  const nagivateToDataFactory = useCallback((appId: string) => {
    setCurAppId(appId);
    const newWindow = window.open('', '_blank');
    if (newWindow) {
      const baseUrl = getBaseUrl();
      const href = `${baseUrl}home/create-app/data-factory?appId=${appId}`;
      newWindow.location.href = href;
    }
  }, [setCurAppId]);

  const nagivateToDataFactoryInternal = useCallback((appId: string) => {
    setCurAppId(appId);
    navigate(`/onebase/${tenantId}/home/create-app/data-factory?appId=${appId}`);
  }, [navigate, setCurAppId, tenantId]);

  const nagivateToRuntimeApp = useCallback((appId: string) => {
    const appUrl = `${getRuntimeURL()}/#/onebase/${tenantId}/${appId}/runtime/`;
    const newWindow = window.open('', '_blank');
    if (newWindow) {
      newWindow.location.href = `${getRuntimeURL()}/#/login?redirectURL=${appUrl}`;
    }
  }, [tenantId]);

  const handleOptionVisibleChange = useCallback((v: boolean, id: string) => {
    setOptionVisibleId(v ? id : '');
  }, []);

  const handleEdit = useCallback((appId: string) => {
    nagivateToDataFactory(appId);
  }, [nagivateToDataFactory]);

  const handleLaunch = useCallback((appId: string) => {
    nagivateToRuntimeApp(appId);
  }, [nagivateToRuntimeApp]);

  const handleDelete = useCallback((item: Application) => {
    setAppName('');
    setDeleteApp(item);
    setDeleteVisible(true);
  }, []);

  useEffect(() => {
    if (!appContainerRef.current) return;
    const containerWidth = appContainerRef.current?.offsetWidth;
    const containerHeight = appContainerRef.current?.offsetHeight;
    import('../const').then(({ calculateMaxItems }) => {
      const maxAppInfo = calculateMaxItems(containerWidth, containerHeight);
      console.log('maxAppInfo', maxAppInfo, containerWidth, containerHeight);
      setPageSize(maxAppInfo.total || 8);
    });
  }, [appContainerRef.current]);

  useEffect(() => {
    pageSize && getApplicationList();
  }, [pageNo, pageSize, name, orderByTime, status, ownerTag, getApplicationList]);

  useEffect(() => {
    setDdtasource(undefined);
  }, []);

  useEffect(() => {
    setCreateType('app');
    setCurrentStep(1);
    setDbTypeSelect('');
  }, [createVisible]);

  useEffect(() => {
    if ((ownerTag === 0 || status === 0) && dataList?.length === 0) {
      setAapplicationEmpty(true);
    } else {
      setAapplicationEmpty(false);
    }
    if ((ownerTag === 1 || status === 1) && dataList?.length === 0) {
      setAapplicationEmpty(false);
      setAapplicationFilterEmpty(true);
    } else {
      setAapplicationFilterEmpty(false);
    }
  }, [ownerTag, status, dataList]);

  return {
    form,
    navigate,
    tenantId,
    pageSize,
    pageNo,
    dataList,
    total,
    name,
    loading,
    ownerTag,
    orderByTime,
    status,
    appName,
    setAppName,
    createType,
    setCreateType,
    deleteApp,
    datasource,
    setDdtasource,
    deleteVisible,
    setDeleteVisible,
    createVisible,
    setCreateVisible,
    createLoading,
    setCreateLoading,
    deleteLoading,
    setDeleteLoading,
    applicationEmpty,
    applicationFilterEmpty,
    currentStep,
    setCurrentStep,
    dbTypeSelect,
    setDbTypeSelect,
    isIframe,
    setIsIframe,
    createDatasourceRef,
    appContainerRef,
    optionVisibleId,
    setOptionVisibleId,
    userPermissionInfo,
    importVisible,
    setImportVisible,
    setPageSize,
    setPageNo,
    setDataList,
    setTotal,
    setName,
    setLoading,
    setOwnerTag,
    setOrderByTime,
    setStatus,
    handleSearchChange,
    handleCreateApp,
    handleDeleteApp,
    handleOptionVisibleChange,
    handleEdit,
    handleLaunch,
    handleDelete,
    getApplicationList
  };
}
