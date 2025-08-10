import {
  Button,
  Divider,
  Form,
  Grid,
  Input,
  Message,
  Popconfirm,
  Select,
  type FormInstance
} from '@arco-design/web-react';
import { useEffect, useState } from 'react';

import { createApplicationTag, listApplicationTag, type CreateApplicationTagReq, type ListTagReq } from '@onebase/app';
import { sample } from 'lodash-es';

import appIconEditSVG from '@/assets/images/app_icon_edit.svg';
import appTypeSVG from '@/assets/images/app_type_selected_icon.svg';
import arrowSVG from '@/assets/images/arrow_icon.svg';
import classicModeSVG from '@/assets/images/classic_mode_icon.svg';
import databaseSVG from '@/assets/images/database_icon.svg';
import formSVG from '@/assets/images/form_icon.svg';
import themeSelectedSVG from '@/assets/images/theme_selected_icon.svg';
import tickSVG from '@/assets/images/tick_icon.svg';
import { IconPlus } from '@arco-design/web-react/icon';
import { appIcon, appIconColor, appThemeColor } from './const';
import styles from './index.module.less';

const Option = Select.Option;

interface IProps {
  form: FormInstance;
  readonly previewBgColor: string;
}

// 创建/修改应用
const BasicSetting = (props: IProps) => {
  const { previewBgColor, form } = props;

  const [tagValue, setTagValue] = useState<string>(''); // 新增标签值
  const [tagList, setTagList] = useState<ListTagReq[]>([]); // 标签列表

  const [iconName, setIconName] = useState<string>(sample(appIcon)!);
  const [iconColor, setIconColor] = useState<string>(sample(appIconColor)!);

  const [themeColor, setThemeColor] = useState<string>('#4FAE7B'); // 应用主题色

  useEffect(() => {
    listAppTagReq();
  }, []);

  const listAppTagReq = async () => {
    const params: ListTagReq = {
      tagName: ''
    };
    // 查询标签
    listApplicationTag(params).then((data: any[]) => {
      setTagList(data || []);
    });
  };

  useEffect(() => {
    form.setFieldsValue({
      ...form.getFieldsValue(),
      iconName,
      iconColor,
      themeColor
    });
  }, [form, iconName, iconColor, themeColor]);

  /* 新增标签 */
  const handleAddTag = async () => {
    if (tagValue === '') {
      Message.warning('请输入标签内容');
      return;
    }
    if (tagList.findIndex((t) => t.tagName === tagValue) !== -1) {
      Message.warning('标签已存在');
      return;
    }
    setTagValue('');
    await createApplicationTag({
      tagName: tagValue
    } as CreateApplicationTagReq);
    await listAppTagReq();
  };

  return (
    <div className={styles.basicSetting}>
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
                <div className={styles.modeConfigTitle}>元数据管理</div>
                <div className={styles.modeConfigDesc}>创建应用后首先需定义元数据，确保您的应用具有清晰的数据结构</div>
              </div>
            </div>
          </div>
        </div>
        <div className={styles.row}>
          <div className={styles.subtitle}>预览图</div>
          <div className={styles.previewImg}></div>
        </div>
        <div className={styles.row}></div>
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
              rules={[{ required: true, message: '请填写应用编码', maxLength: 20 }]}
              style={{ paddingLeft: 32, flex: 1 }}
            >
              <Input placeholder="字母、数字、下划线组合，字母开头，不超过20字符" />
            </Form.Item>
          </Grid.Row>
          <Form.Item
            field="appName"
            label="应用名称"
            rules={[{ required: true, message: '请填写应用名称', maxLength: 50 }]}
          >
            <Input placeholder="请输入应用名称，例如“工时管理系统”" />
          </Form.Item>
          <Form.Item field="tagIds" label="应用标签" rules={[{ required: false, maxLength: 3 }]}>
            <Select
              mode="multiple"
              placeholder="请选择应用标签"
              maxTagCount={{
                count: 3,
                render: (invisibleNumber) => `+${invisibleNumber} more`
              }}
              allowClear
              dropdownRender={(menu) => (
                <div>
                  {menu}
                  <Divider style={{ margin: 0 }} />
                  <div
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      padding: '10px 12px'
                    }}
                  >
                    <Input
                      size="small"
                      style={{ marginRight: 18 }}
                      value={tagValue}
                      maxLength={20}
                      onChange={(value) => setTagValue(value)}
                    />
                    <Button style={{ fontSize: 14, padding: '0 6px' }} type="text" size="mini" onClick={handleAddTag}>
                      <IconPlus />
                      新增标签
                    </Button>
                  </div>
                </div>
              )}
              dropdownMenuStyle={{ maxHeight: 300 }}
            >
              {tagList.map((tag) => (
                <Option key={tag.id} value={tag.id!}>
                  {tag.tagName}
                </Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item field="description" label="应用描述" rules={[{ required: false, maxLength: 100 }]}>
            <Input.TextArea placeholder="请输入应用描述" />
          </Form.Item>

          <div className={styles.formItem}>
            <div className={styles.subtitle}>
              <div className={styles.left}>
                <div>应用模式</div>
                <span>请根据业务需求选择应用的导航模块</span>
              </div>
              <div className={styles.dataImportant}>
                <img src={arrowSVG} alt="Use own data source" />
                使用自有数据源
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

export default BasicSetting;
