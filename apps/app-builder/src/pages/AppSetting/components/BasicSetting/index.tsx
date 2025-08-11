import { type FormInstance } from '@arco-design/web-react';
import EditApp from '@/components/CreateApp';
import { type Application } from '@onebase/app';
import styles from './index.module.less';

interface IProps {
  form: FormInstance;
  data: Application | undefined;
}

// 基础设置
const BasicSetting = (props: IProps) => {
  const { form, data } = props;
  return (
    <div className={styles.basicSetting}>
      <EditApp form={form} data={data} status="update" previewBgColor="#FFF" />
    </div>
  );
};

export default BasicSetting;
