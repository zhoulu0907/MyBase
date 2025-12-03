import InfoPanel from '@/components/InfoPanel';
import { PermissionButton as Button } from '@/components/PermissionControl';
import PlaceholderPanel from '@/components/PlaceholderPanel';
import { Divider, Empty, Layout, Message, Modal, Space, Tabs } from '@arco-design/web-react';
import { TENANT_ROLE_PERMISSION as ACTIONS, hasPermission } from '@onebase/common';
import { RoleType } from '@onebase/platform-center';
import { createRole, deleteRole, updateRole } from '@onebase/platform-center/src/services/role';
import type { RoleVO } from '@onebase/platform-center/src/types/role';
import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import PermissionList from './components/permission-list';
import RoleList from './components/role-list';
import RoleModal from './components/role-modal';
import UserList from './components/user-list';
import styles from './index.module.less';

const Sider = Layout.Sider;
const Header = Layout.Header;
const Content = Layout.Content;
const TabPane = Tabs.TabPane;

export default function RolePage() {
  const [activeRoleId, setActiveRoleId] = useState<number | undefined>(undefined);
  const [showEmpty, setShowEmpty] = useState(false);
  const [activeRole, setActiveRole] = useState<Partial<RoleVO> | undefined>(undefined);
  const [editRole, setEditRole] = useState<Partial<RoleVO> | null>(null);
  const [roleModalVisible, setRoleModalVisible] = useState(false);
  const [modalLoading, setModalLoading] = useState(false);
  const [activeTab, setActiveTab] = useState('user');

  const roleListRef = useRef<any>(null);

  useEffect(() => {
    if (!activeRoleId) {
      setShowEmpty(true);
    }
  }, [activeRoleId]);

  const handleRoleSelect = (id: number | undefined, role: Partial<RoleVO> | undefined) => {
    setActiveRoleId(id);
    setActiveRole(role);
    setShowEmpty(false);
  };

  // 删除角色
  const handleDeleteRole = async (id: number) => {
    try {
      await deleteRole(id);
      Message.success('删除成功');

      // 如果删除的是当前选中的角色，清空选中状态
      if (activeRoleId === id) {
        setActiveRoleId(undefined);
        setActiveRole(undefined);
      }

      return true;
    } catch (error: any) {
      console.log(error.errors);
      return false;
    }
  };

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

  // 删除角色
  const handleDelete = (record: Partial<RoleVO> | null) => {
    if (!record?.id) return;
    Modal.confirm({
      title: `确认要删除角色（${record?.name}）吗？`,
      content: '删除角色后，该角色下关联的用户将失去该角色赋予的权限，请谨慎操作。',
      okButtonProps: { status: 'danger' },
      onOk: async () => {
        try {
          handleDeleteRole(record.id!).then(() => {
            roleListRef.current?.refresh?.();
          });
          Message.success('用户移除成功');
        } catch (error) {
          Message.error('移除用户失败，请重试');
        }
      }
    });
  };

  // 编辑/删除角色按钮
  const OperationButtons = useMemo(
    () => (
      <Space size="small">
        <Button type="secondary" permission={ACTIONS.UPDATE} onClick={() => openRoleModal(activeRole || null)}>
          编辑
        </Button>
        <Button
          type="secondary"
          permission={ACTIONS.DELETE}
          disabled={activeRole?.type === RoleType.SYSTEM}
          onClick={() => handleDelete(activeRole || null)}
        >
          删除
        </Button>
      </Space>
    ),
    [activeRole, openRoleModal, handleDelete]
  );

  return (
    <div className={styles.rolePage}>
      <Layout className={styles.pageLayout}>
        <Sider width={252} className={styles.leftPanel}>
          <RoleList
            ref={roleListRef}
            activeId={activeRoleId}
            onAdd={() => openRoleModal(null)}
            onSelect={handleRoleSelect}
          />
        </Sider>
        <Layout className={styles.rightPanel}>
          {!activeRoleId || showEmpty ? (
            <>
              <Empty />
            </>
          ) : (
            <>
              <Header>
                <InfoPanel
                  title={activeRole?.name}
                  type={activeRole?.type}
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
