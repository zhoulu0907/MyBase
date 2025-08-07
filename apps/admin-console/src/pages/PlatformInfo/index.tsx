import { Card, Descriptions, Space, Typography, Table, Tag, type TableColumnProps, Modal, Upload, Message } from '@arco-design/web-react';
// import { IconInfoCircle } from '@arco-design/web-react/icon';
import React, { useEffect } from 'react';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import styles from './index.module.less';
import { getPlatFormInfoListApi, getPlatformInfoApi, uploadPlatformLicenseApi,  type LicenseInfo } from '@onebase/platform-center'
import { formatTimestamp } from '@/utils/date';

const { Title, Text } = Typography;

const PlatformInfo: React.FC = () => {
  const { t } = useTranslation();
  const [visible, setVisible] = useState(false);
  const [licenseInfoList, setLicenseInfoList] = useState<LicenseInfo[]>([]);
  const [licenseInfo, setLicenseInfo] = useState<LicenseInfo | null>(null);
  
  const getPlatformInfo = async () => {
    const res = await getPlatformInfoApi();
    console.log('platformInfo res:', res);
    if(res.id) {
      setLicenseInfo(res)
    }
  };

  const getPlatformInfoList = async () => {
    console.log('获取认证记录:');
    try {
      const res = await getPlatFormInfoListApi({ pageNum: 1, pageSize: 10 })
      console.log('infoList res:', res);
      if (res && Array.isArray(res.list)) {
        setLicenseInfoList(res.list);
        setPagination((prevPagination) => ({
          ...prevPagination,
          total: res.total,
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
    getPlatformInfoList();
    getPlatformInfo();
  }, [])

  // 认证记录table结构
  const columns: TableColumnProps[] = [
    {
      title: '公司名称',
      dataIndex: 'enterpriseName',
      key: 'enterpriseName',
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
      ),
    },
    {
      title: '当前状态',
      dataIndex: 'status',
      key: 'status',
      render: (text) => (
        <Tag color={text === 'enable' ? 'green' : 'red'}>
          {text === 'enable' ? '已启用' : '已失效'}
        </Tag>
      )
    },
    {
      title: '到期时间',
      dataIndex: 'expireTime',
      key: 'expireTime',
      render: (text) => (
        <div>{formatTimestamp(text)}</div>
      )
    },
    {
      title: '操作',
      dataIndex: 'operation',
      key: 'operation',
      render: (_, record) => (
        <div className={styles.operation}>
          <a href="#" className={styles.btn} onClick={(e) => {
            e.preventDefault();
            setLicenseInfo(record);
            setVisible(true);
          }}>{t('platformInfo.check')}</a>
          
        </div>
      ),
    },
  ];

  // 上传认证
  const handleUploadCertification = async() => {
    // console.log('认证已经上传了');
    try {
      const resp = await uploadPlatformLicenseApi(data);
      console.log('uploadPlatformLicense-res: ', resp);
    } catch (error: any) {
      Message.error(error.message || '认证上传失败');
    }
  }
  const [data, setData] = useState(licenseInfoList);
  // 分页器
  const [pagination, setPagination] = useState({
    // sizeCanChange: true,
    showTotal: true,
    total: 0,
    pageSize: 10,
    pageNum: 1,
    pageSizeChangeResetCurrent: true,
  });
  const [loading, setLoading] = useState(false);

  const onChangeTable = (pagination: { pageNum?: number; pageSize?: number }) => {
    setLoading(true);
    console.log('pagination: ', pagination);
    setLoading(false);
  }

  return (
    <div className={styles.platformInfo}>
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
                <span className={styles.superAdminText}>
                  {licenseInfo?.adminUser || 'admin'}
                </span>
              </Text>
            </div>
            <div className={styles.createdAt}>
              <Text type="secondary">{t('platformInfo.createdAt')}：{formatTimestamp(licenseInfo?.createTime)}</Text>
            </div>
          </div>
        </div>
        {/* 平台基本信息 */}
        <Card title={t('platformInfo.basicInfo')} className={styles.infoCard}>
          <Descriptions
            column={2}
            colon=':'
            data={[
              {
                label: t('platformInfo.platformType'),
                value: licenseInfo?.platformType,
              },
              {
                label: t('platformInfo.authStatus'),
                value: (
                  <span className={styles.statusRunning}>
                    {licenseInfo?.status ? t('platformInfo.enable') : t('platformInfo.disable')}
                  </span>
                ),
              },
              {
                label: t('platformInfo.expireTime'),
                value: formatTimestamp(licenseInfo?.expireTime),
              },
              {
                label: t('platformInfo.version'),
                value: 'v1.0.0',
              },
              {
                label: t('platformInfo.tenantCount'),
                value: (
                  <Space>
                    <span>{licenseInfo?.actualTenantCount}</span>
                    <span> / </span>
                    <span>{licenseInfo?.tenantLimit}</span>
                  </Space>
                  ),
              },
            ]}
          />
        </Card>
        {/* 认证记录 */}
        <div className={styles.authRecord}>
          <Text>
            {t('platformInfo.authRecord')}
          </Text>
          <span onClick={handleUploadCertification}>
            {/* {t('platformInfo.uploadAuth')} */}
            <Upload
              className={styles.uploadAuth}
              showUploadList={false}
              action="/api/upload" // 这里需要替换为实际的上传接口地址
              headers={{
                authorization: 'authorization-text',
              }}
              onChange={(file) => {
                console.log('File uploaded:', file);
                console.log('File uploaded:', file.length);
                if(file.length > 0) {
                  // 清空file
                  file.splice(0, file.length);
                  console.log('after File uploaded:', file.length);
                }
              }}
            >
              <div className={styles.uploadAuthText}>
                {t('platformInfo.uploadAuth')}
              </div>
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
                value: licenseInfo.enterpriseName,
              },
              {
                label: t('platformInfo.certificationContent'),
                value: (
                  <div>
                    租户数量：{licenseInfo.tenantLimit}，用户数量：{licenseInfo.userLimit}
                  </div>
                ),
              },
              {
                label: t('platformInfo.status'),
                value: (
                  <Tag color={licenseInfo?.status === 'enable' ? 'green' : 'red'}>
                    {licenseInfo?.status === 'enable' ? '已启用' : '已失效'}
                  </Tag>
                ),
              },
              {
                label: t('platformInfo.expireTime'),
                value: formatTimestamp(licenseInfo.expireTime),
              },
            ]}
            labelStyle={{ fontWeight: 'bold', width: '100px' }}
          />
        )}
      </Modal>
    </div>
  );
};

export default PlatformInfo;