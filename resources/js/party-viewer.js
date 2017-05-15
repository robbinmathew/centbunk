/**
 * Created by pmathew on 5/12/17.
 */

var partyViewerWindowName = "party-viewer";

function showPartyViewer(title, record) {
    //ignore if already shown.
    var partyViewerPopup = Ext.getCmp(partyViewerWindowName);
    if (partyViewerPopup) {
        //Bring to front
        partyViewerPopup.toFront();
        partyViewerPopup.doLayout();
        partyViewerPopup.center();
        return;
    }
    var myMask = new Ext.LoadMask(Ext.getBody(), {msg: "Loading..."});
    myMask.show();
    buildAllPartyResultStores('ALL_PARTIES', myMask, "buildPartyViewerPopup");
}

function buildPartyViewerPopup(stores, myMask) {

    addEmptyRecords(stores[0], 10300); //Credit parties
    addEmptyRecords(stores[1], 10000); //Employees
    //addEmptyRecords(stores[2], 10250); //Banks

    var statusCombo = new Ext.form.ComboBox({
        store: partyStatusComboBoxStore,
        valueField: "value",
        displayField: "value",
        editable: false,
        listeners: {
            select: {
                fn: function (comboField, r, eopts) {
                    comboField.ownerCt.completeEdit();
                    var gridRecord = comboField.ownerCt.floatParent.getSelectionModel().getSelection()[0];
                }
            }
        }
    });

    var windowHeight = Ext.getBody().getViewSize().height-100;
    var windowWidth = Ext.getBody().getViewSize().width-100;

    //url
    var popUp = Ext.create('Ext.window.Window', {
        //header:false,
        height: windowHeight,
        width:  windowWidth,
        modal:false,
        id:partyViewerWindowName,
        layout: 'vbox',
        anchor:"100% 95%",
        //autoScroll:true,
        title:'Add/Edit Parties',
        maximizable: false,
        minimizable: false,
        autoscroll: true,
        overflowY: 'scroll',
        listeners: {
            close: function (n) {
                onPartyViewerClose();
                return false;
            }
        },
        items:[getGrid(stores[0], "Credit parties", "party-viewer-partiesGrid", statusCombo),
            getGrid(stores[1], "Employees", "party-viewer-empGrid", statusCombo),
            {
                xtype:'fieldset',
                layout: 'hbox',
                defaultType: 'textfield',
                width:'100%',
                items: [
                    {
                        xtype: 'button',
                        text: 'Update',
                        flex:.5,
                        margins: '10 100 10 100', //top, right, bottom, left
                        handler: function () {
                            onPartyDataUpdate();
                        }
                    }
                ]
            }]
    });
    popUp.show();
    myMask.hide();
    return null;
}

function getGrid(store, title, id, statusCombo) {
    return {
        xtype: 'fieldset',
            defaults: {anchor: '100%', height: '30px'},
        layout: 'fit',
        title: title,
        //autoscroll: true,
        width:'100%',
        overflowY: 'scroll',
        height: 'auto',
        defaultType: 'textfield',
        items: [{
        xtype: 'gridpanel',
        id: id,
        autoscroll: true,
        height: 'auto',
        store: store,
        viewConfig: {
            stripeRows: true
        },
        plugins: [
            Ext.create('Ext.grid.plugin.CellEditing', {
                clicksToEdit: 1,
                listeners: {
                    'beforeedit': function(editor, e, eOpts) {
                        var allowed = true;
                        if(e.record.data.partyStatus && e.record.data.partyStatus.endsWith("_S") ) {
                            allowed = false;
                            Ext.Msg.alert("Info", "This record is not editable");
                        }

                        if (e.colIdx == 4 && e.record.data.balance != 0) {
                            allowed = false;
                            Ext.Msg.alert("Info", "Unable to inactivate record " + e.record.data.partyName + " with outstanding balance");
                        }

                        return allowed;
                    },
                    'edit': function(editor, e, eOpts) {
                        if (!e.record.data.partyStatus || e.record.data.partyStatus == '') {
                            e.record.set("partyStatus", "ACTIVE");
                            e.record.set("balance", 0);
                        }
                    }
                }
            })
        ],
        columns: [{
            text: "Party",
            dataIndex: 'partyName',
            flex: 2,
            sortable: false,
            field: {
                allowBlank: false
            },
            renderer: editableColumnRenderer
        }, {
            text: "Detail",
            dataIndex: 'partyDetail',
            flex: 1,
            sortable: false,
            field: {
                allowBlank: true
            },
            renderer: editableColumnRenderer
        }, {
            text: "Phone",
            dataIndex: 'partyPhone',
            flex: 1,
            sortable: false,
            field: {
                allowBlank: true
            },
            renderer: editableColumnRenderer
        }, {
            text: "Balance",
            dataIndex: 'balance',
            flex: 1,
            sortable: false
        }, {
            text: "Status",
            dataIndex: 'partyStatus',
            flex: 1,
            sortable: false,
            editor: statusCombo,
            renderer: editableColumnRenderer
        }]
    }]
    };
}

function onPartyViewerClose() {
    //Update Daily stmt grid if they are available
    var partyCombo = Ext.getCmp("partyCombo");
    if (partyCombo) {
        partyCombo.store.reload();
    }
    var empCombo = Ext.getCmp("empCombo");
    if (empCombo) {
        empCombo.store.reload();
    }
}

function addEmptyRecords(store, newIdStart) {
    //All new ids are above 10000
    for (var i=0; i<5;i++) {
        var newParty = new PartyResult();
        newParty.set('partyId', newIdStart + store.getCount() );
        newParty.set('balance', 0 );
        store.add(newParty);
    }
}

// the combo store
var partyStatusComboBoxStore = new Ext.data.ArrayStore({
    fields: [ "value" ],
    data: [['ACTIVE'], ['INACTIVE']]
});

function onPartyDataUpdate() {
    validatePartyRecords( function() {
        savePartyRecords();
    });
}

function validatePartyRecords(callback) {
    var errors = [];
    var warnings = [];
    validatePartyStore(Ext.getCmp('party-viewer-partiesGrid').store, "Credit party", errors, warnings);
    validatePartyStore(Ext.getCmp('party-viewer-empGrid').store, "Employee", errors, warnings);
    checkErrorsWarningsAndProceed(errors, warnings, callback);
    return true;
}

function validatePartyStore(store, label, errors, warnings) {
    var knownNames = [];
    store.each(function(record) {
        if((!record.data.partyName || record.data.partyName.length <=0) && record.data.partyId < 10000) {
            errors.push("Name is missing for a credit party record");
        }
        if (record.data.partyName && record.data.partyName.length >0) {
            var repeated = false;
            for (i = 0; i<knownNames.length ; i++) {
                var name = knownNames[i];
                if (name == record.data.partyName.toUpperCase()) {
                    repeated = true;
                    errors.push("Name " + name + " is repeated in " + label);
                }
            };
            if (!repeated) {
                knownNames.push(record.data.partyName.toUpperCase());
            }
        }
    });
}

function savePartyRecords() {
    var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Saving receipt..."});
    myMask.show();
    //Prepare object
    var employees = [];
    var parties = [];

    addPartyStoreRecords(Ext.getCmp('party-viewer-partiesGrid').store, parties);
    addPartyStoreRecords(Ext.getCmp('party-viewer-empGrid').store, employees);

    var data = {employees: employees, parties: parties};

    //Save
    Ext.Ajax.request({
        url: 'api/savePartyDetails',
        method: 'POST',
        jsonData:data,
        success: function() {
            myMask.hide();
            //Ext.MessageBox.alert('Success', "Successfully updated party records.");
            var window = Ext.getCmp(partyViewerWindowName);
            if (window) {
                window.close();
            }
        },
        failure : function(response) {
            myMask.hide();
            Ext.Msg.alert("Error", "Failed to update party records. Please try again. <br/> " + response.responseText);
        }
    });
}

function addPartyStoreRecords(store, data) {
    store.each(function(record) {
        if (record.data.partyName && record.data.partyName.length >0) {
            //Include only records with names
            data.push({
                partyId: record.data.partyId,
                partyName: record.data.partyName,
                partyDetail: record.data.partyDetail,
                partyPhone: record.data.partyPhone,
                partyStatus: record.data.partyStatus
            })
        };
    });
}