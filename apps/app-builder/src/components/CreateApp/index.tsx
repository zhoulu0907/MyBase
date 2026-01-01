import { useEffect, useState } from 'react';
import { Form, Radio, type FormInstance, Divider, Avatar, Space, Card } from '@arco-design/web-react';
import Title from '@arco-design/web-react/es/Typography/title';
import { type Application } from '@onebase/app';
import dataSourceSVG from '@/assets/images/appBasic/data_source.svg';
import classicModeSVG from '@/assets/images/appBasic/classic_mode.svg';
import classicCheckSVG from '@/assets/images/appBasic/classic_check_icon.svg';
import checkSVG from '@/assets/images/appBasic/mode_check_icon.svg';
import dataSVG from '@/assets/images/appBasic/app_data_mode.svg';
import pageDesignSVG from '@/assets/images/appBasic/page_design.svg';
import previewSVG from '@/assets/images/appBasic/app_review.svg';
import { hasPermission, PUBLISH_MODULE, TENANT_MENUS } from '@onebase/common';
import AppForm from './components/AppForm';
import ThememCard from '../ThemeCard';
import { defaultTheme } from '@/pages/Home/pages/EnterpriseApp/const';
import styles from './index.module.less';

interface IProps {
  form: FormInstance;
  readonly data?: Application;
  readonly style?: React.CSSProperties;
  readonly dataSourceCreated?: boolean;
  readonly isCreateVisible: boolean;
  onCreateDatasource?: () => void;
}

// 创建/修改应用
const CreateApp = (props: IProps) => {
  const { form, style, dataSourceCreated, isCreateVisible, onCreateDatasource } = props;
  const isOpenedSaaS = hasPermission(TENANT_MENUS.CORP);
  const [themeColor, setThemeColor] = useState<Application['themeColor']>('#009E9E'); // 应用主题色

  useEffect(() => {
    form.setFieldsValue({
      ...form.getFieldsValue(),
      themeColor
    });
  }, [themeColor]);

  const datasourceStatus = () => {
    return dataSourceCreated ? '已配置自有数据源' : '使用自有数据源';
  };

  // 设置主题颜色
  const handleThemeColor = (color: string) => {
    setThemeColor(color);
    console.log('color', color);
  };

  return (
    <div className={styles.createApp} style={style}>
      {/* 基础信息 */}
      <div className={styles.info}>
        <div className={styles.title}>
          <div className={styles.leftShape} />
          <Title heading={6} style={{ margin: 0 }}>
            基础信息
          </Title>
          <Divider type="vertical" className={styles.divider} />
          <span>请填写应用的基础信息，如名称、描述与图标</span>
        </div>
        <Form
          form={form}
          labelCol={{ span: 5 }}
          wrapperCol={{ span: 19 }}
          initialValues={{
            publishModel: PUBLISH_MODULE.INNER
          }}
        >
          {/* 基础信息 */}
          <AppForm form={form} isCreateVisible={isCreateVisible} />
          <div className={styles.title} style={{ marginTop: '20px' }}>
            <div className={styles.leftShape} />
            <Title heading={6} style={{ margin: 0 }}>
              主题设置
            </Title>
            <Divider type="vertical" className={styles.divider} />
            <span>请选择适合您应用的主题色方案，打造独特的视觉风格</span>
          </div>
          <Form.Item field="themeColor" rules={[{ required: false }]}>
            <ThememCard themeColor={themeColor || defaultTheme} cardGap={16} onChange={handleThemeColor} />
          </Form.Item>

          {/* 发布模式 */}
          {isOpenedSaaS && (
            <>
              <div className={styles.title} style={{ marginTop: '20px' }}>
                <div className={styles.leftShape} />
                <Title heading={6} style={{ margin: 0 }}>
                  发布模式
                </Title>
              </div>
              <Form.Item field="publishModel" noStyle rules={[{ required: true, message: '请选择发布模式' }]}>
                <Radio.Group>
                  <Radio value={PUBLISH_MODULE.INNER}>内部模式</Radio>
                  <Radio value={PUBLISH_MODULE.SASS}>SaaS模式</Radio>
                </Radio.Group>
              </Form.Item>
            </>
          )}
        </Form>
      </div>

      {/* 应用模式 */}
      <div className={styles.preview}>
        <div className={styles.appMode}>
          <div className={styles.title}>
            <div className={styles.leftShape} />
            <Title heading={6} style={{ margin: 0 }}>
              应用模式
            </Title>
            <Divider type="vertical" className={styles.divider} />
            <span>请根据业务需求选择应用的导航模块</span>
          </div>
          <div className={styles.dataImportant} onClick={onCreateDatasource}>
            <img src={dataSourceSVG} alt="Use own data source" />
            {datasourceStatus()}
          </div>
        </div>

        <div className={styles.classicMode}>
          <Avatar size={32} className={styles.classicIcon}>
            <img src={classicModeSVG} />
          </Avatar>
          <div className={styles.classicInfo}>
            <div className={styles.classicTitle}>
              经典模式
              <img src={classicCheckSVG} />
            </div>
            <span className={styles.classicDesc}>提供应用开发核心功能，支持快速构建数据驱动的业务应用</span>
          </div>
        </div>

        <div className={styles.modeDetail}>
          <div className={styles.mode} style={{ alignItems: 'center' }}>
            <span className={styles.modeTitle}>模式特点</span>
            <Space>
              <span className={styles.modeSpec}>
                <img src={checkSVG} />
                数据建模先行
              </span>
              <span className={styles.modeSpec}>
                <img src={checkSVG} />
                元数据驱动表单生成
              </span>
            </Space>
          </div>

          <div className={styles.mode}>
            <span className={styles.modeTitle}>模式配置</span>
            <Space>
              <Card bordered size="small" className={styles.modeConfig}>
                <span className={styles.modeSpec} style={{ lineHeight: '20px' }}>
                  <img src={dataSVG} />
                  数据建模
                </span>
                <div className={styles.modeDesc}>完善的数据资产管理</div>
              </Card>

              <Card bordered size="small" className={styles.modeConfig}>
                <span className={styles.modeSpec} style={{ lineHeight: '20px' }}>
                  <img src={pageDesignSVG} />
                  页面设计
                </span>
                <div className={styles.modeDesc}>可视化界面创建表单</div>
              </Card>
            </Space>
          </div>

          <div className={styles.mode}>
            <span className={styles.modeTitle}>预览图</span>
            <Space>
              <img src={previewSVG} className={styles.previewImage} />
            </Space>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CreateApp;
