import PlaceholderPanel from '@/components/PlaceholderPanel';
import { TENANT_INFO_PERMISSION as ACTIONS } from '@/constants/permission';
import { hasPermission } from '@/utils/permission';
import {
  Avatar,
  Form,
  Grid,
  Image,
  Input,
  Message,
  Modal,
  Spin,
  Tooltip,
  Typography,
  Upload
} from '@arco-design/web-react';
import { IconCamera, IconEdit } from '@arco-design/web-react/icon';
import { TokenManager } from '@onebase/common';
import type { CorpDetailResponse, DictData } from '@onebase/platform-center';
import { getDetailsApi, getDictDataByType, updateCorpApi, uploadFile } from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

const { Col, Row } = Grid;
const { Text } = Typography;

const SpaceInfo: React.FC = () => {
  const [form] = Form.useForm();

  const [enterpriseInfo, setEnterpriseInfo] = useState<CorpDetailResponse | null>(null);
  const [industryDict, setTndustryDict] = useState<DictData[] | null>(null);
  const [loading, setLoading] = useState(true);
  const [corpLogo, setCorpLogo] = useState<string>();
  const [renameVisible, setRenameVisible] = useState<boolean>(false);

  // 获取用户信息
  const corpInfo = TokenManager.getCorpIdInfo();

  useEffect(() => {
    console.log('corpInfo: ', corpInfo);
    corpInfo?.corpId && fetchEnterpriseInfo(+corpInfo.corpId);
  }, [corpInfo]);

  const fetchEnterpriseInfo = async (id: number) => {
    try {
      setLoading(true);
      const res = await getDetailsApi(id);
      setEnterpriseInfo(res);
      setCorpLogo(res.corpLogo);
      if (res.id) {
        await fetchIndustryDict(res.id);
      }
    } finally {
      setLoading(false);
    }
  };

  const fetchIndustryDict = async (id: string) => {
    try {
      const res = await getDictDataByType(id);
      setTndustryDict(res);
    } catch (error) {
      console.error('字典数据列表错误', error);
    }
  };

  // 重命名
  const handleRenameSubmit = async () => {
    if (enterpriseInfo === null) return;
    const { newName } = await form.validate();

    if (!newName.trim()) {
      Message.error('空间名称不能为空');
      return;
    }

    try {
      await updateCorpApi({
        id: enterpriseInfo.id!,
        corpName: newName,
        corpCode: enterpriseInfo.corpCode,
        industryType: +enterpriseInfo.industryType!,
        status: enterpriseInfo.status,
        address: enterpriseInfo.address!,
        userLimit: enterpriseInfo.userLimit!
      });

      setEnterpriseInfo({
        ...enterpriseInfo,
        corpName: newName
      });

      setRenameVisible(false);
      form.resetFields();
      Message.success('空间名称更新成功');
    } catch (error) {
      console.error('更新空间信息失败', error);
    }
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

  // 显示加载状态
  if (loading) {
    return (
      <div className={styles.enterprisePage}>
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '300px' }}>
          <Spin tip="加载中..." />
        </div>
      </div>
    );
  }

  // 数据加载完成后但没有租户信息
  if (!enterpriseInfo) {
    return (
      <div className={styles.enterprisePage}>
        <div style={{ textAlign: 'center', padding: '40px' }}>
          <p>无法加载公司信息</p>
        </div>
      </div>
    );
  }

  const getIndustryTypeName = industryDict?.find((data) => data.id === enterpriseInfo.industryType)?.label || '-';

  return (
    <PlaceholderPanel hasPermission={hasPermission(ACTIONS.QUERY)} isLoading={loading}>
      <div className={styles.enterprisePage}>
        <div className={styles.enterprisePageMain}>
          <div className={`${styles.infoCard} ${styles.infoCardPrimary}`}>
            <div className={styles.blockHeader}>基本信息</div>

            <div className={styles.baseInfo}>
              <div className={styles.infoCardPrimaryLeft}>
                <div className={styles.avatarSection}>
                  <Tooltip content="修改Logo">
                    {corpLogo ? (
                      <Upload
                        limit={1}
                        accept="image/*"
                        listType="picture-card"
                        customRequest={async (option) => {
                          const { onProgress, onError, onSuccess, file } = option;
                          try {
                            const uploadImgUrl = await handleUpload(file, onProgress);
                            if (uploadImgUrl !== '') {
                              setCorpLogo(uploadImgUrl);
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
                      >
                        <Image
                          className={styles.reUploadLogo}
                          src={corpLogo}
                          width={160}
                          height={80}
                          preview={false}
                          actions={[<IconCamera />]}
                        />
                      </Upload>
                    ) : (
                      <Avatar
                        shape="square"
                        style={{ width: 160, height: 80, backgroundColor: '#F7F8FA', borderRadius: 12 }}
                      >
                        <span className={styles.avatarText}>{enterpriseInfo.corpName?.slice(0, 6)}</span>
                      </Avatar>
                    )}
                  </Tooltip>
                </div>
                {/* 名称 & ID */}
                <div className={styles.section}>
                  <div className={styles.enterpriseName}>
                    {enterpriseInfo.corpName}{' '}
                    {hasPermission(ACTIONS.UPDATE) && (
                      <IconEdit onClick={() => setRenameVisible(true)} style={{ cursor: 'pointer' }} />
                    )}
                  </div>
                  <div className={styles.enterpriseId}>
                    企业ID：<Text copyable>{enterpriseInfo.id}</Text>
                  </div>
                </div>
              </div>

              {/* 统计信息 */}
              <div className={styles.statsSection}>
                <div className={styles.statCard}>
                  <div className={styles.statLabel}>用户人数(个)</div>
                  <div className={styles.statValue}>{enterpriseInfo.userCount || 0}</div>
                </div>
                <div className={styles.statCard}>
                  <div className={styles.statLabel}>应用数量(个)</div>
                  <div className={styles.statValue}>{enterpriseInfo.appCount || 0}</div>
                </div>
              </div>
            </div>
          </div>

          <div className={styles.infoCard}>
            {/* 访问地址 */}
            <div className={styles.blockHeader}>更多信息</div>

            <div className={styles.bottomSection}>
              <Row gutter={24} style={{ marginBottom: 28 }}>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>企业联系人</span>
                    <span>{enterpriseInfo.adminName || '-'}</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>联系人手机号</span>
                    <span>{enterpriseInfo.mobile || '-'}</span>
                  </div>
                </Col>
              </Row>

              <Row gutter={24} style={{ marginBottom: 28 }}>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>联系人邮箱</span>
                    <span>{enterpriseInfo.email || '-'}</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>用户上限</span>
                    <span>{enterpriseInfo.userLimit || 0}</span>
                  </div>
                </Col>
              </Row>

              <Row gutter={24} style={{ marginBottom: 28 }}>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>行业类型</span>
                    <span>{getIndustryTypeName}</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>企业地址</span>
                    <span>{enterpriseInfo.address || '-'}</span>
                  </div>
                </Col>
              </Row>
            </div>
          </div>
        </div>
      </div>

      {/* 修改企业名称 */}
      <Modal
        title="修改企业名称"
        visible={renameVisible}
        onOk={handleRenameSubmit}
        onCancel={() => setRenameVisible(false)}
      >
        <Form form={form} layout="vertical">
          <Form.Item label="新的企业名称" field="newName" rules={[{ required: true, message: '请输入新的企业名称' }]}>
            <Input placeholder="请输入新的企业名称" />
          </Form.Item>
        </Form>
      </Modal>
    </PlaceholderPanel>
  );
};

export default SpaceInfo;
