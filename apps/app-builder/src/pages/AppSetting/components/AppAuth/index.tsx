import { useState, type FC } from 'react';
import { Button, Menu, Modal } from '@arco-design/web-react';
import {
	IconPlus,
	IconPlusCircle,
	IconUser,
} from '@arco-design/web-react/icon';
import Admin from '../Admin';
import User from '../User';
import styles from './index.module.less';

const MenuItem = Menu.Item;

const AppAuth: FC = () => {
	const [visible, setVisible] = useState<boolean>(false);
	const [activeTab, setActiveTab] = useState<string>('user');

	const handleSelectmenu = (val: string) => {
		if (val === 'add') return;
		setActiveTab(val);
	};

	return (
		<div className={styles.appAuth}>
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
						<Button type='primary' icon={<IconPlusCircle />}>
							添加成员
						</Button>
					</div>
					{activeTab === 'admin' && <Admin />}
					{activeTab === 'user' && <User />}
				</div>
			</div>

			<Modal
				title='选择联系人'
				visible={visible}
				onOk={() => setVisible(false)}
				onCancel={() => setVisible(false)}
				autoFocus={false}
				focusLock={true}
			>
				xxx
			</Modal>
		</div>
	);
};

export default AppAuth;
