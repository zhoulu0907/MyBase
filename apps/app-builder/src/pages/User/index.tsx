import { Layout } from '@arco-design/web-react';
import { getDeptList, type DeptVO } from '@onebase/platform-center';
import { useEffect, useState } from 'react';
import DeptTreeCmp from './components/DeptTree';
import UserTable from './components/UserTable';
import styles from './index.module.less';
import { listToTree } from '@/utils/tree';

const { Sider, Content } = Layout;

export default function UserPage() {
  const [selectedDeptId, setSelectedDeptId] = useState<number | undefined>(undefined);
  const [totalUserCount, setTotalUserCount] = useState<number>(0);
  const [deptTree, setDeptTree] = useState<any[]>([]);
  const [deptLoading, setDeptLoading] = useState<boolean>(false); // 部门数据加载状态

  // 获取部门列表
  const fetchDeptList = async () => {
    setDeptLoading(true);
    try {
      const res = await getDeptList();
      // 计算所有用户总数:取第一层树节点的用户数之和
      const deptMap = res.reduce((acc:Record<number, DeptVO>, cur:DeptVO) => {
        acc[cur.id] = cur;
        return acc;
      }, {});
      const userCount = res.reduce((acc:number, cur:DeptVO) => !deptMap[cur.parentId] ? acc + cur.userCount : acc, 0);
      setTotalUserCount(userCount);
      const treeData = listToTree(res, {}, true);
      setDeptTree(treeData);
    } finally {
      setDeptLoading(false);
    }
  };

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
        <DeptTreeCmp
          selectedDeptId={selectedDeptId}
          onDeptSelect={setSelectedDeptId}
          totalUserCount={totalUserCount}
          treeData={deptTree}
          deptLoading={deptLoading}
        />
      </Sider>
      <Content className={styles.rightPanel}>
        <UserTable
          selectedDeptId={selectedDeptId}
          deptTree={deptTree}
          deptLoading={deptLoading}
        />
      </Content>
    </Layout>
  );
}
