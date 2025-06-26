import { House, Clock, Document, FolderAdd, Location } from "@element-plus/icons-vue";

const menuData: Array<object> = [
    {
        path: "/",
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
                path: "/manage/video",
                name: "video",
                label: "视频管理",
                icon: "VideoCamera",
            },
            {
                path: "/manage/videoSeries",
                name: "series",
                label: "剧集管理",
                icon: "Film",
            },
            {
                path: "/manage/classification",
                name: "classify",
                label: "分类管理",
                icon: "MoreFilled",
            },
            {
                path: "/manage/rewards",
                name: "rewards",
                label: "奖励管理",
                icon: "Present",
            },
            {
                path: "/manage/tasks",
                name: "tasks",
                label: "用户任务管理",
                icon: "Notebook",
            },
            {
                path: "/manage/unlockCondition",
                name: "unlockCondition",
                label: "解锁条件管理",
                icon: "Unlock",
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
                path: "/log/operateLog",
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
                path: "/user/managerList",
                name: "managerList",
                label: "管理员列表",
                icon: "User",
            },
            {
                path: "/user/userList",
                name: "userList",
                label: "用户列表",
                icon: "Memo",
            },
        ],
    },
    {
        path: "/",
        name: "",
        label: "api",
        icon: "Aim",
        children: [{
            path: "/api/apiDoc",
            name: "apiDoc",
            label: "api接口",
            icon: "help",
        },]
    },
]

export default {
    menuData
}