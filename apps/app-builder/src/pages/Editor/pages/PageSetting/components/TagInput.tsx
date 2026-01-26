import { Button, Select, Input, List, Tag } from '@arco-design/web-react';
import { FilterEntityFields, type AppEntityField } from '@onebase/app';
import {
  COMPONENT_MAP,
  COMPONENT_TYPE_DISPLAY_NAME_MAP,
  FIELD_TAG_TYPE,
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
  fieldType: string;
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
  const [searchValue, setSearchValue] = useState('');
  const [isFocused, setIsFocused] = useState(false);
  const fieldSelectorRef = useRef<HTMLDivElement>(null);

  const { mainEntity } = useAppEntityStore();

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (fieldSelectorRef.current && !fieldSelectorRef.current.contains(event.target as Node)) {
        setShowFieldSelector(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [fieldSelectorRef]);

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
            fieldType: field.fieldType,
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

  const getTypeColor = useCallback((type: string) => {
    for (const ele in FIELD_TAG_TYPE) {
      const item = FIELD_TAG_TYPE[ele as keyof typeof FIELD_TAG_TYPE];
      if (item.VALUE === type) {
        return item.COLOR;
      }
    }
    return '#1979FF';
  }, []);

  const getTypeName = useCallback((field: FieldItem) => {
    for (const ele in FIELD_TAG_TYPE) {
      const item = FIELD_TAG_TYPE[ele as keyof typeof FIELD_TAG_TYPE];
      if (item.VALUE === field.fieldType) {
        return item.LABEL;
      }
    }
    return field.fieldType;
  }, []);

  const filteredFields = allFields.filter(
    (field) =>
      field.label.toLowerCase().includes(searchValue.toLowerCase()) ||
      field.displayName.toLowerCase().includes(searchValue.toLowerCase())
  );

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
    ]),
    EditorView.theme({
      '&.cm-editor.cm-focused': {
        outline: '0 solid transparent'
      }
    }),
    EditorView.baseTheme({
      '.cm-gutterElement': { display: 'none' },
      '.cm-content': { padding: '4px 12px' },
      '.cm-gutters.cm-gutters-before': { borderRightWidth: 0 },
      '.cm-activeLine': { backgroundColor: 'transparent' },
      '.cm-activeLineGutter': { backgroundColor: 'transparent' }
    }),
    EditorView.domEventHandlers({
      focus: () => {
        setIsFocused(true);
      },
      blur: () => {
        setIsFocused(false);
      }
    })
  ];

  return (
    <div className={styles.tagInputWrapper}>
      <CodeMirror
        ref={editorRef}
        value={value}
        onChange={onChange}
        height="100px"
        placeholder={isFocused ? '' : placeholder}
        className={styles.editor}
        extensions={extensions}
      />
      <div className={styles.buttonWrapper}>
        <Button type="outline" onClick={() => setShowFieldSelector(!showFieldSelector)}>
          + 添加字段
        </Button>
        {showFieldSelector && (
          <div ref={fieldSelectorRef} className={styles.fieldSelector}>
            <Input
              placeholder="搜索字段"
              value={searchValue}
              onChange={setSearchValue}
              style={{ marginBottom: '8px' }}
            />
            <List
              size="small"
              style={{ maxHeight: '200px', overflowY: 'auto' }}
              dataSource={filteredFields}
              render={(field, index) => (
                <List.Item key={index} onClick={() => handleAddField(field.fieldName)}>
                  <div className={styles.fieldItemContent}>
                    <span>{field.label}</span>
                    <Tag
                      style={{
                        color: getTypeColor(field.fieldType),
                        backgroundColor: `${getTypeColor(field.fieldType)}22`
                      }}
                      size="small"
                    >
                      {getTypeName(field)}
                    </Tag>
                  </div>
                </List.Item>
              )}
            />
          </div>
        )}
      </div>
    </div>
  );
};

export default TagInput;
