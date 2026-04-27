import { IconGridRectangle } from '@douyinfe/semi-icons';
import { IconButton, Tooltip } from '@douyinfe/semi-ui';

export const MinimapSwitch = (props: { minimapVisible: boolean; setMinimapVisible: (visible: boolean) => void }) => {
  const { minimapVisible, setMinimapVisible } = props;

  return (
    <Tooltip content="缩略图">
      <IconButton
        theme="borderless"
        icon={
          <IconGridRectangle
            style={{
              color: minimapVisible ? undefined : '#060709cc'
            }}
          />
        }
        onClick={() => {
          setMinimapVisible(Boolean(!minimapVisible));
        }}
      />
    </Tooltip>
  );
};
