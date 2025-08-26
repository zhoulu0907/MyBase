import { IconServer } from '@douyinfe/semi-icons';
import { Button, Tooltip } from '@douyinfe/semi-ui';
import { usePlaygroundTools } from '@flowgram.ai/fixed-layout-editor';

export const SwitchVertical = () => {
  const tools = usePlaygroundTools();
  return (
    <Tooltip content={!tools.isVertical ? '垂直布局' : '水平布局'}>
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
