import { EditRender, WORKBENCH_COMPONENT_TYPES } from '@onebase/ui-kit';
import { ResizableWorkbenchItem } from './resizable-workbench-item';
import { OperationButtons } from './operatio-buttons';
import type { WorkbenchItemProps } from '../../../types/workbench-component';

/**
 * 工作台组件项
 */
export function WorkbenchItem({
  component,
  isSelected,
  currentWidth,
  containerWidth,
  pageComponentSchema,
  onOperation
}: WorkbenchItemProps) {
  // 按钮组件在 web 端完全隐藏，不渲染任何内容
  if (component.type === WORKBENCH_COMPONENT_TYPES.BUTTON_WORKBENCH) {
    return null;
  }

  const handleSelect = () => {
    onOperation.select(component.id, component);
  };

  return (
    <ResizableWorkbenchItem
      componentId={component.id}
      componentType={component.type}
      currentWidth={currentWidth}
      containerWidth={containerWidth}
      onWidthChange={onOperation.widthChange}
      isSelected={isSelected}
      onSelect={handleSelect}
    >
      <div data-cp-type={component.type} data-cp-displayname={component.displayName} data-cp-id={component.id}>
        <EditRender
          cpId={component.id}
          cpType={component.type}
          runtime={false}
          pageComponentSchema={pageComponentSchema}
        />

        {isSelected && pageComponentSchema && (
          <OperationButtons component={component} pageComponentSchema={pageComponentSchema} onOperation={onOperation} />
        )}
      </div>
    </ResizableWorkbenchItem>
  );
}
