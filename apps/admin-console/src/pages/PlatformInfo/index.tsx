import { useI18n } from '@/hooks/useI18n';
import { formatTimestamp } from '@/utils/date';
import {
  Button,
  Card,
  Descriptions,
  Message,
  Modal,
  Space,
  Spin,
  Table,
  Tag,
  Typography,
  Upload,
  type TableColumnProps
} from '@arco-design/web-react';
import {
  downloadPlatformLicenseApi,
  getPlatFormInfoListApi,
  getPlatformInfoApi,
  uploadPlatformLicenseApi,
  type LicenseInfo,
  type LicenseInfoList
} from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';
import { downloadFile } from '@/utils/download';

const { Title, Text } = Typography;

const PlatformInfo: React.FC = () => {
  const { t } = useI18n();
  const [visible, setVisible] = useState(false);
  const [licenseInfoList, setLicenseInfoList] = useState<LicenseInfoList[]>([]);
  const [licenseInfo, setLicenseInfo] = useState<LicenseInfo | null>(null);
  const [pageLoading, setPageLoading] = useState<boolean>(true);
  const getPlatformInfo = async () => {
    const res = await getPlatformInfoApi();
    console.log('platformInfo res:', res);
    if (res.id) {
      setLicenseInfo(res);
    }
  };

  const getPlatformInfoList = async () => {
    console.log('获取认证记录:');
    try {
      const res = await getPlatFormInfoListApi({ pageNo: 1, pageSize: 10 });
      console.log('infoList res:', res);
      if (res && Array.isArray(res.list)) {
        setLicenseInfoList(res.list);
        setPagination((prevPagination) => ({
          ...prevPagination,
          total: res.total
        }));
      } else {
        console.warn('Invalid response format:', res);
        setLicenseInfoList([]);
        setLicenseInfo(null);
      }
    } catch (error: any) {
      Message.error(error.message || t('auth.loginFailed'));
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      setPageLoading(true);
      try {
        await Promise.all([getPlatformInfoList(), getPlatformInfo()]);
      } finally {
        setPageLoading(false);
      }
    };
    fetchData();
  }, []);

  // 认证记录table结构
  const columns: TableColumnProps[] = [
    {
      title: '公司名称',
      dataIndex: 'enterpriseName',
      key: 'enterpriseName'
    },
    {
      title: '认证内容',
      // 认证内容
      dataIndex: 'certificationContent',
      key: 'certificationContent',
      render: (text, record) => (
        <div>
          租户数量：{record.tenantLimit}，用户数量：{record.userLimit}
        </div>
      )
    },
    {
      title: '当前状态',
      dataIndex: 'status',
      key: 'status',
      render: (text) => <Tag color={text === 'enable' ? 'green' : 'red'}>{text === 'enable' ? '已启用' : '已失效'}</Tag>
    },
    {
      title: '到期时间',
      dataIndex: 'expireTime',
      key: 'expireTime',
      render: (text) => <div>{formatTimestamp(text)}</div>
    },
    {
      title: '操作',
      dataIndex: 'operation',
      key: 'operation',
      render: (_, record) => (
        <div className={styles.operation}>
          <a
            href="#"
            className={styles.btn}
            onClick={(e) => {
              e.preventDefault();
              setLicenseInfo(record);
              setVisible(true);
            }}
          >
            {t('platformInfo.check')}
          </a>
        </div>
      )
    }
  ];

  // 处理文件上传变化
  const handleFileUploadChange = async (fileList: any[]) => {
    console.log('File uploaded:', fileList);
    if (fileList.length > 0) {
      const latestFile = fileList[fileList.length - 1];
      // 确保originFile存在再进行上传操作
      if (latestFile && latestFile.originFile) {
        const formData = new FormData();
        formData.append('file', latestFile.originFile);
        
        setLoading(true);
        try {
          await uploadPlatformLicenseApi(formData);
          Message.success(t('platformInfo.uploadSuccess'));
          // 重新获取列表数据
          await Promise.all([getPlatformInfoList(), getPlatformInfo()]);
        } catch (error: any) {
          Message.error("上传license失败")
          console.error('上传license失败', error);
        } finally {
          setLoading(false);
        }
      }
      console.log('File uploaded:', fileList.length);
    }
  };

  // 下载认证
  const downloadLicense = async () => {
    try {
      const response = await downloadPlatformLicenseApi(1);
      console.log('downloadLicense', response);
        const blob = new Blob([response]);
        downloadFile(blob, 'license.lic.sm4');
    } catch (error) {
      console.error('下载失败:', error);
    }
    
  };

  // const [data, setData] = useState(licenseInfoList);
  // 分页器
  const [pagination, setPagination] = useState({
    // sizeCanChange: true,
    showTotal: true,
    total: 0,
    pageSize: 10,
    pageNo: 1,
    pageSizeChangeResetCurrent: true
  });
  const [loading, setLoading] = useState(false);

  const onChangeTable = (pagination: { pageNo?: number; pageSize?: number }) => {
    setLoading(true);
    console.log('pagination: ', pagination);
    setLoading(false);
  };

  return (
    <div className={styles.platformInfo}>
      <Spin loading={pageLoading} style={{ width: '100%' }}>
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          {/* 页面标题 */}
          <div className={styles.pageHeader}>
            <div className={styles.pageHeaderLeft}>
              <Title heading={4} className={styles.pageHeaderTitle}>
                {licenseInfo?.enterpriseName || '公司名称'}
              </Title>
              <div className={styles.companyId}>
                <Text type="secondary">{licenseInfo?.enterpriseCode || '公司编码'}</Text>
              </div>
              <div className="address">
                <Text type="secondary">{licenseInfo?.enterpriseAddress || '公司地址'}</Text>
              </div>
            </div>
            <div className={styles.pageHeaderRight}>
              <div className={styles.superAdmin}>
                <Text type="secondary">
                  {t('platformInfo.superAdmin')}：
                  <span className={styles.superAdminText}>{licenseInfo?.adminUser || '--'}</span>
                </Text>
              </div>
              <div className={styles.createdAt}>
                <Text type="secondary">
                  {t('platformInfo.createdAt')}：{formatTimestamp(licenseInfo?.createTime)  || '--'}
                </Text>
              </div>
            </div>
          </div>
          {/* 平台基本信息 */}
          <Card title={t('platformInfo.basicInfo')} className={styles.infoCard}>
            <Descriptions
              column={2}
              colon=":"
              data={[
                {
                  label: t('platformInfo.platformType'),
                  value: licenseInfo?.platformType || '--'
                },
                {
                  label: t('platformInfo.authStatus'),
                  value: (
                    <span className={styles.statusRunning}>
                      {licenseInfo?.status ? t('platformInfo.enable') : t('platformInfo.disable')}
                    </span>
                  )
                },
                {
                  label: t('platformInfo.expireTime'),
                  value: formatTimestamp(licenseInfo?.expireTime)
                },
                {
                  label: t('platformInfo.version'),
                  value: 'v1.0.0'
                },
                {
                  label: t('platformInfo.tenantCount'),
                  value: (
                    <Space>
                      <span>{licenseInfo?.actualTenantCount || '--'}</span>
                      <span> / </span>
                      <span>{licenseInfo?.tenantLimit || '--'}</span>
                    </Space>
                  )
                }
              ]}
            />
          </Card>
          {/* 认证记录 */}
          <div className={styles.authRecord}>
            <Text>{t('platformInfo.authRecord')}</Text>
            <span>
              <Button type="primary" onClick={downloadLicense} loading={loading}>{t('platformInfo.downloadAuth')}</Button>
              <Upload
                className={styles.uploadAuth}
                showUploadList={false}
                headers={{
                  authorization: 'authorization-text'
                }}
                onChange={handleFileUploadChange}
              >
                <div className={styles.uploadAuthText}>{t('platformInfo.uploadAuth')}</div>
              </Upload>
            </span>
          </div>
          <Table
            loading={loading}
            columns={columns}
            data={licenseInfoList}
            pagination={{
              ...pagination,
              className: styles.tablePagination
            }}
            onChange={onChangeTable}
            rowKey={(record) => record.id}
            border={false}
          />
        </Space>
      </Spin>
      <Modal
        title={t('platformInfo.licenseDetail')}
        visible={visible}
        onCancel={() => setVisible(false)}
        footer={null}
        className={styles.licenseModal}
      >
        {licenseInfo && (
          <Descriptions
            column={1}
            data={[
              {
                label: t('platformInfo.enterpriseName'),
                value: licenseInfo.enterpriseName
              },
              {
                label: t('platformInfo.certificationContent'),
                value: (
                  <div>
                    租户数量：{licenseInfo.tenantLimit}，用户数量：{licenseInfo.userLimit}
                  </div>
                )
              },
              {
                label: t('platformInfo.status'),
                value: (
                  <Tag color={licenseInfo?.status === 'enable' ? 'green' : 'red'}>
                    {licenseInfo?.status === 'enable' ? '已启用' : '已失效'}
                  </Tag>
                )
              },
              {
                label: t('platformInfo.expireTime'),
                value: formatTimestamp(licenseInfo.expireTime)
              }
            ]}
            labelStyle={{ fontWeight: 'bold', width: '100px' }}
          />
        )}
      </Modal>
    </div>
  );
};

export default PlatformInfo;
