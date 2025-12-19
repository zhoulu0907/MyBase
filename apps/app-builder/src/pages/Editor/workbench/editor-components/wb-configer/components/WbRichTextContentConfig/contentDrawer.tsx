import { useState, useCallback, useMemo, useEffect } from 'react';
import { Drawer, Message } from '@arco-design/web-react';
import type { DrawerProps } from '@arco-design/web-react';
import { Editor, Toolbar } from '@wangeditor/editor-for-react';
import { uploadFile, getFileUrlById } from '@onebase/platform-center';
import { type IDomEditor, type IEditorConfig, type IToolbarConfig } from '@wangeditor/editor';
import styles from './contentDrawer.module.less';

type BaseDrawerProps = Omit<DrawerProps, 'visible' | 'title' | 'children' | 'className'>;
type InsertFnType = (url: string, alt: string, href: string) => void

export interface ContentDrawerProps extends BaseDrawerProps {
  value?: string;
  visible: boolean;
  onClose?: () => void;
  className?: string;
  onChange?: (html: string) => void;
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
}

const toolbarConfig: Partial<IToolbarConfig> = {};
const allowedFormats = ['image/jpeg', 'image/png', 'image/gif'];
const maxSizeMB = 5;
const ContentDrawer = ({ visible, onClose, onChange, value, handlePropsChange, ...rest }: ContentDrawerProps) => {
  const [editor, setEditor] = useState<IDomEditor | null>(null);
  const normalizedValue = value ?? '';

  const handleUpload = async (file: File, onProgress?: (percent: number, event?: ProgressEvent) => void) => {
    const formData = new FormData();
    formData.append('file', file);

    const progressAdapter = onProgress
      ? (progressEvent: ProgressEvent) => {
          if (progressEvent.lengthComputable) {
            const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total);
            onProgress(percent, progressEvent);
          }
        }
      : undefined;

    const res = await uploadFile(formData, progressAdapter);
    return res;
  };

  const editorConfig: Partial<IEditorConfig> = {
    placeholder: '请输入内容...',
    MENU_CONF: {
      // 上传图片配置
      uploadImage: {
        fieldName: 'file',
        maxFileSize: 2 * 1024 * 1024,
        allowedFileTypes: ['image/*'],
        maxNumberOfFiles: 1,
        async customUpload(file: File, insertFn: InsertFnType) {
          try {
            const uploadImgId = await handleUpload(file);
            const uploadImgUrl = getFileUrlById(uploadImgId);
            console.log('uploadImgUrl', uploadImgUrl, file);
            if (uploadImgUrl !== '') {
              handlePropsChange('WbRichTextContentConfig', uploadImgId);
              // 最后插入图片
              insertFn(uploadImgUrl, file?.name, uploadImgUrl);
            } else {
              Message.error('上传失败');
            }
          } catch {
            Message.error('上传失败');
          }
        },
        onBeforeUpload: (file: File) => {
          if (!allowedFormats.includes(file.type)) {
            Message.warning(`不支持该格式，仅支持 JPG / JPEG / PNG / GIF`);
            return false;
          }
          const isLtMax = file.size / 1024 / 1024 < maxSizeMB;
          if (!isLtMax) {
            Message.warning(`文件大小不能超过 ${maxSizeMB}MB`);
            return false;
          }
        }
      }
    },
    scroll: true
  };

  const stableOnChange = useCallback(
    (html: string) => {
      onChange?.(html);
    },
    [onChange]
  );

  // 使用 useMemo 缓存编辑器变化的回调函数
  const handleEditorChange = useMemo(() => {
    return (currentEditor: IDomEditor) => {
      const newHtml = currentEditor.getHtml();
      if (newHtml !== normalizedValue) {
        stableOnChange(newHtml);
      }
    };
  }, [normalizedValue, stableOnChange]);

  useEffect(() => {
    if (!editor) {
      return;
    }
    if (normalizedValue !== editor.getHtml()) {
      editor.setHtml(normalizedValue);
    }
  }, [editor, normalizedValue]);

  useEffect(() => {
    return () => {
      if (editor == null) return;
      editor.destroy();
      setEditor(null);
    };
  }, [editor]);
  return (
    <Drawer
      {...rest}
      visible={visible}
      width="100%"
      placement="bottom"
      // mask={false}
      // maskClosable={false}
      closable={true}
      getPopupContainer={() => document.body}
      className={styles.drawer}
      onCancel={onClose}
      title="内容编辑"
      footer={null}
      height="50vh"
    >
      <div className={styles.content}>
        <Toolbar editor={editor} defaultConfig={toolbarConfig} mode="default" className={styles.Toolbar} />
        <Editor
          defaultConfig={editorConfig}
          onCreated={setEditor}
          onChange={handleEditorChange}
          mode="default"
          className={styles.editor}
        />
      </div>
    </Drawer>
  );
};

export default ContentDrawer;
