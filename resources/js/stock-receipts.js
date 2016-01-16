/**
 * Created by pmathew on 1/15/16.
 */

// the combo store
var receiptAmtComboBoxStore = new Ext.data.SimpleStore({
    fields: [ "value" ],
    data: getReceiptAmts()
});

function getReceiptAmts() {
    var arr = [];
    for (var i = 0; i <= 30; i++) {
        arr.push([i*1000]);
    }
    return arr;
}

function buildFuelReceiptsPanel(title, record) {
    var tankReceiptStore = buildTankReceiptStore(bunkCache.infos.todayDate);
    //Tank ReceiptAmt combobox
    var receiptAmtCombo = new Ext.form.ComboBox({
        store: receiptAmtComboBoxStore,
        valueField: "value",
        displayField: "value",
        editable: false,
        listeners: {
            select: {
                fn: function(c, r, eopts) {
                    c.ownerCt.completeEdit();
                }
            }
        }
    });

    var tankPanelConfig = {
        xtype: 'gridpanel',
        id:'tankReceiptGrid',
        autoscroll: true,
        height: 120,
        //renderTo: Ext.getBody(),
        //region: 'center',
        store: tankReceiptStore,
        //multiSelect: true, // Delete this if you only need single row selection
        //stateful: true,
        forceFit: true,
        loadMask: true,
        //stateId: 'stateGrid',
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
                var receiptAmt = e.record.data.receiptAmt;
                if (receiptAmt && receiptAmt >0) {
                    e.record.set("afterReceiptStock", receiptAmt + e.record.data.closingStock);
                } else {
                    e.record.set("afterReceiptStock", e.record.data.closingStock);
                }
                updateFuelReceiptAmt();
                return true;
            }
        },
        columns:[{
            id: 'tankName',
            text: "Tank Name",
            dataIndex: 'tankName',
            flex: 2,
            sortable: true
        },{
            text: "Product",
            dataIndex: 'productType',
            flex: 2,
            //align: 'right',
            sortable: true
        },{
            id: 'openingStock',
            text: "Opening Stock",
            dataIndex: 'closingStock',
            flex: 1,
            sortable: false
        },{
            text: "Receipt in Liters",
            dataIndex: 'receiptAmt',
            flex: 1,
            sortable: false,
            editor: receiptAmtCombo,
            renderer: editableColumnRenderer
        },{
            id: 'afterReceiptStock',
            text: "Stock after receipt",
            dataIndex: 'afterReceiptStock',
            flex: 1,
            sortable: false
        }]
    };

    var panelItems = [tankPanelConfig];


    return buildReceiptsPanel(title, record, 'FUEL_PRODUCTS', panelItems, 120, false, "FUEL");
}

function buildLubesReceiptsPanel(title, record) {
    return buildReceiptsPanel(title, record, 'OIL_PRODUCTS', [], 250, true, "LUBES");
}

function buildBatteryWaterReceiptsPanel(title, record) {
    return buildReceiptsPanel(title, record, 'ADDITIONAL_PRODUCTS', [], 250, true, "ADD_PRODS");
}

function buildReceiptsPanel(title, record, prodType, panelItems, prodsPanelHeight, editableReceiptAmt, onSaveType) {
    var fuelProdsReceiptStore = buildAvailableProductList(prodType);
    var receiptAmtField;
    var receiptAmtRenderer;
    if(editableReceiptAmt) {
        receiptAmtField = {
            xtype: 'numberfield',
            allowNegative: false,
            allowBlank: false
        };
        receiptAmtRenderer=editableColumnRenderer;

    }

    panelItems.push({
        xtype: 'gridpanel',
        id:'fuelReceiptGrid',
        autoscroll: true,
        height: prodsPanelHeight,
        //renderTo: Ext.getBody(),
        //region: 'center',
        store: fuelProdsReceiptStore,
        //multiSelect: true, // Delete this if you only need single row selection
        //stateful: true,
        forceFit: true,
        loadMask: true,
        //stateId: 'stateGrid',
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
            text: "Product",
            dataIndex: 'productName',
            flex: 2,
            sortable: true
        },{
            text: "Selling price",
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
    });

    panelItems.push({
        xtype:'fieldset',
        defaults: {anchor: '100%',height:'30px'},
        layout: 'hbox',
        title:'Test',
        //width:'100%',
        height:'50px',
        defaultType: 'textfield',
        items: [
            {
                id:'invNo-field',
                fieldLabel: 'Invoice no.',
                name: 'invNo',
                flex:1,
                allowBlank:false,
                fieldStyle : 'background-color:#ffffe5 !important; background-image:none !important;'
            },{
                id:'totalAmt-field',
                fieldLabel: 'Total amount',
                flex:1,
                name: 'totalAmt',
                readOnly: true
            },{
                id:'totalLiters-field',
                fieldLabel: 'Total receipt',
                name: 'totalLiters',
                flex:1,
                readOnly: true,
                allowBlank:false
            }
        ]
    });

    var mainContent = Ext.create('Ext.form.Panel', {
        id:'fuelRecieptPanel',
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
        items: panelItems,
        buttons: [{
            text: 'Save',
            handler: function () {
                onReceiptSave(onSaveType);
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

function onReceiptSave(onSaveType) {
    validateFuelReciept();
    saveFuelReciept(onSaveType);
}

function validateFuelReciept() {
    var errors = [];
    var warnings = [];
    if(!Ext.isNumeric(Ext.getCmp('totalLiters-field').getValue()) || Ext.getCmp('totalLiters-field').getValue() <=0) {
        errors.push('No product receipt mentioned');
    }
    if(Ext.getCmp('invNo-field').getValue().length <=0 ) {
        errors.push('Invoice number is missing.');
    }
    if(errors.length > 0) {
        Ext.MessageBox.alert('Error', prepareErrorMsg("Please fix the below validation errors.", errors));
        return false;
    }
    return true;
}

function saveFuelReciept(onSaveType) {
    if(!validateFuelReciept()) {
        return;
    }
    var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Saving receipt..."});
    myMask.show();
    //Prepare object
    var stockReceipt = {
        invoiceNo : Ext.getCmp('invNo-field').getValue(),
        invoiceAmt : Ext.getCmp('totalAmt-field').getValue(),
        type : onSaveType,
        totalLoad : Ext.getCmp('totalLiters-field').getValue()
    };

    //Add tank transactions
    var tankGrid = Ext.getCmp('tankReceiptGrid');
    if(tankGrid) {
        var tankReceipts = [];
        var tankStore = tankGrid.store;
        tankStore.each(function(record) {
            if(record.data.receiptAmt >0 ) {
                tankReceipts.push({
                    tankId:record.data.tankId,
                    receiptAmt:record.data.receiptAmt
                });
            }
        });
        stockReceipt['tankReceipts'] = tankReceipts;
    }

    //Add fuels transactions
    var fuelReceipts = [];
    var fuelStore = Ext.getCmp('fuelReceiptGrid').store;
    fuelStore.each(function(record) {
        if(record.data.receiptAmt >0 ) {
            fuelReceipts.push({
                productId:record.data.productId,
                margin:record.data.marginPerUnit,
                receiptAmt:record.data.receiptAmt
            });
        }
    });
    stockReceipt['fuelReceipts'] = fuelReceipts;

    //Save
    Ext.Ajax.request({
        url: 'api/stockReceipt',
        method: 'POST',
        jsonData:stockReceipt,
        success: function() {
            myMask.hide();
            Ext.MessageBox.alert('Success', "Successfully saved the stock receipt.");
            updateSummaryPanel();
        },
        failure : function(response) {
            myMask.hide();
            Ext.Msg.alert("Error", "Failed to save the stock receipt. Please try again. <br/> " + response.responseText);
        }
    });
}

function updateMargin(record) {
    var costOnInv = record.data.costOnInv;
    if(record.data.receiptAmt >0) {
        if (costOnInv >=0) {
            record.set("marginPerUnit", (record.data.unitSellingPrice - (record.data.costOnInv / record.data.receiptAmt)).toFixed(2)); //Here you can set the value
        }
    } else {
        record.set("marginPerUnit", 0);

    }

}

function getGroupedReceiptAmtFromTankGrid() {
    var groupedReceiptAmt;
    var tankGrid = Ext.getCmp('tankReceiptGrid');
    if(tankGrid) {
        groupedReceiptAmt = {};
        var tankStore = Ext.getCmp('tankReceiptGrid').store;
        for (var i=0; i<tankStore.data.items.length;i++) {
            if(tankStore.data.items[i].data.receiptAmt && tankStore.data.items[i].data.receiptAmt > 0) {
                if(groupedReceiptAmt[tankStore.data.items[i].data.productType]) {
                    groupedReceiptAmt[tankStore.data.items[i].data.productType] =
                        groupedReceiptAmt[tankStore.data.items[i].data.productType] + tankStore.data.items[i].data.receiptAmt;
                } else {
                    groupedReceiptAmt[tankStore.data.items[i].data.productType] = tankStore.data.items[i].data.receiptAmt;
                }
            }
        }
    }
    return groupedReceiptAmt;
}

function updateFuelReceiptAmt() {
    var fuelStore = Ext.getCmp('fuelReceiptGrid').store;
    //Group receipt amt by product
    var groupedReceiptAmt = getGroupedReceiptAmtFromTankGrid();
    var totalInLiters = 0;
    var totalCost = 0;

    fuelStore.each(function(record) {
        if(groupedReceiptAmt) {
            if(groupedReceiptAmt[record.data.productName]) {
                record.set('receiptAmt', groupedReceiptAmt[record.data.productName]);

            } else {
                record.set('receiptAmt', 0);
                totalCost = totalCost + record.data.costOnInv;
            }
        }
        //Clear negative receiptAmt
        if(record.data.receiptAmt <=0) {
            record.set('receiptAmt', 0);
        }

        //Clear negative costOnInv
        if(record.data.costOnInv <=0) {
            record.set('costOnInv', 0);
        }

        //Clear costOnInv if the receiptAmt is not mentioned
        if(record.data.receiptAmt <=0) {
            record.set('costOnInv', 0);
        }

        totalCost = totalCost + record.data.costOnInv;
        totalInLiters = totalInLiters + record.data.receiptAmt;

        updateMargin(record);
    });
    Ext.getCmp('totalLiters-field').setValue(totalInLiters);
    Ext.getCmp('totalAmt-field').setValue(totalCost);

}
