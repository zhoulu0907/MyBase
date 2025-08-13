import { useState } from 'react';
import { Button, Table, Popconfirm, type TableColumnProps, Message } from '@arco-design/web-react';
import { IconPlusCircle } from '@arco-design/web-react/icon';
import { roleDeleteUser, type RoleDeleteUserReq, type Role } from '@onebase/app';
import styles from './index.module.less';

const data = [
  {
    key: '1',
    name: 'Jane Doe',
    phone: 23000,
    email: 'jane.doe@example.com',
    department: '32 Park Road, London'
  },
  {
    key: '2',
    name: 'Alisa Ross',
    phone: 25000,
    email: 'alisa.ross@example.com',
    department: '35 Park Road, London'
  },
  {
    key: '3',
    name: 'Kevin Sandra',
    phone: 22000,
    email: 'kevin.sandra@example.com',
    department: '31 Park Road, London'
  },
  {
    key: '4',
    name: 'Ed Hellen',
    phone: 17000,
    email: 'ed.hellen@example.com',
    department: '42 Park Road, London'
  },
  {
    key: '5',
    name: 'William Smith',
    phone: 27000,
    email: 'william.smith@example.com',
    department: '62 Park Road, London'
  },
  {
    key: '6',
    name: 'William Smith',
    phone: 27000,
    email: 'william.smith@example.com',
    department: '62 Park Road, London'
  }
];

interface IProps {
  roleInfo: Role | undefined;
  onAddMembers: () => void;
}
// 管理员面板
const Admin = (props: IProps) => {
  const { roleInfo, onAddMembers } = props;

  const [deleteLoading, setDeleteLoading] = useState<boolean>(false);

  const handleDeleteUser = async (userId: number) => {
    try {
      setDeleteLoading(true);
      const params: RoleDeleteUserReq = {
        roleId: roleInfo?.id!,
        userIds: [userId]
      };
      const res = await roleDeleteUser(params);
      if (res) {
        Message.success('移除成功');
      }
    } catch (error) {
      Message.error('移除失败');
    } finally {
      setDeleteLoading(false);
    }
  };

  const columns: TableColumnProps[] = [
    {
      title: '序号',
      dataIndex: 'key',
      align: 'center',
      width: 100
    },
    {
      title: '姓名',
      dataIndex: 'name',
      align: 'center'
    },
    {
      title: '手机号',
      dataIndex: 'phone',
      align: 'center'
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      ellipsis: true,
      align: 'center'
    },
    {
      title: '部门',
      dataIndex: 'department',
      align: 'center'
    },
    {
      title: '操作',
      dataIndex: 'op',
      align: 'center',
      render: (_, _record) => (
        <Popconfirm
          focusLock
          title="移除成员"
          content="确定要移除这个成员吗？"
          okButtonProps={{
            loading: deleteLoading
          }}
          onOk={() => {
            handleDeleteUser(_record.key);
          }}
        >
          <Button type="text">移除</Button>
        </Popconfirm>
      )
    }
  ];

  console.log('admin info', roleInfo);
  return (
    <div className={styles.adminWrapper}>
      <div className={styles.header}>
        <Button type="primary" icon={<IconPlusCircle />} onClick={onAddMembers}>
          添加成员
        </Button>
      </div>
      <Table className={styles.table} columns={columns} data={data} />;
    </div>
  );
};

export default Admin;
