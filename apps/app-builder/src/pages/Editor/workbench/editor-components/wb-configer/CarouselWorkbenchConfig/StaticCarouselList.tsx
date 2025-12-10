import { Button, Form, Input, Message, Popconfirm, Radio, Select } from '@arco-design/web-react';
import { IconCloud, IconDelete, IconDragDotVertical, IconEdit, IconPlus } from '@arco-design/web-react/icon';
import { uploadFile } from '@onebase/platform-center';
import { usePageViewEditorSignal } from '@onebase/ui-kit';
import { useState, useEffect, useRef, useCallback } from 'react';
import { ReactSortable } from 'react-sortablejs';
import ConfigDrawer from '@/pages/Editor/workbench/components/configDrawer';
import styles from './StaticCarouselList.module.less';
import attributeStyles from '../attributes.module.less';

const FormItem = Form.Item;
const Option = Select.Option;

interface CarouselItem {
  title?: string;
  image?: string;
  linkType?: 'internal' | 'external';
  internalPageId?: string;
  url?: string;
  text?: string; // 保留兼容性
  [key: string]: unknown;
}

interface StaticCarouselListProps {
  carouselConfig: CarouselItem[];
  maxSizeMB?: number;
  maxCount?: number;
  onConfigChange: (config: CarouselItem[]) => void;
}

// 允许的文件格式列表
const allowedFormats = ['image/jpeg', 'image/png', 'image/gif'];

const StaticCarouselList = ({ carouselConfig, maxSizeMB = 5, onConfigChange }: StaticCarouselListProps) => {
  const [items, setItems] = useState<CarouselItem[]>(carouselConfig || []);
  const [editingIndex, setEditingIndex] = useState<number | null>(null);
  const [drawerVisible, setDrawerVisible] = useState(false);
  const [form] = Form.useForm();

  useEffect(() => {
    setItems(carouselConfig || []);
  }, [carouselConfig]);

  const { pageViews } = usePageViewEditorSignal;
  const fileInputRef = useRef<HTMLInputElement>(null);

  const getItemName = (item: CarouselItem, index: number) => {
    if (item.title) {
      return item.title;
    }
    if (item.text) {
      return item.text;
    }
    return `图片名称${index + 1}`;
  };

  // 获取页面列表选项
  const getPageOptions = () => {
    const pages = (pageViews.value || {}) as Record<string, { pageName?: string; id: string }>;
    return Object.values(pages).map((page) => ({
      label: page.pageName || page.id,
      value: page.id
    }));
  };

  const handleSort = (newList: CarouselItem[]) => {
    setItems(newList);
    onConfigChange(newList);
  };

  const handleEdit = (index: number) => {
    const item = items[index];
    form.setFieldsValue({
      title: item.title || item.text || '',
      image: item.image || '',
      linkType: item.linkType || 'internal',
      internalPageId: item.internalPageId || '',
      url: item.url || ''
    });
    setEditingIndex(index);
    setDrawerVisible(true);
  };

  const handleDelete = (index: number) => {
    const newItems = items.filter((_, idx) => idx !== index);
    setItems(newItems);
    onConfigChange(newItems);
    Message.success('删除成功');
  };

  const handleAdd = () => {
    const newItem: CarouselItem = {
      title: '图片名称' + (items.length + 1),
      image: '',
      linkType: 'internal',
      internalPageId: '',
      url: ''
    };
    const newItems = [...items, newItem];
    setItems(newItems);
    onConfigChange(newItems);
  };

  const handleDrawerClose = () => {
    setDrawerVisible(false);
    setEditingIndex(null);
    form.resetFields();
  };

  const [pendingValues, setPendingValues] = useState<CarouselItem | null>(null);

  useEffect(() => {
    if (editingIndex === null || !pendingValues) {
      return;
    }
    setItems((prevItems) => {
      if (editingIndex < 0 || editingIndex >= prevItems.length) {
        return prevItems;
      }
      const newItems = prevItems.map((item, idx) => (idx === editingIndex ? { ...item, ...pendingValues } : item));
      onConfigChange(newItems);
      return newItems;
    });
    setPendingValues(null);
  }, [editingIndex, onConfigChange, pendingValues]);

  const handleFormValuesChange = useCallback((_: Record<string, unknown>, allValues: CarouselItem) => {
    setPendingValues(allValues);
  }, []);

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
  const handlePaste = useCallback(
    async (e: ClipboardEvent) => {
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
            const isLtMax = file.size / 1024 / 1024 < maxSizeMB;
            if (!isLtMax) {
              Message.warning(`文件大小不能超过 ${maxSizeMB}MB`);
              return;
            }

            try {
              const uploadImgUrl = await handleUpload(file);
              if (uploadImgUrl !== '') {
                form.setFieldValue('image', uploadImgUrl);
                setPendingValues(form.getFieldsValue());
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
    },
    [maxSizeMB, form]
  );

  // 处理文件选择上传
  const handleFileSelect = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (!allowedFormats.includes(file.type)) {
      Message.warning(`不支持该格式，仅支持 JPG / JPEG / PNG / GIF`);
      return;
    }
    const isLtMax = file.size / 1024 / 1024 < maxSizeMB;
    if (!isLtMax) {
      Message.warning(`文件大小不能超过 ${maxSizeMB}MB`);
      return;
    }

    try {
      const uploadImgUrl = await handleUpload(file);
      if (uploadImgUrl !== '') {
        form.setFieldValue('image', uploadImgUrl);
        setPendingValues(form.getFieldsValue());
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
    if (drawerVisible) {
      document.addEventListener('paste', handlePaste);
      return () => {
        document.removeEventListener('paste', handlePaste);
      };
    }
  }, [drawerVisible, handlePaste]);

  return (
    <>
      <div className={styles.carouselList}>
        <ReactSortable
          list={items.map((item, index) => ({ ...item, id: `item-${index}` }))}
          setList={(newList) => {
            const normalizedList = newList.map(({ id: _id, ...item }) => {
              void _id;
              return item;
            });
            handleSort(normalizedList);
          }}
          handle=".drag-handle"
          animation={200}
        >
          {items.map((item, index) => (
            <div key={index} className={styles.listItem}>
              <div className={`${styles.dragHandle} drag-handle`}>
                <IconDragDotVertical />
              </div>
              <div className={styles.itemContent}>{getItemName(item, index)}</div>
              <div className={styles.itemActions}>
                <IconEdit className={styles.actionIcon} onClick={() => handleEdit(index)} />
                <Popconfirm title="确定要删除这个轮播项吗？" onOk={() => handleDelete(index)}>
                  <IconDelete className={styles.actionIcon} />
                </Popconfirm>
              </div>
            </div>
          ))}
        </ReactSortable>
        <Button type="outline" className={styles.addButton} onClick={handleAdd}>
          <IconPlus />
          添加轮播项
        </Button>
      </div>

      <ConfigDrawer
        visible={drawerVisible}
        title={
          editingIndex !== null && editingIndex < items.length
            ? getItemName(items[editingIndex], editingIndex)
            : '编辑轮播项'
        }
        onClose={handleDrawerClose}
      >
        <Form
          form={form}
          layout="vertical"
          className={attributeStyles.attributes}
          onValuesChange={handleFormValuesChange}
        >
          <FormItem label="图片标题" field="title">
            <Input placeholder="请输入图片标题" />
          </FormItem>
          <FormItem label="轮播图片" field="image">
            <Form.Item noStyle shouldUpdate={(prev, next) => prev.image !== next.image}>
              {() => {
                const imageUrl = form.getFieldValue('image');
                return (
                  <div className={styles.imageUploadContainer}>
                    <div className={styles.imagePreview}>
                      {imageUrl ? (
                        <img src={imageUrl} alt="预览" className={styles.previewImage} />
                      ) : (
                        <div className={styles.uploadPlaceholder}>
                          <IconPlus />
                          <div>上传图片</div>
                        </div>
                      )}
                    </div>
                    <div className={styles.uploadActions}>
                      <Input
                        placeholder="支持直接粘贴上传"
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
                  </div>
                );
              }}
            </Form.Item>
          </FormItem>
          <FormItem label="链接类型" field="linkType">
            <Radio.Group type="button">
              <Radio value="internal">内部页面</Radio>
              <Radio value="external">外部链接</Radio>
            </Radio.Group>
          </FormItem>
          <Form.Item noStyle shouldUpdate={(prev, next) => prev.linkType !== next.linkType}>
            {() => {
              const linkType = form.getFieldValue('linkType');
              return linkType === 'internal' ? (
                <FormItem label="选择页面" field="internalPageId">
                  <Select placeholder="请选择页面" allowClear>
                    {getPageOptions().map((option) => (
                      <Option key={option.value} value={option.value}>
                        {option.label}
                      </Option>
                    ))}
                  </Select>
                </FormItem>
              ) : (
                <FormItem label="链接地址" field="url">
                  <Input placeholder="请输入链接地址" />
                </FormItem>
              );
            }}
          </Form.Item>
        </Form>
      </ConfigDrawer>
    </>
  );
};

export default StaticCarouselList;
