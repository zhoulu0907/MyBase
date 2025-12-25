import { memo, useEffect, useRef } from 'react';
import { nanoid } from 'nanoid';
import { Toast, ImagePicker, Form, Ellipsis, ImagePreview } from '@arco-design/mobile-react';
import { attachmentDownload, attachmentUpload, menuSignal } from '@onebase/app';
import { pagesRuntimeSignal } from '@onebase/common';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, FormSchema } from '@onebase/ui-kit';
import './index.css';

type XImgUploadConfig = typeof FormSchema.XImgUploadSchema.config;


const XImgUpload = memo((props: XImgUploadConfig & { runtime?: boolean; detailMode?: boolean; form?: any }) => {
  const {
    label,
    dataField,
    status,
    verify,
    runtime = true,
    form,
    detailMode,
  } = props;
  const { curMenu } = menuSignal;
  const { rowDataId } = pagesRuntimeSignal;

  const fieldId =
    dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.IMG_UPLOAD}_${nanoid()}`;

  const [tableName, fieldName] = dataField;
  const localUrls = useRef<string[]>([]);

  const getRealUrl = async (fileId: string) => {
    try {
      const lastIndexOf = fieldName.lastIndexOf('.');
      const curFieldName = lastIndexOf === -1 ? fieldName : fieldName.slice(lastIndexOf + 1);
      const param = {
        menuId: curMenu.value.id,
        id: rowDataId.value,
        fieldName: curFieldName,
        fileId
      };

      const url = await attachmentDownload(tableName, param);
      return url;
    } catch (error) {
      return '';
    }
  };

  const initUlr = async () => {
    if (!form || !fieldId) return;
    
    const array = form.getFieldValue(fieldId) || [];
    const urls = [] as string[];
    
    const updatedArray = await Promise.all(
      array.map(async (item: any) => {
        if (item.id && !item.url) {
          try {
            const resTmp = await getRealUrl(item.id);
            urls.push(resTmp);
            return { ...item, url: resTmp, status: 'loaded' };
          } catch (error) {
            return { ...item, status: 'error' };
          }
        }
        return item;
      })
    );
    localUrls.current = urls;
    if (updatedArray.length > 0) {
      form.setFieldValue(fieldId, updatedArray);
    }
  };

  useEffect(() => {
    initUlr();
  }, [fieldId]);

  useEffect(() => {
    return () => {
      localUrls.current.forEach((url) => {
        if (url && url.startsWith('blob:')) {
          URL.revokeObjectURL(url);
        }
      });
    };
  }, []);

  const handleUpload = async ({ file }: { file: File }) => {
    try {
      const formData = new FormData();
      formData.append('file', file);

      if (runtime) {
        const res = await attachmentUpload(tableName, formData);
        return {
          name: file.name,
          response: res
        };
      } else {
        return '';
      }
    } catch (error) {
    }
  };
  const onClick = (e: React.MouseEvent, image: any, index: number) => {
    window.modalInstance = ImagePreview.open({
      showLoading: true,
      openIndex: index,
      loop: true,
      onImageDoubleClick: index => console.log('dbl click', index),
      onImageLongTap: (index, image) => console.log('long tap', index, image),
      images: (form.getFieldValue(fieldId) || []).map((item: any) => ({ src: item.url })),
    });
  }
  return (
    <Form.Item
      className="inputTextWrapperOBMobile ImgUploadWrapperOBMobile"
      label={
        label.display && <Ellipsis text={label.text} maxLine={2} />
      }
      layout="vertical"
      field={fieldId}
      required={verify?.required}
      trigger="fileList"
      style={{
        pointerEvents: runtime ? 'unset' : 'none',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      <ImagePicker
        accept="image/*"
        limit={verify?.maxCount === -1 ? undefined : verify?.maxCount}
        maxSize={verify.maxSize * 1024}
        onClick={onClick}
        upload={handleUpload}
        onMaxSizeExceed={(file) => {
          Toast.toast({
            content: '文件大小超出限制',
            duration: 2000
          });
        }}
        onLimitExceed={(file) =>
          Toast.toast({
            content: '文件数量超出限制',
            duration: 2000
          })
        }
        style={{
          width: '100%'
        }}
      />
    </Form.Item>
  );
});

export default XImgUpload;
