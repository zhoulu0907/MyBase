import React, { useState } from "react";
import EditorConfig from "../Editor/components/config";
import EditorPanel from "../Editor/components/panel/Panel";
import EditorWorkspace from "../Editor/components/workspace/Workspace";
import styles from "./index.module.less";

const ListEditor: React.FC = () => {
    const [showEmpty, setShowEmpty] = useState(true);

    const handleHoverOverWorkspace = (val: boolean) => {
        setShowEmpty(val);
    };

    return (
        <div className={styles.listEditorPage}>
            <EditorPanel onDragComponents={handleHoverOverWorkspace} />
            <EditorWorkspace showEmpty={showEmpty} />
            <EditorConfig />
        </div>
    );
};

export { ListEditor };
