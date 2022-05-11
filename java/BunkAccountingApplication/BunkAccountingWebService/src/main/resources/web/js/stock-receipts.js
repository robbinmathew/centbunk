/**
 * Created by pmathew on 1/15/16.
 */

// the combo store
var receiptAmtComboBoxStore = new Ext.data.SimpleStore({
    fields: [ "value" ],
    data: getReceiptAmts()
});

var stockReceiptLoadMask;

function getReceiptAmts() {
    var arr = [];
    for (var i = 0; i <= 30; i++) {
        arr.push([i*1000]);
        if (i == 4) {
           arr.push([4500]);
        }
        if (i == 13) {
           arr.push([13500]);
        }
    }
    return arr;
}

function buildFuelReceiptsPanel(title, record) {
    stockReceiptLoadMask = new Ext.LoadMask( Ext.getBody(), {
        msg:"Preparing for daily statement...",
        listeners: {
            beforehide: function (loadMask, eOpts) {
                return loadMask.hideMask(loadMask, eOpts);
            }
        }
    });

    stockReceiptLoadMask.show();

    var tankReceiptStore = buildTankReceiptStore(bunkCache.infos.todayDate, stockReceiptLoadMask);
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
        height: 'auto',
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

    var panelItems = getCurrentReceipts();
    panelItems.push(tankPanelConfig);

    return buildReceiptsPanel(title, record, 'FUEL_PRODUCTS', panelItems, false, "FUEL");
}

function buildLubesReceiptsPanel(title, record) {
    return buildReceiptsPanel(title, record, 'OIL_PRODUCTS', getCurrentReceipts(), true, "LUBES");
}

function buildBatteryWaterReceiptsPanel(title, record) {
    return buildReceiptsPanel(title, record, 'ADDITIONAL_PRODUCTS', getCurrentReceipts(), true, "ADD_PRODS");
}

function getCurrentReceipts() {
    var d = new Date();
    var panelItems = [{
                        xtype: 'image',
                        layout:'fit',
                        margins: '0 0 0 0',
                        width:'100%',
                        //height:reportWindowHeight,
                        autoScroll:true,
                        shrinkWrap:true,
                        frame:true,
                        autoEl:"div",
                        overflowY:"auto",overflowX:"auto",
                        src: "/api/report?type=STOCK_RECEIPTS&format=image&dateText="+ bunkCache.infos.todayDateText + "&toDateText=" + bunkCache.infos.todayDateText + "&nocache=" + d.getTime(),
                    }] ;
                    return panelItems;
}

function buildReceiptsPanel(title, record, prodType, panelItems, editableReceiptAmt, onSaveType) {

    var receiptAmtField;
    var receiptAmtRenderer;
    var availableProdsStore;
    var prodReceiptStore;
    var prodNameField;
    if(editableReceiptAmt) {
         prodReceiptStore = buildProdReceiptStore();

        availableProdsStore = buildAvailableProductsStore(prodType, 'ProductStatus', stockReceiptLoadMask);
        availableProdsStore.on('load',function (store, records, successful, eOpts ){
            //Populate the receipt records store
            if(prodReceiptStore) {
                var i = 0;
                store.each(function(record) {
                    var nameExists = false;
                    //Avoid duplicate names.
                    prodReceiptStore.each(function(prodReceiptStoreRecord) {
                        if(prodReceiptStoreRecord.data.productName == record.data.productName) {
                            nameExists = true;
                        }
                    });

                    if(!nameExists) {
                        var newRecord =  new ProdReceipt();
                        newRecord.set("productName", record.data.productName);
                        newRecord.set("productId", record.data.productId);
                        newRecord.set("id", 'existing-prod' + record.data.productId);
                        prodReceiptStore.insert(i++, newRecord);
                    }
                });
            }
        });
        receiptAmtField = numberFieldConfig();
        prodNameField = textFieldConfig();
        receiptAmtRenderer=editableColumnRenderer;
    } else {
        //For Fuel receipt
        prodReceiptStore = buildAvailableProductsStore(prodType, 'ProdReceipt', stockReceiptLoadMask);
    }

    panelItems.push({
        xtype: 'gridpanel',
        id:'fuelReceiptGrid',
        autoscroll: true,
        height: 'auto',
        //renderTo: Ext.getBody(),
        //region: 'center',
        store: prodReceiptStore,
        //multiSelect: true, // Delete this if you only need single row selection
        //stateful: true,
        forceFit: true,
        loadMask: true,
        //stateId: 'stateGrid',
        viewConfig: {
            loadMask: false,
            markDirty:false,
            stripeRows: true
        },
        plugins: [
            Ext.create('Ext.grid.plugin.CellEditing', {
                clicksToEdit: 1
            })
        ],
        listeners : {
            edit: function (editor, e, eOpts) {
                if(e.record.data.receiptAmt <=0 && e.record.data.costOnInv > 0) {
                    Ext.MessageBox.alert('Error', 'No receipt amount mentioned for this product. This wont be saved.');
                }
                updateFuelReceiptAmt();
                if(onSaveType != "FUEL") {
                    assignIdIfExists(e.record);
                }
            }
        },
        columns:[{
            text: "Product",
            dataIndex: 'productName',
            renderer: receiptAmtRenderer,
            flex: 2,
            field: prodNameField,
            sortable: false
        },{
            text: "Selling price",
            dataIndex: 'unitSellingPrice',
            field: receiptAmtField,
            flex: 1,
            //align: 'right',
            renderer: receiptAmtRenderer,
            sortable: false
        },{
            id: 'receiptAmt',
            text: "Quantity",
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
            field: numberFieldConfig(),
            renderer: editableColumnRenderer,
        },{
            id: 'marginPerUnit',
            text: "Margin per unit",
            dataIndex: 'marginPerUnit',
            flex: 1,
            sortable: false
        },{
            text: "Comment",
            dataIndex: 'comment',
            flex: 1,
            sortable: false
        }]
    });

    panelItems.push({
        xtype:'fieldset',
        defaults: {anchor: '100%',height:'30px'},
        layout: 'hbox',
        //title:'Test',
        //width:'100%',
        //height:'50px',
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

    panelItems.push({
        xtype:'fieldset',
        layout: 'hbox',
        defaultType: 'textfield',
        fieldDefaults: {
            msgTarget: 'side',
            labelSeparator : ' :',
            margins: '10 10 10 0',
            labelWidth: 100
        },
        items: [
            {
                xtype: 'button',
                text: 'Save',
                flex:1,
                margins: '10 5 5 200',
                handler: function () {
                    onReceiptSave(onSaveType);
                }
            },{
                xtype: 'button',
                flex:1,
                margins: '10 200 5 0',
                text: 'Clear',
                handler: function () {
                    updateSubPanel(record);

                }
            }
        ]
    });

    if(editableReceiptAmt) {
        panelItems.push({
            xtype: 'fieldset',
            defaults: {anchor: '100%', height: '30px'},
            layout: 'fit',
            title: 'Current Product stock (For reference only)',
            //width:'100%',
            //height:'50px',
            defaultType: 'textfield',
            items: [{
                xtype: 'gridpanel',
                id: 'prodStatusGrid',
                autoscroll: true,
                height: 'auto',
                //renderTo: Ext.getBody(),
                //region: 'center',
                store: availableProdsStore,
                //multiSelect: true, // Delete this if you only need single row selection
                //stateful: true,
                //stateId: 'stateGrid',
                viewConfig: {
                    stripeRows: true
                },
                columns: [{
                    text: "Product",
                    dataIndex: 'productName',
                    flex: 2,
                    sortable: true
                }, {
                    text: "Selling price",
                    dataIndex: 'unitSellingPrice',
                    flex: 1,
                    sortable: true
                }, {
                    text: "Current Stock",
                    dataIndex: 'currentStock',
                    flex: 1,
                    sortable: false
                }, {
                    text: "Margin",
                    dataIndex: 'margin',
                    flex: 1,
                    sortable: false
                }]
            }]
        });
    }

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
        items: panelItems
    });
    return mainContent;
};

function onReceiptSave(onSaveType) {
    validateFuelReciept( function() {
        saveFuelReciept(onSaveType);
    });
}

function validateFuelReciept(callback) {
    var errors = [];
    var warnings = [];
    if(!Ext.isNumeric(Ext.getCmp('totalLiters-field').getValue()) || Ext.getCmp('totalLiters-field').getValue() <=0) {
        errors.push('No product receipt mentioned');
    }
    if(Ext.getCmp('invNo-field').getValue().length <=0 ) {
        errors.push('Invoice number is missing.');
    }

    var fuelStore = Ext.getCmp('fuelReceiptGrid').store;
    fuelStore.each(function(record) {
        if((!record.data.productName || record.data.productName.length <=0) && record.data.receiptAmt>0) {
            errors.push("Product receipt entered without product name.");
        }

        if(record.data.receiptAmt >0 && record.data.marginPerUnit <= 0 ) {
            errors.push("Margin for " + record.data.productName + " is zero. Review the selling price/cost on invoice");
        }
        if(record.data.receiptAmt>0 && record.data.costOnInv <= 0 ) {
            warnings.push("Cost on invoice for " + record.data.productName + " is zero.");
        }
    });
    checkErrorsWarningsAndProceed(errors, warnings, callback);
    return true;
}

function saveFuelReciept(onSaveType) {
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

    //Add fuel/Lube transactions
    var fuelReceipts = [];
    var fuelStore = Ext.getCmp('fuelReceiptGrid').store;
    fuelStore.each(function(record) {
        if(record.data.receiptAmt >0) {
            fuelReceipts.push({
                productId:record.data.productId,
                productName:record.data.productName,
                unitSellingPrice:record.data.unitSellingPrice,
                costOnInv:record.data.costOnInv,
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

function assignIdIfExists(receiptRecord) {
    receiptRecord.set('productName', receiptRecord.data.productName.trim());
    var receiptGridStore = Ext.getCmp('fuelReceiptGrid').store;

    //Move the record to the last used row
    var rowNo = receiptGridStore.indexOf(receiptRecord);
    //Find last used row
    var lastUsedRow = -1;
    receiptGridStore.each(function(record, indx) {
        if(record.data.productName.length > 0 && record !== receiptRecord) {
            lastUsedRow = indx;
        }
    });

    if(rowNo > lastUsedRow + 1) {
        receiptGridStore.remove(receiptRecord);
        receiptGridStore.insert(lastUsedRow+1, receiptRecord);
    }

    if(rowNo >= receiptGridStore.getCount() -1) {
        for (var i=0; i<5;i++) {
            var newLube = new ProdReceipt();
            newLube.set('id', 'new-lube-' + (lastUsedRow + i));
            receiptGridStore.add(newLube);
        }
    }

    //Filter duplicates

    if(!Ext.getCmp('prodStatusGrid') || receiptRecord.data.receiptAmt <=0) {
        receiptRecord.set('comment', '');
        return;
    }

    var prodStatusGridStore = Ext.getCmp('prodStatusGrid').store;

    var prodFound = false;
    prodStatusGridStore.each(function(record) {
        if(record.data.productName.trim() == receiptRecord.data.productName) {
            if( record.data.unitSellingPrice == receiptRecord.data.unitSellingPrice &&
                record.data.margin == receiptRecord.data.marginPerUnit) {
                //Same product found.
                prodFound = true;
                receiptRecord.set('comment', 'Existing product');
                receiptRecord.set('productId', record.data.productId);
            } else {
                prodFound = true;
                receiptRecord.set('comment', 'Price/Margin changed');
                receiptRecord.set('productId', null);
            }
        }
    });

    if(!prodFound) {
        receiptRecord.set('comment', 'New product');
        receiptRecord.set('productId', null);
    }


}

