import EditApp from '@/components/CreateApp';
import styles from './index.module.less';

// 基础设置
const BasicSetting = () => {
	return (
		<div className={styles.basicSetting}>
			<EditApp />
		</div>
	);
};

export default BasicSetting;
