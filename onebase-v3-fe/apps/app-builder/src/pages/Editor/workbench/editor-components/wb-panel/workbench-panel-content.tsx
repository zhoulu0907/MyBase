import { Collapse } from '@arco-design/web-react';
import IconCollapsedDown from '@/assets/images/collapse_down_icon.svg';
import { ComponentList } from './component-list';
import { useWorkbenchItems } from '../../hooks/use-workbench-items';

interface WorkbenchPanelContentProps {
  keyword: string;
}

const COLLAPSE_ITEM_STYLE = {
  style: { border: 'none' },
  contentStyle: { backgroundColor: '#fff', border: 'none', paddingLeft: 13 }
} as const;

/**
 * 工作台组件面板内容
 */
export function WorkbenchPanelContent({ keyword }: WorkbenchPanelContentProps) {
  const basicItems = useWorkbenchItems({ keyword, category: 'basic' });
  const advancedItems = useWorkbenchItems({ keyword, category: 'advanced' });

  return (
    <Collapse
      defaultActiveKey={['basic', 'advanced']}
      accordion={false}
      bordered={false}
      expandIconPosition="right"
      expandIcon={<img src={IconCollapsedDown} alt="" />}
    >
      <Collapse.Item
        header="基础组件"
        name="basic"
        key="basic"
        style={COLLAPSE_ITEM_STYLE.style}
        contentStyle={COLLAPSE_ITEM_STYLE.contentStyle}
      >
        <ComponentList
          items={basicItems.items}
          components={basicItems.components}
          onItemsChange={basicItems.setItems}
        />
      </Collapse.Item>

      <Collapse.Item
        header="高级组件"
        name="advanced"
        key="advanced"
        style={COLLAPSE_ITEM_STYLE.style}
        contentStyle={COLLAPSE_ITEM_STYLE.contentStyle}
      >
        <ComponentList
          items={advancedItems.items}
          components={advancedItems.components}
          onItemsChange={advancedItems.setItems}
        />
      </Collapse.Item>
    </Collapse>
  );
}
