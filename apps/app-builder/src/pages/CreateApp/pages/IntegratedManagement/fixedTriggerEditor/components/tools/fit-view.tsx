import { IconExpand } from '@douyinfe/semi-icons';
import { IconButton, Tooltip } from '@douyinfe/semi-ui';

export const FitView = (props: { fitView: () => void }) => (
  <Tooltip content="FitView">
    <IconButton icon={<IconExpand />} type="tertiary" theme="borderless" onClick={() => props.fitView()} />
  </Tooltip>
);
