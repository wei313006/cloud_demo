
const menuData: Array<object> = [
    {
        path: "/admin/verified/datamanager",
        name: "home",
        label: "首页",
        icon: "HomeFilled",
    },
    {
        path: '/',
        name: '',
        label: '客户端管理',
        icon: 'Platform',
        children: [
            {
                path: "/verified/datamanager/files",
                name: "label",
                label: "文件管理",
                icon: "Files",
            },
        ]
    },
    {
        path: "/",
        name: "",
        label: "日志",
        icon: "List",
        children: [
            {
                path: "/verified/datamanager/operate/log",
                name: "operateLog",
                label: "操作日志",
                icon: "Document",
            },
        ]
    },
    {
        path: "/",
        name: "",
        label: "用户管理",
        icon: "UserFilled",
        children: [
            {
                path: "/verified/datamanager/managers",
                name: "managers",
                label: "管理员列表",
                icon: "User",
            },
            {
                path: "/verified/datamanager/client/info",
                name: "client-info",
                label: "访问信息列表",
                icon: "Memo",
            },
        ],
    },
    {
        path: "/",
        name: "",
        label: "api",
        icon: "ChromeFilled",
        children: [{
            path: "/verified/datamanager/api/doc",
            name: "api-doc",
            label: "api接口",
            icon: "Aim",
        },]
    },
]

export default {
    menuData
}