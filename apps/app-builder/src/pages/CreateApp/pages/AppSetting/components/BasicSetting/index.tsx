import { useState, useRef } from 'react';
import { Modal, Button, type FormInstance } from '@arco-design/web-react';
import EditApp from '@/components/CreateApp';
import { type Application } from '@onebase/app';
import CreateDataSource, { type DataSourceHandle } from '@/components/CreateDataSource';
import styles from './index.module.less';

interface IProps {
  form: FormInstance;
  data: Application | undefined;
}

// 基础设置
const BasicSetting = (props: IProps) => {
  const { form, data } = props;

  const createDatasourceRef = useRef<DataSourceHandle>(null);
  const [visible, setVisible] = useState<boolean>(false);
  const [updating, setUpdating] = useState<boolean>(false);

  // todo
  // useEffect(() => {
  //   if (data && data?.id) {
  //     getDatasourceDetail(data.id);
  //   }
  // }, [data]);

  // const getDatasourceDetail = async (id: string) => {
  //   const res = getDatasource(id);
  //   console.log(res, 'res');
  // };

  const handleUpdate = async () => {
    form.validate((error, data) => {
      if (!error === null) return;
    });
  };

  return (
    <div className={styles.basicSetting}>
      <EditApp
        form={form}
        data={data}
        status="update"
        previewBgColor="#FFF"
        dataSourceCreated={false}
        onCreateDatasource={() => setVisible(true)}
      />

      <Modal
        title={<div style={{ textAlign: 'left' }}>创建/编辑外部数据源</div>}
        visible={visible}
        simple
        unmountOnExit
        footer={
          <div style={{ textAlign: 'right' }}>
            <Button type="default" onClick={() => setVisible(false)} style={{ marginRight: 12 }}>
              取消
            </Button>
            <Button type="primary" loading={updating} onClick={handleUpdate}>
              创建
            </Button>
          </div>
        }
        confirmLoading={true}
        onCancel={() => setVisible(false)}
        style={{
          width: 600
        }}
      >
        <CreateDataSource ref={createDatasourceRef} />
      </Modal>
    </div>
  );
};

export default BasicSetting;
