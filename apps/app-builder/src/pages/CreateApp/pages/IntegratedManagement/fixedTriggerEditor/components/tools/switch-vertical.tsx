import { IconServer } from '@douyinfe/semi-icons';
import { Button, Tooltip } from '@douyinfe/semi-ui';
import { usePlaygroundTools } from '@flowgram.ai/fixed-layout-editor';

export const SwitchVertical = () => {
  const tools = usePlaygroundTools();
  return (
    <Tooltip content={!tools.isVertical ? 'Vertical Layout' : 'Horizontal Layout'}>
      <Button
        theme="borderless"
        size="small"
        onClick={() => tools.changeLayout()}
        icon={
          <IconServer
            style={{
              transform: !tools.isVertical ? '' : 'rotate(90deg)',
              transition: 'transform .3s ease'
            }}
          />
        }
        type="tertiary"
      />
    </Tooltip>
  );
};
