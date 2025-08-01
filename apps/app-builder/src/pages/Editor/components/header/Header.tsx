import AppIcon from '@/assets/images/app_icon.svg';
import activeFormDesignSVG from '@/assets/images/form_design_active_icon.svg';
import defaultFormDesignSVG from '@/assets/images/form_design_default_icon.svg';
import activeListDesignSVG from '@/assets/images/list_design_active_icon.svg';
import defaultListDesignSVG from '@/assets/images/list_design_default_icon.svg';
import activeSourceDataSVG from '@/assets/images/source_data_active_icon.svg';
import defaultSourceDataSVG from '@/assets/images/source_data_default_icon.svg';
import { Button, Tabs } from '@arco-design/web-react';
import { IconArrowLeft } from '@arco-design/web-react/icon';
import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import styles from './index.module.less';

export default function EditorHeader() {
	const navigate = useNavigate();
	const location = useLocation();
	const { t } = useTranslation();

	const tabData = [
		{
			key: 'form-design',
			title: t('formEditor.formDesign'),
			alt: 'Form Design',
			defaultIcon: defaultFormDesignSVG,
			activeIcon: activeFormDesignSVG,
		},
		{
			key: 'list-design',
			title: t('formEditor.listDesign'),
			alt: 'List Design',
			defaultIcon: defaultListDesignSVG,
			activeIcon: activeListDesignSVG,
		},
		{
			key: 'page-setting',
			title: t('formEditor.pageSetting'),
			alt: '',
		},
		{
			key: 'metadata-manage',
			title: t('formEditor.metadataManage'),
			alt: 'Source Data',
			defaultIcon: defaultSourceDataSVG,
			activeIcon: activeSourceDataSVG,
		},
	];

	// Tab 切换
	// 根据当前路径设置 activeTab
	const getTabKeyFromPath = (pathname: string) => {
		if (pathname.includes('onebase/editor/form_editor')) return 'form-design';
		if (pathname.includes('onebase/editor/list_editor')) return 'list-design';
		if (pathname.includes('onebase/editor/page-setting')) return 'page-setting';
		if (pathname.includes('onebase/editor/metadata-manage'))
			return 'metadata-manage';
		return 'form_editor';
	};
	const [activeTab, setActiveTab] = useState(
		getTabKeyFromPath(location.pathname)
	);

	useEffect(() => {
		setActiveTab(getTabKeyFromPath(location.pathname));
	}, [location.pathname]);

	return (
		<div className={styles.editorHeader}>
			{/* 左侧 */}
			<div className={styles.left}>
				<Button shape='circle' type='default' icon={<IconArrowLeft />} />

				<img src={AppIcon} style={{ width: 28, height: 28 }} />

				<span>{t('formEditor.newApplication')}</span>
				<span>&gt;</span>
				<span>页面一</span>
			</div>

			{/* 中间 */}
			<div className={styles.center}>
				<Tabs
					type='line'
					activeTab={activeTab}
					onChange={(key) => {
						setActiveTab(key);
						switch (key) {
							case 'form-design':
								navigate('/onebase/editor/form_editor');
								break;
							case 'list-design':
								navigate('/onebase/editor/list_editor');
								break;
							case 'page-setting':
								navigate('/onebase/editor/page-setting');
								break;
							case 'metadata-manage':
								navigate('/onebase/editor/metadata-manage');
								break;
							default:
								break;
						}
					}}
					size='large'
					inkBarSize={{ width: 106, height: 3 }}
				>
					{tabData.map((tab) => (
						<Tabs.TabPane
							key={tab.key}
							title={
								<div className={styles.tabIcon}>
									<img
										src={
											tab.key === activeTab ? tab.activeIcon : tab.defaultIcon
										}
										alt={tab.alt}
									/>
									{tab.title}
								</div>
							}
						/>
					))}
				</Tabs>
			</div>

			<div className={styles.right}>
				<div className={styles.editorStatus}>{t('formEditor.saved')}</div>
				<Button
					onClick={() => {
						/* 预览逻辑 */
					}}
				>
					{t('createApp.preview')}
				</Button>
				<Button
					type='primary'
					onClick={() => {
						/* 保存逻辑 */
					}}
				>
					{t('common.save')}
				</Button>
			</div>
		</div>
	);
}
