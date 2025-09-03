import { useState, useEffect } from 'react';
import { type FormInstance } from '@arco-design/web-react';
import EditApp from '@/components/CreateApp';
import { type Application } from '@onebase/app';
import { getDatasourceList } from '@onebase/app';
import styles from './index.module.less';

interface IProps {
  form: FormInstance;
  data: Application;
}

// 基础设置
const BasicSetting = (props: IProps) => {
  const { form, data } = props;
  const [isOwnDatasource, setIsOwnDatasource] = useState<boolean>(false); // 是否使用自有数据源

  useEffect(() => {
    if (data && data?.id) {
      getDatasourceDetail();
    }
  }, [data]);

  const getDatasourceDetail = async () => {
    const res = await getDatasourceList({ appId: data?.id });

    // 现阶段一个应用只有一个数据源
    const curDatasourceOrigin = res[0].datasourceOrigin;
    setIsOwnDatasource(curDatasourceOrigin === 1);
  };

  return (
    <div className={styles.basicSetting}>
      <EditApp
        form={form}
        data={data}
        status="update"
        previewBgColor="#FFF"
        dataSourceCreated={false}
        isOwnDatasource={isOwnDatasource}
      />
    </div>
  );
};

export default BasicSetting;
