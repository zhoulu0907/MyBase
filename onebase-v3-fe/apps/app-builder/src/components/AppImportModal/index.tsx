import { Alert, Button, Input, Message, Modal, Progress, Radio, Tag, Upload } from '@arco-design/web-react';
import { IconCheckCircleFill, IconEdit, IconRefresh, IconUpload } from '@arco-design/web-react/icon';
import { ExportStatus, importAppVersion, type Application } from '@onebase/app';
import { useState } from 'react';
import styles from './index.module.less';

interface AppImportModalProps {
  // 控制弹窗是否显示
  visible: boolean;
  // 关闭弹窗的回调
  onClose: () => void;
  onComplete: () => void;
  // 应用信息
  appInfo?: Application;
}

// 应用导入/导入更新更新弹窗
const AppImportModal: React.FC<AppImportModalProps> = ({ visible, onClose, onComplete, appInfo }) => {
  const [currentStep, setCurrentStep] = useState(1);
  const [appStatus, setAppStatus] = useState('');
  const [hasFile, setHasFile] = useState(false);
  const [appCode, setAppCode] = useState('');
  // 编码重复处理类型
  const [updateType, setUpdateType] = useState('');
  const stepList = ['上传文件', '导入范围', '应用安装'];
  const [importRange, setImportRange] = useState<string[]>([]);

  const [progressPercent, setProgressPercent] = useState(0);
  const [installStatus, setInstallStatus] = useState<ExportStatus>(ExportStatus.EXPORTING);

  // 下一步
  const handleNext = () => {
    if (currentStep === 1) {
      if (!hasFile) {
        Message.warning('请先上传文件');
        return;
      }
      setCurrentStep(currentStep + 1);
    }
    if (currentStep === 2) {
      setCurrentStep(currentStep + 1);
      setProgressPercent(100);
      setInstallStatus(ExportStatus.SUCCESS);
    }
  };

  // 完成
  const handleComplete = () => {
    Message.success('导入完成');
    if (onComplete) {
      onComplete();
    }
    onClose();
    setCurrentStep(1);
  };

  // 文件上传
  const handleUpload = async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    if (appInfo?.id) {
      formData.append('applicationId', appInfo?.id);
    }
    const res = await importAppVersion(formData);
    return res;
  };

  return (
    <Modal
      visible={visible}
      title="应用导入"
      unmountOnExit={true}
      maskClosable={false}
      className={styles.appImportModal}
      onCancel={() => onClose()}
      footer={
        <div>
          <Button
            style={{ marginRight: 12 }}
            onClick={() => {
              onClose();
              setCurrentStep(1);
            }}
          >
            取消
          </Button>
          <Button type="primary" onClick={handleComplete} disabled={!hasFile}>
            完成
          </Button>
          {/* {currentStep < stepList.length && (
            <Button type="primary" onClick={handleNext}>
              下一步
            </Button>
          )}
          {currentStep === stepList.length && (
            <Button type="primary" onClick={handleComplete} disabled={progressPercent !== 100}>
              完成
            </Button>
          )} */}
        </div>
      }
    >
      <div>
        {/* <Steps current={currentStep}>
          {stepList.map((item, index) => (
            <Steps.Step key={index} title={item} />
          ))}
        </Steps> */}
        <div className={styles.stepContent}>
          {currentStep === 1 && (
            <div>
              <Upload
                tip="点击上传zip格式的应用文件"
                limit={1}
                showUploadList={{
                  reuploadIcon: (
                    <Button size="mini" type="text" icon={<IconRefresh />}>
                      点击重试
                    </Button>
                  ),
                  successIcon: <IconCheckCircleFill color="#4FAE7B" />
                }}
                beforeUpload={async (file: any) => {
                  if (!['application/x-zip-compressed', 'application/zip'].includes(file.type)) {
                    Message.warning(`不支持该格式，仅支持 zip`);
                    return false;
                  }
                }}
                customRequest={async (option) => {
                  const { onProgress, onError, onSuccess, file } = option;
                  try {
                    const flag = await handleUpload(file);
                    setHasFile(flag);
                    const url = URL.createObjectURL(file);
                    // 上传成功
                    if (flag && url) {
                      onProgress(100);
                      onSuccess({ url });
                      Message.success('上传成功');
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
                    console.log(error);
                  }
                }}
                onRemove={() => {
                  setHasFile(false);
                }}
              >
                <Button type="primary" icon={<IconUpload />}>
                  点击上传
                </Button>
              </Upload>
              {appStatus && (
                <Alert
                  type="warning"
                  className={styles.repeatWarn}
                  content={
                    <div>
                      <div style={{ marginBottom: '8px' }}>{`检测到当前环境已存在编码为 ${appStatus} 的应用`}</div>
                      <Radio.Group value={updateType} onChange={(value) => setUpdateType(value)}>
                        <Radio value={'update'}>增量更新</Radio>
                        <Radio value={'add'}>新增应用</Radio>
                      </Radio.Group>
                      {updateType === 'add' && (
                        <span>
                          为避免冲突，请修改新应用的编码：
                          <Input
                            style={{ display: 'inline-block', width: '170px' }}
                            size="mini"
                            value={appCode}
                            onChange={(value) => setAppCode(value)}
                            placeholder="字母、数字、下划线组合，字母开头，不超过40字符"
                            suffix={<IconEdit />}
                            normalize={(value) => {
                              const matchResult = value.match(/[a-zA-Z_]/);
                              if (!matchResult) {
                                return '';
                              }
                              const index = matchResult.index;
                              const newValue = value.slice(index);
                              return newValue.replaceAll(/[^a-zA-Z_0-9]/g, '');
                            }}
                          />
                        </span>
                      )}
                    </div>
                  }
                ></Alert>
              )}
            </div>
          )}

          {currentStep === 2 && (
            <div>
              <div style={{ marginBottom: '8px' }}>应用创建者</div>
              <Tag className={styles.tag}>{appInfo?.createUser}</Tag>
              {/* <div style={{ margin: '8px 0' }}>导入范围</div>
              <div>
                {importRange.map((item, index) => (
                  <Tag key={`tag-${index}`} className={styles.tag}>
                    {item}
                  </Tag>
                ))}
              </div>
              <div className={styles.tips}>
                注：组织、角色和用户信息将以增量方式导入至应用所在空间，不会覆盖已有数据
              </div> */}
            </div>
          )}

          {currentStep === 3 && (
            <div className={styles.progressContent}>
              <Progress
                percent={progressPercent}
                color={installStatus === ExportStatus.ERROR ? '#FF7D00' : 'rgb(var(--primary-6))'}
                size="large"
                showText={false}
              />
              {installStatus === ExportStatus.EXPORTING && (
                <div className={styles.progressTips}>{`正在安装应用，进度${progressPercent}%`}</div>
              )}
              {installStatus === ExportStatus.SUCCESS && <div className={styles.progressTips}>安装成功</div>}
              {installStatus === ExportStatus.ERROR && (
                <div className={styles.progressTips}>
                  <span>安装失败</span>
                  <Button type="text" status="danger" icon={<IconRefresh />}>
                    重试
                  </Button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </Modal>
  );
};

export default AppImportModal;
