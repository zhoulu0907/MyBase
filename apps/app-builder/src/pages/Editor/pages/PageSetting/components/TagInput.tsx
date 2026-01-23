import { Button, Select } from '@arco-design/web-react';
import { FilterEntityFields, type AppEntityField } from '@onebase/app';
import {
  COMPONENT_MAP,
  COMPONENT_TYPE_DISPLAY_NAME_MAP,
  FORM_COMPONENT_TYPES,
  useAppEntityStore
} from '@onebase/ui-kit';
import React, { useRef, useState, useEffect, useCallback } from 'react';
import CodeMirror, { type ReactCodeMirrorRef } from '@uiw/react-codemirror';
import { EditorView, keymap, type ViewUpdate } from '@codemirror/view';
import { tagPlaceholdersPlugin } from './tagPlaceholders';
import styles from './components.module.less';

interface TagInputProps {
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
}

interface FieldItem {
  id: string;
  displayName: string;
  label: string;
  type: string;
  fieldName: string;
  tableName: string;
  isSystemField: number;
}

interface TagItem {
  label: string;
  fieldName: string;
}

const TagInput: React.FC<TagInputProps> = ({
  value,
  onChange,
  placeholder = '请输入文字或添加字段，至少添加一个字段'
}) => {
  const editorRef = useRef<ReactCodeMirrorRef>(null);
  const updateRef = useRef<ViewUpdate | null>(null);
  const [showFieldSelector, setShowFieldSelector] = useState(false);
  const [customFields, setCustomFields] = useState<FieldItem[]>([]);
  const [systemFields, setSystemFields] = useState<FieldItem[]>([]);
  const [tags, setTags] = useState<TagItem[]>([]);

  const { mainEntity } = useAppEntityStore();

  useEffect(() => {
    if (mainEntity.fields.length > 0) {
      const fields = mainEntity.fields
        .filter((field: AppEntityField) => !FilterEntityFields.includes(field.fieldName))
        .map((field: AppEntityField, index: number) => {
          let cpType = COMPONENT_MAP[field.fieldType];
          if (!cpType) {
            cpType = FORM_COMPONENT_TYPES.INPUT_TEXT;
          }
          return {
            id: `${cpType}-${index}-${Date.now()}`,
            displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[cpType] || '',
            label: field.displayName,
            type: cpType,
            tableName: mainEntity.tableName,
            fieldName: field.fieldName,
            isSystemField: field.isSystemField
          };
        });

      const custom = fields.filter((item) => item.isSystemField === 0);
      const system = fields.filter((item) => item.isSystemField === 1);

      setCustomFields(custom);
      setSystemFields(system);
    }
  }, [mainEntity]);

  const allFields = [...customFields, ...systemFields];

  const insertTag = useCallback(
    (field: FieldItem) => {
      if (!updateRef.current?.view) return;

      const view = updateRef.current.view;
      const state = view.state;
      const [range] = state?.selection?.ranges || [];

      const insertFrom = range?.from || 0;
      const insertTo = range?.to || insertFrom;

      const tagText = `{{${field.label}}}`;

      view.dispatch({
        changes: {
          from: insertFrom,
          to: insertTo,
          insert: tagText
        },
        selection: {
          anchor: insertFrom + tagText.length
        }
      });

      setTags([...tags, { label: field.label, fieldName: field.fieldName }]);
      setShowFieldSelector(false);

      view.focus();
    },
    [tags]
  );

  const handleAddField = useCallback(
    (fieldName: string) => {
      const field = allFields.find((f) => f.fieldName === fieldName);
      if (field) {
        insertTag(field);
      }
    },
    [allFields, tags, insertTag]
  );

  const extensions = [
    EditorView.updateListener.of((update) => {
      updateRef.current = update;
    }),
    tagPlaceholdersPlugin(),
    keymap.of([
      {
        key: 'Backspace',
        run: (view) => {
          const state = view.state;
          const [range] = state.selection.ranges;
          const doc = state.doc;

          if (range.from !== range.to) {
            return false;
          }

          const line = doc.lineAt(range.from);
          const lineText = line.text;
          const lineStart = line.from;

          const beforeCursor = lineText.slice(0, range.from - lineStart);

          const tagMatch = beforeCursor.match(/\{\{.+?\}\}$/);

          if (tagMatch) {
            const tagText = tagMatch[0];
            const tagStart = range.from - tagText.length;

            view.dispatch({
              changes: {
                from: tagStart,
                to: range.from,
                insert: ''
              },
              selection: {
                anchor: tagStart
              }
            });

            const tag = tags.find((t) => `{{${t.label}}}` === tagText);
            if (tag) {
              setTags(tags.filter((t) => t.fieldName !== tag.fieldName));
            }

            return true;
          }

          return false;
        }
      }
    ])
  ];

  return (
    <div className={styles.tagInputWrapper}>
      <CodeMirror
        ref={editorRef}
        value={value}
        onChange={onChange}
        height="100px"
        placeholder={placeholder}
        className={styles.editor}
        extensions={extensions}
      />
      <div style={{ position: 'relative', marginTop: '8px' }}>
        <Button type="primary" onClick={() => setShowFieldSelector(!showFieldSelector)}>
          + 添加字段
        </Button>
        {showFieldSelector && (
          <div className={styles.fieldSelector}>
            <Select
              placeholder="请选择字段"
              options={allFields.map((field) => ({
                label: field.label,
                value: field.fieldName
              }))}
              onChange={handleAddField}
              style={{ width: '100%' }}
              showSearch
            />
          </div>
        )}
      </div>
    </div>
  );
};

export default TagInput;
