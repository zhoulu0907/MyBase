import { useI18n } from '@/hooks/useI18n';
import { Collapse } from '@arco-design/web-react';
import React from 'react';
import IconCollapsedDown from '@/assets/images/collapse_down_icon.svg';
import { ComponentList } from './component-list';
import { useWorkbenchItems } from '../../hooks/use-workbench-items';

interface WorkbenchPanelProps {
  keyword: string;
}

const COLLAPSE_ITEM_STYLE = {
  style: { border: 'none' },
  contentStyle: { backgroundColor: '#fff', border: 'none', paddingLeft: 13 }
} as const;

/**
 * 工作台组件面板
 */
const WorkbenchPanel: React.FC<WorkbenchPanelProps> = ({ keyword }) => {
  const { t } = useI18n();
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
        header={t('editor.basicComponent', '基础组件')}
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
        header={t('editor.advancedComponent', '高级组件')}
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
};

export default WorkbenchPanel;
