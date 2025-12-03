import PlaceholderPanel from '@/components/PlaceholderPanel';
import { listToTree } from '@/utils/tree';
import { Layout } from '@arco-design/web-react';
import { CORP_USER_QUERY, hasPermission } from '@onebase/common';
import { getCorpDeptList, getUserPageInCorp } from '@onebase/platform-center';
import { useEffect, useState } from 'react';
import DeptTreeCmp from './components/DeptTree';
import UserTable from './components/UserTable';
import styles from './index.module.less';
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
      const res = await getCorpDeptList();

      const total = await getUserContent();
      setTotalUserCount(total);
      const treeData = listToTree(res, {}, true);
      setDeptTree(treeData);
    } finally {
      setDeptLoading(false);
    }
  };

  const getUserContent = async () => {
    const resq = await getUserPageInCorp({ pageNo: 1, pageSize: 10 });
    return resq.total;
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
        <PlaceholderPanel hasPermission={hasPermission(CORP_USER_QUERY)}>
          <DeptTreeCmp
            selectedDeptId={selectedDeptId}
            onDeptSelect={setSelectedDeptId}
            totalUserCount={totalUserCount}
            treeData={deptTree}
            deptLoading={deptLoading}
          />
        </PlaceholderPanel>
      </Sider>
      <Content className={styles.rightPanel}>
        <PlaceholderPanel hasPermission={hasPermission(CORP_USER_QUERY)}>
          <UserTable
            selectedDeptId={selectedDeptId}
            deptTree={deptTree}
            deptLoading={deptLoading}
            onRefreshDept={() => fetchDeptList()}
          />
        </PlaceholderPanel>
      </Content>
    </Layout>
  );
}
