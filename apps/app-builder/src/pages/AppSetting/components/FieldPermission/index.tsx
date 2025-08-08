import { useState, type FC } from 'react';
import { Radio, Checkbox, Divider, Grid, Form } from '@arco-design/web-react';
import {
	IconCalendar,
	IconAttachment,
	IconUser,
	IconLocation,
} from '@arco-design/web-react/icon';
import styles from './index.module.less';

const Row = Grid.Row;
const Col = Grid.Col;
const RadioGroup = Radio.Group;

// 字段配置
const fieldConfig = [
	{ key: 'activityLocation', name: '活动地点', icon: <IconAttachment /> },
	{ key: 'activityTime', name: '活动时间', icon: <IconCalendar /> },
	{ key: 'participantLimit', name: '参与人数上限', icon: <IconUser /> },
];
const operationConfig = [
	{ key: 'attachment', name: '活动方案附件', icon: <IconAttachment /> },
];

// 初始表单值
const initialValues = {
	fieldPermissions: {
		activityLocation: { readable: true, editable: true },
		activityTime: { readable: false, editable: false },
		participantLimit: { readable: true, editable: false },
	},
	operationPermissions: {
		attachment: { downloadable: true },
	},
};

// 管理员面板
const FuncPermission: FC = () => {
	const [form] = Form.useForm();

	const [_editableValue, setEditableVValue] = useState(['1', '2', '3']);
	const [_downloadableValue, setDownloadableValue] = useState(['1', '2', '3']);
	const [checkReadableAll, setCheckReadableAll] = useState(false);
	const [checkEditableAll, setCheckEditableAll] = useState(false);
	const [checkDownloadableAll, setCheckDownloadableAll] = useState(false);

	const [indeterminateReadable, setIndeterminateReadable] = useState(true);
	const [indeterminateEditable, setIndeterminateEditable] = useState(true);
	const [indeterminateDownloadable, setIndeterminateDownloadable] =
		useState(true);

	function onChangeReadableAll(checked: boolean) {
		const formData = form.getFieldsValue();

		const updatedFieldPermissions = Object.fromEntries(
			Object.entries(formData.fieldPermissions).map(([key, value]) => [
				key,
				{ ...value, readable: checked },
			])
		);

        setCheckReadableAll(checked);
        setIndeterminateReadable(false);
		form.setFieldValue('fieldPermissions', updatedFieldPermissions);

	}

	function onChangeEditableAll(checked: boolean) {
		if (checked) {
			setIndeterminateEditable(false);
			setCheckEditableAll(true);
			setEditableVValue(['1', '2', '3', '4']);
		} else {
			setIndeterminateEditable(false);
			setCheckEditableAll(false);
			setEditableVValue([]);
		}
	}

	function onChangeDownloadableAll(checked: boolean) {
		if (checked) {
			setIndeterminateDownloadable(false);
			setCheckDownloadableAll(true);
			setDownloadableValue(['1', '2', '3', '4']);
		} else {
			setIndeterminateDownloadable(false);
			setCheckDownloadableAll(false);
			setDownloadableValue([]);
		}
	}

	// 提交处理
	const handleSubmit = (values) => {
		console.log('权限配置已提交:', values);
	};

	return (
		<>
			<RadioGroup direction='vertical' name='lang' defaultValue='custom'>
				<Radio value='admin'>所有字段内容可操作</Radio>
				<Radio value='custom'>自定义权限</Radio>
			</RadioGroup>

			<Form
				form={form}
				initialValues={initialValues}
				onSubmit={handleSubmit}
				style={{ marginTop: 20 }}
			>
				<Form.Item
					field='fieldPermissions'
					label='字段内容权限'
					layout='vertical'
					shouldUpdate
				>
					<div className={styles.table}>
						<Row>
							<Col span={8}></Col>
							<Col span={4}>
								<Checkbox
									onChange={onChangeReadableAll}
									checked={checkReadableAll}
									indeterminate={indeterminateReadable}
								>
									可阅读
								</Checkbox>
							</Col>
							<Col span={4}>
								<Checkbox
									onChange={onChangeEditableAll}
									checked={checkEditableAll}
									indeterminate={indeterminateEditable}
								>
									可编辑
								</Checkbox>
							</Col>
						</Row>
						<Divider />
						{fieldConfig.map((field) => {
							return (
								<Row className={styles.rowItem} key={field.key}>
									<Col span={8}>
										<IconLocation style={{ marginRight: 8 }} />
										<span>{field.name}</span>
									</Col>

									{/* 可阅读权限 */}
									<Col span={4}>
										<Form.Item
											field={`fieldPermissions.${field.key}.readable`}
											triggerPropName='checked'
											noStyle
										>
											<Checkbox />
										</Form.Item>
									</Col>

									{/* 可编辑权限 */}
									<Col span={4}>
										<Form.Item
											field={`fieldPermissions.${field.key}.editable`}
											triggerPropName='checked'
											noStyle
										>
											<Checkbox />
										</Form.Item>
									</Col>
								</Row>
							);
						})}
					</div>
				</Form.Item>
				<Form.Item
					field='operationPermissions'
					label='操作权限'
					layout='vertical'
					shouldUpdate
				>
					<div className={styles.table}>
						<Row className={styles.tableTitle}>
							<Col span={8}></Col>
							<Col span={4}>
								<Checkbox
									onChange={onChangeDownloadableAll}
									checked={checkDownloadableAll}
									indeterminate={indeterminateDownloadable}
								>
									可下载
								</Checkbox>
							</Col>
						</Row>
						<Divider />
						{operationConfig.map((field) => (
							<Row className={styles.rowItem} key={field.key}>
								<Col span={8}>
									<IconAttachment style={{ marginRight: 8 }} />
									<span>{field.name}</span>
								</Col>

								{/* 可下载权限 */}
								<Col span={4}>
									<Form.Item
										field={`operationPermissions.${field.key}.downloadable`}
										triggerPropName='checked'
										noStyle
									>
										<Checkbox />
									</Form.Item>
								</Col>
							</Row>
						))}
					</div>
				</Form.Item>
			</Form>
		</>
	);
};

export default FuncPermission;
