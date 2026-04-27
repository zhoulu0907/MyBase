import { Button, Grid, Slider, Switch, Typography } from '@arco-design/web-react';
import { IconMinus, IconPlus, IconRotateLeft } from '@arco-design/web-react/icon';
import { useMemo, useState } from 'react';
import EasyCropper from 'react-easy-crop';

const { Text } = Typography;

interface ICropperProps {
  file: any;
  onOK: (fileData: any) => void;
  onCancel: () => void;
  aspect?: number; // 初始裁剪比例，不传则默认自由裁剪
}

export const Cropper: React.FC<ICropperProps> = ({ file, onOK, onCancel, aspect: initialAspect }) => {
  const [crop, setCrop] = useState({
    x: 0,
    y: 0
  });
  const [zoom, setZoom] = useState(1);
  const [rotation, setRotation] = useState(0);
  const [croppedAreaPixels, setCroppedAreaPixels] = useState(undefined);
  // 是否固定比例
  const [fixedAspect, setFixedAspect] = useState(initialAspect !== undefined);
  // 当前比例值（宽/高）
  const [aspectRatio, setAspectRatio] = useState(initialAspect ?? 2 / 1);

  const url = useMemo(() => {
    return URL.createObjectURL(file);
  }, [file]);

  async function _getCroppedImg(url: string, pixelCrop: any, rotation = 0) {
    const image: any = await new Promise((resolve, reject) => {
      const image = new Image();
      image.addEventListener('load', () => resolve(image));
      image.addEventListener('error', (error) => reject(error));
      image.src = url;
    });
    const canvas = document.createElement('canvas');
    const ctx = canvas.getContext('2d');

    if (!ctx || !image) {
      return null;
    }

    const imageSize = 2 * ((Math.max(image.width, image.height) / 2) * Math.sqrt(2));
    canvas.width = imageSize;
    canvas.height = imageSize;

    if (rotation) {
      ctx.translate(imageSize / 2, imageSize / 2);
      ctx.rotate((rotation * Math.PI) / 180);
      ctx.translate(-imageSize / 2, -imageSize / 2);
    }

    ctx.drawImage(image, imageSize / 2 - image.width / 2, imageSize / 2 - image.height / 2);
    const data = ctx.getImageData(0, 0, imageSize, imageSize);
    canvas.width = pixelCrop.width;
    canvas.height = pixelCrop.height;
    ctx.putImageData(
      data,
      Math.round(0 - imageSize / 2 + image.width * 0.5 - pixelCrop.x),
      Math.round(0 - imageSize / 2 + image.height * 0.5 - pixelCrop.y)
    );
    return new Promise((resolve) => {
      canvas.toBlob((blob) => {
        resolve(blob);
      });
    });
  } // 裁剪组件

  return (
    <div>
      <div
        style={{
          width: '100%',
          height: 280,
          position: 'relative'
        }}
      >
        <EasyCropper
          style={{
            containerStyle: {
              width: '100%',
              height: 280
            }
          }}
          aspect={fixedAspect ? aspectRatio : undefined}
          image={url}
          crop={crop}
          zoom={zoom}
          rotation={rotation}
          onRotationChange={setRotation}
          onCropComplete={(_, croppedAreaPixels: any) => {
            setCroppedAreaPixels(croppedAreaPixels);
          }}
          onCropChange={setCrop}
          onZoomChange={setZoom}
        />
      </div>

      {/* 比例控制 */}
      <Grid.Row style={{ marginTop: 20, marginBottom: 10, alignItems: 'center' }} gutter={24}>
        <Grid.Col span={7}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Text>固定比例：</Text>
            <Switch
              size="small"
              checked={fixedAspect}
              onChange={(checked) => {
                setFixedAspect(checked);
                if (checked && !aspectRatio) {
                  setAspectRatio(2 / 1);
                }
              }}
            />
          </div>
        </Grid.Col>
        {fixedAspect && (
          <Grid.Col span={17}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <Text style={{ minWidth: 60 }}>宽高比：</Text>
              <IconMinus
                style={{ cursor: 'pointer' }}
                onClick={() => {
                  setAspectRatio(Math.max(0.1, aspectRatio - 0.1));
                }}
              />
              <Slider
                style={{ flex: 1 }}
                step={0.1}
                value={aspectRatio}
                onChange={(v: any) => {
                  setAspectRatio(v);
                }}
                min={0.1}
                max={5}
              />
              <IconPlus
                style={{ cursor: 'pointer' }}
                onClick={() => {
                  setAspectRatio(Math.min(5, aspectRatio + 0.1));
                }}
              />
              <Text style={{ minWidth: 80, marginLeft: 8 }}>{aspectRatio.toFixed(2)}:1</Text>
            </div>
          </Grid.Col>
        )}
      </Grid.Row>

      {/* 缩放和旋转控制 */}
      <Grid.Row style={{ marginTop: 10, marginBottom: 20, alignItems: 'center' }} gutter={24}>
        <Grid.Col span={18}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Text style={{ marginRight: 10, minWidth: 40 }}>缩放：</Text>
            <IconMinus
              style={{ marginRight: 10, cursor: 'pointer' }}
              onClick={() => {
                setZoom(Math.max(0.8, zoom - 0.1));
              }}
            />
            <Slider
              style={{ flex: 1 }}
              step={0.1}
              value={zoom}
              onChange={(v: any) => {
                setZoom(v);
              }}
              min={0.8}
              max={3}
            />
            <IconPlus
              style={{ marginLeft: 10, cursor: 'pointer' }}
              onClick={() => {
                setZoom(Math.min(3, zoom + 0.1));
              }}
            />
          </div>
        </Grid.Col>
        <Grid.Col span={6}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Text>旋转：</Text>
            <IconRotateLeft
              style={{ cursor: 'pointer' }}
              onClick={() => {
                setRotation(rotation - 90);
              }}
            />
          </div>
        </Grid.Col>
      </Grid.Row>

      <Grid.Row justify="end">
        <Button onClick={onCancel} style={{ marginRight: 20 }}>
          取消
        </Button>
        <Button
          type="primary"
          onClick={async () => {
            const blob = await _getCroppedImg(url || '', croppedAreaPixels, rotation);

            if (blob) {
              const newFile = new File([blob as any], file.name || 'image', {
                type: file.type || 'image/*'
              });
              onOK(newFile);
            }
          }}
        >
          确定
        </Button>
      </Grid.Row>
    </div>
  );
};
