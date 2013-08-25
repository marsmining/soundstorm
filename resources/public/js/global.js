//
// global.js - main source js file
//

/*jslint browser: true, devel: true, anon: true, indent: 4 */
/*global $*/

var glo = (function() {
    "use strict";

    var ctx = "",
        con = {
            ctx : ctx,
            pub : ctx + "/pub/api",
            app : ctx + "/app/api",
            adm : ctx + "/admin/api"
        };

    if (window.console === undefined) {
        window.console = {
            log : function(text) {
                // do nothing
            }
        };
    }

    return {

        // constants
        con : con,

        // logging wrapper
        log : function(msg, isInspect) {
            if (typeof msg === 'string' || typeof msg === 'number') {
                console.log(msg);
            } else {
                console.log(glo.inspect(msg, isInspect));
            }
        },

        // debugging
        inspect : function inspect(obj, isFull) {

            var pr = [],
                descr = "",
                cnt = 0,
                i,
                tmpStr,
                keyVar,
                typeName;

            if (typeof obj === 'boolean') {
                return "INSPECT: boolean";
            }

            if (!obj) {
                return "INSPECT: null";
            }

            if (!isFull) {
                if (obj.nodeType) {
                    tmpStr = "na";
                    if (obj.nodeType === 1) {
                        tmpStr = obj.getAttribute('class');
                    }
                    return "INSPECT: node: [name=" + obj.nodeName + ", type=" +
                            obj.nodeType + ", toString=" + String(obj) + ", classes=" +
                            tmpStr + ", id=" + obj.id + "]";
                }
                return "INSPECT: " + $.type(obj);
            }

            // array
            if ($.isArray(obj)) {
                typeName = "array of length: " + obj.length;
                for (i = 0; i < obj.length; i += 1) {
                    pr[cnt] = typeof obj[i] + " " + obj[i];
                }

            } else {

                // object or function
                typeName = $.type(obj);

                for (keyVar in obj) {
                    if (obj.hasOwnProperty(keyVar)) {
                        pr[cnt] = $.type(obj[keyVar]) + " --> " + keyVar;
                        cnt += 1;
                    }
                }
            }

            pr.sort(function(a, b) {
                var at = typeof a,
                    bt = typeof b;
                if (a === b) {
                    return 0;
                }
                if (at === bt) {
                    return a < b ? -1 : 1;
                }
                return at < bt ? -1 : 1;
            });

            for (i = 0; i < pr.length; i += 1) {
                descr += "  property[" + i + "] --> " + pr[i] + "\n";
            }

            return "INSPECT: " + typeName + "\n" + descr;
        }
    };

}());
