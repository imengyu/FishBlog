// 通用列表组件
Vue.component('common-table', {
    props: [ 'columns', 'items', 'nullText', 'loadedStatus','showCheckBoxes' ],
    data: function () {
        return {
            allChecked: false,
        }
    },
    methods: {
      itemClick(item){
        this.$emit('item-click', item);
      },
      itemCustomerClick(customerControlId, item){
        this.$emit('item-customer-click', customerControlId, item);
      },
      retryLoad(){
        this.$emit('retry-load');
      },
      unCheckAll(){
        $('#common-table-' + this._uid + ' .customer-check').prop('checked', false);
      },
      getCheckedItems(){
        var main = this;
        var checkedItems = {};
        $('#common-table-' + this._uid + ' .customer-check:checked').each(function(){
            var index = $(this).attr('data-index');
            checkedItems[index] = main.items[index];
        });
        return checkedItems;
      },
      checkAllSwitch(){
        if(!this.allChecked){
          $('#common-table-' + this._uid + ' .customer-check').prop('checked', true);
        }else{
          $('#common-table-' + this._uid + ' .customer-check').prop('checked', false);
        }
      },
    },
    template: '<div class="position-relative table-responsive" :id="\'common-table-\' + _uid">\
<div v-if="loadedStatus == \'loading\'" class="full position-absolute blur-none">\
<div id="loading-center-absolute"><span id="loading-simple-roll"></span></div>\
</div>\
<table :class="\'table\' + (loadedStatus == \'loading\' ? \' blur-lower\' : \'\')">\
<thead>\
<tr>\
<th v-if="showCheckBoxes" width="30px">\
<div class="custom-control custom-checkbox pr-0" title="全选" v-on:click="checkAllSwitch()">\
<input type="checkbox" class="custom-control-input" id="checkAll" v-model="allChecked">\
<label class="custom-control-label" for="checkAll"></label>\
</div>\
</th>\
<th v-for="column in columns" scope="col" :style="(column.width ? \'min-width: \' + column.width + \';\' : \'\') + (column.textAlign ? \'text-align: \' + column.textAlign + \';\' : \'\')">{{ column.text }}</th>\
</tr>\
</thead>\
<tbody v-if="items && items.length > 0">\
<tr v-for="(item, itemIndex) in items">\
<td v-if="showCheckBoxes">\
<div class="custom-control custom-checkbox pr-0">\
<input type="checkbox" class="custom-control-input customer-check" :id="\'checkItem\' + itemIndex" :data-index="itemIndex">\
<label class="custom-control-label" :for="\'checkItem\' + itemIndex"></label>\
</div>\
</th>\
<td v-for="(column, index) in columns">\
<div v-if="column.useSlot"><slot :name="column.slotName" :item="item"></slot></div>\
<a v-else-if="!column.useSlot && column.useData==\'index\' && column.useLink" v-on:click="itemClick(item)" v-html="item[index]" href="javascript:;"></a>\
<a v-else-if="!column.useSlot && column.useData==\'name\' && column.useLink" v-on:click="itemClick(item)" v-html="item[column.dataName]" href="javascript:;"></a>\
<a v-else-if="!column.useSlot && column.useData==\'custom\' && column.useLink" v-on:click="itemClick(item)" v-html="column.customDataFunc(item)" href="javascript:;"></a>\
<div v-else-if="!column.useSlot && column.useData==\'index\'" v-html="item[index]"></div>\
<div v-else-if="!column.useSlot && column.useData==\'name\'" v-html="item[column.dataName]"></div>\
<div v-else-if="!column.useSlot && column.useData==\'custom\'" v-html="column.customDataFunc(item)"></div>\
</td>\
</tr>\
</tbody>\
</table>\
<div v-if="loadedStatus == \'failed\'">\
<div class="box full text-center text-danger d-flex justify-content-center align-items-center flex-column">\
<i class=" fa fa-times-circle-o" aria-hidden="true" style="font-size: 3.5em"></i>\
<p class="text-secondary mt-2">加载失败<br/><a href="#" v-on:click="retryLoad()">重试</a></p>\
</div></div>\
<p v-else-if="loadedStatus != \'loading\' && (!items || items.length == 0) && nullText" class="text-center">{{ nullText }}</p>\
</div>'
})