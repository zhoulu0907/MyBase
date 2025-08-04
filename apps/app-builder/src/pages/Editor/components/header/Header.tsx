import AppIcon from "@/assets/images/app_icon.svg";
import activeFormDesignSVG from "@/assets/images/form_design_active_icon.svg";
import defaultFormDesignSVG from "@/assets/images/form_design_default_icon.svg";
import activeListDesignSVG from "@/assets/images/list_design_active_icon.svg";
import defaultListDesignSVG from "@/assets/images/list_design_default_icon.svg";
import activeSourceDataSVG from "@/assets/images/source_data_active_icon.svg";
import defaultSourceDataSVG from "@/assets/images/source_data_default_icon.svg";
import { usePageEditorStore } from "@/hooks/useStore";
import { Button, Tabs } from "@arco-design/web-react";
import { IconArrowLeft } from "@arco-design/web-react/icon";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { EDITOR_TYPES } from '../const';
import styles from "./index.module.less";

const tabData = [
    {
        key: EDITOR_TYPES.FORM_EDITOR,
        title: "表单设计",
        alt: "Form Design",
        defaultIcon: defaultFormDesignSVG,
        activeIcon: activeFormDesignSVG,
    },
    {
        key: EDITOR_TYPES.LIST_EDITOR,
        title: "列表设计",
        alt: "List Design",
        defaultIcon: defaultListDesignSVG,
        activeIcon: activeListDesignSVG,
    },
    {
        key: EDITOR_TYPES.PAGE_SETTING,
        title: "页面设置",
        alt: "",
    },
    {
        key: EDITOR_TYPES.METADATA_MANAGE,
        title: "元数据管理",
        alt: "Source Data",
        defaultIcon: defaultSourceDataSVG,
        activeIcon: activeSourceDataSVG,
    },
];

export default function EditorHeader() {
    const { clearCurComponentID, components, pageComponentSchemas } = usePageEditorStore();

    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState("");

    useEffect(() => {
        // 根据当前 URL 动态设置 activeTab
        const hash = window.location.hash;
        if (hash.endsWith(EDITOR_TYPES.FORM_EDITOR)) {
            setActiveTab(EDITOR_TYPES.FORM_EDITOR);
        } else if (hash.endsWith(EDITOR_TYPES.LIST_EDITOR)) {
            setActiveTab(EDITOR_TYPES.LIST_EDITOR);
        } else if (hash.endsWith(EDITOR_TYPES.PAGE_SETTING)) {
            setActiveTab(EDITOR_TYPES.PAGE_SETTING);
        } else if (hash.endsWith(EDITOR_TYPES.METADATA_MANAGE)) {
            setActiveTab(EDITOR_TYPES.METADATA_MANAGE);
        }
    }, []);

    const handleSaveApp = () => {
        console.log("save app");
        console.log(components);
        console.log(pageComponentSchemas);
    }

    return (
        <div className={styles.editorHeader}>
            {/* 左侧 */}
            <div className={styles.left}>
                <Button
                    shape="circle"
                    type="default"
                    size="small"
                    onClick={() => {
                        navigate("/onebase/create-app");
                    }}
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
                        clearCurComponentID();
                        switch (key) {
                            case EDITOR_TYPES.FORM_EDITOR:
                                navigate(`/onebase/editor/${EDITOR_TYPES.FORM_EDITOR}`);
                                break;
                            case EDITOR_TYPES.LIST_EDITOR:
                                navigate(`/onebase/editor/${EDITOR_TYPES.LIST_EDITOR}`);
                                break;
                            case EDITOR_TYPES.PAGE_SETTING:
                                navigate(`/onebase/editor/${EDITOR_TYPES.PAGE_SETTING}`);
                                break;
                            case EDITOR_TYPES.METADATA_MANAGE:
                                navigate(`/onebase/editor/${EDITOR_TYPES.METADATA_MANAGE}`);
                                break;
                            default:
                                break;
                        }
                    }}
                    size="large"
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
                        handleSaveApp();
                    }}
                >
                    保存
                </Button>
            </div>
        </div>
    );
}
