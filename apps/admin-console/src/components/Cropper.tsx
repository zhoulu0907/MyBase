import { Button, Grid, Slider } from "@arco-design/web-react";
import { IconMinus, IconPlus, IconRotateLeft } from "@arco-design/web-react/icon";
import { useState ,useMemo } from "react";
import EasyCropper from 'react-easy-crop';

interface ICropperProps {
  file: any,
  onOK: (fileData: any) =>void;
  onCancel: () =>void
}

const Cropper:React.FC<ICropperProps> = ({file ,onOK, onCancel}) => {
  const [crop, setCrop] = useState({
    x: 0,
    y: 0,
  });
  const [zoom, setZoom] = useState(1);
  const [rotation, setRotation] = useState(0);
  const [croppedAreaPixels, setCroppedAreaPixels] = useState(undefined)

  const url = useMemo(() => {
    return URL.createObjectURL(file);
  }, [file]);
  
  async function _getCroppedImg(url: string, pixelCrop: any, rotation = 0) {
  const image:any = await new Promise((resolve, reject) => {
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
          position: 'relative',
        }}
      >
        <EasyCropper
          style={{
            containerStyle: {
              width: '100%',
              height: 280,
            },
          }}
          aspect={4 / 4}
          image={url}
          crop={crop}
          zoom={zoom}
          rotation={rotation}
          onRotationChange={setRotation}
          onCropComplete={(_, croppedAreaPixels:any) => {
            setCroppedAreaPixels(croppedAreaPixels)
          }}
          onCropChange={setCrop}
          onZoomChange={setZoom}
        />
      </div>
      <Grid.Row justify='space-between' style={{ marginTop: 20, marginBottom: 20 }}>
        <Grid.Row
          style={{
            flex: 1,
            marginLeft: 12,
            marginRight: 12,
          }}
        >
          <IconMinus
            style={{ marginRight: 10 }}
            onClick={() => {
              setZoom(Math.max(1, zoom - 0.1));
            }}
          />
          <Slider
            style={{ flex: 1 }}
            step={0.1}
            value={zoom}
            onChange={(v:any) => {
              setZoom(v);
            }}
            min={0.8}
            max={3}
          />
          <IconPlus
            style={{ marginLeft: 10 }}
            onClick={() => {
              setZoom(Math.min(3, zoom + 0.1));
            }}
          />
        </Grid.Row>
        <IconRotateLeft
          onClick={() => {
            setRotation(rotation - 90);
          }}
        />
      </Grid.Row>

      <Grid.Row justify='end'>
        <Button onClick={onCancel} style={{ marginRight: 20 }}>
          取消
        </Button>
        <Button
          type='primary'
          onClick={async () => {
            const blob = await _getCroppedImg(url || '', croppedAreaPixels, rotation);

            if (blob) {
              const newFile = new File([blob as any], file.name || 'image', {
                type: file.type || 'image/*',
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

export default Cropper;