import FingerprintJS from '@fingerprintjs/fingerprintjs';
import md5 from 'crypto-js/md5';

export const getOrCreateDeviceInfo: any = async () => {
  const STORAGE_KEY = 'onebase_device_id';
  let deviceInfo = localStorage.getItem(STORAGE_KEY);

  if (!deviceInfo) {
    const fp = await FingerprintJS.load();
    const result = await fp.get();
    let deviceId = `${result.visitorId.substring(0, 16)}`;

    deviceInfo = md5(
      JSON.stringify({
        deviceId: deviceId,
        ua: navigator.userAgent
      })
    ).toString();

    localStorage.setItem(STORAGE_KEY, deviceInfo);
  } else {
    console.log('读取缓存deviceInfo: ', deviceInfo);
  }

  return deviceInfo;
};
