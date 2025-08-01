import { type FC, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
    Layout,
    Input,
    Dropdown,
    Button,
    Menu,
    Modal,
} from "@arco-design/web-react";
import { IconPlus } from "@arco-design/web-react/icon";
import {
    IconFile,
    IconFolder,
    IconHome,
    IconPhone,
    IconStorage,
    IconSearch,
} from "@arco-design/web-react/icon";
import { useTranslation } from "react-i18next";
import styles from "./index.module.less";

const MenuItem = Menu.Item;
const Sider = Layout.Sider;
const Content = Layout.Content;

const iconStyle = {
    marginRight: 8,
    fontSize: 16,
    transform: "translateY(1px)",
};

const menuData = [
    {
        key: "0",
        icon: <IconHome />,
        title: "首页",
    },
    {
        key: "1",
        icon: <IconPhone />,
        title: "联系我们",
    },
    {
        key: "2",
        icon: <IconStorage />,
        title: "基础档案",
    },
];

const PageManagerPage: FC = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();


    const [title, setTitle] = useState("");
    const [value, setValue] = useState("");
    const [menuList, setMenuList] = useState<any>(menuData);
    const [visible, setVisible] = useState(false);
    const [activeMenu, setActiveMenu] = useState(menuData[0]);

    const dropList = (
        <Menu style={{ padding: "10px 0" }}>
            <label style={{ marginLeft: 12, color: "#999", fontSize: 16 }}>
                {t("createApp.commom")}
            </label>
            <MenuItem
                key="1"
                onClick={() => {
                    setVisible(true);
                    setTitle(t("createApp.createForm"));
                }}
            >
                <IconFile style={iconStyle} />
                {t("createApp.createForm")}
            </MenuItem>
            <MenuItem
                key="2"
                onClick={() => {
                    setVisible(true);
                    setTitle(t("createApp.createGroup"));
                }}
            >
                <IconFolder style={iconStyle} />
                {t("createApp.createGroup")}
            </MenuItem>
        </Menu>
    );

    const handleCreate = () => {
        setVisible(false);
        const newItem = {
            key: Date.now() + "",
            icon: <IconStorage />,
            title: value,
        };
        const update = menuList.concat(newItem);
        setMenuList(update);
        setValue("");
    };

    return (
        <div className={styles.pageManagerPage}>
            <Layout style={{ height: "100%" }}>
                <Layout>
                    <Sider style={{ width: 225 }}>
                        <div className={styles.header}>
                            <Input
                                style={{
                                    width: 140,
                                    border: "1px solid #dedede",
                                    borderRadius: 3,
                                }}
                                allowClear
                                suffix={<IconSearch />}
                                placeholder={t("common.search")}
                            />
                            <Dropdown
                                droplist={dropList}
                                trigger="click"
                                position="bl"
                            >
                                <Button type="primary" icon={<IconPlus />} />
                            </Dropdown>
                        </div>

                        <Menu style={{ width: 226, padding: 12 }} defaultSelectedKeys={['0']}>
                            {menuList.map((menu: any) => (
                                <MenuItem
                                    key={menu.key}
                                    onClick={() => setActiveMenu(menu)}
                                >
                                    {menu.icon}
                                    {menu.title}
                                </MenuItem>
                            ))}
                        </Menu>
                    </Sider>
                    <Content className={styles.content}>
                        <div className={styles.contentHeader}>
                            <div className={styles.contentTitle}>
                                {activeMenu.title}
                            </div>
                            <Button type="primary" onClick={() => navigate('/onebase/editor/form_editor')}>编辑</Button>
                        </div>
                        <Content className={styles.content}>content</Content>
                    </Content>
                </Layout>
            </Layout>
            <Modal
                title={title}
                visible={visible}
                onOk={handleCreate}
                onCancel={() => setVisible(false)}
                autoFocus={false}
                focusLock={true}
            >
                <Input
                    value={value}
                    onChange={setValue}
                    placeholder="请输入名称"
                />
            </Modal>
        </div>
    );
};

export default PageManagerPage;
