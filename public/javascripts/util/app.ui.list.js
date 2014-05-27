/* * Copyright (c) 2010 TOHUMA (http://jqueryui.com/about)
 * Dual licensed under the MIT (MIT-LICENSE.txt)
 * and GPL (GPL-LICENSE.txt) licenses.
 * * http://docs.jquery.com/UI/inlineform
 * */
(function($) {
    _status = false;
    $.fn.list = function(config) {
        return this.each(function() {
            var select = this;            
            if (select.length <= 1) {
                $.ajax({
                    url:(config.url ? config.url : ''),
                    type:(config.type ? config.type : 'get'),
                    dataType:'json',
                    data:(config.data ? config.data : {}),
                    beforeSend:function() {
                        if (config.before) {
                            config.before();
                        }
                    },
                    success:function(result) {
                        if (result && result.status == 1) {
                            $.each(result.data, function(id, text) {
                                option = "<option value=" + id + ">" + text + "</option>";
                                $(select).append(option);
                            });
                        }
                    },
                    complete:function() {
                        if (config.finish) {
                            config.finish();
                        }
                        if (config.change) {
                            $(select).change(config.change);
                        }
                    },
                    error:ERRORCOMPLETE
                });
            }
        });
    };
}(jQuery) );
