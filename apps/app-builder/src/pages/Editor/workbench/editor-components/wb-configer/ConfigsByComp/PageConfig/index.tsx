import { Form, Switch, ColorPicker, Input, Message } from '@arco-design/web-react';
import { IconCloud } from '@arco-design/web-react/icon';
import { uploadFile, getFileUrlById } from '@onebase/platform-center';
import { useWorkbenchSignal, isPageConfig, PAGE_CONFIG_TYPE, PAGE_CONFIG_DEFAULT } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useState, useRef, useCallback } from 'react';
import styles from './index.module.less';

const FormItem = Form.Item;

// 允许的文件格式列表
const allowedFormats = ['image/jpeg', 'image/png', 'image/gif'];

/**
 * 页面配置组件
 */
const PageConfig = () => {
  useSignals();

  const { curComponentSchema, setCurComponentSchema, setWbComponentSchemas } = useWorkbenchSignal();

  const [pageConfig, setPageConfig] = useState(PAGE_CONFIG_DEFAULT);

  const fileInputRef = useRef<HTMLInputElement>(null);

  const formItemLayout = {
    labelCol: { span: 21 },
    wrapperCol: { span: 1 },
    layout: 'horizontal' as const
  };

  useEffect(() => {
    // 使用工具函数判断是否为页面配置
    if (isPageConfig(curComponentSchema)) {
      const config = curComponentSchema.config || {};
      const newConfig = {
        showHeader: config.showHeader ?? PAGE_CONFIG_DEFAULT.showHeader,
        showSidebar: config.showSidebar ?? PAGE_CONFIG_DEFAULT.showSidebar,
        pageBgColor: config.pageBgColor ?? PAGE_CONFIG_DEFAULT.pageBgColor,
        pageBgImg: config.pageBgImg ?? PAGE_CONFIG_DEFAULT.pageBgImg
      };

      // 只有当配置真正变化时才更新状态
      if (
        newConfig.showHeader !== pageConfig.showHeader ||
        newConfig.showSidebar !== pageConfig.showSidebar ||
        newConfig.pageBgColor !== pageConfig.pageBgColor ||
        newConfig.pageBgImg !== pageConfig.pageBgImg
      ) {
        setPageConfig(newConfig);
      }
    }
  }, [
    curComponentSchema,
    curComponentSchema?.config?.showHeader,
    curComponentSchema?.config?.showSidebar,
    curComponentSchema?.config?.pageBgColor,
    curComponentSchema?.config?.pageBgImg
  ]);

  const handleChange = (key: string, value: boolean | string) => {
    const newPageConfig = { ...pageConfig, [key]: value };
    setPageConfig(newPageConfig);

    // 使用当前 schema 的 id
    const pageConfigId = curComponentSchema?.id || 'page-config';

    const newCurComponentSchema = {
      ...curComponentSchema,
      id: pageConfigId,
      type: PAGE_CONFIG_TYPE,
      config: {
        ...curComponentSchema?.config,
        ...newPageConfig
      },
      editData: curComponentSchema?.editData || {}
    };

    // 同时更新当前选中的 schema 和 wbComponentSchemas 中的页面配置
    setCurComponentSchema(newCurComponentSchema);
    setWbComponentSchemas(pageConfigId, newCurComponentSchema);
  };

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

  // 处理粘贴上传
  const handlePaste = useCallback(async (e: ClipboardEvent) => {
    const items = e.clipboardData?.items;
    if (!items) return;

    for (let i = 0; i < items.length; i++) {
      const item = items[i];
      if (item.type.indexOf('image') !== -1) {
        e.preventDefault();
        const file = item.getAsFile();
        if (file) {
          if (!allowedFormats.includes(file.type)) {
            Message.warning(`不支持该格式，仅支持 JPG / JPEG / PNG / GIF`);
            return;
          }
          const isLtMax = file.size / 1024 / 1024 < 5;
          if (!isLtMax) {
            Message.warning(`文件大小不能超过 5MB`);
            return;
          }

          try {
            const uploadImgId = await handleUpload(file);
            if (uploadImgId !== '') {
              const urlImg = getFileUrlById(uploadImgId);
              handleChange('pageBgImg', urlImg);
              Message.success('图片上传成功');
            } else {
              Message.error('图片上传失败');
            }
          } catch {
            Message.error('图片上传失败');
          }
        }
        break;
      }
    }
  }, []);

  // 处理文件选择上传
  const handleFileSelect = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (!allowedFormats.includes(file.type)) {
      Message.warning(`不支持该格式，仅支持 JPG / JPEG / PNG / GIF`);
      return;
    }
    const isLtMax = file.size / 1024 / 1024 < 5;
    if (!isLtMax) {
      Message.warning(`文件大小不能超过 5MB`);
      return;
    }

    try {
      const uploadImgId = await handleUpload(file);
      if (uploadImgId !== '') {
        const urlImg = getFileUrlById(uploadImgId);
        handleChange('pageBgImg', urlImg);
        Message.success('图片上传成功');
      } else {
        Message.error('图片上传失败');
      }
    } catch {
      Message.error('图片上传失败');
    }

    // 清空 input，以便可以重复选择同一文件
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  // 添加粘贴事件监听
  useEffect(() => {
    document.addEventListener('paste', handlePaste);
    return () => {
      document.removeEventListener('paste', handlePaste);
    };
  }, [handlePaste]);

  return (
    <div className={styles.pageConfig}>
      <div className={styles.pageConfigTitle}>布局配置</div>
      <Form autoComplete="off" layout="vertical">
        <FormItem
          label={
            <div style={{ textAlign: 'left' }}>
              <span>显示顶栏</span>
            </div>
          }
          {...formItemLayout}
          className={styles.formItem}
        >
          <Switch
            size="small"
            checked={pageConfig.showHeader}
            onChange={(value) => {
              handleChange('showHeader', value);
            }}
          />
        </FormItem>
        <FormItem
          label={
            <div style={{ textAlign: 'left' }}>
              <span>显示侧边栏</span>
            </div>
          }
          {...formItemLayout}
          className={styles.formItem}
        >
          <Switch
            size="small"
            checked={pageConfig.showSidebar}
            onChange={(value) => {
              handleChange('showSidebar', value);
            }}
          />
        </FormItem>
        <FormItem
          label={
            <div style={{ textAlign: 'left' }}>
              <span>背景颜色</span>
            </div>
          }
          {...formItemLayout}
          className={styles.formItem}
        >
          <ColorPicker
            size="small"
            value={pageConfig.pageBgColor}
            onChange={(value) => {
              handleChange('pageBgColor', value);
            }}
          />
        </FormItem>
        <FormItem
          label={
            <div style={{ textAlign: 'left' }}>
              <span>背景图片</span>
            </div>
          }
          className={styles.formItem}
        >
          <div className={styles.imageUploadContainer}>
            <div className={styles.uploadActions}>
              <Input
                placeholder="支持直接粘贴上传"
                value={pageConfig.pageBgImg}
                onChange={(value) => handleChange('pageBgImg', value)}
                suffix={<IconCloud onClick={() => fileInputRef.current?.click()} />}
              />
              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                style={{ display: 'none' }}
                onChange={handleFileSelect}
              />
            </div>
            {pageConfig.pageBgImg && (
              <div className={styles.imagePreview}>
                <img src={pageConfig.pageBgImg} alt="背景预览" className={styles.previewImage} />
              </div>
            )}
          </div>
        </FormItem>
      </Form>
    </div>
  );
};

export default PageConfig;
