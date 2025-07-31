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
  const [_deptList, setDeptList] = useState<DeptVO[]>([]); // 部门列表数据
  const [deptTree, setDeptTree] = useState<any[]>([]); // 部门树数据（用于TreeSelect）
  const [deptTreeForDeptTree, setDeptTreeForDeptTree] = useState<any[]>([]); // 部门树数据（用于DeptTree）
  const [deptLoading, setDeptLoading] = useState<boolean>(false); // 部门数据加载状态

  // 获取部门列表
  const fetchDeptList = async () => {
    setDeptLoading(true);
    try {
      const res = await getSimpleDeptList();
      setDeptList(res);
      // 转换为树形结构（用于UserFormModal中的TreeSelect）
      const treeData = convertDeptListToSelectTree(res);
      setDeptTree(treeData);

      // 转换为树形结构（用于DeptTree中的Tree）
      // 将部门列表转换为带用户计数的列表
      const deptWithUserCountList = res.map(dept => ({
        ...dept,
        userCount: 0 // 临时设置为0，实际应该从接口获取或单独查询
      }));
      const treeDataForDeptTree = convertDeptListToTree(deptWithUserCountList);
      setDeptTreeForDeptTree(treeDataForDeptTree);
    } catch (error) {
      console.error('获取部门列表失败:', error);
      // 接口获取失败时使用mock数据
      const mockDepts: DeptVO[] = [
        { id: 1, name: '科创中心', parentId: 0, status: 1, sort: 1, leaderUserId: 1, phone: '', email: '', createTime: new Date(), userCount: 0 },
        { id: 2, name: 'AI部门', parentId: 1, status: 1, sort: 1, leaderUserId: 1, phone: '', email: '', createTime: new Date(), userCount: 0 },
        { id: 3, name: '大数据部门', parentId: 1, status: 1, sort: 2, leaderUserId: 1, phone: '', email: '', createTime: new Date(), userCount: 0 },
        { id: 4, name: '前端部门', parentId: 0, status: 1, sort: 2, leaderUserId: 1, phone: '', email: '', createTime: new Date(), userCount: 0 },
      ];
      setDeptList(mockDepts);
      const treeData = convertDeptListToSelectTree(mockDepts);
      setDeptTree(treeData);

      // 转换为树形结构（用于DeptTree中的Tree）
      // 将部门列表转换为带用户计数的列表
      const deptWithUserCountList = mockDepts.map(dept => ({
        ...dept,
        userCount: 0 // 临时设置为0，实际应该从接口获取或单独查询
      }));
      const treeDataForDeptTree = convertDeptListToTree(deptWithUserCountList);
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