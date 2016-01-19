function buildDailyStatementPanel(title, record) {
    var oilProdCombo = new Ext.form.ComboBox({
        store: buildAvailableProductListStore('SECONDARY_PRODUCTS'),
        valueField: "productId",
        editable: false,
        // Template for the dropdown menu.
        // Note the use of the "x-list-plain" and "x-boundlist-item" class,
        // this is required to make the items selectable.
        tpl: Ext.create('Ext.XTemplate',
            '<ul class="x-list-plain"><tpl for=".">',
            '<li role="option" class="x-boundlist-item">{productName} - {unitSellingPrice}Rs</li>',
            '</tpl></ul>'
        ),
        // template for the content inside text field
        displayTpl: Ext.create('Ext.XTemplate',
            '<tpl for=".">',
            '{productName} - {unitSellingPrice}Rs',
            '</tpl>'
        ),
        listeners: {
            select: {
                fn: function(c, r, eopts) {
                    c.ownerCt.completeEdit();
                    var gridRecord = c.ownerCt.floatParent.getSelectionModel().getSelection()[0];
                    var comboRecord = r[0];
                    //Copy values to grid record
                    gridRecord.set("productName", comboRecord.data.productName + ' - ' + comboRecord.data.unitSellingPrice +'Rs');
                    gridRecord.set("currentStock", comboRecord.data.currentStock);
                    gridRecord.set("unitSellingPrice", comboRecord.data.unitSellingPrice);
//currentStock
                    var oilGridStore = Ext.getCmp('oilSaleGrid').store;

                    var rowNo = oilGridStore.indexOf(gridRecord);
                    //Find last used row
                    var lastUsedRow = -1;
                    oilGridStore.each(function(record, indx) {
                        if(record.data.productName.length > 0 && record !== gridRecord) {
                            lastUsedRow = indx;
                        }
                    });

                    if(rowNo > lastUsedRow + 1) {
                        oilGridStore.remove(gridRecord);
                        oilGridStore.insert(lastUsedRow+1, gridRecord);
                    }

                    if(rowNo >= oilGridStore.getCount() -1) {
                        for (var i=0; i<5;i++) {
                            oilGridStore.insert(rowNo+1, new FuelReceipt());
                        }
                    }
                    gridRecord.set("productId", comboRecord.data.productId + ":" + rowNo+1);
                }
            }
        }
    });

    var partyCombo = new Ext.form.ComboBox({
        store: buildPartyListStore('ALL_ACTIVE_NON_EMPLOYEE_PARTIES'),
        valueField: "id",
        displayField: "name",
        editable: false,
        listeners: {

            select: {
                fn: function(c, r, eopts) {
                    c.ownerCt.completeEdit();
                    var gridRecord = c.ownerCt.floatParent.getSelectionModel().getSelection()[0];
                    var comboRecord = r[0];
                    //Copy values to grid record
                    gridRecord.set("partyName", comboRecord.data.name);
                    gridRecord.set("partyId", comboRecord.data.id);
//currentStock
                    var partyTransGridStore = Ext.getCmp('partyTransGrid').store;

                    var rowNo = partyTransGridStore.indexOf(gridRecord);
                    //Find last used row
                    var lastUsedRow = -1;
                    partyTransGridStore.each(function(record, indx) {
                        if(record.data.partyName.length > 0 && record !== gridRecord) {
                            lastUsedRow = indx;
                        }
                    });

                    if(rowNo > lastUsedRow + 1) {
                        partyTransGridStore.remove(gridRecord);
                        partyTransGridStore.insert(lastUsedRow+1, gridRecord);
                    }

                    if(rowNo >= partyTransGridStore.getCount() -1) {
                        for (var i=0; i<10;i++) {
                            partyTransGridStore.insert(rowNo+1, new PartyTransaction());
                        }
                    }
                    gridRecord.set("id", comboRecord.data.id + ":" + rowNo+1);
                }
            }
        }
    });

    var employeeCombo = new Ext.form.ComboBox({
        store: buildEmployeeListStore(),
        valueField: "id",
        displayField: "name",
        editable: false,
        listeners: {
            select: {
                fn: function(c, r, eopts) {
                    c.ownerCt.completeEdit();
                    var gridRecord = c.ownerCt.floatParent.getSelectionModel().getSelection()[0];
                    var comboRecord = r[0];
                    //Copy values to grid record
                    gridRecord.set("partyName", comboRecord.data.name);
                    gridRecord.set("partyId", comboRecord.data.id);
//currentStock
                    var employeeTransGridStore = Ext.getCmp('empTransGrid').store;

                    var rowNo = employeeTransGridStore.indexOf(gridRecord);
                    //Find last used row
                    var lastUsedRow = -1;
                    employeeTransGridStore.each(function(record, indx) {
                        if(record.data.partyName.length > 0 && record !== gridRecord) {
                            lastUsedRow = indx;
                        }
                    });

                    if(rowNo > lastUsedRow + 1) {
                        employeeTransGridStore.remove(gridRecord);
                        employeeTransGridStore.insert(lastUsedRow+1, gridRecord);
                    }

                    if(rowNo >= employeeTransGridStore.getCount() -1) {
                        for (var i=0; i<5;i++) {
                            employeeTransGridStore.insert(rowNo+1, new EmployeeTransaction());
                        }
                    }
                    gridRecord.set("id", comboRecord.data.id + ":" + rowNo+1);
                }
            }
        }
    });

    var mainContent = Ext.create('Ext.form.Panel', {
        id:'dailyStmtPanel',
        title: title,
        //frame:true,
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
                sortable: false
            },{
                text: "Opening reading",
                dataIndex: 'finalReading',
                flex: 2,
                //align: 'right',
                sortable: false
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
                sortable: false
            },{
                text: "Product",
                dataIndex: 'productType',
                flex: 1,
                sortable: false
            },{
                text: "Opening stock",
                dataIndex: 'closingStock',
                flex: 1,
                //align: 'right',
                sortable: false
            },{
                text: "Sale",
                dataIndex: 'sale',
                flex: 1,
                sortable: false
            },{
                text: "Stock after sale",
                dataIndex: 'stockAfterSale',
                flex: 1,
                sortable: false
            },{
                text: "Test",
                dataIndex: 'testSale',
                flex: 1,
                sortable: false
            },{
                text: "Dip stock",
                dataIndex: 'dipStock',
                flex: 1,
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
                flex: 1,
                sortable: false
            },{
                text: "Diff this month",
                dataIndex: 'newDiffThisMonth',
                flex: 1,
                sortable: false
            }]
        },{
            xtype: 'gridpanel',
            id:'fuelSaleGrid',
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
                flex: 2,
                sortable: false
            },{
                text: "Price",
                dataIndex: 'unitSellingPrice',
                flex: 1,
                sortable: false
            },{
                text: "Total sale",
                dataIndex: 'totalSale',
                flex: 1,
                //align: 'right',
                sortable: false
            },{
                text: "Total sale amt",
                dataIndex: 'totalSaleAmt',
                flex: 1,
                sortable: false
            },{
                text: "Test sale",
                dataIndex: 'testSale',
                flex: 1,
                sortable: false
            },{
                text: "Test sale amt",
                dataIndex: 'testSaleAmt',
                flex: 1,
                sortable: false
            },{
                text: "Actual sale",
                dataIndex: 'actualSale',
                flex: 1,
                sortable: false
            },{
                text: "Actual sale amt",
                dataIndex: 'actualSaleAmt',
                flex: 1,
                sortable: false
            }]
        },{
            xtype: 'gridpanel',
            id:'oilSaleGrid',
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
            listeners : {
                edit: function (editor, e, eOpts) {
                    onOilSaleUpdate();
                }
            },
            columns:[{
                text: "Product",
                dataIndex: 'productName',
                flex: 2,
                sortable: false,
                editor: oilProdCombo,
                renderer: editableColumnRenderer
            },{
                text: "Price",
                dataIndex: 'unitSellingPrice',
                flex: 1,
                sortable: false
            },{
                text: "Stock",
                dataIndex: 'currentStock',
                flex: 1,
                //align: 'right',
                sortable: false
            },{
                text: "Total sale",
                dataIndex: 'actualSale',
                flex: 1,
                sortable: false,
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            },{
                text: "Discount per unit",
                dataIndex: 'discountPerUnit',
                flex: 1,
                sortable: false,
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            },{
                text: "Amount after discount",
                dataIndex: 'actualSaleAmt',
                flex: 1,
                sortable: false
            }]
        },{
            xtype: 'gridpanel',
            id:'partyTransGrid',
            height: 'auto',
            store: buildEmptyPartyTransactionStore(),
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
                    onPartyTransUpdate();
                },
                aftercellclick: function(iView, iCellEl, iColIdx, iRecord, iRowEl, iRowIdx, iEvent) {
                    var fieldName = iView.getGridColumns()[iColIdx].dataIndex;
                    if(fieldName === 'partyName') {
                        partyCombo.onTriggerClick();
                    }
                }
            },
            columns:[{
                text: "Party name",
                dataIndex: 'partyName',
                flex: 2,
                sortable: false,
                editor: partyCombo,
                renderer: editableColumnRenderer
            },{
                text: "Debit detail",
                dataIndex: 'debitDetail',
                flex: 2,
                sortable: false,
                field: {
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            },{
                text: "Debit amt",
                dataIndex: 'debitAmt',
                flex: 1,
                //align: 'right',
                sortable: false,
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            }, {
                xtype: 'checkcolumn',
                header: 'Cheque debit?',
                dataIndex: 'isChequeDebit',
                width:'20px'
            },{
                text: "Credit detail",
                dataIndex: 'creditDetail',
                flex: 2,
                sortable: false,
                field: {
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            },{
                text: "Credit amt",
                dataIndex: 'creditAmt',
                flex: 1,
                sortable: false,
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            }]
        },{
            xtype: 'gridpanel',
            id:'empTransGrid',
            height: 'auto',
            store: buildEmptyEmployeeTransactionStore(),
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
                    //onOilSaleUpdate();
                },
                aftercellclick: function(iView, iCellEl, iColIdx, iRecord, iRowEl, iRowIdx, iEvent) {
                    var fieldName = iView.getGridColumns()[iColIdx].dataIndex;
                    if(fieldName === 'partyName') {
                        partyCombo.onTriggerClick();
                    }
                }
            },
            columns:[{
                text: "Employee name",
                dataIndex: 'partyName',
                flex: 2,
                sortable: false,
                editor: employeeCombo,
                renderer: editableColumnRenderer
            },{
                text: "Salary detail",
                dataIndex: 'salaryDetail',
                flex: 2,
                sortable: false,
                field: {
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            },{
                text: "Salary amt",
                dataIndex: 'salaryAmt',
                flex: 1,
                //align: 'right',
                sortable: false,
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            },{
                text: "Incentive detail",
                dataIndex: 'incentiveDetail',
                flex: 2,
                sortable: false,
                field: {
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            },{
                text: "Incentive amt",
                dataIndex: 'incentiveAmt',
                flex: 1,
                sortable: false,
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            }]
        },{
            xtype: 'gridpanel',
            id:'officeCashGrid',
            autoscroll: true,
            height: 'auto',
            store: buildOfficeCashStore(),
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
                    onOfficeCashUpdate();
                    return true;
                },
                afterlayout: function () {
                    onOfficeCashUpdate();
                }
            },
            columns:[{
                text: "Name",
                dataIndex: 'name',
                flex: 1
            },{
                text: "Opening",
                dataIndex: 'opBal',
                flex: 1
            },{
                text: "To office",
                dataIndex: 'toOffice',
                flex: 1,
                sortable: false,
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            },{
                text: "Paid to bank",
                dataIndex: 'paidToBank',
                flex: 1,
                sortable: false,
                field: {
                    xtype: 'numberfield',
                    allowNegative: false,
                    allowBlank: false
                },
                renderer: editableColumnRenderer
            },{
                text: "Closing",
                dataIndex: 'clBal',
                flex: 1,
                sortable: false
            }]
        },{
            xtype:'fieldset',
            defaults: {anchor: '100%',height:'30px'},
            layout: 'hbox',
            //title:'Test',
            //width:'100%',
            height:'50px',
            defaultType: 'textfield',
            fieldDefaults: {
                msgTarget: 'side',
                labelWidth: 100
            },
            items: [
                {
                    id:'openBal-field',
                    fieldLabel: 'Opening balance',
                    name: 'opBal',
                    flex:1,
                    readOnly: true
                },{
                    id:'clBal-field',
                    fieldLabel: 'Closing balance',
                    flex:1,
                    readOnly: true
                }
            ]
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

function onOilSaleUpdate() {
    var oilSaleStore = Ext.getCmp('oilSaleGrid').store;
    oilSaleStore.each( function(record) {
        //Avoid negative sale/discount
        if(record.data.discountPerUnit < 0 || (record.data.discountPerUnit> record.data.unitSellingPrice)) {
            record.set('discountPerUnit', 0);
        }
        if(record.data.actualSale <= 0) {
            record.set('actualSale', 0);
            record.set('discountPerUnit', 0);
        }

        if(record.data.actualSale > 0) {
            record.set( 'actualSaleAmt', ((record.data.unitSellingPrice - record.data.discountPerUnit)* record.data.actualSale).toFixed(2));
        } else {
            record.set( 'actualSaleAmt', 0);
        }
    });

}

function onOfficeCashUpdate() {
    var officeCashGridStore = Ext.getCmp('officeCashGrid').store;
    officeCashGridStore.each( function(record) {
        //Avoid negative sale/discount
        if(record.data.toOffice < 0 ) {
            record.set('toOffice', 0);
        }
        if(record.data.paidToBank < 0) {
            record.set('paidToBank', 0);
        }

        if(record.data.name == 'OFFICE CHEQUE') {
            record.set('toOffice', getChequeAmtFromPartyTransactions()); //Get from party transactions
        }

        record.set('clBal', (record.data.opBal + record.data.toOffice - record.data.paidToBank).toFixed(2));
    });

}

function onPartyTransUpdate() {
    onOfficeCashUpdate();
}
function getChequeAmtFromPartyTransactions() {
    var partyTransGridStore = Ext.getCmp('partyTransGrid').store;
    var chequeAmt = 0;
    partyTransGridStore.each( function(record) {
        if(record.data.isChequeDebit === true) {
            chequeAmt =  chequeAmt + record.data.debitAmt;
        }
    });
    return chequeAmt.toFixed(2);
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

    var oilSaleGridStore = Ext.getCmp('oilSaleGrid').store;

    oilSaleGridStore.each(function() {

    });
    //var data = combo.findRecordByValue(combo.getValue()).data

}

