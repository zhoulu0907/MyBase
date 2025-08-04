import {
    LoginRequest
} from '../types';

import systemClient from './clients/system';


export const login = (req: LoginRequest) => {
    return systemClient.post('/auth/login', req);
};