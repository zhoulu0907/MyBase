import { Form, Grid, Input, Popconfirm, Select, type FormInstance } from '@arco-design/web-react';
import { useEffect, useState } from 'react';

import {
  createApplicationTag,
  listApplicationTag,
  type Application,
  type CreateApplicationTagReq,
  type ListTagReq
} from '@onebase/app';
import { sample } from 'lodash-es';

import appIconEditSVG from '@/assets/images/app_edit_white.svg';
import appTypeSVG from '@/assets/images/app_type_selected_icon.svg';
import arrowSVG from '@/assets/images/arrow_icon.svg';
import classicModeSVG from '@/assets/images/classic_mode_icon.svg';
import databaseSVG from '@/assets/images/database_icon.svg';
import formSVG from '@/assets/images/form_icon.svg';
import themeSelectedSVG from '@/assets/images/theme_selected_icon.svg';
import tickSVG from '@/assets/images/tick_icon.svg';
import previewSVG from '@/assets/images/app_preview.svg';
import { appIcon, appIconColor, appThemeColor, type Options } from './const';
import styles from './index.module.less';

type AppStatus = 'create' | 'update';
interface IProps {
  form: FormInstance;
  readonly data?: Application;
  readonly status: AppStatus;
  readonly previewBgColor: string;
  readonly style?: React.CSSProperties;
  readonly dataSourceCreated?: boolean;
  onCreateDatasource?: () => void;
}

// 创建/修改应用
const CreateApp = (props: IProps) => {
  const { previewBgColor, form, data, status, style, dataSourceCreated, onCreateDatasource } = props;

  const [tagList, setTagList] = useState<ListTagReq[]>([]); // 标签列表
  const [iconName, setIconName] = useState<Application['iconName']>();
  const [iconColor, setIconColor] = useState<Application['iconColor']>();
  const [themeColor, setThemeColor] = useState<Application['themeColor']>('#4FAE7B'); // 应用主题色

  useEffect(() => {
    listAppTagReq();
    form.resetFields();
  }, []);

  useEffect(() => {
    if (status === 'create') {
      setIconName(sample(appIcon)!);
      setIconColor(sample(appIconColor)!);
    } else {
      if (data && Object.values(data).length) {
        form.setFieldsValue({
          ...data,
          tagIds: data.tags?.map((v) => {
            return {
              label: v.tagName,
              value: v.id
            } as Options;
          })
        });
        setThemeColor(data.themeColor);
        setIconName(data.iconName);
        setIconColor(data.iconColor);
      }
    }
  }, [data, status]);

  // 查询标签
  const listAppTagReq = async () => {
    const params: ListTagReq = {
      tagName: ''
    };
    const res = await listApplicationTag(params);
    setTagList(res || []);
    return res;
  };

  useEffect(() => {
    form.setFieldsValue({
      ...form.getFieldsValue(),
      iconName,
      iconColor,
      themeColor
    });
  }, [form, iconName, iconColor, themeColor]);

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

  return (
    <div className={styles.createApp} style={style}>
      <div className={styles.preview} style={{ backgroundColor: previewBgColor }}>
        <div className={styles.row}>
          <div className={styles.title}>经典模式</div>
          <span className={styles.desc}>提供应用开发核心功能，支持快速构建数据驱动的业务应用</span>
        </div>
        <div className={styles.row}>
          <div className={styles.subtitle}>模式特点</div>
          <div className={styles.modeSpec}>
            <span>
              <img src={tickSVG} alt="Mode Characteristics" />
              开箱即用的数据资产管理
            </span>
            <span>
              <img src={tickSVG} alt="Mode Characteristics" />
              拖拽搭建业务表单
            </span>
            <span>
              <img src={tickSVG} alt="Mode Characteristics" />
              覆盖表单应用开发基础需求
            </span>
          </div>
        </div>
        <div className={styles.row}>
          <div className={styles.subtitle}>模式配置</div>
          <div className={styles.modeConfigWrapper}>
            <div className={styles.modeConfig}>
              <img src={databaseSVG} alt="Mode Icon" />
              <div className={styles.modeConfigRight}>
                <div className={styles.modeConfigTitle}>元数据管理</div>
                <div className={styles.modeConfigDesc}>创建应用后首先需定义元数据，确保您的应用具有清晰的数据结构</div>
              </div>
            </div>
            <div className={styles.modeConfig}>
              <img src={formSVG} alt="Mode Icon" />
              <div className={styles.modeConfigRight}>
                <div className={styles.modeConfigTitle}>表单搭建</div>
                <div className={styles.modeConfigDesc}>通过直观的拖拽式界面，快速创建复杂的业务表单</div>
              </div>
            </div>
          </div>
        </div>
        <div className={styles.row}>
          <div className={styles.subtitle}>预览图</div>
          <img className={styles.previewImg} src={previewSVG} alt="preview image" />
        </div>
      </div>

      {/* 基础信息 */}
      <div className={styles.info}>
        <div className={styles.title}>
          基础信息<span>请填写应用的基础信息，如名称、描述与图标</span>
        </div>
        <Form form={form} layout="vertical">
          <Form.Item field="iconName" hidden>
            <Input />
          </Form.Item>
          <Form.Item field="iconColor" hidden>
            <Input />
          </Form.Item>
          <Grid.Row justify="space-between">
            <div
              className={styles.appIcon}
              style={{
                background: iconColor
              }}
            >
              {iconName && <i className={`iconfont ${iconName}`} />}
              <Popconfirm
                icon={null}
                title={null}
                position="bl"
                okText="确认"
                onOk={() => {
                  form.setFieldsValue({
                    ...form.getFieldsValue(),
                    iconName,
                    iconColor
                  });
                }}
                onCancel={() => {
                  setIconName('');
                  setIconColor('');
                }}
                content={
                  <>
                    <div className={styles.avatarWrapper}>
                      {appIcon.map((icon, index) => (
                        <div
                          className={styles.avatar}
                          key={index}
                          style={{ backgroundColor: icon === iconName ? iconColor : '#d9d9d9' }}
                          onClick={() => setIconName(icon)}
                        >
                          <i className={`iconfont ${icon}`} />
                        </div>
                      ))}
                    </div>
                    <div className={styles.avatarColor}>
                      {appIconColor.map((color, index) => (
                        <div
                          className={styles.color}
                          key={index}
                          style={{ backgroundColor: color, borderWidth: color === iconColor ? 1 : 0 }}
                          onClick={() => setIconColor(color)}
                        />
                      ))}
                    </div>
                  </>
                }
              >
                <img src={appIconEditSVG} alt="Application icon edit" />
              </Popconfirm>
            </div>

            <Form.Item
              field="appCode"
              label="应用编码"
              rules={[
                { required: true, message: '请填写应用编码' },
                { maxLength: 40, message: '长度超过限制' },
                {
                  match: /^[A-Za-z][A-Za-z0-9_]*$/,
                  message: '应用编码不符合填写要求'
                }
              ]}
              style={{ paddingLeft: 32, flex: 1 }}
            >
              <Input placeholder="字母、数字、下划线组合，字母开头，不超过40字符" />
            </Form.Item>
          </Grid.Row>
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
              placeholder="请选择应用标签"
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
            <Input.TextArea placeholder="请输入应用描述" />
          </Form.Item>

          <div className={styles.formItem}>
            <div className={styles.subtitle}>
              <div className={styles.left}>
                <div>应用模式</div>
                <span>请根据业务需求选择应用的导航模块</span>
              </div>
              <div className={styles.dataImportant} onClick={onCreateDatasource}>
                <img src={arrowSVG} alt="Use own data source" />
                {dataSourceCreated ? '已配置自有数据源' : '使用自有数据源'}
              </div>
            </div>
            <div className={styles.appType}>
              <img src={classicModeSVG} alt="Classic Mode" />
              <div className={styles.appTypeInfo}>
                <div className={styles.appTypeName}>经典模式</div>
                <div className={styles.appTypeDesc}>提供应用开发核心功能，支持快速构建数据驱动的业务应用</div>
              </div>
              <img src={appTypeSVG} alt="Select Classic Mode" />
            </div>
          </div>

          <Form.Item field="themeColor" rules={[{ required: false }]}>
            <div className={styles.formItem}>
              <div className={styles.subtitle}>
                <div className={styles.left}>
                  <div>主题设置</div>
                  <span>请选择适合您应用的主题色方案，打造独特的视觉风格</span>
                </div>
              </div>
              <div className={styles.appTheme}>
                {appThemeColor.map((color) => (
                  <div
                    className={styles.theme}
                    key={color}
                    style={{
                      backgroundColor: color
                    }}
                    onClick={() => setThemeColor(color)}
                  >
                    {themeColor === color && <img src={themeSelectedSVG} alt="Select Theme Color" />}
                  </div>
                ))}
              </div>
            </div>
          </Form.Item>
        </Form>
      </div>
    </div>
  );
};

export default CreateApp;
