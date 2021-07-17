/**
 * Created by pmathew on 5/12/17.
 */


function showAdminPanel(title, record) {

    var mainContent = Ext.create('Ext.form.Panel', {
        id:'adminPanel',
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
            width: '100%',
            layout: {
                type: 'table',
                width: '100%',
                columns: 4
            },
            defaultType: 'textfield',
            fieldDefaults: {
                msgTarget: 'side',
                labelSeparator : ' :',
                margins: '10 10 10 0',
                labelWidth: 100
            },
            items: [
                    {
                        rowspan:1,
                        colspan:4,
                        xtype: 'radiogroup',
                        fieldLabel: 'Data type',
                        id:"admindatatype",
                        // Arrange radio buttons into two columns, distributed vertically
                        columns: 1,
                        vertical: true,
                        items: [
                            { boxLabel: 'Price change', name: 'reportTypeGroup', inputValue: 'PRI', checked: true},
                            //{ boxLabel: 'HPCL Transactions', name: 'reportTypeGroup', inputValue: 'HPCL_TRN'},
                            //{ boxLabel: 'Bank Transactions', name: 'reportTypeGroup', inputValue: 'BNK_TRN'},
                            { boxLabel: 'Stock receipt', name: 'reportTypeGroup', inputValue: 'REC'}
                        ],
                        listeners: {
                            change: function(radiogroup, radio) {
                                if (radio.reportTypeGroup == "PRI") {
                                    updateComponents(false, false, false);
                                } else if (radio.reportTypeGroup == "HPCL_TRN") {
                                    updateComponents(false, false, false);
                                } else if (radio.reportTypeGroup == "BNK_TRN") {
                                    updateComponents(false, false, false);
                                } else if (radio.reportTypeGroup == "REC") {
                                    updateComponents(true, false, false);
                                };
                            }
                        }
                    },
                    {
                        xtype: 'numberfield',
                        anchor: '100%',
                        name: 'lastdays',
                        fieldLabel: 'Last days',
                        value: 30,
                        minValue: 5,
                        rowspan:1,
                        colspan:2
                    }
                    , {
                         rowspan:1,
                         colspan:2,
                         xtype: 'button',
                         text: 'Fetch data from HPCL',
                         handler: function () {
                             loadData();
                         }
                     },
                     {
                       xtype     : 'label',
                       fieldLabel: 'Current Data',
                       rowspan:1,
                       colspan:4,
                 },
                 {
                      xtype     : 'textareafield',
                      grow      : true,
                      name      : 'currentData',
                      fieldLabel: 'Current Data',
                      rowspan:1,
                      height: '700',
                      width: '100%',
                      labelWidth: '50',
                      hideLabel : true,
                      colspan:4,
                      anchor    : '100%'
                },{
                      xtype     : 'textareafield',
                      grow      : true,
                      id      : 'newAdminScanData',
                      fieldLabel: 'New Data',
                      height: '700',
                      width: '100%',
                      rowspan:1,
                      colspan:4,
                      anchor    : '100%'
                },{
                    xtype: 'button',
                    rowspan:1,
                    colspan:1,
                    margins: '10 200 5 0',
                    text: 'Save data',
                    handler: function () {
                            saveData();
                    }
                }
            ]
        }]
    });
    return mainContent;
}

function loadData() {
    var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Fetching data from HPCL..."});
    var reportType = Ext.getCmp('admindatatype').getValue();

    var url = '/api/scanData?types=' + reportType.reportTypeGroup + '&fromDays=0';
    //Save
        Ext.Ajax.request({
            url: url,
            method: 'GET',
            success: function(response) {
                myMask.hide();
                Ext.MessageBox.alert('Success', "Successfully fetched data from HPCL. <br/>" + response.responseText);
            },
            failure : function(response) {
                myMask.hide();
                Ext.Msg.alert("Error", "Failed to fetch data from HPCL. Please try again. <br/> " + response.responseText);
            }
        });
}

function saveData() {
    var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Saving records..."});
    myMask.show();
    var newData = Ext.getCmp('newAdminScanData').getValue();
    var reportType = Ext.getCmp('admindatatype').getValue();

    var url = '/api/saveScanData?type=' + reportType.reportTypeGroup;

    //Save
    Ext.Ajax.request({
        url: url,
        method: 'POST',
        jsonData:newData,
        success: function(response) {
            myMask.hide();
            Ext.MessageBox.alert('Success', "Successfully updated the records. <br/>" + response.responseText);
            Ext.getCmp('newAdminScanData').setValue("");
        },
        failure : function(response) {
            myMask.hide();
            Ext.Msg.alert("Error", "Failed to update party records. Please try again. <br/> " + response.responseText);
        }
    });
}