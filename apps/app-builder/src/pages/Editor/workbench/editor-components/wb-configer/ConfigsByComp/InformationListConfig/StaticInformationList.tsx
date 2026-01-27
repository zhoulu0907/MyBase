import { Button, Form, Input, Message, Popconfirm, Radio, DatePicker } from '@arco-design/web-react';
import { IconCloud, IconDelete, IconDragDotVertical, IconEdit, IconPlus } from '@arco-design/web-react/icon';
import { uploadFile, getFileUrlById } from '@onebase/platform-center';
import { useState, useEffect, useRef, useCallback } from 'react';
import { ReactSortable } from 'react-sortablejs';
import ConfigDrawer from '@/pages/Editor/workbench/components/configDrawer';
import type { InformationListItem, StaticInformationListProps } from './types';
import MenuSelector from '@/pages/Editor/workbench/components/MenuSelector';
import { getNextIndex } from '@/pages/Editor/workbench/utils/edit-data';
import styles from './StaticInformationList.module.less';
import attributeStyles from '../../components/CommonWorkbenchAttributes/attributes.module.less';

const FormItem = Form.Item;

// 允许的文件格式列表
const allowedFormats = ['image/jpeg', 'image/png', 'image/gif'];

// 生成唯一ID
const generateId = () => `information-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;

const StaticInformationList = ({
  staticInformationList,
  maxSizeMB = 5,
  maxCount = 10,
  onConfigChange
}: StaticInformationListProps) => {
  console.log('staticInformationList', staticInformationList);
  const [items, setItems] = useState<InformationListItem[]>(() =>
    (staticInformationList || []).map((item) => ({
      ...item,
      id: item.id || generateId()
    }))
  );
  const [editingId, setEditingId] = useState<string | null>(null);
  const [drawerVisible, setDrawerVisible] = useState(false);
  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);
  const [form] = Form.useForm();

  useEffect(() => {
    setItems(
      (staticInformationList || []).map((item) => ({
        ...item,
        id: item.id || generateId()
      }))
    );
  }, [staticInformationList]);

  const fileInputRef = useRef<HTMLInputElement>(null);

  const getItemName = (item?: InformationListItem) => {
    if (!item) return '编辑资讯项';
    if (item.title) {
      return item.title;
    }
    return '资讯项';
  };

  const handleSort = (newList: InformationListItem[]) => {
    setItems(newList);
    onConfigChange(newList);
  };

  const handleEdit = (id: string) => {
    const item = items.find((item) => item.id === id);
    if (!item) return;

    form.setFieldsValue({
      title: item.title || '',
      subtitle: item.subtitle || '',
      author: item.author || '',
      date: item.date || '',
      image: item.image || '',
      linkType: item.linkType || 'internal',
      internalPageId: item.internalPageId || '',
      url: item.url || ''
    });
    setSelectedKeys(item.internalPageId ? [item.internalPageId] : []);
    setEditingId(id);
    setDrawerVisible(true);
  };

  const handleDelete = (id: string) => {
    const newItems = items.filter((item) => item.id !== id);
    setItems(newItems);
    onConfigChange(newItems);
    Message.success('删除成功');
  };

  const handleAdd = () => {
    if (typeof maxCount === 'number' && items.length >= maxCount) {
      Message.warning(`最多只能添加 ${maxCount} 条`);
      return;
    }

    const nextIndex = getNextIndex(items, 'title', '资讯标题');

    const newItem: InformationListItem = {
      id: generateId(),
      title: `资讯标题${nextIndex}`,
      subtitle: '',
      author: '',
      date: '',
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
    setEditingId(null);
    form.resetFields();
  };

  const [pendingValues, setPendingValues] = useState<InformationListItem | null>(null);

  useEffect(() => {
    if (editingId === null || !pendingValues) {
      return;
    }
    setItems((prevItems) => {
      const newItems = prevItems.map((item) => (item.id === editingId ? { ...item, ...pendingValues } : item));
      onConfigChange(newItems);
      return newItems;
    });
    setPendingValues(null);
  }, [editingId, onConfigChange, pendingValues]);

  const handleFormValuesChange = useCallback((_: Record<string, unknown>, allValues: InformationListItem) => {
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
              const uploadImgId = await handleUpload(file);
              if (uploadImgId !== '') {
                const urlImg = getFileUrlById(uploadImgId);
                form.setFieldValue('image', urlImg);
                setPendingValues(form.getFieldsValue() as InformationListItem);
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
      const uploadImgId = await handleUpload(file);
      if (uploadImgId !== '') {
        const urlImg = getFileUrlById(uploadImgId);
        form.setFieldValue('image', urlImg);
        setPendingValues(form.getFieldsValue() as InformationListItem);
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

  const handleMenuChange = (value: string | string[]) => {
    const nextKeys = Array.isArray(value) ? value : [value];
    setSelectedKeys(nextKeys);
    form.setFieldValue('internalPageId', nextKeys[0] || '');
    setPendingValues(form.getFieldsValue() as InformationListItem);
  };

  return (
    <>
      <div className={styles.carouselList}>
        <ReactSortable
          list={items}
          setList={(newList) => {
            handleSort(newList);
          }}
          handle=".drag-handle"
          animation={200}
        >
          {items.map((item) => (
            <div key={item.id} className={styles.listItem}>
              <div className={`${styles.dragHandle} drag-handle`}>
                <IconDragDotVertical />
              </div>
              <div className={styles.itemContent}>{getItemName(item)}</div>
              <div className={styles.itemActions}>
                <IconEdit className={styles.actionIcon} onClick={() => handleEdit(item.id)} />
                <Popconfirm title="确定要删除这个咨询项吗？" onOk={() => handleDelete(item.id)}>
                  <IconDelete className={styles.actionIcon} />
                </Popconfirm>
              </div>
            </div>
          ))}
        </ReactSortable>
        <Button type="outline" className={styles.addButton} onClick={handleAdd}>
          <IconPlus />
          添加资讯项
        </Button>
      </div>

      <ConfigDrawer
        visible={drawerVisible}
        title={editingId !== null ? getItemName(items.find((item) => item.id === editingId)) : '编辑资讯项'}
        onClose={handleDrawerClose}
      >
        <Form
          form={form}
          layout="vertical"
          className={attributeStyles.attributes}
          onValuesChange={handleFormValuesChange}
        >
          <FormItem label="主标题" field="title">
            <Input placeholder="请输入主标题" />
          </FormItem>
          <FormItem label="副标题" field="subtitle">
            <Input placeholder="请输入副标题" />
          </FormItem>
          <FormItem label="作者" field="author">
            <Input placeholder="请输入作者" />
          </FormItem>
          <FormItem label="日期" field="date">
            <DatePicker format="YYYY-MM-DD" placeholder="请选择日期" style={{ width: '100%' }}/>
          </FormItem>
          <FormItem label="封面图片" field="image">
            <Form.Item noStyle shouldUpdate={(prev, next) => prev.image !== next.image}>
              {() => {
                const imageUrl = form.getFieldValue('image');
                return (
                  <div className={styles.imageUploadContainer}>
                    <div className={styles.uploadActions}>
                      <Input
                        placeholder="支持直接粘贴上传"
                        value={imageUrl}
                        readOnly
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
                  <MenuSelector mode="single" value={selectedKeys} onChange={handleMenuChange} />
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

export default StaticInformationList;
