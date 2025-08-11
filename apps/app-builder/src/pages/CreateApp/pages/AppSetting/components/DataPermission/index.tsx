import { useState, type FC } from 'react';
import {
    Divider,
    Tag,
    Space,
    Button,
    Form,
    Popconfirm,
} from '@arco-design/web-react';
import {
    IconEdit,
    IconDelete,
    IconPlusCircle,
} from '@arco-design/web-react/icon';
import PermissionModal from './modal';
import styles from './index.module.less';

const permission = [
    {
        name: '默认权限',
        subTitle: '系统提供的默认权限',
        operation: {
            isOwn: true, // 本人
            purview: 'owner', // 权限范围-拥有者
            viewable: true,
            operable: true,
        },
    },
];

// 数据权限
const DataPermission: FC = () => {
    const [form] = Form.useForm();
    const [visible, setVisible] = useState<boolean>(false);
    const [status, setStatus] = useState<'create' | 'edit'>('create');

    return (
        <div className={styles.dataPermission}>
            {permission.map((perm) => (
                <div className={styles.permItem}>
                    <div className={styles.top}>
                        <div className={styles.left}>
                            <div className={styles.title}>{perm.name}</div>
                            <div className={styles.subtitle}>{perm.subTitle}</div>
                        </div>
                        <div className={styles.right}>
                            <IconEdit
                                style={{ fontSize: 20, color: '#4E5969', cursor: 'pointer' }}
                                onClick={() => {
                                    setVisible(true);
                                    setStatus('edit');
                                }}
                            />
                            <Popconfirm
                                focusLock
                                title='删除数据权限'
                                content='确定要删除这条数据吗？'
                                onOk={() => {
                                    console.log('确认删除');
                                }}
                                onCancel={() => {
                                    console.log('取消删除');
                                }}
                            >
                                <IconDelete
                                    style={{
                                        fontSize: 20,
                                        color: '#F53F3F',
                                        marginLeft: 10,
                                        cursor: 'pointer',
                                    }}
                                />
                            </Popconfirm>
                        </div>
                    </div>
                    <Divider />
                    <div className={styles.bottom}>
                        <span className={styles.name}>操作权限：</span>
                        <span className={styles.desc}>
                            <Space wrap>
                                当前角色可
                                <Tag color='#F2F3F5' style={{ color: '#1D2129' }}>
                                    查看
                                </Tag>
                                <Tag color='#F2F3F5' style={{ color: '#1D2129' }}>
                                    操作
                                </Tag>
                                <Tag color='#E8F3FF' style={{ color: '#3C7EFF' }}>
                                    拥有者
                                </Tag>
                                是
                                <Tag color='#FFF7E8' style={{ color: '#FF7D00' }}>
                                    本人
                                </Tag>
                                且
                                <Tag color='#E8FFEA' style={{ color: '#00B42A' }}>
                                    归档状态 等于 已归档
                                </Tag>
                                <Tag color='#E8FFEA' style={{ color: '#00B42A' }}>
                                    归档人 等于 巫炘
                                </Tag>
                                的数据
                            </Space>
                        </span>
                    </div>
                </div>
            ))}
            <Button
                type='outline'
                size='large'
                icon={<IconPlusCircle fontSize={20} />}
                style={{ display: 'flex', alignItems: 'center' }}
                onClick={() => setVisible(true)}
            >
                添加权限组
            </Button>
            <PermissionModal
                form={form}
                status={status}
                visible={visible}
                onClose={() => setVisible(false)}
            />
        </div>
    );
};

export default DataPermission;
