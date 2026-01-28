import { Button, Form, Checkbox, Radio, Modal, Typography, Progress } from '@arco-design/web-react';
import { IconUpload, IconLoading, IconRefresh, IconCheckCircleFill, IconEdit } from '@arco-design/web-react/icon';
import {
  VersionExportType,
  ExportStatus,
  exportAppVersion,
  exportAppVersionFile,
  getExportAppVersionStatus,
  type Application
} from '@onebase/app';
import { appIconMap, downloadFileByUrl } from '@onebase/ui-kit';
import DynamicIcon from '@/components/DynamicIcon';
import { useState } from 'react';
import styles from './index.module.less';

interface AppExportModalProps {
  // 控制弹窗是否显示
  visible: boolean;
  // 关闭弹窗的回调
  onClose: () => void;
  // 应用信息
  appInfo: Application;
  versionInfo?: any;
}

// 应用导出弹窗
const AppExportModal: React.FC<AppExportModalProps> = ({ visible, onClose, appInfo, versionInfo }) => {
  const [exportValue, setExportValue] = useState<string[]>([]);
  const [version, setVersion] = useState(0);
  const [exportIsError, setExportIsError] = useState(false);
  const exportRange = [
    { label: '应用数据', value: 'data' },
    { label: '组织信息', value: 'organize' },
    { label: '角色信息', value: 'role' },
    { label: '用户信息', value: 'user' }
  ];
  const versionRange = [
    { label: '开发环境', value: VersionExportType.DEV },
    { label: '正式环境', value: VersionExportType.PROD }
  ];

  const [exportLoading, setExportLoading] = useState(false);
  const [downloadLoading, setDownloadLoading] = useState(false);
  const [exportId, setExportId] = useState('');

  const handleExport = async () => {
    setExportLoading(true);
    const param = {
      versionId: versionInfo?.id || version
    };
    const newExportId = await exportAppVersion(param);
    setExportId(newExportId);
    if (newExportId) {
      onClose();
      setProgressVisible(true);
      handleProgress(newExportId);
    }
    setExportLoading(false);
  };

  const [progressVisible, setProgressVisible] = useState(false);
  const [percentValue, setPercentValue] = useState(0);

  // 处理进度
  const handleProgress = async (newExportId: string) => {
    const res = await getExportAppVersionStatus({ exportId: newExportId });
    if (res === ExportStatus.EXPORTING) {
      if (percentValue < 80) {
        setPercentValue((prev) => prev + 20);
      } else {
        setPercentValue(99);
      }
      handleProgress(newExportId);
    } else if (res === ExportStatus.SUCCESS) {
      setPercentValue(100);
    } else {
      setExportIsError(true);
    }
  };
  // 重新导出
  const retryExport = (newExportId: string) => {
    setPercentValue(0);
    handleProgress(newExportId);
  };
  // 下载文件
  const handleDownload = async () => {
    setDownloadLoading(true);
    const fileUrl = await exportAppVersionFile({ exportId }, appInfo.appName);
    if (fileUrl) {
      downloadFileByUrl(fileUrl, `${appInfo.appName}.zip`);
      setProgressVisible(false);
    }
    setDownloadLoading(false);
  };

  return (
    <>
      <Modal
        visible={visible}
        title="应用导出"
        unmountOnExit={true}
        maskClosable={false}
        className={styles.appExportModal}
        onCancel={() => onClose()}
        footer={
          <div>
            <Button style={{ marginRight: 12 }} onClick={() => onClose()}>
              取消
            </Button>
            <Button type="primary" onClick={handleExport} loading={exportLoading}>
              导出
            </Button>
          </div>
        }
      >
        <div className={styles.appExportModalContent}>
          <div className={styles.appInfo}>
            <div className={styles.appIcon} style={{ backgroundColor: appInfo?.iconColor }}>
              <DynamicIcon
                IconComponent={appIconMap[appInfo?.iconName as keyof typeof appIconMap]}
                theme="outline"
                size="52"
                fill="#F2F3F5"
              />
            </div>
            <div className={styles.appContent}>
              <div className={styles.appName}>
                <Typography.Paragraph className={styles.appNameText} ellipsis={{ showTooltip: true, wrapper: 'span' }}>
                  {appInfo.appName}
                </Typography.Paragraph>
                <Typography.Paragraph className={styles.appCode} ellipsis={{ showTooltip: true, wrapper: 'span' }}>
                  {appInfo.appCode}
                </Typography.Paragraph>
              </div>
              <Typography.Paragraph
                className={styles.appDesc}
                ellipsis={{ rows: 2, showTooltip: true, wrapper: 'span' }}
              >
                {appInfo.description ?? '该应用暂无介绍。'}
              </Typography.Paragraph>
            </div>
          </div>
          {!versionInfo && (
            <div className={styles.exportRange}>
              <span>选择版本：</span>
              <Radio.Group value={version} options={versionRange} onChange={(value) => setVersion(value)}></Radio.Group>
            </div>
          )}

          {/* <div className={styles.exportRange}>
            <span>导出范围：</span>
            <Checkbox.Group
              value={exportValue}
              options={exportRange}
              onChange={(value) => setExportValue(value)}
            ></Checkbox.Group>
          </div>
          <div className={styles.tips}>注：导出文件不包含外部系统的认证凭证（如API Key、密码）</div> */}
        </div>
      </Modal>

      <Modal
        visible={progressVisible}
        unmountOnExit={true}
        maskClosable={false}
        className={styles.exportProgressModal}
        onCancel={() => setProgressVisible(false)}
        footer={
          <div>
            <Button
              style={{ marginRight: 12 }}
              onClick={() => {
                setProgressVisible(false);
              }}
            >
              取消
            </Button>
            {percentValue === 100 && (
              <Button type="primary" onClick={handleDownload} loading={downloadLoading}>
                下载
              </Button>
            )}
          </div>
        }
      >
        <div className={styles.progressModalContent}>
          <div className={styles.progressTitle}>应用导出</div>
          <Progress
            percent={percentValue}
            color={exportIsError ? '#FF7D00' : 'rgb(var(--primary-6))'}
            showText={false}
            size="large"
            animation={true}
          />
          <div className={styles.progressTips}>
            {exportIsError ? (
              <>
                <span>安装失败</span>
                <Button type="text" status="danger" icon={<IconRefresh />} onClick={() => retryExport(exportId)}>
                  重试
                </Button>
              </>
            ) : (
              <>
                {percentValue < 100 && `正在导出应用，进度${percentValue}%`}
                {percentValue === 100 && `导出完成`}
              </>
            )}
          </div>
        </div>
      </Modal>
    </>
  );
};

export default AppExportModal;
