import { useState, type FC } from 'react';
import {
	Button,
	Menu,
	Modal,
	Input,
	Tree,
	Space,
	List,
} from '@arco-design/web-react';
import {
	IconPlus,
	IconUser,
	IconClose,
	IconBranch,
	IconPlusCircle,
} from '@arco-design/web-react/icon';
import Admin from '../Admin';
import User from '../User';
import styles from './index.module.less';

const MenuItem = Menu.Item;

// 应用权限
const AppPermission: FC = () => {
	const [visible, setVisible] = useState<boolean>(false);
	const [activeTab, setActiveTab] = useState<string>('admin');

	const handleSelectmenu = (val: string) => {
		if (val === 'add') {
			return;
		}
		setActiveTab(val);
	};

	return (
		<div className={styles.AppPermission}>
			<div className={styles.left}>
				<Menu
					className={styles.menu}
					defaultSelectedKeys={['admin']}
					onClickMenuItem={handleSelectmenu}
				>
					<div className={styles.user}>
						<label>管理员角色</label>
						<MenuItem key='admin'>
							<IconUser />
							管理员
						</MenuItem>
					</div>
					<div>
						<label>用户角色</label>
						<MenuItem key='user'>
							<IconUser />
							普通用户
						</MenuItem>
						<MenuItem key='add' className={styles.add}>
							<IconPlus style={{ color: 'rgb(var(--primary-6))' }} />
							添加角色
						</MenuItem>
					</div>
				</Menu>
			</div>
			<div className={styles.right}>
				<div className={styles.admin}>
					<div className={styles.header}>
						<div className={styles.headerTitle}>管理员</div>
						<Button
							type='primary'
							size='large'
							icon={<IconPlusCircle />}
							onClick={() => setVisible(true)}
						>
							添加成员
						</Button>
					</div>
					{activeTab === 'admin' && <Admin />}
					{activeTab === 'user' && <User />}
				</div>
			</div>

			<Modal
				title={<div style={{ textAlign: 'left' }}>选择成员</div>}
				onOk={() => setVisible(false)}
				onCancel={() => setVisible(false)}
				visible={visible}
				autoFocus={false}
				focusLock={true}
				simple
				closable={true}
				style={{ width: 716 }}
				maskClosable={true}
				footer={
					<div style={{ textAlign: 'right' }}>
						<Button
							type='default'
							onClick={() => setVisible(false)}
							style={{ marginRight: 12 }}
						>
							取消
						</Button>
						<Button type='primary' onClick={() => setVisible(false)}>
							确认
						</Button>
					</div>
				}
			>
				<SelectContactModal />
			</Modal>
		</div>
	);
};

// 树形数据
const treeData = [
	{
		key: '1',
		title: '帮助中心测试',
		icon: <IconBranch />,
		children: [{ key: '2', title: '总裁办', icon: <IconBranch /> }],
	},
	{
		key: '3',
		title: '产品中心',
		icon: <IconBranch />,
		children: [
			{
				key: '4',
				title: '产品1部',
				icon: <IconBranch />,
				children: [
					{ key: '5', title: '陈奕迅', icon: <IconUser /> },
					{ key: '6', title: '刘德华', icon: <IconUser /> },
					{ key: '7', title: '刘德华', icon: <IconUser /> },
					{ key: '8', title: '刘德华', icon: <IconUser /> },
					{ key: '9', title: '刘德华', icon: <IconUser /> },
					{ key: '10', title: '刘德华', icon: <IconUser /> },
					{ key: '11', title: '刘德华', icon: <IconUser /> },
					{ key: '12', title: '刘德华', icon: <IconUser /> },
					{ key: '13', title: '刘德华', icon: <IconUser /> },
					{ key: '14', title: '刘德华', icon: <IconUser /> },
					{ key: '15', title: '刘德华', icon: <IconUser /> },
					{ key: '16', title: '刘德华', icon: <IconUser /> },
					{ key: '17', title: '刘德华', icon: <IconUser /> },
					{ key: '18', title: '刘德华', icon: <IconUser /> },
				],
			},
		],
	},
	{ key: '19', title: '研发中心', icon: <IconBranch /> },
	{ key: '20', title: '销售部', icon: <IconBranch /> },
];

// key 到 title 的映射（右侧显示用）
const keyTitleMap = {};
// @ts-ignore
function buildKeyTitleMap(nodes) {
	// @ts-ignore
	nodes.forEach((node) => {
		// @ts-ignore
		keyTitleMap[node.key] = node.title;
		if (node.children) buildKeyTitleMap(node.children);
	});
}
buildKeyTitleMap(treeData);
const SelectContactModal = () => {
	const [_activeTab, setActiveTab] = useState('0');
	const [selectedKeys, setSelectedKeys] = useState<string[]>([]);

	// 树节点点击事件
	// @ts-ignore
	const handleTreeSelect = (selectedKeysArr, { selected, node }) => {
		const key = node.key;
		// 只有叶子节点才可添加 node.props.isLeaf
		if (selected && !selectedKeys.includes(key)) {
			setSelectedKeys([...selectedKeys, key]);
		}
	};

	// 右侧移除
	// @ts-ignore
	const handleRemove = (key) => {
		setSelectedKeys(selectedKeys.filter((k) => k !== key));
	};

	return (
		<div className={styles.content}>
			<div className={styles.left}>
				<Input placeholder='搜索用户、群组、部门或用户组' />

				<Tree
					className={styles.tree}
					treeData={treeData}
					blockNode
					selectable
					selectedKeys={[]}
					onSelect={handleTreeSelect}
					icon={(node: any) => node.icon}
				/>
			</div>

			<div className={styles.right}>
				已选择：{selectedKeys.length}个<br />
				<br />
				<List
					bordered={false}
					dataSource={selectedKeys}
                    style={{
                    }}
                    wrapperStyle={{
                        // height: '100%',
                        overflow: 'auto',
                        flex: 1,

                    }}
					render={(key) => (
						<Space
							key={key}
							style={{
								display: 'flex',
								alignItems: 'center',
								justifyContent: 'space-between',
								marginBottom: 12,
                                paddingRight: 24,
							}}
						>
							{/* @ts-ignore */}
							{keyTitleMap[key]}
							<Button
								type='text'
								size='mini'
								shape='circle'
								icon={<IconClose />}
								onClick={() => handleRemove(key)}
							/>
						</Space>
					)}
				/>
			</div>
		</div>
	);
};

export default AppPermission;
