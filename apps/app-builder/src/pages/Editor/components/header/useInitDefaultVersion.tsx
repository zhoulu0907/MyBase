import { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { useFlowEditorStor } from '@/store/index';
import { getVersionMgmt, getByBusinessId } from '@onebase/app';
import type { VersionType } from '../constants';

export function useInitDefaultVersion() {
  const [versionList, setVersionList] = useState<VersionType[]>([]);
  const { setBusinessId, setCurrnetFlowId, currentFlowId } = useFlowEditorStor();
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const pageSetId = searchParams.get('pageSetId') || '';

  // 获取版本信息列表
  const getVersionMgmtData = async () => {
    const params = { businessId: pageSetId, sortType: 'create_time' };
    const { list } = await getVersionMgmt(params);
    setVersionList(list);
  };

  // 获取当前版本信息
  const getCurrentVersion = async () => {
    const { id } = await getByBusinessId({ businessId: pageSetId });
    setCurrnetFlowId(id);
  };
  useEffect(() => {
    getCurrentVersion();
    setBusinessId(pageSetId);
  }, []);

  useEffect(() => {
    getVersionMgmtData();
  }, [currentFlowId]);
  return versionList;
}
