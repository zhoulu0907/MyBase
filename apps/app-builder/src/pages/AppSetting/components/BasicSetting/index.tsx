import { type FormInstance } from '@arco-design/web-react';
import EditApp from '@/components/CreateApp';
import styles from './index.module.less';

interface IProps {
    form: FormInstance;
}

// 基础设置
const BasicSetting = (props: IProps) => {
    const { form } = props;
	return (
		<div className={styles.basicSetting}>
      <EditApp form={form} previewBgColor='#F2F3F5BF' />
		</div>
	);
};

export default BasicSetting;
