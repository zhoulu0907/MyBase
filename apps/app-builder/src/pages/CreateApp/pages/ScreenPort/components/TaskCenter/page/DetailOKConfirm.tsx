import { useState, type FC, forwardRef, useImperativeHandle } from 'react';
import { Form, Input, Upload, Message } from '@arco-design/web-react';
import { IconFileImage, IconAttachment } from '@arco-design/web-react/icon';
import { uploadFile } from '@onebase/platform-center';
import '../style/detailOkConfirm.less'

const FormItem = Form.Item;

const maxImgSizeMB = 20
const maxFileSizeMB = 50

const DetailOKConfirm:FC = forwardRef((props: any, ref: any) => {
    const [form] = Form.useForm();
    const [imgUpList, setImgUpList] = useState<any>()

    useImperativeHandle(ref, () => ({
        childMethod: () => {
            console.log('子组件方法被调用', form, form.getFields());
            // 子组件逻辑
            form.setFieldValue('name', 12312313)
        }
    }));
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

    function handleTestClick() {
        form.setFieldValue('name', '12222')
        form.setFieldValue(
            'name2', [{
                uid: '1',
                name: '20200717-103937.png',
                url: 'http://10.0.104.38:9000/onebase/20251014/test - 副本_1760424759764.gif',
                response: "http://10.0.104.38:9000/onebase/20251014/test - 副本_1760424759764.gif",
                status: 'done',
                percent: 100,
            }]
        )
        form.setFieldValue(
            'name3', [{
                uid: '2',
                name: '123.docx',
                url: 'http://10.0.104.38:9000/onebase/20251014/123.docx',
                response: "http://10.0.104.38:9000/onebase/20251014/123.docx"
            }]
        )
        setImgUpList([{
            uid: '1',
            name: '20200717-103937.png',
            url: 'http://10.0.104.38:9000/onebase/20251014/test - 副本_1760424759764.gif'
        }])
        console.log(form.getFieldsValue(), imgUpList)
    }
    return <section className='detail-confirm-page'>
        <Form form={form} layout="vertical">
            <div className='form-item-title'>审批意见</div>
            <FormItem field='name' rules={[{ required: true, message: '审批意见必填' }]}>
                <Input.TextArea
                    maxLength={500}
                    showWordLimit
                    placeholder='请输入审批意见'
                    wrapperStyle={{ width: '100%' }}
                />
            </FormItem>
            <div className='form-item-title'>
                <span><IconFileImage />图片</span>
            </div>
            <FormItem field='name2' rules={[{ required: true, message: '审批意见必填' }]}>
                <Upload
                    limit={9}
                    listType="picture-card"
                    accept=".png, .jpg, .jpeg, .gif"
                    showUploadList
                    beforeUpload={async (file:any) => {
                        // if (!['image/jpeg', 'image/png', 'image/gif'].includes(file.type)) {
                        //     Message.warning(`不支持该格式，仅支持 JPG / JPEG / PNG / GIF`);
                        //     return false;
                        // }
                        // 校验大小
                        const isLtMax = file.size / 1024 / 1024 < maxImgSizeMB;
                        if (!isLtMax) {
                            Message.warning(`图片大小不能超过 ${maxImgSizeMB}MB`);
                            return false;
                        }
                    }}
                    customRequest={async (option) => {
                        const { onProgress, onError, onSuccess, file } = option;
                        try {
                            const uploadImgUrl = await handleUpload(file, onProgress);
                            if (uploadImgUrl !== '') {
                                const newImageInfo = {
                                    image: uploadImgUrl,
                                    tetx: '',
                                    url: ''
                                };
                                // setCarouselConfig((prev) => [...prev, newImageInfo]);
                                // handlePropsChange(carouselKey, [...carouselConfig, newImageInfo]);
                                onSuccess(uploadImgUrl);
                            } else {
                                onError({
                                    status: 'error',
                                    msg: '上传失败'
                                });
                            }
                        } catch (error) {
                            onError({
                                status: 'error',
                                msg: '上传失败'
                            });
                        }
                    }}
                ></Upload>
            </FormItem>
            <div className='form-item-title'>
                <span><IconAttachment />附件</span>
            </div>
            <FormItem field='name3' rules={[{ required: true, message: '审批意见必填' }]}>
                <Upload
                    limit={6}
                    listType="text"
                    accept=".pdf, .xls, .xlsx, .pptx, .ppt, .doc, .docx"
                    showUploadList
                    beforeUpload={async (file:any) => {
                        // 校验大小
                        const isLtMax = file.size / 1024 / 1024 < maxFileSizeMB;
                        if (!isLtMax) {
                            Message.warning(`文件大小不能超过 ${maxFileSizeMB}MB`);
                            return false;
                        }
                    }}
                    customRequest={async (option) => {
                        const { onProgress, onError, onSuccess, file } = option;
                        try {
                            const uploadImgUrl = await handleUpload(file, onProgress);
                            if (uploadImgUrl !== '') {
                                const newImageInfo = {
                                    image: uploadImgUrl,
                                    tetx: '',
                                    url: ''
                                };
                                // setCarouselConfig((prev) => [...prev, newImageInfo]);
                                // handlePropsChange(carouselKey, [...carouselConfig, newImageInfo]);
                                onSuccess(uploadImgUrl);
                            } else {
                                onError({
                                    status: 'error',
                                    msg: '上传失败'
                                });
                            }
                        } catch (error) {
                            onError({
                                status: 'error',
                                msg: '上传失败'
                            });
                        }
                    }}
                    style={{
                        width: '100%',
                        height: '100%',
                        pointerEvents: 'auto'
                    }}
                />
            </FormItem>
        </Form>
        <div onClick={handleTestClick}>sdfsdfsfdsdf</div>
    </section>
})

export default DetailOKConfirm;