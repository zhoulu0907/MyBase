import { Checkbox, Radio } from "@arco-design/web-react";


interface breadCrumbItem { 
    key?: string;
    title: string; 
    id?: string 
}
interface ISelectDeptMemberProps {
    isMultiple: boolean;
    breadCrumbs: breadCrumbItem[];
    deptInfo: any;
    selectedKeys:string[];
    selectedMembers: any[];
    setSelectedKeys:(data: string[]) => void;
    removeMember: (key: string) => void;
    onUpdateSelectedMembers?: (members: any[]) => void;
}

type MemberType =  "radio" | "checkbox";


export const SelectDeptMember:React.FC<ISelectDeptMemberProps> = ({
    deptInfo, 
    breadCrumbs,
    selectedKeys, 
    selectedMembers, 
    isMultiple, 
    setSelectedKeys,
    onUpdateSelectedMembers,
    removeMember 
}) => {
    // 构建部门完整路径
    const buildDepartmentPath = (deptName?: string) => {
        const deptNames = breadCrumbs.slice(1).map((breadcrumb) => breadcrumb.title);
        return deptNames.length > 0 ? deptNames.join('/') : (deptName ? deptName : '未分配部门');
    };

    const getAllSelectedMembers = (type: 'checkbox' | 'radio') => {
        let newSelectedMembers = JSON.parse(JSON.stringify(selectedMembers));
        const addedNewMember = {
            key: deptInfo.key,
            name: deptInfo.title,
            department: buildDepartmentPath(deptInfo?.deptName),
            email: deptInfo.email
        }
        if(type === 'checkbox') {
            newSelectedMembers.push(addedNewMember);
        }else {
            newSelectedMembers = [addedNewMember];
        }
        return newSelectedMembers;
    }

    const updateMembers = (type: MemberType) => {
        if(type === 'checkbox') {
            setSelectedKeys([...selectedKeys, deptInfo.key]);
        }else {
            setSelectedKeys([deptInfo.key])
        }
        const newSelectedMembers = getAllSelectedMembers(type);
        if (onUpdateSelectedMembers) {
            onUpdateSelectedMembers(newSelectedMembers);
        }
    }

    const onChange = (type: MemberType, e: any) => {
        if (type === 'checkbox') {
            if (e) {
                updateMembers(type);
            } else {
                removeMember(deptInfo.key);
            }
        } else {
           updateMembers(type);
        }
    };

    return (
        <>
            {isMultiple ? (
                <Checkbox 
                    checked={selectedKeys.includes(deptInfo.key)}
                    onChange={onChange.bind(null, "checkbox")}
                />
            ) : (
                <Radio key={deptInfo.key}
                    checked={selectedKeys.includes(deptInfo.key)}
                    onChange={onChange.bind(null, "radio")}/>
            )}
        </>
    )
}
