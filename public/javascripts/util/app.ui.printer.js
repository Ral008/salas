/*
 * jQuery UI Panel 1.8
 *
 * Copyright (c) 2010 TOHUMA (http://jqueryui.com/about)
 * Dual licensed under the MIT (MIT-LICENSE.txt)
 * and GPL (GPL-LICENSE.txt) licenses.
 *
 * http://docs.jquery.com/UI/Button
 *
 */
(function( $ ) {
    $.printer=function(opt){
        var _width=(opt.width && opt.width!='undefined'?opt.width:700);
        var _height=(opt.height && opt.height!='undefined'?opt.height:600);
        var _title=(opt.title && opt.title!='undefined'?opt.title:'***REPORTE***');

        var params='';
        if(opt.data && opt.data.length > 0){
            $.each(opt.data,function(key,param){
                _items['key']=key;
                _items['value']=param;
                params=params+_items['key']+'='+_items['value']+'&';
            });
        }

        if($('#ui-report-dialog').length == 0){
            $('body').append('<div id="ui-report-dialog" style="padding:0;display:none;">...</div>');
        }


        var url=opt.file+'?'+params;
        var iframe='<iframe border="0" src="'+url+'" style="width:'+(_width-5)+'px;height:'+(_height-40)+'px;left:0px;top:opx;" />';

        if(typeof opt.type!='undefined' && opt.type=='file'){
            $('#ui-report-dialog').empty().append(iframe);
            return true;
        }

        $('#ui-report-dialog').empty().append(iframe).dialog('destroy').dialog({
            modal:true,
            autoOpen:true,
            width:_width,
            title:_title,
            height:_height,
            open:function(){
                $(this).parents('.ui-dialog:first').find('.ui-dialog-content').css({
                    padding: 0,
                    overflow:'hidden'
                });
            }
        }).dialog('open').fadeIn(50);
        return true;
    };
}( jQuery ) );