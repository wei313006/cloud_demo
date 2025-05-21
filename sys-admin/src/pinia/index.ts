import { defineStore } from "pinia";

export const useGlobalStore = defineStore('globalStore', {

    actions: {
        initGlobalData(data: any) {
            this.globalData = data
        },
    },

    state() {
        return {
            globalData: {},
        }
    },

    getters: {

    },

})