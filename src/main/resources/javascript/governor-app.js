/*
 * default call for page */
$(document).ready(function () {
    var d = new Date();
    var month = d.getMonth()+1;
    var day = d.getDate();
    var output = d.getFullYear() + '/' +
        (month<10 ? '0' : '') + month + '/' +
        (day<10 ? '0' : '') + day;

  //Report title
    var reportTitle = "Report";
    var selectedLang = $('#selectedLang').val();
    var selectedBaseUrl = $('#selectedBaseUrl').val();
    var langFileUrl = getDataTablesLang(selectedBaseUrl, selectedLang);

    $('label.tree-toggler').click(function () {
        $(this).parent().children('ul.tree').toggle(300);
    });

    $('label.tree-toggler').parent().children('ul.tree').toggle();
    $('.div-report').hide();

    $('.datepicker').datepicker().on('changeDate', function(e){
        $(this).datepicker('hide');
    });

    $('.reports-data-table').DataTable( {
        "language": {
            "url": langFileUrl
        },
        "searching": false,
        "Ordering:": false,
        "processing": true,
        "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, 1000] ],
        "columnDefs": [
            {
                "render": function ( data, type, row ) {
                    return "<a target=\"_blank\" href=\""+$('#baseEdit').val()+data+".html\">"+data+"</a>";
                },
                "targets": 1
            }
        ],
        "pagingType": "full_numbers",
        "dom": 'Blfrtip',
        buttons: [
            'csv'
        ]

    } );


    $('#dateAndAuthorSearchByDate').change(function(){
        if( $('#dateAndAuthorSearchByDate').is(':checked') ) {
            $('.searchableByDate').removeClass('hidden');
        } else {
            $('.searchableByDate').addClass('hidden');
        }
    });



    $('#selectAuthor').change(function(){
        if( $('#selectAuthor').is(':checked') ) {
            $('.searchableByAuthor').removeClass('hidden');
        } else {
            $('.searchableByAuthor').addClass('hidden');
        }
    });

    $(".modal-transparent").on('show.bs.modal', function () {
        setTimeout( function() {
            $(".modal-backdrop").addClass("modal-backdrop-transparent");
        }, 0);
    });
    $(".modal-transparent").on('hidden.bs.modal', function () {
        $(".modal-backdrop").addClass("modal-backdrop-transparent");
    });

    $(".modal-fullscreen").on('show.bs.modal', function () {
        setTimeout( function() {
            $(".modal-backdrop").addClass("modal-backdrop-fullscreen");
        }, 0);
    });
    $(".modal-fullscreen").on('hidden.bs.modal', function () {
        $(".modal-backdrop").addClass("modal-backdrop-fullscreen");
    });



});

/* method for show different views
 * when option is selected in the menu */
function showView(id){
    var divId = '#report-' + id;
    $('.div-report').hide();
    $(divId).show();
}

/**
 * Capitalize words
 */
function capitalize(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

/**
 * format str date
 */
function formatedDateFromString(strDate){
    var regex = new RegExp('-', 'g');
    if (strDate.includes("T"))
        strDate = (strDate.split("T")[0]).replace(regex,"/");

    return strDate;
}

/**
 * check if is undefined
 */
function  checkUndefined(input) {
    if (typeof input == "undefined")
       return '---'

    return input;
}

/**
 * check if is empty
 */
function isEmpty(val){
    return (val === undefined || val == null || val.length <= 0) ? true : false;
}


/**
 * Calculate the difference of two dates in total days
 */
function diffDaysBetweenToday(startDate)
{
    var endDate =   new Date();
    var ndays;
    var tv1 = startDate.valueOf();  // msec since 1970
    var tv2 = endDate.valueOf();

    ndays = (tv2 - tv1) / 1000 / 86400;
    ndays = Math.round(ndays - 0.5);
    return ndays;
}

/**
 * loading function start
 */
function ajaxindicatorstart(text)
{
    if(jQuery('body').find('#resultLoading').attr('id') != 'resultLoading'){
        jQuery('body').append('<div id="resultLoading" style="display:none"><div><img src="/modules/content-reports/css/images/ajax-loader.gif"><div>'+text+'</div></div><div class="bg"></div></div>');
    }

    jQuery('#resultLoading').css({
        'width':'100%',
        'height':'100%',
        'position':'fixed',
        'z-index':'10000000',
        'top':'0',
        'left':'0',
        'right':'0',
        'bottom':'0',
        'margin':'auto'
    });

    jQuery('#resultLoading .bg').css({
        'background':'#000000',
        'opacity':'0.7',
        'width':'100%',
        'height':'100%',
        'position':'absolute',
        'top':'0'
    });

    jQuery('#resultLoading>div:first').css({
        'width': '250px',
        'height':'75px',
        'text-align': 'center',
        'position': 'fixed',
        'top':'0',
        'left':'0',
        'right':'0',
        'bottom':'0',
        'margin':'auto',
        'font-size':'16px',
        'z-index':'10',
        'color':'transparent'
    });

    jQuery('#resultLoading .bg').height('100%');
    jQuery('#resultLoading').fadeIn(300);
    jQuery('body').css('cursor', 'wait');
}

/**
 * loading function stop
 */
function ajaxindicatorstop()
{
    jQuery('#resultLoading .bg').height('100%');
    jQuery('#resultLoading').fadeOut(300);
    jQuery('body').css('cursor', 'default');
}


/**
 * get datatables lang
 */

function getDataTablesLang(baseUrl, lang){
	var url = baseUrl + '/css/datatables/lang/';

	if(lang.toLowerCase() === 'en')
		url += 'English.json';
	else if(lang.toLowerCase() === 'fr')
		url += 'French.json';
	else if(lang.toLowerCase() === 'es')
		url += 'Spanish.json';
	else if(lang.toLowerCase() === 'pr')
		url += 'Portuguese.json';
	else if(lang.toLowerCase() === 'jn')
		url += 'Japanese.json';
	else if(lang.toLowerCase() === 'de')
		url += 'German.json';
	else if(lang.toLowerCase() === 'it')
		url += 'Italian.json';
	else
		url += 'English.json';

	return url;
}
