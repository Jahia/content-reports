/**
 * Created by Juan Carlos Rodas on 11/08/2016.
 */

/* function for url action creation */
// pathCol = number of the column to transform the path into a link
/* severalLinesCol = numbers of the columns to display on several lines, cell format should be ["line1","line2","line3"]
  */
function initDataTable (id, url, pathCol = -1, multiLineCols = [], orderEnabled = true, disabledSortingCols = []) {

    // getting the table
    console.log($('#'+id));
    var table = $('#'+id).DataTable();
    table.destroy();
    var table = $('#'+id).DataTable({
        "serverSide": true,
        "processing": true,
        "paging": true,
        "searching": false,
        "ordering": orderEnabled,
        "dom": 'Blfrtip',
        "orderable": true,
        "columnDefs": [
            {
                "render": function ( data, type, row ) {
                    if (pathCol == -1) {
                        return data;
                    } else if (data == null) {
                        return "";
                    } else {
                        if(row.indexOf("nodePresentOnPage")!== -1){
                            return "<a target=\"_blank\" href=\""+$('#baseEdit').val()+data+".html\">"+data+"</a>";
                        }else{
                            let value = data.substring(("/sites/"+$('#siteKey').val()).length, data.length);
                            return "<a target=\"_blank\" href=\""+$('#contentManagerUrl').val()+value+"\">"+data+"</a>";
                        }
                    }
                },
                "targets": pathCol
            },
            {
                "render": function ( data, type, row ) {
                    var array = JSON.parse(data);
                    var arrayLength = array.length;
                    var multiLine = array[0];
                    for (var i = 1; i < arrayLength; i++) {
                        multiLine = multiLine.concat("<br/>", array[i]);
                    }
                    return multiLine;
                },
                "targets": multiLineCols
            },
            {
                "orderable": false,
                "targets": disabledSortingCols
            }
        ],
        "buttons": [
            'csv'
        ],
        "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, 1000] ],
        "pageLength": 10,
        "ajax": {
            "type": "GET",
            "url": url,
            "dataType": "json",
            "contentType": 'application/json; charset=utf-8',
            "complete": function(response) {
            }
        }
    });
}

function initDataTableWithoutAjax (id, pathCol = -1, orderEnabled = true, disabledSortingCols = []) {
    // getting the table
    console.log($('#'+id));
    var table = $('#'+id).DataTable();
    table.destroy();
    return $('#'+id).DataTable( {

        "searching": false,
        "processing": true,
        "ordering": orderEnabled,
        "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, 1000] ],
        "columnDefs": [
            {
                "render": function ( data, type, row ) {
                    if (pathCol == -1) {
                        return data;
                    } else if (data == null) {
                        return "";
                    } else {
                        return "<a target=\"_blank\" href=\""+$('#baseEdit').val()+data+".html\">"+data+"</a>";
                    }
                },
                "targets": pathCol
            }
        ],
        "pagingType": "full_numbers",
        "dom": 'Blfrtip',
        buttons: [
            'csv'
        ]

    } );

}
function getReportActionUrl(baseUrl, reportId, parameters){
    return  baseUrl + '.contentReport.do?reportId=' + reportId + ((parameters==null || parameters=='') ? '' : '&' + parameters);
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



function fillReportByAllDateAndAuthor(baseUrl, gridLabel, totalLabel, loadingLabel){

    var pathTxt = $('#pathTxtRDADA').val();

    if(pathTxt=='null' || pathTxt==''){
        $('#pathTxtRDADA').removeClass("valid").addClass("invalid");
        return;
    }
    else{
        $('#pathTxtRDADA').removeClass("invalid");
    }

    // the loading message

    var typeAuthor = $("input[name='typeAuthorRDA']:checked").val();
    var typeSearch = $("input[name='typeOfSearch']:checked").val();
    var typeDateSearch = $("input[name='typeDateSearch']:checked").val();
    var typeAuthorSearch = $("input[name='typeAuthorSearch']:checked").val();
    var dateBegin = $("input[name='dateBegin']").val();
    var dateEnd = $("input[name='dateEnd']").val();
    var searchAuthor = $("input[name='searchAuthor']:checked").val();
    var searchUsername = $("input[name='searchUsername']").val();
    if (searchAuthor == "on") { searchAuthor = true;} else  { searchAuthor = false; }
    var searchByDate = $("input[name='searchByDate']:checked").val();
    if (searchByDate == "on") { searchByDate = true;} else  { searchByDate = false; }
    var parameters = "&typeAuthor=" + typeAuthor + "&pathTxt='" + pathTxt + "'&typeSearch=" + typeSearch + "&searchByDate=" + searchByDate + "&typeDateSearch=" + typeDateSearch
        + "&dateEnd=" + dateEnd + "&dateBegin=" + dateBegin + "&searchAuthor=" + searchAuthor + "&searchUsername=" + searchUsername + "&typeAuthorSearch=" + typeAuthorSearch;


    if (searchAuthor && searchUsername == '') {
        $('#searchUsername').removeClass("valid").addClass("invalid");
        return;
    } else {
        $('#searchUsername').removeClass("valid").removeClass("invalid");
    }

    ajaxindicatorstart(loadingLabel);

    var actionUrl = getReportActionUrl(baseUrl, 20, parameters);

 //   $.getJSON( actionUrl, function( data ) {



        // setting the table name
        table = initDataTable("byAllDateAndAuthorTable", actionUrl, -1, [], true, [0,1,2,5,6])




        //stop loading message
        ajaxindicatorstop();
}

function fillReportByUntranslated(baseUrl, gridLabel, totalLabel, loadingLabel){
    var pathTxt = $('#pathTxtRDADA').val();
    if(pathTxt=='null' || pathTxt==''){
        $('#pathTxtRDAU').removeClass("valid").addClass("invalid");
        return;
    }
    else{
        $('#pathTxtRDAU').removeClass("invalid");
    }

    // the loading message
    ajaxindicatorstart(loadingLabel);

    var selectLanguageBU = $("#selectLanguageBU").val();
    var selectTypeSearch = $("input[name='typeOfSearchU']:checked").val();

    var parameters = "&pathTxt='" + pathTxt + "&selectLanguageBU="+selectLanguageBU + "&selectTypeSearch=" + selectTypeSearch + "&pathTxt=" + pathTxt;
    console.log("log: " +selectLanguageBU)
    var actionUrl = getReportActionUrl(baseUrl, 21, parameters);

    $.getJSON( actionUrl, function( data ) {



        // setting the table name
        $('#rba-principal-grid-rdau').html(gridLabel);

        // getting the table
        var table =  initDataTableWithoutAjax('byAllUntranslated', -1 , true); // $('#byAllUntranslated').DataTable();

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        $.each(data.items, function( index, val ) {

            table.row.add( [
                data.items[index].title,
                checkUndefined(data.items[index].path),
                checkUndefined(data.items[index].type),
                checkUndefined(data.items[index].date),
                checkUndefined(data.items[index].created)
            ] ).draw();
        });

        // Update footer
        $(table.column(0).footer()).html(totalLabel);
        $(table.column(1).footer()).html(checkUndefined(data.totalContent));


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
            ((currentItemsReportByStatus[id].items[index].lock) === 'true' ? yesLabel : noLabel)
        ] ).draw();
    });
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
            table.row.add( [
                        data.items[index].name,
                        checkUndefined(data.items[index].langTitleOrText),
                        capitalize(data.items[index].type),
                        checkUndefined(data.items[index].created),
                        checkUndefined(data.items[index].lastModified),
                        checkUndefined(data.items[index].lastModifiedBy),
                        ((data.items[index].published) === 'true' ? yesLabel : noLabel),
                        ((data.items[index].lock) === 'true' ? yesLabel : noLabel)
            ] ).draw();
        });

    });
}

/********************************
 *  REPORTS PAGES WITHOUT TITLE *
 ********************************/

function fillReportPageWithoutTitle(baseUrl, labelLoading, labelInsertTitle){
    var actionUrl = getReportActionUrl(baseUrl, 10, null);

    // the loading message
    ajaxindicatorstart(labelLoading);

    table = initDataTable("pageWithoutTitleTable", actionUrl, 0, [], false, [])
    ajaxindicatorstop();

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

/***********************************
 *  REPORTS PAGES WITHOUT KEYWORDS *
 ***********************************/

function fillReportPageWithoutKeywords(baseUrl, loadingLabel, labelAddKeywords){
    var actionUrl = getReportActionUrl(baseUrl, 11, null);

    // the loading message
    // the loading message
    ajaxindicatorstart(loadingLabel);

    initDataTable ("pageWithoutKeywordsTable", actionUrl, 1, [], true, [1]);

    ajaxindicatorstop();

}


/**************************************
 *  REPORTS PAGES WITHOUT DESCRIPTION *
 **************************************/

function fillReportPageWithoutDescription(baseUrl, loadingLabel, labelInsertDescription){
    var actionUrl = getReportActionUrl(baseUrl, 12, null);

    // the loading message
    ajaxindicatorstart(loadingLabel);

    initDataTable ("pageWithoutDescriptionTable", actionUrl, 0, [], false);

    ajaxindicatorstop();
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


    initDataTable ("lockedContentTable", actionUrl, 4, [], true, [0,4]);

    ajaxindicatorstop();

}

/**************************************
 *       REPORTS WIP CONTENT       *
 **************************************/

function fillReportWipContent(baseUrl, loadingLabel){
    var pathTxt = $('#pathTxt').val();
    var typeSearch = $("input[name='typeOfSearch']:checked").val();
    var parameters = "&pathTxt=" + pathTxt + "&typeSearch=" + typeSearch;
    var actionUrl = getReportActionUrl(baseUrl, 22, parameters);

    // the loading message
    ajaxindicatorstart(loadingLabel);


    initDataTable ("wipContentTable", actionUrl, 4, [2], true, [2,4]);

    ajaxindicatorstop();

}

/********************************
 *  REPORTS CONTENT MARKED FOR DELETION *
 ********************************/

function fillReportContentMarkedForDeletion(baseUrl, gridLabel, totalLabel, loadingLabel){
    let pathTxt = $('#pathTxtMFD').val();
    let typeSearch = $("input[name='typeOfSearch']:checked").val();
    let parameters = "pathTxt=" + pathTxt + "&typeSearch=" + typeSearch;
    let actionUrl = getReportActionUrl(baseUrl, 24, parameters);

    // the loading message
    ajaxindicatorstart(loadingLabel);

    table = initDataTable("contentMarkedForDeletion", actionUrl, 3, [], true, [3,4])
    ajaxindicatorstop();

}

/**********************************************
 *       REPORTS DISPLAYED LINKS CONTENT      *
 **********************************************/

function fillReportDisplayLinks(baseUrl, loadingLabel, totalLabel){
    var pathTxtOrigin = $('#pathTxtOrigin').val();
    var pathTxtDestination = $('#pathTxtDestination').val();
    var parameters = "&pathTxtOrigin=" + pathTxtOrigin + "&pathTxtDestination=" + pathTxtDestination;
    var actionUrl = getReportActionUrl(baseUrl, 23, parameters);

    // the loading message
    ajaxindicatorstart(loadingLabel);

    $.getJSON( actionUrl, function( data ) {
        var table =  initDataTableWithoutAjax('displayLinksTable', 4 , true);

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        $.each(data.data, function( index, val ) {
            table.row.add( [
                checkUndefined(val[0]),
                checkUndefined(val[1]),
                checkUndefined(val[2]),
                checkUndefined(val[3]),
                checkUndefined(val[4])
            ] ).draw();
        });

        // Update footer
        $(table.column(0).footer()).html(totalLabel);
        $(table.column(1).footer()).html(checkUndefined(data.recordsTotal));
    });

    ajaxindicatorstop();
}

/***************************************
 * REPORTS WAITING PUBLICATION CONTENT *
 ***************************************/
function fillReportContentWaitingPublication(baseUrl, labelToday, labelYesterday, labelDaysAgo, loadingLabel){
    var actionUrl = getReportActionUrl(baseUrl, 16, null);

    // the loading message
    ajaxindicatorstart(loadingLabel);

    initDataTable ("waitingContentTable", actionUrl, -1, [], false );

    ajaxindicatorstop();

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



function fillReportCustomCacheContent(baseUrl, loadingLabel){
    var actionUrl = getReportActionUrl(baseUrl, 18, null);

    // the loading message
    ajaxindicatorstart(loadingLabel);

    initDataTable ("customCacheContentTable", actionUrl, 3, [], true, [0,3]);

    ajaxindicatorstop();

}



/*****************************************
 *  REPORTS PAGES ACL INHERITANCE BREAK  *
 *****************************************/

function fillReportPageAclInheritanceBreak(baseUrl, loadingLabel){
    ajaxindicatorstart();
    $.ajax($('#contextPath').val() +'/modules/graphql',{
        type:"POST",
        data: JSON.stringify(getAceAndAclInheritanceBreakRequest()),
        contentType:"application/json",
        dataType:"json",
        success: function(response){
            var table = $('#pageAclInheritanceBreakTable').DataTable();

            table.clear().draw();

            let result = response.data.jcr.acl.nodes.map(node => node.parent);
            result = result.concat(response.data.jcr.ace.nodes.map(node => node.parent.parent));


            // Distinct all entries
            result.filter((node, index, list) => {
                return node.isDisplayableNode && list.map(nodeToMap => nodeToMap.path).indexOf(node.path) === index;
            }).forEach(value =>  table.row.add([value.displayName, "<a target=\"_blank\" href=\""+$('#baseEdit').val()+value.path+".html\">"+value.path+"</a>"]).draw());

            ajaxindicatorstop();
        }
    });
}

/************************************************************
 *  REPORTS PAGES Live contents with visibility conditions  *
 ************************************************************/
function fillReportPageByLiveContentsWithVisibilityConditions(baseUrl) {
    const searchPath = $('#searchPath').val();
    const parameters = "&searchPath=" + searchPath
    const actionUrl = getReportActionUrl(baseUrl, 25, parameters);

    // the loading message
    ajaxindicatorstart("Loading reports");
    $.getJSON( actionUrl, function( data ) {
        // getting the table
        let table = $('#liveContentWithVisibilityConditionsContentTable').DataTable();

        // clear all content from table
        table.clear().draw();

        // adding new content to table
        let nodes = data.items;

        // adding new content to table
        $.each(nodes, function( index, node ) {
            table.row.add( [
                checkUndefined(node.name),
                checkUndefined(node.path),
                checkUndefined(node.type),
                checkUndefined(node.listOfConditions),
                checkUndefined(node.isConditionMatched),
                checkUndefined(node.currentStatus),
            ] ).draw();
        });

        //stop loading message
        ajaxindicatorstop();
    });
}

function getAceAndAclInheritanceBreakRequest() {
    let query = {
        query:
            'query aclAndAceQueries($aceQuery:String!, $aclQuery:String!){ jcr {' +
            '   ace:nodesByQuery(query: $aceQuery) { ' +
            '       nodes {' +
            '           parent {' +
            '               parent {' +
            '                   displayName' +
            '                   path' +
            '                   isDisplayableNode' +
            '               }' +
            '           }' +
            '       }' +
            '   }' +
            '   acl:nodesByQuery(query: $aclQuery) { ' +
            '       nodes {' +
            '           parent {' +
            '               displayName' +
            '               path' +
            '               isDisplayableNode' +
            '           }' +
            '       }' +
            '   }' +
            '}}',
        variables: {
            'aclQuery': 'SELECT * FROM [jnt:acl] AS item ' +
                '   WHERE item.[\'j:inherit\'] =\'false\' ' +
                '   AND item.[\'jcr:primaryType\'] <> \'jnt:virtualsite\' ' +
                '   AND ISDESCENDANTNODE(item,[\'/sites/' + $('#siteKey').val() + '\'])',
            'aceQuery': 'SELECT * FROM [jnt:ace] AS item ' +
                '   WHERE item.[\'j:aceType\'] = \'DENY\' ' +
                '   AND item.[\'jcr:primaryType\'] <> \'jnt:virtualsite\' ' +
                '   AND ISDESCENDANTNODE(item,[\'/sites/' + $('#siteKey').val() + '\'])'
        }
    };
    return query;
}