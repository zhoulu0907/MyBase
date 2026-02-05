import { signal } from '@preact/signals-react';

/**
 * 组件校验状态的signal
 */
export const createComponentValidateSignal = () => {
    const pageComponentValidate = signal<{ [key: string]: boolean }>({});

    const setPageComponentValidate = (cp_id: string, status: boolean) => {
        pageComponentValidate.value = { ...pageComponentValidate.value, [cp_id]: status };
    };

    const loadPageComponentValidate = (status: { [key: string]: boolean }) => {
        pageComponentValidate.value = status;
    };

    const delPageComponentValidate = (cp_id: string) => {
        const newValidate = { ...pageComponentValidate.value };
        delete newValidate[cp_id];
        pageComponentValidate.value = newValidate;
    };
    const batchDelPageComponentValidate = (ids: Set<string> | string[]) => {
        const newValidate = { ...pageComponentValidate.value };
        ids.forEach((id: string) => {
            if (newValidate[id]) {
                delete newValidate[id];
            }
        });
        pageComponentValidate.value = newValidate;
    }

    const clearPageComponentValidate = () => {
        pageComponentValidate.value = {};
    };


    return {
        pageComponentValidate,
        setPageComponentValidate,
        loadPageComponentValidate,
        delPageComponentValidate,
        batchDelPageComponentValidate,
        clearPageComponentValidate
    }

};

export const usePageComponentValidateSignal = createComponentValidateSignal();