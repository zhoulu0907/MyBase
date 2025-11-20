import FingerprintJS from '@fingerprintjs/fingerprintjs';

export const getOrCreateDeviceId: any = async () => {
  const STORAGE_KEY = 'onebase_device_id';
  let deviceId = localStorage.getItem(STORAGE_KEY);

  if (!deviceId) {
    const fp = await FingerprintJS.load();
    const result = await fp.get();
    console.log('result: ', result);
    deviceId = `web_${result.visitorId.substring(0, 16)}`;
    localStorage.setItem(STORAGE_KEY, deviceId);
  } else {
    console.log('读取缓存deviceId: ', deviceId);
  }

  return deviceId;
};
