import { useEffect, useState } from 'react';

import { IconRedo, IconUndo } from '@douyinfe/semi-icons';
import { IconButton, Tooltip } from '@douyinfe/semi-ui';
import { usePlayground, usePlaygroundTools, useRefresh } from '@flowgram.ai/fixed-layout-editor';

import { FitView } from './fit-view';
import { Minimap } from './minimap';
import { MinimapSwitch } from './minimap-switch';
import { ToolContainer, ToolSection } from './styles';
import { SwitchVertical } from './switch-vertical';
import { ZoomSelect } from './zoom-select';

export const Tools = () => {
  const tools = usePlaygroundTools();
  const [minimapVisible, setMinimapVisible] = useState(false);
  const playground = usePlayground();
  const refresh = useRefresh();

  useEffect(() => {
    const disposable = playground.config.onReadonlyOrDisabledChange(() => refresh());
    return () => disposable.dispose();
  }, [playground]);

  return (
    <ToolContainer className="fixed-demo-tools">
      <ToolSection>
        <SwitchVertical />
        <ZoomSelect />
        <FitView fitView={tools.fitView} />
        <MinimapSwitch minimapVisible={minimapVisible} setMinimapVisible={setMinimapVisible} />
        <Minimap visible={minimapVisible} />
        {/* <Readonly /> */}
        <Tooltip content="Undo">
          <IconButton
            theme="borderless"
            icon={<IconUndo />}
            disabled={!tools.canUndo || playground.config.readonly}
            onClick={() => tools.undo()}
          />
        </Tooltip>
        <Tooltip content="Redo">
          <IconButton
            theme="borderless"
            icon={<IconRedo />}
            disabled={!tools.canRedo || playground.config.readonly}
            onClick={() => tools.redo()}
          />
        </Tooltip>
        {/* <Save disabled={playground.config.readonly} /> */}
        {/* <Run /> */}
      </ToolSection>
    </ToolContainer>
  );
};
