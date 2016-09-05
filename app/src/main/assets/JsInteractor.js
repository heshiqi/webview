(function (win) {
    var hasOwnProperty = Object.prototype.hasOwnProperty;
    var JsInteractor = win.JsInteractor || (win.JsInteractor = {});
    var JsInteractor_PROTOCOL = 'actionfrom㊣';
    var Inner = {
        call: function (method, params, callback) {
            console.log(method+" "+params+" "+callback);
             var message=Util.getMessage(method,params,callback)
            console.log(message);
            var result=window.prompt(message, "");
            alert(result);

        },


         callasync: function (method, params, callback) {
                    console.log(method+" "+params+" "+callback);
                     var message=Util.getMessage(method,params,callback)
                      console.log(message);
                      window.prompt(message, "");

                }

    };
    var Util = {

        getMessage:function( method, params, callback){

              var msg = JsInteractor_PROTOCOL + this.getJson(method,params,callback);

              return msg;
        },

        getJson:function(method,param,call){
            var msg = new Object();
            msg.action = method;
            msg.params = param;
            msg.callback=call;
            return JSON.stringify(msg);
        }
    };
    for (var key in Inner) {
        if (!hasOwnProperty.call(JsInteractor, key)) {
            JsInteractor[key] = Inner[key];
        }
    }
})(window);