import AppIcon from "@/assets/images/app_icon.svg";
import activeFormDesignSVG from "@/assets/images/form_design_active_icon.svg";
import defaultFormDesignSVG from "@/assets/images/form_design_default_icon.svg";
import activeListDesignSVG from "@/assets/images/list_design_active_icon.svg";
import defaultListDesignSVG from "@/assets/images/list_design_default_icon.svg";
import activeSourceDataSVG from "@/assets/images/source_data_active_icon.svg";
import defaultSourceDataSVG from "@/assets/images/source_data_default_icon.svg";
import { Button, Tabs } from "@arco-design/web-react";
import { IconArrowLeft } from "@arco-design/web-react/icon";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./index.module.less";

const tabData = [
    {
        key: "form-design",
        title: "表单设计",
        alt: "Form Design",
        defaultIcon: defaultFormDesignSVG,
        activeIcon: activeFormDesignSVG,
    },
    {
        key: "list-design",
        title: "列表设计",
        alt: "List Design",
        defaultIcon: defaultListDesignSVG,
        activeIcon: activeListDesignSVG,
    },
    {
        key: "page-setting",
        title: "页面设置",
        alt: "",
    },
    {
        key: "metadata-manage",
        title: "元数据管理",
        alt: "Source Data",
        defaultIcon: defaultSourceDataSVG,
        activeIcon: activeSourceDataSVG,
    },
];

export default function EditorHeader() {
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState("form-design");

    return (
        <div className={styles.editorHeader}>
            {/* 左侧 */}
            <div className={styles.left}>
                <Button
                    shape="circle"
                    type="default"
                    icon={<IconArrowLeft />}
                />

                <img src={AppIcon} style={{ width: 28, height: 28 }} />

                <span>新应用 </span>
                <span>&gt;</span>
                <span>页面一</span>
            </div>

            {/* 中间 */}
            <div className={styles.center}>
                <Tabs
                    type="line"
                    activeTab={activeTab}
                    onChange={(key) => {
                        setActiveTab(key);
                        switch (key) {
                            case "form-design":
                                navigate("/onebase/editor/form_editor");
                                break;
                            case "list-design":
                                navigate("/onebase/editor/list_editor");
                                break;
                            case "page-setting":
                                navigate("/onebase/editor/page-setting");
                                break;
                            case "metadata-manage":
                                navigate("/onebase/editor/metadata-manage");
                                break;
                            default:
                                break;
                        }
                    }}
                    size="large"
                    inkBarSize={{ width: 106, height: 3 }}
                >
                    {tabData.map((tab) => (
                        <Tabs.TabPane
                            key={tab.key}
                            title={
                                <div className={styles.tabIcon}>
                                    <img
                                        src={
                                            tab.key === activeTab
                                                ? tab.activeIcon
                                                : tab.defaultIcon
                                        }
                                        alt={tab.alt}
                                    />
                                    {tab.title}
                                </div>
                            }
                        />
                    ))}
                </Tabs>
            </div>

            <div className={styles.right}>
                <div className={styles.editorStatus}>已保存，未发布</div>
                <Button
                    onClick={() => {
                        /* 预览逻辑 */
                    }}
                >
                    预览
                </Button>
                <Button
                    type="primary"
                    onClick={() => {
                        /* 保存逻辑 */
                    }}
                >
                    保存
                </Button>
            </div>
        </div>
    );
}
