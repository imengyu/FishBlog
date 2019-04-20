// 通用页码组件
Vue.component('common-pagination', {
    props: {
        currentPage: {
            default: 1
        },
        allPage: {
            default: 1
        },
        maxShowPage: {
            default: 1
        },
        showFirstAndEnd: {
            default: true
        },
        showPageSize: {
            default: true
        },
        previousText: {
            default: '上一页'
        },
        nextText: {
            default: '下一页'
        },
        pageSizes: {
            default: function () {
                return [5,10,15,20,30,50]
            }, 
        },
        pageSizeDefault: {
            default: 10
        },
    },
    data: function () {
        return {
            selectedPageSize: 10
        }
    },
    methods: {
        setPageSize(size){
            this.selectedPageSize = size;
        },
        itemClick(item){
            this.$emit('item-click', item);
        },
        itemCustomerClick(customerControlId, item){
            this.$emit('item-customer-click', customerControlId, item);
          },
        getPageRangeStart(){
            var start = this.currentPage - this.maxShowPage;
            if(start < 1) start = 1;
            return start;
        },
        getPageRangeEnd(){
            var end = this.currentPage + this.maxShowPage;
            if(end > this.allPage) end = this.allPage;
            return end;
        },
        getPageRangeShouldShowStart(){
            return this.currentPage - this.maxShowPage > 1;
        },
        getPageRangeShouldShowEnd(){
            return this.currentPage + this.maxShowPage < this.allPage;
        },
        getPageRangeShouldShowStartEllipsis(){
            return this.currentPage - this.maxShowPage > 2;
        },
        getPageRangeShouldShowEndEllipsis(){
            return this.currentPage + this.maxShowPage < this.allPage - 1;
        },
        pageSizeChanged(){
            this.$emit('page-size-changed', this.selectedPageSize);
        },
    },
    template: '<nav aria-label="common-pagination d-flex justify-content-between"><div class="form-inline float-left"><slot name="left-slot"></slot></div><form class="form-inline float-left"><label class="my-1 mr-2" for="disabled">每页显示</label><select class="custom-select my-1 mr-sm-2" id="pageSizeSelect" v-model="selectedPageSize" v-on:change="pageSizeChanged()"><option v-for="size in pageSizes" :value="size">{{ size }} 条</option></select></form><ul v-if="allPage>0" class="pagination justify-content-end"><li :class="\'page-item\' + (1==currentPage? \' disabled\' : \'\')"><span v-if="currentPage == 1" class="page-link">{{previousText}}</span><a v-else class="page-link" href="javascript:void(0)" v-on:click="itemClick(currentPage-1)">{{previousText}}</a></li><li v-if="showFirstAndEnd && currentPage!=1 && getPageRangeShouldShowStart()" class="page-item"><span v-if="currentPage == 1" class="page-link">1</span><a v-else class="page-link" href="javascript:void(0)" v-on:click="itemClick(1)">1</a></li><li v-if="getPageRangeShouldShowStartEllipsis()" class="page-item"><i class="fa fa-ellipsis-h" aria-hidden="true"></i></li><li v-for="index in allPage" v-if="index &gt;= getPageRangeStart() && index &lt;= getPageRangeEnd()" :class="\'page-item\' + (index==currentPage? \' active\' : \'\')"><span v-if="index==currentPage" class="page-link">{{index}}<span class="sr-only">(current)</span></span><a v-else class="page-link" href="javascript:void(0)" v-on:click="itemClick(index)">{{index}}</a></li><li v-if="getPageRangeShouldShowEndEllipsis()" class="page-item"><i class="fa fa-ellipsis-h" aria-hidden="true"></i></li><li v-if="showFirstAndEnd && currentPage!=allPage && getPageRangeShouldShowEnd()" class="page-item"><span v-if="currentPage == allPage" class="page-link">{{allPage}}</span><a v-else class="page-link" href="javascript:void(0)" v-on:click="itemClick(allPage)">{{allPage}}</a></li><li :class="\'page-item\' + (allPage==currentPage? \' disabled\' : \'\')"><span v-if="currentPage == allPage" class="page-link">{{nextText}}</span><a v-else class="page-link" v-on:click="itemClick(currentPage+1)" href="javascript:void(0)">{{nextText}}</a></li></ul><ul v-else class="pagination justify-content-end"><li class="page-item disabled"><span class="page-link">无数据</span></li></ul></nav>'
})