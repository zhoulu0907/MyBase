import InfoPanel from '@/components/InfoPanel';
import { Divider, Empty, Layout, Message, Popconfirm, Space, Tabs } from '@arco-design/web-react';
import { createRole, deleteRole, updateRole } from '@onebase/platform-center/src/services/role';
import type { RoleVO } from '@onebase/platform-center/src/types/role';
import { useCallback, useMemo, useRef, useState } from 'react';
import PermissionList from './components/permission-list';
import RoleList from './components/role-list';
import RoleModal from './components/role-modal';
import UserList from './components/user-list';
import styles from './index.module.less';
import { TENANT_ROLE_PERMISSION as ACTIONS } from '@/constants/permission';
import PlaceholderPanel from '@/components/PlaceholderPanel';
import { PermissionButton as Button } from '@/components/PermissionControl';
import { hasPermission } from '@/utils/permission';
import { RoleType } from '@onebase/platform-center';
import { divide } from 'lodash-es';

const Sider = Layout.Sider;
const Header = Layout.Header;
const Content = Layout.Content;
const TabPane = Tabs.TabPane;

export default function RolePage() {
  const [activeRoleId, setActiveRoleId] = useState<number | undefined>(undefined);
  const [activeRole, setActiveRole] = useState<Partial<RoleVO> | undefined>(undefined);
  const [editRole, setEditRole] = useState<Partial<RoleVO> | null>(null);
  const [roleModalVisible, setRoleModalVisible] = useState(false);
  const [modalLoading, setModalLoading] = useState(false);
  const [activeTab, setActiveTab] = useState('user');
  const [forceRefresh, setForceRefresh] = useState(false);

  const roleListRef = useRef<any>(null);

  const handleRoleSelect = useCallback((id: number | undefined, role: Partial<RoleVO> | undefined) => {
    setActiveRoleId(id);
    setActiveRole(role);
  }, []);

  // 删除角色
  const handleDeleteRole = useCallback(
    async (id: number) => {
      try {
        await deleteRole(id);
        Message.success('删除成功');

        // 如果删除的是当前选中的角色，清空选中状态
        if (activeRoleId === id) {
          setActiveRoleId(undefined);
          setActiveRole(undefined);
        }

        setForceRefresh((prev) => !prev);

        return true;
      } catch (error) {
        Message.error('删除失败');
        return false;
      }
    },
    [activeRoleId]
  );

  // 保存角色
  const handleSaveRole = useCallback(
    async (values: Partial<RoleVO>) => {
      setModalLoading(true);
      try {
        if (editRole?.id) {
          await updateRole({
            ...values,
            id: editRole.id
          });
          setActiveRole((prev) => ({
            ...prev,
            ...values
          }));
          roleListRef.current?.refreshRoleById?.(editRole.id, values);
        } else {
          await createRole(values);
          roleListRef.current?.refresh?.();
        }
        Message.success('保存成功');
        setRoleModalVisible(false);
      } catch (error) {
        console.error('保存失败');
      } finally {
        setModalLoading(false);
      }
    },
    [editRole?.id]
  );

  const openRoleModal = useCallback((role: Partial<RoleVO> | null) => {
    setEditRole(role);
    setRoleModalVisible(true);
  }, []);

  const closeRoleModal = useCallback(() => {
    setRoleModalVisible(false);
    setEditRole(null);
  }, []);

  const handleTabChange = useCallback((key: string) => {
    setActiveTab(key);
  }, []);

  // 编辑/删除角色按钮
  const OperationButtons = useMemo(
    () => (
      <Space size="small">
        <Button permission={ACTIONS.UPDATE} type="secondary" onClick={() => openRoleModal(activeRole || null)}>
          编辑
        </Button>
        <Popconfirm
          position="br"
          focusLock
          title={<div style={{ fontWeight: '700' }}>"移除成员"</div>}
          content={
            <>
              <div>确定要移除这个成员吗？</div>
              <div>该角色下关联的用户将失去该角色赋予的权限!</div>
            </>
          }
          onOk={() => {
            if (activeRole?.id) {
              handleDeleteRole(activeRole.id).then(() => {
                roleListRef.current?.refresh?.();
              });
            }
          }}
          style={{ backgroundColor: '#ccc' }}
        >
          <Button type="secondary" permission={ACTIONS.DELETE} disabled={activeRole?.type === RoleType.SYSTEM}>
            删除
          </Button>
        </Popconfirm>
      </Space>
    ),
    [activeRole, openRoleModal, handleDeleteRole]
  );

  return (
    <div className={styles.rolePage}>
      <Layout className={styles.pageLayout}>
        <Sider width={252} className={styles.leftPanel}>
          <RoleList
            ref={roleListRef}
            activeId={activeRoleId || undefined}
            onAdd={() => openRoleModal(null)}
            onSelect={handleRoleSelect}
          />
        </Sider>
        <Layout className={styles.rightPanel}>
          {!activeRoleId || forceRefresh ? (
            <Empty />
          ) : (
            <>
              <Header>
                <InfoPanel
                  title={activeRole?.name}
                  description={activeRole?.remark}
                  rightChildren={OperationButtons}
                  wrapperClassName={styles.infoPanel}
                ></InfoPanel>
                <Divider style={{ margin: '16px 0' }} />
              </Header>
              <Content>
                <Tabs activeTab={activeTab} onChange={handleTabChange}>
                  <TabPane key="user" title="角色用户">
                    <PlaceholderPanel hasPermission={hasPermission(ACTIONS.USER)}>
                      <UserList selectedRoleId={activeRoleId} />
                    </PlaceholderPanel>
                  </TabPane>
                  <TabPane key="permission" title="关联权限">
                    <PlaceholderPanel hasPermission={hasPermission(ACTIONS.PERMISSION)}>
                      <PermissionList selectedRoleId={activeRoleId} />
                    </PlaceholderPanel>
                  </TabPane>
                </Tabs>
              </Content>
            </>
          )}
        </Layout>
      </Layout>
      {/* 角色新增/编辑对话框 */}
      <RoleModal
        visible={roleModalVisible}
        onCancel={closeRoleModal}
        onOk={(values) => {
          handleSaveRole(values);
        }}
        confirmLoading={modalLoading}
        initialValues={(editRole as any) || undefined}
      />
    </div>
  );
}
