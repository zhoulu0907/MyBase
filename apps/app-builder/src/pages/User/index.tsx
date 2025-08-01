import { Layout } from '@arco-design/web-react';
import { getSimpleDeptList } from '@onebase/platform-center/src/services/dept';
import type { DeptVO } from '@onebase/platform-center/src/types/dept';
import { useEffect, useState } from 'react';
import DeptTree from './components/DeptTree';
import UserTable from './components/UserTable';
import styles from './index.module.less';
import { convertDeptListToSelectTree, convertDeptListToTree } from './utils/deptUtils';

const { Sider, Content } = Layout;

export default function UserPage() {
  const [selectedDeptId, setSelectedDeptId] = useState<number | undefined>(undefined);
  const [totalUserCount, setTotalUserCount] = useState<number>(0);
  const [deptTree, setDeptTree] = useState<any[]>([]); // 部门树数据（用于TreeSelect）
  const [deptTreeForDeptTree, setDeptTreeForDeptTree] = useState<any[]>([]); // 部门树数据（用于传给DeptTree）
  const [deptLoading, setDeptLoading] = useState<boolean>(false); // 部门数据加载状态

  // 获取部门列表
  const fetchDeptList = async () => {
    setDeptLoading(true);
    try {
      const res = await getSimpleDeptList();
      const treeData = convertDeptListToSelectTree(res);
      setDeptTree(treeData);
      const treeDataForDeptTree = convertDeptListToTree(treeData);
      setDeptTreeForDeptTree(treeDataForDeptTree);
    } catch (error) {
      console.error('获取部门列表失败:', error);
      // TODO：接口获取失败时使用mock数据，联调后移除
      const mockDepts: DeptVO[] = [
        { id: 1, name: '科创中心', parentId: 0, status: 1, sort: 1, leaderUserId: 1, phone: '', email: '', createTime: new Date(), userCount: 10, remark: '' },
        { id: 2, name: 'AI部门', parentId: 1, status: 1, sort: 1, leaderUserId: 1, phone: '', email: '', createTime: new Date(), userCount: 5, remark: '' },
        { id: 3, name: '大数据部门', parentId: 1, status: 1, sort: 2, leaderUserId: 1, phone: '', email: '', createTime: new Date(), userCount: 6, remark: '' },
        { id: 4, name: '时空', parentId: 0, status: 1, sort: 2, leaderUserId: 1, phone: '', email: '', createTime: new Date(), userCount: 7, remark: '' },
        { id: 5, name: 'OB', parentId: 1, status: 1, sort: 2, leaderUserId: 1, phone: '', email: '', createTime: new Date(), userCount: 7, remark: '' },
        { id: 6, name: '工业', parentId: 2, status: 1, sort: 2, leaderUserId: 1, phone: '', email: '', createTime: new Date(), userCount: 7, remark: '' },
      ];
      const treeData = convertDeptListToSelectTree(mockDepts);
      setDeptTree(treeData);
      const treeDataForDeptTree = convertDeptListToTree(treeData);
      setDeptTreeForDeptTree(treeDataForDeptTree);
    } finally {
      setDeptLoading(false);
    }
  };

  // 组件加载时获取部门列表
  useEffect(() => {
    fetchDeptList();
  }, []);

  return (
    <Layout className={styles.userPage}>
      <Sider
        className={styles.leftPanel}
        resizeDirections={['right']}
        style={{ minWidth: 252, maxWidth: 500 }}
        collapsible={false}
        trigger={null}
      >
        <DeptTree
          selectedDeptId={selectedDeptId}
          onDeptSelect={setSelectedDeptId}
          totalUserCount={totalUserCount}
          treeData={deptTreeForDeptTree}
          deptLoading={deptLoading}
        />
      </Sider>
      <Content className={styles.rightPanel}>
        <UserTable
          selectedDeptId={selectedDeptId}
          onTotalUserCountChange={setTotalUserCount}
          deptTree={deptTree}
          deptLoading={deptLoading}
        />
      </Content>
    </Layout>
  );
}