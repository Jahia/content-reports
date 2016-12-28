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
    
    $('.governor-data-table').DataTable( {
        "language": {
            "url": langFileUrl
        },
        "pagingType": "full_numbers",
        dom: 'Bfrtip',
        buttons: [
            'copy', 'csv', 'excel',  'print',
            {
                extend: 'pdfHtml5',
                title: reportTitle,
                message: 'Creation date: ' + output ,
                customize: function ( doc ) {
                    doc.content.splice( 1, 0, {
                        margin: [ 0, 0, 0, 12 ],
                        alignment: 'center',
                        image: 'data:image/png;base64,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'
                    } );
                }
            }
        ]

    } );


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
        jQuery('body').append('<div id="resultLoading" style="display:none"><div><img src="/modules/content-governor/css/images/ajax-loader.gif"><div>'+text+'</div></div><div class="bg"></div></div>');
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
        'color':'#ffffff'

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
 * publish node with specific language.
 */
function publishNodeLanguage(baseUrl, nodePath, nodeLanguage,  cb)
{
    var actionUrl =  baseUrl + '.GovernorJcr.do';
    $.ajax({
        type: "POST",
        url: actionUrl,
        data: "jcrActionId=1&nodeLanguage=" + nodeLanguage + "&jcrNodePath='" + nodePath + "'",
        success: function(msg){
            swal("Published!", "Node [" + nodePath + "].", "success");
            cb(true);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            //alert("Error Publishing node (" + nodePath + ")");
            swal("Publishing Error!", "Node [" + nodePath + "].", "danger");
            cb(false);
        }
    });
}

/**
 * publish node.
 */
function publishNode(baseUrl, nodePath,  cb)
{
    var actionUrl =  baseUrl + '.GovernorJcr.do';
    $.ajax({
        type: "POST",
        url: actionUrl,
        data: "jcrActionId=1&jcrNodePath='" + nodePath + "'",
        success: function(msg){
            swal("Published!", "Node [" + nodePath + "].", "success");
            cb(true);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            swal("Publishing Error!", "Node [" + nodePath + "].", "error");
            cb(false);
        }
    });
}


/**
 * Save property node.
 */
function savePropertyNode(baseUrl, nodePath, propertyName, propertyValue, nodeLanguage, cb)
{
    var actionUrl =  baseUrl + '.GovernorJcr.do';
    $.ajax({
        type: "POST",
        url: actionUrl,
        data: "jcrActionId=2&jcrNodePath='" + nodePath + "'&nodePropertyName=" + propertyName + "&nodePropertyValue=" + propertyValue + "&nodeLanguage=" + nodeLanguage,
        success: function(msg){
            swal("Property [" +  propertyName + "] Saved!", "Node [" + nodePath + "].", "success");
            cb(true);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            swal("Error Saving!", "While trying to save the Node [" + nodePath + "] with Property [" + propertyName + "] .", "error");
            cb(false);
        }
    });
}

/**
 * save keywords node
 */
function saveKeywordsNode(baseUrl, nodePath, keywords, cb)
{
    var actionUrl =  baseUrl + '.GovernorJcr.do';
    $.ajax({
        type: "POST",
        url: actionUrl,
        data: "jcrActionId=3&jcrNodePath='" + nodePath + "'&nodeKeywords='" + keywords + "'",
        success: function(msg){
            swal("Keywords Saved!", "Node [" + nodePath + "].", "success");
            cb(true);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            swal("Error Saving!", "While trying to save the Node [" + nodePath + "] with keywords {" + keywords + "} .", "error");
            cb(false);
        }
    });
}

/**
 * Unlock node
 */
function unlockNode(baseUrl, nodePath, cb)
{
    var actionUrl =  baseUrl + '.GovernorJcr.do';
    $.ajax({
        type: "POST",
        url: actionUrl,
        data: "jcrActionId=4&jcrNodePath='" + nodePath + "'",
        success: function(msg){
            swal("Node Unlocked!", "Path [" + nodePath + "].", "success");
            cb(true);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            swal("Error!", "While trying to unlock the Node [" + nodePath + "].", "error");
            cb(false);
        }
    });
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


