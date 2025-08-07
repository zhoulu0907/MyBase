import CreateGroupIcon from '@/assets/images/create_group.svg';
import CreatePageIcon from '@/assets/images/create_page.svg';
import { useAppStore } from '@/store';
import {
  Button,
  Dropdown,
  Form,
  Input,
  Layout,
  Menu,
  Message,
  Modal,
  Select,
  TreeSelect
} from '@arco-design/web-react';
import { IconPlus, IconSearch, IconSettings } from '@arco-design/web-react/icon';
import {
  copyApplicationMenu,
  createApplicationMenu,
  deleteApplicationMenu,
  listApplicationMenu,
  MenuType,
  PageType,
  RootParentPage,
  updateApplicationMenuName,
  type ApplicationMenu,
  type CopyApplicationMenuReq,
  type CreateApplicationMenuReq,
  type DeleteApplicationMenuReq,
  type ListApplicationMenuReq,
  type UpdateApplicationMenuNameReq
} from '@onebase/app';
import { useEffect, useState, type FC } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { EDITOR_TYPES } from '../Editor/components/const';
import styles from './index.module.less';

const MenuItem = Menu.Item;
const SubMenu = Menu.SubMenu;
const Sider = Layout.Sider;
const Content = Layout.Content;

const iconStyle = {
  marginRight: 8,
  transform: 'translateY(5px)'
};

/**
 * 树形数据节点接口
 */
interface TreeNode {
  key: string;
  value: string;
  title: string;
  children?: TreeNode[];
}

/**
 * 递归转换菜单数据为树形结构
 * @param menuItems 菜单项数组
 * @returns 转换后的树形数据
 */
const convertMenuToTreeData = (menuItems: ApplicationMenu[]): TreeNode[] => {
  return menuItems.map((item) => ({
    key: String(item.id),
    value: String(item.id),
    title: item.menuName,
    children: item.children ? convertMenuToTreeData(item.children) : []
  }));
};

const PageManagerPage: FC = () => {
  const { t } = useTranslation();
  const navigate = useNavigate();

  const { curAppId } = useAppStore();

  const [form] = Form.useForm();
  const [renameForm] = Form.useForm();
  const [copyForm] = Form.useForm();

  const [title, setTitle] = useState('');
  const pageTypeOptions = [{ label: '普通表单', value: PageType.NORMAL }];

  const [menuList, setMenuList] = useState<ApplicationMenu[]>([]);

  // 创建弹窗
  const [visibleCreateForm, setVisibleCreateForm] = useState('');

  const [curEditMenuID, setCurEditMenuID] = useState<string>();
  const [curEditMenuName, setCurEditMenuName] = useState<string>();
  const [activeMenu, setActiveMenu] = useState<ApplicationMenu>();
  const [parentPageOptions, setParentPageOptions] = useState<ApplicationMenu[]>([RootParentPage]);

  // 重命名弹窗
  const [visibleRenameForm, setVisibleRenameForm] = useState(false);

  // 复制弹窗
  const [visibleCopyForm, setVisibleCopyForm] = useState(false);

  useEffect(() => {
    if (curAppId !== '') {
      getMenuList();
    }
  }, [curAppId]);

  /**
   * 递归为菜单项补充parentId字段
   * @param menuItems 菜单项数组
   * @param parentId 父级ID
   * @returns 处理后的菜单项数组
   */
  const addParentIdToChildren = (menuItems: ApplicationMenu[], parentId?: string): ApplicationMenu[] => {
    return menuItems.map((menu) => ({
      ...menu,
      parentId: parentId,
      children: menu.children ? addParentIdToChildren(menu.children, menu.id) : []
    }));
  };

  const getMenuList = async () => {
    const req: ListApplicationMenuReq = {
      applicationId: curAppId
    };
    const res = await listApplicationMenu(req);
    console.log('res: ', res);

    // 为每个children元素补充parentId字段
    const processedRes = addParentIdToChildren(res, RootParentPage.id);

    setMenuList(processedRes);

    setParentPageOptions([{ ...RootParentPage, children: processedRes }]);
  };

  /**
   * 递归渲染菜单项
   * @param menuList 菜单列表
   * @param onMenuClick 菜单点击回调
   * @returns 渲染的菜单项数组
   */
  const renderMenuItems = (mList: ApplicationMenu[], onMenuClick: (item: ApplicationMenu) => void) => {
    return mList.map((menu: ApplicationMenu) => {
      if (menu.children && menu.children.length > 0) {
        return (
          <SubMenu
            key={menu.id}
            onClick={(e) => {
              e.stopPropagation();
              onMenuClick(menu);
            }}
            style={{
              width: '200px'
            }}
            title={
              <div className={styles.menuItem}>
                <div className={styles.subMenuItemName}>{menu.menuName}</div>
                <div className={styles.dropdownContainer}>
                  <Dropdown droplist={settingMenuDropList} trigger="click" position="bl">
                    <IconSettings
                      onClick={(e) => {
                        e.stopPropagation();
                        setCurEditMenuID(menu.id);
                        setCurEditMenuName(menu.menuName);
                      }}
                    />
                  </Dropdown>
                </div>
              </div>
            }
          >
            {renderMenuItems(menu.children, onMenuClick)}
          </SubMenu>
        );
      }
      return (
        <MenuItem
          key={menu.id}
          onClick={(e) => {
            e.stopPropagation();
            onMenuClick(menu);
          }}
          style={{
            width: '200px'
          }}
        >
          <div className={styles.menuItem}>
            <div className={styles.menuItemName}>{menu.menuName}</div>
            <div className={styles.dropdownContainer}>
              <Dropdown droplist={settingMenuDropList} trigger="click" position="bl">
                <IconSettings
                  onClick={(e) => {
                    e.stopPropagation();
                    setCurEditMenuID(menu.id);
                    setCurEditMenuName(menu.menuName);
                  }}
                />
              </Dropdown>
            </div>
          </div>
        </MenuItem>
      );
    });
  };

  const createMenuDropList = (
    <Menu style={{ padding: '10px 5px' }}>
      <MenuItem
        key="page"
        onClick={() => {
          setVisibleCreateForm('page');
          form.resetFields();
          setTitle(t('createApp.createPage'));
        }}
      >
        <img src={CreatePageIcon} style={iconStyle} />
        {t('createApp.createPage')}
      </MenuItem>
      <MenuItem
        key="group"
        onClick={() => {
          setVisibleCreateForm('group');
          form.resetFields();
          setTitle(t('createApp.createGroup'));
        }}
      >
        <img src={CreateGroupIcon} style={iconStyle} />
        {t('createApp.createGroup')}
      </MenuItem>
    </Menu>
  );

  const settingMenuDropList = (
    <Menu style={{ padding: '10px 5px' }}>
      <MenuItem
        key="rename"
        onClick={(e) => {
          e.stopPropagation();
          setVisibleRenameForm(true);
          renameForm.resetFields();
          renameForm.setFieldValue('menuName', curEditMenuName);
        }}
      >
        {'重命名'}
      </MenuItem>
      <MenuItem
        key="copy"
        onClick={(e) => {
          e.stopPropagation();
          setVisibleCopyForm(true);
          copyForm.resetFields();
          setTitle(t('createApp.copyPage'));
          console.log(activeMenu?.parentId || RootParentPage.id);
        }}
      >
        {'复制'}
      </MenuItem>
      <MenuItem
        key="hide"
        onClick={(e) => {
          e.stopPropagation();
        }}
      >
        {'隐藏'}
      </MenuItem>
      <MenuItem
        key="delete"
        onClick={(e) => {
          e.stopPropagation();
          handleDelete(activeMenu?.id);
        }}
        style={{ color: 'red' }}
      >
        {'删除'}
      </MenuItem>
    </Menu>
  );

  const handleCreate = async () => {
    let req: CreateApplicationMenuReq = {
      applicationId: curAppId,
      parentId: form.getFieldValue('parentId'),
      menuName: form.getFieldValue('menuName'),
      menuType: MenuType.PAGE,
      menuIcon: 'tmp'
    };

    if (visibleCreateForm === 'page') {
      req.menuType = MenuType.PAGE;
    }
    if (visibleCreateForm === 'group') {
      req.menuType = MenuType.GROUP;
    }

    const res = await createApplicationMenu(req);
    console.log('res: ', res);

    setVisibleCreateForm('');
    getMenuList();
  };

  const handleRename = async () => {
    if (!curEditMenuID) {
      Message.error('请选择要重命名的菜单');
      return;
    }
    console.log('curEditMenuID: ', curEditMenuID);
    const req: UpdateApplicationMenuNameReq = {
      id: curEditMenuID,
      menuName: renameForm.getFieldValue('menuName')
    };
    const res = await updateApplicationMenuName(req);
    console.log('res: ', res);
    setVisibleRenameForm(false);
    getMenuList();
  };

  const handleCopy = async () => {
    if (!activeMenu?.id) {
      Message.error('请选择要复制的菜单');
      return;
    }
    const req: CopyApplicationMenuReq = {
      id: activeMenu?.id,
      menuName: copyForm.getFieldValue('menuName'),
      parentUuid: copyForm.getFieldValue('parentId')
    };
    const res = await copyApplicationMenu(req);
    console.log('res: ', res);
    setVisibleCopyForm(false);
    getMenuList();
  };

  const handleDelete = async (id: string | undefined) => {
    if (!id) {
      Message.error('请选择要删除的菜单');
      return;
    }
    const req: DeleteApplicationMenuReq = {
      id: id
    };
    const res = await deleteApplicationMenu(req);
    console.log('res: ', res);
    getMenuList();
  };

  return (
    <div className={styles.pageManagerPage}>
      <Layout style={{ height: '100%' }}>
        <Layout>
          <Sider style={{ width: 225 }}>
            <div className={styles.header}>
              <Input
                style={{
                  width: 140,
                  border: '1px solid #dedede',
                  borderRadius: 3
                }}
                allowClear
                suffix={<IconSearch />}
                placeholder={t('common.search')}
              />
              <Dropdown droplist={createMenuDropList} trigger="click" position="bl">
                <Button type="primary" icon={<IconPlus />} />
              </Dropdown>
            </div>

            <Menu mode="vertical" levelIndent={0}>
              {renderMenuItems(menuList, (menu) => {
                setActiveMenu(menu);
                console.log('menu: ', menu.menuName);
              })}
            </Menu>
          </Sider>
          <Content className={styles.content}>
            <div className={styles.contentHeader}>
              <div className={styles.contentTitle}>{activeMenu?.menuName}</div>
              <Button type="primary" onClick={() => navigate(`/onebase/editor/${EDITOR_TYPES.FORM_EDITOR}`)}>
                编辑
              </Button>
            </div>
            <Content className={styles.content}>content</Content>
          </Content>
        </Layout>
      </Layout>

      {/* 重命名弹窗 */}
      <Modal
        title={title}
        visible={visibleRenameForm}
        onOk={handleRename}
        onCancel={() => {
          setVisibleRenameForm(false);
        }}
        autoFocus={false}
        focusLock={true}
      >
        <Form
          layout="vertical"
          form={renameForm}
          initialValues={{
            menuName: renameForm.getFieldValue('menuName')
          }}
        >
          <Form.Item label="页面名称" field="menuName" rules={[{ required: true, message: '请输入页面名称' }]}>
            <Input placeholder="请输入页面名称" allowClear />
          </Form.Item>
        </Form>
      </Modal>

      {/* 复制弹窗 */}
      <Modal
        title={title}
        visible={visibleCopyForm}
        onOk={handleCopy}
        onCancel={() => {
          setVisibleCopyForm(false);
        }}
        autoFocus={false}
        focusLock={true}
      >
        <Form
          layout="vertical"
          form={renameForm}
          initialValues={{
            menuName: activeMenu?.menuName + '_副本',
            parentId: activeMenu?.parentId || RootParentPage.id
          }}
        >
          <Form.Item label="页面名称" field="menuName" rules={[{ required: true, message: '请输入页面名称' }]}>
            <Input placeholder="请输入页面名称" allowClear />
          </Form.Item>

          <Form.Item label="父级页面" field="parentId">
            <TreeSelect treeData={convertMenuToTreeData(parentPageOptions)} placeholder="请选择父级页面" allowClear />
          </Form.Item>
        </Form>
      </Modal>

      {/* 创建弹窗 */}
      <Modal
        title={title}
        visible={visibleCreateForm !== ''}
        onOk={handleCreate}
        onCancel={() => {
          setVisibleCreateForm('');
        }}
        autoFocus={false}
        focusLock={true}
      >
        <Form
          layout="vertical"
          form={form}
          initialValues={{
            pageType: PageType.NORMAL,
            menuName: ''
          }}
        >
          <Form.Item
            label="页面类型"
            field="pageType"
            hidden={visibleCreateForm === 'group'}
            rules={[{ required: true, message: '请选择页面类型' }]}
            initialValue={PageType.NORMAL}
          >
            <Select options={pageTypeOptions} placeholder="请选择页面类型" allowClear />
          </Form.Item>

          <Form.Item
            label={visibleCreateForm === 'page' ? '页面名称' : '分组名称'}
            field="menuName"
            rules={[
              { required: true, message: '请输入页面名称' },
              { maxLength: 20, message: '页面名称不能超过20个字符' }
            ]}
          >
            <Input
              maxLength={20}
              placeholder="请输入页面名称，不超过20个字符"
              allowClear
              onChange={(value) => {
                form.setFieldValue('menuName', value);
              }}
            />
          </Form.Item>

          {/* TODO: 添加菜单图标 */}

          <Form.Item label="父级页面" field="parentId" initialValue={RootParentPage.id}>
            <TreeSelect treeData={convertMenuToTreeData(parentPageOptions)} placeholder="请选择父级页面" allowClear />
          </Form.Item>

          {/* TODO: 添加业务实体 */}
        </Form>
      </Modal>
    </div>
  );
};

export default PageManagerPage;
