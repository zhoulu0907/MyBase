import { Input, Typography } from '@arco-design/web-react';
import { Field, type FieldRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { useEffect, useRef } from 'react';
import { Feedback } from '../feedback';
import styles from './index.module.less';
import { IconEdit } from '@arco-design/web-react/icon';

const { Text } = Typography;

export function TitleInput(props: {
  readonly: boolean;
  titleEdit: boolean;
  isSidebar: boolean;
  updateTitleEdit: (setEdit: boolean) => void;
}): JSX.Element {
  const { readonly, titleEdit, isSidebar, updateTitleEdit } = props;
  const ref = useRef<any>();
  const titleEditing = titleEdit && !readonly;
  useEffect(() => {
    if (titleEditing) {
      ref.current?.focus();
    }
  }, [titleEditing]);

  return (
    <div className={isSidebar ? styles.sidebarTitle : styles.title}>
      <Field name="title">
        {({ field: { value, onChange }, fieldState }: FieldRenderProps<string>) => (
          <div style={{ height: 28 }}>
            {titleEditing ? (
              <Input value={value} onChange={onChange} ref={ref} onBlur={() => updateTitleEdit(false)} size="small" />
            ) : (
              <div style={{ display: 'flex', width: '100%' }}>
                <Text
                  ellipsis={{
                    showTooltip: {
                      props: {
                        position: isSidebar ? 'bottom' : 'top'
                      }
                    }
                  }}
                  style={{ fontSize: 15, lineHeight: '28px', width: isSidebar ? 'auto' : '100%', marginBottom: 0 }}
                >
                  {value}
                </Text>
                {isSidebar && <IconEdit style={{ padding: '7px 0 0 8px' }} onClick={() => updateTitleEdit(true)} />}
              </div>
            )}
            <Feedback errors={fieldState?.errors} />
          </div>
        )}
      </Field>
    </div>
  );
}
