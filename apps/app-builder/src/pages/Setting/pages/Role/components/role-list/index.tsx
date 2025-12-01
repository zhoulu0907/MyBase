import ListItem from '@/components/ListItem';
import { PermissionButton as Button } from '@/components/PermissionControl';
import PlaceholderPanel from '@/components/PlaceholderPanel';
import { TENANT_ROLE_PERMISSION as ACTIONS } from '@/constants/permission';
import { Input, Spin, Tag } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import type { PageParam } from '@onebase/platform-center';
import { getRolePage, RoleType, type RoleVO } from '@onebase/platform-center';
import { forwardRef, useCallback, useEffect, useImperativeHandle, useMemo, useRef, useState } from 'react';
import s from '../../index.module.less';

interface RoleListProps {
  activeId: number | undefined;
  onSelect: (id: number | undefined, role: Partial<RoleVO> | undefined) => void;
  onAdd: () => void;
}

export default forwardRef(function RoleList({ activeId, onSelect, onAdd }: RoleListProps, ref) {
  // 状态管理
  const [roleList, setRoleList] = useState<Partial<RoleVO>[]>([]);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [isScrollLoading, setIsScrollLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [searchValue, setSearchValue] = useState('');

  const scrollContainerRef = useRef<HTMLDivElement>(null);

  // 加载角色列表
  const loadRoleList = useCallback(
    async (page: number, append: boolean = false) => {
      if (page === 1) {
        setLoading(true);
      } else {
        setIsScrollLoading(true);
      }

      try {
        const params: PageParam = {
          pageNo: page,
          pageSize: 100
        };
        if (searchValue) params.name = searchValue;

        const res = await getRolePage(params);
        const newRoleList = (res.list || []).reverse();
        if (append) {
          setRoleList((prev) => [...prev, ...newRoleList]);
        } else {
          setRoleList(newRoleList);
        }

        setHasMore(newRoleList.length === 10);
        setCurrentPage(page);
      } finally {
        setLoading(false);
        setIsScrollLoading(false);
      }
    },
    [searchValue]
  );

  // 刷新角色列表
  const refresh = useCallback(() => {
    setCurrentPage(1);
    loadRoleList(1, false);
  }, [loadRoleList]);

  // 刷新角色
  const refreshRoleById = useCallback(
    (roleId: number, values: Partial<RoleVO>) => {
      const roleIndex = roleList.findIndex((role) => role.id === roleId);
      if (roleIndex !== -1) {
        // 更新角色列表中的角色信息
        const updatedRoleList = [...roleList];
        updatedRoleList[roleIndex] = {
          ...updatedRoleList[roleIndex],
          ...values
        };
        setRoleList(updatedRoleList);
      }
    },
    [roleList]
  );

  // 暴露给父组件的方法
  useImperativeHandle(
    ref,
    () => ({
      refresh,
      refreshRoleById
    }),
    [refresh, refreshRoleById]
  );

  // 滚动加载
  useEffect(() => {
    const container = scrollContainerRef.current;
    if (!container) return;

    const handleScroll = () => {
      if (container.scrollTop + container.clientHeight >= container.scrollHeight - 10) {
        if (hasMore && !isScrollLoading && !loading) {
          const nextPage = currentPage + 1;
          loadRoleList(nextPage, true);
        }
      }
    };

    container.addEventListener('scroll', handleScroll);
    return () => container.removeEventListener('scroll', handleScroll);
  }, [hasMore, isScrollLoading, loading, currentPage, loadRoleList]);

  // 初始加载
  useEffect(() => {
    loadRoleList(1, false);
  }, [loadRoleList]);

  // 当角色列表加载完成且没有选中角色时，默认选中第一个
  useEffect(() => {
    if (roleList.length > 0 && !activeId) {
      const firstRole = roleList[0];
      if (firstRole && firstRole.id) {
        onSelect(firstRole.id, firstRole);
      }
    }
  }, [roleList, activeId, onSelect]);

  const handleSearchChange = (value: string) => {
    setSearchValue(value);
    setCurrentPage(1);
  };

  const filteredRoleList = useMemo(() => {
    if (!searchValue) return roleList;
    return roleList.filter((role) => role.name?.toLowerCase().includes(searchValue.toLowerCase()));
  }, [roleList, searchValue]);

  const listTitle = `全部角色(${filteredRoleList?.length})`;

  const roleListItems = useMemo(() => {
    return filteredRoleList?.map((item) => (
      <ListItem
        key={item.id}
        title={item.name || ''}
        active={item.id === activeId}
        onClick={() => item.id && onSelect(item.id, item)}
      >
        {item.type === RoleType.SYSTEM && (
          <Tag color="cyan" style={{ marginLeft: 8 }}>
            系统
          </Tag>
        )}
      </ListItem>
    ));
  }, [filteredRoleList, activeId, onSelect]);

  return (
    <>
      <div className={s.searchInput}>
        <Input.Search
          value={searchValue}
          onChange={handleSearchChange}
          placeholder="输入角色名称"
          allowClear
          style={{ borderRadius: '24px', marginBottom: '8px' }}
        />
      </div>
      <ListItem title={listTitle}>
        <Button
          permission={ACTIONS.CREATE}
          type="text"
          onClick={onAdd}
          style={{ paddingLeft: '8px', paddingRight: '8px' }}
        >
          <IconPlus />
          新建
        </Button>
      </ListItem>
      <PlaceholderPanel hasPermission={true} isLoading={loading} isEmpty={roleList.length === 0}>
        <div
          ref={scrollContainerRef}
          className={s.roleList}
          style={{
            height: 'calc(100% - 110px)',
            overflowY: 'auto',
            position: 'relative'
          }}
        >
          <>
            {roleListItems}
            {isScrollLoading && (
              <div style={{ textAlign: 'center', padding: '12px' }}>
                <Spin size={20} />
              </div>
            )}
          </>
        </div>
      </PlaceholderPanel>
    </>
  );
});
