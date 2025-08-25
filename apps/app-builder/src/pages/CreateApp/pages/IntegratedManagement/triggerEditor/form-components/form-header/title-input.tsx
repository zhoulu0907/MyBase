import { Input, Typography } from '@arco-design/web-react';
import { Field, type FieldRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { useEffect, useRef } from 'react';
import { Feedback } from '../feedback';
import { Title } from './styles';

const { Text } = Typography;

export function TitleInput(props: {
  readonly: boolean;
  titleEdit: boolean;
  updateTitleEdit: (setEdit: boolean) => void;
}): JSX.Element {
  const { readonly, titleEdit, updateTitleEdit } = props;
  const ref = useRef<any>();
  const titleEditing = titleEdit && !readonly;
  useEffect(() => {
    if (titleEditing) {
      ref.current?.focus();
    }
  }, [titleEditing]);

  return (
    <Title>
      <Field name="title">
        {({ field: { value, onChange }, fieldState }: FieldRenderProps<string>) => (
          <div style={{ height: 24 }}>
            {titleEditing ? (
              <Input value={value} onChange={onChange} ref={ref} onBlur={() => updateTitleEdit(false)} size="small" />
            ) : (
              <Text ellipsis={{ showTooltip: true }} style={{ fontSize: 15 }}>
                {value}
              </Text>
            )}
            <Feedback errors={fieldState?.errors} />
          </div>
        )}
      </Field>
    </Title>
  );
}
