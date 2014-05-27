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
(function($) {
    /*
     * Genera un lista concatenada de los check marcados
     * String()
     */
    $.fn.join = function(newseparator) {
        separator = newseparator || ',';
        sJoin = '';
        this.each(function() {
            if (this.checked == true) {
                sJoin = sJoin + this.value + separator;
            }
        });
        return sJoin.slice(0, sJoin.length - 1);
    };

    /*
     * Verifica si un check, esta marcado o no
     * true = marcado
     * false = no marcado
     * boolean ()
     */
    $.fn.isChecked = function() {//return bool (true o false)
        if ($(this).length > 1) {
            var check = 0;
            this.each(function() {
                if ($(this).attr('checked') == false) {
                    check++;
                }
            });
            return (check == 0 ? true : false);
        }
        return $(this).attr('checked');
    };

    /*
     * Marca o Desmarca un check
     * void (bool)
     */
    $.fn.checked = function(bool) {
        return $(this).attr('checked', bool);
    };

    /*
     * Desmarca un CheckBox o RadioButton
     * void ()
     */
    $.fn.unChecked = function() {
        return $(this).attr('checked', false);
    };

    /*
     * Setea 1 si esta marcado.
     * 0 si esta desmarcado
     * void ()
     */
    $.fn.checkValue = function(setting) {
        var opts = {
            check:(setting && setting.check) ? setting.check : 1,
            uncheck:(setting && setting.uncheck) ? setting.uncheck : 0,
            checked: (setting && setting.checked) ? setting.checked : null,
            unchecked:(setting && setting.unchecked) ? setting.unchecked : null
        }

        this.each(function() {
            check = $(this);
            check.click(function() {
                this.value = (this.checked == true ? opts.check : opts.uncheck);
                if (this.checked == true) {
                    if (typeof opts.checked == 'function') {
                        opts.checked();
                    }
                } else {
                    if (typeof opts.unchecked == 'function') {
                        opts.unchecked();
                    }
                }
            });
        });
        return check;
    };

    /*
     * Resetea un combo seleccionado su 1er elemento
     * void ()
     */
    $.fn.reset = function() {
        return $($(this)).find('option:eq(0)').attr('selected', true);
    };

    /*
     * De selecciona un combo
     * void ()
     */
    $.fn.unSelected = function() {
        //return $($(this)).find('option:eq(0)').attr('selected',true);
        return $($(this)).find(':selected').attr('selected', false)
    };

    /*
     * Deshabilita un object html
     * void (bool)
     */
    $.fn.disabled = function(bool) {
        return $(this).attr('disabled', bool);
    };

    $.fn.maxvalue = function (opts) {
        var opt = {
            maxvalue : parseFloat(opts.maxvalue),
            minvalue : parseFloat(opts.minvalue),
            msjmax : opts.msjmax ? opts.msjmax : 'EL MAXIMO VALOR ES ',
            msjmin : opts.msjmin ? opts.msjmin : 'EL MINIMO VALOR ES '
        }
        this.keypress(function (event) {
            _VALUE = this.value + String.fromCharCode(event.charCode);
            _VALUE = parseFloat(_VALUE);
            if (event.which && (_VALUE > opt.maxvalue)) {
                alert(opt.msjmax + opt.maxvalue);
                event.preventDefault();
            }
            if (event.which && (_VALUE < opt.minvalue)) {
                alert(opt.msjmin + opt.minvalue);
                event.preventDefault();
            }
        });

        return this;
    };
    /*
     * Coloca a solo lectura un object html
     * void (bool)
     */
    $.fn.readonly = function(bool) {
        return $(this).attr('readonly', bool);
    };

    /*
     * Verifica que object html este vacio
     * boolean ()
     */
    $.fn.isEmpty = function() {
        return ($.trim($(this).val()) == '' ? true : false);
    };

    /*
     * Cleaned all Inputs
     * void ()
     */
    $.fn.clean = function() {
        return $(this).val('');
    };
    /*
     * Verifica que object html este vacio
     * boolean ()
     */
    $.fn.addList = function(newlist) {
        select = $(this);
        select.empty();
        $.each(newlist, function(id, text) {
            select.append("<option value=" + id + ">" + text + "</option>");
        });
        return select;
    };

    /*
     * Devuelve el date actual asignado, segun el valor del constructor
     * resumeDate(dias);
     * Return date with format d/m/Y
     * Date()
     */
    $.fn.resumeDate = function(dias) {
        var newdate = new Date();
        _dia = newdate.getDate();
        if (_dia < 10) {
            _dia = '0' + _dia;
        }
        _mes = newdate.getMonth() + 1;
        if (_mes < 10) {
            _mes = '0' + _mes;
        }

        if (typeof dias == 'undefined') {
            return newdate.getDate() + '/' + (_mes) + '/' + newdate.getFullYear();
        }
        newdate.setTime(newdate.getTime() - (-0) + dias * 24 * 60 * 60 * 1000);
        _dia = newdate.getDate();
        if (_dia < 10) {
            _dia = '0' + _dia.toString();
        }
        _mes = newdate.getMonth() + 1;
        if (_mes < 10) {
            _mes = '0' + _mes;
        }
        currentdate = (_dia) + '/' + (_mes) + '/' + newdate.getFullYear();
        return $(this).val(currentdate);
    };

    /*
     *Convert Date Y-m-d to d/m/Y
     *String()
     **/
    $.fn.toMySQL = function() {
        currentDate = $(this).val();
        if (currentDate.indexOf('/') != -1) {
            currentDate = currentDate.split('/');
            currentDate = currentDate[2] + '-' + currentDate[1] + '-' + currentDate[0];
            return currentDate;
        }
        return $(this).val();
    };
    /*
     *Check String Interval with Format #-#
     *Boolean()
     **/
    $.fn.interval = function() {
        exp = new RegExp(/^\d{1,}-\d{1,}$/);
        txt = $(this).val();
        return txt.match(exp) == null ? false : true;
    };
    /*
     * Add new option Select-one
     * return Object
     */
    $.fn.addOption = function(opts) {
        select = $(this);
        opt = {
            key:opts.key ? opts.key : '00',
            text:opts.text ? opts.text : '[SELECCIONAR]'
        }
        $("<option/>").val(opt.key).text(opt.text).appendTo(select);
        return this;
    };
    /*
     * Add new option Select-one, in the index
     * return Object
     */
    $.fn.insertOption = function(opts) {
        select = $(this);
        opt = {
            index:opts.index,
            key:opts.key ? opts.key : '00',
            text:opts.text ? opts.text : 'SELECCIONAR'
        }
        newhtml = '<option value="' + opt.key + '">' + opt.text + '</option>';
        switch (opts.index) {
            case 'first':
                $(newhtml).insertBefore(select.find('option:eq(0)'));
                select.reset();
                break;
            case 'last':
                newindex = select.find('option').length - 1;
                $(newhtml).insertAfter(select.find('option:eq(' + newindex + ')'));
                break;
            default:
                newindex = (opts.index ? opts.index : 0);
                $(newhtml).insertAfter(select.find('option:eq(' + newindex + ')'));
                break;
        }
        return select;
    };

    /*
     * Setting datepicker
     * void()
     */
    $.fn.date = function(opt) {
        opts = {mask:'99/99/9999' || opt.mask};
        $(this).mask(opts.mask).datepicker(opt || $.configDate());
    };


    /*
     *
     *Limpia todas la cajas de texto
     *
     */
    $.fn.clearAll = function() {
        _this = $(this);
        return $(_this).find("input,select,textarea").each(function() {
            if (this.type == "select-one") {
                $(this).reset();
            } else if (this.type == "text") {
                this.value = "";
            } else {
                $(this).val("");
            }
        })
    }
    
    $.fn.readAllOnly = function() {
        _this = $(this);
        return $(_this).find("input,select,textarea").each(function() {
            $(this).readonly(true);
        })
    }
    
    $.showMessage = function(type,text,id,autoClose){    	
    	div = id==null ? $("#divMsj") : $("#"+id);
    	div.show().empty();
        if(type=='success'){
        	div.html('<div class="alert alert-success"><a class="close" data-dismiss="alert" href="#">×</a>'+text+'</div>').children()
        	if(autoClose){
        		div.children().delay(1000).slideUp("slow");
        	}
        }else if(type =='warning'){
        	div.html('<div class="alert alert-warning"><a class="close" data-dismiss="alert" href="#">×</a>'+text+'</div>').children()
        	if(autoClose){
        		div.children().delay(1000).slideUp("slow");
        	}
        }else if(type=='error'){
        	div.html('<div class="alert alert-error"><a class="close" data-dismiss="alert" href="#">×</a>'+text+'</div>')
        	if(autoClose){
        		div.children().delay(1000).slideUp("slow");
        	}
        }else if(type=='errorServer'){
        	div.html('<div class="alert alert-error fade in"><a class="close" data-dismiss="alert" href="#">×</a><strong>'+text+'</strong></div>');
        	if(autoClose){
        		div.children().delay(1000).slideUp("slow");
        	}
        }
    }

}(jQuery) );
