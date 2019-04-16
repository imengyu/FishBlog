// 通用页码组件
Vue.component('common-image-list', {
    props: ['items', 'nullText', 'loadedStatus', 'imageWidth', 'imageHeight'],
    data: function () {
        return {

        }
    },
    methods: {
        onDelImage(item){
            this.$emit('delete-click', item);
        },
        onAddImage(item){
            this.$emit('add-click', item);
        },
        getImage(hash, type){
            return getImageUrlFormHashWithType(hash, type)
        },
    },
    template: '<div class="image-list">\
        <div v-if="items && loadedStatus == \'loaded\'" v-for="(item, index) in items" class="image-item" v-on:click="onDelImage(item)">\
            <img :src="getImage(item.hash,item.type)" :style="\'width:\' + (imageWidth?imageWidth:\'auto\') + \';height:\' + (imageHeight?imageHeight:\'auto\')" />\
            <div class="image-box">\
                <label>{{ item.title }}</label>\
                <a href="javascript:;" v-on:click="onDelImage(item)" title="删除该图片"><i class="fa fa-times"></i></a>\
                <a href="javascript:;" v-on:click="onAddImage(item)" title="插入到文章光标位置"><i class="fa fa-hand-o-up"></i></a>\
            </div>\
        </div>\
        <div v-else="loadedStatus == \'loading\'" class="simple-loading-center" style="display:none;height:250px;">\
            <div class="simple-loading-container"><span class="simple-loading"></span></div>\
        </div>\
        <div v-if="nullText && (!items || items.length == 0) && loadedStatus == \'loaded\'">\
            <div class="text-secondary text-center mt-3 mb-3">{{ nullText }}</div>\
        </div>\
    </div>'
})