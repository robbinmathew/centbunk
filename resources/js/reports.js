/**
 * Created by pmathew on 4/18/16.
 */

function buildReportsPanel(title, record) {
    var maxDate = parseDateText(bunkCache.infos.todayDateText);
    //Previous day
    maxDate.setDate(maxDate.getDate()-1);
    var minDate = parseDateText(bunkCache.infos.firstDateText);
    var partyStore = buildPartyListStore('ALL');
    var prodStore = buildAvailableProductsStore("ALL", 'ProductStatus');
    var mainContent = Ext.create('Ext.form.Panel', {
        id:'reportsPanel',
        title: title,
        frame:true,
        autoScroll:true,
        bodyStyle:'padding:0px 0px 0',
        width: '100%',
        fieldDefaults: {
            msgTarget: 'side',
            labelWidth: 75
        },
        defaultType: 'textfield',
        defaults: {
            anchor: '100%'
        },
        items: [{
            xtype:'fieldset',
            //defaults: {anchor: '100%',height:'30px'},
            layout: {
                type: 'table',
                columns: 4
            },
            title:'Select report type',
            width:'100%',
            defaultType: 'textfield',
            fieldDefaults: {
                msgTarget: 'side',
                labelSeparator : ' :',
                markgins: '5 10 5 50',
                labelWidth: 150
            },
            items: [
                {
                    rowspan:1,
                    colspan:4,
                    xtype: 'radiogroup',
                    fieldLabel: 'Report type',
                    id:"reportType",
                    // Arrange radio buttons into two columns, distributed vertically
                    columns: 1,
                    vertical: true,
                    items: [
                        { boxLabel: 'Daily Statement', name: 'reportTypeGroup', inputValue: 'DAILY_STMT', checked: true},
                        { boxLabel: 'Credit Status', name: 'reportTypeGroup', inputValue: 'CREDIT_STATUS'},
                        //{ boxLabel: 'Cash Summary', name: 'reportTypeGroup', inputValue: 'CASH_SUMMARY'},
                        { boxLabel: 'Monthly summary', name: 'reportTypeGroup', inputValue: 'MONTHLY_SUMMARY'},
                        { boxLabel: 'Monthly salary statement', name: 'reportTypeGroup', inputValue: 'MONTHLY_SAL_STMT'},
                        { boxLabel: 'Party statement (Complete)', name: 'reportTypeGroup', inputValue: 'PARTY_COMPLETE_STMT'},
                        { boxLabel: 'Party statement (Credit alone)', name: 'reportTypeGroup', inputValue: 'PARTY_CREDIT_STMT'},
                        { boxLabel: 'Stock Status', name: 'reportTypeGroup', inputValue: 'STOCK_STATUS'},
                        { boxLabel: 'Stock receipts', name: 'reportTypeGroup', inputValue: 'STOCK_RECEIPTS'},
                        { boxLabel: 'Prod sales', name: 'reportTypeGroup', inputValue: 'PROD_SALES'},
                        { boxLabel: 'Daily sales report (Products)', name: 'reportTypeGroup', inputValue: 'DSR_STMT'},
                        //{ boxLabel: 'Tank sales', name: 'reportTypeGroup', inputValue: 'TANK_SALES'},
                        { boxLabel: 'Product sales statement', name: 'reportTypeGroup', inputValue: 'PROD_SALES_STMT'}
                    ],
                    listeners: {
                        change: function(radiogroup, radio) {
                            if (radio.reportTypeGroup == "DAILY_STMT") {
                                updateComponents(false, false, false);
                            } else if (radio.reportTypeGroup == "CREDIT_STATUS") {
                                updateComponents(false, false, false);
                            } else if (radio.reportTypeGroup == "STOCK_STATUS") {
                                updateComponents(false, false, false);
                            } else if (radio.reportTypeGroup == "MONTHLY_SUMMARY") {
                                updateComponents(true, false, false);
                            } else if (radio.reportTypeGroup == "CASH_SUMMARY") {
                                updateComponents(false, false, false);
                            } else if (radio.reportTypeGroup == "MONTHLY_SAL_STMT") {
                                updateComponents(true, false, false);
                            } else if (radio.reportTypeGroup == "PARTY_COMPLETE_STMT") {
                                updateComponents(true, true, false);
                            } else if (radio.reportTypeGroup == "PARTY_CREDIT_STMT") {
                                updateComponents(true, true, false);
                            } else if (radio.reportTypeGroup == "STOCK_RECEIPTS") {
                                updateComponents(true, false, false);
                            } else if (radio.reportTypeGroup == "PROD_SALES") {
                                updateComponents(true, false, true);
                            } else if (radio.reportTypeGroup == "DSR_STMT") {
                                updateComponents(true, false, true);
                            } else if (radio.reportTypeGroup == "TANK_SALES") {
                                updateComponents(true, false, false);
                            } else if (radio.reportTypeGroup == "PROD_SALES_STMT") {
                                updateComponents(true, false, false);
                            };
                        }
                    }
                },{
                    rowspan:1,
                    colspan:4,
                    id: 'partyDropDown',
                    xtype: 'combobox',
                    fieldLabel: 'Party Id',
                    store: partyStore,
                    displayField: 'name',
                    valueField: 'id',
                    editable: false,
                    labelWidth: 100,
                    margin: '0 10 10 10',
                    width: 300
                },{
                    rowspan:1,
                    colspan:4,
                    id: 'prodDropDown',
                    xtype: 'combobox',
                    fieldLabel: 'Product Id',
                    store: prodStore,
                    displayField: 'productName',
                    valueField: 'productId',
                    editable: false,
                    labelWidth: 100,
                    margin: '0 10 10 10',
                    width: 300
                },{
                    rowspan:1,
                    colspan:2,
                    xtype: 'label',
                    text: 'Date'
                },{
                    rowspan:1,
                    colspan:2,
                    xtype: 'label',
                    id: 'toDateLabel',
                    text: 'To date'
                },{
                    rowspan:1,
                    colspan:2,
                    xtype: 'datepicker',
                    id: 'startDate',
                    maxDate: maxDate,
                    minDate: minDate,
                    value: maxDate,
                    margins: '105 100 500 100',
                    showToday : false
                },{
                    rowspan:1,
                    colspan:2,
                    xtype: 'datepicker',
                    id: 'toDate',
                    maxDate: maxDate,
                    minDate: minDate,
                    value: maxDate,
                    showToday : false
                }, {
                    rowspan:1,
                    colspan:4,
                    xtype: 'button',
                    text: 'Show report',
                    handler: function () {
                        onShowReport();
                    }
                }
            ]
        }]
    });
    updateComponents(false, false, false ); //Update components to the default report selected.
    return mainContent;
}

function updateComponents(withToDate, withPartyDropDown, withProdDropDown ) {
    if(withToDate === true) {
        Ext.getCmp("toDate").enable();
        Ext.getCmp("toDateLabel").enable();
    } else {
        Ext.getCmp("toDate").disable();
        Ext.getCmp("toDateLabel").disable();
    }

    if(withPartyDropDown === true) {
        Ext.getCmp("partyDropDown").enable();
    } else {
        Ext.getCmp("partyDropDown").disable();
    }
    if(withProdDropDown === true) {
        Ext.getCmp("prodDropDown").enable();
    } else {
        Ext.getCmp("prodDropDown").disable();
    }

}

function onShowReport() {
    var selectedDate = Ext.getCmp('startDate').getValue();
    var reportType = Ext.getCmp('reportType').getValue();

    var url = '/api/report?type=' + reportType.reportTypeGroup + '&format=image&dateText=' + dateToSimpleText(selectedDate);
    if(Ext.getCmp("toDate").disabled === false) {
        var toDate = Ext.getCmp('toDate').getValue();
        if (toDate < selectedDate) {
            Ext.MessageBox.alert('Error', "'To date' is before 'Date'. Please correct.");
            return;
        }
        url = url + "&toDateText=" + dateToSimpleText(toDate);
    }

    if(Ext.getCmp("partyDropDown").disabled === false) {
        var id = Ext.getCmp('partyDropDown').getValue();
        if (id == undefined || id == "") {
            Ext.MessageBox.alert('Error', "Please select a party");
            return;
        }
        url = url + "&id=" + id;
    }

    if(Ext.getCmp("prodDropDown").disabled === false) {
        var id = Ext.getCmp('prodDropDown').getValue();
        if (id == undefined || id == "") {
            Ext.MessageBox.alert('Error', "Please select a product");
            return;
        }
        if(reportType.reportTypeGroup == "DSR_STMT" && id > 4) {
            Ext.MessageBox.alert('Error', "This report is available only for fuels. Please select a fuel product");
            return;
        }
        url = url + "&id=" + id;
    }

    var reportWindowHeight = Ext.getBody().getViewSize().height-100;
    var reportWindowWidth = Ext.getBody().getViewSize().width-100;

    var image = Ext.create('Ext.Img', {
        src: url,
        listeners : {
            load : {
                element : 'el',  //the rendered img element
                fn : setimg
            }
        }
    });

    function setimg() {
        if(reportWindowWidth > image.getEl().dom.naturalWidth) {
            image.getEl().dom.height = image.getEl().dom.naturalHeight;
            image.getEl().dom.width = image.getEl().dom.naturalWidth;
            var window = Ext.getCmp('report-window');
            window.setWidth(image.getEl().dom.naturalWidth);
            window.doLayout();
            window.center();
        } else {
            var minimumWidth = (reportWindowWidth<560) ? 560: reportWindowWidth;
            var ratio = minimumWidth/image.getEl().dom.naturalWidth;
            image.getEl().dom.height = image.getEl().dom.naturalHeight * ratio;
            image.getEl().dom.width = image.getEl().dom.naturalWidth * ratio;
        }
    }

    //url
    var popUp = Ext.create('Ext.window.Window', {
        //header:false,
        height: reportWindowHeight,
        width:  reportWindowWidth,
        modal:false,
        id:'report-window',
        layout: 'vbox',
        anchor:"100% 95%",
        //autoScroll:true,
        title:'Report',
        maximizable: false,
        minimizable: false,
        items:[{
            //layout:'fit',
            margins: '0 0 0 0',
            width:'100%',
            height:(reportWindowHeight - 65),
            autoScroll:true,
            items : [image]
        },{
            xtype: 'button',
            text: 'Close Window',
            width: '100%',
            height: 30,
            cls: 'close-button',
            handler: function(){
                var win = Ext.WindowManager.getActive();
                if (win) {
                    win.close();
                }
            }
        }]
    });
    //popUp.add({html: '<iframe height="' + (reportWindowHeight - 65) + '", width="' + (reportWindowWidth - 5) + '" src="'+ url +'"></iframe>'});
    //popUp.add({html:'<img src="'+ url +'" height="' + (reportWindowHeight - 65) + '", width="' + (reportWindowWidth - 5) + '" />'});
    /*popUp.add({
        xtype: 'button',
        text: 'Close Window',
        width: '100%',
        height: 30,
        cls: 'close-button',
        handler: function(){
            var win = Ext.WindowManager.getActive();
            if (win) {
                win.close();
            }
        }
    });*/
    popUp.show();
}