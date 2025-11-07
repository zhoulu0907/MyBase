import React, { useEffect, useState } from 'react';
import { Avatar, Spin, Typography, Message, Grid, Tooltip, Upload, Image, Form, Modal, Input } from '@arco-design/web-react';
import type { PlatformTenantInfo } from '@onebase/platform-center';
import { getDetailsApi, uploadFile } from '@onebase/platform-center';
import PlaceholderPanel from '@/components/PlaceholderPanel';
import { hasPermission, /* UserPermissionManager */ } from '@/utils/permission';
import { TENANT_INFO_PERMISSION as ACTIONS } from '@/constants/permission';
import styles from './index.module.less';
import { IconCamera, IconEdit } from '@arco-design/web-react/icon';
// import { TokenManager } from '@onebase/common';

const { Col, Row } = Grid;
const { Text } = Typography;

const SpaceInfo: React.FC = () => {
  const [form] = Form.useForm();

  const [tenantInfo, setTenantInfo] = useState<PlatformTenantInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [logoUrl, setLogoUrl] = useState<string>();
  const [renameVisible, setRenameVisible] = useState<boolean>(false);

  // 获取用户信息
  // const tokenInfo = TokenManager.getTokenInfo();
  // const userPermissionInfo = UserPermissionManager.getUserPermissionInfo();

  // console.log(tokenInfo, userPermissionInfo, 'userPermissionInfo')

  const fetchTenantInfo = async (id: string) => {
    try {
      setLoading(true);
      const res = await getDetailsApi(id);
      setTenantInfo(res);
      // setLogoUrl(res.logoUrl);
      setLogoUrl('http://wiki.virtueit.net/images/logo/default-space-logo.svg');
      // setTenantName(res.name);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTenantInfo('114914409919610880');
  }, []);


  // 重命名
  const handleRenameSubmit = async () => {
    if (!tenantInfo) return;
    const { newName } = await form.validate();

    if (!newName.trim()) {
      Message.error('空间名称不能为空');
      return;
    }

    try {
      // await updatePlatformTenantApi({
      //   id: tenantInfo.id,
      //   name: newName,
      //   tenantAdminUserUpdateReqVOSList: tenantInfo.tenantAdminUserList
      // })

      setTenantInfo({
        ...tenantInfo,
        name: newName
      });

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
      <div className={styles.tenantPage}>
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '300px' }}>
          <Spin tip="加载中..." />
        </div>
      </div>
    );
  }

  // 数据加载完成后但没有租户信息
  if (!tenantInfo) {
    return (
      <div className={styles.tenantPage}>
        <div style={{ textAlign: 'center', padding: '40px' }}>
          <p>无法加载租户信息</p>
        </div>
      </div>
    );
  }

  return (
    <PlaceholderPanel hasPermission={hasPermission(ACTIONS.QUERY)} isLoading={loading}>
      <div className={styles.tenantPage}>
        <div className={styles.tenantPageMain}>

          <div className={`${styles.infoCard} ${styles.infoCardPrimary}`}>
            <div className={styles.blockHeader}>基本信息</div>

            <div className={styles.baseInfo}>
              <div className={styles.infoCardPrimaryLeft}>
                <div className={styles.avatarSection}>
                  <Tooltip content="修改Logo">
                    {logoUrl ? (
                      <Upload
                        limit={1}
                        accept="image/*"
                        listType="picture-card"
                        customRequest={async (option) => {
                          const { onProgress, onError, onSuccess, file } = option;
                          try {
                            const uploadImgUrl = await handleUpload(file, onProgress);
                            if (uploadImgUrl !== '') {
                              setLogoUrl(uploadImgUrl);
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
                          src={logoUrl}
                          width={160}
                          height={80}
                          preview={false}
                          actions={[
                            <IconCamera />
                          ]}
                        />
                      </Upload>) :
                      <Avatar shape="square" style={{ width: 160, height: 80, backgroundColor: '#F7F8FA', borderRadius: 12 }}>
                        <span className={styles.avatarText}>{tenantInfo.name?.slice(0, 6)}</span>
                      </Avatar>}
                  </Tooltip>
                </div>
                {/* 名称 & ID */}
                <div className={styles.section}>
                  <div className={styles.enterpriseName}>
                    {tenantInfo.name} {hasPermission(ACTIONS.UPDATE) && <IconEdit onClick={() => setRenameVisible(true)} style={{ cursor: 'pointer' }} />}
                  </div>
                  <div className={styles.enterpriseId}>企业ID：<Text copyable>{tenantInfo.corpCode}</Text></div>
                </div>
              </div>

              {/* 统计信息 */}
              <div className={styles.statsSection}>
                <div className={styles.statCard}>
                  <div className={styles.statLabel}>用户人数(个)</div>
                  <div className={styles.statValue}>
                    {tenantInfo.accountCount}
                  </div>
                </div>
                <div className={styles.statCard}>
                  <div className={styles.statLabel}>应用数量(个)</div>
                  <div className={styles.statValue}>{tenantInfo.appCount}</div>
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
                    <span>王少青</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>联系人手机号</span>
                    <span>137 0193 5734</span>
                  </div>
                </Col>
              </Row>

              <Row gutter={24} style={{ marginBottom: 28 }}>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>联系人邮箱</span>
                    <span>wangshaoqing@cmsr.chinamobile.com</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>用户上限</span>
                    <span>{tenantInfo.userLimit}</span>
                  </div>
                </Col>
              </Row>

              <Row gutter={24} style={{ marginBottom: 28 }}>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>行业类型</span>
                    <span>工业</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>企业地址</span>
                    <span>{tenantInfo.address}</span>
                  </div>
                </Col>
              </Row>
            </div>
          </div>
        </div>
      </div>

      {/* 修改企业名称 */}
      <Modal title="修改企业名称" visible={renameVisible} onOk={handleRenameSubmit} onCancel={() => setRenameVisible(false)}>
        <Form form={form} layout="vertical">
          <Form.Item label="新的企业名称" field="newName" rules={[{ required: true, message: "请输入新的企业名称" }]}>
            <Input placeholder="请输入新的企业名称" />
          </Form.Item>
        </Form>
      </Modal>
    </PlaceholderPanel>
  );
};

export default SpaceInfo;
