var global = {};
global.callbacks = {}

global.callback = function (callbackName, response) {
   var callbackObject = global.callbacks[callbackName];
   console.log("xxxx"+callbackName);
   if (callbackObject !== undefined){
       if(callbackObject.callback != undefined){
          console.log("xxxxxx"+response);
            var ret = callbackObject.callback(response);
           if(ret === false){
               return
           }
           delete global.callbacks[callbackName];
       }
   }
}

global.takeNativeAction = function(commandName, parameters){
    console.log("global takeNativeAction")
    var request = {};
    request.name = commandName;
    request.param = parameters;
    window.xxwebview.takeNativeAction(JSON.stringify(request));
}

global.takeNativeActionWithCallback = function(commandName, parameters, callback) {
    var callbackName = "nativetojs_callback_" +  (new Date()).getTime() + "_" + Math.floor(Math.random() * 10000);
    global.callbacks[callbackName] = {callback:callback};
    // 构成一个参数对象，发给native接收
    var request = {};
    request.name = commandName;
    request.param = parameters;
    request.param.callbackName = callbackName;
    window.xxwebview.takeNativeAction(JSON.stringify(request));
}

window.global = global;
