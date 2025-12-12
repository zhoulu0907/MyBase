import { Descriptions, Modal, Tag } from "@arco-design/web-react";
import { formatTimestamp } from '@/utils/date';
import { useI18n } from "@/hooks/useI18n";
import type { LicenseInfo } from "@onebase/platform-center";


interface LicenseDetail {
  visible: boolean;
  selectedLicenseInfo: LicenseInfo | null;
  cancelDetailModal: () => void;
}
const licenseDetailModal = (props: LicenseDetail) => {
  const { visible, selectedLicenseInfo, cancelDetailModal } = props;
  const { t } = useI18n();

  return (
    <div>
      <Modal
        title={t('platformInfo.licenseDetail')}
        visible={visible}
        onCancel={cancelDetailModal}
      >
        {selectedLicenseInfo && (
          <Descriptions
            column={1}
            data={[
              {
                label: t('platformInfo.enterpriseName'),
                value: selectedLicenseInfo.enterpriseName
              },
              {
                label: t('platformInfo.certificationContent'),
                value: (
                  <div>
                    空间数量：{selectedLicenseInfo.tenantLimit}，用户数量：{selectedLicenseInfo.userLimit}
                  </div>
                )
              },
              {
                label: t('platformInfo.status'),
                value: (
                  <Tag color={selectedLicenseInfo?.status === 'enable' ? 'green' : 'red'}>
                    {selectedLicenseInfo?.status === 'enable' ? '已启用' : '已失效'}
                  </Tag>
                )
              },
              {
                label: t('platformInfo.expireTime'),
                value: formatTimestamp(selectedLicenseInfo.expireTime)
              }
            ]}
            labelStyle={{ fontWeight: 'bold', width: '100px' }}
          />
        )}
      </Modal>
    </div>
  )
}

export default licenseDetailModal;