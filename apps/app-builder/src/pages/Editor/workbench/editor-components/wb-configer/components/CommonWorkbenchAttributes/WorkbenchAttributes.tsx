import { Form } from '@arco-design/web-react';
import type { ReactNode, FC } from 'react';
import attributeStyles from './attributes.module.less';
import ComponentIdFormItem from './ComponentIdFormItem';
import { useWorkbenchAttributeContext } from './useWorkbenchAttributeContext';

import type { WorkbenchAttributeContext } from './useWorkbenchAttributeContext';

export interface WorkbenchAttributesProps {
  /**
   * 自定义渲染配置面板内容（折叠面板等）
   * 如果不传，则默认直接按照 editData 顺序渲染所有配置项
   */
  renderPanels?: (ctx: WorkbenchAttributeContext) => ReactNode;
}

const WorkbenchAttributes: FC<WorkbenchAttributesProps> = ({ renderPanels }) => {
  const ctx = useWorkbenchAttributeContext();
  const { cpID, editData, isSchemaReady, renderEditItem } = ctx;

  if (!isSchemaReady) {
    return null;
  }

  return (
    <div className={attributeStyles.attributes}>
      {cpID && (
        <Form autoComplete="off" layout="vertical">
          {renderPanels
            ? renderPanels({ ...ctx, cpID })
            : editData.map((item, index) => <div key={`${item.key}-${index}`}>{renderEditItem({ item, index })}</div>)}

          <ComponentIdFormItem cpID={cpID} />
        </Form>
      )}
    </div>
  );
};

export default WorkbenchAttributes;
