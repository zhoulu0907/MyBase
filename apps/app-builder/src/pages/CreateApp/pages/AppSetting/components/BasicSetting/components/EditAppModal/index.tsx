import { Button, Form, Input, Message, Modal, Select, Space } from '@arco-design/web-react';
import styles from './index.module.less';
import ThememCard from '../ThemeCard';
import { defaultTheme } from '@/pages/Home/pages/EnterpriseApp/const';
import {
  createApplicationTag,
  listApplicationTag,
  updateApplication,
  type Application,
  type CreateApplicationTagReq,
  type ListTagReq,
  type UpdateApplicationReq
} from '@onebase/app';
import { useEffect, useState } from 'react';
import { useAppStore } from '@/store';
import type { Options } from '@/components/CreateApp/const';

interface IProps {
  editModalVisible: boolean;
  setEditModalVisible: any;
  appData: Application;
}

const EditAppModal = (props: IProps) => {
  const { editModalVisible, setEditModalVisible, appData } = props;
  const { curAppInfo, setCurAppInfo } = useAppStore();
  const [tagList, setTagList] = useState<ListTagReq[]>([]); // 标签列表
  const [themeColor, setThemeColor] = useState<Application['themeColor']>(); // 应用主题色

  const [form] = Form.useForm();

  useEffect(() => {
    if (editModalVisible) {
      listAppTagReq();
      setThemeColor(appData.themeColor);
      form.setFieldsValue({
        ...appData,
        tagIds: appData.tags?.map((v) => {
          return {
            label: v.tagName,
            value: v.id
          } as Options;
        })
      });
    } else {
      form.resetFields();
    }
  }, [editModalVisible]);

  // 查询标签
  const listAppTagReq = async () => {
    const params: ListTagReq = {
      tagName: ''
    };
    const res = await listApplicationTag(params);
    setTagList(res || []);
    return res;
  };

  const optionMap = tagList.reduce((map: Record<string, ListTagReq>, item) => {
    map[item.tagName] = item;
    return map;
  }, {});

  /* 新增标签 */
  const handleCreateTagChange = async (val: Options[]) => {
    const normalized = await Promise.all(
      val.map(async ({ label, value }) => {
        // 判断是否存在于已有 options
        const exist = optionMap[label];

        if (exist) {
          // 使用已有标签的正确 id（value）
          return { label: exist.tagName, value: exist.id };
        } else {
          // 新标签情况
          await createApplicationTag({
            tagName: value
          } as CreateApplicationTagReq);
          const res = await listAppTagReq();
          const getCurTag = res.find((v: ListTagReq) => v.tagName === value); // 接口返回的最新数据

          return { label: getCurTag.tagName, value: getCurTag.id };
        }
      })
    );
    form.setFieldsValue({ tagIds: normalized });
  };

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

  // 设置主题颜色
  const handleThemeColor = (color: string) => {
    setThemeColor(color);
    form.setFieldValue('themeColor', color);
    console.log('color', color);
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
      <Form form={form} labelCol={{ span: 5 }} wrapperCol={{ span: 19 }}>
        <Form.Item
          field="appName"
          label="应用名称"
          rules={[
            { required: true, message: '请填写应用名称' },
            { maxLength: 50, message: '请填写应用名称' }
          ]}
        >
          <Input placeholder="请输入应用名称，例如“工时管理系统”" />
        </Form.Item>
        <Form.Item field="tagIds" label="应用标签" rules={[{ message: '每个应用的标签数量不超过3个', maxLength: 3 }]}>
          <Select
            mode="multiple"
            placeholder="选择或输入标签"
            maxTagCount={{
              count: 3,
              render: (invisibleNumber) => `+${invisibleNumber} more`
            }}
            allowClear
            allowCreate={{
              formatter: (inputValue) => {
                const getTags = form.getFieldValue('tagIds');
                const hasTag = getTags?.some((v: Options) => v.label === inputValue);
                if (hasTag) {
                  return false;
                }
                return { label: inputValue, value: inputValue };
              }
            }}
            labelInValue
            onChange={handleCreateTagChange}
            options={tagList.map((option: ListTagReq) => ({ label: option.tagName, value: option.id }) as Options)}
          />
        </Form.Item>

        <Form.Item field="description" label="应用描述" rules={[{ maxLength: 100, message: '应用描述超出限制' }]}>
          <Input.TextArea placeholder="请填写应用介绍，简要说明核心功能或用途，帮助他人快速了解你的应用" />
        </Form.Item>

        <Form.Item label="主题设置" field="themeColor" style={{ marginBottom: 0 }}>
          <ThememCard themeColor={themeColor || defaultTheme} cardGap={16} onChange={handleThemeColor} />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default EditAppModal;
