import { EditRender } from '@onebase/ui-kit';
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
