import PlaceholderPanel from '@/components/PlaceholderPanel';

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
import { IconCamera, IconCopy, IconEdit } from '@arco-design/web-react/icon';
import { TENANT_INFO_PERMISSION as ACTIONS, Cropper, hasPermission, TokenManager } from '@onebase/common';
import {
  getTenantInfo,
  PlatformTenantPublishMode,
  updateTenant,
  uploadFile,
  getFileUrlById,
  type PlatformTenantInfo
} from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import copy from 'copy-to-clipboard';
import styles from './index.module.less';
import Tags from './Tags';

const { Col, Row } = Grid;
const { Text } = Typography;

const SpaceInfo: React.FC<{ onTenantInfoChange?: (info: PlatformTenantInfo) => void }> = ({ onTenantInfoChange }) => {
  const [form] = Form.useForm();
  const [spaceInfo, setSpaceInfo] = useState<PlatformTenantInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [logoUrl, setLogoUrl] = useState<string>();
  const [renameVisible, setRenameVisible] = useState<boolean>(false);

  // 获取用户信息
  const tokenInfo = TokenManager.getTokenInfo();

  const fetchSpaceInfo = async (id: string) => {
    try {
      setLoading(true);
      const res = await getTenantInfo(id);
      setSpaceInfo(res);
      setLogoUrl(res.logoUrl);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    tokenInfo?.tenantId && fetchSpaceInfo(`${tokenInfo.tenantId}`);
    form.resetFields();
  }, []);

  const gotoLink = (link: string | null) => {
    if (!link) {
      return;
    }
    window.open(link, '_blank');
  };

  // 上传logo
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

  // 生成完整访问地址的函数
  const generateFullUrl = (path: string | null | undefined) => {
    if (!path) return '';
    const origin = window.location.origin;
    // pathname 是/开头和/结尾 空时为/
    const pathname = window.location.pathname;
    // normalizedPath是/开头
    const normalizedPath = path.startsWith('/') ? path : `/${path}`;
    return `${origin}${pathname}#/tenant/${tokenInfo?.tenantId}${normalizedPath}`;
  };

  // 重命名
  const handleRenameSubmit = async () => {
    if (!spaceInfo) return;
    const { newName } = await form.validate();

    if (!newName.trim()) {
      Message.error('空间名称不能为空');
      return;
    }

    try {
      await updateTenant({
        id: spaceInfo.id || "",
        name: newName,
      });

      setSpaceInfo({
        ...spaceInfo,
        name: newName
      });

      form.resetFields();
      setRenameVisible(false);
      Message.success('空间名称更新成功');
    } catch (error) {
      console.error('更新空间信息失败', error);
    }
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

  // 数据加载完成后但没有空间信息
  if (!spaceInfo) {
    return (
      <div className={styles.tenantPage}>
        <div style={{ textAlign: 'center', padding: '40px' }}>
          <p>无法加载空间信息</p>
        </div>
      </div>
    );
  }

  // 生成完整的工作台和移动端链接
  const fullWebsite = generateFullUrl(spaceInfo.website);

  return (
    <PlaceholderPanel hasPermission={hasPermission(ACTIONS.QUERY)} isLoading={loading}>
      <div className={styles.spaceInfoPage}>
        <div className={styles.spaceInfoPageMain}>
          <div className={`${styles.infoCard} ${styles.infoCardPrimary}`}>
            <div className={styles.blockHeader}>基本信息</div>

            <div className={styles.baseInfo}>
              <div className={styles.infoCardPrimaryLeft}>
                <div className={styles.avatarSection}>
                  <Tooltip content="修改Logo" disabled={!hasPermission(ACTIONS.UPDATE)}>
                    {
                      <Upload
                        disabled={!hasPermission(ACTIONS.UPDATE)}
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

                              // 更新企业Logo
                              try {
                                const res = await updateTenant({
                                  id: spaceInfo.id || '',
                                  logoUrl: uploadImgUrl
                                });

                                if (res) {
                                  const newInfo = {
                                    ...(spaceInfo as PlatformTenantInfo),
                                    logoUrl: uploadImgUrl
                                  };
                                  onTenantInfoChange?.(newInfo);
                                }
                              } catch (error) {
                                console.error('更新信息失败:', error);
                              }
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
                        beforeUpload={(file) => {
                          return new Promise((resolve) => {
                            const modal = Modal.confirm({
                              title: '裁剪图片',
                              onCancel: () => {
                                Message.info('取消上传');
                                resolve(false);
                                modal.close();
                              },
                              simple: false,
                              content: (
                                <Cropper
                                  file={file}
                                  aspect={2 / 1}
                                  onOK={(file: any) => {
                                    resolve(file);
                                    modal.close();
                                  }}
                                  onCancel={() => {
                                    resolve(false);
                                    Message.info('取消上传');
                                    modal.close();
                                  }}
                                />
                              ),
                              footer: null
                            });
                          });
                        }}
                      >
                        {logoUrl ? (
                          <Image
                            className={styles.reUploadLogo}
                            src={getFileUrlById(logoUrl)}
                            width={160}
                            height={80}
                            preview={false}
                            actions={[<IconCamera />]}
                          />
                        ) : (
                          <Avatar
                            shape="square"
                            style={{ width: 160, height: 80, backgroundColor: '#F7F8FA', borderRadius: 12 }}
                          >
                            <span className={styles.avatarText}>{spaceInfo.name?.slice(0, 6)}</span>
                          </Avatar>
                        )}
                      </Upload>
                    }
                  </Tooltip>
                </div>
                {/* 名称 & ID */}
                <div className={styles.section}>
                  <div className={styles.enterpriseName}>
                    {spaceInfo.name}{' '}
                    {hasPermission(ACTIONS.UPDATE) && (
                      <IconEdit onClick={() => setRenameVisible(true)} style={{ cursor: 'pointer' }} />
                    )}
                  </div>
                  <div className={styles.enterpriseId}>
                   空间ID：<Text copyable>{spaceInfo.id}</Text>
                  </div>
                </div>
              </div>

              {/* 统计信息 */}
              <div className={styles.statsSection}>
                <div className={styles.statCard}>
                  <div className={styles.statLabel}>企业数(个)</div>
                  <div className={styles.statValue}>{spaceInfo.corpCount || 0}</div>
                </div>
                <div className={styles.statCard}>
                  <div className={styles.statLabel}>应用数量(个)</div>
                  <div className={styles.statValue}>{spaceInfo.appCount || 0}</div>
                </div>
              </div>
            </div>
          </div>

          <div className={styles.infoCard}>
            {/* 访问地址 */}
            <div className={styles.blockHeader}>更多信息</div>

            <div className={styles.bottomSection}>
              <Row gutter={24} style={{ marginBottom: 30 }}>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>访问地址</span>
                    <span>
                      <Text
                        type="primary"
                        onClick={() => {
                          gotoLink(fullWebsite);
                        }}
                        style={{ cursor: 'pointer' }}
                      >
                        {fullWebsite || '-'}
                      </Text>
                      <IconCopy
                        className={styles.copyIcon}
                        onClick={(e) => {
                          e.stopPropagation(); // 阻止冒泡
                          copy(fullWebsite || '-');
                          Message.success('已复制');
                        }}
                      />
                    </span>
                  </div>
                </Col>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>用户数量</span>
                    <span>
                      {spaceInfo.existUserCount ?? '-'}/{spaceInfo.accountCount || 0}
                    </span>
                  </div>
                </Col>
              </Row>

              <Row gutter={24} style={{ marginBottom: 30 }}>
                <Col span={12}>
                  <div style={{ display: 'flex' }}>
                    <span className={styles.infoKey}>SaaS功能</span>
                    <span>{spaceInfo?.publishModel === PlatformTenantPublishMode.saas ? '已启用' : '未启用'}</span>
                  </div>
                </Col>
                <Col span={12}>
                  <Row>
                    <span className={styles.infoKey}>空间管理员</span>
                    <Tags data={spaceInfo.tenantAdminUserList} />
                  </Row>
                </Col>
              </Row>
            </div>
          </div>
        </div>
      </div>

      {/* 修改空间名称 */}
      <Modal
        title="修改空间名称"
        visible={renameVisible}
        onOk={handleRenameSubmit}
        onCancel={() => setRenameVisible(false)}
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            newName: spaceInfo.name
          }}
        >
          <Form.Item label="新的空间名称" field="newName" rules={[{ required: true, message: '请输入新的空间名称' }]}>
            <Input placeholder="请输入新的空间名称" />
          </Form.Item>
        </Form>
      </Modal>
    </PlaceholderPanel>
  );
};

export default SpaceInfo;
