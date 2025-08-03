import React from "react";
import EditorConfig from "../Editor/components/config";
import EditorPanel from "../Editor/components/panel/Panel";
import EditorWorkspace from "../Editor/components/workspace/Workspace";

import styles from "./index.module.less";

const FormEditor: React.FC = () => {

    return (
        <div className={styles.formEditorPage}>
            <EditorPanel />
            <EditorWorkspace />
            <EditorConfig />
        </div>
    );
};

export { FormEditor };
