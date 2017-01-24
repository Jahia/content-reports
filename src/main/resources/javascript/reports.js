/**
 * Created by Juan Carlos Rodas on 11/08/2016.
 */


/* function for url action creation */
function getReportActionUrl(baseUrl, reportId, parameters){
    return  baseUrl + '.GovernorReport.do?reportId=' + reportId + ((parameters==null || parameters=='') ? '' : '&' + parameters);
}


/************************
 *  REPORTS BY AUTHOR   *
 ************************/

/* items for th report by author */
var currentItemsReportByAuthor;
var byAuthorChart;

function fillReportByAuthor(baseUrl, gridLabel, gridDetailsLabel, gridPagesTitle, totalLabel, yesLabel, noLabel, createdLabel, labelLoading){

    var pathTxt = $('#pathTxt').val();
        if(pathTxt=='null' || pathTxt==''){
            $('#pathTxt').removeClass("valid").addClass("invalid");
            return;
        }
        else{
            $('#pathTxt').removeClass("invalid");
        }

    // the loading message
    ajaxindicatorstart(labelLoading);

    var typeSearch = $("input[name='typeSearch']:checked").val();
    var typeAuthor = $("input[name='typeAuthor']:checked").val();
    var parameters = "typeSearch=" + typeSearch + "&typeAuthor=" + typeAuthor + "&pathTxt='" + pathTxt + "'";

    var actionUrl = getReportActionUrl(baseUrl, 1, parameters);

    $.getJSON( actionUrl, function( data ) {
        currentItemsReportByAuthor = data.items;

        var charLabels = [];
        var chartData= [];

        // setting the table name
        $('#rba-principal-grid').html(gridLabel + '&nbsp(' + data.reportType + ')');
        $('#column-ContentAuthor').html(capitalize(data.reportType.toLowerCase()) + "&nbsp;" + capitalize(createdLabel.toLowerCase()));

        // getting the table
        var table = $('#byAuthorTable').DataTable();

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        $.each(data.items, function( index, val ) {
            charLabels.push(data.items[index].user)
            chartData.push(data.items[index].itemCount)
            table.row.add( [
                "<a href='#' onclick=\"fillReportByAuthorDetail(" + index + ", '" + gridDetailsLabel + "' , '" +  totalLabel + "')\">" + data.items[index].user + "</a>",
                "<a href='#' onclick=\"fillReportByAuthorPages(" + index + ", '" + yesLabel + "', '" + noLabel + "', '" + gridPagesTitle + "')\">" + data.items[index].itemCount + "</a>",
                data.items[index].percentaje.toFixed(2),
            ] ).draw();
        });

        // Update footer
        $(table.column(0).footer()).html(totalLabel);
        $(table.column(1).footer()).html(checkUndefined(data.totalItems));
        $(table.column(2).footer()).html("100.00");


        // drawing the chart
        drawReportByAuthorChart(charLabels, chartData);

        //stop loading message
        ajaxindicatorstop();
    });
}


function fillReportByAuthorDetail(id, gridBaseTitle, totalLabel){
    var gridTitle = currentItemsReportByAuthor[id].user;

    // setting the table name
    $('#rba-secondary-grid-detail').html(gridBaseTitle + '&nbsp' + gridTitle);

    // open the window modal for the author grid
    setTimeout(function(){
      $('#authorDetailModel').modal({
        show: 'true'
      });
    }, 40);


    // getting the table
    var table = $('#byAuthorDetailTable').DataTable();

    // clear all content from table
    table.clear().draw();

    // adding new content to table
    $.each(currentItemsReportByAuthor[id].itemAuthorDetails.items, function( index, val ) {
        table.row.add( [
            capitalize(currentItemsReportByAuthor[id].itemAuthorDetails.items[index].typeName) + "&nbsp(" + currentItemsReportByAuthor[id].itemAuthorDetails.items[index].type + ")",
            currentItemsReportByAuthor[id].itemAuthorDetails.items[index].itemCount,
            currentItemsReportByAuthor[id].itemAuthorDetails.items[index].percentaje.toFixed(2)
        ] ).draw();
    });

    // Update footer
    $(table.column(0).footer()).html(totalLabel);
    $(table.column(1).footer()).html(checkUndefined(currentItemsReportByAuthor[id].itemAuthorDetails.totalCount));
    $(table.column(2).footer()).html("100.00");
}


function fillReportByAuthorPages(id, yesLabel, noLabel, gridPagesTitle) {
    var gridTitle = currentItemsReportByAuthor[id].user;
    var items = [];

    // setting the table name
    $('#rba-secondary-grid-page').html(gridPagesTitle + '&nbsp' + gridTitle);

    // open the window modal for the pages grid
    $('#authorPageModel').modal({
        show: 'true'
    });

    // getting the table
    var table = $('#byAuthorPageTable').DataTable();

    // clear all content from table
    table.clear().draw();

    // adding new content to table
    $.each(currentItemsReportByAuthor[id].itemAuthorPageDetails.items, function( index, val ) {
        table.row.add( [
            currentItemsReportByAuthor[id].itemAuthorPageDetails.items[index].jcrtitle,
            capitalize(currentItemsReportByAuthor[id].itemAuthorPageDetails.items[index].typeName) + "&nbsp(" + currentItemsReportByAuthor[id].itemAuthorPageDetails.items[index].type + ")",
            formatedDateFromString(currentItemsReportByAuthor[id].itemAuthorPageDetails.items[index].created),
            formatedDateFromString(currentItemsReportByAuthor[id].itemAuthorPageDetails.items[index].modified),
            ((currentItemsReportByAuthor[id].itemAuthorPageDetails.items[index].published) === 'true' ? yesLabel : noLabel),
            ((currentItemsReportByAuthor[id].itemAuthorPageDetails.items[index].locked) === 'true' ? yesLabel : noLabel)
        ] ).draw();
    });
}

function drawReportByAuthorChart(labelArray, dataArray) {

    var barChartData = {
        labels: labelArray,
        datasets: [{
            label:'Total',
            backgroundColor: "#ECEFF1",
            data: dataArray
        }]
    };

    if(byAuthorChart!=null){
        byAuthorChart.destroy();
    }

    var ctx = document.getElementById("canvas").getContext("2d");
    byAuthorChart = new Chart(ctx, {
        type: 'bar',
        data: barChartData,
        options: {
            elements: {
                rectangle: {
                    borderWidth: 0,
                    borderColor: 'transparent',
                    borderSkipped: 'bottom'
                }
            },
            legend: {
              display: false
            },
            responsive: true,
            scales: {
                yAxes: [{
                    display: true,
                    ticks: {
                        beginAtZero: true,
                        steps: 10,
                        stepValue: 5
                    }
                }]
            }
        }
    });

    //window.myBar.update();
}


/************************
 *  REPORTS BY DATE   *
 ************************/

/* items for th report by all date */
//var currentItemsReportByAllDate;
var byAllDateChart;
var beforeDateChart;

function fillReportByAllDate(baseUrl, gridLabel, totalLabel, loadingLabel){

    var pathTxt = $('#pathTxtRDA').val();
    if(pathTxt=='null' || pathTxt==''){
        $('#pathTxtRDA').removeClass("valid").addClass("invalid");
        return;
    }
    else{
        $('#pathTxtRDA').removeClass("invalid");
    }

    // the loading message
    ajaxindicatorstart(loadingLabel);

    var typeAuthor = $("input[name='typeAuthorRDA']:checked").val();
    var parameters = "&typeAuthor=" + typeAuthor + "&pathTxt='" + pathTxt + "'";

    var actionUrl = getReportActionUrl(baseUrl, 2, parameters);

    $.getJSON( actionUrl, function( data ) {

        var charLabels = [];
        var chartData= [];
        var chartData2= [];

        // setting the table name
        $('#rba-principal-grid-rda').html(gridLabel + '&nbsp(' + typeAuthor + ')');

        // getting the table
        var table = $('#byAllDateTable').DataTable();

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        $.each(data.items, function( index, val ) {
            charLabels.push(data.items[index].year + ' ' + data.items[index].month);
            chartData.push(data.items[index].pages);
            chartData2.push(data.items[index].content);
            table.row.add( [
                data.items[index].year + " " + data.items[index].month,
                checkUndefined(data.items[index].pages),
                checkUndefined(data.items[index].content)
            ] ).draw();
        });

        // Update footer
        $(table.column(0).footer()).html(totalLabel);
        $(table.column(1).footer()).html(checkUndefined(data.totalPages));
        $(table.column(2).footer()).html(checkUndefined(data.totalContent));


        // drawing the chart
        drawReportByAllDateChart(charLabels, chartData, chartData2);

        //stop loading message
        ajaxindicatorstop();
    });
}

function drawReportByAllDateChart(labelArray, dataArray, dataArray2) {

    var barChartData = {
        labels: labelArray,
        datasets: [{
            label:'Pages',
            backgroundColor: "#ECEFF1",
            data: dataArray
        },
        {
            label:'Content',
            backgroundColor: "#9FA8DA",
            data: dataArray2
        }]
    };

    if(byAllDateChart!=null){
        byAllDateChart.destroy();
    }

    var ctx = document.getElementById("canvasReportAllDate").getContext("2d");
    byAllDateChart = new Chart(ctx, {
        type: 'bar',
        data: barChartData,
        options: {
            elements: {
                rectangle: {
                    borderWidth: 0,
                    borderColor: 'transparent',
                    borderSkipped: 'bottom'
                }
            },
            legend: {
              display: false
            },
            responsive: true,
            scales: {
                yAxes: [{
                    display: true,
                    ticks: {
                        beginAtZero: true,
                        steps: 10,
                        stepValue: 5
                    }
                }]
            }
        }
    });

}


function fillReportBeforeDate(baseUrl, labelLoading){

    var selectedDate = $('#dateRDB').val();
    if(selectedDate=='null' || selectedDate==''){
        $('#dateRDB').removeClass("valid").addClass("invalid");
        return;
    }else{
        $('#dateRDB').removeClass("invalid");
    }

    var pathTxt = $('#pathTxtRDB').val();
    if(pathTxt=='null' || pathTxt==''){
        $('#pathTxtRDB').removeClass("valid").addClass("invalid");
        return;
    }else{
        $('#pathTxtRDB').removeClass("invalid");
    }

    // the loading message
    ajaxindicatorstart(labelLoading);

    var typeAuthor = $("input[name='typeAuthorRDB']:checked").val();
    var parameters = "&date='" + selectedDate + "'&pathTxt='" + pathTxt + "'";
    var actionUrl = getReportActionUrl(baseUrl, 3, parameters);

    $.getJSON( actionUrl, function( data ) {
        var items = [];
        var charLabels = data.chartLabels;
        var chartData= data.chartValues;

        // getting the table
        var table = $('#beforeDateTable').DataTable();

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        $.each(data.items, function( index, val ) {
            table.row.add( [
                data.items[index].date,
                data.items[index].nodeName,
                capitalize(data.items[index].typeName) + "&nbsp(" + data.items[index].type + ")"
            ] ).draw();
        });

        // drawing the chart
        drawReportBeforeDateChart(charLabels, chartData);

        //stop loading message
        ajaxindicatorstop();
    });
}


function drawReportBeforeDateChart(labelArray, dataArray) {

    var barChartData = {
        labels: labelArray,
        datasets: [{
            label:'Content',
            backgroundColor: "#ECEFF1",
            data: dataArray
        }]
    };

    if(byAllDateChart!=null){
        byAllDateChart.destroy();
    }

    var ctx = document.getElementById("canvasReportBeforeDate").getContext("2d");
    beforeDateChart = new Chart(ctx, {
        type: 'bar',
        data: barChartData,
        options: {
            elements: {
                rectangle: {
                    borderWidth: 0,
                    borderColor: 'transparent',
                    borderSkipped: 'bottom'
                }
            },
            legend: {
              display: false
            },
            responsive: true,
            scales: {
                yAxes: [{
                    display: true,
                    ticks: {
                        beginAtZero: true,
                        steps: 10,
                        stepValue: 5
                    }
                }]
            }
        }
    });
}


/************************
 *  REPORTS BY TYPE   *
 ************************/

var byTypeDetailChart;

function fillReportByType(baseUrl, loadingLabel){

    var pathTxt = $('#pathTxtRBT').val();
    if(pathTxt=='null' || pathTxt==''){
        $('#pathTxtRBT').removeClass("valid").addClass("invalid");
        return;
    }else{
        $('#pathTxtRBT').removeClass("invalid");
    }

    // the loading message
    ajaxindicatorstart(loadingLabel);

    var parameters = "'&pathTxt='" + pathTxt + "'";
    var actionUrl = getReportActionUrl(baseUrl, 4, parameters);

    $.getJSON( actionUrl, function( data ) {

        // getting the table
        var table = $('#byTypeTable').DataTable();

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        $.each(data.items, function( index, val ) {
            table.row.add( [
                capitalize(data.items[index].type),
                data.items[index].techName
            ] ).draw();
        });

        //stop loading message
        ajaxindicatorstop();
    });
}

function fillReportByTypeDetailed(baseUrl, totalLabel, loadingLabel){

    var pathTxt = $('#pathTxtRBTD').val();
    if(pathTxt=='null' || pathTxt==''){
        $('#pathTxtRBTD').removeClass("valid").addClass("invalid");
        return;
    }else{
        $('#pathTxtRBTD').removeClass("invalid");
    }

    // the loading message
    ajaxindicatorstart(loadingLabel);

    var parameters = "'&pathTxt='" + pathTxt + "'";
    var actionUrl = getReportActionUrl(baseUrl, 5, parameters);

    $.getJSON( actionUrl, function( data ) {

        var charLabels = [];
        var chartData= [];

        // getting the table
        var table = $('#byTypeDetailedTable').DataTable();

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        $.each(data.items, function( index, val ) {
            charLabels.push(capitalize(data.items[index].type));
            chartData.push(data.items[index].percentaje.toFixed(2));
            table.row.add( [
                capitalize(data.items[index].type),
                data.items[index].techName,
                data.items[index].itemCount,
                data.items[index].percentaje.toFixed(2)
            ] ).draw();
        });

        // Update footer
        $(table.column(0).footer()).html(totalLabel);
        $(table.column(2).footer()).html(checkUndefined(data.totalItems));
        $(table.column(3).footer()).html("100.00");

        drawReportByTypeDetailedChart(charLabels, chartData);

        //stop loading message
        ajaxindicatorstop();

    });


    function drawReportByTypeDetailedChart(labelArray, dataArray) {

        var pieChartData = {
            labels: labelArray,
            datasets: [{
                label:'Content',
                backgroundColor: [
                    "#FF6384",
                    "#4BC0C0",
                    "#FFCE56",
                    "#E7E9ED",
                    "#36A2EB"
                ],
                hoverBackgroundColor: [
                    "#FF6384",
                    "#4BC0C0",
                    "#FFCE56",
                    "#E7E9ED",
                    "#36A2EB"
                ],
                data: dataArray
            }]
        };

        if(byTypeDetailChart!=null){
            byTypeDetailChart.destroy();
        }

        var ctx = document.getElementById("canvasReportByTypeDetailed").getContext("2d");
        byTypeDetailChart = new Chart(ctx, {
            type: 'pie',
            data: pieChartData,
            options: {
                legend: {
                  display: true,
                  position: 'left'
              }
            }
        });
    }
}

/************************
 *  REPORTS BY STATUS   *
 ************************/

/* items for th report by status */
var currentItemsReportByStatus;

function fillReportByStatus(baseUrl, yesLabel, noLabel, labelLoading, publishLabel){

    var pathTxtByStatus = $('#pathTxtByStatus').val();
    if(pathTxtByStatus=='null' || pathTxtByStatus==''){
        $('#pathTxtByStatus').removeClass("valid").addClass("invalid");
        return;
    }
    else{
        $('#pathTxtByStatus').removeClass("invalid");
    }

    // the loading message
    ajaxindicatorstart(labelLoading);

    var parameters = "pathTxt='" + pathTxtByStatus + "'";
    var actionUrl = getReportActionUrl(baseUrl, 6, parameters);

    $.getJSON( actionUrl, function( data ) {

        // filling the current var array
        currentItemsReportByStatus = data.statusItems;

        // getting the table
        var table = $('#byTypeStatusTable').DataTable();

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        $.each(data.statusItems, function( index, val ) {
            table.row.add( [
                "<a href='#' onclick=\"fillReportByStatusDetail('" + baseUrl + "'," + index + ", '" + yesLabel + "', '" + noLabel + "', '" + labelLoading + "', '" + publishLabel + "')\">" + data.statusItems[index].name + "</a>"
            ] ).draw();
        });

        //stop loading message
        ajaxindicatorstop();

    });
}

function fillReportByStatusDetail(baseUrl, id, yesLabel, noLabel, loadingLabel, publishLabel){

    var name = currentItemsReportByStatus[id].name;

    // setting the table name
    $('#rba-status-grid-detail').html(name);

    // open the window modal for the status grid
    $('#statusDetailModel').modal({
        show: 'true'
    });

    // getting the table
    var table = $('#byStatusDetailTable').DataTable();

    // clear all content from table
    table.clear().draw();

    // adding new content to table
    $.each(currentItemsReportByStatus[id].items, function( index, val ) {
        table.row.add( [
            capitalize(currentItemsReportByStatus[id].items[index].displayTitle),
            capitalize(currentItemsReportByStatus[id].items[index].type),
            currentItemsReportByStatus[id].items[index].language,
            currentItemsReportByStatus[id].items[index].created,
            currentItemsReportByStatus[id].items[index].lastModified,
            currentItemsReportByStatus[id].items[index].lastModifiedBy,
            ((currentItemsReportByStatus[id].items[index].published) === 'true' ? yesLabel : noLabel),
            ((currentItemsReportByStatus[id].items[index].lock) === 'true' ? yesLabel : noLabel),
            "<button type=\"button\" class=\"btn btn-default\"  onclick=\"removeAndReloadStatusDetailGrid(" + id + "," + index + ",'" + baseUrl + "','"+ currentItemsReportByStatus[id].items[index].path +"','" + yesLabel + "','" + noLabel + "','" + loadingLabel + "','" + publishLabel + "')\" >" + publishLabel + "</button>"
        ] ).draw();
    });
}


function removeAndReloadStatusDetailGrid(id, parentIndex, baseUrl, path, yesLabel, noLabel, loadingLabel, publishLabel) {


    publishNode(baseUrl, path, function (param) {
        if (param) {
            var itemCount = currentItemsReportByStatus[id].items.length - 1;
            if (itemCount <= 0) {
                // close the window modal for the status grid
                $('#statusDetailModel').modal('hide');
            } else {
                var table = $('#byStatusDetailTable').DataTable();
                var query = ":eq(" + parentIndex + ")";
                table.row(query).remove().draw( false );
            }
            fillReportByStatus(baseUrl, yesLabel, noLabel, loadingLabel, publishLabel);
        }
    })

}


/************************
 *  REPORTS BY LANGUAGE   *
 ************************/

function fillReportByLanguage(baseUrl, yesLabel, noLabel, labelLoading, publishLabel){
    var actionUrl = getReportActionUrl(baseUrl, 7, null);

    // the loading message
    ajaxindicatorstart(labelLoading);

    $.getJSON( actionUrl, function( data ) {

        // getting the table
        var table = $('#byLanguageTable').DataTable();

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        $.each( data.languageItems, function( index, val ) {
            var canPublish = (data.languageItems[index].availableLive == true ? 'true' : 'false');
            table.row.add( [
                "<a href='#' onclick=\"fillReportByLanguageDetail('" + baseUrl + "', '" + data.languageItems[index].locale + "', '" + yesLabel + "', '" + noLabel + "','" + canPublish + "','" + labelLoading + "','" + publishLabel + "')\">" + capitalize(data.languageItems[index].displayLanguage) + "</a>" ,
                data.languageItems[index].locale,
                (data.languageItems[index].availableEdit == true ? yesLabel : noLabel),
                (data.languageItems[index].availableLive == true ? yesLabel : noLabel)
            ] ).draw();

            //stop loading message
            ajaxindicatorstop();
        });
    });
}


function fillReportByLanguageDetail(baseUrl, language ,yesLabel, noLabel, canPublish, loadingLabel, publishLabel){
    var parameters = "reqLang=" + language ;
    var actionUrl = getReportActionUrl(baseUrl, 8, parameters);

    // setting the table name
    $('#language-detail-grid-page').html(language);

    // open the window modal for the author grid
    $('#languageDetailPageModel').modal({
        show: 'true'
    });

    $.getJSON( actionUrl, function( data ) {

        // getting the table
        var table = $('#byLanguageDetailTable').DataTable();

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        $.each( data.items, function( index, val ) {
            var actionContent = (canPublish == 'true' ?  "<a href=\"#\" onclick=\"removeAndReloadLanguageDetailGrid('" + canPublish + "','" + baseUrl + "','"+ data.items[index].path +"','" + yesLabel + "','" + noLabel + "','" + loadingLabel + "','" + publishLabel + "','" + language + "')\" >[" + publishLabel + "]</a>" : "&nbsp;"  )
            table.row.add( [
                        data.items[index].name,
                        checkUndefined(data.items[index].langTitleOrText),
                        capitalize(data.items[index].type),
                        checkUndefined(data.items[index].created),
                        checkUndefined(data.items[index].lastModified),
                        checkUndefined(data.items[index].lastModifiedBy),
                        ((data.items[index].published) === 'true' ? yesLabel : noLabel),
                        ((data.items[index].lock) === 'true' ? yesLabel : noLabel),
                        actionContent
            ] ).draw();
        });

    });
}



function removeAndReloadLanguageDetailGrid(canPublish, baseUrl, path, yesLabel, noLabel, loadingLabel, publishLabel, language) {

    publishNodeLanguage(baseUrl, path, language, function (param) {
        fillReportByLanguageDetail(baseUrl, language ,yesLabel, noLabel, canPublish, loadingLabel, publishLabel);
    })

}


/********************************
 *  REPORTS PAGES WITHOUT TITLE *
 ********************************/

function fillReportPageWithoutTitle(baseUrl, labelLoading, labelInsertTitle){
    var actionUrl = getReportActionUrl(baseUrl, 10, null);

    // the loading message
    ajaxindicatorstart(labelLoading);

    $.getJSON( actionUrl, function( data ) {
        // getting the table
        var table = $('#pageWithoutTitleTable').DataTable();

        // clear all content from table
        table.clear().draw();

        if(data.items.length == 0){
            //stop loading message
            ajaxindicatorstop();
        }

        // adding new content to table
        $.each( data.items, function( index, val ) {
            var items = [];
            items.push("<a href='" + data.items[index].nodeUrl + "' target='_blank' >" +  data.items[index].nodePath + "</a>");
            for (var k in data.items[index].translations){
                var linkAddTitle =  "<a href=\"#\" onclick=\"openModalSaveTitle('" + data.items[index].nodePath + "','" + data.items[index].translations[k].lang + "')\" >" + labelInsertTitle + "</a>";
                var columnValue = data.items[index].translations[k].value;
                var columnContent = (isEmpty(columnValue) ? linkAddTitle : columnValue);
                items.push(columnContent);
            }

            table.row.add(items).draw();

            //stop loading message
            ajaxindicatorstop();
        });



    });
}

function openModalSaveTitle(path, lang){

    $('#insert-title-modal-title').html(path + '&nbsp;[' + lang + ']');
    $('#input-lang').val(lang);
    $('#input-node-path').val(path);
    $('#input-title').val('');

    // open the window modal for insert title
    $('#insertTitlePageModel').modal({
        show: 'true'
    });
}

function modalSaveTitle(baseUrl, labelLoading, labelInsertTitle){
    var lang = $('#input-lang').val();
    var nodePath = $('#input-node-path').val();
    var titleVal = $('#input-title').val();

    savePropertyNode(baseUrl, nodePath, "jcr:title", titleVal, lang, function (param) {
        if (param) {
            $('#insertTitlePageModel').modal('hide');
            fillReportPageWithoutTitle(baseUrl, labelLoading, labelInsertTitle);
        }
    });

}


/***********************************
 *  REPORTS PAGES WITHOUT KEYWORDS *
 ***********************************/

function fillReportPageWithoutKeywords(baseUrl, loadingLabel, labelAddKeywords){
    var actionUrl = getReportActionUrl(baseUrl, 11, null);

    // the loading message
    ajaxindicatorstart(loadingLabel);

    $.getJSON( actionUrl, function( data ) {
       // console.log(data);

        // getting the table
        var table = $('#pageWithoutKeywordsTable').DataTable();

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        $.each( data.items, function( index, val ) {
            var linkAddKeywords =  "<a href=\"#\" onclick=\"openModalAddKeywords('" + data.items[index].nodePath + "')\" >" + labelAddKeywords + "</a>";
            table.row.add( [
                "<a href='" + data.items[index].nodeUrl + "' target='_blank' >" +  data.items[index].nodeTitle + "</a>" ,
                data.items[index].nodePath,
                linkAddKeywords
            ] ).draw();
        });

        //stop loading message
        ajaxindicatorstop();
    });
}

function openModalAddKeywords(path){
    $('#title-addKeywords-modal').html(path);
    $('#input-node-path-keywords').val(path);

    //clean all content
    sheepItForm.removeAllForms();

    // open the window modal for insert title
    $('#addKeywordsPageModel').modal({
        show: 'true'
    });
}

function modalSaveKeywords(baseUrl, labelLoading, labelAddKeywords){
    var keywords = '';
    var nodePath = $('#input-node-path-keywords').val();


    // Get all forms forms
    var forms = sheepItForm.getForms();

    // build the keywords getting each form
    for (x in forms) {
        // var index = forms[x].getPosition() - 1;
        // var value = $('#sheepItForm_' + index + '_keyword').val();
        var value = $('#sheepItForm_' + x + '_keyword').val();
        if(!isEmpty(value)){
            if(keywords.length > 0) {keywords += ',';}
            keywords += value;
        }
    }

    if(!isEmpty(keywords)){
        saveKeywordsNode(baseUrl, nodePath, keywords, function (param) {
            if (param) {
                $('#addKeywordsPageModel').modal('hide');
                fillReportPageWithoutKeywords(baseUrl, labelLoading, labelAddKeywords);
            }
        });
    }else{
        swal("Error!", "While trying to save the Node [" + nodePath + "]. Keywords not found..", "error");
    }

}


/**************************************
 *  REPORTS PAGES WITHOUT DESCRIPTION *
 **************************************/

function fillReportPageWithoutDescription(baseUrl, loadingLabel, labelInsertDescription){
    var actionUrl = getReportActionUrl(baseUrl, 12, null);

    // the loading message
    ajaxindicatorstart(loadingLabel);

    $.getJSON( actionUrl, function( data ) {
        // getting the table
        var table = $('#pageWithoutDescriptionTable').DataTable();

        // clear all content from table
        table.clear().draw();

        if(data.items.length == 0){
            //stop loading message
            ajaxindicatorstop();
        }

        // adding new content to table
        $.each( data.items, function( index, val ) {
            var items = [];
            items.push("<a href='" + data.items[index].nodeUrl + "' target='_blank' >" +  data.items[index].nodeTitle + "</a>");
            items.push(data.items[index].nodePath);
            for (var k in data.items[index].translations){

                var linkAddDescription =  "<a href=\"#\" onclick=\"openModalSaveDescription('" + data.items[index].nodePath + "','" + data.items[index].translations[k].lang + "')\" >" + labelInsertDescription + "</a>";
                var columnValue = data.items[index].translations[k].value;
                var columnContent = (isEmpty(columnValue) ? linkAddDescription : columnValue);
                items.push(columnContent);
            }

            table.row.add(items).draw();

            //stop loading message
            ajaxindicatorstop();
        });
    });
}

function openModalSaveDescription(path, lang){

    $('#insert-description-modal').html(path + '&nbsp;[' + lang + ']');
    $('#input-lang-description').val(lang);
    $('#input-node-path-description').val(path);
    $('#input-description').val('');

    // open the window modal for insert title
    $('#insertDescriptionPageModel').modal({
        show: 'true'
    });
}


function modalSaveDescription(baseUrl, labelLoading, labelInsertDescription){
    var lang = $('#input-lang-description').val();
    var nodePath = $('#input-node-path-description').val();
    var titleVal = $('#input-description').val();

    savePropertyNode(baseUrl, nodePath, "jcr:description", titleVal, lang, function (param) {
        if (param) {
            $('#insertDescriptionPageModel').modal('hide');
            fillReportPageWithoutDescription(baseUrl, labelLoading, labelInsertDescription);
        }
    });

}


/**************************************
 *  REPORTS CONTENT FROM ANOTHER SITE *
 **************************************/

function fillReportContentFromAnotherSite(baseUrl, loadingLabel){
    var actionUrl = getReportActionUrl(baseUrl, 13, null);

    // the loading message
    ajaxindicatorstart(loadingLabel);

    $.getJSON( actionUrl, function( data ) {
        // getting the table
        var table = $('#contentFromAnotherSiteTable').DataTable();

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        $.each( data.items, function( index, val ) {
            table.row.add( [
                data.items[index].displayTitle,
                data.items[index].nodeType,
                "<a href='" + data.items[index].nodeUsedInPageUrl + "' target='_blank' >" +  data.items[index].nodeUsedInPageDisplayableName + "</a>",
                "<a href='" + data.items[index].sourceSiteUrl + "' target='_blank' >" +  data.items[index].sourceSiteDisplayableName + "</a>"
            ] ).draw();

        });

        //stop loading message
        ajaxindicatorstop();
    });
}



/**************************************
 *       REPORTS ORPHAN CONTENT       *
 **************************************/

function fillReportOrphanContent(baseUrl, loadingLabel){
    var actionUrl = getReportActionUrl(baseUrl, 14, null);

    // the loading message
    ajaxindicatorstart(loadingLabel);

    $.getJSON( actionUrl, function( data ) {
        // getting the table
        var table = $('#orphanContentTable').DataTable();

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        $.each( data.items, function( index, val ) {
            table.row.add( [
                data.items[index].displayTitle,
                capitalize(data.items[index].nodeTypeTechName),
                data.items[index].nodeAuthor,
                "<a href='" + data.items[index].nodeUsedInPageUrl + "' target='_blank' >" +  data.items[index].nodeUsedInPagePath + "</a>"
            ] ).draw();
        });

        //stop loading message
        ajaxindicatorstop();
    });
}


/**************************************
 *       REPORTS LOCKED CONTENT       *
 **************************************/

function fillReportLockedContent(baseUrl, loadingLabel, labelUnlock, labelUnlockAll, labelUnlockQuestion, labelUnlockAllQuestion, labelUnlockYes){
    var actionUrl = getReportActionUrl(baseUrl, 15, null);

    // the loading message
    ajaxindicatorstart(loadingLabel);

    $.getJSON( actionUrl, function( data ) {
        // getting the table
        var table = $('#lockedContentTable').DataTable();

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        $.each( data.items, function( index, val ) {
            table.row.add( [
                data.items[index].displayTitle,
                capitalize(data.items[index].nodeTypeTechName),
                data.items[index].nodeAuthor,
                data.items[index].nodeLockedBy,
                "<a href='" + data.items[index].nodeUsedInPageUrl + "' target='_blank' >" +  data.items[index].nodeUsedInPagePath + "</a>",
                "<a href=\"#\" onclick=\"unlockLockedContent('" + baseUrl + "','" + loadingLabel + "','" + labelUnlockYes + "','" + labelUnlock + "','" + labelUnlockAll + "','" + labelUnlockQuestion + "','" + labelUnlockAllQuestion + "','" + data.items[index].nodePath + "'," + false + ")\" >" + labelUnlock + "</a>" + "&nbsp;" +
                "<a href=\"#\" onclick=\"unlockLockedContent('" + baseUrl + "','" + loadingLabel + "','" + labelUnlockYes + "','" + labelUnlock + "','" + labelUnlockAll + "','" + labelUnlockQuestion + "','" + labelUnlockAllQuestion + "','" + data.items[index].nodePath + "'," + true + ")\" >" + labelUnlockAll + "</a>"
            ] ).draw();
        });

        //stop loading message
        ajaxindicatorstop()
    });
}


function unlockLockedContent(baseUrl, loadingLabel, labelUnlockYes, labelUnlock, labelUnlockAll, labelUnlockQuestion, labelUnlockAllQuestion, nodePath, unlockAll){
     swal({
            //title: "Are you sure to unlock the node?",
            title: (unlockAll ? labelUnlockAllQuestion : labelUnlockQuestion),
            text: "Node [" + nodePath + "].",
            type: "warning",
            showCancelButton: true,
            confirmButtonClass: "btn-danger",
           // confirmButtonText: "Yes, Unlock it!",
            confirmButtonText: labelUnlockYes,
            closeOnConfirm: false
        },
        function(){
            unlockNode(baseUrl, nodePath, unlockAll, function (param) {
                if (param) {
                   // swal("Unlocked!", "The node [" + nodePath + "] has been Unlocked.", "success");
                   // fillReportPageWithoutDescription(baseUrl, labelLoading, labelInsertDescription);
                    fillReportLockedContent(baseUrl, loadingLabel, labelUnlock, labelUnlockAll, labelUnlockQuestion, labelUnlockAllQuestion);
                }
            });

        });

}


/***************************************
 * REPORTS WAITING PUBLICATION CONTENT *
 ***************************************/
function fillReportContentWaitingPublication(baseUrl, labelToday, labelYesterday, labelDaysAgo, loadingLabel){
    var actionUrl = getReportActionUrl(baseUrl, 16, null);

    // the loading message
    ajaxindicatorstart(loadingLabel);

    $.getJSON( actionUrl, function( data ) {
        // getting the table
        var table = $('#waitingContentTable').DataTable();

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        $.each( data.items, function( index, val ) {
            var items = [];
            items.push( data.items[index].nodeTitle);
            items.push(capitalize(data.items[index].nodeType));
            for (var k in data.items[index].locales){
                var difDays = diffDaysBetweenToday(new Date(data.items[index].locales[k].wfStarted));
                var timeLabel = "";
                if(difDays == 0) {timeLabel =  labelToday;}
                if(difDays == 1) {timeLabel =  labelYesterday;}
                if(difDays > 1) {timeLabel =  difDays + " " + labelDaysAgo;}

                items.push(capitalize(timeLabel));
                items.push(data.items[index].locales[k].wfName);
            }
            table.row.add(items).draw();
        });

        //stop loading message
        ajaxindicatorstop();
    });
}


/**************************************
 *           REPORT OVERVIEW          *
 **************************************/
function fillReportOverview(baseUrl, loadingLabel){
    var actionUrl = getReportActionUrl(baseUrl, 17, null);

    // the loading message
    ajaxindicatorstart(loadingLabel);

    $.getJSON( actionUrl, function( data ) {
        // getting the table
        var table = $('#overviewTable').DataTable();

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        table.row.add( [
            data.nbPages,
            data.nbTemplates,
            data.nbUsers
        ] ).draw();

        //stop loading message
        ajaxindicatorstop();
    });
}



/**************************************
 *       REPORTS ORPHAN CONTENT       *
 **************************************/

function fillReportCustomCacheContent(baseUrl, loadingLabel){
    var actionUrl = getReportActionUrl(baseUrl, 18, null);

    // the loading message
    ajaxindicatorstart(loadingLabel);

    $.getJSON( actionUrl, function( data ) {
        // getting the table
        var table = $('#customCacheContentTable').DataTable();

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        $.each( data.items, function( index, val ) {
            table.row.add( [
                data.items[index].displayTitle,
                capitalize(data.items[index].nodeTypeTechName),
                data.items[index].expiration,
                data.items[index].nodeAuthor,
                "<a href='" + data.items[index].nodeUsedInPageUrl + "' target='_blank' >" +  data.items[index].nodeUsedInPagePath + "</a>"
            ] ).draw();
        });

        //stop loading message
        ajaxindicatorstop();
    });
}



/*****************************************
 *  REPORTS PAGES ACL INHERITANCE BREAK  *
 *****************************************/

function fillReportPageAclInheritanceBreak(baseUrl, loadingLabel){
    var actionUrl = getReportActionUrl(baseUrl, 19, null);

    // the loading message
    ajaxindicatorstart(loadingLabel);

    $.getJSON( actionUrl, function( data ) {
        // getting the table
        var table = $('#pageAclInheritanceBreakTable').DataTable();

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        $.each( data.items, function( index, val ) {
            table.row.add( [
                "<a href='" + data.items[index].nodeUrl + "' target='_blank' >" +  data.items[index].nodeTitle + "</a>",
                data.items[index].nodePath
            ] ).draw();
        });

        //stop loading message
        ajaxindicatorstop();
    });
}
