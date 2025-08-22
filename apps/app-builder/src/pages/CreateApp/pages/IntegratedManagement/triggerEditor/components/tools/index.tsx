import { useEffect, useState } from 'react';

import { Button } from '@arco-design/web-react';
import { IconFullscreenExit, IconMinus, IconPlus, IconRedo, IconUndo } from '@arco-design/web-react/icon';
import { useClientContext, usePlaygroundTools } from '@flowgram.ai/free-layout-editor';
import styles from './index.module.less';

export function Tools() {
  const { history } = useClientContext();
  const tools = usePlaygroundTools();
  const [canUndo, setCanUndo] = useState(false);
  const [canRedo, setCanRedo] = useState(false);

  useEffect(() => {
    const disposable = history.undoRedoService.onChange(() => {
      setCanUndo(history.canUndo());
      setCanRedo(history.canRedo());
    });
    return () => disposable.dispose();
  }, [history]);

  return (
    <div className={styles.tools}>
      <Button type="default" size="mini" className={styles.toolItem} onClick={() => tools.zoomin()}>
        <IconPlus className={styles.toolIcon} />
      </Button>

      <span>{Math.floor(tools.zoom * 100)}%</span>

      <Button type="default" size="mini" className={styles.toolItem} onClick={() => tools.zoomout()}>
        <IconMinus className={styles.toolIcon} />
      </Button>

      <Button
        type="default"
        size="mini"
        className={styles.toolItem}
        onClick={() => {
          tools.fitView();
          tools.autoLayout();
        }}
      >
        <IconFullscreenExit className={styles.toolIcon} />
      </Button>
      <Button type="default" size="mini" className={styles.toolItem} onClick={() => history.undo()} disabled={!canUndo}>
        <IconUndo className={styles.toolIcon} />
      </Button>
      <Button type="default" size="mini" className={styles.toolItem} onClick={() => history.redo()} disabled={!canRedo}>
        <IconRedo className={styles.toolIcon} />
      </Button>
    </div>
  );
}
