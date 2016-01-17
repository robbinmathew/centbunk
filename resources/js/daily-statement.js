function buildDailyStatementPanel(title, record) {
    var oilProdCombo = new Ext.form.ComboBox({
        store: buildAvailableProductListStore('SECONDARY_PRODUCTS'),
        valueField: "productName",
        displayField: "productName",
        editable: false,
        listeners: {
            select: {
                fn: function(c, r, eopts) {
                    c.ownerCt.completeEdit();
                }
            }
        }
    });

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
            id:'meterSaleGrid',
            autoscroll: true,
            height: 'auto',
            store: buildActiveMeterListStore(),
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
                    onMeterSaleUpdate();
                },
                afterlayout: function () {
                    onMeterSaleUpdate();
                }
            },
            columns:[{
                text: "Meter",
                dataIndex: 'meterName',
                flex: 1,
                sortable: true
            },{
                text: "Opening reading",
                dataIndex: 'finalReading',
                flex: 2,
                //align: 'right',
                sortable: true
            },{
                text: "Closing reading",
                dataIndex: 'closingReading',
                flex: 2,
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
                renderer: editableColumnRenderer,
                sortable: false
            },{
                text: "Total sale",
                dataIndex: 'totalSale',
                flex: 2,
                sortable: false
            },{
                text: "Test sale in liter",
                dataIndex: 'testSale',
                flex: 1,
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
                renderer: editableColumnRenderer,
                sortable: false
            },{
                text: "Actual sale",
                dataIndex: 'actualSale',
                flex: 2,
                sortable: false
            }]
        },{
            xtype: 'gridpanel',
            id:'tankSaleGrid',
            autoscroll: true,
            height: 'auto',
            store: buildTankReceiptStore(bunkCache.infos.todayDate),
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
                    onMeterSaleUpdate();
                }
            },
            columns:[{
                text: "Tank name",
                dataIndex: 'tankName',
                flex: 1,
                sortable: true
            },{
                text: "Product",
                dataIndex: 'productType',
                flex: 1,
                sortable: true
            },{
                text: "Opening stock",
                dataIndex: 'closingStock',
                flex: 2,
                //align: 'right',
                sortable: true
            },{
                text: "Sale",
                dataIndex: 'sale',
                flex: 1,
                sortable: false
            },{
                text: "Stock after sale",
                dataIndex: 'stockAfterSale',
                flex: 2,
                sortable: false
            },{
                text: "Test",
                dataIndex: 'testSale',
                flex: 1,
                sortable: false
            },{
                text: "Dip stock",
                dataIndex: 'dipStock',
                flex: 2,
                sortable: false,
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            },{
                text: "Diff today",
                dataIndex: 'diffToday',
                flex: 2,
                sortable: false
            },{
                text: "Diff this month",
                dataIndex: 'newDiffThisMonth',
                flex: 2,
                sortable: false
            }]
        },{
            xtype: 'gridpanel',
            id:'fuelSaleGrid',
            autoscroll: true,
            height: 'auto',
            store: buildAvailableProductListStore('FUEL_PRODUCTS'),
            forceFit: true,
            loadMask: true,
            viewConfig: {
                stripeRows: true
            },
            columns:[{
                text: "Product",
                dataIndex: 'productName',
                flex: 1,
                sortable: true
            },{
                text: "Price",
                dataIndex: 'unitSellingPrice',
                flex: 1,
                sortable: true
            },{
                text: "Total sale",
                dataIndex: 'totalSale',
                flex: 2,
                //align: 'right',
                sortable: true
            },{
                text: "Total sale amt",
                dataIndex: 'totalSaleAmt',
                flex: 1,
                sortable: false
            },{
                text: "Test sale",
                dataIndex: 'testSale',
                flex: 2,
                sortable: false
            },{
                text: "Test sale amt",
                dataIndex: 'testSaleAmt',
                flex: 1,
                sortable: false
            },{
                text: "Actual sale",
                dataIndex: 'actualSale',
                flex: 2,
                sortable: false
            },{
                text: "Actual sale amt",
                dataIndex: 'actualSaleAmt',
                flex: 2,
                sortable: false
            }]
        },{
            xtype: 'gridpanel',
            id:'oilSaleGrid',
            autoscroll: true,
            height: 'auto',
            store: buildEmptyProductSaleStore(),
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
            columns:[{
                text: "Product",
                dataIndex: 'productName',
                flex: 1,
                sortable: true,
                editor: oilProdCombo,
                renderer: editableColumnRenderer
            },{
                text: "Price",
                dataIndex: 'unitSellingPrice',
                flex: 1,
                sortable: true
            },{
                text: "Margin",
                dataIndex: 'totalSale',
                flex: 2,
                //align: 'right',
                sortable: true
            },{
                text: "Total sale",
                dataIndex: 'actualSaleAmt',
                flex: 1,
                sortable: false
            },{
                text: "Test sale",
                dataIndex: 'testSale',
                flex: 2,
                sortable: false
            },{
                text: "Test sale amt",
                dataIndex: 'testSaleAmt',
                flex: 1,
                sortable: false
            },{
                text: "Actual sale",
                dataIndex: 'actualSale',
                flex: 2,
                sortable: false
            },{
                text: "Actual sale amt",
                dataIndex: 'actualSaleAmt',
                flex: 2,
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

function onMeterSaleUpdate() {
    var meterSaleStore = Ext.getCmp('meterSaleGrid').store;
    //Group amt by product
    var groupedSaleAmtByTankId = {};
    var groupedTestAmtByTankId = {};
    var groupedSaleAmtByProdId = {};
    var groupedTestAmtByProdId = {};

    meterSaleStore.each(function(record) {
        //Update sale, test
        if(record.data.closingReading < record.data.finalReading) {
            //Reset closing
            record.set('closingReading', record.data.finalReading);
        }

        //Update sale
        record.set('totalSale', (record.data.closingReading - record.data.finalReading).toFixed(2));

        //Check test sale, reset if higher than sale
        if(record.data.totalSale < record.data.testSale) {
            //Reset testSale
            record.set('testSale', 0);
        }

        //Update actual sale
        record.set('actualSale', (record.data.totalSale - record.data.testSale).toFixed(2));


        if(groupedSaleAmtByTankId[record.data.tankId]) {
            groupedSaleAmtByTankId[record.data.tankId] = groupedSaleAmtByTankId[record.data.tankId] + record.data.totalSale;
        } else {
            groupedSaleAmtByTankId[record.data.tankId] = record.data.totalSale;
        }

        if(groupedTestAmtByTankId[record.data.tankId]) {
            groupedTestAmtByTankId[record.data.tankId] = groupedTestAmtByTankId[record.data.tankId] + record.data.testSale;
        } else {
            groupedTestAmtByTankId[record.data.tankId] = record.data.testSale;
        }

        if(groupedSaleAmtByProdId[record.data.productId]) {
            groupedSaleAmtByProdId[record.data.productId] = groupedSaleAmtByProdId[record.data.productId] + record.data.totalSale;
        } else {
            groupedSaleAmtByProdId[record.data.productId] = record.data.totalSale;
        }

        if(groupedTestAmtByProdId[record.data.productId]) {
            groupedTestAmtByProdId[record.data.productId] = groupedTestAmtByProdId[record.data.productId] + record.data.testSale;
        } else {
            groupedTestAmtByProdId[record.data.productId] = record.data.testSale;
        }
    });

    var tankSaleGridStore = Ext.getCmp('tankSaleGrid').store;

    tankSaleGridStore.each(function(record) {
        if(groupedSaleAmtByTankId[record.data.tankId]) {
            record.set("sale", groupedSaleAmtByTankId[record.data.tankId].toFixed(2));
        } else {
            record.set("sale", 0.0);
        }

        if(groupedTestAmtByTankId[record.data.tankId]) {
            record.set("testSale", groupedTestAmtByTankId[record.data.tankId].toFixed(2));
        } else {
            record.set("testSale", 0);
        }

        //Set stock after sale
        record.set("stockAfterSale", (record.data.closingStock - record.data.sale).toFixed(2));

        //Avoid negative dip stock
        if(record.data.dipStock < 0) {
            //Reset closing
            record.set('dipStock', 0);
        }

        record.set("diffToday", (record.data.dipStock - record.data.stockAfterSale).toFixed(2));
        record.set("newDiffThisMonth", (record.data.diffThisMonth + record.data.diffToday).toFixed(2));
    });

    var fuelSaleGridStore = Ext.getCmp('fuelSaleGrid').store;

    fuelSaleGridStore.each(function(record) {
        if(groupedSaleAmtByProdId[record.data.productId]) {
            record.set("actualSale", groupedSaleAmtByProdId[record.data.productId].toFixed(2));
            record.set("actualSaleAmt", (groupedSaleAmtByProdId[record.data.productId] * record.data.unitSellingPrice).toFixed(2));
        } else {
            record.set("actualSale", 0.0);
            record.set("actualSaleAmt", 0.0);
        }

        if(groupedTestAmtByProdId[record.data.productId]) {
            record.set("testSale", groupedTestAmtByProdId[record.data.productId].toFixed(2));
            record.set("testSaleAmt", (groupedTestAmtByProdId[record.data.productId] * record.data.unitSellingPrice).toFixed(2));
        } else {
            record.set("testSale", 0);
            record.set("testSaleAmt", 0);
        }
        record.set("totalSale", (record.data.testSale + record.data.actualSale).toFixed(2));
        record.set("totalSaleAmt", (record.data.totalSale * record.data.unitSellingPrice).toFixed(2));

    });

}

