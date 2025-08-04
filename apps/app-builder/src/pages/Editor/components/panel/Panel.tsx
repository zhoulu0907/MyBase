import { Button, Tabs } from '@arco-design/web-react';
import { IconBranch, IconLayout, IconLeft, IconRight, IconSend } from '@arco-design/web-react/icon';
import { useEffect, useState } from 'react';
import type { EditorType } from '../const';
import { EDITOR_TYPES } from '../const';
import MaterialContainer from './components/material';
import MetadataContainer from './components/metadata';
import TelegramContainer from './components/telegram';
import styles from './index.module.less';

// 定义类型
const PANNEL_KEYS = ['material', 'metadata', 'telegram'] as const;
type PannelKey = typeof PANNEL_KEYS[number];

export default function EditorPanel() {
    const [showDrawer, setShowDrawer] = useState(true);
    const [activeTab, setActiveTab] = useState<EditorType>(EDITOR_TYPES.FORM_EDITOR);
    const [activeLeftTabKey, setActiveLeftTabKey] = useState<PannelKey>('material');

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

    return (
      <div className={styles.editorPanel}
        style={{
            width: showDrawer ? "340px" : "0px",
            transition: "width 0.3s cubic-bezier(0.4, 0, 0.4, 1)",
        }}
      >
        <Button
            size="mini"
            className={styles.drawerButton}
            icon={showDrawer ? <IconLeft /> : <IconRight />}
            type="dashed"
            onClick={() => setShowDrawer(!showDrawer)}
        />
        <div className={styles.left}
            style={{
                width: showDrawer ? "48px" : "0px",
            }}
        >
          <Tabs
              type="text"
              activeTab={activeLeftTabKey}
              onChange={(key) => {
                setActiveLeftTabKey(key as PannelKey);
              }}
              size="large"
              direction="vertical"
          >
              <Tabs.TabPane key={"material"} title={<IconLayout fontSize={20}/>} />
              <Tabs.TabPane key={"metadata"} title={<IconBranch fontSize={20}/>} />
              {activeTab === EDITOR_TYPES.FORM_EDITOR && <Tabs.TabPane key={"telegram"} title={<IconSend fontSize={20}/>} />}
              {/* <Tabs.TabPane key={"key4"} title={<IconStorage fontSize={20}/>} /> */}
          </Tabs>
        </div>

        <div className={styles.right}
          style={{
            width: showDrawer ? "100%" : "0px",
          }}
        >
            {
                activeLeftTabKey === "material" &&
                <MaterialContainer activeTab={activeTab} />
            }
            {
                activeLeftTabKey === "metadata" &&
                <MetadataContainer/>
            }
            {
                activeLeftTabKey === "telegram" &&
                <TelegramContainer />
            }
        </div>
      </div>
    );
}