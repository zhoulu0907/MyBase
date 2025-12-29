import { Form, Grid, Input, Select, type FormInstance } from '@arco-design/web-react';
import {
  createApplicationTag,
  listApplicationTag,
  type Application,
  type CreateApplicationTagReq,
  type ListTagReq
} from '@onebase/app';
import type { Options } from '../../const';
import ThememCard from '@/components/ThemeCard';
import { useEffect, useState } from 'react';
import { defaultTheme } from '@/pages/Home/pages/EnterpriseApp/const';
import AppAvatarIcon from '@/components/appAvatarIcon';
import Col from '@arco-design/web-react/es/Grid/col';

interface IProps {
  form: FormInstance;
  appData?: Application;
  isEditModalVisible?: boolean;
  isCreateVisible?: boolean;
}

const AppForm = (props: IProps) => {
  const { form, appData, isEditModalVisible, isCreateVisible } = props;
  const [tagList, setTagList] = useState<ListTagReq[]>([]); // 标签列表
  const [themeColor, setThemeColor] = useState<Application['themeColor']>(); // 应用主题色

  useEffect(() => {
    const visible = isEditModalVisible || isCreateVisible;

    if (!visible) {
      form.resetFields();
      return;
    }

    // 打开任意一个弹窗都要拉标签
    listAppTagReq();

    // 只有编辑态才需要回填数据
    if (isEditModalVisible && appData) {
      setThemeColor(appData.themeColor);
      form.setFieldsValue({
        ...appData,
        tagIds:
          appData.tags?.map((v) => ({
            label: v.tagName,
            value: v.id
          })) ?? []
      });
    }
  }, [isEditModalVisible, isCreateVisible]);

  const optionMap = tagList.reduce((map: Record<string, ListTagReq>, item) => {
    map[item.tagName] = item;
    return map;
  }, {});

  // 查询标签
  const listAppTagReq = async () => {
    const params: ListTagReq = {
      tagName: ''
    };
    const res = await listApplicationTag(params);
    setTagList(res || []);
    return res;
  };

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

  // 设置主题颜色
  const handleThemeColor = (color: string) => {
    setThemeColor(color);
    form.setFieldValue('themeColor', color);
    console.log('color', color);
  };
  return (
    <>
      {isCreateVisible && (
        <Grid.Row>
          <Col span={5} style={{ paddingLeft: '8px' }}>
            <AppAvatarIcon avatarSize={60} iconSize={40} isCreateApp={isCreateVisible} form={form} />
          </Col>

          <Col span={19}>
            <Form.Item
              field="appCode"
              label="应用编码"
              layout="vertical"
              rules={[
                { required: true, message: '请填写应用编码' },
                { maxLength: 40, message: '长度超过限制' },
                {
                  match: /^[A-Za-z][A-Za-z0-9_]*$/,
                  message: '应用编码不符合填写要求'
                }
              ]}
            >
              <Input placeholder="字母、数字、下划线组合，字母开头，不超过40字符" />
            </Form.Item>
          </Col>
        </Grid.Row>
      )}
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

      {isEditModalVisible && (
        <Form.Item label="主题设置" field="themeColor" style={{ marginBottom: 0 }}>
          <ThememCard themeColor={themeColor || defaultTheme} cardGap={16} onChange={handleThemeColor} />
        </Form.Item>
      )}
    </>
  );
};

export default AppForm;
