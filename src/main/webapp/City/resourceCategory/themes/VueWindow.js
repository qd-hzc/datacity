/**
 * Created by wgx on 2016/4/22.
 */
Vue.component('esi-window', {
    template: '<div v-show="show" class="modal-bg"> <div class="modal-box"><button @click="close" class="modal-close"><span class="icon-remove-sign"></span></button><div class="modal-word"><h4>{{word}}</h4><p>{{subword}}</p></div><div class="modal-btn"><button @click="confirm" class="c-block-btn">确定</button></div> </div> </div>',
    data: function () {
        return {
            show: false,
            word: "important",
            subword: "discription",
            todo: function () {
            }
        }
    },
    methods: {
        confirm: function () {
            this.todo();
            this.show = false;
            this.todo = function () {
            };
        },
        close: function () {
            this.show = false;
        }
    },
    ready: function () {
        this.$on("Modal", function (modal) {
            this.show = true;
            this.word = modal.word;
            this.subword = modal.subword;
            this.todo = modal.todo;
        })
    }
})
;