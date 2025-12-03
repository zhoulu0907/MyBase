import { Form, Radio, TreeSelect, type FormInstance } from '@arco-design/web-react';
import { listApplicationMenu, type ListApplicationMenuReq, MenuType } from '@onebase/app';
import { getPopupContainer } from '@onebase/ui-kit';
import { useAppStore } from '@/store';
import { useEffect, useState } from 'react';
import webLayout1 from '@/assets/images/appRelease/web_layout1.svg';
import webLayout2 from '@/assets/images/appRelease/web_layout2.svg';
import webLayout3 from '@/assets/images/appRelease/web_layout3.svg';
import mobileLayout1 from '@/assets/images/appRelease/mobile_layout1.svg';
import mobileLayout2 from '@/assets/images/appRelease/mobile_layout2.svg';
import styles from './index.module.less';

interface IProps {
  form: FormInstance;
  data?: any;
}
const NavigatorSetting = (props: IProps) => {
  const { curAppId } = useAppStore();
  const { form, data } = props;
  const webHomeType = Form.useWatch('webHomeType', form);
  const mobileHomeType = Form.useWatch('mobileHomeType', form);

  const [menuTree, setMenuTree] = useState<any[]>([]);

  useEffect(() => {
    getPages();
  }, []);

  useEffect(() => {
    form.setFieldsValue(data);
  }, [data]);

  // 获取下拉页面
  const getPages = async () => {
    const params: ListApplicationMenuReq = {
      applicationId: curAppId
    };
    const res = await listApplicationMenu(params);
    const newMenuData = [...res];
    handlemenuData(newMenuData);
    setMenuTree(newMenuData);
  };
  // 递归 判断是否可以选择
  const handlemenuData = (treeData: any[]) => {
    treeData.forEach((item: any) => {
      if (item.menuType === MenuType.GROUP) {
        item.disabled = true;
        if (item.children?.length) {
          handlemenuData(item.children);
        }
      }
    });
  };

  return (
    <Form form={form} layout="vertical" className={styles.navigatorForm}>
      <div className={styles.navigatorSetting}>
        <div className={styles.moduleTitle}>web端导航设置</div>

        <Form.Item label="web端首页" field="webHomeType">
          <Radio.Group direction="vertical">
            <Radio value="default">
              <span>默认首页</span>
              <span className={styles.radioTip}>自动使用首个非隐藏的菜单页面</span>
            </Radio>
            <Radio value="custom">
              自定义首页
              {webHomeType === 'custom' && (
                <Form.Item field="webHomePageId" className={styles.homePage}>
                  <TreeSelect
                    getPopupContainer={getPopupContainer}
                    treeData={menuTree}
                    placeholder="请选择"
                    fieldNames={{ key: 'id', title: 'menuName' }}
                  ></TreeSelect>
                </Form.Item>
              )}
            </Radio>
          </Radio.Group>
        </Form.Item>

        <Form.Item label="导航布局" field="webLayoutType" initialValue={1}>
          <Radio.Group>
            <Radio value={1}>
              {({ checked }) => (
                <>
                  <div className={styles.radioContainer} style={{ borderColor: checked ? '#009E9E' : 'transparent' }}>
                    <img className={styles.radioLayout} src={webLayout1} alt="" />
                  </div>
                  <div className={styles.radioTips}>侧边导航</div>
                </>
              )}
            </Radio>
            <Radio value={2}>
              {({ checked }) => (
                <>
                  <div className={styles.radioContainer} style={{ borderColor: checked ? '#009E9E' : 'transparent' }}>
                    <img className={styles.radioLayout} src={webLayout2} alt="" />
                  </div>
                  <div className={styles.radioTips}>顶部导航</div>
                </>
              )}
            </Radio>
            <Radio value={3}>
              {({ checked }) => (
                <>
                  <div className={styles.radioContainer} style={{ borderColor: checked ? '#009E9E' : 'transparent' }}>
                    <img className={styles.radioLayout} src={webLayout3} alt="" />
                  </div>
                  <div className={styles.radioTips}>厂字导航</div>
                </>
              )}
            </Radio>
          </Radio.Group>
        </Form.Item>
      </div>
      <div className={styles.navigatorSetting}>
        <div className={styles.moduleTitle}>移动端导航设置</div>

        <Form.Item label="移动端首页" field="mobileHomeType">
          <Radio.Group direction="vertical">
            <Radio value="default">
              <span>默认首页</span>
            </Radio>
            <Radio value="custom">
              自定义首页
              {mobileHomeType === 'custom' && (
                <Form.Item field="mobileHomePageId" className={styles.homePage}>
                  <TreeSelect
                    getPopupContainer={getPopupContainer}
                    treeData={menuTree}
                    placeholder="请选择"
                    fieldNames={{ key: 'id', title: 'menuName' }}
                  ></TreeSelect>
                </Form.Item>
              )}
            </Radio>
          </Radio.Group>
        </Form.Item>

        <Form.Item label="导航布局" field="mobileLayoutType" initialValue={1}>
          <Radio.Group>
            <Radio value={1}>
              {({ checked }) => (
                <>
                  <div
                    className={styles.radioMobileContainer}
                    style={{ borderColor: checked ? '#009E9E' : 'transparent' }}
                  >
                    <img className={styles.radioLayout} src={mobileLayout1} alt="" />
                  </div>
                  <div className={styles.radioTips}>网格式菜单</div>
                </>
              )}
            </Radio>
            <Radio value={2}>
              {({ checked }) => (
                <>
                  <div
                    className={styles.radioMobileContainer}
                    style={{ borderColor: checked ? '#009E9E' : 'transparent' }}
                  >
                    <img className={styles.radioLayout} src={mobileLayout2} alt="" />
                  </div>
                  <div className={styles.radioTips}>列表式菜单</div>
                </>
              )}
            </Radio>
          </Radio.Group>
        </Form.Item>
      </div>
    </Form>
  );
};

export default NavigatorSetting;
