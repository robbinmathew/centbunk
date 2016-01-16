function buildDailyStatementPanel(title, record) {
    var mainContent = Ext.create('Ext.form.Panel', {
        id:'dailyStmtPanel',
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
            xtype: 'gridpanel',
            id:'fuelReceiptGrid',
            autoscroll: true,
            height: prodsPanelHeight,
            store: fuelProdsReceiptStore,
            forceFit: true,
            loadMask: true,
            viewConfig: {
                stripeRows: true
            },
            plugins: [
                Ext.create('Ext.grid.plugin.CellEditing', {
                    clicksToEdit: 1
                })
            ],
            listeners : {
                edit: function (editor, e, eOpts) {
                    var costOnInv = e.record.data.costOnInv;
                    if(e.record.data.receiptAmt <=0 && costOnInv > 0) {
                        Ext.MessageBox.alert('Error', 'No receipt amount mentioned for this product. This wont be saved.');
                    }
                    updateFuelReceiptAmt();
                }
            },
            columns:[{
                text: "Type",
                dataIndex: 'type',
                flex: 2,
                sortable: true
            },{
                text: "Opening",
                dataIndex: 'unitSellingPrice',
                flex: 1,
                //align: 'right',
                sortable: true
            },{
                id: 'receiptAmt',
                text: "Receipt amount",
                dataIndex: 'receiptAmt',
                flex: 1,
                field: receiptAmtField,
                renderer: receiptAmtRenderer,
                sortable: false
            },{
                id: 'costOnInv',
                text: "Cost on invoice",
                dataIndex: 'costOnInv',
                flex: 1,
                sortable: false,
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
                renderer: editableColumnRenderer,
            },{
                id: 'marginPerUnit',
                text: "Margin per litre",
                dataIndex: 'marginPerUnit',
                flex: 1,
                sortable: false
            }]
        }

        ],
        buttons: [{
            text: 'Save',
            handler: function () {
                onDailyStmtSave();
            }
        },{
            text: 'Submit',
            handler: function () {
                onDailyStmtSubmit();
            }
        },{
            text: 'Clear',
            handler: function () {
                updateSubPanel(record);

            }
        }]
    });
    return mainContent;
};

function onDailyStmtSave() {
    saveFuelReciept(onSaveType);
}

function onDailyStmtSubmit() {
    validateDailyStmt();
    saveDailyStmt(onSaveType);
}

function validateDailyStmt() {
    var errors = [];
    var warnings = [];
    if(errors.length > 0) {
        Ext.MessageBox.alert('Error', prepareErrorMsg("Please fix the below validation errors.", errors));
        return false;
    }
    return true;
}

function saveDailyStmt() {
    if(!validateFuelReciept()) {
        return;
    }
    var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Saving daily statement..."});
    myMask.show();
    //Prepare object
    //Save
    console.log("test");

}