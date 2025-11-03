import React from 'react';
import { Typography } from '@arco-design/web-react';

interface FormItemProps {
  label?: string;
  value: any;
  onChange: (value: any) => void;
  isEdit: boolean;
  component: React.ElementType;
  componentProps?: Record<string, any>;
 
}

const EditableFormItem: React.FC<FormItemProps> = ({
    value,
    onChange,
    isEdit,
    component: Component,
    componentProps = {},
}) => {
  const renderReadOnlyContent = () => {
    if (value === undefined || value === null || value === '') {
      return <span>--</span>;
    }

    if (Array.isArray(value)) {
      return value.join(', ');
    }

    return value;
  };

  return (
    <div style={{ flexGrow: 1 }}>
        {/* 内容 */}
        {isEdit ? (
          <Component
            value={value}
            onChange={onChange}
            {...componentProps}
          />
        ) : (
          renderReadOnlyContent()
        )}
    </div>
  );
};

export default EditableFormItem;
