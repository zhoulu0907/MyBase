import { Form, Message, Modal } from '@arco-design/web-react';
import { updateApplication, type Application, type UpdateApplicationReq } from '@onebase/app';
import { useAppStore } from '@/store';
import type { Options } from '@/components/CreateApp/const';
import AppForm from '@/components/CreateApp/components/AppForm';
import styles from './index.module.less';

interface IProps {
  editModalVisible: boolean;
  setEditModalVisible: any;
  appData: Application;
}

const EditAppModal = (props: IProps) => {
  const { editModalVisible, setEditModalVisible, appData } = props;
  const { curAppInfo, setCurAppInfo } = useAppStore();

  const [form] = Form.useForm();

  /* 基础设置编辑 */
  const handleSaveApp = async () => {
    form.validate(async (error, data) => {
      try {
        if (error !== null) return;
        const { appName, description, tagIds, themeColor } = data;
        const params: UpdateApplicationReq = {
          ...appData,
          appName,
          description,
          iconColor: appData.iconColor || '',
          iconName: appData.iconName || '',
          tagIds: tagIds?.map((t: Options) => t.value),
          themeColor
        };
        const res = await updateApplication(params);
        if (res) {
          Message.success('保存成功');
          setCurAppInfo({
            ...curAppInfo,
            appName: appName || '--'
          });
        }
        setEditModalVisible(false);
      } catch (_error) {
        console.error('保存失败 _error:', _error);
      } finally {
      }
    });
  };

  return (
    <Modal
      className={styles.editAppModal}
      title={<span className={styles.modalTitleLeft}>修改基础信息</span>}
      visible={editModalVisible}
      onOk={() => handleSaveApp()}
      onCancel={() => setEditModalVisible(false)}
      autoFocus={false}
      focusLock={true}
    >
      <Form form={form}>
        <AppForm form={form} isEditModalVisible={editModalVisible} appData={appData} />
      </Form>
    </Modal>
  );
};

export default EditAppModal;
