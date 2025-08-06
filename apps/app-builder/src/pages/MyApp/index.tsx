import appDeleteSVG from '@/assets/images/app_delete.svg';
import appIconSVG from '@/assets/images/app_icon.svg';
import appMenuSVG from '@/assets/images/app_menu.svg';
import {
	Avatar,
	Button,
	Input,
	List,
	Modal,
	Pagination,
	Select,
	Tag,
	Form,
} from '@arco-design/web-react';
import { IconPlusCircle, IconSearch } from '@arco-design/web-react/icon';
import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import CreateApp from '@/components/CreateApp';
import styles from './index.module.less';

const Option = Select.Option;
const appOptions = ['全部应用', '我创建的'];
const createTimeOptions = ['按创建时间排序', '按更新时间排序'];
const statusOptions = ['全部状态', '开发中', '已发布'];

// 模拟200条数据
const allData = Array.from({ length: 200 }, (_, i) => ({
	id: i + 1,
	content: `项目 ${i + 1}`,
}));

const MyAppPage: React.FC = () => {
	const [form] = Form.useForm();
	const navigate = useNavigate();
	const { t } = useTranslation();

	const [currentPage, setCurrentPage] = useState<number>(1);
	const [pageSize, setPageSize] = useState<number>(12);
	const [inputValue, setInputValue] = useState<string>('');
	const [deleteVisible, setDeleteVisible] = useState<boolean>(false);
	const [createVisible, setCreateVisible] = useState<boolean>(true);
	// const [reqFilter, setReqFilter] = useState({
	// 	appOwn: "all",
	// 	timeSort: "create",
	// 	status: "all",
	// });

	// 计算当前页数据
	const showData = allData.slice(
		(currentPage - 1) * pageSize,
		currentPage * pageSize
	);

	return (
		<div className={styles.myAppPage}>
			<div className={styles.myAppPageHeader}>
				<div className={styles.myAppWelcome}>Hi 巫炘，晚上好！</div>
				<Button
					type='primary'
					size='large'
					icon={<IconPlusCircle />}
					className={styles.createAppButton}
					// onClick={() => navigate("/onebase/create-app/data-factory")}
					onClick={() => setCreateVisible(true)}
				>
					{t('myApp.createApp')}
				</Button>
			</div>

			<div className={styles.myAppContainer}>
				<div className={styles.myAppFilter}>
					<Input
						allowClear
						style={{ width: 316, height: 42, borderRadius: 6 }}
						suffix={<IconSearch />}
						placeholder='请输入应用名称'
					/>

					{/* 筛选下拉框 */}
					<div>
						<Select
							placeholder='全部应用'
							bordered={false}
							style={{ width: 100 }}
							onChange={(value) => console.log(value)}
						>
							{appOptions.map((option, index) => (
								<Option key={option} disabled={index === 3} value={option}>
									{option}
								</Option>
							))}
						</Select>
						<Select
							placeholder='按创建时间排序'
							bordered={false}
							style={{ width: 138 }}
							onChange={(value) => console.log(value)}
						>
							{createTimeOptions.map((option, index) => (
								<Option key={option} disabled={index === 3} value={option}>
									{option}
								</Option>
							))}
						</Select>
						<Select
							placeholder='全部状态'
							bordered={false}
							style={{ width: 100 }}
							onChange={(value) => console.log(value)}
						>
							{statusOptions.map((option, index) => (
								<Option key={option} disabled={index === 3} value={option}>
									{option}
								</Option>
							))}
						</Select>
					</div>
				</div>

				{/* 我的应用列表 */}
				<div className={styles.myAppList}>
					<List
						grid={{
							sm: 24,
							md: 12,
							lg: 8,
							xl: 6,
						}}
						dataSource={showData}
						bordered={false}
						render={(_item, index) => (
							<List.Item key={index}>
								<div className={styles.myAppCard} key={index}>
									<div className={styles.myAppCardHeader}>
										<div className={styles.myAppName}>
											<img src={appIconSVG} alt='应用图标' />
											<div>工时管理系统{index}</div>
										</div>
										<Tag
											style={{
												fontSize: 11,
												color: 'rgba(42, 130, 228, 1)',
											}}
										>
											开发中
										</Tag>
									</div>

									<div className={styles.myAppCardBody}>
										<div className={styles.myAppDesc}>
											这是一段描述这是一段描述这是一段描述这是一段描述这是一段描述这是一段描述这…这是一段描述这是一段描述这是一段描述这是一段描述这是一段描述这是一段描述这…
										</div>
										<div className={styles.myAppTime}>2025-07-21 18:20:44</div>
									</div>

									<div className={styles.myAppCardFooter}>
										<div className={styles.myAppCreator}>
											<Avatar
												size={20}
												style={{
													backgroundColor: '#4FAE7B',
													marginRight: 6,
												}}
											>
												U
											</Avatar>
											<div>巫炘</div>
										</div>

										<div className={styles.myAppOperate}>
											<img src={appMenuSVG} alt='菜单' />
											<img
												src={appDeleteSVG}
												alt='删除'
												onClick={() => setDeleteVisible(true)}
											/>
										</div>
									</div>
								</div>
							</List.Item>
						)}
					/>
				</div>

				<Pagination
					total={allData.length}
					current={currentPage}
					pageSize={pageSize}
					onChange={(page, pSize) => {
						setCurrentPage(page);
						setPageSize(pSize);
					}}
					showTotal={(total) => <span>{`总共：${total}项`}</span>}
					style={{
						marginBottom: 20,
						width: '100%',
						padding: '0 20px',
						boxSizing: 'border-box',
						justifyContent: 'space-between',
					}}
				/>
			</div>

			<Modal
				title='确认删除应用'
				visible={deleteVisible}
				onOk={() => setDeleteVisible(false)}
				onCancel={() => setDeleteVisible(false)}
				autoFocus={false}
				focusLock={true}
				confirmLoading={false}
				okButtonProps={{
					disabled: inputValue?.trim().length === 0,
					style: {
						backgroundColor: '#FF4D4F', // 自定义背景色
						borderColor: '#FF4D4F', // 自定义边框色
					},
				}}
			>
				<div
					style={{
						height: 171,
						display: 'flex',
						flexDirection: 'column',
						justifyContent: 'space-between',
					}}
				>
					<div>
						删除应用则页面及数据将一并删除，并且无法还原。
						<br />
						为防止误操作，如确定删除，请输入
						<strong>&quot;&lt;应用名称&gt;&quot;</strong>进行确认：
					</div>
					<Input
						value={inputValue}
						style={{ width: 476 }}
						allowClear
						placeholder='请输入要删除的应用名称'
						onChange={setInputValue}
					/>
				</div>
			</Modal>

			<Modal
				title={<div style={{ textAlign: 'left' }}>创建空白应用</div>}
				visible={createVisible}
				simple
				footer={
					<div style={{ textAlign: 'right' }}>
						<Button
							type='default'
							onClick={() => setCreateVisible(false)}
							style={{ marginRight: 12 }}
						>
							取消
						</Button>
						<Button
							type='primary'
							htmlType='submit'
							onClick={() => {
								form.validate((errors, data) => {
									console.log(errors, data);
								});
							}}
						>
							创建
						</Button>
					</div>
				}
				confirmLoading={true}
				onCancel={() => setCreateVisible(false)}
				className={styles.createAppModal}
			>
				<div className={styles.createAppWrapper}>
					<CreateApp form={form} previewBgColor='#F2F3F5BF' />
				</div>
			</Modal>
		</div>
	);
};

export default MyAppPage;
