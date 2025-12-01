import React from 'react';
import { Space } from '@arco-design/web-react';
import { formatIndustryType } from '../../utils';

interface FormItemProps {
  label?: string;
  logoContent?: React.ReactNode;
  value: any;
  onChange: (value: any) => void;
  isEdit: boolean;
  component: React.ElementType;
  componentProps?: Record<string, any>;

}

const EditableFormItem: React.FC<FormItemProps> = ({
  logoContent,
  label,
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

    if(label=== "industryType") {
      return formatIndustryType(componentProps?.options, value);
    }

    return value;
  };

  const renderEditContent = () => {
    if (label === "logo") {
      return (
        <Space direction='vertical'>
          {value}
          <Component
            value={value}
            onChange={onChange}
            {...componentProps}
          >{logoContent}</Component>
        </Space>
      )
    } else {
      return <Component
        value={value}
        onChange={onChange}
        {...componentProps}
      />
    }
  }

  return (
    <div style={{ flexGrow: 1 }}>
      {isEdit ? renderEditContent() : renderReadOnlyContent()}
    </div>
  );
};

export default EditableFormItem;
