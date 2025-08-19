
import { infraService } from './clients';


export const uploadFile = (data: any) => {
    return infraService.post('/file/upload', data, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
};